---

title:                 "SeqWare Query Engine"
toc_includes_sections: true
markdown:              basic 

---

## Overview

The SeqWare Query Engine is intended to be a universal store for sequence variants. Features include fine-grained control over versioning, a plug-in framework for new MapReduce plug-ins, rich value tags/annotations on most objects, and set organization. The current iteration of the SeqWare Query engine has the ability to store and search data stored in a modern NoSQL database, HBase, while using Google's Protocol Buffers for serialization.

## Setup

Please see the [Install Guide](/docs/github_readme/2-queryengine/)

## Loading Data

We currently load data via our command-line programs. In order to do this, you will want to go to the root directory and compile a full version of our jar with dependencies included. You will probably wish to skip the tests for our other components. This should look something like:
<pre title="Title of the snippet">
  <kbd>cd ...</kbd>
  <kbd>mvn clean install</kbd>
  <kbd>cd seqware-distribution/target</kbd>
</pre>

In this distribution directory, you can run our command line tools for import.

The first time you run these tools in a new namespace, you may wish to create a common reference and then create an ad hoc tag set that will store all tags that do not match known tags. Most of our command-line tools output a key value file that you can keep in order to record the ID of your created objects.

<pre title="Title of the snippet">
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>java -classpath seqware-distribution-0.13.6.5-qe-full.jar com.github.seqware.queryengine.system.ReferenceCreator</kbd>
Only 0 arguments found
ReferenceCreator <reference_name> [output_file]
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>java -classpath seqware-distribution-0.13.6.5-qe-full.jar com.github.seqware.queryengine.system.ReferenceCreator hg_19 keyValue_ref.out</kbd>
Reference written with an ID of:
hg_19
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>cat keyValue_ref.out</kbd>
referenceID    hg_19
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>java -classpath seqware-distribution-0.13.6.5-qe-full.jar com.github.seqware.queryengine.system.TagSetCreator</kbd>
Only 0 arguments found
TagSetCreator <TagSet name> [output_file]
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>java -classpath seqware-distribution-0.13.6.5-qe-full.jar com.github.seqware.queryengine.system.TagSetCreator ad_hoc keyValue_adHoc.out</kbd>
TagSet written with an ID of:
ad_hoc
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>cat keyValue_adHoc.out</kbd>
TagSetID	ad_hoc
namespace	BATMAN	
</pre>

You may also wish to pre-populate the database with (Sequence Ontology) SO terms in a TagSet:

<pre title="Title of the snippet">
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>java -classpath seqware-distribution-0.13.6.5-qe-full.jar com.github.seqware.queryengine.system.importers.OBOImporter</kbd>
Only 0 arguments found
OBOImporter <input_file> [output_file]
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>java -classpath seqware-distribution-0.13.6.5-qe-full.jar com.github.seqware.queryengine.system.importers.OBOImporter ../../seqware-queryengine/src/test/resources/com/github/seqware/queryengine/system/so.obo keyValueOBO.out</kbd>
6861 terms written to a TagSet written with an ID of:
42860461-0620-4990-bf15-32e6d34701b3
<span class="prompt">dyuen@odl-dyuen:~/seqware_github/seqware-distribution/target$</span> <kbd>cat keyValueOBO.out</kbd>
TagSetID	42860461-0620-4990-bf15-32e6d34701b3
namespace	BATMAN	
</pre>

The previous steps should only really need to be done once when first setting up a namespace. Afterwards, the VCF file importer can be called repeatedly for each of your datasets. Note that you will need to substitue the TagSetID from the previous step into the next step.

