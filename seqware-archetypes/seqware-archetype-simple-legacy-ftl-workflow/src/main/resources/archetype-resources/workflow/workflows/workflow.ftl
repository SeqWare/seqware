<?xml version="1.0" encoding="UTF-8"?>
<adag xmlns="http://pegasus.isi.edu/schema/DAX" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://pegasus.isi.edu/schema/DAX http://pegasus.isi.edu/schema/dax-3.2.xsd" version="3.2" count="1" index="0" name="${workflow-name}">

<!--

This is an extremely simple HelloWorld workflow. Use it as a template to build
your own workflow.  While this workflow is really simple and only has one step,
it does show you how to provision an input file, process it somehow to produce
and output, save the output file, and save metadata back to the database. That
should be almost everything you need to be build more complex workflows.

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
  <job id="IDPRE1" namespace="seqware" name="java" version="${java_version}">
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

    <!-- Prejob to make output directory -->
    <@requires_dir "${data_dir}"/>

  </job>

  <!-- HelloWorld job, just calls a simple script. -->
  <#assign algo = "job1"/>
  <job id="ID001" namespace="seqware" name="java" version="${java_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/bin:${workflow_bundle_dir}/${workflow_name}/lib/seqware-distribution-${seqware_version}-full.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --no-metadata
      --module net.sourceforge.seqware.pipeline.modules.GenericCommandRunner
      --
      --gcr-command ${workflow_bundle_dir}/${workflow_name}/bin/hello.sh ${data_dir}/${basename} ${data_dir}/${basename}.hello
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

  <!-- Save output locations to MetaDB -->
  <#assign algo = "jobpost2"/>
  <job id="IDPOST2" namespace="seqware" name="java" version="${java_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-distribution-${seqware_version}-full.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
      <#list parentAccessions?split(",") as pa>
      --metadata-parent-accession ${pa}
      </#list>
      --metadata-processing-accession-file ${data_dir}/${algo}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-accession ${workflow_run_accession}
      --module net.sourceforge.seqware.pipeline.modules.GenericMetadataSaver
      --
      --gms-algorithm ComprehensiveMitochondrialGenomeAnalysisSummary
      --gms-suppress-output-file-check
      --gms-output-file HelloWorld::text/plain::${output_dir}/seqware-${seqware_version}_HelloWorld-${workflow_version}/${random}/${basename}.hello
    </argument>

    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">2000</profile>

  </job>




<!-- End of Job Definitions -->

<!-- Part 2: list of control-flow dependencies -->

  <!-- Define task group dependencies -->
  <child ref="ID001">
    <parent ref="IDPRE1"/>
  </child>
  <child ref="IDPOST1">
    <parent ref="ID001"/>
  </child>
  <child ref="IDPOST2">
    <parent ref="IDPOST1"/>
  </child>

<!-- End of Dependencies -->

</adag>
