---

title:                 "Workflow Examples"
toc_includes_sections: true
markdown:              basic

---

Java Example
	package net.sourceforge.seqware;


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
        
	        SqwFile file2 = this.createFile("file_in_1");
	        file2.setSourcePath(this.getWorkflowBaseDir()+"/data/input.txt");
	        file2.setType("text/plain");
	        file2.setIsInput(true);
	        
	        SqwFile file3 = this.createFile("file_in_2");
	        file3.setSourcePath(this.getWorkflowBaseDir()+"/data/input1.txt");
	        file3.setType("text/plain");
	        file3.setIsInput(true);
         
	        SqwFile file1 = this.createFile("file_out"); 
	        file1.setSourcePath("dir2/output");
	        file1.setType("text/plain");
	        file1.setIsOutput(true);
	        file1.setForceCopy(true);
        
	        return this.getFiles();
	    }
    
	    @Override
	    public void setupDirectory() {
	        this.addDirectory("dir1");
	        this.addDirectory("dir2");
	    }
    
	    @Override
	    public void buildWorkflow() {
	        Job job00 = this.getWorkflow().createBashJob("bash_mkdir");
	        job00.getCommand().addArgument("mkdir test1");      
        
	        Job job01 = this.getWorkflow().createPerlJob("perl_mkdir", 		this.getWorkflowBaseDir()+"/dependency/perltest.pl");
	        job01.getCommand().addArgument("test2");
 
	        Job job02 = this.getWorkflow().createBashJob("bash_mkdir");
	        job02.setCommand("mkdir test3");
	        
	        Job job03 = this.getWorkflow().createBashJob("bash_mkdir");
	        job03.setCommand("mkdir test4");
        
	        Job job10 = this.getWorkflow().createBashJob("bash_cp");
	        job10.setCommand("cp " + this.getFiles().get("file_in_0").getProvisionedPath() + " test1");
	        job10.addParent(job00);
        
	        Job job11 = this.getWorkflow().createBashJob("bash_cp");
	        job11.setCommand("cp " + this.getFiles().get("file_in_1").getProvisionedPath() + " test2");
	        job11.addParent(job01);
               
	        Job job13 = this.getWorkflow().createBashJob("bash_cp");
	        job13.setCommand("cp " + this.getFiles().get("file_in_2").getProvisionedPath() + " test4");
	        job13.addParent(job03);
	        SqwFile fileout13 =  new SqwFile();
	        fileout13.setIsOutput(true);
	        fileout13.setType("text/key-value");
	        fileout13.setSourcePath("test4/input1.txt");
	        fileout13.setForceCopy(true);
	        job13.addFile(fileout13);
        
	        Job job20 = this.getWorkflow().createBashJob("bash_cat");
	        job20.setCommand("cat test1/* test2/* > dir1/output_tmp");
	        job20.addParent(job10);
	        job20.addParent(job11);
	        SqwFile fileout = new SqwFile();
	        fileout.setIsOutput(true);
	        fileout.setType("text/key-value");
	        fileout.setSourcePath("dir1/output_tmp");
	        fileout.setForceCopy(true);
	        job20.addFile(fileout);
        
	        Job job30 = this.getWorkflow().createBashJob("bash_3");
	        SqwFile file = new SqwFile();
	        file.setIsInput(true);
	        file.setType("text/key-value");
	        file.setSourcePath(this.getWorkflowBaseDir()+"/data/input3.txt");
	        job30.addFile(file);
	        job30.getCommand().addArgument("cat " + file.getProvisionedPath() + " dir1/output_tmp > test3/input3" );
	        job30.addParent(job20);
	        job30.addParent(job02);
        
	        Job job31 = this.getWorkflow().createBashJob("bash_3");
	        job31.setCommand("cat test3/input3 > dir2/output");
	        job31.addParent(job20);
        

	    }

	}

FTL Examples

	<workflow>
		<files>
			<file name="file_in_0" type="text/plain" forcecopy="false" sourcepath="${workflow_base_dir}/data/input.txt" input="true"/>
			<file name="file_in_1" type="text/plain" forcecopy="false" sourcepath="${workflow_base_dir}/data/input.txt" input="true"/>
			<file name="file_in_3" type="text/plain" forcecopy="false" sourcepath="${workflow_base_dir}/data/input1.txt" input="true"/>
			<file name="file_out" type="text/plain" forcecopy="true" sourcepath="${workflow_base_dir}/dir2/output" input="false"/>
		</files>
		<dirs>
			<dir>dir1</dir>
			<dir>dir2</dir>
		</dirs>
	    <jobs>
	        <job refid="ID000">
         	   <algorithm>bash_mkdir</algorithm>
	            <argument>mkdir test1</argument>
	        </job>
	        <job refid="ID001" type="perl" script="${workflow_base_dir}/dependency/perltest.pl">
         	   <algorithm>perl_mkdir</algorithm>
	            <argument>test2</argument>
	        </job>
	        <job refid="ID002">
         	   <algorithm>bash_mkdir</algorithm>
	            <argument>mkdir test3</argument>
	        </job>
	        <job refid="ID003">
         	   <algorithm>bash_mkdir</algorithm>
	            <argument>mkdir test4</argument>
	        </job>
        
	        <job refid="ID100">
         	   <algorithm>bash_cp</algorithm>
	            <argument>cp ${workflow_base_dir}/data/input.txt test1</argument>
         	   <parent>ID000</parent>
	        </job>
	        <job refid="ID101">
         	   <algorithm>bash_cp</algorithm>
	            <argument>cp ${workflow_base_dir}/data/input.txt test2</argument>
         	   <parent>ID001</parent>
	        </job>
	        <job refid="ID102">
         	   <algorithm>bash_cp</algorithm>
	            <argument>cp ${workflow_base_dir}/data/input1.txt test3</argument>
         	   <parent>ID002</parent>
	            <file name="file_out" type="text/plain" forcecopy="true" sourcepath="dir1/output13_tmp" input="false"/>
	        </job>
	    </jobs>
	</workflow>