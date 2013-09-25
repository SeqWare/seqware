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

* install workflows
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

	$ seqware bundle install --zip ~/packaged-bundles/Workflow_Bundle_MyHelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>.zip 
	Installing Bundle
	Bundle: /home/seqware/packaged-bundles/Workflow_Bundle_MyHelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>.zip
	Now packaging /home/seqware/packaged-bundles/Workflow_Bundle_MyHelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>.zip to a zip file and transferring to the directory: /home/seqware/released-bundles Please be aware, this process can take hours if the bundle is many GB in size.
	  PROCESSING INPUT: /home/seqware/packaged-bundles/Workflow_Bundle_MyHelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>.zip OUTPUT: /home/seqware/released-bundles OUTPUT FILE: null

	WORKFLOW_ACCESSION: 17
	Bundle Has Been Installed to the MetaDB and Provisioned to /home/seqware/packaged-bundles/Workflow_Bundle_MyHelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>.zip!

What happens here is the <code>Workflow_Bundle_MyHelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>.zip</code> copied to your released-bundles directory and unzip'd into your provisioned-bundles directory. The metadata about the workflow is then saved to the database.

<%= render '/includes/monitor_workflows/' %>

For more information see the [Monitor Configuration](/docs/6-pipeline/monitor_configuration/) documentation.

## How to Cancel Workflows

After launching a workflow, you can cancel it in order to stop further execution. This will set the status of the workflow run to `submitted_cancel`, and after the next status propagation, will set the status to `cancelled`.

    $ seqware workflow-run cancel --accession 28
    $ seqware workflow-run report --accession 28
	-[ RECORD 0 ]------------------+-------------------------------------
	Workflow                       | HelloWorld 1.0-SNAPSHOT              
	Workflow Run SWID              | 28                                   
	Workflow Run Status            | cancelled                            
	Workflow Run Create Timestamp  | 2013-08-23 14:58:35.374              
	Workflow Run Host              | master                               
	Workflow Run Status Command    | 0000000-130823144527997-oozie-oozi-W 
	Library Sample Names           |                                      
	Library Sample SWIDs           |                                      
	Identity Sample Names          |                                      
	Identity Sample SWIDs          |                                      
	Input File Meta-Types          |                                      
	Input File SWIDs               |                                      
	Input File Paths               |                                      
	Immediate Input File Meta-Types|                                      
	Immediate Input File SWIDs     |                                      
	Immediate Input File Paths     |                                      
	Output File Meta-Types         |                                      
	Output File SWIDs              |                                      
	Output File Paths              |                                      
	Workflow Run Time              | 33.0s


## How to Retry Failed and Cancelled Workflows

<%= render '/includes/debug/oozie_restart/' %>

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

