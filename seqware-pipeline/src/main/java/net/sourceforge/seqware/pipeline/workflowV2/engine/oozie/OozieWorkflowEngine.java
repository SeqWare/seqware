package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowEngine;


public class OozieWorkflowEngine extends AbstractWorkflowEngine {

	private File dir;
	@Override
	public ReturnValue launchWorkflow(AbstractWorkflowDataModel objectModel) {
		//parse objectmodel 
		ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);
		this.setupEnvironment();
		this.parseDataModel(objectModel);
		//ret = this.runWorkflow(objectModel, dax);
		return ret;
	}
	
	/**
	 * @throws IOException 
	 * 
	 */
	private void setupEnvironment() {
		//create a working directory in /nfs
		//hardcode for now
		String work_dir = "/home/seqware/oozie";
		try {
			this.dir = FileTools.createDirectoryWithUniqueName(new File(work_dir), "oozie");
		} catch (IOException e) {
			e.printStackTrace();
		}
		//make the same folder in hdfs
/*		Configuration conf = new Configuration();
		conf.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
        conf.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));
        //copy directory to HDFS
        
		FileSystem fileSystem;
		try {
			fileSystem = FileSystem.get(conf);
			Path path = new Path("seqware_examples/"+this.dir.getName());
			fileSystem.mkdirs(path);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		//generate job.properties
		this.generateJobProperties();
		//copy the seqware-pipeline.jar
		
		//generate workflow.xml
	}
	
	/**
	 * return a workflow.xml for hadoop
	 * @param objectModel
	 * @return
	 */
	private File parseDataModel(AbstractWorkflowDataModel objectModel) {
		File file = new File(this.dir,"workflow.xml");
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
	

}