<?xml version="1.0" encoding="UTF-8"?>
<adag xmlns="http://pegasus.isi.edu/schema/DAX" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://pegasus.isi.edu/schema/DAX http://pegasus.isi.edu/schema/dax-3.2.xsd" version="3.2" count="1" index="0" name="GATKRecalibrationAndVariantCalling_1.3.16">

<#--
DESCRIPTION:

This workflow is designed to take a BAM file, break it down by chromosome, perform realignment, recalibration,
duplicate flagging, and variant calling for small indels and SNVs. The result is a VCF file for SNVs and indels both filtered
and un-filtered.

This workflow is designed to work with GATK version 1.3.16.

Docs: 

* http://www.broadinstitute.org/gsa/wiki/index.php/Best_Practice_Variant_Detection_with_the_GATK_v2
* http://www.broadinstitute.org/gsa/wiki/index.php/The_Genome_Analysis_Toolkit

TODO: 

* need to add WGS support where needed, see ./postProcessing/gatk/oicr_gatk_helper.sh
* need to add annotation steps?
-->

<!-- the relative path -->
<#assign workflow_name = "GATKRecalibrationAndVariantCalling/1.x.x"/>

<!-- MACRO: to create a mkdir pre job and stage mkdir binary -->
<#macro requires_dir dir>
  <profile namespace="env" key="GRIDSTART_PREJOB">/${workflow_bundle_dir}/${workflow_name}/bin/pegasus-dirmanager -c -d ${dir}</profile>
</#macro>

<!-- VARS -->
<#-- Set seqware version -->
<#assign seqware_version = "0.10.0"/>
<#assign workflow_version = "1.3.16"/>
<#assign inputs="${bam_inputs}"/>
<#-- Set relative paths for files within the run-->
<#assign bin_dir = "bin"/>
<#assign data_dir = "data"/>
<#assign accession_dir = "accessions"/>
<#assign tmp_dir = "tmp"/>
<!-- parent accessions -->
<#assign parentAccessions = "${parent_accession}"/>

<!-- EXECUTABLES INCLUDED WITH BUNDLE -->
<executable namespace="seqware" name="runner" version="${seqware_version}" 
            arch="x86_64" os="linux" installed="true" >
  <!-- the path to the tool that actually runs a given module -->
  <pfn url="file:///${workflow_bundle_dir}/${workflow_name}/bin/seqware-java-wrapper.sh" site="${seqware_cluster}"/>
</executable>

<executable namespace="pegasus" name="dirmanager" version="${seqware_version}" 
            arch="x86_64" os="linux" installed="true" >
  <!-- the path to the tool that creates directories -->
  <pfn url="file:///${workflow_bundle_dir}/${workflow_name}/bin/pegasus-dirmanager" site="${seqware_cluster}"/>
</executable>



<!-- PROVISION -->

<!-- Part 1: Job definitions -->

  <!-- Pre Job: make directories -->
  <job id="IDPRE0.1" namespace="pegasus" name="dirmanager" version="${seqware_version}">
    <argument>
      -c -d ${bin_dir}
    </argument>
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">500</profile>
  </job>

  <!-- Pre Job: make directories -->
  <job id="IDPRE0.2" namespace="pegasus" name="dirmanager" version="${seqware_version}">
    <argument>
      -c -d ${data_dir}
    </argument>
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">500</profile>
  </job>

  <!-- Pre Job: make directories -->
  <job id="IDPRE0.3" namespace="pegasus" name="dirmanager" version="${seqware_version}">
    <argument>
      -c -d ${accession_dir}
    </argument>
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">500</profile>
  </job>

  <!-- Pre Job: make directories -->
  <job id="IDPRE0.4" namespace="pegasus" name="dirmanager" version="${seqware_version}">
    <argument>
      -c -d ${tmp_dir}
    </argument>
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">500</profile>
  </job>

  <!-- Jobs for making data and scripts available -->

  <!-- Pre Job: makes seqware perl scripts available -->
  <job id="IDPRE1" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --no-metadata
      --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionDependenciesBundle
      --
      --input-file ${workflow_bundle_dir}/${workflow_name}/dependencies/noarch/seqware-pipeline-perl-bin.noarch.zip
      --output-dir ${bin_dir}
    </argument>

    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">2000</profile>

  </job>

  <!-- Pre Job: makes annovar scripts available-->
  <job id="IDPRE2" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --no-metadata
      --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionDependenciesBundle
      --
      --input-file ${workflow_bundle_dir}/${workflow_name}/dependencies/noarch/annovar-20110506.noarch.zip
      --output-dir ${bin_dir}
    </argument>

    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">2000</profile>

  </job>

<!-- TODO: Update to latest version -->

  <!-- Pre Job: makes GATK available -->
  <job id="IDPRE3" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --no-metadata
      --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionDependenciesBundle
      --
      --input-file ${workflow_bundle_dir}/${workflow_name}/dependencies/noarch/GenomeAnalysisTK-1.3.16.g6a5d5e7.noarch.zip
      --output-dir ${bin_dir}
    </argument>

    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">2000</profile>

  </job>

  <!-- Pre Job: makes picard available -->
  <job id="IDPRE4" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --no-metadata
      --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionDependenciesBundle
      --
      --input-file ${workflow_bundle_dir}/${workflow_name}/dependencies/noarch/picard-tools-1.48.noarch.zip
      --output-dir ${bin_dir}
    </argument>

    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">2000</profile>

  </job>

  <!-- Pre Job: makes samtools available -->
  <job id="IDPRE5" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --no-metadata
      --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionDependenciesBundle
      --
      --input-file ${workflow_bundle_dir}/${workflow_name}/dependencies/x86_64/samtools-0.1.17.x86_64.zip
      --output-dir ${bin_dir}
    </argument>

    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">2000</profile>

  </job>


<!-- MODULE CALLS -->

<!-- 
Overview: 

* want to do a merge of all the Bam files and then do target and realignment
* realign per chr
* then merge everything
* collapse at that point
-->

