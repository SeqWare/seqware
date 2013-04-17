package ${package};


import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.model.Job;
import net.sourceforge.seqware.pipeline.workflowV2.model.SqwFile;

public class WorkflowClient extends AbstractWorkflowDataModel {

    @Override
    public Map<String, SqwFile> setupFiles() {

      try {

        // register an input file
        SqwFile file0 = this.createFile("file_in_0");
        file0.setSourcePath(this.getWorkflowBaseDir()+"/data/input.txt");
        file0.setType("text/plain");
        file0.setIsInput(true);

        // register an output file
        SqwFile file1 = this.createFile("file_out");
        file1.setSourcePath("dir1/output");
        file1.setType("text/plain");
        file1.setIsOutput(true);
        file1.setForceCopy(true);
        // if output_file is set in the ini then use it to set the destination of this file
        if (hasPropertyAndNotNull("output_file")) { file1.setOutputPath(getProperty("output_file")); }
        return this.getFiles();

      } catch (Exception ex) {
        Logger.getLogger(WorkflowClient.class.getName()).log(Level.SEVERE, null, ex);
        return(null);
      }
    }
    
    @Override
    public void setupDirectory() {
        // creates a dir1 directory in the current working directory where the workflow runs
        this.addDirectory("dir1");
    }
    
    @Override
    public void buildWorkflow() {

        // a simple bash job to call mkdir
        Job job00 = this.getWorkflow().createBashJob("bash_mkdir");
        job00.getCommand().addArgument("mkdir test1");      
        
        // a simple bash job to call cp on a file
        Job job10 = this.getWorkflow().createBashJob("bash_cp");
        job10.setCommand("cp " + this.getFiles().get("file_in_0").getProvisionedPath() + " test1");
        job10.addParent(job00);
        
        // a simple bash job to copy an input to an output file
        Job job11 = this.getWorkflow().createBashJob("bash_cp");
        job11.setCommand("cp " + this.getFiles().get("file_in_0").getProvisionedPath() + " dir1/output");
        job11.addParent(job00);
               

    }

}
