---

title:                 "Debugging, Troubleshooting, & Restarting Workflow"
markdown:              advanced
is_dynamic:            true
toc_includes_sections: true

---

## Overview

## The Oozie Engine

The Oozie Engine has both command line tools and a web interface for interacting with workflow status information.
The web interface is particularly helpful, you can find it typically at:

	http://<host>:11000/oozie/

### Debugging Workflows

Navigate to the Oozie console at the URL above. Recent workflows and their status can be found here. Click on the 
workflow for more information including stderr/stdout for the jobs.

### Restarting Workflows 

While Oozie does support workflow re-submission (via the 'seqware workflow-run retry' command) the recommended approach for failed workflows in SeqWare is simply to resubmit the workflows. You can either do this by using the 'seqware workflow schedule' command anew or by using 'seqware workflow-run reschedule' to re-schedule a new workflow-run using exactly the same configuration as a previous schedule of a workflow. 

## Debugging and Troubleshooting Tutorial

### Debugging Workflows that Fail During Launch-time

Here, we will demonstrate a few things that can go wrong when launching a buggy workflow. In this situation, you will be informed about the failures by the WorkflowLauncher. You can demonstrate these failures by running the workflows using the development command 'seqware bundle launch'. On the seqware VM, you will be able to read the log associated with 'seqware workflow-run launch-scheduled' in the crontab for the seqware user.

First, create the workflow that we will demonstrate debugging with. 

	mvn archetype:generate -DarchetypeCatalog=local -Dpackage=com.github.seqware -DgupId=com.github.seqware -DarchetypeArtifactId=seqware-archetype-java-workflow -Dversion=1.0-SNAPSHOT -DarchetypeGroupId=com.github.seqware -DartifactId=BuggyWorkflow -Dworkflow-name=BuggyWorkflow -B
	...
	[INFO] ------------------------------------------------------------------------
	[INFO] BUILD SUCCESS
	[INFO] ------------------------------------------------------------------------
	[INFO] Total time: 0.885s
	[INFO] Finished at: Tue Aug 05 13:19:14 EDT 2014
	[INFO] Final Memory: 13M/480M
	[INFO] ------------------------------------------------------------------------

Next, modify the Java workflow class to have a large number of errors

	package com.github.seqware;

	import java.io.File;
	import java.util.Map;
	import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
	import net.sourceforge.seqware.pipeline.workflowV2.model.Job;
	import net.sourceforge.seqware.pipeline.workflowV2.model.SqwFile;

	public class BuggyWorkflowWorkflow extends AbstractWorkflowDataModel {

	    private boolean manualOutput = false;
	    private String catPath, echoPath;
	    private String greeting = "";

	    private void init() {
		try {
		    // optional properties
		    if (hasPropertyAndNotNull("manual_output")) {
			manualOutput = Boolean.valueOf(getProperty("manual_output"));
		    }
		    if (hasPropertyAndNotNull("greeting")) {
			greeting = getProperty("greeting");
		    }
		    // these two properties are essential to the workflow. If they are null or do not
		    // exist in the INI, the workflow should exit.
		    catPath = getProperty("cat");
		    echoPath = getProperty("echo");
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new RuntimeException(e);
		}
		// do something silly here
		System.err.println("Let's divide by zero");
		int y = 10 / 0;
		System.out.println(y);
	    }

	    @Override
	    public void setupDirectory() {
		// since setupDirectory is the first method run, we use it to initialize variables too.
		init();
		// creates a dir1 directory in the current working directory where the workflow runs
		this.addDirectory("dir1");
		// do something silly here
		System.err.println("Let's use a null pointer");
		File file = null;
		if (1 * 10 == 5) {
		    // need to fool the compiler into thinking this might be not null
		    file = new File("random file name");
		}
		System.out.println(file.getAbsoluteFile());
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
		    throw new RuntimeException(ex);
		}
		// do something silly here
		System.err.println("Let's use an incorrect array index");
		int[] arr = new int[5];
		for (int i = 0; i <= arr.length; i++) {
		    System.out.println(arr[i]);
		}
		return this.getFiles();
	    }

	    @Override
	    public void buildWorkflow() {

		// a simple bash job to call mkdir
		// note that this job uses the system's mkdir (which depends on the system being *nix)
		// this also translates into a 3000 h_vmem limit when using sge
		Job mkdirJob = this.getWorkflow().createBashJob("bash_mkdir").setMaxMemory("3000");
		mkdirJob.getCommand().addArgument("mkdir test1");

		String inputFilePath = this.getFiles().get("file_in_0").getProvisionedPath();

		// a simple bash job to cat a file into a test file
		// the file is not saved to the metadata database
		Job copyJob1 = this.getWorkflow().createBashJob("bash_cp").setMaxMemory("3000");
		copyJob1.setCommand(catPath + " " + inputFilePath + "> test1/test.out");
		copyJob1.addParent(mkdirJob);

		// a simple bash job to echo to an output file and concat an input file
		// the file IS saved to the metadata database
		Job copyJob2 = this.getWorkflow().createBashJob("bash_cp").setMaxMemory("3000");
		copyJob2.getCommand().addArgument(echoPath).addArgument(greeting).addArgument(" > ").addArgument("dir1/output");
		copyJob2.getCommand().addArgument(";");
		copyJob2.getCommand().addArgument(catPath + " " + inputFilePath + " >> dir1/output");
		copyJob2.addParent(mkdirJob);
		copyJob2.addFile(createOutputFile("dir1/output", "txt/plain", manualOutput));

		// do something silly here
		System.err.println("Create a negative size array ");
		int[] arr = new int[-5];
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
		    file1.setOutputPath(this.getMetadata_output_file_prefix() + getMetadata_output_dir() + "/" + this.getName() + "_"
			    + this.getVersion() + "/" + this.getRandom() + "/" + workingPath);
		}
		return file1;
	    }

	}

