---

title:                 "Running workflows through the Web Service"
toc_includes_sections: true
markdown:              advanced 
is_dynamic:		true

---

Most of the functionality of the Web service allows access to the MetaDB on a very low level

## Get a list of all workflows
URL: GET http://localhost:8080/seqware-webservice-<%= seqware_release_version %>/workflows

## Get a particular workflow
URL: GET http://localhost:8080/seqware-webservice-<%= seqware_release_version %>/workflows/11034 (use the sw_accession)

## Get the workflow and its parameters
URL: GET http://localhost:8080/seqware-webservice-<%= seqware_release_version %>/workflows/11034?show=params

## Launch workflow

![Simple DB](/assets/images/metadb/Study_hierarchy.png)

URL: POST to http://localhost:8080/seqware-webservice-<%= seqware_release_version %>/workflows/28522/runs with the INI file in the method body and the following query parameters :
* parent-accessions - comma-separated list of sw_accessions of parents to link the new Processing events to (e.g. a Lane of data, or a Processing object with a data file from previous analysis)
* link-workflow-run-to-parents - comma-separated list of parents to link the workflow_run to (e.g. the original Lane or IUS that the workflow_run is operating on)
* no-metadata - run without linking to any parents. If you leave the previous two fields blank, this option is automatically enabled. If it is not specified and the previous two fields are specified, then the runs with metadata by default. If this option is enabled, no metadata is run, regardless of the previous two fields.

A sample study hierarchy is on the right to explain the distinction between parent-accessions and workflow run parents. A workflow run parent is always an IUS or a Lane and is routed through the ius_workflow_runs table or lane_workflow_runs table, and is the sw_accession of that object. The parent accession can be a lane, ius or a processing event, and indicates the chronological order of the pipeline (assuming a pipeline is multiple workflows chained together). In the case of WorkflowB Run 1 in the diagram, the link-workflow-run-to-parents would be IUSA, but the parent accession would be WorkflowA, Run1, Step3. This node contains the link to the data file being used in WorkflowB. More explanation of this Study hierarchy is available on the [Understanding_the_SeqWare_MetaDB](/docs/4-metadb/) page.

I'm working on the queries to find the parent objects at the moment, but for now you should be able to call the HelloWorld workflow without any parameters.

For example:

* POST to http://localhost:8080/seqware-webservice-<%= seqware_release_version %>/workflows/11034/runs?no-metadata=true&parent-accessions=4765,4707&link-workflow-run-to-parents=4765,4707 would run without metadata
* POST to http://localhost:8080/seqware-webservice-<%= seqware_release_version %>/workflows/11034/runs?parent-accessions=4765,4707&link-workflow-run-to-parents=4765,4707 would run WITH metadata
* POST to http://localhost:8080/seqware-webservice-<%= seqware_release_version %>/workflows/11034/runs?parent-accessions=4765,4707 would run without metadata
* POST to http://localhost:8080/seqware-webservice-<%= seqware_release_version %>/workflows/11034/runs would run without metadata

The request returns a ReturnValue object. The returnValue attribute of this object is the sw_accession for the workflow_run, which you can monitor in the next step.

## Monitor workflow run
URL: GET http://localhost:8080/seqware-webservice-<%= seqware_release_version %>/workflowruns/18500 (use the sw_accession)

By checking the 'status' attribute of the run, you can see whether it has changed. It may be in any of the following states:

* submitted - submitting the workflow has worked, but the CRON job has not yet detected its presence
* pending - the CRON job has found the job and is processing it
* running - the workflow is running on the cluster
* completed - the workflow has finished successfully
* failed - the workflow has failed
* unknown - the workflow status is temporarily unavailable.
