The workflow manifest (<tt>metadata.xml</tt>) includes the workflow name,
version, description, test command, and enough information so that the SeqWare
tools can test, execute, and install the workflow. Here is an example from the
MyHelloWorld workflow:

<pre><code>#!xml
&lt;bundle version="1.0-SNAPSHOT"&gt;
  &lt;workflow name="MyHelloWorld" version="1.0-SNAPSHOT" seqware_version="<%= seqware_release_version %>"
  basedir="${workflow_bundle_dir}/Workflow_Bundle_MyHelloWorld/1.0-SNAPSHOT"&gt;
    &lt;description&gt;Add a description of the workflow here.&lt;/description&gt;
    &lt;workflow_class path="${workflow_bundle_dir}/Workflow_Bundle_MyHelloWorld/1.0-SNAPSHOT/classes/com/github/seqware/WorkflowClient.java"/&gt;
    &lt;config path="${workflow_bundle_dir}/Workflow_Bundle_MyHelloWorld/1.0-SNAPSHOT/config/workflow.ini"/&gt;
    &lt;requirements compute="single" memory="20M" network="local"  workflow_engine="Pegasus,Oozie" workflow_type="java"/&gt;
  &lt;/workflow&gt;
&lt;/bundle&gt;
</code></pre>

As mentioned above, you can edit the description and workflow name in the workflow.properties file.
