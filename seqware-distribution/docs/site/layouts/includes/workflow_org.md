The directory structure created by the maven archetype includes a
<tt>pom.xml</tt> file which is our Maven build file, a <tt>src</tt> directory
which contains the Java workflow, and a workflow directory that contains any
bundled data, the basic workflow config file which includes all the parameters
this workflow accepts, the metadata.xml which defines the workflows available
in this bundle, and any scripts, binaries, or libraries your workflow needs (in
bin and lib respectively).

When you issue the <tt>mvn install</tt> command the target direct is created
which contains the compiled workflow along with the various necessary files all
correctly assembled in the proper directory structure.  You can change
directory to the workflow target directory (in this case
<tt>target/Workflow_Bundle_HelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %></tt> and
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
|   `-- Workflow_Bundle_HelloWorld_1.0-SNAPSHOT_SeqWare_<%= seqware_release_version %>
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
|               |   `-- seqware-distribution-<%= seqware_release_version %>-full.jar
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

