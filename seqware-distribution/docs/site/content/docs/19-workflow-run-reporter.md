---

title:                 "Workflow Run Reporter"
toc_includes_sections: true
markdown:              basic

---

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
