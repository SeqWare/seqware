---

title:                 "Developer Tutorial"
markdown:              advanced
toc_includes_sections: true

---

This guide picks up where the [User Tutorial](/docs/3-getting-started/user-tutorial/) left off. In that previous guide we showed you how to start up your local VM, create studies, experiments, and samples, associate an input file with a sample, and then launch a workflow to process that file.  This process is the same generic process we use at OICR to analyze samples from fastq to, eventually, annotated variants.  In this production system the workflows are fairly complex (they include branching and looping) and we string multiple worklfows together (output of one as input for the next) using <kbd>deciders</kbd>.

The next step presented in this tutorial is to create a workflow of your own based on the HelloWorld that comes bundled with the VM.  In theory you could use either a local VM or an Amazon instance to follow the tutorial below but in our case we will base it on the local VM.

## By the End of This Tutorial

By the end of these tutorials you will:

* create a new SeqWare Pipeline workflow bundle based on HelloWorld
* package, install, schedule, and run your new workflow bundle in Pipeline and MetaDB
* generate a report on the outputs of your workflows in Pipeline and Portal
* be prepared to move on to more detailed documentation for each sub-project


<p class="warning"><strong>WARNING:</strong>These directions are updated to include Maven archetypes.  The traditional maven archetypes do not mark items in the ${workflow_bundle_dir}/bin as executable.  This will cause workflows to immediatly fail and you have to "chmod" the binaries in the workflow bundles created with "mvn install". For example "chmod -R a+x Workflow_Bundle_simple-legacy-ftl-workflow/1.0-SNAPSHOT/bin/*".</p>

## First Steps

Please launch your local VM in VirtualBox and login as user <kbd>seqware</kbd>, password <kbd>seqware</kbd> at this time. Click on the "SeqWare Directory" link on the desktop which will open a terminal to the location where we installed the SeqWare tools. 

## Overview of Workflow Development Using the VM

You should be in the ~seqware/SeqWare directory now, this is
the working directory.  Notice there is a jar file here and also two important
directories: provisioned-bundles which contains unzipped workflow bundles and
is where you will work on new bundles and released-bundles (SW_BUNDLE_REPO_DIR
in the config) which contains zip versions of the workflows that you create
when you package up these bundles and install them locally or on the cloud. You
will work in provisioned-bundles, copying a template HelloWorld workflow to a
new bundle that you can modify You will also test your bundles from there as
well. Once you finish with the new bundle you will package and install it via
the web service (to either the remote cloud VM or local VM). Once
installed you can use the workflow scheduling tools you have used before to
trigger workflows (on the cloud or the local VM), monitor them, and get data
back.

Please note that the parent of this directory also contains a copy of the SeqWare source code retrieved via the procedure in [source code](/docs/13-code/).

First, build SeqWare and then copy the distribution jar to your home and name it somethign convenient. 

	cd ~/seqware-github-development/
	cp seqware-distribution/target/seqware-distribution-0.13.6-SNAPSHOT-full.jar ~/seqware-full.jar

## Generating Workflow Bundles

Generate your workflow bundles using Maven archetypes. 

	mvn archetype:generate
	# 690: local -> com.github.seqware:seqware-archetype-simple-legacy-ftl-workflow (A very simple SeqWare legacy ftl workflow archetype)

## Listing the Workflow

	seqware@seqwarevm Workflow_Bundle_workflow-hello-simple-legacy-ftl-workflow]$ java -jar ~/seqware-github-development/seqware-distribution/target.jar -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- -l -b `pwd`

	Running Plugin: net.sourceforge.seqware.pipeline.plugins.BundleManager
	Setting Up Plugin: net.sourceforge.seqware.pipeline.plugins.BundleManager@25595f51

	List Workflows:

	 Workflow:
	  Name : hello
	  Version : 1.0
	  Description : Add a description of the workflow here.
	  Test Command: java -jar ${workflow_bundle_dir}/Workflow_Bundle_workflow-hello-simple-legacy-ftl-workflow/1.0-SNAPSHOT/lib/seqware-distribution-0.13.3-full.jar --plugin net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --no-metadata --provisioned-bundle-dir ${workflow_bundle_dir} --workflow hello --version 1.0 --ini-files ${workflow_bundle_dir}/Workflow_Bundle_workflow-hello-simple-legacy-ftl-workflow/1.0-SNAPSHOT/config/workflow.ini
	  Template Path:${workflow_bundle_dir}/Workflow_Bundle_workflow-hello-simple-legacy-ftl-workflow/1.0-SNAPSHOT/workflows/workflow.ftl
	  Config Path:${workflow_bundle_dir}/Workflow_Bundle_workflow-hello-simple-legacy-ftl-workflow/1.0-SNAPSHOT/config/workflow.ini
	  Requirements Compute: single Memory: 20M Network: local


