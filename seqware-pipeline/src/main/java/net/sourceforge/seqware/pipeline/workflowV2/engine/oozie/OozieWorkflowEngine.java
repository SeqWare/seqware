package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;
import org.apache.oozie.client.WorkflowAction;
import org.apache.oozie.client.WorkflowJob;

import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowEngine;


public class OozieWorkflowEngine extends AbstractWorkflowEngine {

	private File dir;
	private String work_dir = "/home/seqware/oozie";
	@Override
	public ReturnValue launchWorkflow(AbstractWorkflowDataModel objectModel) {
		//parse objectmodel 
		ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);
		this.setupEnvironment();
		this.parseDataModel(objectModel);
		this.setupHDFS(objectModel);
		ret = this.runWorkflow(objectModel);
		return ret;
	}
	
	private ReturnValue runWorkflow(AbstractWorkflowDataModel objectModel) {
		ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);
	    OozieClient wc = new OozieClient("http://localhost:11000/oozie");

	    try {
		    Properties conf = wc.createConfiguration();
		    String app_path = "hdfs://localhost:8020/user/seqware/seqware_examples/" + this.dir.getName();
		    conf.setProperty(OozieClient.APP_PATH, app_path);
		    conf.setProperty("jobTracker", "localhost:8021");
		    conf.setProperty("nameNode", "hdfs://localhost:8020");
		    conf.setProperty("queueName", "default");
	//	    conf.setProperty("inputDir", "/home/seqware/dot");
	//	    conf.setProperty("outputDir", "/home/seqware/dot");
	//	    conf.setProperty("mapred.output.dir", "/home/seqware/dot");
	//	    conf.setProperty("mapred.work.output.dir", "/home/seqware/dot");
		    String jobId = wc.run(conf);
		    System.out.println("Workflow job submitted");
	
		    while (wc.getJobInfo(jobId).getStatus() == WorkflowJob.Status.RUNNING) {
		       System.out.println("Workflow job running ...");
		       printWorkflowInfo(wc.getJobInfo(jobId));
		       Thread.sleep(10 * 1000);
		    }
		    System.out.println("Workflow job completed ...");
		    printWorkflowInfo(wc.getJobInfo(jobId));
	
		    System.out.println(wc.getJobInfo(jobId)); 
		    //LocalOozie.stop();
		}catch(OozieClientException oozieClientException){
		    oozieClientException.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return ret;
	}
	
	private void printWorkflowInfo(WorkflowJob wf) {
        System.out.println("Application Path   : " + wf.getAppPath());
        System.out.println("Application Name   : " + wf.getAppName());
        System.out.println("Application Status : " + wf.getStatus());
        System.out.println("Application Actions:");
        for (WorkflowAction action : wf.getActions()) {
        System.out.println(MessageFormat.format("   Name: {0} Type: {1} Status: {2}", action.getName(),
                                                    action.getType(), action.getStatus()));
        }
        System.out.println();
    }

	
	/**
	 * copy the local dir to HDFS
	 */
	private void setupHDFS(AbstractWorkflowDataModel objectModel) {
		Configuration conf = new Configuration();
		conf.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
		conf.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));
		conf.addResource(new Path("/etc/hadoop/conf/mapred-site.xml"));

		FileSystem fileSystem = null;
		try {
			fileSystem = FileSystem.get(conf);
			Path path = new Path("seqware_examples/"+this.dir.getName());
			fileSystem.mkdirs(path);
			Path pathlib = new Path("seqware_examples/"+this.dir.getName() + "/lib");
			fileSystem.mkdirs(pathlib);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			this.copyFromLocal(fileSystem, this.work_dir + "/" +this.dir.getName() + "/job.properties",
					"seqware_examples/"+this.dir.getName() );
			this.copyFromLocal(fileSystem, this.work_dir + "/" +this.dir.getName() + "/workflow.xml",
					"seqware_examples/"+this.dir.getName());
			//copy lib
			this.copyFromLocal(fileSystem, objectModel.getWorkflowBaseDir() + 
					"/lib/seqware-distribution-"+objectModel.getTags().get("seqware_version")+"-full.jar",
					"seqware_examples/"+this.dir.getName()+"/lib");
		} catch (IOException e1) {
			e1.printStackTrace();
		} 			
		
		try {
			fileSystem.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @throws IOException 
	 * 
	 */
	private void setupEnvironment() {
		//create a working directory in /nfs
		//hardcode for now
		
		try {
			this.dir = FileTools.createDirectoryWithUniqueName(new File(work_dir), "oozie");
			this.dir.setWritable(true, false);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//generate job.properties
		this.generateJobProperties();
		//create lib dir
		File lib = new File(this.dir,"lib");
		lib.mkdir();
	}
	
	/**
	 * return a workflow.xml for hadoop
	 * @param objectModel
	 * @return
	 */
	private File parseDataModel(AbstractWorkflowDataModel objectModel) {
		File file = new File(this.dir,"workflow.xml");
		//add oozie_working_dir
		objectModel.getConfigs().put("oozie_working_dir", this.dir.getAbsolutePath());
		//generate dax
		OozieWorkflowXmlGenerator daxv2 = new OozieWorkflowXmlGenerator();
		daxv2.generateWorkflowXml(objectModel, file.getAbsolutePath());
		return file;
	}
	
	private void generateJobProperties() {
		File file = new File(this.dir,"job.properties");
		try {
			FileWriter fw = new FileWriter(file);
			fw.write("nameNode=hdfs://localhost:8020\n");
			fw.write("jobTracker=localhost:8021\n");
			fw.write("queueName=default\n");
			fw.write("oozie.wf.application.path=${nameNode}/user/${user.name}/oozie/" + this.dir.getName());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void copyFromLocal (FileSystem fileSystem, String source, String dest) throws IOException {
		Path srcPath = new Path(source);
			 
		Path dstPath = new Path(dest);
		// Check if the file already exists
		if (!(fileSystem.exists(dstPath))) {
			System.out.println("No such destination " + dstPath);
			return;
		}
			 
		// Get the filename out of the file path
		String filename = source.substring(source.lastIndexOf('/') + 1, source.length());
			 
		try{
			fileSystem.copyFromLocalFile(srcPath, dstPath);
			System.out.println("File " + filename + "copied to " + dest);
		}catch(Exception e){
			System.err.println("Exception caught! :" + e);
			System.exit(1);
		}
	}
}