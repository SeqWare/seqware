---

title:                 "User Tutorial"
markdown:              advanced
toc_includes_sections: false

---

The majority of this guide is dedicated to walking users and developers through the basics of using SeqWare. We assume that people are most interested in the Pipeline sub-project and focus most of our time on that.  The examples below will all be based on a local VM but the environment on our cloud instance is almost identical, so most of the examples below will be applicable to either VM type. In the future we will probably have a spearate user guide that focuses on the differences of running on Amazon's cloud.

## By the End of This Tutorial

This guide will show you how to use command line tools from Pipeline to access the MetaDB. This will allow you to do the following tasks using tools that can be scripted versus our Portal web-based interface that requires a user to click on an interface (we will show examples of the latter along the way too). By the end of these tutorials you will be able to:

* use both command line and web-based tools from Pipeline and Portal respectively
* create studies, experiments, and samples in the MetaDB
* upload data such as fastq files to the VM and associate that data with particular samples in the MetaDB
* find the list of available workflows and the parameters they accept using Pipeline
* schedule a HelloWorld workflow and monitor its progress using Pipeline
* generate a report on the outputs of your workflows in Pipeline and Portal
* download files produced by a workflow using Pipeline tools

The command line tools are all Java tools from SeqWare Pipeline that wrap our RESTful SeqWare Web Service. If you would like to learn more about the low-level API (perhaps you want to call it directly in a program or script) you can find more information in the [SeqWare Web Service](/docs/7-web-service/) documentation.

## The Example

In this tutorial we will use a simple HelloWorld workflow that takes a text file as input and creates another file as output. The same examples could be applied to any workflows and input data types.  How to build your own workflows (**the central purpose of SeqWare**) is covered in the [Developer Tutorial](/docs/3-getting-started/developer-tutorial/).

## First Steps

Please launch your local VM in VirtualBox and login as user <kbd>seqware</kbd>, password <kbd>seqware</kbd> at this time. Click on the "SeqWare Directory" link on the desktop which will open a terminal to the location where we installed the SeqWare tools.

## The SeqWare Command Line Tool

SeqWare is open source architecture built mostly in Java. In the <kbd>/home/seqware/SeqWare</kbd> directory you will see a jar file. This contains SeqWare Pipeline code that will allow you to interact with the SeqWare Web service (actually either on a VM, installed on another local machine/cluster, or in the cloud) that controls, among other things, workflow execution. This jar is, essentially, the command line interface for the whole SeqWare project.

<p class="warning"><strong>Tip:</strong> The VM will contain a recent version of the jar that we have validated with this tutorial.  You may want to upgrade to the latest version, though, which you can download from our continuous build server. Please choose the jar that has the -full suffix, e.g. <a href="http://jenkins.res.oicr.on.ca/job/seqware/lastStableBuild/net.sourceforge.seqware$seqware-distribution/">seqware-distribution-0.12.5-SNAPSHOT-full.jar</a>. Keep in mind we make no promisses that the latest version will be bug free!</p>

## The SeqWare Settings File

The SeqWare jar file uses a simple configuration file that has been setup for you already on the VM. By default the location is ~/.seqware/settings.

This file contains the web address of the RESTful web service, your username and password, and you Amazon public and private keys that will allow you to push and pull data files to and from the cloud, etc.  Here is the example settings file from the VM, this will be ready to work on the VM but keep in mind, this is where you would change settings if you, for example, setup the Web Service and MetaDB on another server or you launched a VM on the cloud and wanted to use the local VM command line jar to control the remote server.  Another common thing you may want to do is use the ProvisionFiles module (described later) to push and pull data into/out of the cloud. This is the file where you would supply your access and secret keys that you got when signing up for Amazon (keep those safe!):

