---

title:                 "User Tutorial"
markdown:              advanced
is_dynamic:            true
toc_includes_sections: false

---

## Overview

<p class="warning"><strong>Note:</strong>This guide assumes you have installed
SeqWare already. If you have not, please install SeqWare by either downloading
the VirtualBox VM or launching the AMI on the Amazon cloud.  See <a
href="/docs/2-installation/">Installation</a> for directions.</p>

The majority of this guide is dedicated to walking users (people who use
workflows) through the basics of using SeqWare. The core functionality we will
explore is how to get data into the system, how to run workflows someone else
created and installed for you, and getting the resulting data back out.  We
assume that people are most interested in the Pipeline sub-project and focus
most of our time on that.  The examples below will all be based on a local
VirtualBox VM but the environment on our cloud instance is almost identical, so
most of the examples below will be applicable to either VM type. Any difference
will be pointed out in a tip box.

## By the End of This Tutorial

This guide will show you how to use command line tools from Pipeline to access
the MetaDB via the Web Service in order to setup workflows to run in Pipeline,
watch over them, and get results back. This will allow you to do the following
tasks using tools that can be scripted versus our Portal web-based interface
that requires a user to click on an interface. By the end of these tutorials
you will be able to:

* use command line tools from Pipeline as a workflow user
* create studies, experiments, and samples in the MetaDB
* upload data such as fastq files to the VM and associate that data with particular samples in the MetaDB
* find the list of available workflows and the parameters they accept using Pipeline
* schedule a HelloWorld workflow and monitor its progress using Pipeline
* generate a report on the outputs of your workflows in Pipeline
* download files produced by a workflow using Pipeline tools
* debug workflows by downloading stdout and stderr for your workflows

The command line tools are all Java tools from SeqWare Pipeline that wrap our
RESTful SeqWare Web Service. If you would like to learn more about the
low-level API (perhaps you want to call it directly in a program or script) you
can find more information in the [SeqWare Web Service](/docs/7-web-service/)
documentation.

<p class="warning"><strong>Tip:</strong>If you want to see how to do the same
steps covered in this tutorial via the Portal web GUI instead of command line
tools see the <a href="/docs/5-portal/user-guide.md">Portal User Guide</a>.</p>

## First Steps

<%= render '/includes/launch_vm/' %>

## The Example

In this tutorial we will use a simple HelloWorld workflow that takes a text
file as input and creates another file as output. The same examples could be
applied to any workflow and input data types.  How to build your own workflows
(**which is really the central purpose of SeqWare**) is covered in the
[Developer Tutorial](/docs/3-getting-started/developer-tutorial/). How to
install these workflows and present them to users is covered in the [Admin
Tutorial](/docs/3-getting-started/admin-tutorial/).

<p class="warning"><strong>Cloud Tip:</strong>Any differences between the local
VirtualBox VM and Amazon cloud AMI will be described in a "Cloud Tip" box like
this one.</p>

## The SeqWare Command Line Tool

SeqWare is an open source software project built mostly in Java. In the seqware
user's home directory (<kbd>/home/seqware/</kbd>) you will see a SeqWare jar
file (<kbd>seqware-distribution-<%= seqware_release_version %>-full.jar</kbd>).
This jar is, essentially, the command line interface for the whole SeqWare
project. It contains the SeqWare Pipeline code that will allow you to interact
with the SeqWare Web service (whether it is on a VM, installed on another local
machine/cluster, or in the cloud) that controls, among other things, workflow
execution. 

In the image below you get a glimpse of how these SeqWare tools fit together.
For users of the SeqWare system the command line tools, Web Service, or web
Portal application all provide access to the lifecycle of workflow usage. This
includes finding the workflows that are available, seeing what parameters they
take, launching a workflow on specified inputs/parameters, monitoring the
status, debugging the output if something goes wrong, and getting results back.
This process is pretty much identical whether SeqWare is installed locally on a
VirtualBox VM, running on a self-contained cloud instances, or a production
installation running on a real HPC cluster.

<img src="/assets/images/seqware_tool_interaction.png" width="600px"/>

For more information about the command line tools see the
[Plugin](/docs/17-plugins/) and [Modules](/docs/17a-modules/) references which
gives the usage for all our command line utilities.

<p class="warning"><strong>Tip:</strong> The VM will contain a recent version
of the jar that we have validated with this tutorial.  You may want to upgrade
to the latest version, though, which you can download from our <a
href="http://jenkins.res.oicr.on.ca/job/seqware/">continuous build server</a>.
Please choose the jar that has the -full suffix, e.g.
seqware-distribution-0.13.6-full.jar. Keep in mind we make no promises that the
latest version will be bug free!</p>

