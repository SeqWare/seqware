---

title:                 "Java Workflows"
markdown:              advanced
is_dynamic:            true
toc_includes_sections: true

---

## Overview

<p class="warning"><strong>Tip:</strong> The Java workflow language is 
our recommended workflow language for new development.</p>

This document really focuses on the format of the Java workflow langauge. For
more information about the entire workflow bundle please see the [Developer
Tutorial](/docs/3-getting-started/developer-tutorial/).  You should read this
guide before this page.

## Limitations

The Java workflows work with both the Pegasus and Oozie Workflow Engines. 
That being said, if you use MapReduce or other Hadoop-specific job types in your Java
workflows they will not function in the Pegasus Workflow Engine (since
Hadoop is only present for the Oozie Workflow Engine).

## Note About Working Dir vs. Workflow Bundle Dir

TODO

## Creating a Java Workflow Bundle

In the [Developer Tutorial](/docs/3-getting-started/developer-tutorial/) you
saw how to create a HelloWorld Java workflow using archetype.

<%= render '/includes/java_archetype/' %>

Enter the workflow name and version you want to use.  When complete, you can
<tt>cd</tt> into the new workflow directory and use <tt>mvn install</tt> to
build the workflow. This copies files to the correct location and pulls in
needed dependencies.

<%= render '/includes/maven_workflow_build/' %>

You will now have a workflow directory called <tt>target/Workflow_Bundle_*</tt>
which contains your assembled workflow.

## A Tour of the Java Workflow Syntax

<%= render '/includes/java_workflows/java_workflow/' %>

The full contents of the <tt>WorkflowClient.java</tt> are included below, we will describe each section in more detail next:

<%= render '/includes/java_workflows/java_workflow_full/' %>

### Variables

Variables are simply defined in <tt>&lt;#assign&gt;</tt> blocks. To access variables from the workflow's ini file simply use the syntax <tt>${variable_name}</tt>.

<%= render '/includes/java_workflows/java_workflow_vars/' %>

### Files & Directories

Files that are inputs or outputs from workflows need to be manually copied in or out respectively.  This uses the ProvisionFiles module that knows how to move around local files or remote files on Amazon's S3.

<%= render '/includes/java_workflows/java_workflow_files/' %>

You can also specify directories to be created in the working directory of your workflow.

<%= render '/includes/java_workflows/java_workflow_dirs/' %>

### Jobs & Dependencies

The jobs need to have distinct IDs and you can generate these using a for loop in FTL if need be. You can put any command in the <tt><argument></tt> section but mostly this is used to call GenericCommandRunner which runs the command provided in a Bash shell. Notice the use of <tt><profile></tt> to specify the job thread and memory count.

<%= render '/includes/java_workflows/java_workflow_jobs/' %>

The dependencies section links together all the individual jobs in the correct order so they can be executed successfully. Parent/child relationships are used to specify job pre-requisits.

TODO: discuss the JobTypes, namely Bash

## Running the Workflow

You can run the Workflow using the test process shown in the [Developer Tutorial](/docs/3-getting-started/developer-tutorial/).  For example:

<pre><code>#!bash
java -jar ~/seqware-full.jar -p net.sourceforge.seqware.pipeline.plugins.BundleManager -- -b `pwd` -t --workflow simple-legacy-ftl-workflow --version 1.0
Running Plugin: net.sourceforge.seqware.pipeline.plugins.BundleManager
Setting Up Plugin: net.sourceforge.seqware.pipeline.plugins.BundleManager@e80d1ff
Testing Bundle
  Running Test Command:
java -jar /home/seqware/Temp/simple-legacy-ftl-workflow/target/Workflow_Bundle_simple-legacy-ftl-workflow_1.0-SNAPSHOT_SeqWare_0.13.6.x/Workflow_Bundle_simple-legacy-ftl-workflow/1.0-SNAPSHOT/lib/seqware-distribution-0.13.6.x-full.jar --plugin net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher --provisioned-bundle-dir /home/seqware/Temp/simple-legacy-ftl-workflow/target/Workflow_Bundle_simple-legacy-ftl-workflow_1.0-SNAPSHOT_SeqWare_0.13.6.x --workflow simple-legacy-ftl-workflow --version 1.0 --ini-files /home/seqware/Temp/simple-legacy-ftl-workflow/target/Workflow_Bundle_simple-lSHOT_SeqWare_0.13.6.x/Workflow_Bundle_simple-legacy-ftl-workflow/1.0-SNAPSHOT/config/workflow.ini
MONITORING PEGASUS STATUS:
RUNNING: step 1 of 5 (20%)
RUNNING: step 2 of 5 (40%)
...
</code></pre>

## For More Information

See the  [Developer Tutorial](/docs/3-getting-started/developer-tutorial/) and the [Java Workflow](/docs/6-pipeline/java-workflows/) documents for more information. For FTL syntax questions consult the [FreeMarker website](http://freemarker.sourceforge.net/).