<pre title="Title of the snippet">
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>java -classpath seqware-distribution-0.13.6.5-qe-full.jar com.github.seqware.queryengine.system.importers.SOFeatureImporter</kbd>
usage: SOFeatureImporter
 -a <adHocTagSet>   (optional) an ID for an ad hoc TagSet, Tags will
                    either be found or added to this set, a new TagSet
                    will be generated if this option is not used
 -b <batch_size>    (optional) batch-size for the number of features in
                    memory to keep before a flush, will automatically be
                    chosen if not specified, we use 100000 for now
 -c                 (optional) whether we are working with compressed
                    input
 -f <featureSet>    (optional) for benchmarking for now, append features
                    to an existing featureset
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
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>java -classpath seqware-distribution-0.13.6.5-qe-full.jar com.github.seqware.queryengine.system.importers.SOFeatureImporter -i ../../seqware-queryengine/src/test/resources/com/github/seqware/queryengine/system/FeatureImporter/consequences_annotated.vcf -o keyValueVCF.out -r hg_19 -s 42860461-0620-4990-bf15-32e6d34701b3 -a ad_hoc -w VCFVariantImportWorker </kbd>
FeatureSet written with an ID of:
4bd2ced0-5e37-4930-bffe-207b862a09a6
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd> echo $? </kbd>
0
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd> cat keyValueVCF.out </kbd>
FeatureSetID    4bd2ced0-5e37-4930-bffe-207b862a09a6
</pre>

