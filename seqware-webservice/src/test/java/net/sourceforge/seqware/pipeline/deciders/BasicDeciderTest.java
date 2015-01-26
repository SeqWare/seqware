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
package net.sourceforge.seqware.pipeline.deciders;

import static org.junit.Assert.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import io.seqware.Reports;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.sourceforge.seqware.common.err.NotFoundException;
import net.sourceforge.seqware.common.hibernate.FindAllTheFiles.Header;
import net.sourceforge.seqware.common.metadata.MetadataWS;
import net.sourceforge.seqware.common.model.ExperimentAttribute;
import net.sourceforge.seqware.common.model.FileAttribute;
import net.sourceforge.seqware.common.model.IUSAttribute;
import net.sourceforge.seqware.common.model.LaneAttribute;
import net.sourceforge.seqware.common.model.ProcessingAttribute;
import net.sourceforge.seqware.common.model.SampleAttribute;
import net.sourceforge.seqware.common.model.SequencerRunAttribute;
import net.sourceforge.seqware.common.model.StudyAttribute;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.maptools.ReservedIniKeys;
import net.sourceforge.seqware.common.util.testtools.BasicTestDatabaseCreator;
import net.sourceforge.seqware.pipeline.plugins.PluginTest;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.lf5.PassingLogRecordFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * <p>
 * BasicDeciderTest class.
 * </p>
 *
 * @author boconnor, dyuen
 * @version $Id: $Id
 * @since 0.13.3
 */
public class BasicDeciderTest extends PluginTest {

    /**
     * Write JSON representations of Attributes map so that we can ensure that deciders are given a superset of attributes for backwards
     * compatibility
     */
    private static final boolean WRITE_JSON_ATTRIBUTES = false;

    private final List<String> fastq_gz = new ArrayList<>();

    private final BasicTestDatabaseCreator dbCreator = new BasicTestDatabaseCreator();

    @BeforeClass
    public static void beforeClass() {
        BasicTestDatabaseCreator.resetDatabaseWithUsers();
        Reports.triggerProvenanceReport();
    }

    @Before
    @Override
    public void setUp() {
        super.setUp();
        instance = new TestingDecider();
        // instance = new BasicDecider();
        instance.setMetadata(metadata);
        fastq_gz.add("chemical/seq-na-fastq-gzip");
    }

    @Test
    public void testIsWorkflowRunWithFailureStatus() {
        TestingDecider decider = (TestingDecider) instance;
        decider.setMetaws((MetadataWS) metadata);
        boolean pendingStatus = decider.determineStatus(metadata.getWorkflowRun(6602).getStatus()) == BasicDecider.PREVIOUS_RUN_STATUS.OTHER;
        boolean failedStatus = decider.determineStatus(metadata.getWorkflowRun(6603).getStatus()) == BasicDecider.PREVIOUS_RUN_STATUS.FAILED;
        boolean completedStatus = decider.determineStatus(metadata.getWorkflowRun(6604).getStatus()) == BasicDecider.PREVIOUS_RUN_STATUS.COMPLETED;
        Assert.assertTrue("pending status was not false", pendingStatus == true);
        Assert.assertTrue("failed status was not true", failedStatus == true);
        Assert.assertTrue("completed status was not false", completedStatus == true);
    }

