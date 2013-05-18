---

title:                 "Developer Tutorial"
markdown:              advanced
is_dynamic:            true
toc_includes_sections: true

---

<!-- TODO 
* really, should be a tutorial for MyHelloWorld so it doesn't conflict with the workflow already installed
* the adding new job step below should show how to call a user-created script! A one-step workflow!
-->

This guide picks up where the [User
Tutorial](/docs/3-getting-started/user-tutorial/) left off. In that previous
guide we showed you how to start up your local VM, create studies, experiments,
and samples, associate an input file with a sample, and then launch a workflow
to process that file.  These workflows can be complex (they include branching
and looping) and in future tutorials you will see how to string multiple
workflows together (output of one as input for the next) using
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

Workflows in the SeqWare Pipeline system can be written in one of three
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

**In this tutorial we will write a workflow using the Java Workflow Language and run it on the Pegasus Workflow Engine.  We will also show you a preview of the Oozie engine for those of you using a SeqWare 1.0.x virtual machine.**

| Workflow Language | Language Production Ready | Oozie Engine | Pegasus Engine |
| ------ | ------ | ------ | ------ | 
| FTL    | Y      | N      | Y      |
| Simple Markup    | N      | Y      | Y      |
| Java   | Y      | Y      | **Y**      |


<p class="warning"><strong>Tip:</strong> 
In the 1.0.x release the Oozie engine will provide a very powerful way to combine traditional
NGS analysis tools with tools designed to run in a Hadoop environment so much of
our current development focuses on this new engine. If you use the development
AMI you can try the 1.0.x release and Oozie engine early.</p>


## The Theory Behind a SeqWare Workflow Bundle

In many workflow environments the concept of a workflow is encoded as a simple
XML markup file that defines a series of steps, data inputs, etc. This may be
interpreted by a user interface of some sort, e.g. a drag-n-drop workflow
creation tool. These workflow systems tend to treat workflows as very
light-weigh representations of steps.  One problem with this lightweight
approach is dependencies for steps in the workflow, such as genome indexes for
an aligner, are often times treated as parameters and are not managed by the
workflow system.  SeqWare's concept of a workflow is much more akin to a Linux
distribution package (like RPM or DEB files) in which all necessary components
are packaged inside a single binary file. In SeqWare we use Zip64 files to
group the workflow definition file, workflow itself, sample settings, and data
dependencies in a single file that can be exchanged between SeqWare users or
archived. This allows SeqWare bundles to be much more portable that lightweight
workflows that reference external tools and data. Being self-contained is at
the core of the design goals for SeqWare bundles with the expense of often
times large workflow bundle sizes.

## Note About Working Directory vs. Workflow Bundle Directory

Just to be clear, there are two directory locations to consider when working
with workflows.  The workflow bundle directory (often referred to as
<tt>${workflow_bundle_dir}</tt> in various components) refers to the location
of the installed workflow bundle. You use this variable throughout your
workflow bundle to refer to the install directory since that will only be known
after the workflow bundle is installed.  For example, in the Java workflow
language you would refer to a script called <tt>foo.pl</tt> installed in the
<tt>bin</tt> directory within the workflow bundle as
<tt>this.getWorkflowBaseDir()+"/bin/foo.pl"</tt>.  Similarly, you can refer to
a data file in the workflow INI config file as
<tt>${workflow_bundle_dir}/data/data_file.txt</tt>.

The second directory is the current working directory. Every time a workflow is
launched, a temporary working directory is created for just that particular run
of the workflow.  Regardless of the workflow engine (Pegasus or Oozie) a shared
filesystem (NFS, gluster, etc) is required to ensure each job in a workflow is
able to access this shared workflow working location regardless of what cluster
node is selected to run a particular job.  Before a job in a workflow executes
the current working directory is set so workflow authors can assume their
individual jobs are already in the correct location.

## First Steps

<%= render '/includes/launch_vm/' %>

## Overview of Workflow Development Using the VM

You should be in the /home/seqware/ directory now, this is the working
directory.  Notice there is a jar file here and also two important directories:
provisioned-bundles (SW_BUNDLE_DIR in the config) which contains unzipped
workflow bundles and released-bundles
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
: The developer writes and tests the workflow both with bundled test data and real data locally and on a cluster or cloud resource
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

<%= render '/includes/java_archetype/' %>

