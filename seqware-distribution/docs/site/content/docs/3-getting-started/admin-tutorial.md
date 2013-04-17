---

title:                 "Admin Tutorial"
markdown:              advanced
toc_includes_sections: true
is_dynamic:            true

---

This guide is intended for a SeqWare administrator. Currently, it covers the
tools required to install workflows, monitor workflows globally, and launch
scheduled jobs. We also cover tools that are required for cancelling workflows
that have started and restarting workflows.

<!--In the near future, this guide will also include information on how to setup
SeqWare at your site or on the cloud.  It focuses on what you need to do to get
“real” work done e.g. to run workflows you create on datasets that require
multiple nodes to analyze the data in a reasonable amount of time.  There are
basically two approaches for this, connect the VirtualBox VM to a cluster at
your local site or to launch a full SeqWare cluster on EC2 using Starcluster.
Either of these approaches will leave you with a system that can process large
amounts of data. This guide assumes you are an IT admin at your site or are
working with an admin since some of the steps will require “root” privileges.
-->

## By the End of These Tutorials

By the end of these tutorials you will:

* be prepared to install workflows
* monitor workflows
* cancel and restart workflows
* see how to connect a local VM to a local cluster for running large-scale workflows
* see how to launch a cluster on Amazon’s cloud for running large-scale workflows

## How to Install a Workflow

<!-- make this install from a zip for the admin guide --> 
When provided with a tested workflow from a workflow developer, the next step
is to install it, this means it will be inserted into the MetaDB via a running
web service.  During this process it will copy the bundle and put it into your
released-bundles directory and provision it into your provisioned bundles
directory. The provisioned bundles directory is where running workflows will
access their files.

Here is an example showing how this process works on the VM and what is
happening in the database and your released-bundles directory as you do this.
You may want to delete the zip file that is in the released-bundles directory
before you do this step below (or back it up somewhere first).

See the [Developer Tutorial](/docs/3-getting-started/developer-tutorial/) for
how to make the zipped workflow bundle. After the zip bundle is created, the
bundle can be provided to the admin for install as below.

	java -jar ~/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- --b packaged/Workflow_Bundle_HelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>.zip -i
	
	Running Plugin: net.sourceforge.seqware.pipeline.plugins.BundleManager
	Setting Up Plugin: net.sourceforge.seqware.pipeline.plugins.BundleManager@29e97f9f
	Installing Bundle
	Bundle: packaged/Workflow_Bundle_HelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>.zip
	Now packaging /tmp/testing/HelloWorld/target/Workflow_Bundle_HelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>/packaged/Workflow_Bundle_HelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>.zip to a zip file and transferring to the directory: /home/seqware/released-bundles Please be aware, this process can take hours if the bundle is many GB in size.
	  PROCESSING INPUT: /tmp/testing/HelloWorld/target/Workflow_Bundle_HelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>/packaged/Workflow_Bundle_HelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>.zip OUTPUT: /home/seqware/released-bundles
	
	Mar 28, 2013 10:43:03 AM org.restlet.ext.httpclient.HttpClientHelper start
	INFO: Starting the Apache HTTP client
	WORKFLOW_ACCESSION: 6804
	Bundle Has Been Installed to the MetaDB and Provisioned to packaged/Workflow_Bundle_HelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>.zip!
	
	[seqware@seqwarevm Workflow_Bundle_HelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>]$ ls -alhtr ~/released-bundles/ | tail -n1
	-rw-rw-r-- 1 seqware seqware 151M Mar 28 10:42 Workflow_Bundle_HelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>.zip
	
	[seqware@master Workflow_Bundle_HelloWorld_1.0-SNAPSHOT_SeqWare_0.13.6.5]$ ls -alhtr ~/provisioned-bundles/ | grep HelloWorld
	drwxrwxr-x 5 seqware seqware 4.0K Apr 17 12:18 HelloWorld
	drwxrwxr-x 3 seqware seqware 4.0K Apr 17 12:27 Workflow_Bundle_HelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>


What happens here is the <code>Workflow_Bundle_hello_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %></code> directory is zip'd up to your released-bundles directory and the metadata about the workflow is saved to the database.

