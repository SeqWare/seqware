---

title:                 "User Tutorial"
markdown:              advanced
is_dynamic:            true
toc_includes_sections: false

---

The majority of this guide is dedicated to walking users (people who use workflows) through the basics of using SeqWare. The core functionality we will explore is how to get data into the system, how to run workflows someone else created and installed for you (a workflow developer), and getting the resulting data back out.  We assume that people are most interested in the Pipeline sub-project and focus most of our time on that but this tutorial touches on other sub-projects like the Portal.  The examples below will all be based on a local VM but the environment on our cloud instance is almost identical, so most of the examples below will be applicable to either VM type. In the future we will probably have a separate user guide that focuses on the differences of running on Amazon's cloud.

## By the End of This Tutorial

This guide will show you how to use command line tools from Pipeline and web app from Portal to access the MetaDB. This will allow you to do the following tasks using tools that can be scripted versus our Portal web-based interface that requires a user to click on an interface (we will show examples of the latter along the way too). By the end of these tutorials you will be able to:

* use both command line and web-based tools from Pipeline and Portal respectively
* create studies, experiments, and samples in the MetaDB
* upload data such as fastq files to the VM and associate that data with particular samples in the MetaDB
* find the list of available workflows and the parameters they accept using Pipeline
* schedule a HelloWorld workflow and monitor its progress using Pipeline
* generate a report on the outputs of your workflows in Pipeline and Portal
* download files produced by a workflow using Pipeline tools
* simple debugging of workflows by downloading stdout and stderr for your workflows

The command line tools are all Java tools from SeqWare Pipeline that wrap our RESTful SeqWare Web Service. If you would like to learn more about the low-level API (perhaps you want to call it directly in a program or script) you can find more information in the [SeqWare Web Service](/docs/7-web-service/) documentation.

## The Example

In this tutorial we will use a simple HelloWorld workflow that takes a text file as input and creates another file as output. The same examples could be applied to any workflows and input data types.  How to build your own workflows (**which is really the central purpose of SeqWare**) is covered in the [Developer Tutorial](/docs/3-getting-started/developer-tutorial/). How to install these workflows and present them to users is covered in the [Admin Tutorial](/docs/3-getting-started/admin-tutorial/).

## First Steps

Please launch your local VM in VirtualBox and login as user <kbd>seqware</kbd>, password <kbd>seqware</kbd> at this time. Click on the "SeqWare Directory" link on the desktop which will open a terminal to the location where we installed the SeqWare tools.

## The SeqWare Command Line Tool

SeqWare is open source architecture built mostly in Java. In the <kbd>/home/seqware/SeqWare</kbd> directory you will see a jar file. This contains SeqWare Pipeline code that will allow you to interact with the SeqWare Web service (actually either on a VM, installed on another local machine/cluster, or in the cloud) that controls, among other things, workflow execution. This jar is, essentially, the command line interface for the whole SeqWare project.

<p class="warning"><strong>Tip:</strong> The VM will contain a recent version of the jar that we have validated with this tutorial.  You may want to upgrade to the latest version, though, which you can download from our <a href="http://jenkins.res.oicr.on.ca/job/seqware/">continuous build server</a>. Please choose the jar that has the -full suffix, e.g. seqware-distribution-0.13.6-full.jar. Keep in mind we make no promises that the latest version will be bug free!</p>

For more information about the command line tools see the [Plugin](/docs/17-plugins/) and [Modules](/docs/17a-modules/) reference.

## The SeqWare Settings File

The SeqWare jar file uses a simple configuration file that has been setup for you already on the VM. By default the location is ~/.seqware/settings.

This file contains the web address of the RESTful web service, your username and password, and you Amazon public and private keys that will allow you to push and pull data files to and from the cloud, etc. For this tutorial the config file should be ready to go, you will not need to modify it.

For more information see the [Settings](/docs/6-pipeline/user-configuration/) documentation which covers the details on the user config file.

## Creating Studies, Experiments, and Samples

SeqWare MetaDB lets you track studies, experiment, and samples and then link those to files (like FASTQ or something similar). You can then run workflows on those files, track the resulting files, and use those files for the next workflow.  You will want to set up your study, experiments, and samples before uploading your text or other data files.  This ensures you have "parents" to attach these files to.  Otherwise you will not be able to use them as parameters for workflows. 

