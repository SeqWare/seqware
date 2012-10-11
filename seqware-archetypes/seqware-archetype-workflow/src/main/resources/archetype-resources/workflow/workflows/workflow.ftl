<?xml version="1.0" encoding="UTF-8"?>
<adag xmlns="http://pegasus.isi.edu/schema/DAX" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://pegasus.isi.edu/schema/DAX http://pegasus.isi.edu/schema/dax-3.2.xsd" version="3.2" count="1" index="0" name="${workflow-name}">

<!--

This is a sample HelloWorld workflow. Use it as a template to build your own workflow.

-->

<!-- the directory structure inside the bundle -->
<#assign workflow_name = "Workflow_Bundle_${workflow-directory-name}/${version}"/>

<!-- MACRO: to create a mkdir pre job and stage mkdir binary -->
<#macro requires_dir dir>
  <profile namespace="env" key="GRIDSTART_PREJOB">/${workflow_bundle_dir}/${workflow_name}/bin/globus/pegasus-dirmanager -c -d ${dir}</profile>
</#macro>

<!-- VARS -->
<#-- workflow and seqware versions -->
<#assign seqware_version = "${seqware-version}"/>
<#assign workflow_version = "${workflow-version}"/>
<#assign java_version = "1.6.0"/>
<#assign perl_version = "5.14.1"/>
<#-- make sure it is a string -->
<#assign parentAccessions = "${parent_accessions}"/>
<#-- Set relative paths for files within the run-->
<#assign bin_dir = "bin"/>
<#assign data_dir = "data"/>
<#assign lib_dir = "lib"/>

<!-- Update When Using HelloWorldExample Module -->
<!--
  By default (with 'using_helloworldexample_module' set to false) this workflow has 4 steps
  and uses the built in GenericCommandRunner to echo the hello world input text into an output
  file.
  
  To explore creating a custom module change the value of 'using_helloworldexample_module' to
  true and provide a module with the expected name.
  
  See:
  http://sourceforge.net/apps/mediawiki/seqware/index.php?title=Creating_Workflow_Bundles_and_Modules_Using_Maven_Archetypes
  For full details. A summary is included below.
  
  Before setting to 'true' create the default module using the seqware-archetype-module.
  $ mvn archetype:generate (choose the seqware-archetype-module option)
  'groupId': : net.sourceforge.seqware
  'artifactId': : module-helloworld-example
  'version': 1.0
  'package': net.sourceforge.seqware: net.sourceforge.seqware.module
  
  Add to the pom.xml:
  <dependency>
    <groupid>net.sourceforge.seqware</groupid>
    <artifactId>module-helloworld-example</artifactId>
    <version>1.0</version>
  </dependency>  
-->
<#assign using_helloworldexample_module = false/>

<!-- BASE FILE NAME -->
<#-- Set the basename from input file name -->
<#list input_file?split("/") as tmp>
  <#assign basename = tmp/>
</#list>

<!-- EXECUTABLES INCLUDED WITH BUNDLE -->
<executable namespace="seqware" name="java" version="${java_version}" 
            arch="x86_64" os="linux" installed="true" >
  <!-- the path to the tool that actually runs a given module -->
  <pfn url="file:///${workflow_bundle_dir}/${workflow_name}/bin/jre1.6.0_29/bin/java" site="${seqware_cluster}"/>
</executable>

<executable namespace="seqware" name="perl" version="${perl_version}" 
            arch="x86_64" os="linux" installed="true" >
  <!-- the path to the tool that actually runs a given module -->
  <pfn url="file:///${workflow_bundle_dir}/${workflow_name}/bin/perl-5.14.1/perl" site="${seqware_cluster}"/>
</executable>

<executable namespace="pegasus" name="dirmanager" version="1" 
            arch="x86_64" os="linux" installed="true" >
  <!-- the path to the tool that actually runs a given module -->
  <pfn url="file:///${workflow_bundle_dir}/${workflow_name}/bin/globus/pegasus-dirmanager" site="${seqware_cluster}"/>     
</executable>


<!-- Part 1: Define all jobs -->

   <!-- Provision input file, make link if local file, copy from HTTP/S3 otherwise -->
  <job id="IDPRE2" namespace="seqware" name="java" version="${java_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-distribution-${seqware_version}-full.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --no-metadata      
      --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles
      --
      --input-file ${input_file}
      --output-dir ${data_dir}
    </argument>

    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">2000</profile>

  </job>