<%= render '/includes/monitor_workflows/' %>

For more information see the [Monitor Configuration](/docs/6-pipeline/monitor_configuration/) documentation.

## How to Cancel Workflows

After launching a workflow, you can cancel it in order to stop further execution. This will set the status of the workflow run to 'failed'.

	java -jar ~/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --ini-files workflow.ini --workflow-accession 6730  --parent-accessions 839 --host `hostname --long`                      
	
	Running Plugin: net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher                                            
	Setting Up Plugin: net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher@288051                                  
	Mar 28, 2013 11:29:56 AM org.restlet.ext.httpclient.HttpClientHelper start                                           
	INFO: Starting the Apache HTTP client                                                                                
	requiresNewLauncher - byClass ${workflow_bundle_dir}/Workflow_Bundle_seqware-archetype-java-workflow/1.0-SNAPSHOT/classes/com/seqware/github/WorkflowClient.java                                                                                              
	Attempting to instantiate /tmp/1364426207469-0/seqware-archetype-java-workflow/target/Workflow_Bundle_seqware-archetype-java-workflow_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>/Workflow_Bundle_seqware-archetype-java-workflow/1.0-SNAPSHOT/classes/com/seqware/github/WorkflowClient.java                                                                                                              
	  INI FILE: workflow.ini                                                                                                       
	CREATING DAX IN: /tmp/dax71095297362814544976755889839517                                                                      
	SUBMITTING TO PEGASUS: pegasus-plan -Dpegasus.user.properties=/home/seqware/.seqware/pegasus/properties --dax /tmp/dax71095297362814544976755889839517 --dir /home/seqware/pegasus-dax -o seqwarevm --force --submit -s seqwarevm                     
	PEGASUS STATUS COMMAND: pegasus-status -l /home/seqware/pegasus-dax/seqware/pegasus/seqware-archetype-java-workflow/run0126                                                                                                                           
	[seqware@seqwarevm testing]$ condor_q                                                                                          


	-- Submitter: seqwarevm : <10.0.2.15:57652> : seqwarevm
	 ID      OWNER            SUBMITTED     RUN_TIME ST PRI SIZE CMD               
	2848.0   seqware         3/28 11:30   0+00:00:12 R  0   0.3  condor_dagman -f -

	1 jobs; 0 completed, 0 removed, 0 idle, 1 running, 0 held, 0 suspended
	[seqware@seqwarevm testing]$ condor_rm 2848.0
	Job 2848.0 marked for removal
	[seqware@seqwarevm testing]$ condor_q


	-- Submitter: seqwarevm : <10.0.2.15:57652> : seqwarevm
	 ID      OWNER            SUBMITTED     RUN_TIME ST PRI SIZE CMD
	2850.0   seqware         3/28 11:30   0+00:00:00 X  10  0.1  kickstart -n seqwa

	1 jobs; 0 completed, 1 removed, 0 idle, 0 running, 0 held, 0 suspended
	[seqware@seqwarevm testing]$ condor_q


	-- Submitter: seqwarevm : <10.0.2.15:57652> : seqwarevm
	 ID      OWNER            SUBMITTED     RUN_TIME ST PRI SIZE CMD

	0 jobs; 0 completed, 0 removed, 0 idle, 0 running, 0 held, 0 suspended


## How to Rescue Failed Workflows

<%= render '/includes/debug/pegasus_restart/' %>

## See Also

<p class="warning"><strong>Note:</strong>
Before proceeding further, it is worth noting that the SeqWare MetaDB should be regularly backed-up. 
On our deployment, we have a cron script which calls the SymLinkFileReporter and <code>pg_dump</code> nightly to do back-up. 
</p>


As an admin the next steps are to explore the various sub-project guides in
this documentation.  Also take a look at the guide for [creating a SeqWare
VM](/docs/2a-installation-from-scratch/) which provides low-level, technical
details on how to install the components of the SeqWare software stack. 


<!--
## Coming Soon

We are also preparing guides which will walk administrators through

* Hooking up to an SGE cluster (Pegasus)
* Hooking up to an Oozie cluster
* Hooking up to an LSF cluster
-->

