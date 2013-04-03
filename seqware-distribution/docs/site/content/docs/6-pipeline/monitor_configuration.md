---

title:                 "SeqWare Pipeline Monitor Configuration"
markdown:              advanced
is_dynamic:            true
toc_includes_sections: true

---

## Overview

You saw in the [Admin Tutorial](/docs/3-getting-started/admin-tutorial/) how to both [launch]() and [monitor]() workflows that have been scheduled by a user. This guide provides a bit more details on this process.

In the SeqWare Pipeline system there are two ways to launch workflows. A user can use WorkflowLauncher to directly launch a workflow provided the workflow engine is setup on this host (e.g. it is an Oozie or Pegasus submission host). Alternatively, a user can "schedule" a workflow using WorkflowLauncher through a web service. Another process can monitor the web service and launch the workflows that have been scheduled.  On the SeqWare VM/AMI this all happens on the same box.  But it is possible to have the user scheduling a workflow, the SeqWare Web Service, and the workflow launcher for SeqWare Pipeline all can (and likely should) be on seperate servers. This latter method for launching workflows is the preferred mechansim, especially in a production environment.

<!-- TODO: a nice figure showing workflow launching -->

The setup and configuration of the Web Service and user command line tools are covered elsewhere.  Here we detail the needed cron jobs running on the SeqWare Pipeline host that will query the Web Service, launch scheduled workflows, and monitor their progress.

## Limitations

One core limitation of SeqWare Pipeline is the lack of a single daemon for controlling workflow launching and monitoring.  Instead the cooridination of workflows happens via the SeqWare Web Service.  Each workflow that gets scheduled by a user is associated with that users Web Service credentials. In order to launch that scheduled workflow the WorkflowLauncher needs to connect to the Web Service with the same credentials, find the workflow, and then launch it on the SeqWare Pipeline box. If workflows are scheduled by multiple accounts each account needs its own launcher and monitor cron job to periodically launch and monitor workflows.

Typically this is not a huge limitation since the number of SeqWare accounts responsible in a production environment is relatively limited. It does make it more difficult, though, when the number of SeqWare users is high and each must have their own distinct account.

## Configuration

Show table showing user launching and host interactions...

Host - server1         |               |         |Network |    Host - server2  |                      |
---------------------- | -------------- | ------- |--------| ------------------  | -------------------  |
User scheduling |  Web Service Account  | Host param |   ->      | User Account   | Web Service Account      |
Bob             |  bob@lab.net          | server2    |   ->      | seqware-bob   |  bob@lab.net             |

## How to Launch

The [Workflow Launcher plugin](/docs/17-plugins/#workflowlauncher) is
responsible for scheduling workflow launches and launching them, both
synchronously and asynchronously. In our reference SeqWare environment, we
typically schedule jobs and then launch them asynchronously using the
WorkflowLauncher  scheduled  via a cronjob. 

Specifically, a user will schedule workflow launches using a command similar to
that below:

        java -jar seqware-distribution-0.13.6.2-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --ini-files workflow.ini --workflow-accession $workflow_acc --parent-accessions 99 --host `hostname --long` 

Then in a cronjob we use the following command to launch scheduled jobs. 

        java -jar seqware-distribution-0.13.6.2-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --launch-scheduled

Note that in the first command, we allow jobs to be scheduled on a specific
host. When we launch scheduled workflows, we check this value in order to
determine whether a particular scheduled workflow should be launched on this
host.  A  --force-host option can be used to force launches to occur on a
particular host. Note that while we normally use a fully qualified hostname,
any unique string can be used to designate a host for launching  (for example
on Amazon S3). 

For more information see the [Monitor Configuration]() documentation.

## How to Monitor

A cronjob that accompanies the launch-scheduled option of the WorkflowLauncher
is the [Workflow Status Checker
plugin](/docs/17-plugins/#workflowstatuschecker). This uses the pegasus-status
command in order to retrieve the status of a currently running workflows and
updates the MetaDB with their status. 

## Cron Jobs

 
