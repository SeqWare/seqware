<pre>
<code>
# key=input_file:type=file:display=F:file_meta_type=text/plain
input_file=${workflow_bundle_dir}/Workflow_Bundle_${workflow-directory-name}/${version}/data/input.txt
# key=greeting:type=text:display=T:display_name=Greeting
greeting=Testing
# this is just a comment, the output directory is a conventions and used in many workflows to specify a relative output path
output_dir=seqware-results
# the output_prefix is a convention and used to specify the root of the absolute output path or an S3 bucket name
# you should pick a path that is available on all cluster nodes and can be written by your user
output_prefix=./
</code>
</pre>

This is the workflow.ini file and contains all the parameters that a workflow accepts along with default values.

You access these variables in the Java workflow using the <tt>getProperty()</tt> method. When installing the workflow the ini file is parsed and extra metadata about each parameter is examined. This gives the system information about the type of the variable (integer, string, etc) and any default values.

The ini file(s) follow the general pattern of:

<pre>
# comment/specification
key=value
</pre>

To achieve this overloaded role for ini files you need to include hints to ensure the BundleManager that installs workflow bundles has enough information. Here is what the annotation syntax looks like:

        # key=<name>:type=[integer|float|text|pulldown|file]:display=[T|F][:display_name=<name_to_display>][:file_meta_type=<mime_meta_type>][:pulldown_items=<key1>|<value1>;<key2>|<value2>]
        key=default_value

The file_meta_type is only used for type=file.

The pulldown type means that the pulldown_items should be defined as well. This looks like:

        pulldown_items=<key1>|<value1>;<key2>|<value2>

The default value for this will refer to either value1 or value2 above.
If you fail to include a metadata line for a particular key/value then it's assumed to be:

        key=<name>:type=text:display=F

This is convenient since many of the values in an INI file should not be displayed to the end user.


### Common Variables

There are several variables that you will see in various files, including the config ini file and <tt>metadata.xml</tt> file.

* ${date}: a string representing the date the DAX was created, this is always defined so consider this a reserved variable name. 

* ${random}: a randomly generated string, this is always defined so consider this a reserved variable name. 

* ${workflow_bundle_dir}: if this workflow is part of a workflow bundle this variable will be defined and points to the path of the root of the directory this workflow bundle has been expanded to.

* ${workflow_base_dir}: ${workflow_bundle_dir}/Workflow_Bundle_{workflow_name}/{workflow_version}. This is really used in a ton of places since we need a variable that points to the install location for the bundle since we cannot hard code this.

