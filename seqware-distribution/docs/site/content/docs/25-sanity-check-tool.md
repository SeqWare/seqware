---

title:                 "Sanity Check"
toc_includes_sections: true
markdown:              basic
is_dynamic:		true

---

Seqware Sanity Check is a tool developed in order to see if the seqware environment is set up correctly in a given system. It runs preliminary checks and will return an exit code of 0 if all the checks have passed. The tool also contains different parameters in order to specify certain tests to be included or not.

##Requirements

In order to run the Sanity Check tool, you must have the following available to you:

* SeqWare Distribution JAR (1.0.14 or higher)
* Seqware Sanity Check JAR (1.0.14 or higher)
* SeqWare settings file set up to contact the SeqWare Web service (contact your local SeqWare admin to get the path)

##Command line parameters

You can annotate the following attributes:
<table><tr><th>
Command-line option</th>	<th>Description</th></tr>
<tr><td>
--master, -m</td>	<td>Master mode. Use this if you are running on a master node. Will do more admin testing if included.</td></tr><tr><td>
--tutorial, -t</td><td>Runs through the seqware tutorial as well.</td></tr><tr><td>
--help, -h	</td>	<td>Displays this list of parameters.</td></tr>
</table>


##Examples

Running the sanity tool on a user node.

	java -jar seqware-sanity-check/target/seqware-sanity-check-1.0.14-SNAPSHOT-jar-with-dependencies.jar

Running the tool on a master node.

	java -jar seqware-sanity-check/target/seqware-sanity-check-1.0.14-SNAPSHOT-jar-with-dependencies.jar --master

Running the tutorial as well

	java -jar seqware-sanity-check/target/seqware-sanity-check-1.0.14-SNAPSHOT-jar-with-dependencies.jar --master --tutorial

Help

	java -jar seqware-sanity-check/target/seqware-sanity-check-1.0.14-SNAPSHOT-jar-with-dependencies.jar --help


##Results

Once all the tests are completed, an exit code will be returned. If the exit code is non-zero, then one of the checks specified has failed. The stacktrace will also be displayed if an error occurs while running the checks.

