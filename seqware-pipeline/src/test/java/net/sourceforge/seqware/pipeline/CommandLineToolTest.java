/*
 * Copyright (C) 2012 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.seqware.pipeline;

import static org.junit.Assert.assertTrue;
import net.sourceforge.seqware.pipeline.runner.PluginRunner;

import org.junit.Test;

/**
 * <p>CommandLineToolTest class.</p>
 *
 * @author yongliang
 * @version $Id: $Id
 * @since 0.13.3
 */
public class CommandLineToolTest {

  /**
   * <p>listTable.</p>
   */
  @Test
  public void listTable() {
    PluginRunner runner = new PluginRunner();
    String[] listTable = new String[] { "--plugin", "net.sourceforge.seqware.pipeline.plugins.Metadata", "--",
        "--list-tables" };
    runner.run(listTable);
    assertTrue(true);
  }

  /**
   * <p>listField.</p>
   */
  @Test
  public void listField() {
    PluginRunner runner = new PluginRunner();
    String[] listField = new String[] { "-p", "net.sourceforge.seqware.pipeline.plugins.Metadata", "--", "--table",
        "study", "--list-fields" };
    runner.run(listField);
    assertTrue(true);
  }

  /**
   * <p>addStudy.</p>
   */
  @Test
  public void addStudy() {
    PluginRunner runner = new PluginRunner();
    String[] addStudy = new String[] { "-p", "net.sourceforge.seqware.pipeline.plugins.Metadata", "--", "--table",
        "study", "--create", "--field", "title::New Test Study", "--field", "description::This is a test description",
        "--field", "accession::InternalID123", "--field", "center_name::Courtagen", "--field",
        "center_project_name::Courtagen Test Project", "--field", "study_type::4" };
    runner.run(addStudy);
    assertTrue(true);
  }

  /**
   * <p>addExperiment.</p>
   */
  @Test
  public void addExperiment() {
    PluginRunner runner = new PluginRunner();
    String[] addExp = new String[] { "-p", "net.sourceforge.seqware.pipeline.plugins.Metadata", "--", "--table",
        "experiment", "--create", "--field", "title::New Test Experiment", "--field",
        "description::This is a test description", "--field", "study_accession::29830", "--field", "platform_id::26" };
    runner.run(addExp);
    assertTrue(true);
  }

  /**
   * <p>addSample.</p>
   */
  @Test
  public void addSample() {
    PluginRunner runner = new PluginRunner();
    String[] addSample = new String[] { "-p", "net.sourceforge.seqware.pipeline.plugins.Metadata", "--", "--table",
        "sample", "--create", "--field", "title::New Test Sample", "--field",
        "description::This is a test description", "--field", "experiment_accession::29831", "--field",
        "organism_id::26" };
    runner.run(addSample);
    assertTrue(true);
  }

  /**
   * <p>provisionFiles.</p>
   */
  @Test
  public void provisionFiles() {
    // -p net.sourceforge.seqware.pipeline.plugins.ModuleRunner -- --module
    // net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles
    // --metadata-output-file-prefix /tmp/test/1/ --metadata-parent-accession
    // 218886 --metadata-processing-accession-file new_accession.txt -- -im
    // jar::chemical/seq-na-fastq-gzip::test_R1.fastq.gz -o /tmp/test/2
    PluginRunner runner = new PluginRunner();
    String[] args = new String[] { "-p", "net.sourceforge.seqware.pipeline.plugins.ModuleRunner", "--", "--module",
        "net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles", "--metadata-output-file-prefix",
        "/Users/yongliang/Desktop/seqware/1/", "--metadata-parent-accession", "218886",
        "--metadata-processing-accession-file", "testdata/new_accession.txt", "--", "-im",
        "jar::chemical/seq-na-fastq-gzip::testdata/pe_test_1.fastq", "-o", "./testdata" };
    runner.run(args);
    assertTrue(true);
  }

  /**
   * <p>genericMetadataSaver.</p>
   */
  @Test
  public void genericMetadataSaver() {
    // java -jar seqware-pipeline-0.11.4-r4770.jar -p
    // net.sourceforge.seqware.pipeline.plugins.ModuleRunner -- --module
    // net.sourceforge.seqware.pipeline.modules.GenericMetadataSaver
    // --metadata-parent-accession 25192 -- --gms-output-file
    // fastq::chemical/seq-na-fastq-gzip::s3://bucket/sample/Sample_GPP100008/GPP100008_AGGCAGAA_L001_R1_004.fastq.gz
    // --gms-algorithm UploadLane1 --gms-suppress-output-file-check
    PluginRunner runner = new PluginRunner();
    String[] args = new String[] { "-p", "net.sourceforge.seqware.pipeline.plugins.ModuleRunner", "--", "--module",
        "net.sourceforge.seqware.pipeline.modules.GenericMetadataSaver", "--metadata-parent-accession", "218886", "--",
        "--gms-output-file", "fastq::chemical/seq-na-fastq-gzip::./testdata/test1", "--gms-algorithm", "UploadLane1",
        "--gms-suppress-output-file-check" };
    runner.run(args);
    assertTrue(true);

  }

  /**
   * <p>listInstall.</p>
   */
  @Test
  public void listInstall() {
    PluginRunner runner = new PluginRunner();
    String[] args = new String[] { "-p", "net.sourceforge.seqware.pipeline.plugins.BundleManager", "--",
        "--list-install" };

    runner.run(args);
    assertTrue(true);
  }

  /**
   * <p>workflowrunReport.</p>
   */
  @Test
  public void workflowrunReport() {
    // -p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter --
    // --workflow-run-accession 24770
    PluginRunner runner = new PluginRunner();
    String[] args = new String[] { "-p", "net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter", "--",
        "--workflow-run-accession", "218972" };
    runner.run(args);
    assertTrue(true);

  }

  /**
   * <p>sequencerRunReport.</p>
   */
  @Test
  public void sequencerRunReport() {
    PluginRunner runner = new PluginRunner();
    String[] args = new String[] { "-p", "net.sourceforge.seqware.pipeline.plugins.SequencerRunReporter", "--",
        "--output-filename", "./testdata/foo.txt" };
    runner.run(args);
    assertTrue(true);

  }

  /**
   * <p>symlinkReport.</p>
   */
  @Test
  public void symlinkReport() {
    PluginRunner runner = new PluginRunner();
    String[] args = new String[] { "-p", "net.sourceforge.seqware.pipeline.plugins.SymLinkFileReporter", "--",
        "--no-links", "--output-filename", "./testdata/symlinkreport", "--study", "Brian New Test Study 2" };
    runner.run(args);
    assertTrue(true);
  }

  /**
   * <p>listworkflowparams.</p>
   */
  @Test
  public void listworkflowparams() {
    PluginRunner runner = new PluginRunner();
    String[] args = new String[] { "-p", "net.sourceforge.seqware.pipeline.plugins.BundleManager", "--",
        "--list-workflow-params", "--workflow-accession", "62692" };
    runner.run(args);
    assertTrue(true);
  }

}