    @Test
    public void testListAllFiles() {
        // we need to use a valid workflow now that we filter with valid parent workflows
        // this is actually a bit misnamed, we return all files that are associated with all studies
        String[] params = { "--all", "--wf-accession", "4773", "--parent-wf-accessions",
                "2861,4767,4768,4769,4773,4780,4778,4775,4774,5692,6594,6595,6596,6597,6598,6599,6685,6692,2860,4", "--test" };
        launchAndCaptureOutput(params);
        // we need to override handleGroupByAttribute in order to count the number of expected files
        TestingDecider decider = (TestingDecider) instance;
        // we expect to see 133 files in total
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(),
                decider.getFileCount() == 80);
    }

    @Test
    public void testFilesForOneStudy() {
        String[] params = { "--study-name", "AbcCo_Exome_Sequencing", "--wf-accession", "4773", "--parent-wf-accessions",
                "2861,4767,4768,4769,4773,4780,4778,4775,4774,5692,6594,6595,6596,6597,6598,6599,6685,6692,2860", "--test" };
        launchAndCaptureOutput(params);
        // we need to override handleGroupByAttribute in order to count the number of expected files
        TestingDecider decider = (TestingDecider) instance;
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(),
                decider.getFileCount() == 17);
    }

    @Test
    public void testFilesForInvalidStudyName() {
        String[] params = { "--study-name", "_INVALID_STUDY_NAME_", "--wf-accession", "4773", "--parent-wf-accessions",
                "2861,4767,4768,4769,4773,4780,4778,4775,4774,5692,6594,6595,6596,6597,6598,6599,6685,6692,2860", "--test" };
        try {
            launchAndCaptureOutput(params);
            // we need to override handleGroupByAttribute in order to count the number of expected files
            TestingDecider decider = (TestingDecider) instance;
            Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(),
                    decider.getFileCount() == 17);
        } catch (NotFoundException e) {
            assertEquals("The study with the name \"_INVALID_STUDY_NAME_\" could not be found.", e.getMessage());
        }
    }

    @Test
    public void testFilesForOneSample() {
        String[] params = { "--sample-name", "Exome_ABC015069_Test_2", "--wf-accession", "4773", "--parent-wf-accessions",
                "2861,4767,4768,4769,4773,4780,4778,4775,4774,5692,6594,6595,6596,6597,6598,6599,6685,6692,2860", "--test" };
        launchAndCaptureOutput(params);
        // we need to override handleGroupByAttribute in order to count the number of expected files
        TestingDecider decider = (TestingDecider) instance;
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(),
                decider.getFileCount() == 17);
    }

    @Test
    public void testFilesForInvalidRootSample() {
        String[] params = { "--sample-name", "Exome_ABC015069_Test_2", "--wf-accession", "4773", "--root-sample-name",
                "OBC_0001_DOES_NOT_EXIST", "--parent-wf-accessions",
                "2861,4767,4768,4769,4773,4780,4778,4775,4774,5692,6594,6595,6596,6597,6598,6599,6685,6692,2860", "--test" };
        try {
            launchAndCaptureOutput(params);
            // we need to override handleGroupByAttribute in order to count the number of expected files
            TestingDecider decider = (TestingDecider) instance;
            Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(),
                    decider.getFileCount() == 17);
        } catch (NotFoundException e) {
            assertEquals("The sample with the name \"OBC_0001_DOES_NOT_EXIST\" could not be found.", e.getMessage());
        }
    }

    @Test
    public void testFilesForOneSequencerRun() {
        String[] params = { "--sequencer-run-name", "SRKDKJKLFJKLJ90039", "--wf-accession", "4773", "--parent-wf-accessions",
                "2861,4767,4768,4769,4773,4780,4778,4775,4774,5692,6594,6595,6596,6597,6598,6599,6685,6692,2860", "--test" };
        launchAndCaptureOutput(params);
        // we need to override handleGroupByAttribute in order to count the number of expected files
        TestingDecider decider = (TestingDecider) instance;
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(),
                decider.getFileCount() == 32);
    }

    @Test
    public void testFilesForInvalidSequencerRunName() {
        String[] params = { "--sequencer-run-name", "_INVALID_SEQUENCER_RUN_NAME_", "--wf-accession", "4773", "--parent-wf-accessions",
                "2861,4767,4768,4769,4773,4780,4778,4775,4774,5692,6594,6595,6596,6597,6598,6599,6685,6692,2860", "--test" };
        boolean testPassed = false;
        try {
            launchAndCaptureOutput(params);
            // we need to override handleGroupByAttribute in order to count the number of expected files
            TestingDecider decider = (TestingDecider) instance;
            Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(),
                    decider.getFileCount() == 32);
        }
        // The code we're testing here in MetadataWS doesn't return a message in this case, it throws a RuntimeException
        // with no message. So the test will pass if the RuntimeException is caught.
        catch (RuntimeException e) {
            testPassed = true;
        }
        if (testPassed)
            return;
        else
            fail("RuntimeException was expected due to invalid SequencerRun name.");
    }

    @Test
    public void testNumberOfChecks() {
        String[] params = { "--all", "--wf-accession", "6685", "--parent-wf-accessions", "4767", "--test" };
        launchAndCaptureOutput(params);
        // int launchesDetected = StringUtils.countMatches(redirected, "java -jar");
        // we need to override handleGroupByAttribute in order to count the number of expected files
        TestingDecider decider = (TestingDecider) instance;
        // we expect to see 133 files in total
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(),
                decider.getFileCount() == 3);
        // we expect to launch 3 times
        Assert.assertTrue("output does not contain the correct number of launches, we saw " + decider.getFinalChecks(),
                decider.getFinalChecks() == 3);
    }

    @Test
    public void testNumberOfChecksForAllFileTypes() {
        String[] params = {
                "--all",
                "--wf-accession",
                "4773",
                "--meta-types",
                "text/h-tumour,application/vcf-4-gzip,text/annovar-tags,application/zip-report-bundle,txt,chemical/seq-na-fastq-gzip,application/bam,text/vcf-4,chemical/seq-na-fastq",
                "--test" };
        launchAndCaptureOutput(params);
        // int launchesDetected = StringUtils.countMatches(redirected, "java -jar");
        // we need to override handleGroupByAttribute in order to count the number of expected files
        TestingDecider decider = (TestingDecider) instance;
        // we expect to see 133 files in total
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(),
                decider.getFileCount() == 80);
        // we expect to launch 3 times
        Assert.assertTrue("output does not contain the correct number of launches, we saw " + decider.getFinalChecks(),
                decider.getFinalChecks() == 80);
    }

    @Test
    public void testFinalCheckFailure() {
        // swap out the decider
        instance = new AlwaysBlockFinalCheckDecider();
        // instance = new BasicDecider();
        instance.setMetadata(metadata);

        // a AlwaysBlockFinalCheckDecider decider should never launch
        String[] params = { "--all", "--wf-accession", "6685", "--parent-wf-accessions", "4767", "--ignore-previous-runs", "--test" };
        launchAndCaptureOutput(params);
        // we need to override handleGroupByAttribute in order to count the number of expected files
        TestingDecider decider = (TestingDecider) instance;
        // we expect to see 133 files in total
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(),
                decider.getFileCount() == 3);
        // we expect to never launch with the halting decider
        Assert.assertTrue("output does not contain the correct number of launches, we saw " + decider.getLaunches(),
                decider.getLaunches() == 0);

        // swap back the decider
        instance = new TestingDecider();
        // instance = new BasicDecider();
        instance.setMetadata(metadata);
    }

    @Test
    public void testIgnorePreviousRuns() {
        // swap out the decider
        instance = new HaltingDecider();
        // instance = new BasicDecider();
        instance.setMetadata(metadata);

        // a halting decider should launch twice after denying one launch
        String[] params = { "--all", "--wf-accession", "6685", "--parent-wf-accessions", "4767", "--ignore-previous-runs", "--test" };
        launchAndCaptureOutput(params);
        // we need to override handleGroupByAttribute in order to count the number of expected files
        TestingDecider decider = (TestingDecider) instance;
        // we expect to see 133 files in total
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(),
                decider.getFileCount() == 3);
        // we expect to never launch with the halting decider
        Assert.assertTrue("output does not contain the correct number of launches, we saw " + decider.getLaunches(),
                decider.getLaunches() == 2);

        // swap back the decider
        instance = new TestingDecider();
        // instance = new BasicDecider();
        instance.setMetadata(metadata);
    }

    @Test
    public void testSEQWARE1298() {
        // swap out the decider
        instance = new HaltingDecider();
        // instance = new BasicDecider();
        instance.setMetadata(metadata);

        // a halting decider should launch twice after denying one launch
        String[] params = { "--all", "--wf-accession", "6685", "--parent-wf-accessions", "4767", "--test" };
        launchAndCaptureOutput(params);
        // we need to override handleGroupByAttribute in order to count the number of expected files
        TestingDecider decider = (TestingDecider) instance;
        // we expect to see 133 files in total, this changes due to file provenance report
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(),
                decider.getFileCount() == 3);
        // we expect to launch exactly twice
        Assert.assertTrue("output does not contain the correct number of launches, we saw " + decider.getLaunches(),
                decider.getLaunches() == 2);

        // swap back the decider
        instance = new TestingDecider();
        // instance = new BasicDecider();
        instance.setMetadata(metadata);
    }

    @Test
    public void testMetaTypes() {
        String[] params = { "--all", "--wf-accession", "4773", "--meta-types", "application/bam,text/vcf-4,chemical/seq-na-fastq-gzip",
                "--test" };
        launchAndCaptureOutput(params);
        TestingDecider decider = (TestingDecider) instance;
        // we expect to see 133 files in total
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(),
                decider.getFileCount() == 80);
        Assert.assertTrue("output does not contain the correct number of launches, we saw " + decider.getLaunches(),
                decider.getLaunches() == 61);
    }

    @Test
    public void testSEQWARE1297DoNotLaunchProcessingWorkflows() {
        String[] params = { "--sample", "Sample_Tumour", "--wf-accession", "2860", "--meta-types",
                "application/bam,text/vcf-4,chemical/seq-na-fastq-gzip", "--test" };
        launchAndCaptureOutput(params);
        TestingDecider decider = (TestingDecider) instance;
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(),
                decider.getFileCount() == 15);
        Assert.assertTrue("output does not contain the correct number of launches, we saw " + decider.getLaunches(),
                decider.getLaunches() == 12);
    }

    @Test
    public void testSEQWARE1297DoNotLaunchFailedWorkflows() {
        // trying to find a good test for this, doesn't look like there is one in the testing database?
        // there is one pending workflow_run as revealed by "select * from workflow_run  WHERE status = 'pending';"
        // however, it doesn't appear to be properly linked in
        // see
        // "select sh.*, s.* FROM sample_hierarchy sh , (select DISTINCT s.sample_id from workflow_run wr, ius_workflow_runs iwr, ius, sample s WHERE status = 'pending' AND wr.workflow_run_id=iwr.workflow_run_id AND iwr.ius_id=ius.ius_id AND ius.sample_id=s.sample_id) sq, sample s WHERE sh.sample_id=sq.sample_id AND s.sample_id=sh.parent_id;"
        String[] params = new String[] { "--sample", "", "--wf-accession", "4773", "--meta-types",
                "application/bam,text/vcf-4,chemical/seq-na-fastq-gzip", "--rerun-max", "10", "--test" };
        launchAndCaptureOutput(params);
        TestingDecider decider = (TestingDecider) instance;
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(),
                decider.getFileCount() == 47);
        Assert.assertTrue("output does not contain the correct number of launches, we saw " + decider.getLaunches(),
                decider.getLaunches() == 36);

        params = new String[] { "--sample", "", "--wf-accession", "4773", "--meta-types",
                "application/bam,text/vcf-4,chemical/seq-na-fastq-gzip", "--rerun-max", "1", "--test" };
        launchAndCaptureOutput(params);
        Assert.assertTrue("output 2 does not contain the correct number of files, we saw " + decider.getFileCount(),
                decider.getFileCount() == 47);
        Assert.assertTrue("output 2 does not contain the correct number of launches, we saw " + decider.getLaunches(),
                decider.getLaunches() == 34);
    }

    @Test
    public void testSEQWARE1918RerunMax0() {
        String[] params = new String[] { "--sample", "", "--wf-accession", "4773", "--meta-types",
                "application/bam,text/vcf-4,chemical/seq-na-fastq-gzip", "--rerun-max", "10", "--test" };
        launchAndCaptureOutput(params);
        TestingDecider decider = (TestingDecider) instance;
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(),
                decider.getFileCount() == 47);
        Assert.assertTrue("output does not contain the correct number of launches, we saw " + decider.getLaunches(),
                decider.getLaunches() == 36);

        params = new String[] { "--sample", "", "--wf-accession", "4773", "--meta-types",
                "application/bam,text/vcf-4,chemical/seq-na-fastq-gzip", "--rerun-max", "0", "--test" };
        launchAndCaptureOutput(params);
        Assert.assertTrue("output 2 does not contain the correct number of files, we saw " + decider.getFileCount(),
                decider.getFileCount() == 47);
        Assert.assertTrue("output 2 does not contain the correct number of launches, we saw " + decider.getLaunches(),
                decider.getLaunches() == 34);
    }

    @Test
    public void testSEQWARE1924FailedProcessing() {
        dbCreator.runUpdate("update workflow_run set status = 'completed' where sw_accession = 6481;");
        metadata.fileProvenanceReportTrigger();
        String[] params = new String[] { "--all", "", "--wf-accession", "4773", "--meta-types", "application/bam", "--force-run-all",
                "--test" };
        launchAndCaptureOutput(params);
        TestingDecider decider = (TestingDecider) instance;
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(),
                decider.getFileCount() == 80);
        Assert.assertTrue("output does not contain the correct number of launches, we saw " + decider.getLaunches(),
                decider.getLaunches() == 13);
    }

    @Test
    public void testDecidingWithAttributes() {
        BasicTestDatabaseCreator.resetDatabaseWithUsers();
        Reports.triggerProvenanceReport();
        
        // swap out the decider
        instance = new AttributeCheckingDecider();
        // instance = new BasicDecider();
        instance.setMetadata(metadata);
        AttributeCheckingDecider decider = (AttributeCheckingDecider) instance;

        // create a hierarchy so that we can check for parent samples
        dbCreator.runUpdate("insert into sample_hierarchy VALUES(11,12);");
        // create attributes on all relevant columns
        StudyAttribute studyAttribute = new StudyAttribute();
        studyAttribute.setTag("studyTag");
        studyAttribute.setValue("studyValue");
        metadata.annotateStudy(120, studyAttribute, false);
        ExperimentAttribute experimentAttribute = new ExperimentAttribute();
        experimentAttribute.setTag("experimentTag");
        experimentAttribute.setValue("experimentValue");
        metadata.annotateExperiment(4782, experimentAttribute, false);
        SampleAttribute sampleAttribute = new SampleAttribute();
        sampleAttribute.setTag("sampleTag");
        sampleAttribute.setValue("sampleValue");
        metadata.annotateSample(4783, sampleAttribute, false);
        SampleAttribute parentSampleAttribute = new SampleAttribute();
        parentSampleAttribute.setTag("parentSampleTag");
        parentSampleAttribute.setValue("parentSampleValue");
        metadata.annotateSample(6158, parentSampleAttribute, false);
        SequencerRunAttribute sequencerRunAttribute = new SequencerRunAttribute();
        sequencerRunAttribute.setTag("sequencerRunTag");
        sequencerRunAttribute.setValue("sequencerRunValue");
        metadata.annotateSequencerRun(4715, sequencerRunAttribute, false);
        metadata.annotateSequencerRun(47150, sequencerRunAttribute, false);
        LaneAttribute laneAttribute = new LaneAttribute();
        laneAttribute.setTag("laneTag");
        laneAttribute.setValue("laneValue");
        metadata.annotateLane(6105, laneAttribute, false);
        metadata.annotateLane(4788, laneAttribute, false);
        IUSAttribute iusAttribute = new IUSAttribute();
        iusAttribute.setTag("iusTag");
        iusAttribute.setValue("iusValue");
        metadata.annotateIUS(6106, iusAttribute, false);
        metadata.annotateIUS(4789, iusAttribute, false);
        ProcessingAttribute processingAttribute = new ProcessingAttribute();
        processingAttribute.setTag("processingTag");
        processingAttribute.setValue("processingValue");
        metadata.annotateProcessing(6104, processingAttribute, false);
        metadata.annotateProcessing(6063, processingAttribute, false);
        metadata.annotateProcessing(5988, processingAttribute, false);
        metadata.annotateProcessing(5981, processingAttribute, false);
        metadata.annotateProcessing(5978, processingAttribute, false);
        metadata.annotateProcessing(5876, processingAttribute, false);
        metadata.annotateProcessing(5873, processingAttribute, false);
        metadata.annotateProcessing(5868, processingAttribute, false);
        metadata.annotateProcessing(5166, processingAttribute, false);
        metadata.annotateProcessing(4821, processingAttribute, false);
        metadata.annotateProcessing(4787, processingAttribute, false);
        FileAttribute fileAttribute = new FileAttribute();
        fileAttribute.setTag("fileTag");
        fileAttribute.setValue("fileValue");
        metadata.annotateFile(6103, fileAttribute, false);
        metadata.annotateFile(6102, fileAttribute, false);
        metadata.annotateFile(6064, fileAttribute, false);
        metadata.annotateFile(6065, fileAttribute, false);
        metadata.annotateFile(6066, fileAttribute, false);
        metadata.annotateFile(6067, fileAttribute, false);
        metadata.annotateFile(5989, fileAttribute, false);
        metadata.annotateFile(5990, fileAttribute, false);
        metadata.annotateFile(5986, fileAttribute, false);
        metadata.annotateFile(5979, fileAttribute, false);
        metadata.annotateFile(5877, fileAttribute, false);
        metadata.annotateFile(5878, fileAttribute, false);
        metadata.annotateFile(5874, fileAttribute, false);
        metadata.annotateFile(5869, fileAttribute, false);
        metadata.annotateFile(5167, fileAttribute, false);
        metadata.annotateFile(4822, fileAttribute, false);
        metadata.annotateFile(4823, fileAttribute, false);
        metadata.annotateFile(4824, fileAttribute, false);
        metadata.annotateFile(4825, fileAttribute, false);
        metadata.annotateFile(4786, fileAttribute, false);
        metadata.annotateFile(4785, fileAttribute, false);
        // in this version of deciders, we need to refresh the report
        Reports.triggerProvenanceReport();
        // run decider and check that attributes are pulled back properly
        String[] params = {
                "--study-name",
                "AbcCo_Exome_Sequencing",
                "--wf-accession",
                "4",
                "--meta-types",
                "text/h-tumour,application/vcf-4-gzip,text/annovar-tags,application/zip-report-bundle,txt,chemical/seq-na-fastq-gzip,application/bam,text/vcf-4,chemical/seq-na-fastq",
                "--test" };
        launchAndCaptureOutput(params);
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(),
                decider.getFileCount() == 17);
        Assert.assertTrue("we didn't check the correct number of files, we checked " + decider.filesChecked, decider.filesChecked == 17);

        // swap back the decider
        instance = new TestingDecider();
        // instance = new BasicDecider();
        instance.setMetadata(metadata);

    }

    @Test
    public void testDecidingWithFilemetadata() {
        // swap out the decider
        instance = new FileMetadataDecider();
        // instance = new BasicDecider();
        instance.setMetadata(metadata);
        FileMetadataDecider decider = (FileMetadataDecider) instance;

        // update the database so targetted files will contain full metadata
        dbCreator
                .runUpdate("update file set md5sum = sw_accession + 42 WHERE sw_accession IN (SELECT file_swa from file_provenance_report WHERE study_swa=120);");
        dbCreator
                .runUpdate("update file set description = 'funky_description' WHERE sw_accession IN (SELECT file_swa from file_provenance_report WHERE study_swa=120);");
        dbCreator
                .runUpdate("update file set size = sw_accession + 1701 WHERE sw_accession IN (SELECT file_swa from file_provenance_report WHERE study_swa=120);");
        // in this version of deciders, we need to refresh the report
        Reports.triggerProvenanceReport();
        // use a special decider to ensure that filemetadata is populated
        String[] params = {
                "--study-name",
                "AbcCo_Exome_Sequencing",
                "--wf-accession",
                "4",
                "--meta-types",
                "text/h-tumour,application/vcf-4-gzip,text/annovar-tags,application/zip-report-bundle,txt,chemical/seq-na-fastq-gzip,application/bam,text/vcf-4,chemical/seq-na-fastq",
                "--test" };
        launchAndCaptureOutput(params);
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(),
                decider.getFileCount() == 17);
        Assert.assertTrue("we didn't check the correct number of files, we checked " + decider.filesChecked, decider.filesChecked == 17);

        // swap back the decider
        instance = new TestingDecider();
        // instance = new BasicDecider();
        instance.setMetadata(metadata);
    }

    public class AlwaysBlockFinalCheckDecider extends TestingDecider {

        @Override
        protected ReturnValue doFinalCheck(String commaSeparatedFilePaths, String commaSeparatedParentAccessions) {
            return new ReturnValue(ReturnValue.FAILURE);
        }

    }

    public class HaltingDecider extends TestingDecider {

        boolean haltedOnce = false;

        @Override
        protected ReturnValue doFinalCheck(String commaSeparatedFilePaths, String commaSeparatedParentAccessions) {
            super.doFinalCheck(commaSeparatedFilePaths, commaSeparatedParentAccessions);
            ReturnValue localRet;
            if (!haltedOnce) {
                haltedOnce = true;
                localRet = new ReturnValue(ReturnValue.FAILURE);
            } else {
                localRet = new ReturnValue(ReturnValue.SUCCESS);
            }
            return localRet;
        }

    }

    public class AttributeCheckingDecider extends TestingDecider {

        int filesChecked = 0;

        @Override
        protected boolean checkFileDetails(ReturnValue returnValue, FileMetadata fm) {
            filesChecked++;
            int file_swa = Integer.valueOf(returnValue.getAttribute(Header.FILE_SWA.getTitle()));
            try {
                String attribute = returnValue.getAttribute(Header.STUDY_TAG_PREFIX.getTitle() + "studyTag");
                Assert.assertTrue("studyTag attribute didn't make it for " + file_swa, attribute.equals("studyValue"));
                attribute = returnValue.getAttribute(Header.EXPERIMENT_TAG_PREFIX.getTitle() + "experimentTag");
                Assert.assertTrue("experimentTag attribute didn't make it for " + file_swa, attribute.equals("experimentValue"));
                attribute = returnValue.getAttribute(Header.PARENT_SAMPLE_TAG_PREFIX.getTitle() + "parentSampleTag.6158");
                Assert.assertTrue("parentSampleTag attribute didn't make it for " + file_swa, attribute.equals("parentSampleValue"));
                attribute = returnValue.getAttribute(Header.SAMPLE_TAG_PREFIX.getTitle() + "sampleTag");
                Assert.assertTrue("sampleTag attribute didn't make it for " + file_swa, attribute.equals("sampleValue"));
                attribute = returnValue.getAttribute(Header.SEQUENCER_RUN_TAG_PREFIX.getTitle() + "sequencerRunTag");
                Assert.assertTrue("sequencerRunTag attribute didn't make it for " + file_swa, attribute.equals("sequencerRunValue"));
                attribute = returnValue.getAttribute(Header.LANE_TAG_PREFIX.getTitle() + "laneTag");
                Assert.assertTrue("lane attribute didn't make it for " + file_swa, attribute.equals("laneValue"));
                attribute = returnValue.getAttribute(Header.IUS_TAG_PREFIX.getTitle() + "iusTag");
                Assert.assertTrue("ius attribute didn't make it for " + file_swa, attribute.equals("iusValue"));
                attribute = returnValue.getAttribute(Header.PROCESSING_TAG_PREFIX.getTitle() + "processingTag");
                Assert.assertTrue("processing attribute didn't make it for " + file_swa, attribute.equals("processingValue"));
                attribute = returnValue.getAttribute(Header.FILE_TAG_PREFIX.getTitle() + "fileTag");
                Assert.assertTrue("file attribute didn't make it for " + file_swa, attribute.equals("fileValue"));
            } catch (NullPointerException ex) {
                Assert.assertTrue("returnvalue attributes missing: " + returnValue.getAttributes().toString(), false);
            }
            try {
                if (BasicDeciderTest.WRITE_JSON_ATTRIBUTES) {
                    Gson gson = new GsonBuilder().create();
                    String toJson = gson.toJson(returnValue.getAttributes());
                    File file = new File(file_swa + ".json");
                    FileUtils.writeStringToFile(file, toJson);
                } else {
                    // ensure that json check files contain a subset of the decider attributes
                    String query = FileUtils.readFileToString(new File((BasicDeciderTest.class.getResource(file_swa + ".json").getPath())));
                    Gson gson = new GsonBuilder().create();
                    Type fooType = new TypeToken<Map<String, String>>() {
                    }.getType();
                    Map<String, String> oldAttributes = gson.fromJson(query, fooType);
                    // remove last modified time from both since time changes when annotating
                    oldAttributes.remove(Header.PROCESSING_DATE.getTitle());
                    returnValue.getAttributes().remove(Header.PROCESSING_DATE.getTitle());
                    boolean contained = returnValue.getAttributes().entrySet().containsAll(oldAttributes.entrySet());
                    if (!contained) {
                        String oldStr = oldAttributes.toString();
                        String newStr = returnValue.getAttributes().toString();
                        oldAttributes.entrySet().removeAll(returnValue.getAttributes().entrySet());
                        String diff = oldAttributes.toString();
                        Assert.assertTrue("old returnvalue is not a subset: " + oldStr + "\n" + newStr + "\n" + diff, false);
                    }
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            return false;
        }
    }

    public class FileMetadataDecider extends TestingDecider {

        int filesChecked = 0;

        @Override
        protected boolean checkFileDetails(ReturnValue returnValue, FileMetadata fm) {
            filesChecked++;
            int file_swa = Integer.valueOf(returnValue.getAttribute(Header.FILE_SWA.getTitle()));
            Assert.assertTrue("file path is empty " + file_swa, !(fm.getFilePath() == null) && !fm.getFilePath().isEmpty());
            // FindAllTheFiles doesn't actually populate this information, go figure
            Assert.assertTrue("file md5sum is wrong for " + file_swa, fm.getMd5sum().equals(Integer.toString(file_swa + 42)));
            Assert.assertTrue("file size is wrong ", fm.getSize().equals((long) file_swa + 1701));
            Assert.assertTrue("file description is empty " + file_swa, !(fm.getDescription() == null) && !fm.getDescription().isEmpty());
            Assert.assertTrue("meta type is empty" + file_swa, !(fm.getMetaType() == null) && !fm.getMetaType().isEmpty());
            return false;
        }
    }

    public class TestingDecider extends BasicDecider {

        private final Set<String> fileSet = new HashSet<>();
        private int finalChecks = 0;
        private int launches = 0;

        public int getLaunches() {
            return launches;
        }

        public int getFileCount() {
            return fileSet.size();
        }

        public int getFinalChecks() {
            return finalChecks;
        }

        @Override
        protected boolean reportLaunch() {
            launches = launches + 1;
            return false;
        }

        @Override
        protected ReturnValue doFinalCheck(String commaSeparatedFilePaths, String commaSeparatedParentAccessions) {
            ReturnValue returnValue = super.doFinalCheck(commaSeparatedFilePaths, commaSeparatedParentAccessions);
            finalChecks = finalChecks + 1;
            return returnValue;
        }

        @Override
        public ReturnValue init() {
            Log.debug("INIT");
            fileSet.clear(); // reset count
            finalChecks = 0;
            launches = 0;

            // this.setHeader(Header.IUS_SWA);
            // this.setMetaType(Arrays.asList("application/bam"));
            // allows anything defined on the command line to override the 'defaults' here.
            ReturnValue val = super.init();
            return val;

        }

        @Override
        protected String handleGroupByAttribute(String attribute) {
            fileSet.add(attribute);
            Log.debug("GROUP BY ATTRIBUTE: " + getHeader().getTitle() + " " + attribute);
            return attribute;
        }

        @Override
        protected boolean checkFileDetails(ReturnValue returnValue, FileMetadata fm) {
            Log.debug("CHECK FILE DETAILS:" + fm);
            // pathToAttributes.put(fm.getFilePath(), returnValue);
            return super.checkFileDetails(returnValue, fm);
        }

        @Override
        protected Map<String, String> modifyIniFile(String commaSeparatedFilePaths, String commaSeparatedParentAccessions) {
            Log.debug("INI FILE:" + commaSeparatedFilePaths);

            Map<String, String> iniFileMap = new TreeMap<>();
            iniFileMap.put(ReservedIniKeys.INPUT_FILE.getKey(), commaSeparatedFilePaths);

            return iniFileMap;
        }
    }

    @Test
    public void testIsContained_Same() {
        TestingDecider decider = (TestingDecider) instance;
        decider.setMetaws((MetadataWS) metadata);
        decider.setMetaType(fastq_gz);

        List<String> filesToRun = new ArrayList<>();
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R1_001_index8.fastq.gz");
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R2_001_index8.fastq.gz");
        int workflowRunAcc = 6654;

        // assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
        Assert.assertTrue(((BasicDecider) instance).isToRunContained(metadata.getWorkflowRun(workflowRunAcc).getInputFileAccessions(),
                filesToRun));
    }

    @Test
    public void testIsContained_SameSize_different_set() {
        TestingDecider decider = (TestingDecider) instance;
        decider.setMetaws((MetadataWS) metadata);
        decider.setMetaType(fastq_gz);

        List<String> filesToRun = new ArrayList<>();
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R1_001_index8.fastq.gz");
        filesToRun.add("s3://non_matching_file");
        int workflowRunAcc = 6654;

        // assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
        Assert.assertTrue(!((BasicDecider) instance).isToRunContained(metadata.getWorkflowRun(workflowRunAcc).getInputFileAccessions(),
                filesToRun));
    }

    @Test
    public void testIsContained_More() {
        TestingDecider decider = (TestingDecider) instance;
        decider.setMetaws((MetadataWS) metadata);
        decider.setMetaType(fastq_gz);

        List<String> filesToRun = new ArrayList<>();
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R1_001_index8.fastq.gz");
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R2_001_index8.fastq.gz");
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R3_001_index8.fastq.gz");
        int workflowRunAcc = 6654;

        // assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
        Assert.assertTrue(!((BasicDecider) instance).isToRunContained(metadata.getWorkflowRun(workflowRunAcc).getInputFileAccessions(),
                filesToRun));
    }

    @Test
    public void testIsContained_Less() {
        TestingDecider decider = (TestingDecider) instance;
        decider.setMetaws((MetadataWS) metadata);
        decider.setMetaType(fastq_gz);

        List<String> filesToRun = new ArrayList<>();
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R1_001_index8.fastq.gz");
        int workflowRunAcc = 6654;

        // assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
        Assert.assertTrue(((BasicDecider) instance).isToRunContained(metadata.getWorkflowRun(workflowRunAcc).getInputFileAccessions(),
                filesToRun));
    }

    /**
     * <p>
     * testCompareWorkflowRunFiles_Same.
     * </p>
     */
    @Test
    public void testCompareWorkflowRunFiles_Same() {
        TestingDecider decider = (TestingDecider) instance;
        decider.setMetaws((MetadataWS) metadata);
        decider.setMetaType(fastq_gz);

        List<String> filesToRun = new ArrayList<>();
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R1_001_index8.fastq.gz");
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R2_001_index8.fastq.gz");
        int workflowRunAcc = 6654;

        // assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
        Assert.assertTrue(((BasicDecider) instance).compareWorkflowRunFiles(metadata.getWorkflowRun(workflowRunAcc)
                .getInputFileAccessions(), filesToRun) == BasicDecider.FILE_STATUS.SAME_FILES);
    }

    /**
     * <p>
     * testCompareWorkflowRunFiles_Same.
     * </p>
     */
    @Test
    public void testCompareWorkflowRunFiles_BothEmpty() {
        TestingDecider decider = (TestingDecider) instance;
        decider.setMetaws((MetadataWS) metadata);

        List<String> filesToRun = new ArrayList<>();

        // assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
        Assert.assertTrue(((BasicDecider) instance).compareWorkflowRunFiles(new HashSet<Integer>(), filesToRun) == BasicDecider.FILE_STATUS.SAME_FILES);
    }

    /**
     * <p>
     * testCompareWorkflowRunFiles_Bigger.
     * </p>
     */
    @Test
    public void testCompareWorkflowRunFiles_Bigger() {
        TestingDecider decider = (TestingDecider) instance;
        decider.setMetaws((MetadataWS) metadata);
        decider.setMetaType(fastq_gz);

        List<String> filesToRun = new ArrayList<>();
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R1_001_index8.fastq.gz");
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R2_001_index8.fastq.gz");
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R3_001_index8.fastq.gz");
        int workflowRunAcc = 6654;

        // assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
        Assert.assertTrue(((BasicDecider) instance).compareWorkflowRunFiles(metadata.getWorkflowRun(workflowRunAcc)
                .getInputFileAccessions(), filesToRun) == BasicDecider.FILE_STATUS.PAST_SUBSET_OR_INTERSECTION);
    }

    /**
     * <p>
     * testCompareWorkflowRunFiles_SameButDifferent.
     * </p>
     */
    @Test
    public void testCompareWorkflowRunFiles_SameButDifferent() {
        TestingDecider decider = (TestingDecider) instance;
        decider.setMetaws((MetadataWS) metadata);
        decider.setMetaType(fastq_gz);

        List<String> filesToRun = new ArrayList<>();
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R1_001_index8.fastq.gz");
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R3_001_index8.fastq.gz");
        int workflowRunAcc = 6654;

        // assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
        Assert.assertTrue(((BasicDecider) instance).compareWorkflowRunFiles(metadata.getWorkflowRun(workflowRunAcc)
                .getInputFileAccessions(), filesToRun) == BasicDecider.FILE_STATUS.PAST_SUBSET_OR_INTERSECTION);
    }

    /**
     * <p>
     * testCompareWorkflowRunFiles_Smaller.
     * </p>
     */
    @Test
    public void testCompareWorkflowRunFiles_Smaller() {
        TestingDecider decider = (TestingDecider) instance;
        decider.setMetaws((MetadataWS) metadata);
        decider.setMetaType(fastq_gz);

        List<String> filesToRun = new ArrayList<>();
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R1_001_index8.fastq.gz");
        int workflowRunAcc = 6654;
        // assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
        Set<Integer> inputFiles = metadata.getWorkflowRun(workflowRunAcc).getInputFileAccessions();
        Assert.assertTrue(((BasicDecider) instance).compareWorkflowRunFiles(inputFiles, filesToRun) == BasicDecider.FILE_STATUS.PAST_SUPERSET);
    }

    /**
     * <p>
     * testCompareWorkflowRunFiles_Smaller.
     * </p>
     */
    @Test
    public void testCompareWorkflowRunFiles_Disjoint() {
        TestingDecider decider = (TestingDecider) instance;
        decider.setMetaws((MetadataWS) metadata);
        decider.setMetaType(fastq_gz);

        List<String> filesToRun = new ArrayList<>();
        filesToRun.add("s3://garbage.gz");
        int workflowRunAcc = 6654;

        // assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
        Assert.assertTrue(((BasicDecider) instance).compareWorkflowRunFiles(metadata.getWorkflowRun(workflowRunAcc)
                .getInputFileAccessions(), filesToRun) == BasicDecider.FILE_STATUS.DISJOINT_SETS);
    }

    /**
     * Don't use the output of this thing unless you really really have to stdout can change a lot
     *
     * @param params
     * @return
     */
    protected String launchAndCaptureOutput(String[] params) {
        ByteArrayOutputStream testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
        launchPlugin(params);
        String redirected = testOut.toString();
        System.setOut(System.out);
        return redirected;
    }
}