## A Tour of Workflow Bundle Components

In this section we will examine the internals of the Workflow Bundle that was just generated.
The first thing you should do is take a look at the workflow manifest showing which workflows
are present in this bundle (a single Workflow Bundle can contain many workflows).
Notice in the command below that we use the SeqWare jar from **inside** the workflow bundle. This
ensures we are using the version of SeqWare this bundle was built with which minimize incompatibility issues.

<pre>
cd /home/seqware/workflow-dev/MyHelloWorld/target/Workflow_Bundle_MyHelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>/

java -jar Workflow_Bundle_MyHelloWorld/1.0-SNAPSHOT/lib/seqware-distribution-<%= seqware_release_version %>-full.jar  -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- -l -b `pwd`

Running Plugin: net.sourceforge.seqware.pipeline.plugins.BundleManager
Setting Up Plugin: net.sourceforge.seqware.pipeline.plugins.BundleManager@630045eb

List Workflows:

 Workflow:
  Name : MyHelloWorld
  Version : 1.0-SNAPSHOT
  Description : Add a description of the workflow here.
  Test Command: java -jar ${workflow_bundle_dir}/Workflow_Bundle_MyHelloWorld/1.0-SNAPSHOT/lib/seqware-distribution-<%= seqware_release_version %>-full.jar --plugin net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --no-metadata --provisioned-bundle-dir ${workflow_bundle_dir} --workflow MyHelloWorld --version 1.0-SNAPSHOT --ini-files ${workflow_bundle_dir}/Workflow_Bundle_MyHelloWorld/1.0-SNAPSHOT/config/workflow.ini
  Template Path:
  Config Path:${workflow_bundle_dir}/Workflow_Bundle_MyHelloWorld/1.0-SNAPSHOT/config/workflow.ini
  Requirements Compute: single Memory: 20M Network: local
</pre>

This shows one workflow in the generated workflow bundle.

### Directory Organization

<%= render '/includes/workflow_org/' %>

### Workflow Manifest

<%= render '/includes/workflow_man/' %>

### Workflow Java Class

<%= render '/includes/java_workflows/java_workflow/' %>

#### Files

<%= render '/includes/java_workflows/java_workflow_files/' %>

This method sets up files that are inputs and/or outputs for this workflow.  In
this example the input <tt>data/input.txt</tt> comes from the workflow bundle
itself. The ultimate location of the output file is determined by two
parameters passed into the WorkflowLauncher which actually runs the workflow:
<tt>--metadata-output-file-prefix</tt> (our <tt>output_prefix</tt> in the ini
file) and <tt>--metadata-output-dir</tt> (or <tt>output_dir</tt> in the ini
file). Alternatively, you can actually override the output location for a file
as is the case with the above "output_file".  When this parameter is available
in the ini file the automatic location of the output file
("output_prefix"+/+"output_dir"+/+"output") is overridden for the value of
"output_file".

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

<%= render '/includes/workflow_conf/' %>

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
"mvn clean install" to refresh the workflow bundle in the target directory. For
example:

<%= render '/includes/maven_workflow_build/' %>

## Testing the Workflow 

The next step after authoring your workflows in the Java workflow language is to run them in a test mode.

### Running with the Pegasus Workflow Engine

This is the default backend and the only workflow engine we recommend for SeqWare 0.13.6.x.

