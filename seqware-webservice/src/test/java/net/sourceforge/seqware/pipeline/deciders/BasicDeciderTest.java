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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import net.sourceforge.seqware.common.metadata.MetadataWS;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.deciders.BasicDecider.FILE_STATUS;
import net.sourceforge.seqware.pipeline.plugins.PluginTest;
import org.junit.*;

/**
 * <p>BasicDeciderTest class.</p>
 *
 * @author boconnor, dyuen
 * @version $Id: $Id
 * @since 0.13.3
 */
public class BasicDeciderTest extends PluginTest {
    
    private final List<String> fastq_gz = new ArrayList<String>();

    @Before
    @Override
    public void setUp() {
        super.setUp();
        instance = new TestingDecider();
        //instance = new BasicDecider();
        instance.setMetadata(metadata);
        fastq_gz.add("chemical/seq-na-fastq-gzip");
    }
    
    @Test 
    public void testIsWorkflowRunWithFailureStatus(){
        TestingDecider decider = (TestingDecider) instance;
        decider.setMetaws((MetadataWS)metadata);
        boolean pendingStatus = decider.determineStatus(6602) == BasicDecider.PREVIOUS_RUN_STATUS.OTHER;
        boolean failedStatus = decider.determineStatus(6603) == BasicDecider.PREVIOUS_RUN_STATUS.FAILED;
        boolean completedStatus = decider.determineStatus(6604) == BasicDecider.PREVIOUS_RUN_STATUS.COMPLETED;
        Assert.assertTrue("pending status was not false", pendingStatus == true);
        Assert.assertTrue("failed status was not true", failedStatus == true);
        Assert.assertTrue("completed status was not false", completedStatus == true);
    }

