## How to Launch

In our reference SeqWare environment, we
typically schedule jobs and then launch them asynchronously via a cronjob. 

A user will schedule workflow launches using a command similar to
that below:

	seqware workflow schedule --accession $workflow_acc --parent-accession 99  --ini workflow.ini --host `hostname --long` 

Then in a cronjob we use the following command to launch scheduled jobs: 

	seqware workflow-run launch-scheduled

Note that in the first command, we allow jobs to be scheduled on a specific
host. When we launch scheduled workflows, we check this value in order to
determine whether a particular scheduled workflow should be launched on this
host.  Note that while we normally use a fully qualified hostname,
any unique string can be used to designate a host for launching  (for example
on Amazon S3). 

## How to Monitor

Since the engine that executes the workflow is separate from the SeqWare MetaDB, a separate process is used to propagate statuses between the workflow engine and MetaDB:

    $ seqware workflow-run propagate-statuses

Once this is executed, workflow-run reports will reflect the updated status.

### Cron Jobs

The SeqWare VM performs both of the above functions via a cronjob:

	$ crontab -l
	* * * * * /home/seqware/crons/status.cron >> /home/seqware/logs/status.log

	$ cat /home/seqware/crons/status.cron

	#!/bin/bash

	source /home/seqware/.bash_profile

	seqware workflow-run launch-scheduled
	seqware workflow-run propagate-statuses --threads 10

        
This script runs every minute and uses the first command to launch workflows that have been previously scheduled while the
second command is used to check the status of launched workflows.