<#if using_helloworldexample_module >
  <!-- First HelloWorld job, this uses the sample module in the bundle. -->
  <#assign algo = "job1"/>
  <job id="ID001" namespace="seqware" name="java" version="${java_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/bin:${workflow_bundle_dir}/${workflow_name}/lib/seqware-distribution-${seqware_version}-full.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
      <#list parentAccessions?split(",") as pa>
      --metadata-parent-accession ${pa}
      </#list>
      --metadata-processing-accession-file ${data_dir}/${algo}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-accession ${workflow_run_accession}
      --module net.sourceforge.seqware.module.HelloWorldExample
      --
      --greeting "${greeting}, job 1"
      --repeat 2
      --input-file ${data_dir}/${basename}
      --output-file ${data_dir}/job1.txt
      --cat-binary-path ${workflow_bundle_dir}/${workflow_name}/bin/gnu-coreutils-5.67/cat
      --echo-binary-path ${workflow_bundle_dir}/${workflow_name}/bin/gnu-coreutils-5.67/echo
    </argument>

    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">2000</profile>

    <!-- Prejob to make output directory -->
    <@requires_dir "${data_dir}"/>

  </job>

  <!-- Second HelloWorld job, this uses the sample module in the bundle. -->
  <#assign parentAlgo = "job1"/>
  <#assign algo = "job2"/>
  <job id="ID002" namespace="seqware" name="java" version="${java_version}">
    <argument>
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
      --greeting "${greeting}, job 2"
      --repeat 4
      --input-file ${data_dir}/job1.txt
      --output-file ${data_dir}/${basename}.hello
      --cat-binary-path ${workflow_bundle_dir}/${workflow_name}/bin/gnu-coreutils-5.67/cat
      --echo-binary-path ${workflow_bundle_dir}/${workflow_name}/bin/gnu-coreutils-5.67/echo
    </argument>

    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">2000</profile>

    <!-- Prejob to make output directory -->
    <@requires_dir "${output_dir}/seqware-${seqware_version}_HelloWorld-${workflow_version}/${random}"/>

  </job>
</#if>

  <!-- Third HelloWorld job, this uses the GenericCommandRunner to just use a tool on the command line. -->
  <#assign parentAlgo = "job2"/>
  <#assign algo = "job3"/>
  <job id="ID003" namespace="seqware" name="java" version="${java_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-distribution-${seqware_version}-full.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}  
      --metadata-parent-accession-file ${data_dir}/${parentAlgo}_accession
      --metadata-processing-accession-file ${data_dir}/${algo}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-ancestor-accession ${workflow_run_accession}    
      --module net.sourceforge.seqware.pipeline.modules.GenericCommandRunner
      --
      --gcr-algorithm HelloWorldGenericCommandRunner
      --gcr-output-file HelloWorldGenericCommandRunner::text/plain::${data_dir}/${basename}.hello
      --gcr-command ${workflow_bundle_dir}/${workflow_name}/bin/gnu-coreutils-5.67/echo "${greeting}, job3" >> ${data_dir}/${basename}.hello
    </argument>

    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">2000</profile>

  </job>

  <!-- Provision output file to either local dir or S3 -->
  <job id="IDPOST1" namespace="seqware" name="java" version="${java_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-distribution-${seqware_version}-full.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --no-metadata      
      --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles
      --
      --force-copy
      --input-file ${data_dir}/${basename}.hello
      --output-dir ${output_prefix}${output_dir}/seqware-${seqware_version}_HelloWorld-${workflow_version}/${random}
    </argument>

    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">2000</profile>

  </job>

<!-- End of Job Definitions -->

<!-- Part 2: list of control-flow dependencies -->

  <!-- Define task group dependencies -->
<#if using_helloworldexample_module >
  <child ref="ID001">
    <parent ref="IDPRE2"/>
  </child>
  <child ref="ID002">
    <parent ref="ID001"/>
  </child>
  <child ref="ID003">
     <parent ref="ID002"/>
   </child>
  <child ref="IDPOST1">
    <parent ref="ID003"/>
  </child>
<#else>
  <child ref="ID003">
     <parent ref="IDPRE2"/>
   </child>
  <child ref="IDPOST1">
    <parent ref="ID003"/>
  </child>
</#if>

<!-- End of Dependencies -->

</adag>
