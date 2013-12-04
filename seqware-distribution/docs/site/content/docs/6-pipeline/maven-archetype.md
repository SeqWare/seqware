---

title:                 "Maven Archetype"
markdown:              advanced
is_dynamic:            true
toc_includes_sections: true

---

## Working on your own Workflows

Going back to creation of new workflows in the maven-bundles directory, we will explain some of the magic numbers and settings that we skipped over while demoing workflow launching. 

Assuming one uses the Java workflow, use the following options after mvn archetype:generate

        Choose a number or apply filter (format: [groupId:]artifactId, case sensitive contains): 16: 62
	Define value for property 'package': : net.sf.seqware
	Define value for property 'groupId':  net.sf.seqware: : 
	Define value for property 'workflow-name': : MyHelloWorld
	Define value for property 'artifactId':  workflow-MyHelloWorld: : 
	Define value for property 'version':  1.0-SNAPSHOT: : 1.0
	[INFO] Using property: package = net.sf.seqware
	Confirm properties configuration:
	package: net.sf.seqware
	groupId: net.sf.seqware
	workflow-name: MyHelloWorld
	artifactId: workflow-MyHelloWorld
	version: 1.0
	package: net.sf.seqware
	 Y: : Y
	[INFO] ----------------------------------------------------------------------------
	[INFO] Using following parameters for creating project from Archetype: seqware-archetype-java-workflow:1.0.8-SNAPSHOT
	[INFO] ----------------------------------------------------------------------------
	[INFO] Parameter: groupId, Value: net.sf.seqware
	[INFO] Parameter: artifactId, Value: workflow-MyHelloWorld
	[INFO] Parameter: version, Value: 1.0
	[INFO] Parameter: package, Value: net.sf.seqware
	[INFO] Parameter: packageInPathFormat, Value: net/sf/seqware
	[INFO] Parameter: version, Value: 1.0
	[INFO] Parameter: package, Value: net.sf.seqware
	[INFO] Parameter: groupId, Value: net.sf.seqware
	[INFO] Parameter: workflow-name, Value: MyHelloWorld
	[INFO] Parameter: artifactId, Value: workflow-MyHelloWorld
        [INFO] project created from Archetype in dir: /home/seqware/SeqWare/maven-bundles/workflow-MyHelloWorld
        [INFO] ------------------------------------------------------------------------
        [INFO] BUILD SUCCESSFUL
        [INFO] ------------------------------------------------------------------------
        [INFO] Total time: 4 minutes 35 seconds
        [INFO] Finished at: Fri Nov 23 14:45:24 EST 2013
        [INFO] Final Memory: 23M/166M
        [INFO] ------------------------------------------------------------------------



Note the following conventions:

* package
: The Java package to place the workflow. Usually, we use the reversed domain of the primary project, e.g. net.github.seqware or ca.on.oicr.pde
* groupId
: Usually identical to the package. The workflow belongs to this group. This group id is part of the workflow's unique Maven identity.
* workflowName
: A unique camel case workflow name. This will also be used as the name of the directory containing all the files associated with this workflow.
* artifactId
: The specific name of the workflow. Also part of the workflow's unique Maven identity. The archetype defaults to workflow${workflowName}
* version
: The version of this workflow. Again, part of the workflow's unique identity. When maintaining the code in the future it will be possible to update this value.


<!-- this explicit pre tag should be avoided, but the automatic nanoc formatting does not 
seem to work here -->
<pre>
        $ cd workflow-MyHelloWorld
        $ ls
        pom.xml src workflow  workflow.properties
</pre>

* pom.xml
: A Maven project file. Edit this to change the version of the workflow and to add or modify workflow dependencies such as program, modules and data.
* workflow
: This directory contains the workflow skeleton. Look in here to modify the workflow .ini, .java files (Java workflow) or workflow.ftl(FTL workflow). The examples of Java and FTL can be found <a href="/docs/15-workflow-examples/">here</a>.
* src 
: This directory contains the Java client. Look in here to modify the .java files (Java workflow). The examples of Java and FTL can be found <a href="/docs/15-workflow-examples/">here</a>.
* workflow.properties
: You can edit the description and workflow names in this file.

#### Variables

Automatically Defined

* ${date}: a string representing the date the DAX was created, this is always defined so consider this a reserved variable name. 

* ${random}: a randomly generated string, this is always defined so consider this a reserved variable name. 

* ${workflow_bundle_dir}: if this workflow is part of a workflow bundle this variable will be defined and points to the path of the root of the directory this workflow bundle has been expanded to. 

* ${workflow_base_dir}: ${workflow_bundle_dir}/Workflow_Bundle_{workflow_name}/{workflow_version}