<!-- BEGIN LOOP: provision input BAM files and then index them -->
<#list inputs?split(",") as input>

  <#list input?split("/") as tmp>
    <#assign basename = tmp/>
  </#list>

  <!-- Job: figure out if the input is a URL and, if it is, correclty download it to a staging area otherwise link to it -->
  <job id="ID0.${input_index}" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --no-metadata
      --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles
      --
      --input-file ${input}
      --output-dir ${data_dir}
    </argument>

    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">2000</profile>

  </job>

  <!-- TODO: this needs to output in name sorted for the fixmate step, need to test -->
  <!-- Here's an example filtering based on quality too: samtools view -b -F 4 ./bam/110316_I580_00038_612RG_LT_s_1_sequence_SE_novoalign.sam.sorted.bam | samtools view -b -F 256 - | samtools view -b -q 30 - > tmp/110316_I580_00038_612RG_LT_s_1_sequence_SE_novoalign.sam.sorted.bam -->
  <!-- Job: Samtools filter out unmapped and multi-hit -->
  <#assign module = "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner"/>
  <#assign algo = "SamtoolsUniqueHitFilter"/>
  <job id="ID1.${input_index}" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
      <#list parentAccessions?split(",") as pa>
      --metadata-parent-accession ${pa}
      </#list>
      --metadata-processing-accession-file ${accession_dir}/${algo}_${input_index}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-accession ${workflow_run_accession}
      --module ${module}
      --
      --gcr-algorithm ${algo}
      --gcr-command ${bin_dir}/${samtools} view -b -F 4 ${data_dir}/${basename} | ${bin_dir}/${samtools} view -b -F 256 - | ${bin_dir}/${samtools} sort -n -m ${samtools_slots_memory_gigabytes - 2}000000000 - ${data_dir}/${basename}.filtered.namesorted
    </argument>

    <!-- See http://www.globus.org/api/c-globus-4.0.3/globus_gram_job_manager/html/globus_job_manager_rsl.html -->
    <!-- See http://pegasus.isi.edu/wms/docs/3.0/advanced_concepts_profiles.php#id2738647 -->
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">${samtools_slots}</profile>
    <profile namespace="globus" key="maxmemory">${samtools_slots_memory_gigabytes}000</profile>

  </job>

  <!-- Job: FixMateInfo -->
  <#assign module = "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner"/>
  <#assign parentAlgo = "${algo}"/>
  <#assign algo = "PicardFixMateInformation"/>
  <job id="ID10.${input_index}" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
      --metadata-parent-accession-file ${accession_dir}/${parentAlgo}_${input_index}_accession
      --metadata-processing-accession-file ${accession_dir}/${algo}_${input_index}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-ancestor-accession ${workflow_run_accession}
      --module ${module}
      --
      --gcr-algorithm ${algo}
      --gcr-command ${java}
      -Xmx${picard_fixmate_mem}g
      -jar ${bin_dir}/${picardfixmate}
      INPUT=${data_dir}/${basename}.filtered.namesorted.bam
      OUTPUT=${data_dir}/${basename}.filtered.fixmate.sorted.bam
      VALIDATION_STRINGENCY=SILENT TMP_DIR=${tmp_dir}
      SORT_ORDER=coordinate
    </argument>

    <!-- See http://www.globus.org/api/c-globus-4.0.3/globus_gram_job_manager/html/globus_job_manager_rsl.html -->
    <!-- See http://pegasus.isi.edu/wms/docs/3.0/advanced_concepts_profiles.php#id2738647 -->
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">${picard_slots}</profile>
    <profile namespace="globus" key="maxmemory">${picard_fixmate_mem + 4}000</profile>

  </job>

  <!-- Index input BAMs -->
  <#assign algo = "IndexBam1"/>
  <#assign module = "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner"/>
  <job id="ID15.${input_index}" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
      --metadata-parent-accession-file ${accession_dir}/${parentAlgo}_${input_index}_accession
      --metadata-processing-accession-file ${accession_dir}/${algo}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-ancestor-accession ${workflow_run_accession}
      --module ${module}
      --
      --gcr-algorithm ${algo}
      --gcr-command ${java}
      -Xmx${picard_index_bam_mem}g
      -jar ${bin_dir}/${picardindex}
      INPUT=${data_dir}/${basename}.filtered.fixmate.sorted.bam
    </argument>

    <!-- See http://www.globus.org/api/c-globus-4.0.3/globus_gram_job_manager/html/globus_job_manager_rsl.html -->
    <!-- See http://pegasus.isi.edu/wms/docs/3.0/advanced_concepts_profiles.php#id2738647 -->
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">${picard_slots}</profile>
    <profile namespace="globus" key="maxmemory">${picard_index_bam_mem + 4}000</profile>

  </job>

</#list>
<!-- END LOOP: provision input BAM files and then index them -->


<!-- BEGIN LOOP: by chromosome -->
<#list chr_sizes?split(",") as chr_size>

  <#assign chrArr = chr_size?split(":")/>
  <#assign chr = chrArr[0]/>
  <#assign size = chrArr[1]?number/>

  <!-- Job: RealignerTargetCreator -->
  <#assign parentAlgo = "IndexBam1"/>
  <#assign algo = "RealignerTargetCreator"/>
  <#assign module = "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner"/>
  <job id="ID20.${chr_size_index}" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
      --metadata-parent-accession-file ${accession_dir}/${parentAlgo}_accession
      --metadata-processing-accession-file ${accession_dir}/${algo}_${chr}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-ancestor-accession ${workflow_run_accession}
      --module ${module}
      --
      --gcr-algorithm ${algo}
      --gcr-command ${java}
      -Xmx${gatk_realign_target_creator_mem}g
      -Djava.io.tmpdir=${tmp_dir} 
      -jar ${bin_dir}/GenomeAnalysisTK-1.3-16-g6a5d5e7/GenomeAnalysisTK.jar
      -T RealignerTargetCreator
      -R ${ref_fasta}
      -o ${data_dir}/gatk.${chr}.intervals -L ${chr} -known ${gatk_dbsnp_vcf} -et NO_ET  
<#list inputs?split(",") as input>
  <#-- Set the basename from input name -->
  <#list input?split("/") as tmp>
    <#assign basename = tmp/>
  </#list>
      -I ${data_dir}/${basename}.filtered.fixmate.sorted.bam
