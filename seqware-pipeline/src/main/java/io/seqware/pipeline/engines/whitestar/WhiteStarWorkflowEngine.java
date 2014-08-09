package io.seqware.pipeline.engines.whitestar;

import io.seqware.common.model.WorkflowRunStatus;
import io.seqware.pipeline.api.WorkflowEngine;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import net.sourceforge.seqware.common.metadata.MetadataFactory;
import net.sourceforge.seqware.common.metadata.MetadataWS;
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
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
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

    private final File nfsWorkDir;
    private WorkflowApp workflowApp;

    /**
     * 
     * @param objectModel
     * @param useSge
     * @param threadsSgeParamFormat
     * @param maxMemorySgeParamFormat
     * @param createDirectories
     *            true when creating the engine to launch a job
     */
    public WhiteStarWorkflowEngine(AbstractWorkflowDataModel objectModel, boolean useSge, String threadsSgeParamFormat,
            String maxMemorySgeParamFormat, boolean createDirectories) {
        this.useSge = useSge;
        this.threadsSgeParamFormat = threadsSgeParamFormat;
        this.maxMemorySgeParamFormat = maxMemorySgeParamFormat;

        if (createDirectories) {
            this.nfsWorkDir = initNfsWorkDir(objectModel);
        } else {
            this.nfsWorkDir = null;
        }
    }

    public static File initNfsWorkDir(AbstractWorkflowDataModel model) {
        try {
            File nfsWorkDir = FileTools.createDirectoryWithUniqueName(new File(model.getEnv().getOOZIE_WORK_DIR()), "oozie");
            nfsWorkDir.setWritable(true, false);
            System.out.println("Using working directory: " + nfsWorkDir.getAbsolutePath());
            return nfsWorkDir;
        } catch (IOException e) {
            throw rethrow(e);
        }
    }

    public static String seqwareJarPath(AbstractWorkflowDataModel objectModel) {
        return objectModel.getWorkflowBaseDir() + "/lib/seqware-distribution-" + objectModel.getTags().get("seqware_version") + "-full.jar";
    }

    @Override
    public void prepareWorkflow(AbstractWorkflowDataModel objectModel) {
        // parse objectmodel
        this.populateNfsWorkDir();
        /** regardless of the truth, tell the workflow app that we're always using sge in order to generate all generated scripts */
        this.workflowApp = new WorkflowApp(objectModel, this.nfsWorkDir.getAbsolutePath(), new Path("dummy-value"), true, new File(
                seqwareJarPath(objectModel)), this.threadsSgeParamFormat, this.maxMemorySgeParamFormat);
        // go ahead and create the required script files
        this.workflowApp.serializeXML();
        this.jobId = objectModel.getWorkflow_run_accession();
    }

    @Override
    public ReturnValue runWorkflow() {
        ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);
        try {
            // run this workflow synchronously
	    List<List<OozieJob>> jobs =  this.workflowApp.getOrderedJobs();
	    for (List<OozieJob> rowOfJobs : jobs) {
            for (OozieJob job : rowOfJobs) {
                CommandLine cmdLine;
                File scriptsDir = job.getScriptsDir();
                String optionsFileName = OozieJob.optsFileName(job.getName());
                String runnerFileName = OozieJob.runnerFileName(job.getName());

                if (this.useSge) {
                    cmdLine = new CommandLine("qsub");
                    cmdLine.addArgument("-sync");
                    cmdLine.addArgument("yes");
                    cmdLine.addArgument("-@");
                    cmdLine.addArgument(scriptsDir.getAbsolutePath() + "/" + optionsFileName);
                } else {
                    cmdLine = new CommandLine("bash");
                }

                cmdLine.addArgument(scriptsDir.getAbsolutePath() + "/" + runnerFileName);

                Executor executor = new DefaultExecutor();
                executor.setWorkingDirectory(scriptsDir);
                Log.stdoutWithTime("Running command: " + cmdLine.toString());
                // record output ourselves if not using sge
                if (!this.useSge) {
                    // we can only use the last 9 characters to fit into an int
                    String time = String.valueOf(System.currentTimeMillis()).substring(4);

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
                    PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
                    executor.setStreamHandler(streamHandler);
                    // execute!
                    executor.execute(cmdLine);
                    // grab stdout and stderr
                    FileUtils.write(new File(scriptsDir.getAbsolutePath() + "/" + runnerFileName + ".e" + time), outputStream.toString());
                    FileUtils.write(new File(scriptsDir.getAbsolutePath() + "/" + runnerFileName + ".o" + time), errorStream.toString());

                } else {
                    executor.execute(cmdLine);
                }
            }
            }
        } catch (IOException e) {
            throw rethrow(e);
        }
        Log.stdoutWithTime("Setting workflow-run status to complete for: " + this.jobId);
        // set the status to completed
        MetadataWS ws = MetadataFactory.getWS(ConfigTools.getSettings());
        WorkflowRun workflowRun = ws.getWorkflowRun(Integer.valueOf(this.jobId));
        workflowRun.setStatus(WorkflowRunStatus.completed);
        ws.updateWorkflowRun(workflowRun);

        return ret;
    }

    @Override
    public ReturnValue watchWorkflow(String jobToken) {
        MetadataWS ws = MetadataFactory.getWS(ConfigTools.getSettings());
        WorkflowRun workflowRun = ws.getWorkflowRun(Integer.valueOf(jobToken));
        Log.stdout("Workflow run " + jobToken + " is now " + workflowRun.getStatus().name());
        return new ReturnValue(workflowRun.getStatus() == WorkflowRunStatus.completed ? ReturnValue.SUCCESS : ReturnValue.FAILURE);
    }

    /**
     * 
     */
    private void populateNfsWorkDir() {
        File lib = new File(this.nfsWorkDir, "lib");
        lib.mkdir();
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
