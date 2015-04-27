package io.seqware.pipeline.engines.whitestar;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import io.seqware.common.model.WorkflowRunStatus;
import io.seqware.pipeline.SqwKeys;
import io.seqware.pipeline.api.WorkflowEngine;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.metadata.MetadataFactory;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import static net.sourceforge.seqware.common.util.Rethrow.rethrow;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object.OozieJob;
import net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object.WorkflowApp;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.Path;

/**
 * This is a synchronous bare-bones implementation of the WorkflowEngine for prototyping and debugging.
 *
 * This re-uses much of the OozieWorkflowXml generation code in order to generate the required Bash scripts and supporting files.
 *
 * @author dyuen
 */
public class WhiteStarWorkflowEngine implements WorkflowEngine {

    // for this engine, just use the SWID
    private String jobId;
    private final boolean useSge;
    private final String threadsSgeParamFormat;
    private final String maxMemorySgeParamFormat;

    private File nfsWorkDir;
    private WorkflowApp workflowApp;
    private final boolean parallel;
    private Persistence persistence;

    /**
     *
     * @param objectModel
     * @param useSge
     * @param threadsSgeParamFormat
     * @param maxMemorySgeParamFormat
     * @param createDirectories
     *            true when creating the engine to launch a job
     * @param parallel
     */
    public WhiteStarWorkflowEngine(AbstractWorkflowDataModel objectModel, boolean useSge, String threadsSgeParamFormat,
            String maxMemorySgeParamFormat, boolean createDirectories, boolean parallel) {
        this.useSge = useSge;
        this.threadsSgeParamFormat = threadsSgeParamFormat;
        this.maxMemorySgeParamFormat = maxMemorySgeParamFormat;

        if (createDirectories) {
            this.nfsWorkDir = initNfsWorkDir(objectModel);
        } else {
            this.nfsWorkDir = null;
        }
        this.parallel = parallel;
    }

    private static File initNfsWorkDir(AbstractWorkflowDataModel model) {
        try {
            File nfsWorkDir = FileTools.createDirectoryWithUniqueName(new File(model.getEnv().getOOZIE_WORK_DIR()), "oozie");

            boolean setWritable = nfsWorkDir.setWritable(true, false);
            if (!setWritable) {
                throw new RuntimeException("Unable to write to working directory");
            }
            System.out.println("Using working directory: " + nfsWorkDir.getAbsolutePath());
            return nfsWorkDir;
        } catch (IOException e) {
            throw rethrow(e);
        }
    }

    private static String seqwareJarPath(AbstractWorkflowDataModel objectModel) {
        return objectModel.getWorkflowBaseDir() + "/lib/seqware-distribution-" + objectModel.getTags().get("seqware_version") + "-full.jar";
    }

    @Override
    public void prepareWorkflow(AbstractWorkflowDataModel objectModel) {
        prepareWorkflow(objectModel, null);
    }

    /**
     *
     * @param objectModel
     * @param nfsWorkDir
     *            pass a working directory to skip creation of scripts in the generated-scripts
     */
    public void prepareWorkflow(AbstractWorkflowDataModel objectModel, File nfsWorkDir) {
        // parse objectmodel
        if (nfsWorkDir == null) {
            this.populateNfsWorkDir();
        } else {
            this.nfsWorkDir = nfsWorkDir;
        }
        /** regardless of the truth, tell the workflow app that we're always using sge in order to generate all generated scripts */
        this.workflowApp = new WorkflowApp(objectModel, this.nfsWorkDir.getAbsolutePath(), new Path("dummy-value"), true, new File(
                seqwareJarPath(objectModel)), this.threadsSgeParamFormat, this.maxMemorySgeParamFormat);
        // go ahead and create the required script files
        if (nfsWorkDir == null) {
            this.workflowApp.serializeXML();
        }
        this.jobId = objectModel.getWorkflow_run_accession();
        this.persistence = new Persistence(this.nfsWorkDir);
    }

    @Override
    public ReturnValue runWorkflow() {
        return runWorkflow(new TreeSet<String>());
    }

    public ReturnValue runWorkflow(SortedSet<String> set) {
        ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);

        // run this workflow synchronously
        List<List<OozieJob>> jobs = this.workflowApp.getOrderedJobs();
        SortedSet<String> completedJobs = Collections.synchronizedSortedSet(set);

        int swid = Integer.parseInt(this.jobId);
        persistence.persistState(Integer.parseInt(this.jobId), completedJobs);

