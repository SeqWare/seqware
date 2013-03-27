---

title:                 "Admin Tutorial"
markdown:              advanced
toc_includes_sections: true

---

**This guide is a work in progress.**
This guide is intended for a SeqWare administrator. Currently, it covers the tools required to install workflows, monitor workflows globally, and launch scheduled jobs. We also cover tools that are required for cancelling workflows that have started and restarting workflows.

In the near future, this guide will also include information on how to setup SeqWare at your site or on the cloud.
It focuses on what you need to do to get “real” work done e.g. to run workflows you create on
datasets that require multiple nodes to analyze the data in a reasonable amount of time.
There are basically two approaches for this, connect the VirtualBox VM to a cluster at your
local site or to launch a full SeqWare cluster on EC2 using Starcluster. Either of these
approaches will leave you with a system that can process large amounts of data. This guide
assumes you are an IT admin at your site or are working with an admin since some of the
steps will require “root” privileges.

## By the End of These Tutorials

By the end of these tutorials you will:

* be prepared to install workflows
* monitor workflows
* cancel and restart workflows
* see how to connect a local VM to a local cluster for running large-scale workflows
* see how to launch a cluster on Amazon’s cloud for running large-scale workflows

## How to Install a Workflow

<!-- make this install from a zip for the admin guide --> 
When provided with a tested workflow from a workflow developer, the next step is to install it locally,
this means it will be inserted into the MetaDB via the locally running web
service.  During this process it will zip up the workflow bundle and put it
into your released-bundles directory. Once you have the zip file you can share it with
other users and, in the future, upload it to an AppStore to make it even easier to share.

Here is an example showing how this
process works on the VM and what is happening in the database and your
released-bundles directory as you do this.  You may want to delete the zip file
that is in the released-bundles directory before you do this step below (or back
it up somewhere first).  

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



## How to Launch

The [Workflow Launcher plugin](/docs/17-plugins/#workflowlauncher) is responsible for scheduling workflow launches and launching them, both synchronously and asynchronously. In our reference SeqWare environment, we typically schedule jobs and then launch them asynchronously using the WorkflowLauncher  scheduled  via a cronjob. 

Specifically, we schedule workflow launches using a command similar to that below:

	java -jar seqware-distribution-0.13.6.2-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --ini-files workflow.ini --workflow-accession $workflow_acc --parent-accessions 99 --host `hostname --long` 

Then in a cronjob we use the following command to launch scheduled jobs. 

	java -jar seqware-distribution-0.13.6.2-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --launch-scheduled

Note that in the first command, we allow jobs to be scheduled on a specific host. When we launch scheduled workflows, we check this value in order to determine whether a particular scheduled workflow should be launchedi on this host.  A  --force-host option can be used to force launches to occur on a particular host. Note that while we normally use a fully qualified hostname, any unique string can be used to designate a host for launching  (for example on Amazon S3). 

## How to Monitor

A cronjob that accompanies the launch-scheduled option of the WorkflowLauncher is the [Workflow Status Checker plugin](/docs/17-plugins/#workflowstatuschecker). This uses the pegasus-status command in order to retrieve the status of a currently running workflows and updates the MetaDB with their status. 

### Cron Jobs

The SeqWare VM uses two cron tasks to detect and launch scheduled workflows. Take a look at:

	seqware@seqwarevm SeqWare]$ crontab -l
	1 0 * * * /home/seqware/crons/update_db.sh >> /home/seqware/logs/update_db.log
	* * * * * /home/seqware/crons/status.cron >> /home/seqware/logs/status.log

The first script runs at one minute past midnight every night. This runs the stored procedures which populate the "Study Report" and the "SequenceRunReport" in the SeqWare Portal. 
The second script runs every minute. This script uses two plugins, the WorkflowLauncher plugin which is used to launch workflows that have been previously scheduled while the WorkflowStatusChecker plugin is used to check the status of launched workflows. 


## How to Cancel Workflows
## How to Rescue Failed Workflows


## See Also

<p class="warning"><strong>Note:</strong>
Before proceeding further, it is worth noting that the SeqWare MetaDB should be regularly backed-up. 
On our deployment, we have a cron script which calls the SymLinkFileReporter and <code>pg_dump</code> nightly to do back-up. 
</p>


Take a look at the guide for [creating a SeqWare VM](/docs/2a-installation-from-scratch/) which provides technical details on how to install the components of the SeqWare software stack. 

## Coming Soon

We are also preparing guides which will walk administrators through

* Hooking up to an SGE cluster (Pegasus)
* Hooking up to an Oozie cluster
* Hooking up to an LSF cluster
