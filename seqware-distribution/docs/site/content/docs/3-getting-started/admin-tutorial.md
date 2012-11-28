---

title:                 "Admin Tutorial"
markdown:              advanced
toc_includes_sections: true

---

**This guide is a work in progress.**
This guide will, in the near future, focus on how to setup SeqWare at your site or on the cloud.
It focuses on what you need to do to get “real” work done e.g. to run workflows you create on
datasets that require multiple nodes to analyze the data in a reasonable amount of time.
There are basically two approaches for this, connect the VirtualBox VM to a cluster at your
local site or to launch a full SeqWare cluster on EC2 using Starcluster. Either of these
approaches will leave you with a system that can process large amounts of data. This guide
assumes you are an IT admin at your site or are working with an admin since some of the
steps will require “root” privileges.

## By the End of These Tutorials

By the end of these tutorials you will:

* see how to connect a local VM to a local cluster for running large-scale workflows
* see how to launch a cluster on Amazon’s cloud for running large-scale workflows
* more to come

## How to Launch
* note for multi-users will be researched and put here
* should be a parameter of some kind

## How to Monitor



### Cron Jobs

The SeqWare VM uses two cron tasks to detect and launch scheduled workflows. Take a look at:

	seqware@seqwarevm SeqWare]$ crontab -l
	1 0 * * * /home/seqware/crons/update_db.sh >> /home/seqware/logs/update_db.log
	* * * * * /home/seqware/crons/status.cron >> /home/seqware/logs/status.log

The first script runs at one minute past midnight every night. This runs the stored procedures which populate the "Study Report" and the "SequenceRunReport" in the SeqWare Portal. 
The second script runs every minute. This script uses two plugins, the WorkflowLauncher plugin which is used to launch workflows that have been previously scheduled while the WorkflowStatusChecker plugin is used to check the status of launched workflows. 

### Workflow Run Reporter

The Workflow Run Reporter is a command-line tool that will generated a tab-separated file containing information about one or more workflow runs. The workflow runs can either be retrieved according to time period, workflow type, or by workflow run SeqWare accession.

 
#### Requirements
In order to run the WorkflowRunReporter plugin, you must have the following available to you:
* SeqWare Pipeline JAR (0.12.0 or higher)
* SeqWare settings file set up to contact the SeqWare Web service (contact your local SeqWare admin to get the path)

If you are working on the SeqWare VM, these will already be setup for you. 


#### Command line parameters

There are three ways to retrieve data through this plugin

* Report a workflow_run by SWID - retrieves only that workflow_run
* Report all workflow_runs within a certain time period - according to the workflow_run create_tstmp
* Report all workflow_runs from a certain workflow, optionally by time period

| Command-line option | Description | 
| ------ | ------ | 
|  --workflow-run-accession    | the SWID of the workflow run | 
|  --time-period   | Dates to check for workflow runs. Dates are in format YYYY-MM-DD. If one date is provided, from that point to the present is checked. If two, separated by hyphen YYYY-MM-DDL:YYYY-MM-DD then it checks that range    | 
|  --workflow-accession   |  the SWID of a workflow. All the workflow runs for that workflow will be retrieved    | 
|  --stdout   |   (0.12.5) Print the results to standard out instead of to a file   | 
|  -o, --output-filename   |   (0.12.5) Optional: The output filename   | 

#### Examples
Retrieves the workflow run report of workflow_run SWID 24770:

	java -jar seqware-distribution/target/seqware-distribution-0.13.6-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter -- --workflow-run-accession 24770

Retrieves the workflow run report of all workflows run between April 20 2012 and May 1 2012:

	java -jar seqware-distribution/target/seqware-distribution-0.13.6-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter -- --time-period 2012-04-20:2012-05-01

Retrieves the workflow run reports for all runs of workflow SWID 23456 since Sept 1 2011:

	java -jar seqware-distribution/target/seqware-distribution-0.13.6-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter -- --workflow-accession 62691 --time-period 2011-09-01

Retrieves the workflow run reports for all runs of workflow SWID 23456:

	java -jar seqware-distribution/target/seqware-distribution-0.13.6-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter -- --workflow-accession 62691


#### Specification

Regardless of the command used to produce it, the file created by the plugin has the following information:

* Workflow name and version
* Workflow run SWID
* Workflow run status
* Workflow run status command
* Identity sample name and SWID (last in the sample hierarchy)
* Library sample name and SWID (root of the sample hierarchy)
* Input files: input files for a workflow run, with meta-type, SWID and file path
* Output files for a workflow run, with meta-type, SWID and file path
* Time spent on the run according to DB.


#### Web service resources
These are the web service URIs that provide the data for the report. Although it is not necessary to understand these URLs to use the plugin, they can be used without the seqware-pipeline-x.x.x-full.jar to retrieve the report.

Examples:

* [http://localhost:8080/SeqWareWebService/reports/workflowruns/{SWID}](http://localhost:8080/SeqWareWebService/reports/workflowruns/{SWID}) - retrieves one workflow run
* [http://localhost:8080/SeqWareWebService/workflowruns](http://localhost:8080/SeqWareWebService/workflowruns) - retrieves all workflow runs. Can be filtered using the query parameters earliestdate and latestdate with the date in the format yyyyMMdd. e.g. ?earliestdate=20120101 to retrieve all workflow runs started after January 1, 2012.
* [http://localhost:8080/SeqWareWebService/reports/workflows/{SWID}/runs](http://localhost:8080/SeqWareWebService/reports/workflows/{SWID}/runs) - retrieves all workflow runs from a particular workflow. Can be filtered using the query parameters earliestdate and latestdate with the date in the format yyyyMMdd. e.g. ?earliestdate=20120101 to retrieve all workflow runs started after January 1, 2012.

## How to Recover Workflows

### Condor (brief)


## How to Cancel Workflows

## How to Clean-up


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