## The SeqWare Settings File

The SeqWare jar file uses a simple configuration file that has been setup for
you already on the VM. By default the location is ~/.seqware/settings.

This file contains the web address of the SeqWare Web Service, your username
and password, and you Amazon public and private keys that will allow you to
push and pull data files to and from the cloud, etc. For this tutorial the
config file should be ready to go, you will not need to modify it.

For more information see the [Settings](/docs/6-pipeline/user-configuration/)
documentation which covers the details on the user config file.

## Creating Studies, Experiments, and Samples

This tutorial starts with setting up study, experiment, and sample metadata in
the SeqWare MetaDB.  SeqWare MetaDB lets you track studies, experiment, and
samples and then link those to files (like FASTQ or something similar). You can
then run workflows on those files, track the resulting files, and use those
files for the next workflow.

You can run workflows without metadata writeback to the MetaDB but most users
will want to associate a run of a workflow with a particular sample so that is
why we start with setting up this information.  You will want to set up your
study, experiments, and samples before uploading your text or other data files.
This ensures you have "parents" to attach these files to.  Otherwise you will
not be able to use them as parameters for workflows. 

The functionality for each of these metadata tools described below is fairly
limited.  For example, the rich descriptive "Spot Decoding String" language for
describing how a read is structured is not yet fully supported and updates to
existing entities are not yet possible. That is functionality we hope to add in
future releases.  However the basic functionality to create studies,
experiments, and samples now exists in a scriptable form and that is really the
point.  By providing command line tools you can automate the setup of this
information using simple Bash shell scripts or the language of your choice.

<p class="warning"><strong>Tip:</strong>You can use SeqWare Portal to edit the
entries you make with the command line tools (or create more studies,
experiments, and samples). See the <a href="/docs/5-portal/user-guide/">Portal
User Guide</a> for more information.</p>

First, you can find out what tables this tool is capable of writing to:

	java -jar ~/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --list-tables
	
	TableName

	study
	experiment
	sample
	sequencer_run
	ius	
	lane


Now, for a given table, you can find out what fields you can write back to and their type:

	java -jar ~/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --table study --list-fields
	
	Field    Type    Possible_Values
	title    String
	description    String
	accession    String
	center_name    String
	center_project_name    String
	study_type    Integer    [1: Whole Genome Sequencing, 2: Metagenomics, 3: Transcriptome Analysis, 4: Resequencing, 5: Epigenetics, 6: Synthetic Genomics, 7: Forensic or Paleo-genomics, 8: Gene Regulation Study, 9: Cancer Genomics, 10: Population Genomics, 11: Other]

So using the information above you can create a new study:

	java -jar ~/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --table study --create --field 'title::New Test Study' --field 'description::This is a test description' --field 'accession::InternalID123' --field 'center_name::SeqWare' --field 'center_project_name::SeqWare Test Project' --field study_type::4
	
	SWID: 29830

The output of this command above includes the line “SWID: 29830” (or whatever
number is appropriate for your database).  This is very important since this
number is a unique identifier across the database and used to link together
entities.  For example, you will use the number produced by the study add
command as the parent for the experiment you create below.  If you do not track
and supply these numbers then the hierarchy of study/experiment/sample cannot
be created.

The next step is to create an experiment and link it to the study you created
above. You can find the platform ID using the <tt>--list-fields</tt> option shown above:

	java -jar ~/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --table experiment --create --field 'title::New Test Experiment' --field 'description::This is a test description' --field platform_id::26 --field study_accession::29830
	
	SWID: 29831

Again, you use the SWID from the above output in the next step to create an
associated sample. You can find the platform ID using the <tt>--list-fields</tt> option
shown above:

	java -jar ~/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --table sample --create --field 'title::New Test Sample' --field 'description::This is a test description' --field organism_id::26 --field experiment_accession::29831
	
	SWID: 29832

At this point you should have a nice study/experiment/sample hierarchy.  You
can, of course, add multiple samples per experiment and multiple experiments
per study.  For each of the samples you can now upload one or more files.  You
will need the SWID from the sample creation above for this step (or visible in
the Portal).  Here is a screenshot of what the above commands produce in the
[Portal](/docs/5-portal/) (note, the SWIDs do not match but these are just examples):

<img src="/assets/images/final_exp.png" width="600px"/>


## Uploading Files and Associating with a Sample

