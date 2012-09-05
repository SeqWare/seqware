---

title:                 "Developer Tutorial"
markdown:              advanced
toc_includes_sections: true

---

This guide picks up where the [User Tutorial](/docs/3-getting-started/user-tutorial/) left off. In that previous guide we showed you how to start up your local VM, create studies, experiments, and samples, associate an input file with a sample, and then launch a workflow to process that file.  This process is the same generic process we use at OICR to analyze samples from fastq to, eventually, annotated variants.  In this production system the workflows are fairly complex (they include branching and looping) and we string multiple worklfows together (output of one as input for the next) using <kbd>deciders</kbd>.

The next step presented in this tutorial is to create a workflow of your own based on the HelloWorld that comes bundled with the VM.  In theory you could use either a local VM or an Amazon instance to follow the tutorial below but in our case we will base it on the local VM.

## By the End of This Tutorial

By the end of these tutorials you will:

* create a new SeqWare Pipeline workflow bundle based on HelloWorld
* package, install, schedule, and run your new workflow bundle in Pipeline and MetaDB
* generate a report on the outputs of your workflows in Pipeline and Portal
* be prepared to move on to more detailed documentation for each sub-project

## First Steps

Please launch your local VM in VirtualBox and login as user <kbd>seqware</kbd>, password <kbd>seqware</kbd> at this time. Click on the "SeqWare Directory" link on the desktop which will open a terminal to the location where we installed the SeqWare tools.

## Overview of Workflow Development Using the VM

You should be in the ~seqware/SeqWare directory now, this is
the working directory.  Notice there is a jar file here and also two important
directories: provisioned-bundles which contains unzipped workflow bundles and
is where you will work on new bundles and released-bundles (SW_BUNDLE_REPO_DIR
in the config) which contains zip versions of the workflows that you create
when you package up these bundles and install them locally or on the cloud. You
will work in provisioned-bundles, copying a template HelloWorld workflow to a
new bundle that you can modify You will also test your bundles from there as
well. Once you finish with the new bundle you will package and install it via
the web service (to either the remote cloud VM or local VM). Once
installed you can use the workflow scheduling tools you have used before to
trigger workflows (on the cloud or the local VM), monitor them, and get data
back.

## The Sample Workflow Bundle

Go into the bundle directory so you can look around:

 cd provisioned-bundles/Workflow_Bundle_HelloWorldWorkflow_1.0_SeqWare_0.12.5/Workflow_Bundle_helloworld/1.0/

You
should look at three files in particular.  The workflows/workflow.ftl and the
config/workflow.ini.  The first is the workflow script file written in an XML
file format from the Pegasus project.  It is templatized using FreeMarker so you
can replace variables using values from the config file and also write simple
macros and control blocks.  In this workflow example you see 4 key jobs.  The
first (IDPRE1) is the job that copies the input file specified either from S3
or another location.  The second job (ID001) is the meat of the workflow, it is
simply calling a script in the bin directory that echos a file and also writes
"Hello World" to the output.  The third and fourth jobs are related.  The first
(IDPOST1) copies the output file to the final destination while the second
records the output file back to the metadatabase.

Here is the example workflow XML:

<pre>
<?xml version="1.0" encoding="UTF-8"?>
<adag xmlns="http://pegasus.isi.edu/schema/DAX" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://pegasus.isi.edu/schema/DAX http://pegasus.isi.edu/schema/dax-3.2.xsd" version="3.2" count="1" index="0" name="HelloWorldWorkflow">
<!--
This is a sample HelloWorld workflow. Use it as a template to build your own workflow.
-->
<!-- the directory structure inside the bundle -->
<#assign workflow_name = "Workflow_Bundle_helloworld/1.0"/>
<!-- MACRO: to create a mkdir pre job and stage mkdir binary -->
<#macro requires_dir dir>
  <profile namespace="env" key="GRIDSTART_PREJOB">/${workflow_bundle_dir}/${workflow_name}/bin/globus/pegasus-dirmanager -c -d ${dir}</profile>
