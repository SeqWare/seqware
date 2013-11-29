The workflow manifest (<tt>metadata.xml</tt>) includes the workflow name,
version, description, test command, and enough information so that the SeqWare
tools can test, execute, and install the workflow. Here is an example from the
HelloWorld workflow:

<pre><code>#!xml
&lt;bundle version="1.0"&gt;
  &lt;workflow name="HelloWorld" version="1.0" seqware_version="<%= seqware_release_version %>"
  basedir="${workflow_bundle_dir}/Workflow_Bundle_HelloWorld/1.0"&gt;
    &lt;description&gt;Add a description of the workflow here.&lt;/description&gt;
    &lt;workflow_class path="${workflow_bundle_dir}/Workflow_Bundle_HelloWorld/1.0/classes/com/github/seqware/HelloWorldWorkflow.java"/&gt;
    &lt;config path="${workflow_bundle_dir}/Workflow_Bundle_HelloWorld/1.0/config/HelloWorldWorkflow.ini"/&gt;
    &lt;requirements compute="single" memory="20M" network="local"  workflow_engine="Pegasus,Oozie" workflow_type="java"/&gt;
  &lt;/workflow&gt;
&lt;/bundle&gt;
</code></pre>

As mentioned above, you can edit the description and workflow name in the workflow.properties file.
