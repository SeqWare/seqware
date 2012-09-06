---

title:                 "SeqWare Query Engine"
toc_includes_sections: true
markdown:              basic

---

## Overview

The SeqWare Query Engine is intended to be a universal store for sequence variants. Features include fine-grained control over versioning, a plug-in framework for new MapReduce plug-ins, rich value tags/annotations on most objects, and set organization. The current iteration of the SeqWare Query engine has the ability to store and search data stored in a modern NoSQL database, HBase, while using Google's Protocol Buffers for serialization.

## Admin Setup

We can start by getting your development environment setup with the appropriate prerequisites. The [Installing with a Local VM](/docs/2-installation/) guide will give you access to a VM which has these setup correctly. However, if you wish to set this up yourself and you have git and mvn installed:

1.	You will want to go to the [SeqWare Github repository](https://github.com/SeqWare/seqware) and run the command <code>git clone git@github.com:SeqWare/seqware.git seqware_github</code>     
2. 	You will also need to setup Hadoop and HBase if it is not already setup. We highly recommend [Cloudera's CDH packages](https://ccp.cloudera.com/display/CDH4DOC/CDH4+Quick+Start+Guide) since they are tested for package incompatibilities between Hadoop projects (which are still common). It is also worth double-checking the web interface for HBase which is usually at [http://localhost:60010/master-status](http://localhost:60010/master-status).

For both the VM and a local development environment, continue here:

There are a number of constants that may have to be set, particularly for a developer. These are currently in the <code>com.github.seqware.queryengine.Constants</code> file although they will eventually be moved to an external configuration file. In particular, you should set your <code>NAMESPACE</code> to avoid collisions with other developers and if you wish for your distribution jar to be automatically copied to the cluster when launching MapReduce tasks, you will need to correct the <code>DEVELOPMENT_DEPENDENCY</code>.
<p class="warning"><strong>Note:</strong>
	   It is important that you check your <code>NAMESPACE</code>, <code>HBASE_REMOTE_TESTING</code>, and <code>HBASE_PROPERTIES</code> variables. They currently control the prefix for your tables, whether you connect to a local install of HBase, and which remote install of HBase you want to connect to respectively.
</p>
1. 	Refresh the code for the query engine by doing a <code>git fetch</code> and <code>git pull</code> in the seqware_github directory. On the VM, you may need to merge changes or simply discard changes with a command such as <code>git checkout seqware-queryengine/src/main/java/com/github/seqware/queryengine/Constants.java</code>
2. 	If the [web interface](http://localhost:60010/master-status) for HBase stalls or is inactive, you may need to restart the HBase processes. This can be done by the following commands:
	<pre title="Title of the snippet">
	sudo bash
	/etc/init.d/hbase-regionserver stop
	/etc/init.d/hbase-master stop
	/etc/init.d/hadoop-zookeeper-server stop
	/etc/init.d/hbase-regionserver start
	/etc/init.d/hbase-master start
	/etc/init.d/zookeeper-server start
	jps	
	</pre>
3. 	When setup of Hadoop and HBase is complete, you can go into the query-engine directory, compile it, and run the tests. Please note that the web-service and legacy directories in the root have additional dependencies and may not necessarily compile following only these instructions.
	<pre title="Title of the snippet">
	mvn clean install
	mvn javadoc:javadoc
	mvn javadoc:test-javadoc
	</pre>
This will generate javadoc documentation for both the main code and the testing code in <code>seqware-queryengine/target/site/apidocs/index.html</code> and <code>seqware-queryengine/target/site/testapidocs/index.html</code> respectively. 

## Loading Data

We currently load data via our command-line programs. In order to do this, you will want to go to the root directory and compile a full version of our jar with dependencies included. You will probably wish to skip the tests for our other components. This should look something like:
<pre title="Title of the snippet">
  <kbd>cd ...</kbd>
  <kbd>mvn clean install -Dmaven.test.skip=true</kbd>
  <kbd>cd seqware-distribution/target</kbd>
</pre>

In this distribution directory, you can run our command line tools for import.

The first time you run these tools in a new namespace, you may wish to create a common reference and then create an ad hoc tag set that will store all tags that do not match known terms.

<pre title="Title of the snippet">
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>java -classpath seqware-queryengine-0.12.0-full.jar com.github.seqware.queryengine.system.ReferenceCreator</kbd>
Only 0 arguments found
ReferenceCreator <reference_name> [output_file]
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>java -classpath seqware-queryengine-0.12.0-full.jar com.github.seqware.queryengine.system.ReferenceCreator hg_42 keyValue_ref.out</kbd>
Reference written with an ID of:
59a1aca5-4a5f-4006-b395-ca8cb5dd8c50
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>cat keyValue_ref.out</kbd>
referenceID    59a1aca5-4a5f-4006-b395-ca8cb5dd8c50
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>java -classpath seqware-queryengine-0.12.0-full.jar com.github.seqware.queryengine.system.TagSetCreator</kbd>
Only 0 arguments found
TagSetCreator <TagSet name> [output_file]
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>java -classpath seqware-queryengine-0.12.0-full.jar com.github.seqware.queryengine.system.TagSetCreator ad_hoc keyValue_adHoc.out</kbd>
TagSet written with an ID of:
84ec5dfb-1cba-4b7b-b06a-eebbfc3dfd60
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>cat keyValue_adHoc.out</kbd>
TagSetID    84ec5dfb-1cba-4b7b-b06a-eebbfc3dfd60
</pre>

You may also wish to pre-populate the database with (Sequence Ontology) SO terms in a TagSet:

<pre title="Title of the snippet">
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>java -classpath seqware-queryengine-0.12.0-full.jar com.github.seqware.queryengine.system.importers.OBOImporter</kbd>
Only 0 arguments found
OBOImporter <input_file> [output_file]
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>java -classpath seqware-queryengine-0.12.0-full.jar com.github.seqware.queryengine.system.importers.OBOImporter ../../seqware-queryengine/src/test/resources/com/github/seqware/queryengine/system/so.obo keyValueOBO.out</kbd>
3870 terms written to a TagSet written with an ID of:
5e7a2327-08ac-4455-9687-ec4c3737074f
<span class="prompt">dyuen@odl-dyuen:~/seqware_github/seqware-distribution/target$</span> <kbd>cat keyValueOBO.out</kbd>
TagSetID    5e7a2327-08ac-4455-9687-ec4c3737074f
</pre>

The previous steps should only really need to be done once when first setting up a namespace. Afterwards, the VCF file importer can be called repeatedly for each of your datasets.

<pre title="Title of the snippet">
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd> java -classpath seqware-queryengine-0.12.0-full.jar com.github.seqware.queryengine.system.importers.SOFeatureImporter</kbd>
usage: SOFeatureImporter
 -a <adHocTagSet>   (optional) an ID for an ad hoc TagSet, Tags will
                    either be found or added to this set, a new TagSet
                    will be generated if none is specified here
 -c                 (optional) whether we are working with compressed
                    input
 -i <inputFile>     (required) comma separated input files
 -o <outputFile>    (optional) output file with our resulting key values
 -r <reference>     (required) the reference ID to attach our FeatureSet
                    to
 -s <tagSet>        (optional) comma separated TagSet IDs, new Tags will
                    be linked to the first set that they appear, these
                    TagSets will not be modified
 -t <threads>       (optional: default 1) the number of threads to use in
                    our import
 -w <worker>        (required) the work module and thus the type of file
                    we are working with
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd> java -classpath seqware-queryengine-0.12.0-full.jar com.github.seqware.queryengine.system.importers.SOFeatureImporter -i ../../seqware-queryengine/src/test/resources/com/github/seqware/queryengine/system/FeatureImporter/consequences_annotated.vcf -o keyValueVCF.out -r 59a1aca5-4a5f-4006-b395-ca8cb5dd8c50 -s 5e7a2327-08ac-4455-9687-ec4c3737074f -a 84ec5dfb-1cba-4b7b-b06a-eebbfc3dfd60 -w VCFVariantImportWorker </kbd>
FeatureSet written with an ID of:
99d3cc0e-26e0-4b23-9ebf-90fbe07a6c5e
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd> echo $? </kbd>
0
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd> cat keyValueVCF.out </kbd>
FeatureSetID    99d3cc0e-26e0-4b23-9ebf-90fbe07a6c5e
</pre>

## Querying Data

The SOFeatureImporter will output a FeatureSet ID that should be used as part of the input to the VCFDumper command in order to export a FeatureSet.

<pre title="Title of the snippet">
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>  java -classpath seqware-queryengine-0.12.0-full.jar com.github.seqware.queryengine.system.exporters.VCFDumper</kbd>
0 arguments found
VCFDumper <featureSetID> [outputFile]
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>  java -classpath seqware-queryengine-0.12.0-full.jar com.github.seqware.queryengine.system.exporters.VCFDumper 99d3cc0e-26e0-4b23-9ebf-90fbe07a6c5e test_out.vcf</kbd>
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>  sort test_out.vcf > sorted_test_out.vcf</kbd>
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>  sort ../../seqware-queryengine/src/test/resources/com/github/seqware/queryengine/system/FeatureImporter/consequences_annotated.vcf  > control.vcf</kbd>
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>  diff sorted_test_out.vcf control.vcf</kbd>
</pre>

Most of these commands use non-zero return codes to indicate that an error occurred. For example:

<pre title="Title of the snippet">
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>  java -classpath seqware-queryengine-0.12.0-full.jar com.github.seqware.queryengine.system.importers.SOFeatureImporter -i ../../seqware-queryengine/src/test/resources/com/github/seqware/queryengine/system/FeatureImporter/test_invalid.vcf -o keyValueVCF.out -r 59a1aca5-4a5f-4006-b395-ca8cb5dd8c50 -s 5e7a2327-08ac-4455-9687-ec4c3737074f -a 84ec5dfb-1cba-4b7b-b06a-eebbfc3dfd60 -w VCFVariantImportWorker</kbd>
[SeqWare Query Engine] 1    [Thread-3] FATAL com.github.seqware.queryengine.system.importers.workers.VCFVariantImportWorker  - Exception thrown with file: ../../seqware-queryengine/src/test/resources/com/github/seqware/queryengine/system/FeatureImporter/test_invalid.vcf
java.lang.NumberFormatException: For input string: "51xxx"
    at java.lang.NumberFormatException.forInputString(NumberFormatException.java:48)
    at java.lang.Integer.parseInt(Integer.java:458)
    at java.lang.Integer.parseInt(Integer.java:499)
    at com.github.seqware.queryengine.system.importers.workers.VCFVariantImportWorker.run(VCFVariantImportWorker.java:273)
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>  echo $?</kbd>
10
</pre>

The default VCFDumper simply exports all Features that are in a FeatureSet. However, it is also an excellent starting point for experimenting with new queries and various features of the query engine.

One example of how one may wish to extend and adapt the VCFDumper is available in <code>com.github.seqware.queryengine.tutorial.BrianTest</code> while specific queries can be found in <code>com.github.seqware.queryengine.model.test.QueryInterfaceTest</code> and copy-and-pasted into <code>BrianTest</code>. 

<p class="warning"><strong>Note:</strong>
 More details on the inner workings of the query engine will be available in "Extending the Query Engine." However, for a developer that will primarily interact with the model objects and command-line tools, the most important distinction to be aware of is which operations update individual features in a feature set and which operations perform a copy-on-write operation for the whole feature set since the latter is much more expensive. 

In general, operations that iterate through a feature set, read or update individual features. Operations that go through the Query Interface and call plug-ins will perform a copy-on-write. Operations that perform a copy-on-write always provide a TTL (time-to-live) parameter.
</p>

<code>QueryInterfaceTest</code> brings us to the code in the testing directories which demonstrate many of the features available to the Query Engine.

## Testing

The testing directories are <code>com.github.seqware.queryengine.model.test</code>, <code>com.github.seqware.queryengine.impl.test</code>, and <code>com.github.seqware.queryengine.system.test</code>. These directories test the model objects that outside developers can manipulate and interact with, specific features of the back-end, and the command-line tools respectively. Note that the tests can be run from a <code>TestSuite</code> that is available in each directory while new tests should be added to the <code>DynamicSuiteBuilder</code> in each directory. Note that the tests in the model directory can be run against a variety of back-ends and two serialization techniques. 

Note, after running through the full test suite multiple times, the tests run against the simpler back-ends with no optimization will slow down. This can be fixed by running the following code via the HBase shell in order to clear out all stored data:

<pre title="Title of the snippet">
  <kbd>hbase shell</kbd>
  <kbd>disable_all '.*'</kbd>
  <kbd>drop_all '.*'</kbd>
</pre>

## Features

Our unit tests are designed to test and highlight various features that are available in the Query Engine. Some highlights include:

* QueryInterface - demonstrates our query language and running queries that create new filtered feature sets
* TaggableTest - demonstrates how to associate tags, read tags, and what can be stored in tags
* TTLTest - demonstrates storing and reading time-to-live fields
* FriendlyNameTest - an interface for explicitly naming objects rather than using randomly generated IDs
* SOFeatureImporterTest - demonstrates various some options available to the feature importer command-line tool

## Extending the Query Engine

Seeing how the command-line tools work in <code>com.github.seqware.queryengine.system</code> is a good introduction to our QueryEngine. In particular, <code>VCFImportWorker</code> and <code>VCFDumper</code> show how a front-end developer should interact with our code. New entities are created via the <code>CreateUpdateManager</code> and Queries are performed through the <code>QueryInterface</code>. Note the use of a Builder Design Pattern in the <code>ImportWorker</code>. This allows us to abstract the actual implementation of many of our model classes and emphasizes the idea that we create objects that are largely immutable once we write them to the database although we can always write new versions.

## Making Custom Plugins

TODO: This is still a work in progress.

Currently, the plug-in infrastructure allows you to:

* Install, list, and call arbitrary plug-ins via the <code>installAnalysisPlugin</code>, <code>getAnalysisPlugins</code>, and <code>getFeaturesByPlugin</code> methods in the <code>QueryInterface</code> class and demonstrated in <code>QueryInterfaceTest</code>. 
* Specify and prototype new plug-ins by working in the <code>com.github.seqware.queryengine.plugins.inmemory</code> directory.
* Create new Map/Reduce plugins by extending <code>AnalysisPluginInterface</code> directly or (preferred) extending the plugins in <code>com.github.seqware.queryengine.plugins.hbasemr</code>

We intend on further cleaning up the plug-in architecture, improving the persistence of analysis events, and adding support for scan plug-ins. 

## More to Come ...

TODO: In the future, we will be developing:

1.	REST API: We will be developing a RESTful API to allow for the development of web applications and to allow users to query and view their data easily.
2. 	Web App: A sample web application demoing genome browsing  