</#macro>
<!-- VARS -->
<#-- workflow and seqware versions -->
<#assign seqware_version = "0.12.5"/>
<#assign workflow_version = "1.0"/>
<#assign java_version = "1.6.0"/>
<#assign perl_version = "5.14.1"/>
<#-- make sure it is a string -->
<#assign parentAccessions = "${parent_accessions}"/>
<#-- Set relative paths for files within the run-->
<#assign bin_dir = "bin"/>
<#assign data_dir = "data"/>
<#assign lib_dir = "lib"/>

<!-- BASE FILE NAME -->
<#-- Set the basename from input file name -->
<#list input_file?split("/") as tmp>
  <#assign basename = tmp/>
</#list>
<!-- EXECUTABLES INCLUDED WITH BUNDLE -->
<executable namespace="seqware" name="java" version="${java_version}" 
            arch="x86_64" os="linux" installed="true" >
  <!-- the path to the tool that actually runs a given module -->
  <pfn url="file:///${workflow_bundle_dir}/${workflow_name}/bin/jre1.6.0_29/bin/java" site="${seqware_cluster}"/>
</executable>
<executable namespace="seqware" name="perl" version="${perl_version}" 
            arch="x86_64" os="linux" installed="true" >
  <!-- the path to the tool that actually runs a given module -->
  <pfn url="file:///${workflow_bundle_dir}/${workflow_name}/bin/perl-5.14.1/perl" site="${seqware_cluster}"/>
</executable>
<executable namespace="pegasus" name="dirmanager" version="1" 
            arch="x86_64" os="linux" installed="true" >
  <!-- the path to the tool that actually runs a given module -->
  <pfn url="file:///${workflow_bundle_dir}/${workflow_name}/bin/globus/pegasus-dirmanager" site="${seqware_cluster}"/>     
</executable>

<!-- Part 1: Define all jobs -->
   <!-- find input base file name -->
   <#list input_file?split("/") as tmp>
     <#assign basename = tmp/>
   </#list>
   <!-- Provision input file, make link if local file, copy from HTTP/S3 otherwise -->
  <job id="IDPRE1" namespace="seqware" name="java" version="${java_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}-full.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --no-metadata      
      --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles
      --
      --input-file ${input_file}
      --output-dir ${data_dir}
    </argument>
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">2000</profile>
  </job>
  <!-- HelloWorld job, just calls a simple script. -->
  <#assign algo = "job1"/>
  <job id="ID001" namespace="seqware" name="java" version="${java_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/bin:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}-full.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
      <#list parentAccessions?split(",") as pa>
      --metadata-parent-accession ${pa}
      </#list>
      --metadata-processing-accession-file ${data_dir}/${algo}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-accession ${workflow_run_accession}
      --module net.sourceforge.seqware.pipeline.modules.GenericCommandRunner
      --
      --gcr-command ${workflow_bundle_dir}/${workflow_name}/bin/hello.sh ${data_dir}/${basename} ${data_dir}/${basename}.hello
    </argument>
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">2000</profile>
    <!-- Prejob to make output directory -->
    <@requires_dir "${data_dir}"/>
  </job>
  <!-- Provision output file to either local dir or S3 -->
  <job id="IDPOST1" namespace="seqware" name="java" version="${java_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}-full.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --no-metadata      
      --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles
      --
      --force-copy
      --input-file ${data_dir}/${basename}.hello
      --output-dir ${output_prefix}${output_dir}/seqware-${seqware_version}_HelloWorld-${workflow_version}/${random}
    </argument>
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">2000</profile>
  </job>
  <!-- Save output locations to MetaDB -->
  <#assign algo = "jobpost2"/>
  <job id="IDPOST2" namespace="seqware" name="java" version="${java_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}-full.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
      <#list parentAccessions?split(",") as pa>
      --metadata-parent-accession ${pa}
      </#list>
      --metadata-processing-accession-file ${data_dir}/${algo}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-accession ${workflow_run_accession}
      --module net.sourceforge.seqware.pipeline.modules.GenericMetadataSaver
      --
      --gms-algorithm ComprehensiveMitochondrialGenomeAnalysisSummary
      --gms-suppress-output-file-check
      --gms-output-file HelloWorld::text/plain::${output_dir}/seqware-${seqware_version}_HelloWorld-${workflow_version}/${random}/${basename}.hello
    </argument>
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">2000</profile>
  </job>