SeqWare bundles have a test command built into their metadata.xml. In order to trigger this, run with the following command. Note that the workflow name and version need to match the name and version given when the workflow is listed above. 

	cd /home/seqware/workflow-dev/MyHelloWorld
	[seqware@master MyHelloWorld]$ mvn clean install -DskipITs=false
	<snipped>
	[INFO] [exec:exec {execution: recursive_executable}]
	[INFO] [exec:exec {execution: test_bundle}]
	Running Plugin: net.sourceforge.seqware.pipeline.plugins.BundleManager
	Setting Up Plugin: net.sourceforge.seqware.pipeline.plugins.BundleManager@4229ab3e
	Testing Bundle
	  Running Test Command:
	java -jar /mnt/databases/workflow-dev/MyMyHelloWorld/target/Workflow_Bundle_MyMyHelloWorld_1.0-SNAPSHOT_SeqWare_0.13.6.5/Workflow_Bundle_MyMyHelloWorld/1.0-SNAPSHOT/lib/seqware-distribution-0.13.6.5-full.jar --plugin net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --no-metadata --provisioned-bundle-dir /mnt/databases/workflow-dev/MyMyHelloWorld/target/Workflow_Bundle_MyHelloWorld_1.0-SNAPSHOT_SeqWare_0.13.6.5 --workflow MyHelloWorld --version 1.0-SNAPSHOT --ini-files /mnt/databases/workflow-dev/MyHelloWorld/target/Workflow_Bundle_MyHelloWorld_1.0-SNAPSHOT_SeqWare_0.13.6.5/Workflow_Bundle_MyHelloWorld/1.0-SNAPSHOT/config/workflow.ini 
	MONITORING PEGASUS STATUS:
	RUNNING: step 1 of 7 (14%)
	RUNNING: step 2 of 7 (28%)
	RUNNING: step 3 of 7 (42%)
	RUNNING: step 4 of 7 (57%)
	RUNNING: step 5 of 7 (71%)
	RUNNING: step 6 of 7 (85%)
	WORKFLOW COMPLETED SUCCESSFULLY!
	Bundle Passed Test!
	[INFO] [install:install {execution: default-install}]
	[INFO] Installing /mnt/databases/workflow-dev/MyHelloWorld/target/MyHelloWorld-1.0-SNAPSHOT.jar to /home/seqware/.m2/repository/com/github/seqware/MyHelloWorld/1.0-SNAPSHOT/MyHelloWorld-1.0-SNAPSHOT.jar
	[INFO] ------------------------------------------------------------------------
	[INFO] BUILD SUCCESSFUL
	[INFO] ------------------------------------------------------------------------
	[INFO] Total time: 5 minutes 39 seconds
	[INFO] Finished at: Wed Apr 24 08:58:38 PDT 2013
	[INFO] Final Memory: 72M/489M
	[INFO] ------------------------------------------------------------------------


Under the hood, this is just calling the BundleManager --test option. In other words, you can do the same thing by:

	cd /home/seqware/workflow-dev/MyHelloWorld/target/Workflow_Bundle_MyHelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>
	java -jar ~/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- -b `pwd` -t --workflow MyHelloWorld --version 1.0-SNAPSHOT
	Running Plugin: net.sourceforge.seqware.pipeline.plugins.BundleManager
	Setting Up Plugin: net.sourceforge.seqware.pipeline.plugins.BundleManager@2fb3f8f6
	Testing Bundle
	  Running Test Command:
	java -jar /home/seqware/workflow-dev/MyHelloWorld/target/Workflow_Bundle_MyHelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>/Workflow_Bundle_MyHelloWorld/1.0-SNAPSHOT/lib/seqware-distribution-<%= seqware_release_version %>-full.jar --plugin net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --no-metadata --provisioned-bundle-dir /home/seqware/workflow-dev/MyHelloWorld/target/Workflow_Bundle_MyHelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %> --workflow MyHelloWorld --version 1.0-SNAPSHOT --ini-files /home/seqware/workflow-dev/MyHelloWorld/target/Workflow_Bundle_MyHelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>/Workflow_Bundle_MyHelloWorld/1.0-SNAPSHOT/config/workflow.ini 
	MONITORING PEGASUS STATUS:
	RUNNING: step 1 of 8 (12%)
	RUNNING: step 2 of 8 (25%)
	RUNNING: step 3 of 8 (37%)
	RUNNING: step 4 of 8 (50%)
	RUNNING: step 5 of 8 (62%)
	RUNNING: step 6 of 8 (75%)
	RUNNING: step 7 of 8 (87%)
	WORKFLOW COMPLETED SUCCESSFULLY!
	Bundle Passed Test!

<p class="warning"><strong>Tip:</strong> 
Note in the testing command above it prints out the underlying command it calls using the <tt>WorkflowLauncher</tt> plugin. If all you want to do is to run a workflow with some settings without metadata writeback you could directly just call WorkflowLauncher as above. This bypasses the whole workflow scheduling and asynchronous launching process that you saw in the User Tutorial. What you lose is the metadata tracking functionality. The command runs the workflow which produces file outputs but that is all, no record of the run will be recorded in the MetaDB.
</p>

### Running with the Oozie Workflow Engine

