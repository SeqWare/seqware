package ca.on.oicr.pde;


import java.util.Map;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.model.Job;
import net.sourceforge.seqware.pipeline.workflowV2.model.SqwFile;

public class WorkflowClient extends AbstractWorkflowDataModel {

    @Override
    public Map<String, SqwFile> setupFiles() {
        SqwFile file0 = this.createFile("file_in_0");
        file0.setSourcePath(this.getWorkflowBaseDir()+"/data/input.txt");
        file0.setType("text/plain");
        file0.setIsInput(true);
                
        SqwFile file1 = this.createFile("file_out");
        file1.setSourcePath("dir1/output");
        file1.setType("text/plain");
        file1.setIsOutput(true);
        file1.setForceCopy(true);
        
        return this.getFiles();
    }
    
    @Override
    public void setupDirectory() {
        this.addDirectory("dir1");
    }
    
    @Override
    public void buildWorkflow() {
        Job job00 = this.getWorkflow().createBashJob("bash_mkdir");
        job00.getCommand().addArgument("mkdir test1");      
        
        Job job10 = this.getWorkflow().createBashJob("bash_cp");
        job10.setCommand("cp " + this.getFiles().get("file_in_0").getProvisionedPath() + " test1");
        job10.addParent(job00);
        
        Job job11 = this.getWorkflow().createBashJob("bash_cp");
        job11.setCommand("cp " + this.getFiles().get("file_in_0").getProvisionedPath() + " dir1/output");
        job11.addParent(job00);
               

    }

}