<!-- End of Job Definitions -->
<!-- Part 2: list of control-flow dependencies -->
  <!-- Define task group dependencies -->
  <child ref="ID001">
    <parent ref="IDPRE1"/>
  </child>
  <child ref="IDPOST1">
    <parent ref="ID001"/>
  </child>
  <child ref="IDPOST2">
    <parent ref="IDPOST1"/>
  </child>
<!-- End of Dependencies -->
</adag>
</pre>

The ini config file is the place where you define 1) what parameters the
workflow will take (via the Portal web app or Web Service) and 2) what the
default values should be.  Often times only a small handful of variables need
to be exposed, the rest can be hidden from the Porta/Web Service (display=F)
and just the default value used.

Looking at:

 /home/seqware/SeqWare/provisioned-bundles/Workflow_Bundle_HelloWorldWorkflow_1.0_SeqWare_0.12.5/Workflow_Bundle_helloworld/1.0/config/workflow.ini

<pre>
# key=input_file:type=file:display=F:file_meta_type=text/plain
input_file=${workflow_bundle_dir}/Workflow_Bundle_helloworld/1.0/data/input.txt
# this is just a comment, the output directory is a conventions and used in many workflows to specify a relative output path
output_dir=seqware-results
# the output_prefix is a convension and used to specify the root of the absolute output path or an S3 bucket name 
# you should pick a path that is available on all custer nodes and can be written by your user
# ends in a "/"
output_prefix=./provisioned/
</pre>

The final file to look at is metadata.xml which is the place where workflows included in a bundle are declared.  This is the place where you would rename a workflow, update its version string, etc.

Look at: 

 /home/seqware/SeqWare/provisioned-bundles/Workflow_Bundle_HelloWorldWorkflow_1.0_SeqWare_0.12.5/Workflow_Bundle_helloworld/1.0/metadata.xml

<pre>
<bundle version="1.0">
  <workflow name="HelloWorldWorkflow" version="1.0">
    <description>This is just a simple HelloWorld showing you how to create a very basic workflow.</description>
    <test command="java -jar ${workflow_bundle_dir}/Workflow_Bundle_helloworld/1.0/lib/seqware-pipeline-0.12.5.jar --plugin net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --no-metadata --provisioned-bundle-dir ${workflow_bundle_dir} --workflow HelloWorldWorkflow --version 1.0 --ini-files ${workflow_bundle_dir}/Workflow_Bundle_helloworld/1.0/config/workflow.ini "/>
    <workflow_command command="java -jar ${workflow_bundle_dir}/Workflow_Bundle_helloworld/1.0/lib/seqware-pipeline-0.12.5.jar --plugin net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --bundle ${workflow_bundle_dir} --workflow HelloWorldWorkflow --version 1.0 "/>
    <workflow_template path="${workflow_bundle_dir}/Workflow_Bundle_helloworld/1.0/workflows/workflow.ftl"/>
    <config path="${workflow_bundle_dir}/Workflow_Bundle_helloworld/1.0/config/workflow.ini"/>
    <build command="ant -f ${workflow_bundle_dir}/Workflow_Bundle_helloworld/1.0/build.xml"/>
    <requirements compute="single" memory="20M" network="local"/>
  </workflow>
</bundle>
</pre>

This is only a brief introduction to the workflow bundle process.  Take a look at http://sourceforge.net/apps/mediawiki/seqware/index.php?title=How_to_Write_a_Bundled_Workflow for extensive details on the workflow development process.  There is also information on this site on using Maven to create workflow bundles using the excellent archetype system.  And we also are prototyping a workflow language based on Java. These are experimental but could be very useful. Eventually that documentation will be migrated to this guide.

## Creating a New Worlflow Bundle

