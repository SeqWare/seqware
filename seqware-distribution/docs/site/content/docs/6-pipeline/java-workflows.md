---

title:                 "Java Workflows"
markdown:              advanced
is_dynamic:            true
toc_includes_sections: true

---

<!-- TODO: 
* add more info on variables defined like random
-->

## Overview

<p class="warning"><strong>Tip:</strong> The Java workflow language is 
our recommended workflow language for new development.</p>

This document really focuses on the format of the Java workflow language. For
more information about the entire workflow bundle please see the [Developer
Tutorial](/docs/3-getting-started/developer-tutorial/).  You should read this
guide before this page.

## Limitations

The Java workflows work with both the Pegasus and Oozie Workflow Engines. 
That being said, if you use MapReduce or other Hadoop-specific job types in your Java
workflows they will not function in the Pegasus Workflow Engine (since
Hadoop is only present for the Oozie Workflow Engine).

## Creating a Java Workflow Bundle

In the [Developer Tutorial](/docs/3-getting-started/developer-tutorial/) you
saw how to create a HelloWorld Java workflow using archetype.

<%= render '/includes/java_archetype/' %>

Alternatively, enter the workflow name and version you want to use.  When complete, you can
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

Variables are simply defined as any other object variables would be in Java. To
access variables from the workflow's ini file simply use the
<tt>getProperty("key")</tt> method.

<%= render '/includes/java_workflows/java_workflow_vars/' %>

### Files & Directories

Files that are inputs or outputs from workflows need to be copied in or out
respectively.  Under the hood, this uses the ProvisionFiles module that knows
how to move around local files, files over HTTP, or remote files on Amazon's
S3. The Java syntax simplifies the declaration of these input and output files
for workflows by providing the method below. Keep in mind, you can also just
transfer input or output files by using a standard job that calls the necessary
command line tool and bypass this built in system. But the
<tt>setupFiles()</tt> method will likely work for most purposes and is the
easiest way to register workflow files.

<%= render '/includes/java_workflows/java_workflow_files/' %>

You can also specify directories to be created in the working directory of your workflow.

<%= render '/includes/java_workflows/java_workflow_dirs/' %>

### Jobs & Dependencies

The jobs need to have distinct IDs and you can generate these using a for loop
in FTL if need be. You can put any command in the <tt>argument</tt> section
but mostly this is used to call GenericCommandRunner which runs the command
provided in a Bash shell. 

<%= render '/includes/java_workflows/java_workflow_jobs/' %>

Currently only the job supported is using the <tt>createBashJob()</tt> method. In the
future we will provide and expanded list of convenience job types for example
MapReduce, Pig, Java jar, etc.

The dependencies section links together all the individual jobs in the correct
order so they can be executed successfully. Parent/child relationships are used
to specify job pre-requisites.

TODO: discuss the JobTypes, namely Bash

### Symbolic Links to Local Dependencies

This type of dependency is not generally recommended. In most cases you will want to check a dependency into a maven repository or add it into the binary or data directories of a workflow. 

However, in the following cases, you will need to rely on symbolic links:

*   The dependency is too large to go into a maven repository 
*   The dependency is proprietary or cannot be redistributed 

By convention, the symlinks go into a directory in the root of the workflow called links. They should link to a directory, not to a single file (for the purposes of copying the dependencies to the final bundle. Maven doesn't accept single files for copying upon install.


First, create the link:

    [seqware@seqwarevm HelloWorld]$ cd links
    [seqware@seqwarevm links]$ rm -Rf *
    [seqware@seqwarevm links]$ ln -s ../workflow/data/
    [seqware@seqwarevm links]$ ls -alhtr
    total 8.0K
    drwxrwxr-x 5 seqware seqware 4.0K May 31 17:39 ..
    lrwxrwxrwx 1 seqware seqware   17 May 31 17:41 data -> ../workflow/data/
    drwxrwxr-x 2 seqware seqware 4.0K May 31 17:41 .


Second, modify your bundle's pom.xml to create a link when compiling your bundle:
	
    <build>
       ...
        <plugins>
	   ...
            <plugin>
                <groupId>com.pyx4j</groupId>
                <artifactId>maven-junction-plugin</artifactId>
                <executions>
                        <execution>
                        <phase>package</phase>
                        <goals>
                                <goal>link</goal>
                        </goals>
                </execution>
                <execution>
                        <id>unlink</id>
                        <phase>clean</phase>
                        <goals>
                                <goal>unlink</goal>
                        </goals>
              </execution>
            </executions>
            <configuration>
              <links>
               <link>
                  <dst>${project.build.directory}/Workflow_Bundle_${workflow-name}_${project.version}_SeqWare_${seqware-version}/Workflow_Bundle_${workflow-directory-name}/${project.version}/data/data</dst>
                  <src>${basedir}/links/data</src>
                </link>
              </links>
            </configuration>
         </plugin>
        </plugins>
    </build>
    </project>

Your bundles will now contain a symbolic link to your dependency after "mvn clean install" in the data directory and this will only be included in the bundle when the bundle is packaged (and/or installed). 


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

See the  [Developer Tutorial](/docs/3-getting-started/developer-tutorial/)
document for more information. For Java API documentation consult the [SeqWare
Javadocs](/docs/11-api/).
