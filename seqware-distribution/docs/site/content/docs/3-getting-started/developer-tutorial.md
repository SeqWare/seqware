---

title:                 "Developer Tutorial"
markdown:              advanced
is_dynamic:            true
toc_includes_sections: true

---

This guide picks up where the [User
Tutorial](/docs/3-getting-started/user-tutorial/) left off. In that previous
guide we showed you how to start up your local VM, create studies, experiments,
and samples, associate an input file with a sample, and then launch a workflow
to process that file.  These workflows can be complex (they include branching
and looping) and in future tutorials you will see how to string multiple
worklfows together (output of one as input for the next) using
<kbd>deciders</kbd> for automation.

In this tutorial the focus is on creating a workflow of your own based on the
HelloWorld that comes bundled with the VM.  In theory you could use either a
local VM or an Amazon instance to follow the tutorial below but in our case we
will base it on the local VM.

## By the End of This Tutorial

By the end of these tutorials you will:

* create a new SeqWare Pipeline workflow bundle based on HelloWorld
* test your workflow bundle locally
* package your new workflow as a bundle for hand-off to an administrator for installation into SeqWare Pipeline 


## A Note About Workflow Languages & Engines

Workflows in the SeqWarePipeline system can be written in one of three
languages and executed in one of two cluster environments.  The most tested
combination is [FTL](http://freemarker.sourceforge.net/) workflows running on
the [Pegasus](http://pegasus.isi.edu/) workflow engine. However we have
recently added support for workflows written in Java along with workflows
written in a simplified FTL syntax. The latter of which is the least tested of
the workflow languages.  Given its flexibility and power we recommend most
users use the Java workflow language.  In addition, we have implemented a
workflow engine for submission to Oozie (Hadoop) clusters.  This is still in
testing so we currently recommend the Pegasus engine for the tutorials here.

<img width="600" src="/assets/images/seqware_hpc_oozie.png"/>

**In this tutorial we will write a workflow using the Java Workflow Language and run it on the Pegasus Workflow Engine.**

| Workflow Language | Language Production Ready | Oozie Engine | Pegasus Engine |
| ------ | ------ | ------ | ------ | 
| FTL    | Y      | N      | Y      |
| Simple Markup    | N      | Y      | Y      |
| Java   | Y      | Y      | **Y**      |


<p class="warning"><strong>Tip:</strong> 
In the future the Oozie engine will provide a very powerful way to combine traditional
NGS analysis tools with tools designed to run in a Hadoop environment so much of
our current development focuses on this new engine.</p>


## The Theory Beind a SeqWare Workflow Bundle

In many workflow environments the concept of a workflow is encoded as a simple
XML markup file that defines a series of steps, data inputs, etc. This may be
interpreted by a user interface of some sort, e.g. a drag-n-drop workflow
creation tool. These workflow systems tend to treat workflows as very
light-weigh representations of steps.  One problem with this lightweight
approach is depdendenices for steps in the workflow, such as genome indexes for
an aligner, are often times treated as parameters and are not managed by the
workflow system.  SeqWare's concept of a workflow is much more akin to a Linux
distribution package (like RPM or DEB files) in which all necessary components
are packaged inside a single binary file. In SeqWare we use Zip64 files to
group the workflow definition file, workflow itself, sample settings, and data
dependencies in a single file that can be exchanged between SeqWare users or
archived. This allows SeqWare bundles to be much more portable that lightweigh
workflows that reference external tools and data. Being self-contained is at
the core of the design goals for SeqWare bundles with the expense of often
times large workflow bundle sizes.


## First Steps

Please launch your local VM in VirtualBox and login as user <kbd>seqware</kbd>,
password <kbd>seqware</kbd> at this time. Click on the "SeqWare Directory" link
on the desktop which will open a terminal to the location where we installed
the SeqWare tools. 


## Overview of Workflow Development Using the VM

You should be in the /home/seqware/SeqWare directory now, this is the working
directory.  Notice there is a jar file here and also two important directories:
provisioned-bundles (SW_BUNDLE_DIR in the config) which contains unzipped
workflow bundles and is where you will work on new bundles and released-bundles
(SW_BUNDLE_REPO_DIR in the config) which contains zip versions of the workflows
that you create when you package up these bundles and install them.

There are two ways to create a new workflow, the first is simply to copy an
existing workflow bundle from provisioned-bundles, rename it, and modify the
workflow to meet your needs. The second is using [Maven
Archetypes](http://maven.apache.org/guides/introduction/introduction-to-archetypes.html),
a template system which generates a workflow skeleton making it fast and easy
to get started with workflow development. In this tutorial we will use the
Maven Archetypes system here since it is fast and easy. We will also test the
new bundle and, once you finish, we will package it up, ready for handoff to an
admin that will install it in the SeqWare system so users can run it.

### Common Steps

Most workflow developers follow a similar series of steps when developing new workflows. Generally one goes through the following process:

* Plan your workflow
: Most developers are bioinformaticists and will spend some time exploring the tools and algorithms they want to use in this workflow and decide what problems their workflow is trying to solve.
* Find your tools and sample data
: Tools are collected, synthetic or real test datasets are prepared, prototyping is done
* Make and test the workflow
: The developer writes and test the workflow both with bundled test data and real data locally and on a cluster or cloud resource
* Packaging and handoff
: The developer zips up the finished workflow and hands off to an admin that installs the workflow so users can use it


## Creating a New Workflow

The SeqWare workflow archetype allows workflow developers to quickly create new
workflows. The Maven archetypes generate skeletons that contain a simple
example program that can be modified to create a new workflow. The archetypes
also take parameters prior to skeleton generation that allow the name of the
workflow to be specified and all configuration files to adjusted with respect
to these parameters.

The code generated by the archetype contains all the necessary files to
generate a complete workflow bundle when the <tt>mvn install</tt> command is
issued. This command will combine the workflow definition, the config file, the Java workflow file, and
other external dependencies pulled in by Maven to create a complete
workflow bundle. This makes the code  generated by the archetype ideal to place
under version control. As maintenance changes are made to the Java file or any
other aspect of the workflow, these files can be updated and a new workflow
reflecting these changes can be generated by re-issuing the  <tt>mvn
install</tt> command.

The first step to get started is to generate your workflow skeleton using Maven
archetypes. You will want to do this in a directory without pom.xml files (i.e.
outside of the SeqWare development directories).  Here we are working in the provisioned-bundles directory:
	
	cd /home/seqware/SeqWare
	cd provisioned-bundles 
	mvn archetype:generate
	...
	720: local -> com.github.seqware:seqware-archetype-decider (SeqWare Java Decider archetype)
	721: local -> com.github.seqware:seqware-archetype-java-workflow (SeqWare Java workflow archetype)
	722: local -> com.github.seqware:seqware-archetype-simplified-ftl-workflow (SeqWare FTL workflow archetype)
	723: local -> com.github.seqware:seqware-archetype-module (SeqWare module archetype)
	724: local -> com.github.seqware:seqware-archetype-legacy-ftl-workflow (SeqWare workflow legacy ftl archetype)
	725: local -> com.github.seqware:seqware-archetype-simple-legacy-ftl-workflow (A very simple SeqWare legacy ftl workflow archetype)
	# select 721 above, the "SeqWare Java workflow archetype"
	# use HelloWorld as the name of your workflow and use the default workflow version
	cd HelloWorld
	mvn install
	cd target/Workflow_Bundle_HelloWorld_1.0-SNAPSHOT_SeqWare_0.13.6.5/

The numbers used to identify  the archetypes (720 through 725) will vary
depending on what you have installed, so you will need to scan through the list
to find the SeqWare archetype you are looking for, in this case "SeqWare Java workflow archetype".

In this example, one would chose 721, the template for workflow using Java
objects.  Then use "HelloWorld" as the artifactId.  You may wish to change the
version number for your workflow but the defaults for the remaining questions
should be fine.


## A Tour of Workflow Bundle Components

In this section we will examine the internals of the Workflow Bundle that was just generated.
The first thing you should do is take a look at the workflow mainifest showing which workflows
are present in this bundle (a single Workflow Bundle can contain many workflows).
Notice in the command below that we use the SeqWare jar from **inside** the workflow bundle. This
ensures we are using the version of SeqWare this bundle was built with which minimize incompatibility issues.

<pre>
java -jar Workflow_Bundle_HelloWorld/1.0-SNAPSHOT/lib/seqware-distribution-0.13.6.5-full.jar  -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- -l -b `pwd`

Running Plugin: net.sourceforge.seqware.pipeline.plugins.BundleManager
Setting Up Plugin: net.sourceforge.seqware.pipeline.plugins.BundleManager@630045eb

List Workflows:

 Workflow:
  Name : HelloWorld
  Version : 1.0-SNAPSHOT
  Description : Add a description of the workflow here.
  Test Command: java -jar ${workflow_bundle_dir}/Workflow_Bundle_HelloWorld/1.0-SNAPSHOT/lib/seqware-distribution-0.13.6.5-full.jar --plugin net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --no-metadata --provisioned-bundle-dir ${workflow_bundle_dir} --workflow HelloWorld --version 1.0-SNAPSHOT --ini-files ${workflow_bundle_dir}/Workflow_Bundle_HelloWorld/1.0-SNAPSHOT/config/workflow.ini
  Template Path:
  Config Path:${workflow_bundle_dir}/Workflow_Bundle_HelloWorld/1.0-SNAPSHOT/config/workflow.ini
  Requirements Compute: single Memory: 20M Network: local
</pre>

This shows one workflow in the generated workflow bundle.

### Directory Organization

The directory structure created by the maven archetype includes a
<tt>pom.xml</tt> file which is our Maven build file, a <tt>src</tt> directory
which contains the Java workflow, and a workflow directory that contains any
bundled data, the basic workflow config file which includes all the paramters
this workflow accepts, the metadata.xml which defines the workflows available
in this bundle, and any scripts, binaries, or libraries your workflow needs (in
bin and lib respectively).

When you issue the <tt>mvn install</tt> command the target direct is created
which contains the compiled workflow along with the various necessary files all
correctly assembled in the proper directory structure.  You can change
directory to the workflow target directory (in this case
<tt>target/Workflow_Bundle_HelloWorld_1.0-SNAPSHOT_SeqWare_0.13.6.5</tt> and
run the workflow in test mode or package up the workflow as a zip file for
exchange with others. Both topics are covered later in this tutorial.

The Maven archetype workflows are quite nice, too, since it is easy to check in
everything but the target directory into source control like git or subversion.
This makes it a lot easier to share the development of workflows between
developers.

<pre>
|-- pom.xml
|-- src
|   `-- main
|       `-- java
|           `-- com
|               `-- github
|                   `-- seqware
|                       `-- WorkflowClient.java
|-- target
|   `-- Workflow_Bundle_HelloWorld_1.0-SNAPSHOT_SeqWare_0.13.6.5
|       `-- Workflow_Bundle_HelloWorld
|           `-- 1.0-SNAPSHOT
|               |-- bin
|               |-- classes
|               |   `-- com
|               |       `-- github
|               |           `-- seqware
|               |               `-- WorkflowClient.class
|               |-- config
|               |   `-- workflow.ini
|               |-- data
|               |   `-- input.txt
|               |-- lib
|               |   `-- seqware-distribution-0.13.6.5-full.jar
|               `-- metadata.xml
|-- workflow
|   |-- config
|   |   `-- workflow.ini
|   |-- data
|   |   `-- input.txt
|   |-- lib
|   |-- metadata.xml
|   `-- workflows
`-- workflow.properties
</pre>

Here are some additional details about these files:

* pom.xml
: A maven project file. Edit this to change the version of the workflow and to add or modify workflow dependencies such as program, modules and data.
* workflow
: This directory contains the workflow skeleton. Look in here to modify the workflow .ini, .java files (Java workflow) or workflow.ftl(FTL workflow). The examples of Java and FTL can be found <a href="/docs/15-workflow-examples/">here</a>.
* src 
: This directory contains the Java client. Look in here to modify the .java files (Java workflow). The examples of Java and FTL can be found <a href="/docs/15-workflow-examples/">here</a>.
* workflow.properties
: You can edit the description and workflow names in this file.

### Workflow Manifest

The workflow manifest (<tt>metadata.xml</tt>) includes the workflow name,
version, description, test command, and enough information so that the SeqWare
tools can test, execute, and install the workflow. Here is an example from the
HelloWorld workflow:

<pre><code>#!xml
&lt;bundle version=&quot;1.0-SNAPSHOT&quot;&gt;
  &lt;workflow name=&quot;HelloWorld&quot; version=&quot;1.0-SNAPSHOT&quot; seqware_version=&quot;0.13.6.5&quot;
  basedir=&quot;${workflow_bundle_dir}/Workflow_Bundle_HelloWorld/1.0-SNAPSHOT&quot;&gt;
    &lt;description&gt;Add a description of the workflow here.&lt;/description&gt;
    &lt;test command=&quot;java -jar ${workflow_bundle_dir}/Workflow_Bundle_HelloWorld/1.0-SNAPSHOT/lib/seqware-distribution-0.13.6.5-full.jar --plugin net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --no-metadata --provisioned-bundle-dir ${workflow_bundle_dir} --workflow HelloWorld --version 1.0-SNAPSHOT --ini-files ${workflow_bundle_dir}/Workflow_Bundle_HelloWorld/1.0-SNAPSHOT/config/workflow.ini &quot;/&gt;
    &lt;workflow_command command=&quot;java -jar ${workflow_bundle_dir}/Workflow_Bundle_HelloWorld/1.0-SNAPSHOT/lib/seqware-distribution-0.13.6.5-full.jar --plugin net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --bundle ${workflow_bundle_dir} --workflow HelloWorld --version 1.0-SNAPSHOT &quot;/&gt;
    &lt;workflow_template path=&quot;&quot;/&gt;
    &lt;workflow_class path=&quot;${workflow_bundle_dir}/Workflow_Bundle_HelloWorld/1.0-SNAPSHOT/classes/com/github/seqware/WorkflowClient.java&quot;/&gt;
    &lt;config path=&quot;${workflow_bundle_dir}/Workflow_Bundle_HelloWorld/1.0-SNAPSHOT/config/workflow.ini&quot;/&gt;
    &lt;build command=&quot;ant -f ${workflow_bundle_dir}/Workflow_Bundle_HelloWorld/1.0-SNAPSHOT/build.xml&quot;/&gt;
    &lt;requirements compute=&quot;single&quot; memory=&quot;20M&quot; network=&quot;local&quot;  workflow_engine=&quot;Pegasus,Oozie&quot; workflow_type=&quot;java&quot;/&gt;
  &lt;/workflow&gt;
&lt;/bundle&gt;
</code></pre>

As mentioned above, you can edit the description and workflow name in the workflow.properties file.

### Workflow Java Class

You can see the full Java workflow source code by looking at [Workflow
Examples](/docs/15-workflow-examples/) or, in this case, just the
<tt>WorkflowClient.java</tt> file produced by the Maven Archetype above.

This Java class is pretty simple in its construction. It is used to define
input and output files along with the individual steps in the workflow and how
they relate to each other.  It is used to create a workflow object model which
is then handed of to a workflow engine that knows how to turn that into a
directed acyclic graph of jobs that can run on a cluster (local VM, an HPC
cluster, a cloud-based cluster, etc).

#### Files

<pre>
<code>#!java
    @Override
    public Map<String, SqwFile> setupFiles() {
        SqwFile file0 = this.createFile("file_in_0");
        file0.setSourcePath(this.getWorkflowBaseDir()+"/data/input.txt");
        file0.setType("text/plain");
        file0.setIsInput(true);

        SqwFile file1 = this.createFile("file_out");
        file1.setSourcePath("dir1/output");
        file1.setType("text/plain");
        file1.setIsOutput(true);
        file1.setForceCopy(true);

        return this.getFiles();
    }
</code>
</pre>

This method sets up files that are inputs and/or outputs for this workflow.  In
this example the input <tt>data/input.txt</tt> comes from the workflow bundle
itself. The ultimate location of the output file is determined by two
parameters passed into the WorkflowLaucher which actually runs the workflow:
<tt>--metadata-output-file-prefix</tt> and <tt>--metadata-output-dir</tt>.

#### Directories

<pre>
<code>#!java
    @Override
    public void setupDirectory() {
        this.addDirectory("dir1");
    }
</code>
</pre>

This method sets up directories in the working directory that the workflow run in. In this case the workflow creates a directory called "dir1".

#### Workflow Steps

<pre>
<code>#!java
    @Override
    public void buildWorkflow() {
        Job job00 = this.getWorkflow().createBashJob("bash_mkdir");
        job00.getCommand().addArgument("mkdir test1");

        Job job10 = this.getWorkflow().createBashJob("bash_cp");
        job10.setCommand("cp " + this.getFiles().get("file_in_0").getProvisionedPath() + " test1");
        job10.addParent(job00);

        Job job11 = this.getWorkflow().createBashJob("bash_cp");
        job11.setCommand("cp " + this.getFiles().get("file_in_0").getProvisionedPath() + " dir1/output");
        job11.addParent(job00);


    }
</code>
</pre>

In this buildWorkflow() method three jobs are created.  You can see that the
<tt>createBashJob</tt> can be used to run any arbitrary command. In the future
we will add more job types (such as Map/Reduce for the Oozie engine).  Each
child job is linked to its parent using the <tt>addParent</tt> method.  This
information is enough to correctly schedule these jobs and run them in the
correct order locally on the VM, on an HPC cluster, or on the cloud.  The more
detailed Pipeline documentation will cover optional useful job methods
including examples of how to control memory requirements for particular jobs.

<p class="warning"><strong>Tip:</strong>
It can be confusing at first but there are two directories to think about when
working with workflows.  The first is the ${workflow_bundle_dir} which is the
location where the workflow bundle has been unzipped. This variable can be used
both in the Java object (via the getWorkflowBaseDir() method) and the various
config and metadata files (via ${workflow_bundle_dir}). You use this to access
the location of data and other file types that you have included in the
workflow bundle.  The second directory is the current working directory that
your workflow steps will be executed in. This is a directory created at runtime
by the underlying workflow engine and is shared for all steps in your workflow.
You can use this as your temporary directory to process intermediate files.
</p>


### Configuration File

<pre>
<code>
# key=input_file:type=file:display=F:file_meta_type=text/plain
input_file=${workflow_bundle_dir}/Workflow_Bundle_${workflow-directory-name}/${version}/data/input.txt
# key=greeting:type=text:display=T:display_name=Greeting
greeting=Testing
# this is just a comment, the output directory is a conventions and used in many workflows to specify a relative output path
output_dir=seqware-results
# the output_prefix is a convension and used to specify the root of the absolute output path or an S3 bucket name 
# you should pick a path that is available on all custer nodes and can be written by your user
output_prefix=./
</code>
</pre>

This is the workflow.ini file and contains 

You access these variables in the Java workflow using the <tt>getProperty()</tt> method. When installing the workflow the ini file is parsed and extra metadata about each parameter is examined. This gives the system information about the type of the variable (integer, string, etc) and any default values.

The ini file(s) follow the general pattern of:

<pre>
# comment/specification
key=value
</pre>

To achieve this overloaded role for ini files you need to include hints to ensure the BundleManager that installs workflow bundles has enough information. Here is what the annotation syntax looks like:


	# key=<name>:type=[integer|float|text|pulldown|file]:display=[T|F][:display_name=<name_to_display>][:file_meta_type=<mime_meta_type>][:pulldown_items=<key1>|<value1>;<key2>|<value2>]
	key=default_value

The file_meta_type is only used for type=file. 

The pulldown type means that the pulldown_items should be defined as well. This looks like:

	pulldown_items=<key1>|<value1>;<key2>|<value2>

The default value for this will refer to either value1 or value2 above.
If you fail to include a metadata line for a particular key/value then it's assumed to be:

	key=<name>:type=text:display=F

This is convenient since many of the values in an INI file should not be displayed to the end user.


### Common Variables

There are several variables that you will see in various files, including the config ini file and <tt>metadata.xml</tt> file.

* ${date}: a string representing the date the DAX was created, this is always defined so consider this a reserved variable name. 

* ${random}: a randomly generated string, this is always defined so consider this a reserved variable name. 

* ${workflow_bundle_dir}: if this workflow is part of a workflow bundle this variable will be defined and points to the path of the root of the directory this workflow bundle has been expanded to.

* ${workflow_base_dir}: ${workflow_bundle_dir}/Workflow_Bundle_{workflow_name}/{workflow_version}. This is really used in a ton of places since we need a variable that points to the install location for the bundle since we cannot hard code this.



## Modifying the Workflow

At this point, one would normally want to edit the workflow by modifying the WorkflowClient.java file as is appropriate for the workflow.
In the example below I just added an extra job that does a simple shell operation (job12).

<pre>
<code>#!java
    @Override
    public void buildWorkflow() {
        Job job00 = this.getWorkflow().createBashJob("bash_mkdir");
        job00.getCommand().addArgument("mkdir test1");

        Job job10 = this.getWorkflow().createBashJob("bash_cp");
        job10.setCommand("cp " + this.getFiles().get("file_in_0").getProvisionedPath() + " test1");
        job10.addParent(job00);

        Job job11 = this.getWorkflow().createBashJob("bash_cp");
        job11.setCommand("cp " + this.getFiles().get("file_in_0").getProvisionedPath() + " dir1/output");
        job11.addParent(job00);

        Job job12 = this.getWorkflow().createBashJob("bash_date");
        job12.setCommand("date > dir1/time");
        job12.addParent(job11);
    }
</code>
</pre>



## Building the Workflow

If you made changes to the workflow files now would be a good time to to use
"mvn install" to refresh the workflow bundle in the target directory. For
example:

        [seqware@seqwarevm maven-bundles]$ cd helloworld/
        [seqware@seqwarevm helloworld]$ mvn install
        [INFO] Scanning for projects...                                                                                       
        [INFO] ------------------------------------------------------------------------                                       
        [INFO] Building seqware-workflow                                                                                      
        [INFO]    task-segment: [install]                                                                                     
        [INFO] ------------------------------------------------------------------------                                       
        [INFO] [properties:read-project-properties {execution: properties-maven-plugin-execution}]                            
        [debug] execute contextualize                                                                                         
        [INFO] [resources:copy-resources {execution: copy-resources}]                                                         
        [INFO] Using 'UTF-8' encoding to copy filtered resources.                                                             
        [INFO] Copying 3 resources                                                                                            
        [debug] execute contextualize                                                                                         
        [INFO] [resources:resources {execution: default-resources}]                                                           
        [INFO] Using 'UTF-8' encoding to copy filtered resources.    
        ...
        main:
        [INFO] Executed tasks
        [INFO] [antrun:run {execution: chmod-perl}]
        [INFO] Executing tasks

        main:
        [INFO] Executed tasks
        [INFO] ------------------------------------------------------------------------
        [INFO] BUILD SUCCESSFUL
        [INFO] ------------------------------------------------------------------------
        [INFO] Total time: 26 seconds
        [INFO] Finished at: Fri Nov 23 14:48:15 EST 2012
        [INFO] Final Memory: 67M/423M
        [INFO] ------------------------------------------------------------------------


The next step is to look at examples of workflows at [Workflow Examples](/docs/15-workflow-examples/).



## Testing the Workflow 

SeqWare bundles have a test command built into their metadata.xml. In order to trigger this, run with the following command. Note that the workflow name and version need to match the name and version given when the workflow is listed above. 

	java -jar ~/seqware-full.jar -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- -b `pwd` -t --workflow simple-legacy-ftl-workflow --version 1.0

	Running Plugin: net.sourceforge.seqware.pipeline.plugins.BundleManager
	Setting Up Plugin: net.sourceforge.seqware.pipeline.plugins.BundleManager@e80d1ff
	Testing Bundle
	  Running Test Command:
	java -jar /home/seqware/Temp/simple-legacy-ftl-workflow/target/Workflow_Bundle_simple-legacy-ftl-workflow_1.0-SNAPSHOT_SeqWare_0.13.3/Workflow_Bundle_simple-legacy-ftl-workflow/1.0-SNAPSHOT/lib/seqware-distribution-0.13.3-full.jar --plugin net.sourceforge.seqware.pipeline.plugins.Workflow-provisioned-bundle-dir /home/seqware/Temp/simple-legacy-ftl-workflow/target/Workflow_Bundle_simple-legacy-ftl-workflow_1.0-SNAPSHOT_SeqWare_0.13.3 --workflow simple-legacy-ftl-workflow --version 1.0 --ini-files /home/seqware/Temp/simple-legacy-ftl-workflow/target/Workflow_Bundle_simple-lSHOT_SeqWare_0.13.3/Workflow_Bundle_simple-legacy-ftl-workflow/1.0-SNAPSHOT/config/workflow.ini
	MONITORING PEGASUS STATUS:
	RUNNING: step 1 of 5 (20%)
	RUNNING: step 2 of 5 (40%)
	...

## Packaging the Workflow into a Workflow Bundle

Assuming the workflow above worked fine the next step is to package it.

	[seqware@seqwarevm Workflow_Bundle_SampleJavaWorkflow_1.0-SNAPSHOT_SeqWare_0.13.6.3]$ mkdir packaged
	[seqware@seqwarevm Workflow_Bundle_SampleJavaWorkflow_1.0-SNAPSHOT_SeqWare_0.13.6.3]$  java -jar ~/Development/gitroot/seqware-github/seqware-distribution/target/seqware-distribution-0.13.6.5-full.jar -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- --b packaged -p `pwd`
	Running Plugin: net.sourceforge.seqware.pipeline.plugins.BundleManager
	Setting Up Plugin: net.sourceforge.seqware.pipeline.plugins.BundleManager@20b9b538
	Packaging Bundle
	Bundle: packaged path: /tmp/testing/SampleJavaWorkflow/target/Workflow_Bundle_SampleJavaWorkflow_1.0-SNAPSHOT_SeqWare_0.13.6.3
	Bundle Has Been Packaged to packaged!

	cd target
	mkdir output
	java -jar ~/seqware-full.jar -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- -b output -p Workflow_Bundle_helloWorld_1.0-SNAPSHOT_SeqWare_0.13.6.5

What happens here is the <code>Workflow_Bundle_hello_1.0-SNAPSHOT_SeqWare_0.13.3</code> directory is zip'd up to your output directory and that can be provided to an admin for install.


## Next Steps

The next step is the [Admin Tutorial](/docs/3-getting-started/admin-tutorial/) which will show you how to install the workflow created above so other users can call it.