For those of you using the 1.0.x (or newer) SeqWare release you can use
[Oozie](http://oozie.apache.org/) as your workflow engine instead of Pegasus.
Oozie provides a very robust and performant workflow execution environment and,
in the future, using this workflow engine will allow for mixed workflows that
include traditional scripts along with steps using MapReduce, Pig, Hive, and
other Hadoop-associated technologies. We are also working on job submitter
plugin for Oozie that will allow jobs to be managed and scheduled by Oozie but
run on a traditional Sun Grid Engine (SGE) cluster.  Even without these more
advanced features the Oozie workflow engine provides terrific performance,
stability, and monitoring tools.  Since SeqWare workflows are engine agnostic
you can submit Java-based workflows unmodified to either the Oozie or Pegasus
engines, no workflow modifications are required.

There are a few caveats for the Oozie workflow engine in SeqWare.  For example,
to run the workflow above you will need to do the following:

* Ensure your .seqware/settings file includes the correct parameters. If you are using our VM this will be true.
* Jobs are run by the 'mapred' user not the seqware user. So when you author and run workflows make sure the output destination can be written to by mapred. In the future we will eliminate this constraint.
* Workflows include bash jobs but in the future we will add other Hadoop-specific types (e.g. MapReduce). For now these are not implemented.
* The monitoring tools for Oozie are far better than for Pegasus, check out Hue, for example, running on port 8888 on the VM. You can monitor and debug workflows through this very nice web interface.
* This engine will only work on the 1.0.0 release (or preview release) of SeqWare or newer. The 0.13.6.x and earlier releases will only work with the Pegasus workflow engine.

In this example the same workflow as above is executed using the
WorkflowLauncher, the underlying tool called by BundleManager when in test
mode. We use this plugin directly here since BundleManager just triggers the
test, it does not expose the workflow engine option. The <tt>--workflow-engine
oozie</tt> parameter for WorkflowLauncher executes the workflow using Oozie to
Hadoop rather than Pegasus to Condor to Globus GRAM to SGE.

<pre>
<code>#!bash
cd /home/seqware/workflow-dev/MyHelloWorld/target/Workflow_Bundle_MyHelloWorld_1.0-SNAPSHOT_SeqWare_1.0.0-SNAPSHOT
java -jar Workflow_Bundle_MyHelloWorld/1.0-SNAPSHOT/lib/seqware-distribution-1.0.0-SNAPSHOT-full.jar --plugin net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --no-metadata --provisioned-bundle-dir `pwd` --workflow MyHelloWorld --version 1.0-SNAPSHOT --ini-files Workflow_Bundle_MyHelloWorld/1.0-SNAPSHOT/config/workflow.ini --workflow-engine oozie --wait
</code>
</pre>

This will cause the workflow to run and not exit until it finishes (the
<tt>--wait</tt> option).  You can also monitor the workflow using the Hue web
application installed at http://hostname:8888/oozie/. For our VMs the username
and password are "seqware". This is a great way to monitor and debug workflows,
you can very easily get to the logs for each step, for example.

The Oozie engine will continue to evolve over time and will become our default
workflow engine in the 1.x series.

## Packaging the Workflow into a Workflow Bundle

Assuming the workflow above worked fine the next step is to package it.

	[seqware@seqwarevm Workflow_Bundle_MyHelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>]$ mkdir ~/packaged-bundles
	[seqware@seqwarevm Workflow_Bundle_MyHelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>]$ java -jar ~/seqware-distribution-<%= seqware_release_version %>-full.jar -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- --b ~/packaged-bundles -p `pwd`
	Running Plugin: net.sourceforge.seqware.pipeline.plugins.BundleManager
	Setting Up Plugin: net.sourceforge.seqware.pipeline.plugins.BundleManager@20b9b538
	Packaging Bundle
	Bundle: packaged path: /home/seqware/workflow-dev/MyHelloWorld/target/Workflow_Bundle_MyHelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>
	Bundle Has Been Packaged to /home/seqware/packaged-bundles!

What happens here is the <code>Workflow_Bundle_hello_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %></code> directory is zip'd up to your output directory and that can be provided to an admin for install. In this VM you will find the bundled workflow written to the file /home/seqware/packaged-bundles/Workflow_Bundle_MyHelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>.zip

## Next Steps

The next step is the [Admin Tutorial](/docs/3-getting-started/admin-tutorial/) which will show you how to install the workflow created above so other users can call it.