Build the workflow and then test launch it. You should see a stack trace which demonstrates an exception occuring during the launching of the workflow. 

	$ mvn clean install
	...
	$ seqware bundle launch --dir target/Workflow_Bundle_BuggyWorkflow_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>/
	...
	Caused by: java.lang.ArithmeticException: / by zero
		at com.github.seqware.BuggyWorkflowWorkflow.init(BuggyWorkflowWorkflow.java:46)
		at com.github.seqware.BuggyWorkflowWorkflow.setupDirectory(BuggyWorkflowWorkflow.java:53)
		... 30 more
	[SeqWare Pipeline] FATAL [2014/08/05 13:35:27] | Exception constructing data model, failing workflow 28
	java.lang.RuntimeException: java.lang.reflect.InvocationTargetException
		at net.sourceforge.seqware.common.util.Rethrow.rethrow(Rethrow.java:26)
		at net.sourceforge.seqware.pipeline.workflowV2.WorkflowDataModelFactory.getWorkflowDataModel(WorkflowDataModelFactory.java:216)

Fix the error in the init method as suggested by the stack trace and rebuild plus relaunch. 

	$ vim src/main/java/com/github/seqware/BuggyWorkflowWorkflow.java 
	$ mvn clean install
	$ seqware bundle launch --dir target/Workflow_Bundle_BuggyWorkflow_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>/
	...
	Caused by: java.lang.NullPointerException
		at com.github.seqware.BuggyWorkflowWorkflow.setupDirectory(BuggyWorkflowWorkflow.java:59)
		... 30 more
	[SeqWare Pipeline] FATAL [2014/08/05 13:46:43] | Exception constructing data model, failing workflow 30
	java.lang.RuntimeException: java.lang.reflect.InvocationTargetException

Fix the error in the setupDirectory method and rebuild plus relaunch.  

	$ vim src/main/java/com/github/seqware/BuggyWorkflowWorkflow.java 
	$ mvn clean install
	$ seqware bundle launch --dir target/Workflow_Bundle_BuggyWorkflow_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>/
	...
	Caused by: java.lang.ArrayIndexOutOfBoundsException: 5
		at com.github.seqware.BuggyWorkflowWorkflow.setupFiles(BuggyWorkflowWorkflow.java:72)
		... 30 more

You know the drill by now, fix the error in the buildWorkflow method and rebuild plus relaunch. 


### Debugging Workflows that Fail During Run-time