<pre>
#
# SEQWARE PIPELINE SETTINGS
#
# the name of the cluster as defined in the Pegasus sites.xml config file
SW_CLUSTER=seqwarevm
# the directory used to store the generated DAX workflow documents before submission to the cluster
SW_DAX_DIR=/home/seqware/SeqWare/pegasus-dax
# the directory containing all the Pegasus config files this instance of SeqWare should use
SW_PEGASUS_CONFIG_DIR=/home/seqware/.seqware/pegasus
# SeqWare MetaDB communication method, can be "database" or "webservice" or "none"
SW_METADATA_METHOD=webservice
# a directory to copy bundles to for archiving/installing
SW_BUNDLE_DIR=/home/seqware/SeqWare/provisioned-bundles
# the central repository for installed bundles
SW_BUNDLE_REPO_DIR=/home/seqware/SeqWare/released-bundles
#
# SEQWARE WEBSERVICE SETTINGS
#
# the base URL for the RESTful SeqWare API
SW_REST_URL=http://localhost:8080/SeqWareWebService
# the username and password to connect to the REST API, this is used by SeqWare Pipeline to write back processing info to the DB
SW_REST_USER=admin@admin.com
SW_REST_PASS=admin

# SEQWARE TESTING SETTINGS
#
SW_DB_USER=seqware
SW_DB_PASS=seqware
SW_DB_SERVER=localhost
SW_DB=test_seqware_meta_db

#
# AMAZON CLOUD SETTINGS
#
# used by tools reading and writing to S3 buckets (dependency data/software bundles, inputs, outputs, etc)
# most likely not used here at OICR
AWS_ACCESS_KEY=lksjdflksjdklf
AWS_SECRET_KEY=slkdjfeoiksdlkjflksjejlfkjeloijxelkj
</pre>

## Creating Studies, Experiments, and Samples

SeqWare MetaDB lets you track studies, experiment, and samples and then link those to files (like FASTQ or something similar). You can then run workflows on those files, track the resulting files, and use those files for the next workflow.  You will want to set up your study, experiments, and samples before uploading your text or other data files.  This ensures you have “parents” to attach these files to.  Otherwise you will not be able to use them as parameters for workflows. 

