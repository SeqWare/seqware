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
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.plugins.PluginTest;
import org.apache.commons.lang.StringUtils;
import org.junit.*;

/**
 * <p>BasicDeciderTest class.</p>
 *
 * @author boconnor, dyuen
 * @version $Id: $Id
 * @since 0.13.3
 */
public class BasicDeciderTest extends PluginTest {

    /**
     * <p>Constructor for BasicDeciderTest.</p>
     */
    public BasicDeciderTest() {
    }

    @Before
    @Override
    public void setUp() {
        super.setUp();
        instance = new TestingDecider();
        //instance = new BasicDecider();
        instance.setMetadata(metadata);
    }

    @Test
    public void testListAllFiles() {
        // this is actually a bit misnamed, we return all files that are associated with all studies
        String[] params = {"--all", "--wf-accession", "4", "--parent-wf-accessions", "5", "--test"};
        String redirected = launchAndCaptureOutput(params);
        String[] split = redirected.split("study");
        // five studies in test DB mean the output splits into 7 parts
        Assert.assertTrue("output does not contain five studies", split.length == 7);
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
        // we expect to never launch with the halting decider 
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
        Assert.assertTrue("output does not contain the correct number of launches, we saw " + decider.getLaunches(), decider.getLaunches() == 95);
    }
    
    @Test
    public void testSEQWARE1297Failed() {
        TestingDecider decider = (TestingDecider) instance;
        
        String[] params = new String[]{"--all", "--wf-accession", "4773", "--meta-types", "application/bam,text/vcf-4,chemical/seq-na-fastq-gzip", "--rerun-max", "10", "--test"};
        launchAndCaptureOutput(params);
        decider = (TestingDecider) instance;
        // we expect to see 133 files in total
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(), decider.getFileCount() == 133);
        Assert.assertTrue("output does not contain the correct number of launches, we saw " + decider.getLaunches(), decider.getLaunches() == 95);
        
        params = new String[]{"--all", "--wf-accession", "4773", "--meta-types", "application/bam,text/vcf-4,chemical/seq-na-fastq-gzip", "--rerun-max", "1", "--test"};
        launchAndCaptureOutput(params);
        // we expect to see 133 files in total
        Assert.assertTrue("output does not contain the correct number of files, we saw " + decider.getFileCount(), decider.getFileCount() == 133);
        // ok, we're running five less files which seems to match "select w.sw_accession from workflow_run r, workflow w where status='failed' AND r.workflow_id = w.workflow_id;"
        Assert.assertTrue("output does not contain the correct number of launches, we saw " + decider.getLaunches(), decider.getLaunches() == 90);
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

    /**
     * The tests below were already here when I (Denis) started work on BasicDecider , but they don't seem to do anything.
     */
    
    /**
     * <p>testCompareWorkflowRunFiles_Same.</p>
     */
    @Test
    public void testCompareWorkflowRunFiles_Same() {
        List<String> filesToRun = new ArrayList<String>();
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R1_001_index8.fastq.gz");
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R2_001_index8.fastq.gz");
        String workflowRunAcc = "6654";

        //assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
//	assertFalse(((BasicDecider)instance).compareWorkflowRunFiles(workflowRunAcc, filesToRun));
    }

    /**
     * <p>testCompareWorkflowRunFiles_Bigger.</p>
     */
    @Test
    public void testCompareWorkflowRunFiles_Bigger() {

        List<String> filesToRun = new ArrayList<String>();
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R1_001_index8.fastq.gz");
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R2_001_index8.fastq.gz");
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R3_001_index8.fastq.gz");
        String workflowRunAcc = "6654";

        //assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
//	assertTrue(((BasicDecider)instance).compareWorkflowRunFiles(workflowRunAcc, filesToRun));
    }

    /**
     * <p>testCompareWorkflowRunFiles_SameButDifferent.</p>
     */
    @Test
    public void testCompareWorkflowRunFiles_SameButDifferent() {

        List<String> filesToRun = new ArrayList<String>();
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R1_001_index8.fastq.gz");
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R3_001_index8.fastq.gz");
        String workflowRunAcc = "6654";

        //assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
//	assertTrue(((BasicDecider)instance).compareWorkflowRunFiles(workflowRunAcc, filesToRun));
    }

    /**
     * <p>testCompareWorkflowRunFiles_Smaller.</p>
     */
    @Test
    public void testCompareWorkflowRunFiles_Smaller() {


        List<String> filesToRun = new ArrayList<String>();
        filesToRun.add("s3://abcco.uploads/s_G1_L001_R1_001_index8.fastq.gz");
        String workflowRunAcc = "6654";

        //assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
//	assertFalse(((BasicDecider)instance).compareWorkflowRunFiles(workflowRunAcc, filesToRun));
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