The previous section concerned workflows that fail during launch time when the workflow data model is created. This section concerns workflows that fail when they are run. Use the following code for your workflow class.

	package com.github.seqware;

	import java.io.File;
	import java.util.Map;
	import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
	import net.sourceforge.seqware.pipeline.workflowV2.model.Job;
	import net.sourceforge.seqware.pipeline.workflowV2.model.SqwFile;

	public class BuggyWorkflowWorkflow extends AbstractWorkflowDataModel {

	    private boolean manualOutput = false;
	    private String catPath, echoPath;
	    private String greeting = "";

	    private void init() {
		try {
		    // optional properties
		    if (hasPropertyAndNotNull("manual_output")) {
			manualOutput = Boolean.valueOf(getProperty("manual_output"));
		    }
		    if (hasPropertyAndNotNull("greeting")) {
			greeting = getProperty("greeting");
		    }
		    // these two properties are essential to the workflow. If they are null or do not
		    // exist in the INI, the workflow should exit.
		    catPath = getProperty("cat");
		    echoPath = getProperty("echo");
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new RuntimeException(e);
		}
	    }

	    @Override
	    public void setupDirectory() {
		// since setupDirectory is the first method run, we use it to initialize variables too.
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
		    throw new RuntimeException(ex);
		}
		return this.getFiles();
	    }

	    @Override
	    public void buildWorkflow() {

		// a simple bash job to call mkdir
		// note that this job uses the system's mkdir (which depends on the system being *nix)
		// this also translates into a 3000 h_vmem limit when using sge
		Job mkdirJob = this.getWorkflow().createBashJob("bash_mkdir").setMaxMemory("3000");
		mkdirJob.getCommand().addArgument("mkdir $fubar_test");
		// mkdirJob.getCommand().addArgument("mkdir test1");

		String inputFilePath = this.getFiles().get("file_in_0").getProvisionedPath();

		// a simple bash job to cat a file into a test file
		// the file is not saved to the metadata database
		Job copyJob1 = this.getWorkflow().createBashJob("bash_cp").setMaxMemory("3000");
		copyJob1.setCommand(catPath + " " + inputFilePath + "> fubar/test.out");
		// copyJob1.setCommand(catPath + " " + inputFilePath + "> test1/test.out");
		copyJob1.addParent(mkdirJob);

		// a simple bash job to echo to an output file and concat an input file
		// the file IS saved to the metadata database
		Job copyJob2 = this.getWorkflow().createBashJob("bash_cp").setMaxMemory("3000");
		copyJob2.getCommand().addArgument(echoPath).addArgument(greeting).addArgument(" > ").addArgument("fubar/output");
		// copyJob2.getCommand().addArgument(echoPath).addArgument(greeting).addArgument(" > ").addArgument("dir1/output");
		copyJob2.getCommand().addArgument(";");
		copyJob2.getCommand().addArgument(catPath + " " + inputFilePath + " >> dir1/output");
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
		    file1.setOutputPath(this.getMetadata_output_file_prefix() + getMetadata_output_dir() + "/" + this.getName() + "_"
			    + this.getVersion() + "/" + this.getRandom() + "/" + workingPath);
		}
		return file1;
	    }

	}

Build the workflow and launch using 'seqware bundle launch'. You should see the workflow launch successfully, submit to Oozie, and then the WorkflowWatcher start up to watch the running of the workflow: 

	$ mvn clean install
	$ seqware bundle launch --dir target/Workflow_Bundle_BuggyWorkflow_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>/
	...
	Files copied to /usr/tmp/oozie/oozie-3967f3de-6153-4618-806e-2e29a51293b9
	Submitted Oozie job: 0000003-140801172645564-oozie-oozi-W
	[--plugin, io.seqware.pipeline.plugins.WorkflowWatcher, --, --workflow-run-accession, 38]
	...
	Workflow job running ...
	Application Path   : hdfs://localhost:8020/user/dyuen/seqware_workflow/oozie-3967f3de-6153-4618-806e-2e29a51293b9
	Application Name   : BuggyWorkflow
	Application Status : RUNNING
	Application Actions:
	   Name: :start: Type: :START: Status: OK
	   Name: start_0 Type: sge Status: OK
	   Name: provisionFile_file_in_0_1 Type: sge Status: OK
	   Name: bash_mkdir_2 Type: sge Status: RUNNING

	Workflow job completed ...
	Application Path   : hdfs://localhost:8020/user/dyuen/seqware_workflow/oozie-3967f3de-6153-4618-806e-2e29a51293b9
	Application Name   : BuggyWorkflow
	Application Status : KILLED
	Application Actions:
	   Name: :start: Type: :START: Status: OK
	   Name: start_0 Type: sge Status: OK
	   Name: provisionFile_file_in_0_1 Type: sge Status: OK
	   Name: bash_mkdir_2 Type: sge Status: ERROR
	   Name: fail Type: :KILL: Status: OK
	The method 'do_run' exited abnormally so the Runner will terminate here!
	Return value was: 15