</#list>
    </argument>

    <!-- See http://www.globus.org/api/c-globus-4.0.3/globus_gram_job_manager/html/globus_job_manager_rsl.html -->
    <!-- See http://pegasus.isi.edu/wms/docs/3.0/advanced_concepts_profiles.php#id2738647 -->
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">${gatk_slots}</profile>
    <profile namespace="globus" key="maxmemory">${gatk_indel_realigner_mem + 4}000</profile>

  </job>

  <!-- Job: IndelRealigner -->
  <!-- NOTE: I had to remove -targetNotSorted to get this to work -->
  <#assign parentAlgo = "RealignerTargetCreator"/>
  <#assign algo = "IndelRealigner"/>
  <#assign module = "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner"/>
  <job id="ID30.${chr_size_index}" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
      --metadata-parent-accession-file ${accession_dir}/${parentAlgo}_${chr}_accession
      --metadata-processing-accession-file ${accession_dir}/${algo}_${chr}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-ancestor-accession ${workflow_run_accession}
      --module ${module}
      --
      --gcr-algorithm ${algo}
      --gcr-command ${java}
      -Xmx${gatk_indel_realigner_mem}g
      -Djava.io.tmpdir=${tmp_dir} 
      -jar ${bin_dir}/GenomeAnalysisTK-1.3-16-g6a5d5e7/GenomeAnalysisTK.jar
      -R ${ref_fasta}
      -T IndelRealigner
      -L ${chr}
      -targetIntervals ${data_dir}/gatk.${chr}.intervals
      -o ${data_dir}/gatk.realigned.${chr}.bam
      -compress 0
      -et NO_ET
<#list inputs?split(",") as input>
  <#-- Set the basename from input name, removing .input -->
  <#list input?split("/") as tmp>
    <#assign basename = tmp/>
  </#list>
      -I ${data_dir}/${basename}.filtered.fixmate.sorted.bam
</#list>
    </argument>

    <!-- See http://www.globus.org/api/c-globus-4.0.3/globus_gram_job_manager/html/globus_job_manager_rsl.html -->
    <!-- See http://pegasus.isi.edu/wms/docs/3.0/advanced_concepts_profiles.php#id2738647 -->
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">${gatk_slots}</profile>
    <profile namespace="globus" key="maxmemory">${gatk_indel_realigner_mem + 4}000</profile>

  </job>

  <!-- Job: Sort by Query Name for Fixmate step, this require query name sort not coordinate, see http://picard.svn.sourceforge.net/viewvc/picard/tags/1.48/src/java/net/sf/picard/sam/FixMateInformation.java?revision=1005&view=markup -->
  <#assign parentAlgo = "IndelRealigner"/>
  <#assign algo = "RealignedBAMResorting"/>
  <#assign module = "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner"/>
  <job id="ID35.${chr_size_index}" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
      --metadata-parent-accession-file ${accession_dir}/${parentAlgo}_${chr}_accession
      --metadata-processing-accession-file ${accession_dir}/${algo}_${chr}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-ancestor-accession ${workflow_run_accession}
      --module ${module}
      --
      --gcr-algorithm ${algo}
      --gcr-command ${java}
      -Xmx${picard_index_bam_mem}g
      -jar ${bin_dir}/${picardsort}
      INPUT=${data_dir}/gatk.realigned.${chr}.bam
      OUTPUT=${data_dir}/gatk.realigned.${chr}.queryname-sorted.bam
      SORT_ORDER=queryname
    </argument>

    <!-- See http://www.globus.org/api/c-globus-4.0.3/globus_gram_job_manager/html/globus_job_manager_rsl.html -->
    <!-- See http://pegasus.isi.edu/wms/docs/3.0/advanced_concepts_profiles.php#id2738647 -->
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">${picard_slots}</profile>
    <profile namespace="globus" key="maxmemory">${picard_sort_mem + 4}000</profile>

  </job>

  <!-- Job: FixMates -->
  <#assign parentAlgo = "${algo}"/>
  <#assign algo = "FixMates"/>
  <#assign module = "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner"/>
  <job id="ID40.${chr_size_index}" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
      --metadata-parent-accession-file ${accession_dir}/${parentAlgo}_${chr}_accession
      --metadata-processing-accession-file ${accession_dir}/${algo}_${chr}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-ancestor-accession ${workflow_run_accession}
      --module ${module}
      --
      --gcr-algorithm ${algo}
      --gcr-command ${java}
      -Xmx${picard_fixmate_mem}g
      -Djava.io.tmpdir=${tmp_dir}
      -jar ${bin_dir}/${picardfixmate} 
      INPUT=${data_dir}/gatk.realigned.${chr}.queryname-sorted.bam
      OUTPUT=${data_dir}/gatk.realigned.${chr}.fixmate.bam
      SO=coordinate
    </argument>

    <!-- See http://www.globus.org/api/c-globus-4.0.3/globus_gram_job_manager/html/globus_job_manager_rsl.html -->
    <!-- See http://pegasus.isi.edu/wms/docs/3.0/advanced_concepts_profiles.php#id2738647 -->
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">${picard_slots}</profile>
    <profile namespace="globus" key="maxmemory">${picard_fixmate_mem + 4}000</profile>

  </job>

  <!-- Job: Index BAM: Can't we just have picard tools above do this? -->
  <#assign parentAlgo = "${algo}"/>
  <#assign algo = "IndexBam2"/>
  <#assign module = "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner"/>
  <job id="ID50.${chr_size_index}" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
      --metadata-parent-accession-file ${accession_dir}/${parentAlgo}_${chr}_accession
      --metadata-processing-accession-file ${accession_dir}/${algo}_${chr}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-ancestor-accession ${workflow_run_accession}
      --module ${module}
      --
      --gcr-algorithm ${algo}
      --gcr-command ${java}
      -Xmx${picard_index_bam_mem}g
      -Djava.io.tmpdir=${tmp_dir}
      -jar ${bin_dir}/${picardindex}
      INPUT=${data_dir}/gatk.realigned.${chr}.fixmate.bam
    </argument>

    <!-- See http://www.globus.org/api/c-globus-4.0.3/globus_gram_job_manager/html/globus_job_manager_rsl.html -->
    <!-- See http://pegasus.isi.edu/wms/docs/3.0/advanced_concepts_profiles.php#id2738647 -->
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">${picard_slots}</profile>
    <profile namespace="globus" key="maxmemory">${picard_index_bam_mem + 4}000</profile>

  </job>

  <!-- Job: MarkDuplicates -->
  <#assign parentAlgo = "${algo}"/>
  <#assign algo = "PicardMarkDuplicates"/>
  <#assign module = "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner"/>
  <job id="ID55.${chr_size_index}" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
      --metadata-parent-accession-file ${accession_dir}/${parentAlgo}_${chr}_accession
      --metadata-processing-accession-file ${accession_dir}/${algo}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-ancestor-accession ${workflow_run_accession}
      --module ${module}
      --
      --gcr-algorithm ${algo}
      --gcr-command ${java}
      -Xmx${picard_mark_dup_mem}g
      -jar ${bin_dir}/${picardmarkdups}
      INPUT=${data_dir}/gatk.realigned.${chr}.fixmate.bam
      OUTPUT=${data_dir}/gatk.realigned.${chr}.fixmate.markdups.bam
      CREATE_INDEX=true
      METRICS_FILE=${data_dir}/gatk.realigned.${chr}.fixmate.markdups.metrics
      VALIDATION_STRINGENCY=SILENT TMP_DIR=${tmp_dir}
    </argument>

    <!-- See http://www.globus.org/api/c-globus-4.0.3/globus_gram_job_manager/html/globus_job_manager_rsl.html -->
    <!-- See http://pegasus.isi.edu/wms/docs/3.0/advanced_concepts_profiles.php#id2738647 -->
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">${picard_slots}</profile>
    <profile namespace="globus" key="maxmemory">${picard_mark_dup_mem + 4}000</profile>

  </job>