        for (int j = 0; j < jobs.size(); j++) {
            List<OozieJob> rowOfJobs = jobs.get(j);
            // determine number of possible retry loops
            int retryLoops = Integer.parseInt(ConfigTools.getSettingsValue(SqwKeys.OOZIE_RETRY_MAX));
            int totalAttempts = retryLoops + 1;
            SortedSet<OozieJob> jobsLeft = Collections.synchronizedSortedSet(new TreeSet<>(rowOfJobs));
            Set<OozieJob> jobsToRemove = new TreeSet<>();
            // filter out completed jobs from a pervious run
            for (OozieJob job : jobsLeft) {
                if (completedJobs.contains(job.getLongName())) {
                    jobsToRemove.add(job);
                }
            }
            if (jobsToRemove.size() > 0) {
                Log.stdoutWithTime("Skipping " + Joiner.on(",").join(jobsToRemove) + " found in persistent map");
                jobsLeft.removeAll(jobsToRemove);
            }

            final SortedSet<OozieJob> jobsFailed = Collections.synchronizedSortedSet(new TreeSet<OozieJob>());

            for (int i = 1; i <= totalAttempts && !jobsLeft.isEmpty(); i++) {
                Log.stdoutWithTime("Row #" + j + " , Attempt #" + i + " out of " + totalAttempts + " : " + StringUtils.join(jobsLeft, ","));
                // for each row of Jobs in the DAG
                ListeningExecutorService pool = null;
                try {
                    if (this.parallel) {
                        pool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(jobsLeft.size()));
                    } else {
                        pool = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
                    }
                    // keep track of memory for submitted jobs and ensure it doesn't reach our limits
                    int memoryLimit = Integer.parseInt(ConfigTools.getSettingsValue(SqwKeys.WHITESTAR_MEMORY_LIMIT));

                    if (!validateJobMemoryLimits(jobsLeft, memoryLimit, swid)) {
                        alterWorkflowRunStatus(swid, WorkflowRunStatus.failed);
                        return new ReturnValue(ReturnValue.FAILURE);
                    }
                    ListenableFuture<List<Integer>> batch;
                    while (!jobsLeft.isEmpty()) {
                        batch = scheduleMemoryLimitedBatch(jobsLeft, pool, jobsFailed, completedJobs);
                        try {
                            batch.get();
                        } catch (InterruptedException | ExecutionException ex) {
                            Log.stdoutWithTime("\tBatch of jobs failed: " + Joiner.on(",").join(jobsFailed));
                            break;
                        }
                    }
                } finally {
                    if (pool != null) {
                        pool.shutdown();
                    }
                }
                jobsLeft.addAll(jobsFailed);
                jobsFailed.clear();
            }