The first step in uploading a file and associating with a sample is to identify
the sample’s SWID. The easiest way to do this is to use the Portal web
application to navigate through the Study/Experiment/Sample tree to the sample
you want to upload text for and to note its associated SWID.  Of course you
could programmatically use the WebService as well but that is a topic for a
different tutorial, see [SeqWare Web Service](/docs/7-web-service/).  Once you
decide on the "parent" sample to attach the file to you then need to know the
destination location to put your file in.  For the local VM this is either a
directory (which defaults to <kbd>/datastore</kbd>) or a shared filesystem over
NFS if you have connected your VM instance to shared storage and a cluster.
The default <kbd>/datastore</kbd> will work fine here on this single,
self-contained VM instance (either the VirtualBox VM or Amazon AMI).

Once you have these two pieces of information (destination path "/datastore/"
and the sample SWID) you can then use either the Portal or the command line utilities
(ProvisionFiles or GenericMetadataSaver) to put your files into the right place
and associate them with the correct sample.

### Creating a HelloWorld Text File

Moving on with the HelloWorld example workflow, you will now need to create an input text
file to associate with the sample created previously. This will be the input file for the
HelloWorld workflow.  For example, do the following in your home directory:

	echo 'testing HelloWorld' > ~/input.txt

### Associating Uploaded Files with a Sample 

Here is an example of calling the ProvisionFiles command line utility which
will copy a file (<tt>~/input.txt</tt>) to a destination (<tt>/datastore/</tt>)
and also update the database to link the parent sample (SWID:29832 above) to
the newly copied file that has the final path <tt>/datastore/input.txt</tt>:

	java -jar ~/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.ModuleRunner -- --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles --metadata-output-file-prefix /datastore/ --metadata-processing-accession-file accession.txt --metadata-parent-accession 29832 -- -im text::text/plain::/home/seqware/input.txt -o /datastore/ --force-copy 

In this example it will copy the /home/seqware/input.txt text file to
/datastore/ directory (which we are using for this tutorial) and will link it 
to the sample identified by 29832 (the sample’s SWID).  So the final output
file is "/datastore/input.txt" in the database. If you left off --force-copy
you would get a symlink in this case since it is a local file operation.  If
you left off "--metadata-output-file-prefix /datastore/" then the file path in
the DB would just be "input.txt". The parameter
"--metadata-processing-accession-file accession.txt" will cause the SWID for
the ProvisionFiles event to be written to the accession.txt file. Take a look
at the accession for the ProvisionFiles event now, you will use this value as
the parent for the actual run of the HelloWorld workflow:

	cat /home/seqware/accession.txt
	29854

In this case the SWID for the ProvisionFiles is 29854.

<p class="warning"><strong>Tip:</strong> you can find a list of the meta types
(like chemical/seq-na-text-gzip or text/plain above) at <a
href="http://seqware.github.io/docs/16-module-conventions/">Module
Conventions - Module MIME Types</a>. This is the list we add to as needed when
creating new workflows.  It is extremely important to be consistent with these
since a workflow will not recognize your input unless the meta type string
matches what it expects exactly.</p>

### Associating Existing Files with a Sample 

The ProvisionFiles utility above both uploads/copies the input file and also
saves the metadata back to the database.  However, sometimes you have already
uploaded data or, as is the case for the local VM, it is a single filesystem so
there is no reason to make copies of the data (the same would be true if
/home/seqware/ was on an NFS share on a cluster for example).  In this case you
just want to link the files to particular samples in the database.
GenericMetadataSaver is the tool you can use to accomplish this, for example,
if you already had input2.txt in /datastore you could insert this into the
MetaDB using:

	java -jar ~/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.ModuleRunner -- --module net.sourceforge.seqware.pipeline.modules.GenericMetadataSaver --metadata-parent-accession 25192 --metadata-processing-accession-file accession.txt -- --gms-output-file text::text/plain::/datastore/input2.txt --gms-algorithm UploadText --gms-suppress-output-file-check

Here files are associated with the parent (SWID: 25192 which is a sample). One
word of caution, if you expect people to download your files through the Portal
then the paths you inject into the database must be in a place where the Portal
can "see" it. See the [Portal documentation](/docs/5-portal/) for information
on setting the shared directory it expects to find uploaded files in.  For the
VM, this is /datastore/.

## Listing Available Workflows and Their Parameters

Once you have uploaded data the next step is to find the available workflows
and their parameters.  To see the list of available workflows you can execute
the following command:

	java -jar ~/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- --list-install

You will get a tab-delimited list of workflows showing their name, version, and 
(most importantly) their SWID.

<p class="warning"><strong>Tip:</strong> it may be easier for you to read the
output by cutting and pasting into a spreadsheet, it is tab-delimited.</p>

