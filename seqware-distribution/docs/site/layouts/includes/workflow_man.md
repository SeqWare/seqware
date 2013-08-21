The workflow manifest (<tt>metadata.xml</tt>) includes the workflow name,
version, description, test command, and enough information so that the SeqWare
tools can test, execute, and install the workflow. Here is an example from the
MyHelloWorld workflow:

<pre><code>#!xml
&lt;bundle version=&quot;1.0-SNAPSHOT&quot;&gt;
  &lt;workflow name=&quot;MyHelloWorld&quot; version=&quot;1.0-SNAPSHOT&quot; seqware_version=&quot;<%= seqware_release_version %>&quot;
  basedir=&quot;${workflow_bundle_dir}/Workflow_Bundle_MyHelloWorld/1.0-SNAPSHOT&quot;&gt;
    &lt;description&gt;Add a description of the workflow here.&lt;/description&gt;
    &lt;test command=&quot;java -jar ${workflow_bundle_dir}/Workflow_Bundle_MyHelloWorld/1.0-SNAPSHOT/lib/seqware-distribution-<%= seqware_release_version %>-full.jar --plugin net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --no-metadata --wait --provisioned-bundle-dir ${workflow_bundle_dir} --workflow MyHelloWorld --version 1.0-SNAPSHOT --ini-files ${workflow_bundle_dir}/Workflow_Bundle_MyHelloWorld/1.0-SNAPSHOT/config/workflow.ini &quot;/&gt;
    &lt;workflow_command command=&quot;java -jar ${workflow_bundle_dir}/Workflow_Bundle_MyHelloWorld/1.0-SNAPSHOT/lib/seqware-distribution-<%= seqware_release_version %>-full.jar --plugin net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --bundle ${workflow_bundle_dir} --workflow MyHelloWorld --version 1.0-SNAPSHOT &quot;/&gt;
    &lt;workflow_template path=&quot;&quot;/&gt;
    &lt;workflow_class path=&quot;${workflow_bundle_dir}/Workflow_Bundle_MyHelloWorld/1.0-SNAPSHOT/classes/com/github/seqware/WorkflowClient.java&quot;/&gt;
    &lt;config path=&quot;${workflow_bundle_dir}/Workflow_Bundle_MyHelloWorld/1.0-SNAPSHOT/config/workflow.ini&quot;/&gt;
    &lt;build command=&quot;ant -f ${workflow_bundle_dir}/Workflow_Bundle_MyHelloWorld/1.0-SNAPSHOT/build.xml&quot;/&gt;
    &lt;requirements compute=&quot;single&quot; memory=&quot;20M&quot; network=&quot;local&quot;  workflow_engine=&quot;Pegasus,Oozie&quot; workflow_type=&quot;java&quot;/&gt;
  &lt;/workflow&gt;
&lt;/bundle&gt;
</code></pre>

As mentioned above, you can edit the description and workflow name in the workflow.properties file.