</#list>
<!-- END LOOP: by chromosome -->


  <!-- TODO: SO ROB POINTS OUT THAT COUNT COVARIANTS ACTUALLY ONLY WORKS ON A PER LANE BASIS SO WE COULD SPLIT BY THAT RG_ID (BY LANE) -->
  <!-- Job: Count Covariates -->
  <#assign parentAlgo = "PicardMarkDuplicates"/>
  <#assign algo = "CountCovariates"/>
  <#assign module = "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner"/>
  <job id="ID60" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
      --metadata-parent-accession-file ${accession_dir}/${parentAlgo}_accession
      --metadata-processing-accession-file ${accession_dir}/${algo}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-ancestor-accession ${workflow_run_accession}
      --module ${module}
      --
      --gcr-algorithm ${algo}
      --gcr-command ${java}
      -Xmx${gatk_count_covariate_mem}g
      -Djava.io.tmpdir=${tmp_dir} 
      -jar ${bin_dir}/GenomeAnalysisTK-1.3-16-g6a5d5e7/GenomeAnalysisTK.jar
      -l INFO
      -R ${ref_fasta}
      -knownSites ${gatk_dbsnp_vcf}
<#list chr_sizes?split(",") as chr_size>
  <#assign chrArr = chr_size?split(":")/>
  <#assign chr = chrArr[0]/>
  <#assign size = chrArr[1]?number/>
      -I ${data_dir}/gatk.realigned.${chr}.fixmate.markdups.bam
</#list>
      -T CountCovariates -cov ReadGroupCovariate -cov QualityScoreCovariate -cov CycleCovariate -cov DinucCovariate 
      -recalFile ${data_dir}/recal_data.csv -nt 8 -et NO_ET
    </argument>

<!-- TODO: merge, do this on the combined bam files instead -->

    <!-- See http://www.globus.org/api/c-globus-4.0.3/globus_gram_job_manager/html/globus_job_manager_rsl.html -->
    <!-- See http://pegasus.isi.edu/wms/docs/3.0/advanced_concepts_profiles.php#id2738647 -->
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">${gatk_slots}</profile>
    <profile namespace="globus" key="maxmemory">${gatk_count_covariate_mem + 4}000</profile>

  </job>