Now that you have seen the components for a bundle the next step is to create your own bundle based on HelloWorld.  We are only going to make superficial changes here but would expect that you would make much more radical changes if you were creating a new workflow.  While you can use the "copy and hack" method, in the future we will recommend the archetype approach.

First you need to change directories to the workflow and create a duplicate:

<pre>
cd /home/seqware/SeqWare/provisioned-bundles
cp -r Workflow_Bundle_HelloWorldWorkflow_1.0_SeqWare_0.12.5 Workflow_Bundle_MyHelloWorldWorkflow_1.0_SeqWare_0.12.5
</pre>

Now modify the metadata.xml, change the name of the workflow here:

<pre>
<bundle version="1.0">
  <workflow name="MyHelloWorldWorkflow" version="1.0">
    <description>This is just a simple HelloWorld showing you how to create a very basic workflow.</description>
    <test command="java -jar ${workflow_bundle_dir}/Workflow_Bundle_helloworld/1.0/lib/seqware-pipeline-0.12.5.jar --plugin net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --no-metadata --provisioned-bundle-dir ${workflow_bundle_dir} --workflow MyHelloWorldWorkflow --version 1.0 --ini-files ${workflow_bundle_dir}/Workflow_Bundle_helloworld/1.0/config/workflow.ini "/>
    <workflow_command command="java -jar ${workflow_bundle_dir}/Workflow_Bundle_helloworld/1.0/lib/seqware-pipeline-0.12.5.jar --plugin net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --bundle ${workflow_bundle_dir} --workflow MyHelloWorldWorkflow --version 1.0 "/>
    <workflow_template path="${workflow_bundle_dir}/Workflow_Bundle_helloworld/1.0/workflows/workflow.ftl"/>
    <config path="${workflow_bundle_dir}/Workflow_Bundle_helloworld/1.0/config/workflow.ini"/>
    <build command="ant -f ${workflow_bundle_dir}/Workflow_Bundle_helloworld/1.0/build.xml"/>
    <requirements compute="single" memory="20M" network="local"/>
  </workflow>
</bundle>
</pre>

Next, change the name at the top of the workflow bundle:

<pre>
<?xml version="1.0" encoding="UTF-8"?>
<adag xmlns="http://pegasus.isi.edu/schema/DAX" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://pegasus.isi.edu/schema/DAX http://pegasus.isi.edu/schema/dax-3.2.xsd" version="3.2" count="1" index="0" name="MyHelloWorldWorkflow">
...
</pre>

That should be it for this example. More radical changes would, of course, be made in a real-world workflow.


## Test the MyHelloWorld Workflow Bundle on the VM

At this point you should be in the new workflow directory:

<pre>
/home/seqware/SeqWare/provisioned-bundles/Workflow_Bundle_MyHelloWorldWorkflow_1.0_SeqWare_0.12.5/Workflow_Bundle_helloworld/1.0
</pre>

This is where you will execute the test command to try to run the workflow on the local VM with sample default data.  You will repeat this process many times in your new bundle development process as you tweak and change your workflows, debugging as you go.

<p class="warning"><strong>Tip:</strong> if someone gives you a zipped workflow bundle use a command similar to the below to unzip it to your /home/seqware/SeqWare/provisioned-bundles/ directory:

<pre>
java -jar ~/SeqWare/seqware-pipeline-0.12.5.jar -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- --bundle released-bundles/Workflow_Bundle_Someone_Gave_Me.zip --list
</pre>

This automatically unzips the bundle to the provisioned-bundles directory and then lists out the contents.
 
<pre>
# sample output
Running Plugin: net.sourceforge.seqware.pipeline.plugins.BundleManager
Setting Up Plugin: net.sourceforge.seqware.pipeline.plugins.BundleManager@a94884d
TEMPLATE PATH: ${workflow_bundle_dir}/Workflow_Bundle_helloworld/1.0/workflows/workflow.ftl
List Workflows:
 Workflow:
  Name : HelloWorldWorkflow
  Version : 1.0
  Description : This is just a simple HelloWorld showing you how to create a very basic workflow.
  Test Command: java -jar ${workflow_bundle_dir}/Workflow_Bundle_helloworld/1.0/lib/seqware-pipeline-0.12.5.jar --plugin net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --no-metadata --provisioned-bundle-dir ${workflow_bundle_dir} --workflow HelloWorldWorkflow --version 1.0 --ini-files ${workflow_bundle_dir}/Workflow_Bundle_helloworld/1.0/config/workflow.ini
  Template Path:${workflow_bundle_dir}/Workflow_Bundle_helloworld/1.0/workflows/workflow.ftl
  Config Path:${workflow_bundle_dir}/Workflow_Bundle_helloworld/1.0/config/workflow.ini
  Requirements Compute: single Memory: 20M Network: local