## Ensure that the files in the Workflow bin directory are executable

	chmod -R a+x Workflow_Bundle_simple-legacy-ftl-workflow/1.0-SNAPSHOT/bin/*


## Test the MyHelloWorld Workflow Bundle on the VM

	java -jar ~/seqware-full.jar -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- -b `pwd` -t --workflow simple-legacy-ftl-workflow --version 1.0

	Running Plugin: net.sourceforge.seqware.pipeline.plugins.BundleManager
	Setting Up Plugin: net.sourceforge.seqware.pipeline.plugins.BundleManager@e80d1ff
	Testing Bundle
	  Running Test Command:
	java -jar /home/seqware/Temp/simple-legacy-ftl-workflow/target/Workflow_Bundle_simple-legacy-ftl-workflow_1.0-SNAPSHOT_SeqWare_0.13.3/Workflow_Bundle_simple-legacy-ftl-workflow/1.0-SNAPSHOT/lib/seqware-distribution-0.13.3-full.jar --plugin net.sourceforge.seqware.pipeline.plugins.Workflow-provisioned-bundle-dir /home/seqware/Temp/simple-legacy-ftl-workflow/target/Workflow_Bundle_simple-legacy-ftl-workflow_1.0-SNAPSHOT_SeqWare_0.13.3 --workflow simple-legacy-ftl-workflow --version 1.0 --ini-files /home/seqware/Temp/simple-legacy-ftl-workflow/target/Workflow_Bundle_simple-lSHOT_SeqWare_0.13.3/Workflow_Bundle_simple-legacy-ftl-workflow/1.0-SNAPSHOT/config/workflow.ini
	MONITORING PEGASUS STATUS:
	RUNNING: step 1 of 5 (20%)
	RUNNING: step 2 of 5 (40%)

## Packaging and Installing the MyHelloWorld Locally

Assuming the workflow above worked fine the next step is to install it locally,
this means it will be inserted into the MetaDB via the locally running web
service.  During this process it will zip up the workflow bundle and put it
into your released-bundles directory. Once you have the zip file you can share it with
other users and, in the future, upload it to an AppStore to make it even easier to share.

Here is an example showing how this
process works on the VM and what is happening in the database and your
released-bundles directory as you do this.  You may want to delete the zip file
that is in the released-bundles directory before you do this step below (or back
it up somewhere first).  To connect to the database in the example below you
can issue the following command in the terminal:

	psql -U seqware -W seqware_meta_db

...with password <code>seqware</code>.

	seqware@seqwarevm Workflow_Bundle_hello_1.0-SNAPSHOT_SeqWare_0.13.3]$ java -jar ~/seqware-full.jar -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- -b `pwd` -i
	Running Plugin: net.sourceforge.seqware.pipeline.plugins.BundleManager
	Setting Up Plugin: net.sourceforge.seqware.pipeline.plugins.BundleManager@2b5ac3c9
	Installing Bundle
	Bundle: /home/seqware/Temp/workflow-hello-simple-legacy-ftl-workflow/target/Workflow_Bundle_hello_1.0-SNAPSHOT_SeqWare_0.13.3
	Now packaging /home/seqware/Temp/workflow-hello-simple-legacy-ftl-workflow/target/Workflow_Bundle_hello_1.0-SNAPSHOT_SeqWare_0.13.3 to a zip file and transferring to the directory: /home/seqware/SeqWare/released-bundles Please be aware, this process can take hours if the bundle is many GB in size.
	Dec 4, 2012 7:34:44 PM org.restlet.ext.httpclient.HttpClientHelper start
	INFO: Starting the Apache HTTP client
	WORKFLOW_ACCESSION: 6730
	Bundle Has Been Installed to the MetaDB and Provisioned to /home/seqware/Temp/workflow-hello-simple-legacy-ftl-workflow/target/Workflow_Bundle_hello_1.0-SNAPSHOT_SeqWare_0.13.3!

What happens here is the <code>Workflow_Bundle_hello_1.0-SNAPSHOT_SeqWare_0.13.3</code> directory is zip'd up to your released-bundles directory and the metadata about the workflow is saved to the database.
 
## Generate an ini file 
Make sure you clean out the cruft from the ini file here!
Delete the lines before and after (and including) "-----------------------------------------------------"


	[seqware@seqwarevm Workflow_Bundle_hello_1.0-SNAPSHOT_SeqWare_0.13.3]$ java -jar ~/seqware-full.jar -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- --list-workflow-params --workflow-accession 6730 > workflow.ini
	Dec 4, 2012 7:36:34 PM org.restlet.ext.httpclient.HttpClientHelper start
	INFO: Starting the Apache HTTP client
	vim workflow.ini 


Now, we will try to schedule the workflow to the database instead of running it directly (people running workflows in production and deciders should take this approach rather than executing directory with WorkflowLauncher and --wait)


## Schedule a workflow

	Note when scheduling, you need --host.  This will now correctly schedule the workflow in the database

	[seqware@seqwarevm Workflow_Bundle_hello_1.0-SNAPSHOT_SeqWare_0.13.3]$ java -jar ~/seqware-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --ini-files workflow.ini --workflow-accession 6730 --schedule --parent-accessions 839 --host `hostname --long`
	Running Plugin: net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher
	Setting Up Plugin: net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher@e80d1ff
	Dec 4, 2012 7:40:45 PM org.restlet.ext.httpclient.HttpClientHelper start
	INFO: Starting the Apache HTTP client
	WORKFLOW_RUN ACCESSION: 6731

Note that this doesn't work!

	[seqware@seqwarevm Workflow_Bundle_hello_1.0-SNAPSHOT_SeqWare_0.13.3]$ java -jar ~/seqware-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --ini-files workflow.ini --workflow-accession 6730 --schedule --parent-accessions 839


## Launch a Workflow

Launch a workflow that's been scheduled (you can tell it which one with --workflow-run-accession, typically this command is a cron). It launched workflows scheduled by your REST username where the host field in workflow_run matches the host you run this on.

	seqware@seqwarevm Workflow_Bundle_hello_1.0-SNAPSHOT_SeqWare_0.13.3]$ java -jar ~/seqware-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --launch-scheduled
	Running Plugin: net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher
	Setting Up Plugin: net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher@25595f51
	Dec 4, 2012 7:43:00 PM org.restlet.ext.httpclient.HttpClientHelper start
	INFO: Starting the Apache HTTP client
	Number of submitted workflows: 5
	Working Run: 6705
	Invalid run by host check: 6705
	Working Run: 6706
	Invalid run by host check: 6706
	Working Run: 6683
	Invalid run by host check: 6683
	Working Run: 6684
	Invalid run by host check: 6684
	Working Run: 6731
	Valid run by host check: 6731
	requiresNewLauncher - fall-through
	Launching via old launcher: 6731
	Workflow Run 6731
	Workflow: 6730
	TEMPLATE FILE: /home/seqware/Temp/workflow-hello-simple-legacy-ftl-workflow/target/Workflow_Bundle_hello_1.0-SNAPSHOT_SeqWare_0.13.3/Workflow_Bundle_workflow-hello-simple-legacy-ftl-workflow/1.0-SNAPSHOT/workflows/workflow.ftl
	INI FILES:
	PARENT ACCESSIONS: 839
	CREATING DAX IN: /tmp/dax695420372787314356595792546946156
	TEMPLATE FILE: /home/seqware/Temp/workflow-hello-simple-legacy-ftl-workflow/target/Workflow_Bundle_hello_1.0-SNAPSHOT_SeqWare_0.13.3/Workflow_Bundle_workflow-hello-simple-legacy-ftl-workflow/1.0-SNAPSHOT/workflows/workflow.ftl
	CREATING DAX IN: /tmp/dax695420372787314356595792546946156
	  INI FILE:
	  KEY: parent_accessions VALUE: 839
	  KEY: parent-accessions VALUE: 839
	  KEY: output_prefix VALUE: ./provisioned/
	  KEY: output_dir VALUE: seqware-results
	  KEY: workflow_run_accession VALUE: 6731
	  KEY: parent_accession VALUE: 839
	  KEY: input_file VALUE: ${workflow_bundle_dir}/Workflow_Bundle_workflow-hello-simple-legacy-ftl-workflow/1.0-SNAPSHOT/data/input.txt
	  KEY: workflow-run-accession VALUE: 6731
	  KEY: seqware_cluster VALUE: seqwarevm
	  KEY: workflow_bundle_dir VALUE: /home/seqware/Temp/workflow-hello-simple-legacy-ftl-workflow/target/Workflow_Bundle_hello_1.0-SNAPSHOT_SeqWare_0.13.3
	  KEY: metadata VALUE: metadata
	SUBMITTING TO PEGASUS: pegasus-plan -Dpegasus.user.properties=/home/seqware/.seqware/pegasus/properties --dax /tmp/dax695420372787314356595792546946156 --dir /home/seqware/SeqWare/pegasus-dax -o seqwarevm --force --submit -s seqwarevm
	PEGASUS STATUS COMMAND: pegasus-status -l /home/seqware/SeqWare/pegasus-dax/seqware/pegasus/hello/run0001

## Monitoring Workflows

Monitor the running workflows (for the ones that have metadata writeback and have entries in the workflow_run table) no params here let you monitor all running workflows.  It automatically is only monitoring ones owned by your REST username and launched as your linux user on the host you're running this command on

	java -jar ~/seqware-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowStatusChecker -- --workflow-run-accession 6731



You can also launch with --wait, this manually runs a workflow using WorkflowLauncher (not scheduled first in the DB and not using the integrated testing via BundleManager) 

	[seqware@seqwarevm Workflow_Bundle_hello_1.0-SNAPSHOT_SeqWare_0.13.3]$ java -jar ~/seqware-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --ini-files workflow.ini --workflow-accession 6730 --parent-accessions 839 --wait --host seqwarevm
	Running Plugin: net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher
	MONITORING PEGASUS STATUS:
	RUNNING: step 1 of 4 (25%)
	RUNNING: step 2 of 4 (50%)
	RUNNING: step 3 of 4 (75%)
	WORKFLOW COMPLETED SUCCESSFULLY!


## Final Thoughts

You can also launch async (without --wait), this manually runs the workflow using WorkflowLauncher but does not stick around for it to finish.  You can get in trouble here if you launch a workflow without metadata writeback in which case there will be no saved record this workflow is running!  Be careful!  Typically you will want to either schedule a workflow *or* you will want to use BundleManager to test (during development or if someone hands you a workflow budle) which runs it and monitors the status *or* you will directly launch the workflow with your own .ini file and use --wait so it sticks around monitors the progress.

	 java -jar ~/seqware-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --ini-files workflow.ini --workflow-accession 6730 --parent-accessions 839 --host seqwarevm


Note, when you do "BundleManager --test" you're actually running the "old" WorkflowLauncher that's bundled inside the workflow whereas the other techniques use the WorkflowLauncher inside the particular SeqWare jar you're directly executing.  Just be aware if you get strange behavior with BundleManager and test, you always have the option if directly running the test with the WorkflowLauncher command. 



## Next Steps

The step-by-step guide at [Creating New Workflow Bundles and Modules Using Maven Archetypes](/docs/14-workflow-mvn/) gives an introduction on how to create new workflows.
The guide [How to Write a Workflow Bundle](http://sourceforge.net/apps/mediawiki/seqware/index.php?title=How_to_Write_a_Bundled_Workflow) on the public SeqWare project wiki goes into very detailed information about workflow bundles, how to create them, the syntax they use, and other key information. In the near future this will be migrated to this site but the directions there should still be up to date.