<!-- BEGIN LOOP: by chromosome -->
<#list chr_sizes?split(",") as chr_size>
  <#assign chrArr = chr_size?split(":")/>
  <#assign chr = chrArr[0]/>
  <#assign size = chrArr[1]?number/>

  <!-- Job: Table Recal -->
  <#assign parentAlgo = "CountCovariates"/>
  <#assign algo = "TableRecalibration"/>
  <#assign module = "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner"/>
  <job id="ID70.${chr_size_index}" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
      --metadata-parent-accession-file ${accession_dir}/${parentAlgo}_accession
      --metadata-processing-accession-file ${accession_dir}/${algo}_${chr}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-ancestor-accession ${workflow_run_accession}
      --module ${module}
      --
      --gcr-algorithm ${algo}
      --gcr-command ${java}
      -Xmx${gatk_count_covariate_mem}g
      -Djava.io.tmpdir=${tmp_dir} 
      -jar ${bin_dir}/GenomeAnalysisTK-1.3-16-g6a5d5e7/GenomeAnalysisTK.jar
      --preserve_qscores_less_than 2 -T TableRecalibration -l INFO -R ${ref_fasta}
      -I ${data_dir}/gatk.realigned.${chr}.fixmate.markdups.bam
      --out ${data_dir}/gatk.realigned.recal.${chr}.bam -recalFile ${data_dir}/recal_data.csv -L ${chr} -et NO_ET
    </argument>

    <!-- See http://www.globus.org/api/c-globus-4.0.3/globus_gram_job_manager/html/globus_job_manager_rsl.html -->
    <!-- See http://pegasus.isi.edu/wms/docs/3.0/advanced_concepts_profiles.php#id2738647 -->
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">${gatk_slots}</profile>
    <profile namespace="globus" key="maxmemory">${gatk_count_covariate_mem + 4}000</profile>

  </job>

  <!-- Job: Index BAM -->
  <#assign parentAlgo = "TableRecalibration"/>
  <#assign algo = "IndexBam3"/>
  <#assign module = "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner"/>
  <job id="ID80.${chr_size_index}" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
      --metadata-parent-accession-file ${accession_dir}/${parentAlgo}_${chr}_accession
      --metadata-processing-accession-file ${accession_dir}/${algo}_${chr}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-ancestor-accession ${workflow_run_accession}
      --module ${module}
      --
      --gcr-algorithm ${algo}
      --gcr-command ${java}
      -Xmx${picard_index_bam_mem}g
      -Djava.io.tmpdir=${tmp_dir}
      -jar ${bin_dir}/${picardindex}
      INPUT=${data_dir}/gatk.realigned.recal.${chr}.bam
    </argument>

    <!-- See http://www.globus.org/api/c-globus-4.0.3/globus_gram_job_manager/html/globus_job_manager_rsl.html -->
    <!-- See http://pegasus.isi.edu/wms/docs/3.0/advanced_concepts_profiles.php#id2738647 -->
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">${picard_slots}</profile>
    <profile namespace="globus" key="maxmemory">${picard_index_bam_mem + 4}000</profile>

  </job>

  <!-- Job: Unified Genotyper SNP caller -->
  <!-- FIXME: it seems like I can only use -nt 1 according to http://getsatisfaction.com/gsa/topics/genotyper_error_unable_to_create_basicfeaturereader_using_feature_file -->
  <#assign parentAlgo = "IndexBam3"/>
  <#assign algo = "GATKUnifiedGenotyperSNV"/>
  <#assign module = "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner"/>
  <job id="ID90.${chr_size_index}" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
      --metadata-parent-accession-file ${accession_dir}/${parentAlgo}_${chr}_accession
      --metadata-processing-accession-file ${accession_dir}/${algo}_${chr}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-ancestor-accession ${workflow_run_accession}
      --module ${module}
      --
      --gcr-algorithm ${algo}
      --gcr-command ${java}
      -Xmx${gatk_unified_genotyper_mem}g
      -Djava.io.tmpdir=${tmp_dir}
      -jar ${bin_dir}/GenomeAnalysisTK-1.3-16-g6a5d5e7/GenomeAnalysisTK.jar
      -R ${ref_fasta}
      -T UnifiedGenotyper
      -I ${data_dir}/gatk.realigned.recal.${chr}.bam
      -D ${gatk_dbsnp_vcf}
      -o ${data_dir}/gatk.realigned.recal.bam.snps.raw.${chr}.vcf
      -stand_call_conf 30 -stand_emit_conf 1.0 -metrics ${data_dir}/gatk.realigned.recal.bam.snps.raw.${chr}.metrics
      -nt 1 -L ${chr} -et NO_ET
    </argument>

    <!-- See http://www.globus.org/api/c-globus-4.0.3/globus_gram_job_manager/html/globus_job_manager_rsl.html -->
    <!-- See http://pegasus.isi.edu/wms/docs/3.0/advanced_concepts_profiles.php#id2738647 -->
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">${gatk_slots}</profile>
    <profile namespace="globus" key="maxmemory">${gatk_unified_genotyper_mem + 4}000</profile>

  </job>

 <!-- TODO: move this to the SomaticIndelDetector and use "unpaired" to paramaterize, "minFraction .3", "minCoverage 4" -->
 <!-- see http://www.broadinstitute.org/gsa/wiki/index.php/Somatic_Indel_Detector -->
 

  <!-- Job: Indel caller -->
  <#assign parentAlgo = "IndexBam3"/>
  <#assign algo = "GATKUnifiedGenotyperIndel"/>
  <#assign module = "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner"/>
  <job id="ID100.${chr_size_index}" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
      --metadata-parent-accession-file ${accession_dir}/${parentAlgo}_${chr}_accession
      --metadata-processing-accession-file ${accession_dir}/${algo}_${chr}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-ancestor-accession ${workflow_run_accession}
      --module ${module}
      --
      --gcr-algorithm ${algo}
      --gcr-command ${java}
      -Xmx${gatk_indel_genotyper_mem}g
      -Djava.io.tmpdir=${tmp_dir}
      -jar ${bin_dir}/GenomeAnalysisTK-1.3-16-g6a5d5e7/GenomeAnalysisTK.jar
      -T UnifiedGenotyper
      -l INFO
      -R ${ref_fasta}
      -I ${data_dir}/gatk.realigned.recal.${chr}.bam
      -o ${data_dir}/gatk.realigned.recal.bam.indels.raw.${chr}.vcf
      -glm INDEL -G Standard -stand_emit_conf 10 -stand_call_conf 50 -dcov 1000 
      -metrics ${data_dir}/gatk.realigned.recal.bam.indels.raw.${chr}.metrics
      -D ${gatk_dbsnp_vcf}
      -L ${chr} -et NO_ET
    </argument>

    <!-- See http://www.globus.org/api/c-globus-4.0.3/globus_gram_job_manager/html/globus_job_manager_rsl.html -->
    <!-- See http://pegasus.isi.edu/wms/docs/3.0/advanced_concepts_profiles.php#id2738647 -->
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">${gatk_slots}</profile>
    <profile namespace="globus" key="maxmemory">${gatk_indel_genotyper_mem + 4}000</profile>

  </job>

  <!-- Job: Indel Filter -->
  <#assign parentAlgo = "GATKUnifiedGenotyperIndel"/>
  <#assign algo = "GATKUnifiedGenotyperIndelFilter"/>
  <#assign module = "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner"/>
  <job id="ID110.${chr_size_index}" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
      --metadata-parent-accession-file ${accession_dir}/${parentAlgo}_${chr}_accession
      --metadata-processing-accession-file ${accession_dir}/${algo}_${chr}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-ancestor-accession ${workflow_run_accession}
      --module ${module}
      --
      --gcr-algorithm ${algo}
      --gcr-command bash 
      ${workflow_bundle_dir}/${workflow_name}/bin/sw_module_gatk_indel_filter.sh
      ${java}
      ${gatk_variant_filter_mem} 
      ${tmp_dir}
      ${bin_dir}/GenomeAnalysisTK-1.3-16-g6a5d5e7/GenomeAnalysisTK.jar
      ${ref_fasta}
      ${data_dir}/gatk.realigned.recal.bam.indels.raw.${chr}.vcf 
      ${data_dir}/gatk.realigned.recal.bam.indels.filtered.${chr}.vcf
      ${chr}
    </argument>

    <!-- See http://www.globus.org/api/c-globus-4.0.3/globus_gram_job_manager/html/globus_job_manager_rsl.html -->
    <!-- See http://pegasus.isi.edu/wms/docs/3.0/advanced_concepts_profiles.php#id2738647 -->
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">${gatk_slots}</profile>
    <profile namespace="globus" key="maxmemory">${gatk_variant_filter_mem + 4}000</profile>

  </job>

  <!-- Job: SNV filter -->
  <!-- FIXME: will need a WGS version -->
  <#assign parentAlgo = "GATKUnifiedGenotyperSNV"/>
  <#assign algo = "GATKUnifiedGenotyperSNVFilter"/>
  <#assign module = "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner"/>
  <job id="ID120.${chr_size_index}" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
      --metadata-parent-accession-file ${accession_dir}/${parentAlgo}_${chr}_accession
      --metadata-processing-accession-file ${accession_dir}/${algo}_${chr}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-ancestor-accession ${workflow_run_accession}
      --module ${module}
      --
      --gcr-algorithm ${algo}
      --gcr-command bash
      ${workflow_bundle_dir}/${workflow_name}/bin/sw_module_gatk_snv_filter.sh
      ${java}
      ${gatk_variant_filter_mem}
      ${tmp_dir}
      ${bin_dir}/GenomeAnalysisTK-1.3-16-g6a5d5e7/GenomeAnalysisTK.jar
      ${ref_fasta}
      ${data_dir}/gatk.realigned.recal.bam.snps.raw.${chr}.vcf
      ${data_dir}/gatk.realigned.recal.bam.snps.filtered.${chr}.vcf
      ${chr}
      ${data_dir}/gatk.realigned.recal.bam.indels.raw.${chr}.vcf 
    </argument>

    <!-- See http://www.globus.org/api/c-globus-4.0.3/globus_gram_job_manager/html/globus_job_manager_rsl.html -->
    <!-- See http://pegasus.isi.edu/wms/docs/3.0/advanced_concepts_profiles.php#id2738647 -->
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">${gatk_slots}</profile>
    <profile namespace="globus" key="maxmemory">${gatk_variant_filter_mem + 4}000</profile>

  </job>

