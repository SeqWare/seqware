<pre><code>#!xml
&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
&lt;adag xmlns=&quot;http://pegasus.isi.edu/schema/DAX&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; xsi:schemaLocation=&quot;http://pegasus.isi.edu/schema/DAX http://pegasus.isi.edu/schema/dax-3.2.xsd&quot; version=&quot;3.2&quot; count=&quot;1&quot; index=&quot;0&quot; name=&quot;HelloWorld&quot;&gt;

&lt;!--

This is a sample HelloWorld workflow. Use it as a template to build your own workflow.

--&gt;

&lt;!-- the directory structure inside the bundle --&gt;
&lt;#assign workflow_name = &quot;Workflow_Bundle_HelloWorld/1.0&quot;/&gt;

&lt;!-- MACRO: to create a mkdir pre job and stage mkdir binary --&gt;
&lt;#macro requires_dir dir&gt;
  &lt;profile namespace=&quot;env&quot; key=&quot;GRIDSTART_PREJOB&quot;&gt;/${workflow_bundle_dir}/${workflow_name}/bin/globus/pegasus-dirmanager -c -d ${dir}&lt;/profile&gt;
&lt;/#macro&gt;

&lt;!-- VARS --&gt;
&lt;#-- workflow and seqware versions --&gt;
&lt;#assign seqware_version = &quot;0.13.6.5&quot;/&gt;
&lt;#assign workflow_version = &quot;1.0&quot;/&gt;
&lt;#assign java_version = &quot;1.6.0&quot;/&gt;
&lt;#assign perl_version = &quot;5.14.1&quot;/&gt;
&lt;#-- make sure it is a string --&gt;
&lt;#assign parentAccessions = &quot;${parent_accessions}&quot;/&gt;
&lt;#-- Set relative paths for files within the run--&gt;
&lt;#assign bin_dir = &quot;bin&quot;/&gt;
&lt;#assign data_dir = &quot;data&quot;/&gt;
&lt;#assign lib_dir = &quot;lib&quot;/&gt;

&lt;!-- Update When Using HelloWorldExample Module --&gt;
&lt;!--
  By default (with &apos;using_helloworldexample_module&apos; set to false) this workflow has 4 steps
  and uses the built in GenericCommandRunner to echo the hello world input text into an output
  file.
  
  To explore creating a custom module change the value of &apos;using_helloworldexample_module&apos; to
  true and provide a module with the expected name.
  
  See:
  http://sourceforge.net/apps/mediawiki/seqware/index.php?title=Creating_Workflow_Bundles_and_Modules_Using_Maven_Archetypes
  For full details. A summary is included below.
  
  Before setting to &apos;true&apos; create the default module using the seqware-archetype-module.
  $ mvn archetype:generate (choose the seqware-archetype-module option)
  &apos;groupId&apos;: : net.sourceforge.seqware
  &apos;artifactId&apos;: : module-helloworld-example
  &apos;version&apos;: 1.0
  &apos;package&apos;: net.sourceforge.seqware: net.sourceforge.seqware.module
  
  Add to the pom.xml:
  &lt;dependency&gt;
    &lt;groupId&gt;net.sourceforge.seqware&lt;/groupId&gt;
    &lt;artifactId&gt;module-helloworld-example&lt;/artifactId&gt;
    &lt;version&gt;1.0&lt;/version&gt;
  &lt;/dependency&gt;  
--&gt;
&lt;#assign using_helloworldexample_module = false/&gt;
&lt;#assign manual_output = true/&gt;

&lt;!-- BASE FILE NAME --&gt;
&lt;#-- Set the basename from input file name --&gt;
&lt;#list input_file?split(&quot;/&quot;) as tmp&gt;
  &lt;#assign basename = tmp/&gt;
&lt;/#list&gt;

&lt;!-- EXECUTABLES INCLUDED WITH BUNDLE --&gt;
&lt;executable namespace=&quot;seqware&quot; name=&quot;java&quot; version=&quot;${java_version}&quot; 
            arch=&quot;x86_64&quot; os=&quot;linux&quot; installed=&quot;true&quot; &gt;
  &lt;!-- the path to the tool that actually runs a given module --&gt;
  &lt;pfn url=&quot;file:///${workflow_bundle_dir}/${workflow_name}/bin/jre1.6.0_29/bin/java&quot; site=&quot;${seqware_cluster}&quot;/&gt;
&lt;/executable&gt;

&lt;executable namespace=&quot;seqware&quot; name=&quot;perl&quot; version=&quot;${perl_version}&quot; 
            arch=&quot;x86_64&quot; os=&quot;linux&quot; installed=&quot;true&quot; &gt;
  &lt;!-- the path to the tool that actually runs a given module --&gt;
  &lt;pfn url=&quot;file:///${workflow_bundle_dir}/${workflow_name}/bin/perl-5.14.1/perl&quot; site=&quot;${seqware_cluster}&quot;/&gt;
&lt;/executable&gt;

&lt;executable namespace=&quot;pegasus&quot; name=&quot;dirmanager&quot; version=&quot;1&quot; 
            arch=&quot;x86_64&quot; os=&quot;linux&quot; installed=&quot;true&quot; &gt;
  &lt;!-- the path to the tool that actually runs a given module --&gt;
  &lt;pfn url=&quot;file:///${workflow_bundle_dir}/${workflow_name}/bin/globus/pegasus-dirmanager&quot; site=&quot;${seqware_cluster}&quot;/&gt;     
&lt;/executable&gt;


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

&lt;!-- Part 2: list of control-flow dependencies --&gt;

  &lt;!-- Define task group dependencies --&gt;
&lt;#if using_helloworldexample_module &gt;
  &lt;child ref=&quot;ID001&quot;&gt;
    &lt;parent ref=&quot;IDPRE1&quot;/&gt;
  &lt;/child&gt;
  &lt;child ref=&quot;ID002&quot;&gt;
    &lt;parent ref=&quot;ID001&quot;/&gt;
  &lt;/child&gt;
  &lt;child ref=&quot;ID003&quot;&gt;
     &lt;parent ref=&quot;ID002&quot;/&gt;
   &lt;/child&gt;
  &lt;child ref=&quot;IDPOST1&quot;&gt;
    &lt;parent ref=&quot;ID003&quot;/&gt;
  &lt;/child&gt;
&lt;#else&gt;
  &lt;child ref=&quot;ID003&quot;&gt;
     &lt;parent ref=&quot;IDPRE1&quot;/&gt;
   &lt;/child&gt;
  &lt;child ref=&quot;IDPOST1&quot;&gt;
    &lt;parent ref=&quot;ID003&quot;/&gt;
  &lt;/child&gt;
&lt;/#if&gt;

&lt;!-- End of Dependencies --&gt;

&lt;/adag&gt;
</code></pre>