In this example we are going to use the latest (at the time of this writing)
HelloWorld workflow bundle (SWID 7 below).  The output of the above command
includes the line:

	HelloWorldWorkflow      1.0     Wed Aug 15 19:00:11 EDT 2012    7       /home/seqware/released-bundles/Workflow_Bundle_HelloWorldWorkflow_1.0_SeqWare_<%= seqware_release_version %>.zip

The fourth column includes the SWID for this workflow that you will use in the
next command to find all the parameters (and their defaults) that this workflow
takes.  Here is the command, notice we redirect the output to create a basic ini
file that can later be customized and used to submit a run of this workflow:

	java -jar ~/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- --list-workflow-params --workflow-accession 7 > /home/seqware/workflow.ini

In this example the workflow “HelloWorldWorkflow” version 1.0 (SWID 7)
parameters are listed.  The output conforms to the input you can use to
parameterize and launch workflows.  For example:

<pre>
#key=input_file:type=file:display=F:display_name=input_file:file_meta_type=text/plain
input_file=${workflow_bundle_dir}/Workflow_Bundle_helloworld/1.0/data/input.txt
#key=output_dir:type=text:display=F:display_name=output_dir
output_dir=seqware-results
#key=output_prefix:type=text:display=F:display_name=output_prefix
output_prefix=/datastore/
</pre>

<p class="warning"><strong>Important!:</strong> the lines above have been
wrapped but you should not include line breaks in your file.  Instead, make
sure the file that you create using this tool (and customize for later
launching a workflow) includes comment lines starting with “#” and the
key=value lines only.  In the command above the redirect to the file
workflow.ini will include some extra lines of status output.  Make sure you
remove these before continuing to launch the workflow with this ini file.</p>

You can customize any values from the key/value pairs that you need to.  For
example, the most frequent parameters you will customize are input files.  In
the workflow example above you will want to customize <tt>input_file</tt> (set
it to the location of the file you uploaded) and the <tt>output_prefix</tt>
(set it to the common shared directory we use on this VM/AMI
<tt>/datastore/</tt>). For example:

	input_file=/datastore/input.txt
	output_prefix=/datastore/

Since this is a low-level tool you may see many more parameters exposed with
this tool than you would using the web Portal application.  Please use caution
when customizing these values since some refer to items that affect the
underlying infrastructure. Generally, when you see <tt>display=F</tt> that is
an indication that the parameter should usually be left as the default value.

<p class="warning"><strong>Tip:</strong> when you customize key-values in the
ini file prepared above you do not need to include key-values that you leave
unchanged.  If you do not include these the workflow will run with those values
by default anyway.  Removing unchanged key-values will greatly reduce the size
of your ini files making it much easier to see the key-values you are
interested in. In the example above the minimal ini file is simply the two
lines for <tt>input_file</tt> and <tt>output_prefix</tt>.</p>


## Triggering a Workflow and Monitoring Progress 

At this point you know what workflow you are going to run and you have a
customized ini file that contains the <tt>input_file</tt> and
<tt>output_prefix</tt>. The next step is to trigger the workflow using the ini
file you prepared. Make sure you use the correct workflow accession and parent
accession. The former was listed when you listed all workflows (SWID:7 in this
example) and the latter was printed to the <tt>accession.txt</tt> file when you
copied the file using ProvisionFile (SWID:29854 in this example).

	java -jar ~/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --ini-files /home/seqware/workflow.ini --workflow-accession 7 --schedule --parent-accessions 29854 --host `hostname --long` 

<p class="warning"><strong>Tip:</strong> the parent-accessions is the SWID of
the ProvisionFiles element that was added under the sample when use used this
tool to upload the text files in the example above.  You MUST specify this
otherwise the workflow's results will not be linked to anything (they will be
orphaned and will not be visible in the Portal or present in the reports
below). Conveniently the ProvisionFiles tool will write these accessions to a
file and the portal displays these values.</p>

This schedules the workflow to run on the VM. Notice it also prints the
workflow run accession which you can use to help monitor the workflow.

You can then monitor workflow progress (and getting a list of the outputs)
using the WorkflowRunReporter plugin. This will let you script the monitoring
of workflow runs.

	java -jar ~/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter -- --workflow-accession 7

In this example all the status information for workflows with workflow
accession 7 are printed out to a file in the local directory, for example
<tt>20130414_201452__workflow_7.csv</tt>.  This includes several columns of
interest including the status of the workflow, the output file types, and their
locations in S3 or the file system. You can use this information to automate
the checking of workflows and the retrieval of the results!