</#list>
<!-- END LOOP: by chromosome -->

  <!-- Job: merge SNV -->
  <#assign parentAlgo = "GATKUnifiedGenotyperSNV"/>
  <#assign algo = "MergeRawSNV"/>
  <#assign module = "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner"/>
  <job id="ID130" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
<#list chr_sizes?split(",") as chr_size>
  <#assign chrArr = chr_size?split(":")/>
  <#assign chr = chrArr[0]/>
  <#assign size = chrArr[1]?number/>
      --metadata-parent-accession-file ${accession_dir}/${parentAlgo}_${chr}_accession
</#list>
      --metadata-processing-accession-file ${accession_dir}/${algo}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-ancestor-accession ${workflow_run_accession}
      --module ${module}
      --
      --gcr-algorithm ${algo}
      --gcr-command ${perl}
      ${workflow_bundle_dir}/${workflow_name}/bin/sw_module_merge_GATK_VCF.pl
<#list chr_sizes?split(",") as chr_size>
  <#assign chrArr = chr_size?split(":")/>
  <#assign chr = chrArr[0]/>
  <#assign size = chrArr[1]?number/>
      --vcf-input-file ${data_dir}/gatk.realigned.recal.bam.snps.raw.${chr}.vcf
</#list>
      --vcf-output-file ${data_dir}/gatk.realigned.recal.bam.snps.raw.merged.vcf
    </argument>

    <!-- See http://www.globus.org/api/c-globus-4.0.3/globus_gram_job_manager/html/globus_job_manager_rsl.html -->
    <!-- See http://pegasus.isi.edu/wms/docs/3.0/advanced_concepts_profiles.php#id2738647 -->
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">2000</profile>

  </job>

<!-- TODO: add a annotation step  for things like overlaps with indel --> 

  <!-- Job: merge filtered SNV -->
  <#assign parentAlgo = "GATKUnifiedGenotyperSNVFilter"/>
  <#assign algo = "MergeFilteredSNV"/>
  <#assign module = "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner"/>
  <job id="ID140" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
<#list chr_sizes?split(",") as chr_size>
  <#assign chrArr = chr_size?split(":")/>
  <#assign chr = chrArr[0]/>
  <#assign size = chrArr[1]?number/>
      --metadata-parent-accession-file ${accession_dir}/${parentAlgo}_${chr}_accession
</#list>
      --metadata-processing-accession-file ${accession_dir}/${algo}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-ancestor-accession ${workflow_run_accession}
      --module ${module}
      --
      --gcr-algorithm ${algo}
      --gcr-command ${perl}
      ${workflow_bundle_dir}/${workflow_name}/bin/sw_module_merge_GATK_VCF.pl
<#list chr_sizes?split(",") as chr_size>
  <#assign chrArr = chr_size?split(":")/>
  <#assign chr = chrArr[0]/>
  <#assign size = chrArr[1]?number/>
      --vcf-input-file ${data_dir}/gatk.realigned.recal.bam.snps.filtered.${chr}.vcf
</#list>
      --vcf-output-file ${data_dir}/gatk.realigned.recal.bam.snps.filtered.merged.vcf
    </argument>

    <!-- See http://www.globus.org/api/c-globus-4.0.3/globus_gram_job_manager/html/globus_job_manager_rsl.html -->
    <!-- See http://pegasus.isi.edu/wms/docs/3.0/advanced_concepts_profiles.php#id2738647 -->
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">2000</profile>

  </job>


  <!-- Job: merge indels -->
  <#assign parentAlgo = "GATKUnifiedGenotyperIndel"/>
  <#assign algo = "MergeRawIndel"/>
  <#assign module = "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner"/>
  <job id="ID150" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
