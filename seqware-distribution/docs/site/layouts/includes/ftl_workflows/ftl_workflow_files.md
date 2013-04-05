<pre><code>#!xml
&lt;!-- Part 1: Define all jobs --&gt;

   &lt;!-- Provision input file, make link if local file, copy from HTTP/S3 otherwise --&gt;
  &lt;job id=&quot;IDPRE1&quot; namespace=&quot;seqware&quot; name=&quot;java&quot; version=&quot;${java_version}&quot;&gt;
    &lt;argument&gt;
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-distribution-${seqware_version}-full.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --no-metadata      
      --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles
      --
      --input-file ${input_file}
      --output-dir ${data_dir}
    &lt;/argument&gt;

    &lt;profile namespace=&quot;globus&quot; key=&quot;jobtype&quot;&gt;condor&lt;/profile&gt;
    &lt;profile namespace=&quot;globus&quot; key=&quot;count&quot;&gt;1&lt;/profile&gt;
    &lt;profile namespace=&quot;globus&quot; key=&quot;maxmemory&quot;&gt;2000&lt;/profile&gt;
    
    &lt;!-- Prejob to make output directory --&gt;
    &lt;@requires_dir &quot;${data_dir}&quot;/&gt;

  &lt;/job&gt;
</code></pre>
