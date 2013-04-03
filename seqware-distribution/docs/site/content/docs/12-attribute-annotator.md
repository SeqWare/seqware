---

title:                 "Attribute Annotator"
toc_includes_sections: true
markdown:              basic

---

The Attribute Annotator is a SeqWare plugin that allow you to annotate Lanes, IUSes and Sequencer Runs with Key-Value pairs or 'skip' fields. The 'skip' field is a boolean that indicates to a decider that the results from that lane, IUS or sequencer run should be skipped in downstream annotation. When the 'skip' field is enabled through this plugin, a key-value pair can also be added that describes the reason for skipping the lane, ius or sequencer run. Alternatively, this plugin also allows an object to be annotated with any key-value pair.

##Requirements

In order to run the WorkflowRunReporter plugin, you must have the following available to you:

* SeqWare Pipeline JAR (0.11.4 or higher)
* SeqWare settings file set up to contact the SeqWare Web service (contact your local SeqWare admin to get the path)

##Command line parameters

You can annotate the following attributes:
<table><tr><th>
Command-line option</th>	<th>Description</th></tr>
<tr><td>
--sequencer-run-accession, -sr</td>	<td>The SWID of the sequencer run to annotate.</td></tr><tr><td>
--lane-accession, -l</td><td>The SWID of the lane to annotate.</td></tr><tr><td>
--ius-accession, -i	</td>	<td>The SWID of the ius to annotate.</td></tr><tr><td>
--experiment-accession, -e</td>	<td>	The SWID of the experiment to annotate.</td></tr><tr><td>
--processing-accession, -p	</td>	<td>	The SWID of the processing to annotate.</td></tr><tr><td>
--sample-accession, -s	</td>	<td>The SWID of the sample to annotate.</td></tr><tr><td>
--study-accession, -st	</td>	<td>The SWID of the study to annotate.</td></tr><tr><td>
--workflow-accession, -w	</td>	<td>The SWID of the workflow to annotate.</td></tr><tr><td>
--workflow-run-accession, -wr	</td>	<td>The SWID of the workflow run to annotate.</td></tr><tr><td>
--skip	</td>	<td>Sets the 'skip' flag to either true or false</td></tr><tr><td>
--key	</td>	<td>The key that defines this attribute. The default value is 'skip'.</td></tr><tr><td>
--value	</td>	<td>The description of this key. If not specified, no attribute will be created.</td></tr><tr><td>
--file	</td>	<td>A CSV file that with multiple TYPE, SWID, KEY, VALUE</td></tr>
</table>

There are four ways to use this plugin

* Annotate an attribute with 'skip'
* Annotate an attribute with 'skip' and a description
* Annotate an attribute with a key-value pair
* Bulk annotate attributes with a CSV file

##Examples

Set the 'skip' value of lane with SWID 1234 to 'true'. No key-value annotations are added.

	java -jar seqware-distribution/target/seqware-distribution-0.13.6.3-full.jar --plugin net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator -- --lane-accession 1234 --skip true

Set the 'skip' value of sequencer run with SWID 3456 to 'true' and enter a key-value pair with the key "skip" and value "Improperly entered into the LIMS". Notice that the key is not specified but defaults to 'skip'.

	java -jar seqware-distribution/target/seqware-distribution-0.13.6.3-full.jar --plugin net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator -- --sequencer-run-accession 3456 --skip true --value "Improperly entered into the LIMS"

Annotate the IUS with SWID 2345 with a key-value pair of key "dual-barcodes" and value "barcode 1". The 'skip' field is not affected.

	java -jar seqware-distribution/target/seqware-distribution-0.13.6.3-full.jar --plugin net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator -- --ius-accession 2345 --key "dual-barcodes" --value "barcode 1"

Bulk insert attribute annotation with file

	java -jar seqware-distribution/target/seqware-distribution-0.13.6.3-full.jar --plugin net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator -- --file file.csv

The file is in CSV format

	e,106288,t11,v11
	e,106288,t21,v21
	e,106288,t31,v11
	e,1234,key1,value1
	s,1000,key1,value1
	st,321,key1,value1

##View the annotated attributes

To check the attribute annotation, you can use the similar URI. See <a href="http://seqware.github.com/seqware/">http://seqware.github.com/seqware/</a> for more detail.

	http://host:port/seqware-webservice/lanes/{swid}?show=attributes

