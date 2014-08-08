package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie;

import io.seqware.pipeline.api.WorkflowEngine;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import static net.sourceforge.seqware.common.util.Rethrow.rethrow;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;
import org.apache.oozie.client.WorkflowAction;
import org.apache.oozie.client.WorkflowJob;
import org.apache.oozie.client.WorkflowJob.Status;

/**
 * This is the implementation of the WorkflowEngine with a Oozie back-end.
 * 
 * @author dyuen
 */
public class OozieWorkflowEngine implements WorkflowEngine {

    private String jobId;
    private AbstractWorkflowDataModel dataModel;
    private final boolean useSge;
    private final String threadsSgeParamFormat;
    private final String maxMemorySgeParamFormat;

    private final File nfsWorkDir;
    private final Configuration conf;
    private final Path hdfsWorkDir;

    /**
     * 
     * @param objectModel
     * @param useSge
     * @param threadsSgeParamFormat
     * @param maxMemorySgeParamFormat
     * @param createDirectories
     *            true when creating the engine to launch a job
     */
    public OozieWorkflowEngine(AbstractWorkflowDataModel objectModel, boolean useSge, String threadsSgeParamFormat,
            String maxMemorySgeParamFormat, boolean createDirectories) {
        this.dataModel = objectModel;
        this.useSge = useSge;
        this.threadsSgeParamFormat = threadsSgeParamFormat;
        this.maxMemorySgeParamFormat = maxMemorySgeParamFormat;
        this.conf = initConf(objectModel);

        if (createDirectories) {
            this.nfsWorkDir = initNfsWorkDir(objectModel);
            this.hdfsWorkDir = initHdfsWorkDir(objectModel, conf, this.nfsWorkDir);
        } else {
            this.nfsWorkDir = null;
            this.hdfsWorkDir = null;
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

    public static Configuration initConf(AbstractWorkflowDataModel model) {
        Configuration conf = new Configuration();
        conf.set("mapred.job.tracker", model.getEnv().getMapred_job_tracker());
        if (model.getEnv().getFs_default_name() != null) conf.set("fs.default.name", model.getEnv().getFs_default_name());
        if (model.getEnv().getFs_defaultFS() != null) conf.set("fs.defaultFS", model.getEnv().getFs_defaultFS());
        conf.set("fs.hdfs.impl", model.getEnv().getFs_hdfs_impl());
        return conf;
    }

    public static Path initHdfsWorkDir(AbstractWorkflowDataModel model, Configuration conf, File nfsWorkDir) {
        FileSystem fileSystem = null;
        try {
            fileSystem = FileSystem.get(conf);
            Path path = new Path(model.getEnv().getOOZIE_APP_ROOT() + "/" + nfsWorkDir.getName());
            fileSystem.mkdirs(path);
            return fileSystem.getFileStatus(path).getPath();
        } catch (IOException e) {
            throw rethrow(e);
        } finally {
            if (fileSystem != null) {
                try {
                    fileSystem.close();
                } catch (IOException e) {
                    // gulp
                }
            }
        }
    }

    public static String seqwareJarPath(AbstractWorkflowDataModel objectModel) {
        return objectModel.getWorkflowBaseDir() + "/lib/seqware-distribution-" + objectModel.getTags().get("seqware_version") + "-full.jar";
    }

    @Override
    public void prepareWorkflow(AbstractWorkflowDataModel objectModel) {
        // parse objectmodel
        this.dataModel = objectModel;
        this.populateNfsWorkDir();
        this.parseDataModel(objectModel, useSge, new File(seqwareJarPath(objectModel)));
        this.populateHdfsWorkDir(objectModel);
    }

    @Override
    public ReturnValue runWorkflow() {
        ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);
        OozieClient wc = this.getOozieClient();

        try {
            Properties propertiesConf = wc.createConfiguration();
            propertiesConf.setProperty(OozieClient.APP_PATH, hdfsWorkDir.toString());
            propertiesConf.setProperty("jobTracker", this.dataModel.getEnv().getOOZIE_JOBTRACKER());
            propertiesConf.setProperty("nameNode", this.dataModel.getEnv().getOOZIE_NAMENODE());
            propertiesConf.setProperty("queueName", this.dataModel.getEnv().getOOZIE_QUEUENAME());

            jobId = wc.run(propertiesConf);
            Log.stdout("Submitted Oozie job: " + jobId);

        } catch (Exception e) {
            throw rethrow(e);
        }

        return ret;
    }

    @Override
    public ReturnValue watchWorkflow(String jobToken) {
        OozieClient wc = this.getOozieClient();

        try {
            Properties localConf = wc.createConfiguration();
            // localConf.setProperty(OozieClient.APP_PATH, hdfsWorkDir.toString());
            localConf.setProperty("jobTracker", this.dataModel.getEnv().getOOZIE_JOBTRACKER());
            localConf.setProperty("nameNode", this.dataModel.getEnv().getOOZIE_NAMENODE());
            localConf.setProperty("queueName", this.dataModel.getEnv().getOOZIE_QUEUENAME());

            return watchWorkflowInternal(wc, jobToken, new ReturnValue());

        } catch (Exception e) {
            throw rethrow(e);
        }
    }

    private ReturnValue watchWorkflowInternal(OozieClient wc, String jobId, ReturnValue ret) throws OozieClientException,
            InterruptedException {
        Log.stdout("");
        Log.stdout("Polling workflow run status every 10 seconds.");
        Log.stdout("Terminating this program will NOT affect the running workflow.");
        Thread.sleep(2 * 1000);
        // Ensure that we can pull the job info from oozie
        int maxwait = 5;
        while (maxwait-- > 0) {
            try {
                wc.getJobInfo(jobId);
                // job info available
                break;
            } catch (Exception e) {
                if (maxwait == 0) {
                    Log.stdout("\nTimed out waiting for workflow job to be available.");
                    throw rethrow(e);
                } else {
                    Log.stdout("\nWorkflow job pending ...");
                    Thread.sleep(5 * 1000);
                }
            }
        }
        while (wc.getJobInfo(jobId).getStatus() == WorkflowJob.Status.RUNNING) {
            Log.stdout("\nWorkflow job running ...");
            printWorkflowInfo(wc.getJobInfo(jobId));
            Thread.sleep(10 * 1000);
        }
        Log.stdout("\nWorkflow job completed ...");
        WorkflowJob job = wc.getJobInfo(jobId);
        printWorkflowInfo(job);
        if (job.getStatus() != Status.SUCCEEDED) {
            ret = new ReturnValue(ReturnValue.FAILURE);
        }
        return ret;
    }

    private void printWorkflowInfo(WorkflowJob wf) {
        Log.stdout("Application Path   : " + wf.getAppPath());
        Log.stdout("Application Name   : " + wf.getAppName());
        Log.stdout("Application Status : " + wf.getStatus());
        Log.stdout("Application Actions:");
        for (WorkflowAction action : wf.getActions()) {
            Log.stdout(MessageFormat.format("   Name: {0} Type: {1} Status: {2}", action.getName(), action.getType(), action.getStatus()));
        }
    }

    /**
     * copy the local dir to HDFS
     */
    private void populateHdfsWorkDir(AbstractWorkflowDataModel objectModel) {
        FileSystem fileSystem = null;
        try {
            fileSystem = FileSystem.get(conf);
            Path pathlib = new Path(hdfsWorkDir, "lib");
            fileSystem.mkdirs(pathlib);
            copyFromLocal(fileSystem, nfsWorkDir + "/job.properties", hdfsWorkDir);
            copyFromLocal(fileSystem, nfsWorkDir + "/workflow.xml", hdfsWorkDir);

            if (!useSge) {
                // copy lib
                copyFromLocal(fileSystem, seqwareJarPath(objectModel), pathlib);
            }
            System.out.println("Files copied to " + nfsWorkDir);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (fileSystem != null) {
                try {
                    fileSystem.close();
                } catch (IOException e) {
                    // gulp
                }
            }
        }
    }

    /**
     * @throws IOException
     * 
     */
    private void populateNfsWorkDir() {
        try {
            File file = new File(nfsWorkDir, "job.properties");
            try (FileWriter fw = new FileWriter(file)) {
                fw.write("nameNode=" + this.dataModel.getEnv().getOOZIE_NAMENODE() + "\n");
                fw.write("jobTracker=" + this.dataModel.getEnv().getOOZIE_JOBTRACKER() + "\n");
                fw.write("queueName=" + this.dataModel.getEnv().getOOZIE_QUEUENAME() + "\n");
                fw.write("oozie.wf.application.path=" + this.hdfsWorkDir);
            }

            File lib = new File(this.nfsWorkDir, "lib");
            lib.mkdir();

        } catch (IOException e) {
            throw rethrow(e);
        }
    }

    @Override
    public String getWorkingDirectory() {
        return nfsWorkDir == null ? null : nfsWorkDir.getAbsolutePath();
    }

    /**
     * return a workflow.xml for hadoop
     * 
     * @param objectModel
     * @return
     */
    private File parseDataModel(AbstractWorkflowDataModel objectModel, boolean useSge, File seqwareJar) {
        File file = new File(nfsWorkDir, "workflow.xml");
        // generate dax
        OozieWorkflowXmlGenerator daxv2 = new OozieWorkflowXmlGenerator();
        daxv2.generateWorkflowXml(objectModel, file.getAbsolutePath(), this.nfsWorkDir.getAbsolutePath(), hdfsWorkDir, useSge, seqwareJar,
                this.threadsSgeParamFormat, this.maxMemorySgeParamFormat);
        return file;
    }

    public static void copyFromLocal(FileSystem fileSystem, String source, Path dstPath) {
        try {
            Path srcPath = new Path(source);

            // Check if the file already exists
            if (!(fileSystem.exists(dstPath))) {
                System.out.println("No such destination " + dstPath);
                return;
            }

            fileSystem.copyFromLocalFile(srcPath, dstPath);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getLookupToken() {
        return this.jobId;
    }

    private OozieClient getOozieClient() {
        OozieClient oc = new OozieClient(this.dataModel.getEnv().getOOZIE_URL());
        return oc;
    }

    // @Override
    // public String getId() {
    // return this.jobId;
    // }
    //
    // @Override
    // public String getStatus(String id) {
    // OozieClient oc = this.getOozieClient();
    // try {
    // WorkflowJob wfJob = oc.getJobInfo(id);
    // if (wfJob == null)
    // return null;
    // return wfJob.getStatus().toString();
    // } catch (OozieClientException e) {
    // e.printStackTrace();
    // return null;
    // }
    // }
    //
    // @Override
    // /**
    // * get the first failed job's error message
    // */
    // public String getStdErr(String id) {
    // OozieClient oc = this.getOozieClient();
    // StringBuilder sb = new StringBuilder();
    // try {
    // WorkflowJob wfJob = oc.getJobInfo(id);
    //
    // if (wfJob == null)
    // return null;
    // for (WorkflowAction action : wfJob.getActions()) {
    // if (action.getErrorMessage() != null) {
    // sb.append(MessageFormat.format("   Name: {0} Type: {1} ErrorMessage: {2}",
    // action.getName(),
    // action.getType(), action.getErrorMessage()));
    // sb.append("\n");
    // }
    // }
    // return sb.toString();
    // } catch (OozieClientException e) {
    // e.printStackTrace();
    // return null;
    // }
    // }
    //
    // @Override
    // public String getStdOut(String id) {
    // OozieClient oc = this.getOozieClient();
    // StringBuilder sb = new StringBuilder();
    // try {
    // WorkflowJob wfJob = oc.getJobInfo(id);
    //
    // if (wfJob == null)
    // return null;
    // for (WorkflowAction action : wfJob.getActions()) {
    // if (action.getErrorMessage() != null) {
    // sb.append(MessageFormat.format("   Name: {0} Type: {1} ErrorMessage: {2}",
    // action.getName(),
    // action.getType(), action.getStatus()));
    // sb.append("\n");
    // }
    // }
    // return sb.toString();
    // } catch (OozieClientException e) {
    // e.printStackTrace();
    // return null;
    // }
    // }
    //
    // @Override
    // public String getStatus() {
    // return this.getStatus(this.getId());
    // }
}