Notice that the workflow was killed due to an error in the 'bash_mkdir_2' step. 
The easiest way to determine why that step failed is to pull back the stdout and stderr from the steps of the workflow. This information is saved in the metadb via 'seqware workflow-run propagate-statuses' which is run on a schedule on the seqware VM. Note that you can also look at the raw working directory which is reported as the Workflow Run Working Dir by the 'seqware workflow-run report'.

	$ seqware workflow-run propagate-statuses
	[2014/08/05 14:45:26] | Propagated workflow engine statuses
	$ seqware workflow-run report --accession 38
	-[ RECORD 0 ]------------------+----------------------------------------------------------
	Workflow                       | BuggyWorkflow 1.0-SNAPSHOT                                
	Workflow Run SWID              | 38                                                        
	Workflow Run Status            | failed                                                    
	Workflow Run Create Timestamp  | 2014-08-05 14:42:09.985                                   
	Workflow Run Host              | master                                                 
	Workflow Run Working Dir       | /usr/tmp/oozie/oozie-3967f3de-6153-4618-806e-2e29a51293b9 
	Workflow Run Engine ID         | 0000003-140801172645564-oozie-oozi-W                      

	$ seqware workflow-run stderr --accession 38
	Created file 38.err
	$ cat 38.err
	-----------------------------------------------------------------------
	Job Name: start_0
	Job ID:   3241
	File:     /usr/tmp/oozie/oozie-3967f3de-6153-4618-806e-2e29a51293b9/generated-scripts/start_0.e3241
	Updated:  Tue Aug 05 14:42:20 EDT 2014
	Contents:

	-----------------------------------------------------------------------

	-----------------------------------------------------------------------
	Job Name: provisionFile_file_in_0_1
	Job ID:   3242
	File:     /usr/tmp/oozie/oozie-3967f3de-6153-4618-806e-2e29a51293b9/generated-scripts/provisionFile_file_in_0_1.e3242
	Updated:  Tue Aug 05 14:42:50 EDT 2014
	Contents:
	-----------------------------------------------------------------------

	-----------------------------------------------------------------------
	Job Name: bash_mkdir_2
	Job ID:   3243
	File:     /usr/tmp/oozie/oozie-3967f3de-6153-4618-806e-2e29a51293b9/generated-scripts/bash_mkdir_2.e3243
	Updated:  Tue Aug 05 14:43:20 EDT 2014
	Contents:
	mkdir: missing operand
	Try `mkdir --help' for more information.
	mkdir: missing operand
	Try `mkdir --help' for more information.
	The method 'do_run' exited abnormally so the Runner will terminate here!
	Return value was: 1
	-----------------------------------------------------------------------

Fix the error, rebuild, and relaunch. The workflow should now proceed onto the next errors in the parallel steps 'bash_cp_3' and 'bash_cp_4'. Use the same kind of procedure to locate the error and fix it. The relevant error information from the stderr should be as follows.

	-----------------------------------------------------------------------
	Job Name: bash_cp_3
	Job ID:   3250
	File:     /usr/tmp/oozie/oozie-0fe6d124-1768-48a2-99f7-1f541ef6629a/generated-scripts/bash_cp_3.e3250
	Updated:  Tue Aug 05 15:06:20 EDT 2014
	Contents:
	/usr/tmp/oozie/oozie-0fe6d124-1768-48a2-99f7-1f541ef6629a/generated-scripts/bash_cp_3.sh: line 6: fubar/test.out: No such file or directory
	/usr/tmp/oozie/oozie-0fe6d124-1768-48a2-99f7-1f541ef6629a/generated-scripts/bash_cp_3.sh: line 6: fubar/test.out: No such file or directory
	The method 'do_run' exited abnormally so the Runner will terminate here!
	Return value was: 1
	-----------------------------------------------------------------------

	-----------------------------------------------------------------------
	Job Name: bash_cp_4
	Job ID:   3251
	File:     /usr/tmp/oozie/oozie-0fe6d124-1768-48a2-99f7-1f541ef6629a/generated-scripts/bash_cp_4.e3251
	Updated:  Tue Aug 05 15:06:20 EDT 2014
	Contents:
	/usr/tmp/oozie/oozie-0fe6d124-1768-48a2-99f7-1f541ef6629a/generated-scripts/bash_cp_4.sh: line 6: fubar/output: No such file or directory
	/usr/tmp/oozie/oozie-0fe6d124-1768-48a2-99f7-1f541ef6629a/generated-scripts/bash_cp_4.sh: line 6: fubar/output: No such file or directory
	The method 'do_run' exited abnormally so the Runner will terminate here!
	Return value was: 1
	-----------------------------------------------------------------------

Fix the two errors and your workflow should now run to completion. 


## The Pegasus Engine - Unsupported

The Pegasus engine is deprecated in 1.0.X and is no longer supported in 11.0.
The Pegasus Engine used <tt>pegasus-status</tt> as the primary command for monitoring the status of workflows.

### Debugging Workflows

<%= render '/includes/debug/pegasus_debug/' %>

### Restarting Workflows 

<%= render '/includes/debug/pegasus_restart/' %>