</pre>
</p>

Now in this example below I am just changing into the MyHelloWorld workflow directory and using the WorkflowLauncher to launch the workflow with the sample ini file included.

<pre>
# cd into the right directory
cd /home/seqware/SeqWare/provisioned-bundles/Workflow_Bundle_MyHelloWorldWorkflow_1.0_SeqWare_0.12.5
 
# and launch the test
java -jar ~/SeqWare/seqware-pipeline-0.12.5.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --no-metadata --provisioned-bundle-dir `pwd` --workflow MyHelloWorldWorkflow --version 1.0 --ini-files Workflow_Bundle_helloworld/1.0/config/workflow.ini --wait
</pre>

This should work just fine, it will give you status information and gradually complete the workflow.  If something goes wrong you should see status messages indicating what happened.  These will tell you the particular steps with the problem so you can look at the workflow file to debug.  Here is the sample output when I ran the command:

<pre>
TEMPLATE PATH: ${workflow_bundle_dir}/Workflow_Bundle_helloworld/1.0/workflows/workflow.ftl
TEMPLATE FILE: /home/seqware/SeqWare/provisioned-bundles/Workflow_Bundle_HelloWorldWorkflow_1.0_SeqWare_0.12.5/Workflow_Bundle_helloworld/1.0/workflows/workflow.ftl
INI FILES: Workflow_Bundle_helloworld/1.0/config/workflow.ini
CREATING DAX IN: /tmp/dax88379466867104723094914822612050
  INI FILE: Workflow_Bundle_helloworld/1.0/config/workflow.ini
  KEY: parent-accessions VALUE: 0
  KEY: parent_accessions VALUE: 0
  KEY: output_prefix VALUE: ./provisioned/
  KEY: output_dir VALUE: seqware-results
  KEY: workflow_run_accession VALUE: 0
  KEY: parent_accession VALUE: 0
  KEY: input_file VALUE: ${workflow_bundle_dir}/Workflow_Bundle_helloworld/1.0/data/input.txt
  KEY: workflow-run-accession VALUE: 0
  KEY: seqware_cluster VALUE: seqwarevm
  KEY: metadata VALUE: no-metadata
  KEY: workflow_bundle_dir VALUE: /home/seqware/SeqWare/provisioned-bundles/Workflow_Bundle_HelloWorldWorkflow_1.0_SeqWare_0.12.5
SUBMITTING TO PEGASUS: pegasus-plan -Dpegasus.user.properties=/home/seqware/.seqware/pegasus/properties --dax /tmp/dax88379466867104723094914822612050 --dir /home/seqware/SeqWare/pegasus-dax -o seqwarevm --force --submit -s seqwarevm
PEGASUS STATUS COMMAND: pegasus-status -l /home/seqware/SeqWare/pegasus-dax/seqware/pegasus/HelloWorldWorkflow/run0004
MONITORING PEGASUS STATUS:
RUNNING: step 1 of 5 (20%)
RUNNING: step 2 of 5 (40%)
RUNNING: step 3 of 5 (60%)
RUNNING: step 4 of 5 (80%)
WORKFLOW COMPLETED SUCCESSFULLY!
</pre>

## Packaging and Installing the MyHelloWorld Workflow Bundle Locally

Assuming the workflow above worked fine the next step is to install it locally,
this means it will be inserted into the MetaDB via the locally running web
service.  During this process it will zip up the workflow bundle and put it
into your released-bundles directory. Once you have the zip file you can share it with
other users and, in the future, upload it to an AppStore to make it even easier to share.