    @Test
    public void testListAllFiles() {
        // this is actually a bit misnamed, we return all files that are associated with all studies
        String[] params = {"--all", "--wf-accession", "4", "--parent-wf-accessions", "5", "--test"};
        launchAndCaptureOutput(params);
        // we need to override handleGroupByAttribute in order to count the number of expected files
        TestingDecider decider = (TestingDecider) instance;
        // we expect to see 133 files in total
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(), decider.getFileCount() == 133);
    }

    @Test
    public void testFilesForOneStudy() {
        String[] params = {"--study-name", "AbcCo_Exome_Sequencing", "--wf-accession", "4", "--parent-wf-accessions", "5", "--test"};
        launchAndCaptureOutput(params);
        // we need to override handleGroupByAttribute in order to count the number of expected files
        TestingDecider decider = (TestingDecider) instance;
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(), decider.getFileCount() == 21);
    }

    @Test
    public void testFilesForOneSample() {
        String[] params = {"--sample-name", "Exome_ABC015069_Test_2", "--wf-accession", "4", "--parent-wf-accessions", "5", "--test"};
        launchAndCaptureOutput(params);
        // we need to override handleGroupByAttribute in order to count the number of expected files
        TestingDecider decider = (TestingDecider) instance;
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(), decider.getFileCount() == 21);
    }

    @Test
    public void testFilesForOneSequencerRun() {
        String[] params = {"--sequencer-run-name", "SRKDKJKLFJKLJ90039", "--wf-accession", "4", "--parent-wf-accessions", "5", "--test"};
        launchAndCaptureOutput(params);
        // we need to override handleGroupByAttribute in order to count the number of expected files
        TestingDecider decider = (TestingDecider) instance;
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(), decider.getFileCount() == 42);
    }
    
    @Test
    public void testNumberOfChecks() {
        String[] params = {"--all", "--wf-accession", "6685", "--parent-wf-accessions", "4767", "--test"};
        launchAndCaptureOutput(params);
        //int launchesDetected = StringUtils.countMatches(redirected, "java -jar");
        // we need to override handleGroupByAttribute in order to count the number of expected files
        TestingDecider decider = (TestingDecider) instance;
        // we expect to see 133 files in total
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(), decider.getFileCount() == 133);
        // we expect to launch 3 times 
        Assert.assertTrue("output does not contain the correct number of launches, we saw " + decider.getFinalChecks(), decider.getFinalChecks() == 3);
    }
    
    @Test
    public void testNumberOfChecksForAllFileTypes() {
        String[] params = {"--all", "--wf-accession", "4773", "--meta-types", "text/h-tumour,application/vcf-4-gzip,text/annovar-tags,application/zip-report-bundle,txt,chemical/seq-na-fastq-gzip,application/bam,text/vcf-4,chemical/seq-na-fastq", "--test"};
        launchAndCaptureOutput(params);
        //int launchesDetected = StringUtils.countMatches(redirected, "java -jar");
        // we need to override handleGroupByAttribute in order to count the number of expected files
        TestingDecider decider = (TestingDecider) instance;
        // we expect to see 133 files in total
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(), decider.getFileCount() == 133);
        // we expect to launch 3 times 
        Assert.assertTrue("output does not contain the correct number of launches, we saw " + decider.getFinalChecks(), decider.getFinalChecks() == 133);
    }
    
    @Test
    public void testForceAll() {
        // swap out the decider
        instance = new HaltingDecider();
        //instance = new BasicDecider();
        instance.setMetadata(metadata);
        
        // a halting decider should launch twice after denying one launch, but when force-run-all is used, it goes back to 3
        String[] params = {"--all", "--wf-accession", "6685", "--parent-wf-accessions", "4767", "--force-run-all", "--test"};
        launchAndCaptureOutput(params);
        // we need to override handleGroupByAttribute in order to count the number of expected files
        TestingDecider decider = (TestingDecider) instance;
        // we expect to see 133 files in total
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(), decider.getFileCount() == 133);
        // we expect to never launch with the halting decider 
        Assert.assertTrue("output does not contain the correct number of launches, we saw " + decider.getLaunches(), decider.getLaunches() == 3);
        
        // swap back the decider
        instance = new TestingDecider();
        //instance = new BasicDecider();
        instance.setMetadata(metadata);
    }
    
    @Test
    public void testSEQWARE1298() {
        // swap out the decider
        instance = new HaltingDecider();
        //instance = new BasicDecider();
        instance.setMetadata(metadata);
        
        // a halting decider should launch twice after denying one launch
        String[] params = {"--all", "--wf-accession", "6685", "--parent-wf-accessions", "4767", "--test"};
        launchAndCaptureOutput(params);
        // we need to override handleGroupByAttribute in order to count the number of expected files
        TestingDecider decider = (TestingDecider) instance;
        // we expect to see 133 files in total
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(), decider.getFileCount() == 133);
        // we expect to launch exactly twice 
        Assert.assertTrue("output does not contain the correct number of launches, we saw " + decider.getLaunches(), decider.getLaunches() == 2);
        
        // swap back the decider
        instance = new TestingDecider();
        //instance = new BasicDecider();
        instance.setMetadata(metadata);
    }
    
    @Test
    public void testMetaTypes() {
        String[] params = {"--all", "--wf-accession", "4773", "--meta-types", "application/bam,text/vcf-4,chemical/seq-na-fastq-gzip", "--test"};
        launchAndCaptureOutput(params);
        TestingDecider decider = (TestingDecider) instance;
        // we expect to see 133 files in total
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(), decider.getFileCount() == 133);
        Assert.assertTrue("output does not contain the correct number of launches, we saw " + decider.getLaunches(), decider.getLaunches() == 96);
    }
    
    @Test
    public void testSEQWARE1297DoNotLaunchProcessingWorkflows() {
        String[] params = {"--sample", "Sample_Tumour", "--wf-accession", "2860", "--meta-types", "application/bam,text/vcf-4,chemical/seq-na-fastq-gzip", "--test"};
        launchAndCaptureOutput(params);
        TestingDecider decider = (TestingDecider) instance;
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(), decider.getFileCount() == 39);
        Assert.assertTrue("output does not contain the correct number of launches, we saw " + decider.getLaunches(), decider.getLaunches() == 20);
    }
    
    
    @Test
    public void testSEQWARE1297DoNotLaunchFailedWorkflows() {
        TestingDecider decider = (TestingDecider) instance;
        // trying to find a good test for this, doesn't look like there is one in the testing database?
        // there is one pending workflow_run as revealed by "select * from workflow_run  WHERE status = 'pending';"
        // however, it doesn't appear to be properly linked in 
        // see "select sh.*, s.* FROM sample_hierarchy sh , (select DISTINCT s.sample_id from workflow_run wr, ius_workflow_runs iwr, ius, sample s WHERE status = 'pending' AND wr.workflow_run_id=iwr.workflow_run_id AND iwr.ius_id=ius.ius_id AND ius.sample_id=s.sample_id) sq, sample s WHERE sh.sample_id=sq.sample_id AND s.sample_id=sh.parent_id;"
        String[] params = new String[]{"--sample", "", "--wf-accession", "4773", "--meta-types", "application/bam,text/vcf-4,chemical/seq-na-fastq-gzip", "--rerun-max", "10", "--test"};
        launchAndCaptureOutput(params);
        decider = (TestingDecider) instance;
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(), decider.getFileCount() == 68);
        Assert.assertTrue("output does not contain the correct number of launches, we saw " + decider.getLaunches(), decider.getLaunches() == 57);
        
        params = new String[]{"--sample", "", "--wf-accession", "4773", "--meta-types", "application/bam,text/vcf-4,chemical/seq-na-fastq-gzip", "--rerun-max", "1", "--test"};
        launchAndCaptureOutput(params);
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(), decider.getFileCount() == 68);
        Assert.assertTrue("output does not contain the correct number of launches, we saw " + decider.getLaunches(), decider.getLaunches() == 57);
    }
    
    public class HaltingDecider extends TestingDecider{
        boolean haltedOnce = false;
        @Override
        protected ReturnValue doFinalCheck(String commaSeparatedFilePaths, String commaSeparatedParentAccessions) {
            super.doFinalCheck(commaSeparatedFilePaths, commaSeparatedParentAccessions);
            ReturnValue ret;
            if (!haltedOnce){
                haltedOnce = true;
                ret = new ReturnValue(ReturnValue.FAILURE);
            } else{
                ret = new ReturnValue(ReturnValue.SUCCESS);
            }
            return ret;
        }
        
    }

    public class TestingDecider extends BasicDecider {

        private Set<String> fileSet = new HashSet<String>();
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
            
            //this.setHeader(Header.IUS_SWA);
            //this.setMetaType(Arrays.asList("application/bam"));

            //allows anything defined on the command line to override the 'defaults' here.
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
            //pathToAttributes.put(fm.getFilePath(), returnValue);
            return super.checkFileDetails(returnValue, fm);
        }

        @Override
        protected Map<String, String> modifyIniFile(String commaSeparatedFilePaths, String commaSeparatedParentAccessions) {
            Log.debug("INI FILE:" + commaSeparatedFilePaths);

            Map<String, String> iniFileMap = new TreeMap<String, String>();
            iniFileMap.put("input_file", commaSeparatedFilePaths);

            return iniFileMap;
        }
    }
    

    @Test
    public void testIsContained_Same() {
        TestingDecider decider = (TestingDecider) instance;
        decider.setMetaws((MetadataWS)metadata);
        decider.setMetaType(fastq_gz);
        
        List<String> filesToRun = new ArrayList<String>();
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R1_001_index8.fastq.gz");
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R2_001_index8.fastq.gz");
        int workflowRunAcc = 6654;
        

        //assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
	Assert.assertTrue(((BasicDecider)instance).isToRunContained(workflowRunAcc, filesToRun));
    }
    
    @Test
    public void testIsContained_SameSize_different_set() {
        TestingDecider decider = (TestingDecider) instance;
        decider.setMetaws((MetadataWS)metadata);
        decider.setMetaType(fastq_gz);
        
        List<String> filesToRun = new ArrayList<String>();
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R1_001_index8.fastq.gz");
        filesToRun.add("s3://non_matching_file");
        int workflowRunAcc = 6654;
        

        //assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
	Assert.assertTrue(!((BasicDecider)instance).isToRunContained(workflowRunAcc, filesToRun));
    }
    
    @Test
    public void testIsContained_More() {
        TestingDecider decider = (TestingDecider) instance;
        decider.setMetaws((MetadataWS)metadata);
        decider.setMetaType(fastq_gz);
        
        List<String> filesToRun = new ArrayList<String>();
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R1_001_index8.fastq.gz");
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R2_001_index8.fastq.gz");
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R3_001_index8.fastq.gz");
        int workflowRunAcc = 6654;
        

        //assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
	Assert.assertTrue(!((BasicDecider)instance).isToRunContained(workflowRunAcc, filesToRun));
    }
    
    @Test
    public void testIsContained_Less() {
        TestingDecider decider = (TestingDecider) instance;
        decider.setMetaws((MetadataWS)metadata);
        decider.setMetaType(fastq_gz);
        
        List<String> filesToRun = new ArrayList<String>();
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R1_001_index8.fastq.gz");
        int workflowRunAcc = 6654;
        

        //assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
	Assert.assertTrue(((BasicDecider)instance).isToRunContained(workflowRunAcc, filesToRun));
    }
    
    /**
     * <p>testCompareWorkflowRunFiles_Same.</p>
     */
    @Test
    public void testCompareWorkflowRunFiles_Same() {
        TestingDecider decider = (TestingDecider) instance;
        decider.setMetaws((MetadataWS)metadata);
        decider.setMetaType(fastq_gz);
        
        List<String> filesToRun = new ArrayList<String>();
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R1_001_index8.fastq.gz");
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R2_001_index8.fastq.gz");
        int workflowRunAcc = 6654;
        

        //assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
	Assert.assertTrue(((BasicDecider)instance).compareWorkflowRunFiles(workflowRunAcc, filesToRun) == BasicDecider.FILE_STATUS.SAME_FILES_SAME_PATHS_3);
    }

    /**
     * <p>testCompareWorkflowRunFiles_Bigger.</p>
     */
    @Test
    public void testCompareWorkflowRunFiles_Bigger() {
        TestingDecider decider = (TestingDecider) instance;
        decider.setMetaws((MetadataWS)metadata);
        decider.setMetaType(fastq_gz);

        List<String> filesToRun = new ArrayList<String>();
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R1_001_index8.fastq.gz");
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R2_001_index8.fastq.gz");
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R3_001_index8.fastq.gz");
        int workflowRunAcc = 6654;

        //assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
	Assert.assertTrue(((BasicDecider)instance).compareWorkflowRunFiles(workflowRunAcc, filesToRun) == BasicDecider.FILE_STATUS.MORE_CURRENT_FILES_1);
    }

    /**
     * <p>testCompareWorkflowRunFiles_SameButDifferent.</p>
     */
    @Test
    public void testCompareWorkflowRunFiles_SameButDifferent() {
        TestingDecider decider = (TestingDecider) instance;
        decider.setMetaws((MetadataWS)metadata);
        decider.setMetaType(fastq_gz);

        List<String> filesToRun = new ArrayList<String>();
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R1_001_index8.fastq.gz");
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R3_001_index8.fastq.gz");
        int workflowRunAcc = 6654;

        //assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
	Assert.assertTrue(((BasicDecider)instance).compareWorkflowRunFiles(workflowRunAcc, filesToRun) == BasicDecider.FILE_STATUS.SAME_FILES_DIFFERENT_PATHS_2);
    }

    /**
     * <p>testCompareWorkflowRunFiles_Smaller.</p>
     */
    @Test
    public void testCompareWorkflowRunFiles_Smaller() {
        TestingDecider decider = (TestingDecider) instance;
        decider.setMetaws((MetadataWS)metadata);
        decider.setMetaType(fastq_gz);

        List<String> filesToRun = new ArrayList<String>();
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R1_001_index8.fastq.gz");
        int workflowRunAcc = 6654;

        //assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
	Assert.assertTrue(((BasicDecider)instance).compareWorkflowRunFiles(workflowRunAcc, filesToRun) == BasicDecider.FILE_STATUS.MORE_PAST_FILES_4);
    }

    /**
     * Don't use the output of this thing unless you really really have to
     * stdout can change a lot
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
