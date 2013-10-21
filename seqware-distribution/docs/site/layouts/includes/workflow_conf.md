
Each workflow uses a simple INI file to record which variables it accepts,
their types, and default values.  For example:

<pre><code>#!ini
# key=input_file:type=file:display=F:file_meta_type=text/plain
input_file=${workflow_bundle_dir}/Workflow_Bundle_${workflow-directory-name}/${version}/data/input.txt
# key=greeting:type=text:display=T:display_name=Greeting
greeting=Testing
# this is just a comment, the output directory is a conventions and used in many workflows to specify a relative output path
output_dir=seqware-results
# the output_prefix is a convention and used to specify the root of the absolute output path or an S3 bucket name
# you should pick a path that is available on all cluster nodes and can be written by your user
output_prefix=./
</code></pre>

You access these variables in the Java workflow using the
<tt>getProperty()</tt> method. When installing the workflow the ini file is
parsed and extra metadata about each parameter is examined. This gives the
system information about the type of the variable (integer, string, etc) and
any default values.

The ini file(s) follow the general pattern of:

<pre>
# comment/specification
key=value
</pre>

To achieve this overloaded role for ini files you need to include hints to
ensure the BundleManager that installs workflow bundles has enough information.
Here is what the annotation syntax looks like:

    # key=<name>:type=[integer|float|text|pulldown|file]:display=[T|F][:display_name=<name_to_display>][:file_meta_type=<mime_meta_type>][:pulldown_items=<key1>|<value1>;<key2>|<value2>]
    key=default_value

The file_meta_type is only used for type=file.

The pulldown type means that the pulldown_items should be defined as well. This looks like:

    pulldown_items=<key1>|<value1>;<key2>|<value2>

The default value for this will refer to either value1 or value2 above.
If you fail to include a metadata line for a particular key/value then it is assumed to be:

    key=<name>:type=text:display=F

This is convenient since many of the values in an INI file should not be displayed to the end user.

### Required INI Entries

There are (currently) two required entries that all workflows should define
in their ini files. These are related to output file provisioning. In your workflow,
if you produce output files and use the file provisioning mechanism built into
workflows these two entries are used to construct the output location for the
output file.

* output_dir
* output_prefix

For example, if you have a <tt>SqwFile</tt> object can call
<tt>file.setIsOutput(true);</tt> the workflow engine constructs an output path
for this file using the following:

	<output_prefix>/<output_dir>/<file_name>

You can use <tt>s3://bucketname/</tt> or a local path as the prefix.


<p class="warning"><strong>Note:</strong> While the above entries are required, it is STRONGLY suggested that workflow developers no longer rely on them to decide the output path of a provisioned file.  Instead we recommend explicitly providing in the ini file whatever paths you may require, possibly using the variables described below, and then assigning that path to the output file via <code>SqwFile.setOutputPath(String path)</code>.</p>


### INI Variables

The ini files support variables, in the format `$(variable-name}`, that will be replaced when the workflow run is launched. The variable name can refer to another entry in the ini file, or can refer to the following SeqWare generated values:

* `sqw.bundle-dir`: the path to the directory of this workflow's bundle. Support for the legacy version of this variable, `workflow_bundle_dir`, may be removed in a future version.
* `sqw.date`: the current date in ISO 8601 format, e.g., 2013-10-31.
* `sqw.datetime`: the current datetime in ISO 8601 format, e.g., 2013-10-31T16:45:30.  Support for the legacy version of this variable, `date`, may be removed in a future version.
* `sqw.random`: a randomly generated integer from 0 to 2147483647.  Support for the legacy version of this variable, `random`, may be removed in a future version.
* `sqw.timestamp`: the current number of milliseconds since January 1, 1970.
* `sqw.uuid`: a randomly generated <a href="http://en.wikipedia.org/wiki/Universally_unique_identifier#Version_4_.28random.29">universally unique identifier</a>.

Each instance of the above `sqw.*` variables in an ini file will be replaced with a separately resolved value, e.g., multiple instances of `${sqw.uuid}` will each resolve to different values. If you desire to reuse the same generated value, do somthing akin to the following:

<pre><code>#!ini
dirname=output
filename=${sqw.random}
text_file=${dirname}/${filename}.txt
json_file=${dirname}/${filename}.json
</code></pre>

Thus if `filename` resolved to a value of `12345`, then `text_file` will have a value of `output/12345.txt` and `json_file` will have a value of `output/12345.json`.

