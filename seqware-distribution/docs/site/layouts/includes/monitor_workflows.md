## How to Launch

The [Workflow Launcher plugin](/docs/17-plugins/#workflowlauncher) is
responsible for scheduling workflow launches and launching them, both
synchronously and asynchronously. In our reference SeqWare environment, we
typically schedule jobs and then launch them asynchronously using the
WorkflowLauncher  scheduled  via a cronjob. 

Specifically, a user will schedule workflow launches using a command similar to
that below:

	java -jar seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --ini-files workflow.ini --workflow-accession $workflow_acc --parent-accessions 99 --host `hostname --long` 

Then in a cronjob we use the following command to launch scheduled jobs. 

	java -jar seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --launch-scheduled

Note that in the first command, we allow jobs to be scheduled on a specific
host. When we launch scheduled workflows, we check this value in order to
determine whether a particular scheduled workflow should be launched on this
host.  A  --force-host option can be used to force launches to occur on a
particular host. Note that while we normally use a fully qualified hostname,
any unique string can be used to designate a host for launching  (for example
on Amazon S3). 

## How to Monitor

A cronjob that accompanies the launch-scheduled option of the WorkflowLauncher
is the [Workflow Status Checker
plugin](/docs/17-plugins/#workflowstatuschecker). This uses the pegasus-status
command in order to retrieve the status of a currently running workflows and
updates the MetaDB with their status. 

### Cron Jobs

The SeqWare VM uses two cron tasks to detect and launch scheduled workflows.
Take a look at:

	seqware@seqwarevm SeqWare]$ crontab -l
	* * * * * /home/seqware/crons/status.cron >> /home/seqware/logs/status.log

	[seqware@seqwarevm SeqWare]$ cat /home/seqware/crons/status.cron

	#!/bin/bash

	source /home/seqware/.bash_profile

	java -jar /home/seqware/crons/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --launch-scheduled
	java -jar /home/seqware/crons/seqware-distribution-<%= seqware_release_version %>-full.jar  -p net.sourceforge.seqware.pipeline.plugins.WorkflowStatusChecker -- --tp 1000
        
This script runs every minute and uses two plugins, the WorkflowLauncher plugin
which is used to launch workflows that have been previously scheduled while the
WorkflowStatusChecker plugin is used to check the status of launched workflows.