You can do this either with the [Portal](http://SeqWarePortal "SeqWare Portal") or via the command line tools below.

### Via the Portal

First, login to the portal, the URL is http://localhost:8080/SeqWarePortal and you will need to use the browser inside the VM to access this. The default username is <kbd>admin@admin.com</kdb> and the default password is <kbd>admin</kdb>.  Feel free to change your password in the web app.

<p class="warning"><strong>Tip:</strong>You could setup the VM so it gets a "real" IP address on your network, in which case you could access both the Portal and Web Service via any computer on your network.</p>

In the following screenshots you see the process that allows you to create these entities through the web application.
When you first login you will see your studies (of which you have none).  First, create a study which can be though of as a project that can have many distinct experimental designs:

<img src="/assets/images/create_study.png" width="600px"/>

You will then see your new study which includes a link to add an experiment. An experiment encapsulates a particular experimental design, for example 2x50 sequencing on the Illumina platform.  If you then did another experiment with a 2x250 read structure on the same platform that would be considered a novel experimental design.

<img src="/assets/images/create_experiment.png" width="600px"/>

Once you click on the "add experiment" link you will then be able to define the NGS platform used and the nature of the experimental design. Of note is the "Spot Decoding String" which allows you to document the structure of the reads.

The next step is to create one or more samples and associate them with this new experiment.  Under the experiment you just added you will see a link for "add sample", click it and populate the sample information.

At this point you will see your complete study/experiment/sample hierarchy in the "My Studies" section.

<img src="/assets/images/my_studies.png" width="600px"/>

<p class="warning"><strong>Tip:</strong> Notice the "SWID: 14" next to "My Test Study". The other items in the hierarchy have an SWID as well.  This is common for every item in the MetaDB, everything has a SWID aka accession.  When you use command line tools they will often take a "parent accession" to attach output to and this is refering to what you see as SWID in the Portal.</p>

<p class="warning"><strong>Tip:</strong> You can also track sequencer runs and associate samples with the particular "lanes" on the flowcell (or equivalent on the particualr sequencing platform).  This is not covered here but will be in the SeqWare Portal guide.  Suffice it to say, this lets you use Portal as a light-weight LIMS system for tracking both studies/experiments/sample and sequencer runs/lanes and how they relate to each other.</p>


### Via Command Line Tools

There are also command line tools for creating study, experiment, and samples.  The functionality for each of these is fairly limited.  For example, the rich descriptive "Spot Decoding String" language for describing how a read is structured is not yet fully supported and updates to existing entities are not yet possible.  However the basic functionality to create studies, experiments, and samples now exists in a scriptable form and that is really the point.  By providing command line tools you can automate the setup of this information using simple Bash shell scripts or the language of your choice.

First, you can find out what tables this tool is capable of writing to:

	java -jar seqware-pipeline-0.12.5.jar -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --list-tables
	
	TableName
	study
	experiment
	sample

Now, for a given table, you can find out what fields you can write back to and their type:

	java -jar seqware-pipeline-0.12.5.jar -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --table study --list-fields
	
	Field    Type    Possible_Values
	title    String
	description    String
	accession    String
	center_name    String
	center_project_name    String
	study_type    Integer    [1: Whole Genome Sequencing, 2: Metagenomics, 3: Transcriptome Analysis, 4: Resequencing, 5: Epigenetics, 6: Synthetic Genomics, 7: Forensic or Paleo-genomics, 8: Gene Regulation Study, 9: Cancer Genomics, 10: Population Genomics, 11: Other]

So using the information above you can create a new study:

	java -jar seqware-pipeline-0.12.5.jar -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --table study --create --field 'title::New Test Study' --field 'description::This is a test description' --field 'accession::InternalID123' --field 'center_name::SeqWare' --field 'center_project_name::SeqWare Test Project' --field study_type::4
	
	SWID: 29830

The output of this command above includes the line “SWID: 29830” (or whatever number is appropriate for your database).  This is very important since this number is a unique identifier across the database and used to link together entities.  For example, you will use the number produced by the study add command as the parent for the experiment you create below.  If you do not track and supply these numbers then the hierarchy of study/experiment/sample cannot be created.

The next step is to create an experiment and link it to the study you created above. You can find the platform ID using the --list-fields option shown above:

	java -jar seqware-pipeline-0.12.5.jar -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --table experiment --create --field 'title::New Test Experiment' --field 'description::This is a test description' --field study_accession::29830 --field platform_id::26
	
	SWID: 29831

Again, you use the SWID from the above output in the next step to create an associated sample. You can find the platform ID using the --list-fields option shown above:

	java -jar seqware-pipeline-0.12.5.jar -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --table sample --create --field 'title::New Test Sample' --field 'description::This is a test description' --field experiment_accession::29831 --field organism_id::26
	
	SWID: 29832

At this point you should have a nice study/experiment/sample hierarchy.  You can, of course, add multiple samples per experiment and multiple experiments per study.  For each of the samples you can now upload one or more files.  You will need the SWID from the sample creation above for this step (or visible in the Portal).  Here is a screenshot of what the above commands produce in the Portal (note, the SWIDs do not match but these are just examples):

<img src="/assets/images/final_exp.png" width="600px"/>


## Uploading Files and Associating with a Sample

The first step in uploading a file and associating with a sample is to
identify the sample’s SWID. The easiest way to do this is to use the Portal web
application to navigate through the Study/Experiment/Sample tree to the sample
you want to upload text for and to note its associated SWID.  You then need to
know the destination location to put your file in.  For the local VM this is either a directory (which defaults to <kbd>/datastore</kbd>) or a shared filesystem over NFS if you have connected your VM instance to shared storage and a cluster. The important thing is this directory is shared across your VM and any other cluster nodes it connects to if any.  The default <kbd>/datastore</kbd> will work fine here.  For Amazon's cloud this shared location is their S3 service which allows you to store arbitrary files in "buckets".  You will see in the SeqWare Pipeline section of this manual how to work with files on S3.

Once you have these two
pieces of information (destination path "/datastore/" and the SWID) you can then use either the Portal or the ProvisionFiles command line utility to put your
files into the right place and associate them with the correct sample.

###Via the Portal

First, create your text document that you want to associate with a sample (we are using a text document here since the sample HelloWorld workflow takes this as an input but typically you would upload Fastq files for a sample).  I created a document called <kbd>simple.txt</kbd> that just has some random text in it.

Now, navigate to "My Studies", pick a sample, and then click "upload file".  You can then either give the complete path to the text file ("enter the file URL into the database" or you can use the "File to upload" browser to upload the simple.txt file. The third option (using a dedicated transfer tool) requires admin configuration and only works on the cloud so we will skip it here. Click upload when you are done.

If possible you should enter a path or URL (http://server/file/path or s3://<bucket>/file/path) since this will not result in duplicate data or long uploads. If you do this then the file path should be accessible to whaever computer the workflow jobs run on (either a shared filesystem or S3). If you choose to upload it might fail for large files and it will also cause a duplicate file to live in <kbd>/datastore/uploads</kbd>.  In these examples we will just provide a URL.

<img src="/assets/images/upload.png" width="600px"/>

You can verify the upload has been successful by browsing "My Studies" section. You should be able to re-download the file, if so it was put in a location visible to the web server.

### Via Command Line Tools

The same process of uploading data described above can also be used on the command line. Here is an example of calling the ProvisionFiles command line utility:

	java -jar seqware-pipeline-0.12.5.jar -p net.sourceforge.seqware.pipeline.plugins.ModuleRunner -- --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles --metadata-output-file-prefix /datastore/ --metadata-parent-accession 29832 --metadata-processing-accession-file simple.txt -- -im text::text/plain::simple.txt -o /datastore/ --force-copy

In this example it will copy the simple.txt text files to /datastore/ directory and
will link them to the sample identified by 29832 (the sample’s SWID).  So the
final output file is "/datastore/simple.txt" in the database. If you left off
--force-copy you would get a symlink in this case since it is a local file
operation. Use the portal to find out the SWID for an existing sample or get
the SWID using the command line tool when you create a new study. Providing
that here will cause the text to be associated with that parent sample.

<p class="warning"><strong>Tip:</strong> you can find a list of the meta types (like chemical/seq-na-text-gzip or text/plain above) at <a href="http://sourceforge.net/apps/mediawiki/seqware/index.php?title=Module_Conventions#Module_MIME_Types">Module Conventions - Module MIME Types</a>. This is the list we add to as needed when creating new workflows.  It is extremely important to be consistent with these since a workflow will not recognize your input unless the meta type string matches what it expects exactly.</p>


#### Associating Existing Files with a Sample 

The best way to get data into the cloud is to use the ProvisionFiles utility above since it both uploads the data (using multiple threads to maximize performance) and also saves the metadata back to the database.  However, sometimes you have already uploaded data or, as is the case for the local VM, it is a single filesystem so there is no reason to make copies of the data (same would be true if /home/seqware/ was on an NFS share).  In this case you just want to link the files to particular samples in the database.  GenericMetadataSaver is the tool you can use to accomplish this, for example:

	java -jar seqware-pipeline-0.12.5.jar -p net.sourceforge.seqware.pipeline.plugins.ModuleRunner -- --module net.sourceforge.seqware.pipeline.modules.GenericMetadataSaver --metadata-parent-accession 25192 -- --gms-output-file text::text/plain::/home/seqware/SeqWare/simple.txt --gms-algorithm UploadText --gms-suppress-output-file-check

Here files are associated with the parent (SWID: 25192 which is a sample). One word of caution, if you expect people to download your files through the Portal then the paths you inject into the database must start with /datastore/ or wherever Tomcat (the web server) expects to find uploaded files.  For the VM, this is /datastore/. See the section on SeqWare Portal for more information on configuring Tomcat.

## Listing Available Workflows and Their Parameters

Once you have uploaded data the next step is to find the available workflows and their parameters.  To see the list of available workflows you can execute the following command:

	java -jar seqware-pipeline-0.12.5.jar -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- --list-install

You will get a tab-delimited list of workflows showing their name, version, and (most importantly) their SWID.  

<p class="warning"><strong>Tip:</strong> it may be easier for you to read the output by cutting and pasting into a spreadsheet.</p>

In this example we are going to use the latest (at the time of this writing) HelloWorld workflow bundle (SWID 7 below).  The output of the above command includes the line:

  HelloWorldWorkflow      1.0     Wed Aug 15 19:00:11 EDT 2012    7       /home/seqware/SeqWare/released-bundles/Workflow_Bundle_HelloWorldWorkflow_1.0_SeqWare_0.12.5.zip

The fourth column includes the SWID for this workflow that you will use in the next command to find all the parameters (and their defaults) that this workflow takes.  Here is the command, notice I redirect the output to create a basic ini file that can later be customized and used to submit a run of this workflow:

	java -jar seqware-pipeline-0.12.5.jar -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- --list-workflow-params --workflow-accession 7 > workflow.ini

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

<p class="warning"><strong>Tip:</strong> when you customize key-values in the ini file prepared above you do not need to include key-values that you leave unchanged.  If you do not include these the workflow will run with those values by default anyway.  Removing unchanged key-values will greatly reduce the size of your ini files making it much easier to see the key-values you are interested in.


## Triggering a Workflow and Monitoring Progress 

At this point you know what workflow you are going to run and you have a customized ini file that contains, for example, the input files. The next step is to trigger the workflow using the ini file you prepared. Make sure you use the correct workflow accession and parent accession. Use the parent accession of the "Analysis Event" that the file is attached to not the file itself.

	java -jar seqware-pipeline-0.12.5.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --ini-files workflow.ini --workflow-accession 7 --schedule --parent-accessions 24

<p class="warning"><strong>Tip:</strong> the parent-accessions is the SWID of the ProvisionFiles element that was added under the sample when use used this tool to upload the text files in the example above.  You MUST specify this otherwise the workflow’s results will not be linked to anything (they will be orphaned and will not be visible in the Portal or present in the reports below). Conveniently the ProvisionFiles tool will write these accessions to a file and the portal displays these values.

This schedules the workflow to run on the VM. Notice it also prints the workflow run accession which you can use to help monitor the workflow.

Once submitted, you can use the Portal to list the number of submitted, running, and failed workflows.  Log into the Portal and click on the “Show Analysis” link under the Analysis panel.  You can then click on the tab for “Running Analysis” to see what is submitted/running/failed.

<img src="/assets/images/running.png" width="600px"/>

A better way of monitoring workflows (and getting a list of the outputs) is to use the WorkflowRunReporter plugin. This will let you script the monitoring of workflow runs.

	java -jar ~/seqware-pipeline-0.12.5.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter -- -wa 7

In this example all the status information for workflows with workflow accession 7 are printed out to a file in the local file system.  This includes several columns of interest including the status of the workflow, the output file types, and their locations in S3 or the file system. You can use this information to automate the checking of workflows and the retrieval of the results!

In the output from the above command you will see accessions for each workflow run. If the status is “failed” you can use resources directly on the Web Service to see what went wrong by returning the stderr and stdout from the workflow. This is how you might do that for a workflow_run with an accession of 6774:

	GET -C admin@admin.com:admin http://localhost:8080/SeqWareWebService/reports/workflowruns/6774/stderr

Keep in mind two things: 

1. this is a non-standard URL since this feature is in testing
1. this direct GET request will be replaced with a command-line tool in the near future.

## Downloading Workflow Results

Once a workflow has finished running you will want to list out the associated files and download the results.  While you can use the Portal for downloading files the best way to get files in bulk is to use our reporting tool. This produces a tab-delimited file that lists all the files produced for the workflows you are interested in.  You can then use the same ProvisionFiles utility above to pull files back.  Since the report produces a simple tab-delimited file you can easily automate the downloading of results by looping over the output files and calling ProvisionFiles using a script.

	java -jar seqware-pipeline-0.12.5.jar -p net.sourceforge.seqware.pipeline.plugins.SymLinkFileReporter -- --no-links --output-filename study_report --workflow-accession 7 --study Test

The output here is a study_report.csv file that contains a line for each file (both those uploaded and those produced by workflows).  You can also filter by file types, for example if you want to see report bundles (not applicable to the HelloWorld workflow but you get the idea):

	java -jar seqware-pipeline-0.12.5.jar -p net.sourceforge.seqware.pipeline.plugins.SymLinkFileReporter -- --no-links --output-filename study_report --workflow-accession 13224 --study 20120403_SEQ1 --file-type application/zip-report-bundle

Or an example filtering by sample (again, not directly applicable to the HelloWorld output):

	java -jar seqware-pipeline-0.12.5.jar -p net.sourceforge.seqware.pipeline.plugins.SymLinkFileReporter -- --no-links --output-filename study_report --workflow-accession 13224  --sample 20120403_SEQ1_GAG

You can use these URLs (such as s3://bucket/samplename/test_R1.text.gz) with ProvisionFiles to download results (if they were remote, in the local VM they are just local files).  Here’s an example downloading a report bundle:

	java -jar seqware-pipeline-0.12.5.jar -p net.sourceforge.seqware.pipeline.plugins.ModuleRunner -- --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles --no-metadata -- -i s3://bucket/results/seqware-0.10.0_ComprehensiveExomeGenomeAnalysis-0.10.5/59491657/GAG.fa.variant_quality.gatk.hg19.report.zip -o ./

Here the zip report bundle is downloaded to the current working directory on the computer you are working on.  In this way you can pull back the results of workflows entirely through scripts that wrap the SymLinkFileReporter and ProvisionFiles.

Also note the SymLinkFileReporter gives you SWIDs for processing events and entities such as studies, samples, and experiments.  You can use this tool to find these SWIDs that are used as “parents” for workflow runs.

In addition to the command line tools, you can also use the Portal to explore the output of workflows triggered through the command line tools.

You can find more information on this report tool on the public SeqWare wiki: [Study Reporter](http://sourceforge.net/apps/mediawiki/seqware/index.php?title=SymLink_Reporter).

<p class="warning"><strong>Note:</strong> in the example above I use --no-metadata with ProvisionFiles. This is to prevent the tool from writing back an event to the central database. Since you are just downloading a file (versus uploading a file) you do not really want to record that download event in the database.


## To Do

Here are the items we are currently working on to improve the command line tools. The goal is to make the command line tools a fully functional replacement for the Portal. This will allow users to increase the throughput of file upload and workflow triggering through the system since the tools can be easily scripted.

* command line tools to list, search, and update Study, Sample, and Experiments without having to use the Portal
* displaying SWID on every entity in the Portal (making it easier to “attach” items such as Fastq files to items you see in the Portal) -- done
* command line tool for listing and monitoring active workflows -- partially done
* command line tool  for listing the parameters (ini_file field) that were used for a given workflow run (and maybe the full DAX too)

We also want to make sure the Portal is fully usable.  Future versions of this document will show:

* workflow launching via Portal too

We also will probably create a second version of this document that is tailored for running a VM on Amazon's cloud versus the local VM. This process is almost identical but the cloud has another option for storing files.