Alternatively, you can just get the status of a particular workflow run, for
example, the workflow run accession printed when you launched the workflow with
the WorkflowLauncher(for example SWID: 30182).  You can also skip the output
file by just using the <tt>--stdout</tt> option which is helpful if you are scripting
on top of this command.

	java -jar ~/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter -- --workflow-run-accession 30182 --stdout

In the output from the above command you will see accessions for each workflow
run. If the status is “failed” you can download the stderr and stdout from the
workflow run. This is how you might do that for a workflow_run with an
accession of SWID: 30182:

	java -jar ~/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter -- --wra 30182 --wr-stderr
	java -jar ~/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter -- --wra 30182 --wr-stdout

Again, this command automatically creates output files for stderr and stdout,
for example <tt>20130414_202120__workflowrun_30182_STDERR.csv</tt>.  You can
use the <tt>--stdout</tt> option if you wish to skip the output file and just
write to the terminal.

## The Resulting Structure in MetaDB

After a few minutes the HelloWorld workflow you just launched should be
complete with a status of "completed".  If you have followed the directions
carefully for creating a study, experiment, and sample in the MetaDB, uploading
an input file with ProvisionFile, and running a workflow you should have a
structure very similar to the following present in the MetaDB:

<img src="/assets/images/20130414_sample_workflow_run.png" width="600px"/>

You can see the study, experiment, and sample linked together along with
a processing event (ProvisionFiles) attached directly to the sample. This
event is associated with the <tt>input.txt</tt> file and it is the parent
of the first step in the HelloWorld workflow run. This workflow run
has three steps in this example and the final step is associated to the 
output file <tt>output.txt</tt>.  The processing event for Step3 could
then go on to become the parent for a subsequent workflow.

For a more detailed explination of the SeqWare MetaDB and the relationships it
encodes please see the [MetaDB Documentation](/docs/4-metadb/). You can use
either the [Portal](/docs/5-portal/) or various reporting tools available in
the [Pipeline](/docs/6-pipeline/) and/or [Web Service](/docs/7-web-service/) to
explore the data structures and files created when running workflows.


## Downloading Workflow Results

Once a workflow has finished running you will want to list out the associated
output files and download the results.  While you can use the Portal for
downloading files the best way to get files in bulk is to use our reporting
tool. This produces a tab-delimited file that lists all the files produced for
the workflows you are interested in.  You can then use the same ProvisionFiles
utility above to pull files back.  Since the report produces a simple
tab-delimited file you can easily automate the downloading of results by
looping over the output files and calling ProvisionFiles using a script.

	java -jar ~/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.SymLinkFileReporter -- --no-links --output-filename study_report --workflow-accession 7 --study 'New Test Study'

The output here is a <tt>study_report.csv</tt> file that contains a line for
each file output for this workflow.  You can also filter by file types, for
example if you want to see just plain text files:

	java -jar ~/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.SymLinkFileReporter -- --no-links --output-filename study_report --workflow-accession 7 --study 'New Test Study' --file-type 'text/plain'

Or an example filtering by a particular sample:

	java -jar ~/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.SymLinkFileReporter -- --no-links --output-filename study_report --workflow-accession 7  --sample 'New Test Sample'

You can use these output file URLs (such as
s3://bucket/samplename/test_R1.text.gz) with ProvisionFiles to download results
if they are remote. In the local VM they are just local files so they do not
need to be copied.  Here is an example, though, of how to download a report
bundle that is hosted on Amazon's S3:

	java -jar ~/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.ModuleRunner -- --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles --no-metadata -- -i s3://bucket/results/seqware-0.10.0_ComprehensiveExomeGenomeAnalysis-0.10.5/59491657/GAG.fa.variant_quality.gatk.hg19.report.zip -o /home/seqware/

Here the zip report bundle is downloaded to the seqware home directory.  In
this way you can pull back the results of workflows entirely through scripts
that wrap the SymLinkFileReporter and ProvisionFiles.

Also note the SymLinkFileReporter gives you SWIDs for processing events and
entities such as studies, samples, and experiments.  You can use this tool to
find these SWIDs that are used as “parents” for subsequent workflow runs.

You can find more information on this report tool on the [Study
Reporter](/docs/21-study-reporter/) page.

<p class="warning"><strong>Note:</strong> in the example above I use
--no-metadata with ProvisionFiles. This is to prevent the tool from writing
back an event to the central database. Since you are just downloading a file
(versus uploading a file) you do not really want to record that download event
in the database.</p>


## Next Steps

See the [Developer Tutorial](/docs/3-getting-started/developer-tutorial/) for
how to create a new workflow.  How to install workflows and present them to
users is covered in the [Admin
Tutorial](/docs/3-getting-started/admin-tutorial/).