Here is an example showing how this
process works on the VM and what is happing in the database and your
released-bundles directory as you do this.  You may want to delete the zip file
that is in the released-bundles directory before you do this step below (or back
it up somewhere first).  To connect to the database in the example below you
can issue the following command in the terminal:

<pre> "psql -U seqware -W
seqware_meta_db"</pre>

...with password seqware.

<pre>
cd /home/seqware/SeqWare
java -jar ~/SeqWare/seqware-pipeline-0.12.5.jar -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- --bundle /home/seqware/SeqWare/provisioned-bundles/Workflow_Bundle_MyHelloWorldWorkflow_1.0_SeqWare_0.12.5 --install
</pre>

What happens here is the /home/seqware/SeqWare/provisioned-bundles/Workflow_Bundle_MyHelloWorldWorkflow_1.0_SeqWare_0.12.5 directory is zip'd up to your released-bundles directory and the metadata about the workflow is saved to the database.
 
The output of this program:

<pre>
# the output
Installing Bundle
Bundle: /home/seqware/SeqWare/provisioned-bundles/Workflow_Bundle_MyHelloWorldWorkflow_1.0_SeqWare_0.12.5
Now packaging /home/seqware/SeqWare/provisioned-bundles/Workflow_Bundle_MyHelloWorldWorkflow_1.0_SeqWare_0.12.5 to a zip file and transferring to the directory: /home/seqware/SeqWare/released-bundles Please be aware, this process can take hours if the bundle is many GB in size.
...
Bundle Has Been Installed to the MetaDB and Provisioned to /home/seqware/SeqWare/provisioned-bundles/Workflow_Bundle_MyHelloWorldWorkflow_1.0_SeqWare_0.12.5!
</pre>
 
Now notice two things, it is now in the local DB via the web service.

<pre>
seqware_meta_db=# select * from workflow order by create_tstmp desc limit 1;
-[ RECORD 1 ]-------------+----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
workflow_id               | 3
name                      | MyHelloWorldWorkflow
description               | This is just a simple HelloWorld showing you how to create a very basic workflow.
input_algorithm           |
version                   | 1.0
seqware_version           |
owner_id                  | 1
base_ini_file             | ${workflow_bundle_dir}/Workflow_Bundle_helloworld/1.0/config/workflow.ini
cmd                       | java -jar ${workflow_bundle_dir}/Workflow_Bundle_helloworld/1.0/lib/seqware-pipeline-0.12.5.jar --plugin net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --bundle ${workflow_bundle_dir} --workflow MyHelloWorldWorkflow --version 1.0
current_working_dir       | /home/seqware/SeqWare/provisioned-bundles/Workflow_Bundle_MyHelloWorldWorkflow_1.0_SeqWare_0.12.5
host                      |
username                  |
workflow_template         | ${workflow_bundle_dir}/Workflow_Bundle_helloworld/1.0/workflows/workflow.ftl
create_tstmp              | 2012-08-15 01:20:22.457
update_tstmp              | 2012-08-15 01:20:22.505
sw_accession              | 6
permanent_bundle_location | /home/seqware/SeqWare/released-bundles/Workflow_Bundle_MyHelloWorldWorkflow_1.0_SeqWare_0.12.5.zip
</pre>

And also notice you now have a zip file for this workflow:

<pre>
cd /home/seqware/SeqWare/released-bundles
ls -lth
total 50M
-rw-rw-r-- 1 seqware seqware 50M Aug 15 01:20 Workflow_Bundle_MyHelloWorldWorkflow_1.0_SeqWare_0.12.5.zip
</pre>

Now that this is installed you can trigger it just like you have done in the [User Tutorial](/docs/3-getting-started/user-tutorial/) using the web service previously.

## Next Steps

The guide http://sourceforge.net/apps/mediawiki/seqware/index.php?title=How_to_Write_a_Bundled_Workflow on the public SeqWare project wiki goes into very detailed information about workflow bundles, how to create them, the syntax they use, and other key information. In the near future this will be migrated to this site but the directions there should still be up to date.


