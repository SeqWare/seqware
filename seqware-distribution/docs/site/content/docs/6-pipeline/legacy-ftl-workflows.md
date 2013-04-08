---

title:                 "Legacy FTL Workflows"
markdown:              advanced
is_dynamic:            true
toc_includes_sections: true

---
<!-- TODO: 
* add more info on variables defined like random
-->

## Overview

<p class="warning"><strong>Tip:</strong> The Legacy FTL Workflow format is our
older, more verbose workflow language based on the DAX format from the Pegasus
project with embedded FreeMarker template code (for loops, if statements, etc).
Just to be completely clear, we highly recommend new workflow development use
the Java Workflow syntax instead. It is far more succinct and powerful. The
only disadvantage of the Java workflow language is the fact that it must be
compiled after editing. The FTL workflow language is interpreted at runtime so
no compilation is needed.</p>

This document really focuses on the format of the FLT workflow language. For
more information about the entire workflow bundle please see the [Developer
Tutorial](/docs/3-getting-started/developer-tutorial/).  You should read this
guide before this page.

## Limitations

The FTL workflows only work with the Pegasus Workflow Engine. It is extremely
unlikely that we will support others in the future since the FTL syntax is
semi-deprecated at this point.

## Creating an FTL Workflow Bundle

In the [Developer Tutorial](/docs/3-getting-started/developer-tutorial/) you
saw how to create a HelloWorld Java workflow using archetype. We can use the
same process to create an FTL workflow.  Use the following command and choose
the "SeqWare workflow legacy ftl archetype".

	mvn archetype:generate

Enter the workflow name and version you want to use.  When complete, you can
<tt>cd</tt> into the new workflow directory and use <tt>mvn install</tt> to
build the workflow. This copies files to the correct location and pulls in
needed dependencies.

	mvn install

You will now have a workflow directory called <tt>target/Workflow_Bundle_*</tt>
which contains your assembled workflow.

## A Tour of the FTL Workflow Syntax

Take a look at the <tt>workflow.ftl</tt> file in the
<tt>target/Workflow_Bundle_*/${name}/${version}/workflow</tt> directory.

<%= render '/includes/ftl_workflows/ftl_workflow/' %>

### Variables

Variables are simply defined in <tt>&lt;#assign&gt;</tt> blocks. To access variables from the workflow's ini file simply use the syntax <tt>${variable_name}</tt>.

<%= render '/includes/ftl_workflows/ftl_workflow_variables/' %>

### Files

Files that are inputs or outputs from workflows need to be manually copied in or out respectively.  This uses the ProvisionFiles module that knows how to move around local files or remote files on Amazon's S3.

<%= render '/includes/ftl_workflows/ftl_workflow_files/' %>

### Jobs

The jobs need to have distinct IDs and you can generate these using a for loop in FTL if need be. You can put any command in the <tt><argument></tt> section but mostly this is used to call GenericCommandRunner which runs the command provided in a Bash shell. Notice the use of <tt><profile></tt> to specify the job thread and memory count.

<%= render '/includes/ftl_workflows/ftl_workflow_jobs/' %>

### Dependencies

The dependencies section links together all the individual jobs in the correct order so they can be executed successfully. Parent/child relationships are used to specify job pre-requisite.

<%= render '/includes/ftl_workflows/ftl_workflow_deps/' %>

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
