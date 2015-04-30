---

title:                 "SeqWare Pipeline: Developing in Partial SeqWare Environments with Whitestar"
markdown:              advanced
toc_includes_sections: true
is_dynamic:            true

---


## Overview

A typical SeqWare environment consists of many different dependencies including the SeqWare web service for tracking metadata (which uses a SeqWare metadb running on postgres), Oozie (a Hadoop workflow scheduler which requires Hadoop HDFS), and an execution environment such as SGE (Sun Grid Engine) or Hadoop jobtrackers+task trackers. While these are all provided by our VM images or our Bindle provisioning software profile, running these images can still be resource intensive or inappropriate for particular environments. It is possible to use the White Star series of workflow engines (included with SeqWare) and a simulated metadata layer in order to run or develop workflow bundles in an environment that is missing components of SeqWare.

## Typical Development 

Our [docs](/docs/2-installation/) cover how to start-up a SeqWare environment. We normally recommend either the VirtualBox images or Amazon images as a starting off point. 

As described in our 'Getting Started' guide, you can create a HelloWorld workflow and use 'seqware bundle launch' to run a workflow using Oozie backed by Oozie-sge and with metadata. 

	$ seqware bundle launch --dir target/Workflow_Bundle_HelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>/
	Performing launch of workflow 'HelloWorld' version '1.0-SNAPSHOT'
	[--plugin, net.sourceforge.seqware.pipeline.plugins.BundleManager, --, --install-dir-only, --bundle, target/Workflow_Bundle_HelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>/, --out, /tmp/bundle_manager3283774875907860918out]
	Installing Bundle (Working Directory Only)
	Bundle: target/Workflow_Bundle_HelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>/
	Added 'HelloWorld' (SWID: 6709)
	Bundle Has Been Installed to the MetaDB and Provisioned to target/Workflow_Bundle_HelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>/!
	[--plugin, io.seqware.pipeline.plugins.WorkflowScheduler, --, --workflow-accession, 6709, --host, odl-dyuen, --out, /tmp/scheduler851239179059254818out, --i, /home/dyuen/workflow-dev/HelloWorld/target/Workflow_Bundle_HelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>/Workflow_Bundle_HelloWorld/1.0-SNAPSHOT/config/HelloWorldWorkflow.ini]
	Created workflow run with SWID: 6710
	[--plugin, io.seqware.pipeline.plugins.WorkflowLauncher, --, --launch-scheduled, 6710]
	[2014/08/13 11:57:18] | Number of submitted workflows: 13
	...
	Files copied to /usr/tmp/oozie/oozie-bbd59038-c380-463f-bcc5-2bb0c390af1b
	Submitted Oozie job: 0000000-140812132047315-oozie-oozi-W
	[--plugin, io.seqware.pipeline.plugins.WorkflowWatcher, --, --workflow-run-accession, 6710]
	...
	Workflow job completed ...
	Application Path   : hdfs://localhost:8020/user/dyuen/seqware_workflow/oozie-bbd59038-c380-463f-bcc5-2bb0c390af1b
	Application Name   : HelloWorld
	Application Status : SUCCEEDED

In this default mode, all our dependencies are installed and metadata is tracked both in order to launch the workflow and on each step of your workflow. 

## Peeling Back the Onion

### Development without Oozie but with Metadata and SGE

You can shutdown Oozie if desired to save resources. 

	$ sudo service oozie stop

Run your workflows using the whitestar-sge engine. Note that all workflows run by whitestar will run synchronously via the WorkflowLauncher. 

	$ seqware bundle launch --dir target/Workflow_Bundle_HelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>/ --engine whitestar-sge
	...
	Using working directory: /usr/tmp/oozie/oozie-2f30c387-6a75-4cd4-8ade-d32208721cff
	[2014/08/13 12:12:19] | Running command: qsub -sync yes -@ /usr/tmp/oozie/oozie-2f30c387-6a75-4cd4-8ade-d32208721cff/generated-scripts/start_0-qsub.opts /usr/tmp/oozie/oozie-2f30c387-6a75-4cd4-8ade-d32208721cff/generated-scripts/start_0-runner.sh
	Your job 3491 ("start_0") has been submitted
	Job 3491 exited with exit code 0.
	[2014/08/13 12:12:22] | Running command: qsub -sync yes -@ /usr/tmp/oozie/oozie-2f30c387-6a75-4cd4-8ade-d32208721cff/generated-scripts/provisionFile_file_in_0_1-qsub.opts /usr/tmp/oozie/oozie-2f30c387-6a75-4cd4-8ade-d32208721cff/generated-scripts/provisionFile_file_in_0_1-runner.sh
	Your job 3492 ("provisionFile_file_in_0_1") has been submitted
	Job 3492 exited with exit code 0.
	...
	Workflow run 6719 is now completed

### Development without Oozie and SGE but with Metadata

Currently, you have a choice of two workflow engines, whitestar-parallel and whitestar which run workflows using a simple callout to Bash. The two engines run all jobs on a particular level of your job DAG either concurrently or one-by-one respectively.

	$ seqware bundle launch --dir target/Workflow_Bundle_HelloWorld_1.0-SNAPSHOT_SeqWare__<%= seqware_release_version %>/ --engine whitestar-parallel
	$ seqware bundle launch --dir target/Workflow_Bundle_HelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>/ --engine whitestar


### Development without Oozie, SGE, or Metadata

This is the most degraded environment, you basically only need Linux, Java, and whatever dependencies your workflow bundle requires of course. 

First change your metadata environment to the in-memory metadata environment. This is a partial implementation of metadata which allows you to only store metadata while a particular JVM is active, sufficient only for launching workflows.  

In your .seqware/settings file, you will be replacing

	SW_METADATA_METHOD=webservice

with 

	SW_METADATA_METHOD=inmemory

Next, you will need to run your workflows without metadata being recorded for individual steps of your workflows. 

	$ seqware bundle launch --dir target/Workflow_Bundle_HelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>/ --engine whitestar --no-metadata

## Additional Notes 

When working with SeqWare Whitestar, there are some additional changes in functionality from Oozie-based versions of SeqWare that should be noted. The first obvious change is that workflows are launched and run synchronously without scheduling. Additional notes are below:

* The number of times that Whitestar will attempt a particular step in a workflow is configurable based on the OOZIE_RETRY_MAX key in your ~/.seqware/settings. You can set this to 0 if you do not want steps to be retried at all
* When retrying workflows with Whitestar, you will need to use "seqware workflow-run retry --working-dir" rather than using an accession due the possibility that you may not be running with metadata. For example, in order to retry one of the workflows above you would use the following 

        $ seqware workflow-run retry --working-dir /usr/tmp/oozie/oozie-2f30c387-6a75-4cd4-8ade-d32208721cff

* All jobs in a level of a DAG will be launched by default. However, you can override this behaviour by setting the key WHITESTAR_MEMORY_LIMIT in your ~/.seqware/settings. For example, setting that to 3000 will mean that 3000MB worth of jobs will be launched at a time.    


## Summary

This tutorial demonstrates how to run SeqWare bundles in environments without our dependencies for quick development and debugging. Note that workflows should still be tested with metadata and with Oozie before deployment to a typical SeqWare production environment since certain short-cuts will be possible when running in these environments which will not be portable.
