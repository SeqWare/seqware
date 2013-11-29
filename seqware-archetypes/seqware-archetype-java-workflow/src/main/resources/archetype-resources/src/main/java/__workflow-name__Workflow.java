#set( $symbol_dollar = '$' )
package ${package};

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.model.Job;
import net.sourceforge.seqware.pipeline.workflowV2.model.SqwFile;
/**
 * <p>For more information on developing workflows, see the documentation at
 * <a href="http://seqware.github.io/docs/6-pipeline/java-workflows/">SeqWare Java Workflows</a>.</p>
 * 
 * Quick reference for the order of methods called:
 * 1. setupDirectory
 * 2. setupFiles
 * 3. setupWorkflow
 * 4. setupEnvironment
 * 5. buildWorkflow
 * 
 * See the SeqWare API for 
 * <a href="http://seqware.github.io/javadoc/stable/apidocs/net/sourceforge/seqware/pipeline/workflowV2/AbstractWorkflowDataModel.html#setupDirectory%28%29">AbstractWorkflowDataModel</a> 
 * for more information.
 */
public class ${workflow-name}Workflow extends AbstractWorkflowDataModel {

    private boolean manualOutput=false;
    private String catPath, echoPath;
    private String greeting ="";

    private void init() {
	try {
	    //optional properties
	    if (hasPropertyAndNotNull("manual_output")) {
		manualOutput = Boolean.valueOf(getProperty("manual_output"));
	    }
	    if (hasPropertyAndNotNull("greeting")) {
		greeting = getProperty("greeting");
	    }
	    //these two properties are essential to the workflow. If they are null or do not 
	    //exist in the INI, the workflow should exit.
	    catPath = getProperty("cat");
	    echoPath = getProperty("echo");
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    @Override
    public void setupDirectory() {
	//since setupDirectory is the first method run, we use it to initialize variables too.
	init();
        // creates a dir1 directory in the current working directory where the workflow runs
        this.addDirectory("dir1");
    }
 
    @Override
    public Map<String, SqwFile> setupFiles() {
      try {
        // register an plaintext input file using the information from the INI
	// provisioning this file to the working directory will be the first step in the workflow
        SqwFile file0 = this.createFile("file_in_0");
        file0.setSourcePath(getProperty("input_file"));
        file0.setType("text/plain");
        file0.setIsInput(true);
      
      } catch (Exception ex) {
        ex.printStackTrace();
	System.exit(1);
      }
      return this.getFiles();
    }
    
   
    @Override
    public void buildWorkflow() {

        // a simple bash job to call mkdir
	// note that this job uses the system's mkdir (which depends on the system being *nix)
        Job mkdirJob = this.getWorkflow().createBashJob("bash_mkdir");
        mkdirJob.getCommand().addArgument("mkdir test1");      
       
	String inputFilePath = this.getFiles().get("file_in_0").getProvisionedPath();
	 
        // a simple bash job to cat a file into a test file
	// the file is not saved to the metadata database
        Job copyJob1 = this.getWorkflow().createBashJob("bash_cp");
        copyJob1.setCommand(catPath + " " + inputFilePath + "> test1");
        copyJob1.addParent(mkdirJob);
        
        // a simple bash job to echo to an output file and concat an input file
	// the file IS saved to the metadata database
        Job copyJob2 = this.getWorkflow().createBashJob("bash_cp");
	copyJob2.getCommand().addArgument(echoPath).addArgument(greeting).addArgument(" > ").addArgument("dir1/output");
	copyJob2.getCommand().addArgument(";");
	copyJob2.getCommand().addArgument(catPath + " " +inputFilePath+ " >> dir1/output");
        copyJob2.addParent(mkdirJob);
	copyJob2.addFile(createOutputFile("dir1/output", "txt/plain", manualOutput));        

    }

    private SqwFile createOutputFile(String workingPath, String metatype, boolean manualOutput) {
    // register an output file
        SqwFile file1 = new SqwFile(); 
        file1.setSourcePath(workingPath);
        file1.setType(metatype);
        file1.setIsOutput(true);
        file1.setForceCopy(true);
	
        // if manual_output is set in the ini then use it to set the destination of this file
        if (manualOutput) { 
	    file1.setOutputPath(this.getMetadata_output_file_prefix() + getMetadata_output_dir() + "/" + workingPath);
	} else {
	    file1.setOutputPath(this.getMetadata_output_file_prefix() + getMetadata_output_dir() + "/" 
		+ this.getName() + "_" + this.getVersion() + "/" + this.getRandom() + "/" + workingPath);
	}
	return file1;
    }

}