<#list chr_sizes?split(",") as chr_size>
  <#assign chrArr = chr_size?split(":")/>
  <#assign chr = chrArr[0]/>
  <#assign size = chrArr[1]?number/>
      --metadata-parent-accession-file ${accession_dir}/${parentAlgo}_${chr}_accession
</#list>
      --metadata-processing-accession-file ${accession_dir}/${algo}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-ancestor-accession ${workflow_run_accession}
      --module ${module}
      --
      --gcr-algorithm ${algo}
      --gcr-command ${perl}
      ${workflow_bundle_dir}/${workflow_name}/bin/sw_module_merge_GATK_VCF.pl
<#list chr_sizes?split(",") as chr_size>
  <#assign chrArr = chr_size?split(":")/>
  <#assign chr = chrArr[0]/>
  <#assign size = chrArr[1]?number/>
      --vcf-input-file ${data_dir}/gatk.realigned.recal.bam.indels.raw.${chr}.vcf
</#list>
      --vcf-output-file ${data_dir}/gatk.realigned.recal.bam.indels.raw.merged.vcf
    </argument>

    <!-- See http://www.globus.org/api/c-globus-4.0.3/globus_gram_job_manager/html/globus_job_manager_rsl.html -->
    <!-- See http://pegasus.isi.edu/wms/docs/3.0/advanced_concepts_profiles.php#id2738647 -->
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">2000</profile>

  </job>


  <!-- Job: merge filtered Indel -->
  <#assign parentAlgo = "GATKUnifiedGenotyperIndelFilter"/>
  <#assign algo = "MergeFilteredIndel"/>
  <#assign module = "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner"/>
  <job id="ID160" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
<#list chr_sizes?split(",") as chr_size>
  <#assign chrArr = chr_size?split(":")/>
  <#assign chr = chrArr[0]/>
  <#assign size = chrArr[1]?number/>
      --metadata-parent-accession-file ${accession_dir}/${parentAlgo}_${chr}_accession
</#list>
      --metadata-processing-accession-file ${accession_dir}/${algo}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-ancestor-accession ${workflow_run_accession}
      --module ${module}
      --
      --gcr-algorithm ${algo}
      --gcr-command ${perl}
      ${workflow_bundle_dir}/${workflow_name}/bin/sw_module_merge_GATK_VCF.pl
<#list chr_sizes?split(",") as chr_size>
  <#assign chrArr = chr_size?split(":")/>
  <#assign chr = chrArr[0]/>
  <#assign size = chrArr[1]?number/>
      --vcf-input-file ${data_dir}/gatk.realigned.recal.bam.indels.filtered.${chr}.vcf
</#list>
      --vcf-output-file ${data_dir}/gatk.realigned.recal.bam.indels.filtered.merged.vcf
    </argument>

    <!-- See http://www.globus.org/api/c-globus-4.0.3/globus_gram_job_manager/html/globus_job_manager_rsl.html -->
    <!-- See http://pegasus.isi.edu/wms/docs/3.0/advanced_concepts_profiles.php#id2738647 -->
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">2000</profile>

  </job>


  <!-- TODO: need to add an annotation step that works off of the filtered SNV VCF file and the filtered Indel VCF file as inputs that does a filter to annotate these files where indels and snvs overlap -->

  <!-- Job: merge raw variants ID150 130-->
  <#assign algo = "MergeRawVariants"/>
  <#assign module = "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner"/>
  <job id="ID170" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
      --metadata-parent-accession-file ${accession_dir}/MergeRawIndel_accession
      --metadata-parent-accession-file ${accession_dir}/MergeRawSNV_accession
      --metadata-processing-accession-file ${accession_dir}/${algo}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-ancestor-accession ${workflow_run_accession}
      --module ${module}
      --
      --gcr-algorithm ${algo}
      --gcr-output-file ${algo}:text/vcf-4:${output_dir}/seqware-${seqware_version}_GATKREcalibrationAndVariantCalling-${workflow_version}/${random}/${identifier}.gatk.realigned.recal.bam.variants.raw.merged.vcf
      --gcr-command ${perl}
      ${workflow_bundle_dir}/${workflow_name}/bin/sw_module_merge_GATK_VCF.pl
      --vcf-input-file ${data_dir}/gatk.realigned.recal.bam.snps.raw.merged.vcf
      --vcf-input-file ${data_dir}/gatk.realigned.recal.bam.indels.raw.merged.vcf
      --vcf-output-file ${output_dir}/seqware-${seqware_version}_GATKREcalibrationAndVariantCalling-${workflow_version}/${random}/${identifier}.gatk.realigned.recal.bam.variants.raw.merged.vcf
    </argument>

    <!-- See http://www.globus.org/api/c-globus-4.0.3/globus_gram_job_manager/html/globus_job_manager_rsl.html -->
    <!-- See http://pegasus.isi.edu/wms/docs/3.0/advanced_concepts_profiles.php#id2738647 -->
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">2000</profile>

    <@requires_dir "${output_dir}/seqware-${seqware_version}_GATKREcalibrationAndVariantCalling-${workflow_version}/${random}"/>

  </job>


  <!-- Job: merge filtered variants 140 160-->
  <#assign algo = "MergeFilteredVariants"/>
  <#assign module = "net.sourceforge.seqware.pipeline.modules.GenericCommandRunner"/>
  <job id="ID180" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --${metadata}
      --metadata-parent-accession-file ${accession_dir}/MergeFilteredIndel_accession
      --metadata-parent-accession-file ${accession_dir}/MergeFilteredSNV_accession
      --metadata-processing-accession-file ${accession_dir}/${algo}_accession
      --metadata-output-file-prefix ${output_prefix}
      --metadata-workflow-run-ancestor-accession ${workflow_run_accession}
      --module ${module}
      --
      --gcr-algorithm ${algo}
      --gcr-output-file ${algo}:text/vcf-4:${output_dir}/seqware-${seqware_version}_GATKREcalibrationAndVariantCalling-${workflow_version}/${random}/${identifier}.gatk.realigned.recal.bam.variants.filtered.merged.vcf
      --gcr-command ${perl}
      ${workflow_bundle_dir}/${workflow_name}/bin/sw_module_merge_GATK_VCF.pl
      --vcf-input-file ${data_dir}/gatk.realigned.recal.bam.indels.filtered.merged.vcf
      --vcf-input-file ${data_dir}/gatk.realigned.recal.bam.snps.filtered.merged.vcf
      --vcf-output-file ${output_dir}/seqware-${seqware_version}_GATKREcalibrationAndVariantCalling-${workflow_version}/${random}/${identifier}.gatk.realigned.recal.bam.variants.filtered.merged.vcf
    </argument>

    <!-- See http://www.globus.org/api/c-globus-4.0.3/globus_gram_job_manager/html/globus_job_manager_rsl.html -->
    <!-- See http://pegasus.isi.edu/wms/docs/3.0/advanced_concepts_profiles.php#id2738647 -->
    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">2000</profile>

    <@requires_dir "${output_dir}/seqware-${seqware_version}_GATKREcalibrationAndVariantCalling-${workflow_version}/${random}"/>

  </job>

  <!-- Job: copy the output to the correct location -->
  <job id="ID190" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --no-metadata
      --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles
      --
      --force-copy
      --input-file ${output_dir}/seqware-${seqware_version}_GATKREcalibrationAndVariantCalling-${workflow_version}/${random}/${identifier}.gatk.realigned.recal.bam.variants.filtered.merged.vcf
      --output-dir ${output_prefix}${output_dir}/seqware-${seqware_version}_GATKREcalibrationAndVariantCalling-${workflow_version}/${random}
    </argument>

    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">2000</profile>

  </job>

  <!-- Job: copy the output to the correct location -->
  <job id="ID200" namespace="seqware" name="runner" version="${seqware_version}">
    <argument>
      -Xmx1000M
      -classpath ${workflow_bundle_dir}/${workflow_name}/classes:${workflow_bundle_dir}/${workflow_name}/lib/seqware-pipeline-${seqware_version}.jar
      net.sourceforge.seqware.pipeline.runner.Runner
      --no-metadata
      --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles
      --
      --force-copy
      --input-file ${output_dir}/seqware-${seqware_version}_GATKREcalibrationAndVariantCalling-${workflow_version}/${random}/${identifier}.gatk.realigned.recal.bam.variants.raw.merged.vcf
      --output-dir ${output_prefix}${output_dir}/seqware-${seqware_version}_GATKREcalibrationAndVariantCalling-${workflow_version}/${random}
    </argument>

    <profile namespace="globus" key="jobtype">condor</profile>
    <profile namespace="globus" key="count">1</profile>
    <profile namespace="globus" key="maxmemory">2000</profile>

  </job>


