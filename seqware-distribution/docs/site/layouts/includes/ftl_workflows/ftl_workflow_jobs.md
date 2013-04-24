<pre><code>#!xml
&lt;!-- Part 1: Define all jobs --&gt;

&lt;#if using_helloworldexample_module &gt;
  &lt;!-- First HelloWorld job, this uses the sample module in the bundle. --&gt;
  &lt;#assign algo = &quot;job1&quot;/&gt;
  &lt;job id=&quot;ID001&quot; namespace=&quot;seqware&quot; name=&quot;java&quot; version=&quot;${java_version}&quot;&gt;
    &lt;argument&gt;
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/bin:${workflow_bundle_dir}/${workflow_name}/lib/seqware-distribution-${seqware_version}-full.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
      &lt;#list parentAccessions?split(&quot;,&quot;) as pa&gt;
      --metadata-parent-accession ${pa}
      &lt;/#list&gt;
      --metadata-processing-accession-file ${data_dir}/${algo}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-accession ${workflow_run_accession}
      --module net.sourceforge.seqware.module.HelloWorldExample
      --
      --greeting &quot;${greeting}, job 1&quot;
      --repeat 2
      --input-file ${data_dir}/${basename}
      --output-file ${data_dir}/job1.txt
      --cat-binary-path ${workflow_bundle_dir}/${workflow_name}/bin/gnu-coreutils-5.67/cat
      --echo-binary-path ${workflow_bundle_dir}/${workflow_name}/bin/gnu-coreutils-5.67/echo
    &lt;/argument&gt;

    &lt;profile namespace=&quot;globus&quot; key=&quot;jobtype&quot;&gt;condor&lt;/profile&gt;
    &lt;profile namespace=&quot;globus&quot; key=&quot;count&quot;&gt;1&lt;/profile&gt;
    &lt;profile namespace=&quot;globus&quot; key=&quot;maxmemory&quot;&gt;2000&lt;/profile&gt;

    &lt;!-- Prejob to make output directory --&gt;
    &lt;@requires_dir &quot;${data_dir}&quot;/&gt;

  &lt;/job&gt;

  &lt;!-- Second HelloWorld job, this uses the sample module in the bundle. --&gt;
  &lt;#assign parentAlgo = &quot;job1&quot;/&gt;
  &lt;#assign algo = &quot;job2&quot;/&gt;
  &lt;job id=&quot;ID002&quot; namespace=&quot;seqware&quot; name=&quot;java&quot; version=&quot;${java_version}&quot;&gt;
    &lt;argument&gt;
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/bin:${workflow_bundle_dir}/${workflow_name}/lib/seqware-distribution-${seqware_version}-full.jar
      net.sourceforge.seqware.pipeline.runner.Runner    
      --${metadata}  
      --metadata-parent-accession-file ${data_dir}/${parentAlgo}_accession
      --metadata-processing-accession-file ${data_dir}/${algo}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-ancestor-accession ${workflow_run_accession}    
      --module net.sourceforge.seqware.module.HelloWorldExample
      --
      --greeting &quot;${greeting}, job 2&quot;
      --repeat 4
      --input-file ${data_dir}/job1.txt
      --output-file ${data_dir}/${basename}.hello
      --cat-binary-path ${workflow_bundle_dir}/${workflow_name}/bin/gnu-coreutils-5.67/cat
      --echo-binary-path ${workflow_bundle_dir}/${workflow_name}/bin/gnu-coreutils-5.67/echo
    &lt;/argument&gt;

    &lt;profile namespace=&quot;globus&quot; key=&quot;jobtype&quot;&gt;condor&lt;/profile&gt;
    &lt;profile namespace=&quot;globus&quot; key=&quot;count&quot;&gt;1&lt;/profile&gt;
    &lt;profile namespace=&quot;globus&quot; key=&quot;maxmemory&quot;&gt;2000&lt;/profile&gt;

    &lt;!-- Prejob to make output directory --&gt;
    &lt;@requires_dir &quot;${output_dir}/seqware-${seqware_version}_HelloWorld-${workflow_version}/${random}&quot;/&gt;

  &lt;/job&gt;
&lt;/#if&gt;

  &lt;!-- Third HelloWorld job, this uses the GenericCommandRunner to just use a tool on the command line. --&gt;
  &lt;#assign parentAlgo = &quot;job2&quot;/&gt;
  &lt;#assign algo = &quot;job3&quot;/&gt;
  &lt;job id=&quot;ID003&quot; namespace=&quot;seqware&quot; name=&quot;java&quot; version=&quot;${java_version}&quot;&gt;
    &lt;argument&gt;
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-distribution-${seqware_version}-full.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}  
      --metadata-output-file-prefix ${output_prefix}
      &lt;#if using_helloworldexample_module &gt;
      --metadata-workflow-run-ancestor-accession ${workflow_run_accession} 
      --metadata-parent-accession-file ${data_dir}/${parentAlgo}_accession
      --metadata-processing-accession-file ${data_dir}/${algo}_accession
      &lt;#else&gt;
      --metadata-workflow-run-accession ${workflow_run_accession} 
      &lt;#list parentAccessions?split(&quot;,&quot;) as pa&gt;
      --metadata-parent-accession ${pa}
      &lt;/#list&gt;
      --metadata-processing-accession-file ${data_dir}/${algo}_accession
      &lt;/#if&gt;   
      --module net.sourceforge.seqware.pipeline.modules.GenericCommandRunner
      --
      --gcr-algorithm HelloWorldGenericCommandRunner
      --gcr-output-file HelloWorldGenericCommandRunner::text/plain::${data_dir}/${basename}.hello
      --gcr-command ${workflow_bundle_dir}/${workflow_name}/bin/gnu-coreutils-5.67/echo &quot;${greeting}, job3&quot; &gt;&gt; ${data_dir}/${basename}.hello
    &lt;/argument&gt;

    &lt;profile namespace=&quot;globus&quot; key=&quot;jobtype&quot;&gt;condor&lt;/profile&gt;
    &lt;profile namespace=&quot;globus&quot; key=&quot;count&quot;&gt;1&lt;/profile&gt;
    &lt;profile namespace=&quot;globus&quot; key=&quot;maxmemory&quot;&gt;2000&lt;/profile&gt;

  &lt;/job&gt;

  &lt;!-- Provision output file to either local dir or S3 --&gt;
  &lt;job id=&quot;IDPOST1&quot; namespace=&quot;seqware&quot; name=&quot;java&quot; version=&quot;${java_version}&quot;&gt;
    &lt;argument&gt;
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-distribution-${seqware_version}-full.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --no-metadata      
      --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles
      --
      --force-copy
      --input-file ${data_dir}/${basename}.hello
      &lt;#if manual_output &gt;
      --output-dir ${output_prefix}${output_dir}/seqware-${seqware_version}_HelloWorld-${workflow_version}
      &lt;#else&gt;
      --output-dir ${output_prefix}${output_dir}/seqware-${seqware_version}_HelloWorld-${workflow_version}/${random}
      &lt;/#if&gt;
    &lt;/argument&gt;

    &lt;profile namespace=&quot;globus&quot; key=&quot;jobtype&quot;&gt;condor&lt;/profile&gt;
    &lt;profile namespace=&quot;globus&quot; key=&quot;count&quot;&gt;1&lt;/profile&gt;
    &lt;profile namespace=&quot;globus&quot; key=&quot;maxmemory&quot;&gt;2000&lt;/profile&gt;

  &lt;/job&gt;

&lt;!-- End of Job Definitions --&gt;
</code></pre>