The ID of your feature sets and parameters will obviously change from run to run. Depending on the size of your data, you may also need to either tune the size of your batches (via the <code>'-b</code> option) when loading features or allocate more memory to java (<code>java -Xmx4096m -classpath ... </code>).

## Querying Data

The SOFeatureImporter will output a FeatureSet ID that should be used as part of the input to the VCFDumper command in order to export a FeatureSet.

<pre title="Title of the snippet">
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>  java -classpath seqware-distribution-0.13.6.5-qe-full.jar com.github.seqware.queryengine.system.exporters.VCFDumper</kbd>
0 arguments found
VCFDumper <featureSetID> [outputFile]
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>  java -classpath seqware-distribution-0.13.6.5-qe-full.jar com.github.seqware.queryengine.system.exporters.VCFDumper 4bd2ced0-5e37-4930-bffe-207b862a09a6 test_out.vcf</kbd>
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>  sort test_out.vcf > sorted_test_out.vcf</kbd>
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>  sort ../../seqware-queryengine/src/test/resources/com/github/seqware/queryengine/system/FeatureImporter/consequences_annotated.vcf  > control.vcf</kbd>
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>  diff -b sorted_test_out.vcf control.vcf</kbd>
</pre>

Most of these commands use non-zero return codes to indicate that an error occurred. For example:

<pre title="Title of the snippet">
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>java -classpath seqware-distribution-0.13.6.5-qe-full.jar com.github.seqware.queryengine.system.importers.SOFeatureImporter -i ../../seqware-queryengine/src/test/resources/com/github/seqware/queryengine/system/FeatureImporter/test_invalid.vcf -o keyValueVCF.out -r hg_19 -s 42860461-0620-4990-bf15-32e6d34701b3 -a ad_hoc -w VCFVariantImportWorker</kbd>
[SeqWare Query Engine] 1    [Thread-3] FATAL com.github.seqware.queryengine.system.importers.workers.VCFVariantImportWorker  - Exception thrown with file: ../../seqware-queryengine/src/test/resources/com/github/seqware/queryengine/system/FeatureImporter/test_invalid.vcf
java.lang.NumberFormatException: For input string: "51xxx"
	at java.lang.NumberFormatException.forInputString(NumberFormatException.java:48)
	at java.lang.Integer.parseInt(Integer.java:458)
	at java.lang.Integer.parseInt(Integer.java:499)
	at com.github.seqware.queryengine.system.importers.workers.VCFVariantImportWorker.run(VCFVariantImportWorker.java:285)
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
 
### Querying Data via Query Language

The <code>QueryVCFDumper</code> allows you to test queries written in our query language. This class takes in a featureID as input in order to perform a query and output the last feature set to VCF format. Our query language is currently specified [here](https://github.com/SeqWare/seqware/blob/3b96ae372bbdbf523090a4edf600bb7d34e9cdda/seqware-queryengine/src/main/java/com/github/seqware/queryengine/kernel/README.md) but will be migrated to this site.

Here is an example of how to interact with the utility, here we run through compiling a few queries and running them.

<pre title="Title of the snippet">
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>  java -cp seqware-distribution-0.13.6.5-qe-full.jar  com.github.seqware.queryengine.system.exporters.QueryVCFDumper</kbd>
usage: QueryVCFDumper
 -f <feature set ID>     (required) the ID of the featureset that we will
                         be querying and exporting
 -k <keyValue file>      (optional) a key value file that includes the
                         featureset ID of each featureset that is created
                         during querying and the final featureset ID
 -o <output file>        (optional) output file for the VCF
 -p <query parameters>   (required) full classname for the class
                         implementing QueryDumperInterface
 -s <query string>       (required) plain text query
[SeqWare Query Engine] 0    [main] FATAL com.github.seqware.queryengine.system.exporters.QueryVCFDumper  - 
org.apache.commons.cli.MissingOptionException: Missing required options: f, [-s (required) plain text query, -p (required) full classname for the class implementing QueryDumperInterface]
	at org.apache.commons.cli.Parser.checkRequiredOptions(Parser.java:299)
	at org.apache.commons.cli.Parser.parse(Parser.java:231)
	at org.apache.commons.cli.Parser.parse(Parser.java:85)
	at com.github.seqware.queryengine.system.exporters.QueryVCFDumper.runMain(QueryVCFDumper.java:78)
	at com.github.seqware.queryengine.system.exporters.QueryVCFDumper.main(QueryVCFDumper.java:42)
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>java -classpath  seqware-distribution-0.13.6.5-qe-full.jar com.github.seqware.queryengine.system.exporters.QueryVCFDumper -f 0737bf87-28c5-4323-a285-7898137e22ab -k keyValue.out -o output.vcf -s "seqid==\"21\""</kbd>
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>java -classpath  seqware-distribution-0.13.6.5-qe-full.jar com.github.seqware.queryengine.system.exporters.QueryVCFDumper -f 0737bf87-28c5-4323-a285-7898137e22ab -k keyValue.out -o output.vcf -s "seqid==\"21\" && start >= 20000000 && stop &lt;= 30000000"</kbd>
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>java -classpath  seqware-distribution-0.13.6.5-qe-full.jar com.github.seqware.queryengine.system.exporters.QueryVCFDumper -f 0737bf87-28c5-4323-a285-7898137e22ab -k keyValue.out -o output.vcf -s "seqid==\"21\" && start >= 20000000 && stop &lt;= 30000000 && tagOccurrence(\"ad_hoc\",\"non_synonymous_codon\")"</kbd>
</pre>


### Querying Data via Java API Calls

The <code>QueryVCFDumper</code> also allows you to quickly test Java queries written against the <code>QueryInterface</code> directly. This class takes in a featureID as input and a class name for a class that implements the <code>com.github.seqware.queryengine.system.exporters.QueryDumperInterface</code> in order to perform a few queries and output the last feature set to VCF format. 

Here is an example of how to interact with the utility, here we run through compiling a few queries and running them.

<pre title="Title of the snippet">
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd>  cp ../../seqware-queryengine/src/test/java/com/github/seqware/queryengine/system/test/queryDumper/VCFDumperParameterExample.java QueryTutorial.java</kbd>
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd> gvim QueryTutorial.java</kbd>
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd> javac -cp seqware-distribution-0.13.6.5-qe-full.jar QueryTutorial.java</kbd>
<span class="prompt">~/seqware_github/seqware-distribution/target$</span> <kbd> java -cp .:seqware-distribution-0.13.6.5-qe-full.jar  com.github.seqware.queryengine.system.exporters.QueryVCFDumper -f 4bd2ced0-5e37-4930-bffe-207b862a09a6 -k keyValue.out -o output.vcf -p QueryTutorial</kbd>
</pre>

During the <code>gvim</code> step, it is important to delete the package line, delete the import from the test package, change the classname, and then perform the required changes to the queries. For example, to search for intron_variants rather than non_synonymous_codon, we will need to change the file to the following:

<pre title="Title of the snippet"><code class="language-java">
/*
 * Copyright (C) 2012 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.kernel.RPNStack;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.QueryFuture;
import com.github.seqware.queryengine.model.QueryInterface;
import com.github.seqware.queryengine.system.exporters.QueryDumperInterface;

/**
 * An example of a parameter file. See more possible Queries in {@link QueryInterfaceTest}.
 * @author dyuen
 */
public class QueryTutorial implements QueryDumperInterface{

    @Override
    public int getNumQueries() {
        // we will run three queries
        return 3;
    }

    @Override
    public QueryFuture<FeatureSet> getQuery(FeatureSet set, int queryNum) {
        if (queryNum == 0){
            /// limits us to CHROM #21
            return SWQEFactory.getQueryInterface().getFeaturesByAttributes(0, set, new RPNStack(new RPNStack.FeatureAttribute("seqid"), new RPNStack.Constant("21"), RPNStack.Operation.EQUAL));
        } else if (queryNum == 1){
            // limits us to the range of 20000000 through 30000000
            return SWQEFactory.getQueryInterface().getFeaturesByRange(0, set, QueryInterface.Location.INCLUDES, "21", 20000000, 30000000);
        } else{
            // limits us to features with a particular tag
            return SWQEFactory.getQueryInterface().getFeaturesByAttributes(0, set, new RPNStack(new RPNStack.TagOccurrence("ad_hoc", "intron_variant")));
        }
    }
    
}

</code></pre>

## Testing

The testing directories are <code>com.github.seqware.queryengine.model.test</code>, <code>com.github.seqware.queryengine.impl.test</code>, and <code>com.github.seqware.queryengine.system.test</code>. These directories test the model objects that outside developers can manipulate and interact with, specific features of the back-end, and the command-line tools respectively. Note that the tests can be run from a <code>TestSuite</code> that is available in each directory while new tests should be added to the <code>DynamicSuiteBuilder</code> in each directory. Note that the tests in the model directory can be run against a variety of back-ends and two serialization techniques. 

By default, tests will run against a mini-HBase cluster that starts up on the localhost. However, if you override this via <code>Constants</code> or the <code>~/.seqware/settings</code> file, after running through the full test suite multiple times, the tests run against the simpler back-ends with no optimization will slow down. This can be fixed by running the following code via the HBase shell in order to clear out all stored data:

<pre title="Title of the snippet">
  <kbd>hbase shell</kbd>
  <kbd>disable_all '.*'</kbd>
  <kbd>drop_all '.*'</kbd>
</pre>

<p class="warning"><strong>Note:</strong>
	This will destroy all data on the HBase storage. If you have any doubt or are working in a production environment, it is better to restrict the delete to one namespace, for example <code>disable_all '<namespace>.*' </code>
</p>


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

Currently, the plug-in infrastructure allows you to:

* Install, list, and call arbitrary plug-ins via the <code>installAnalysisPlugin</code>, <code>getAnalysisPlugins</code>, and <code>getFeaturesByPlugin</code> methods in the <code>QueryInterface</code> class and demonstrated in <code>QueryInterfaceTest</code>. 
* Specify and prototype new plug-ins by working in the <code>com.github.seqware.queryengine.plugins.inmemory</code> directory.
* Create new Map/Reduce plugins by extending <code>AnalysisPluginInterface</code> directly or (preferred) extending the plugins in <code>com.github.seqware.queryengine.plugins.hbasemr</code>

We intend on further cleaning up the plug-in architecture, improving the persistence of analysis events, and adding support for scan plug-ins. 

## More to Come ...

In the future, we will be developing:

1.	REST API: We will be developing a RESTful API to allow for the development of web applications and to allow users to query and view their data easily.
2. 	Web App: A sample web application demoing genome browsing  