            if (!jobsLeft.isEmpty()) {
                alterWorkflowRunStatus(swid, WorkflowRunStatus.failed);
                return new ReturnValue(ReturnValue.FAILURE);
            }
        }
        alterWorkflowRunStatus(swid, WorkflowRunStatus.completed);
        return ret;
    }

    /**
     * Schedule a batch of jobs dependent on the memory limit.
     *
     * @param jobsLeft
     *            a list of jobs to be scheduled, scheduled jobs will be removed
     * @param pool
     *            an execution service to schedule jobs to
     * @param jobsFailed
     *            will contain a list of jobs that failed
     * @param completedJobs
     *            set of completed jobs
     * @return a future that will return when all jobs are complete
     */
    private ListenableFuture<List<Integer>> scheduleMemoryLimitedBatch(final Set<OozieJob> jobsLeft, final ListeningExecutorService pool,
            final Set<OozieJob> jobsFailed, final SortedSet<String> completedJobs) {
        int memoryLimit = Integer.parseInt(ConfigTools.getSettingsValue(SqwKeys.WHITESTAR_MEMORY_LIMIT));
        int memoryUsed = 0;
        List<OozieJob> currentBatch = Lists.newArrayList();
        for (OozieJob job : jobsLeft) {
            int memoryAttempt = Integer.parseInt(job.getJobObject().getMaxMemory());
            if (memoryUsed + memoryAttempt <= memoryLimit) {
                // add job to batch
                currentBatch.add(job);
                memoryUsed += memoryAttempt;
            }
        }
        Log.stdoutWithTime("\tSubmitting " + memoryUsed + "M batch with: " + Joiner.on(",").join(currentBatch));
        List<ListenableFuture<Integer>> memoryBatchFutures = Lists.newArrayList();
        for (final OozieJob job : currentBatch) {
            ListenableFuture<Integer> future = pool.submit(new ExecutionThread(job));
            Futures.addCallback(future, new FutureCallback<Integer>() {
                @Override
                public void onSuccess(Integer result) {
                    if (result != null && result == 0) {
                        Log.stdoutWithTime("\tWorkflow step succeeded: " + job.getLongName());
                        completedJobs.add(job.getLongName());
                        persistence.persistState(Integer.parseInt(WhiteStarWorkflowEngine.this.jobId), completedJobs);
                    } else {
                        jobsFailed.add(job);
                        Log.stdoutWithTime("\tWorkflow step failed: " + job.getLongName());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.stdoutWithTime("\tWorkflow step " + job.getLongName() + " was interrupted or threw an exception");
                    jobsFailed.add(job);
                }

            });
            memoryBatchFutures.add(future);
        }
        jobsLeft.removeAll(currentBatch);
        return Futures.allAsList(memoryBatchFutures);
    }

    /**
     *
     * @param jobsLeft
     * @param memoryLimit
     * @param swid
     * @return true iff all jobs are under the memory limit
     * @throws NumberFormatException
     */
    private boolean validateJobMemoryLimits(Set<OozieJob> jobsLeft, int memoryLimit, int swid) {
        // validate that all jobs are under the memory limit
        for (OozieJob job : jobsLeft) {
            int memoryAttempt = Integer.parseInt(job.getJobObject().getMaxMemory());
            if (memoryAttempt > memoryLimit) {
                Log.stdoutWithTime("Workflow step " + job.getLongName() + " exceeds the memory limit of " + memoryLimit);
                alterWorkflowRunStatus(swid, WorkflowRunStatus.failed);
                return false;
            }
        }
        return true;
    }

    private void alterWorkflowRunStatus(int jobId, WorkflowRunStatus status) {
        Log.stdoutWithTime("Setting workflow-run status to " + status + " for: " + jobId);
        // set the status to completed
        Metadata ws = MetadataFactory.get(ConfigTools.getSettings());
        WorkflowRun workflowRun = ws.getWorkflowRun(jobId);
        workflowRun.setStatus(status);
        ws.updateWorkflowRun(workflowRun);
    }

    private final class ExecutionThread implements Callable<Integer> {
        private final OozieJob job;

        protected ExecutionThread(OozieJob job) {
            this.job = job;
        }

        @Override
        public Integer call() throws Exception {
            CommandLine cmdLine;
            File scriptsDir = job.getScriptsDir();
            String optionsFileName = OozieJob.optsFileName(job.getLongName());
            String runnerFileName = OozieJob.runnerFileName(job.getLongName());

            if (!WhiteStarWorkflowEngine.this.useSge) {
                cmdLine = new CommandLine("bash");
            } else {
                cmdLine = new CommandLine("qsub");
                cmdLine.addArgument("-sync");
                cmdLine.addArgument("yes");
                cmdLine.addArgument("-@");
                cmdLine.addArgument(scriptsDir.getAbsolutePath() + "/" + optionsFileName);
            }

            cmdLine.addArgument(scriptsDir.getAbsolutePath() + "/" + runnerFileName);

            Executor executor = new DefaultExecutor();
            executor.setWorkingDirectory(scriptsDir);
            Log.stdoutWithTime("\tRunning command: " + cmdLine.toString());

            // record output ourselves if not using sge
            if (!WhiteStarWorkflowEngine.this.useSge) {
                // we can only use the last 9 characters to fit into an int
                String time = String.valueOf(System.currentTimeMillis()).substring(4);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
                PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
                executor.setStreamHandler(streamHandler);
                // execute!
                try {
                    executor.execute(cmdLine);
                    // grab stdout and stderr
                } catch (ExecuteException e) {
                    Log.debug("\tFatal error in workflow at step: " + job.getLongName());
                    return -1;
                } catch (IOException e) {
                    throw rethrow(e);
                } finally {
                    FileUtils.write(new File(scriptsDir.getAbsolutePath() + "/" + job.getLongName() + ".e" + time),
                            outputStream.toString(StandardCharsets.UTF_8.name()), StandardCharsets.UTF_8);
                    FileUtils.write(new File(scriptsDir.getAbsolutePath() + "/" + job.getLongName() + ".o" + time),
                            errorStream.toString(StandardCharsets.UTF_8.name()), StandardCharsets.UTF_8);
                }

            } else {
                try {
                    executor.execute(cmdLine);
                } catch (ExecuteException ex) {
                    Log.debug("Fatal error in workflow at step: " + job.getLongName());
                    return -1;
                }
            }

            return 0;
        }
    }

    @Override
    public ReturnValue watchWorkflow(String jobToken) {
        Metadata ws = MetadataFactory.get(ConfigTools.getSettings());
        WorkflowRun workflowRun = ws.getWorkflowRun(Integer.parseInt(jobToken));
        Log.stdout("Workflow run " + jobToken + " is currently " + workflowRun.getStatus().name());
        return new ReturnValue(workflowRun.getStatus() == WorkflowRunStatus.completed ? ReturnValue.SUCCESS : ReturnValue.FAILURE);
    }

    /**
     *
     */
    private void populateNfsWorkDir() {
        File lib = new File(this.nfsWorkDir, "lib");
        boolean mkdir = lib.mkdir();
        if (!mkdir) {
            throw new RuntimeException("Unable to make directory in working dir");
        }
    }

    @Override
    public String getWorkingDirectory() {
        return nfsWorkDir == null ? null : nfsWorkDir.getAbsolutePath();
    }

    @Override
    public String getLookupToken() {
        return this.jobId;
    }

}