You can do this either with the [Portal](/docs/5-portal/) and/or the command line tools below.

### Via the Portal

First, login to the portal, the URL is http://localhost:8080/SeqWarePortal and you will need to use the browser inside the VM to access this. The default username is <kbd>admin@admin.com</kbd> and the default password is <kbd>admin</kbd>.  Feel free to change your password in the web app.

<p class="warning"><strong>Tip:</strong>You could setup the VM so it gets a "real" IP address on your network, in which case you could access both the Portal and Web Service via any computer on your network. Check out the (documentation)[https://www.virtualbox.org/] on VirtualBox for information on setting up an accessible IP address for your running VM.</p>

In the following screenshots you see the process that allows you to create these entities through the web application.
When you first login you will see your studies (of which you have none).  First, create a study which can be though of as a project that can have many distinct experimental designs:

<img src="/assets/images/create_study.png" width="600px"/>

You will then see your new study which includes a link to add an experiment. An experiment encapsulates a particular experimental design, for example 2x50 sequencing on the Illumina platform.  If you then did another experiment with a 2x250 read structure on the same platform that would be considered a novel experimental design.

<img src="/assets/images/create_experiment.png" width="600px"/>

Once you click on the "add experiment" link you will then be able to define the NGS platform used and the nature of the experimental design. Of note is the "Spot Decoding String" which allows you to document the structure of the reads.

The next step is to create one or more samples and associate them with this new experiment.  Under the experiment you just added you will see a link for "add sample", click it and populate the sample information.

At this point you will see your complete study/experiment/sample hierarchy in the "My Studies" section.

<img src="/assets/images/my_studies.png" width="600px"/>

<p class="warning"><strong>Tip:</strong> Notice the "SWID: 14" next to "My Test Study". The other items in the hierarchy have an SWID as well.  This is common for every item in the MetaDB, everything has a SWID aka accession.  When you use command line tools they will often take a "parent accession" to attach output to and this is referring to what you see as SWID in the Portal.</p>

<p class="warning"><strong>Tip:</strong> You can also track sequencer runs and associate samples with the particular "lanes" on the flowcell (or equivalent on the particular sequencing platform).  This is not covered here but will be in the SeqWare Portal guide.  Suffice it to say, this lets you use Portal as a light-weight LIMS system for tracking both studies/experiments/sample and sequencer runs/lanes and how they relate to each other.</p>


### Via Command Line Tools

There are also command line tools for creating study, experiment, and samples.  The functionality for each of these is fairly limited.  For example, the rich descriptive "Spot Decoding String" language for describing how a read is structured is not yet fully supported and updates to existing entities are not yet possible.  However the basic functionality to create studies, experiments, and samples now exists in a scriptable form and that is really the point.  By providing command line tools you can automate the setup of this information using simple Bash shell scripts or the language of your choice.

First, you can find out what tables this tool is capable of writing to:

	java -jar seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --list-tables
	
	TableName

	study

	experiment

	sample

	sequencer_run

	ius	

	lane


Now, for a given table, you can find out what fields you can write back to and their type:

	java -jar seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --table study --list-fields
	
	Field    Type    Possible_Values
	title    String
	description    String
	accession    String
	center_name    String
	center_project_name    String
	study_type    Integer    [1: Whole Genome Sequencing, 2: Metagenomics, 3: Transcriptome Analysis, 4: Resequencing, 5: Epigenetics, 6: Synthetic Genomics, 7: Forensic or Paleo-genomics, 8: Gene Regulation Study, 9: Cancer Genomics, 10: Population Genomics, 11: Other]

So using the information above you can create a new study:

	java -jar seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --table study --create --field 'title::New Test Study' --field 'description::This is a test description' --field 'accession::InternalID123' --field 'center_name::SeqWare' --field 'center_project_name::SeqWare Test Project' --field study_type::4
	
	SWID: 29830

The output of this command above includes the line “SWID: 29830” (or whatever number is appropriate for your database).  This is very important since this number is a unique identifier across the database and used to link together entities.  For example, you will use the number produced by the study add command as the parent for the experiment you create below.  If you do not track and supply these numbers then the hierarchy of study/experiment/sample cannot be created.

The next step is to create an experiment and link it to the study you created above. You can find the platform ID using the --list-fields option shown above:

	java -jar seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --table experiment --create --field 'title::New Test Experiment' --field 'description::This is a test description' --field study_accession::29830 --field platform_id::26
	
	SWID: 29831

Again, you use the SWID from the above output in the next step to create an associated sample. You can find the platform ID using the --list-fields option shown above:

	java -jar seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --table sample --create --field 'title::New Test Sample' --field 'description::This is a test description' --field experiment_accession::29831 --field organism_id::26
	
	SWID: 29832

At this point you should have a nice study/experiment/sample hierarchy.  You can, of course, add multiple samples per experiment and multiple experiments per study.  For each of the samples you can now upload one or more files.  You will need the SWID from the sample creation above for this step (or visible in the Portal).  Here is a screenshot of what the above commands produce in the Portal (note, the SWIDs do not match but these are just examples):

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
self-contained VM instance.

Once you have these two pieces of information (destination path "/datastore/"
and the SWID) you can then use either the Portal or the command line utilities
(ProvisionFiles or GenericMetadataSaver) to put your files into the right place
and associate them with the correct sample.

###Via the Portal

First, create your text document that you want to associate with a sample (we
are using a text document here since the sample HelloWorld workflow takes this
as an input but typically you would upload Fastq files for a sample).  I
created a document called <kbd>simple.txt</kbd> that just has some random text
in it. You can do that in the Desktop directory of the SeqWare user on this VM.

Now, navigate to the Study you created above, pick a sample, and then click
"upload file".  You can then either give the complete path to the text file
("enter the file URL into the database") or you can use the "File to upload"
browser to upload the simple.txt file.  If possible you should enter a path or
URL (http://server/file/path or /directory/file) since this will not result in
duplicate data or long uploads. If you do this then the file path should be
accessible to the user running the examples, in this tutorial it is the
"seqware" user.  The third option (using the Nimbus Transfer Tool) requires
admin configuration and only works when SeqWare is deployed on the Amazon Cloud
so we will skip it here. Click upload when you are done.

<img src="/assets/images/upload.png" width="600px"/>

You can verify the upload has been successful by browsing "My Studies" section.
You should be able to re-download the file. If not, you referenced a file that
was not in a location viewable by the web server user ("tomcat").

<p class="warning"><strong>Tip:</strong>The timeout for the Portal web application is set pretty low. If you have problems uploading files, for example, ensure your login has not expired.</p>

### Via Command Line Tools

#### Associating Uploaded Files with a Sample 

The same process of uploading data described above can also be used on the
command line. Here is an example of calling the ProvisionFiles command line
utility which will copy a file to a destination and also update the database to
link the parent sample to the newly copied file:

	java -jar seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.ModuleRunner -- --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles --metadata-output-file-prefix /datastore/ --metadata-parent-accession 29832 --metadata-processing-accession-file accession.txt -- -im text::text/plain::/home/seqware/Desktop/simple.txt -o /datastore/ --force-copy

In this example it will copy the /home/seqware/Desktop/simple.txt text files to
/datastore/ directory (which we are using for this tutorial) and will link them
to the sample identified by 29832 (the sample’s SWID).  So the final output
file is "/datastore/simple.txt" in the database. If you left off --force-copy
you would get a symlink in this case since it is a local file operation.  If
you left off "--metadata-output-file-prefix /datastore/" then the file path in
the DB would just be "simple.txt". The parameter
"--metadata-processing-accession-file accession.txt" will cause the SWID for
the file to be written to the accession.txt file.  Use the portal to find out
the SWID for an existing sample or get the SWID using the command line tool
when you create a new study. Providing that here will cause the text file to be
associated with that parent sample.  You can verify this in the portal, you
should be able to download the file via the Portal just as you did before
(assuming your destination was in /datastore as it is here).

<p class="warning"><strong>Tip:</strong> you can find a list of the meta types
(like chemical/seq-na-text-gzip or text/plain above) at <a
href="http://seqware.github.io/docs/16-module-conventions/">Module
Conventions - Module MIME Types</a>. This is the list we add to as needed when
creating new workflows.  It is extremely important to be consistent with these
since a workflow will not recognize your input unless the meta type string
matches what it expects exactly.</p>


#### Associating Existing Files with a Sample 

The ProvisionFiles utility above both uploads/copies the input file and also
saves the metadata back to the database.  However, sometimes you have already
uploaded data or, as is the case for the local VM, it is a single filesystem so
there is no reason to make copies of the data (the same would be true if
/home/seqware/ was on an NFS share for example).  In this case you just want to link the
files to particular samples in the database.  GenericMetadataSaver is the tool
you can use to accomplish this, for example, if you already had simple2.txt in
/datastore you could insert this into the MetaDB using:

	java -jar seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.ModuleRunner -- --module net.sourceforge.seqware.pipeline.modules.GenericMetadataSaver --metadata-parent-accession 25192 --metadata-processing-accession-file accession.txt -- --gms-output-file text::text/plain::/datastore/simple2.txt --gms-algorithm UploadText --gms-suppress-output-file-check

Here files are associated with the parent (SWID: 25192 which is a sample). One
word of caution, if you expect people to download your files through the Portal
then the paths you inject into the database must start with /datastore/ or
wherever Tomcat (the web server) expects to find uploaded files.  For the VM,
this is /datastore/. See the section on SeqWare Portal for more information on
configuring Tomcat.

## Listing Available Workflows and Their Parameters

Once you have uploaded data the next step is to find the available workflows and their parameters.  To see the list of available workflows you can execute the following command:

	java -jar seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- --list-install

You will get a tab-delimited list of workflows showing their name, version, and (most importantly) their SWID.  

<p class="warning"><strong>Tip:</strong> it may be easier for you to read the output by cutting and pasting into a spreadsheet.</p>

In this example we are going to use the latest (at the time of this writing) HelloWorld workflow bundle (SWID 7 below).  The output of the above command includes the line:

  HelloWorldWorkflow      1.0     Wed Aug 15 19:00:11 EDT 2012    7       /home/seqware/SeqWare/released-bundles/Workflow_Bundle_HelloWorldWorkflow_1.0_SeqWare_0.12.5.zip

The fourth column includes the SWID for this workflow that you will use in the next command to find all the parameters (and their defaults) that this workflow takes.  Here is the command, notice I redirect the output to create a basic ini file that can later be customized and used to submit a run of this workflow:

	java -jar seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- --list-workflow-params --workflow-accession 7 > workflow.ini

In this example the workflow “HelloWorldWorkflow” version 1.0 (SWID 7) parameters are listed.  The output conforms to the input you can use to parameterize and launch workflows.  For example:

<pre>
#key=input_file:type=file:display=F:display_name=input_file:file_meta_type=text/plain
input_file=${workflow_bundle_dir}/Workflow_Bundle_helloworld/1.0/data/input.txt
#key=output_dir:type=text:display=F:display_name=output_dir
output_dir=seqware-results
#key=output_prefix:type=text:display=F:display_name=output_prefix
output_prefix=/datastore/
</pre>

<p class="warning"><strong>Note:</strong> the lines above have been wrapped but you should not include line breaks in your file.  Instead, make sure the file that you create using this tool (and customize for later launching a workflow) includes comment lines starting with “#” and the key=value lines only.  In the command above the redirect to the file workflow.ini will include some extra lines of status output.  Make sure you remove these before continuing to launch the workflow with this ini file.  You can customize any values from the key/value pairs that you need to.  For example, the most frequent parameters you will customize are input files.  In the workflow example above you will want to customize input_file.  For example, if you wanted to process the file you uploaded you would customize the line as:

	input_file=/datastore/simple.txt

Since this is a low-level tool you may see many more parameters exposed with this tool than you would using the web Portal application.  Please use caution when customizing these values since some refer to items that affect the underlying infrastructure.
</p>

<p class="warning"><strong>Tip:</strong> when you customize key-values in the ini file prepared above you do not need to include key-values that you leave unchanged.  If you do not include these the workflow will run with those values by default anyway.  Removing unchanged key-values will greatly reduce the size of your ini files making it much easier to see the key-values you are interested in.</p>


## Triggering a Workflow and Monitoring Progress 

At this point you know what workflow you are going to run and you have a customized ini file that contains, for example, the input files. The next step is to trigger the workflow using the ini file you prepared. Make sure you use the correct workflow accession and parent accession. Use the parent accession of the "Analysis Event" that the file is attached to not the file itself.

	java -jar seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --ini-files workflow.ini --workflow-accession 7 --schedule --parent-accessions 24 --host `hostname --long` 

<p class="warning"><strong>Tip:</strong> the parent-accessions is the SWID of
the ProvisionFiles element that was added under the sample when use used this
tool to upload the text files in the example above.  You MUST specify this
otherwise the workflow's results will not be linked to anything (they will be
orphaned and will not be visible in the Portal or present in the reports
below). Conveniently the ProvisionFiles tool will write these accessions to a
file and the portal displays these values.</p>

This schedules the workflow to run on the VM. Notice it also prints the workflow run accession which you can use to help monitor the workflow.

Once submitted, you can use the Portal to list the number of submitted, running, and failed workflows.  Log into the Portal and click on the "Show Analysis" link under the Analysis panel.  You can then click on the tab for "Running Analysis" to see what is submitted/running/failed.

<img src="/assets/images/running.png" width="600px"/>

A better way of monitoring workflows (and getting a list of the outputs) is to use the WorkflowRunReporter plugin. This will let you script the monitoring of workflow runs.

	java -jar ~/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter -- -wa 7

In this example all the status information for workflows with workflow accession 7 are printed out to a file in the local file system.  This includes several columns of interest including the status of the workflow, the output file types, and their locations in S3 or the file system. You can use this information to automate the checking of workflows and the retrieval of the results!

In the output from the above command you will see accessions for each workflow run. If the status is “failed” you can download the stderr and stdout from the workflow run. This is how you might do that for a workflow_run with an accession of 6774:

	java -jar ~/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter -- --wra 6774 --wr-stderr
	java -jar ~/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter -- --wra 6774 --wr-stdout

## Downloading Workflow Results

Once a workflow has finished running you will want to list out the associated files and download the results.  While you can use the Portal for downloading files the best way to get files in bulk is to use our reporting tool. This produces a tab-delimited file that lists all the files produced for the workflows you are interested in.  You can then use the same ProvisionFiles utility above to pull files back.  Since the report produces a simple tab-delimited file you can easily automate the downloading of results by looping over the output files and calling ProvisionFiles using a script.

	java -jar seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.SymLinkFileReporter -- --no-links --output-filename study_report --workflow-accession 7 --study 'New Test Study'

The output here is a study_report.csv file that contains a line for each file (both those uploaded and those produced by workflows).  You can also filter by file types, for example if you want to see report bundles (not applicable to the HelloWorld workflow but you get the idea):

	java -jar seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.SymLinkFileReporter -- --no-links --output-filename study_report --workflow-accession 13224 --study 20120403_SEQ1 --file-type application/zip-report-bundle

Or an example filtering by sample (again, not directly applicable to the HelloWorld output):

	java -jar seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.SymLinkFileReporter -- --no-links --output-filename study_report --workflow-accession 13224  --sample 20120403_SEQ1_GAG

You can use these URLs (such as s3://bucket/samplename/test_R1.text.gz) with ProvisionFiles to download results (if they were remote, in the local VM they are just local files).  Here’s an example downloading a report bundle:

	java -jar seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.ModuleRunner -- --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles --no-metadata -- -i s3://bucket/results/seqware-0.10.0_ComprehensiveExomeGenomeAnalysis-0.10.5/59491657/GAG.fa.variant_quality.gatk.hg19.report.zip -o ./

Here the zip report bundle is downloaded to the current working directory on the computer you are working on.  In this way you can pull back the results of workflows entirely through scripts that wrap the SymLinkFileReporter and ProvisionFiles.

Also note the SymLinkFileReporter gives you SWIDs for processing events and entities such as studies, samples, and experiments.  You can use this tool to find these SWIDs that are used as “parents” for workflow runs.

In addition to the command line tools, you can also use the Portal to explore the output of workflows triggered through the command line tools.

You can find more information on this report tool on the public SeqWare wiki: [Study Reporter](/docs/21-study-reporter/).

<p class="warning"><strong>Note:</strong> in the example above I use --no-metadata with ProvisionFiles. This is to prevent the tool from writing back an event to the central database. Since you are just downloading a file (versus uploading a file) you do not really want to record that download event in the database.
</p>


## Next Steps

See the [Developer Tutorial](/docs/3-getting-started/developer-tutorial/) for how to create a new workflow.

