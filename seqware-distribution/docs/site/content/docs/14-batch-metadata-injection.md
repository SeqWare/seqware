---

title:					"Batch Metadata Injection"
toc_includes_sections: 	true
markdown:				basic
is_dynamic:				true
---

The Batch Metadata Injection plugin is a plugin which allows you to insert data directly into the database. There is an interactive mode featured in the plugin which will allow you to interactively enter in the data, or you could import in a JSON file with the required data in it. You are are also able to export your results into a json format.

##Requirements

In order to run the plugin, you need:

* Seqware Pipeline Jar
* SeqWare settings file set up to contact the SeqWare Web service (contact your local SeqWare admin to get the path)

##Command Line Parameters

The BMI plugin takes in the following parameters:
<table><th>
	<tr>
		<th>Command-line option</th>
		<th>Description</th>
	</tr>
	</th>
	<tr>
		<td>–c, –create</td>
	  	<td>Optional: indicates you want to create a new row, must supply –table and all the required –field params.</td>
	</tr>
	<tr>
		<td>–export-json-sequencer-run</td>
		<td> The data entered will be exported as a JSON document. The argument should be the location of where you wish to place said document.</td>
	</tr>
	<tr>
		<td>–f, –field</td>
	  	<td>Optional: the field you are interested in writing. This is encoded as "&#60;field_name&#62;::&#60;value&#62;". You should use single quotes when the value includes spaces. You can supply multiple –field arguments for a given table insert.
	</td>
	</tr>
	<tr>
		<td>–file</td>
	  	<td>Optional: one file option can be specified when you create a file, one or more –file options can be specified when you create a workflow run. This is encoded as "&#60;algorithm&#62;::&#60;file-meta-type&#62;::&#60;file-path&#62;". You should use single quotes when the value includes spaces.
		</td>
	</tr>
	<tr>
		<td>–import-json-sequencer-run</td>
		<td>Allows you to choose a valid JSON file to import into the database. The argument should be the location of the JSON file.
		</td>
	</tr>
	<tr>
		<td>–interactive</td>
	  	<td>Optional: turn on interactive input</td>
	</tr>
	<tr>
		<td>–lf, –list-fields</td>
	  	<td>Optional: if provided along with the –table option this will list out the fields for that table and their type.</td>
	</tr>
	<tr>
		<td>–list-tables, –lt</td>
	  	<td>Optional: if provided will list out the tables this tools knows how to read and/or write to.</td>
	</tr>
	<tr>
		<td>–miseq-sample-sheet</td>
	  	<td>The location of the Miseq Sample Sheet</td>
	</tr>
	<tr>
		<td>–new</td>
	  	<td>Create a new study from scratch. Used instead of miseq-sample-sheet</td>
	</tr>
	<tr>
		<td>–of, –output-file</td>
	  	<td>Optional: if provided along with the –list or –list-tables options this will cause the output list of rows/tables to be written to the file specified rather than stdout.</td>
	</tr>
	<tr>
		<td>–parent-accession</td>
	  	<td>Optional: one or more –parent-accession options can be specified when you create a workflow_run.</td>
	</tr>
	<tr>
		<td>–record</td>
	  	<td>Optional: saves information about the injection in a text file</td>
	</tr>
	<tr>
		<td>–t, –table</td>
	  	<td>Required: the table you are interested in reading or writing.</td>
	</tr>
	<tr>
		<td>–validate-json-sequencer-run</td>
		<td>The JSON file which you wish to validate</td>
	</table>

##Examples

Imports in a new study into the database from a JSON file

	java -jar seqware-distribution/target/seqware-distribution-1.0.11-full.jar --plugin net.sourceforge.seqware.pipeline.plugins.BatchMetadataInjection -- --import-json-sequencer-run input.json

Creates a new study, populates the fields interactively and then exports it as a JSON file
	java -jar seqware-distribution/target/seqware-distribution-1.0.11-full.jar --plugin net.sourceforge.seqware.pipeline.plugins.BatchMetadataInjection -- --new --interactive --export-json-sequencer-run hello.json

Validates the JSON file
	java -jar seqware-distribution/target/seqware-distribution-1.0.11-full.jar --plugin net.sourceforge.seqware.pipeline.plugins.BatchMetadataInjection -- --validate-json-sequencer-run validateMe.json

##Viewing Results

You can view if your data has been inputted correctly by connecting to the webservice via the URL http://host:port/seqware-webservice/{field}, where field is the property which you wish to look at. See <a href="http://seqware.github.com/seqware/">http://seqware.github.com/seqware/</a> for more detail.