<!-- Part 2: list of control-flow dependencies (may be empty) -->

<!-- loop around directory creation -->
<#list 1..4 as i>
  <#list 1..5 as j>
  <child ref="IDPRE${j}">
    <parent ref="IDPRE0.${i}"/>
  </child>
  </#list>
</#list>

<!-- first job depends on the pre jobs -->
<#list 1..5 as j>
<#list inputs?split(",") as input>
  <child ref="ID0.${input_index}">
    <parent ref="IDPRE${j}"/>
  </child>
</#list>
</#list>

<!-- counts of inputs -->
<#list inputs?split(",") as input>
  <child ref="ID1.${input_index}">
    <parent ref="ID0.${input_index}"/>
  </child>
  <child ref="ID10.${input_index}">
    <parent ref="ID1.${input_index}"/>
  </child>
  <child ref="ID15.${input_index}">
    <parent ref="ID10.${input_index}"/>
  </child>
<#list chr_sizes?split(",") as chr_size>
  <child ref="ID20.${chr_size_index}">
    <parent ref="ID15.${input_index}"/>
  </child>
</#list>
</#list>

<#list chr_sizes?split(",") as chr_size>
  <child ref="ID30.${chr_size_index}">
    <parent ref="ID20.${chr_size_index}"/>
  </child>
  <child ref="ID35.${chr_size_index}">
    <parent ref="ID30.${chr_size_index}"/>
  </child>
  <child ref="ID40.${chr_size_index}">
    <parent ref="ID35.${chr_size_index}"/>
  </child>
  <child ref="ID50.${chr_size_index}">
    <parent ref="ID40.${chr_size_index}"/>
  </child>
  <child ref="ID55.${chr_size_index}">
    <parent ref="ID50.${chr_size_index}"/>
  </child>
  <child ref="ID60">
    <parent ref="ID55.${chr_size_index}"/>
  </child>
  <child ref="ID70.${chr_size_index}">
    <parent ref="ID60"/>
  </child>
  <child ref="ID80.${chr_size_index}">
    <parent ref="ID70.${chr_size_index}"/>
  </child>
  <child ref="ID90.${chr_size_index}">
    <parent ref="ID80.${chr_size_index}"/>
  </child>
  <child ref="ID100.${chr_size_index}">
    <parent ref="ID80.${chr_size_index}"/>
  </child>
  <child ref="ID110.${chr_size_index}">
    <parent ref="ID100.${chr_size_index}"/>
  </child>
  <child ref="ID120.${chr_size_index}">
    <parent ref="ID90.${chr_size_index}"/>
  </child>
  <child ref="ID130">
    <parent ref="ID90.${chr_size_index}"/>
  </child>
  <child ref="ID140">
    <parent ref="ID120.${chr_size_index}"/>
  </child>
  <child ref="ID150">
    <parent ref="ID100.${chr_size_index}"/>
  </child>
  <child ref="ID160">
    <parent ref="ID110.${chr_size_index}"/>
  </child>
</#list>

  <child ref="ID180">
    <parent ref="ID140"/>
  </child>
  <child ref="ID180">
    <parent ref="ID160"/>
  </child>
  <child ref="ID170">
    <parent ref="ID150"/>
  </child>
  <child ref="ID170">
    <parent ref="ID130"/>
  </child>
  <child ref="ID190">
    <parent ref="ID180"/>
  </child>
  <child ref="ID200">
    <parent ref="ID170"/>
  </child>


</adag>
