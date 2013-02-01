/*
 * Copyright (C) 2013 SeqWare
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
package net.sourceforge.seqware.pipeline.plugins;

import java.util.Set;
import net.sourceforge.seqware.pipeline.plugins.batchmetadatainjection.ParseMisecFile;
import net.sourceforge.seqware.pipeline.plugins.batchmetadatainjection.RunInfo;
import net.sourceforge.seqware.pipeline.plugins.batchmetadatainjection.SampleInfo;
import org.junit.*;

/**
 *
 * @author mtaschuk
 */
public class BatchMetadataInjectionTest {

    private static String misecPath = null;

    public BatchMetadataInjectionTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        misecPath = BatchMetadataInjectionTest.class.getResource("SampleSheet.csv").getPath();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of parseMiSecFile method, of class BatchMetadataInjection.
     */
    @Test
    public void testParseMiSecFile() throws Exception {
        System.out.println("parseMiSecFile");
        ParseMisecFile instance = new ParseMisecFile(null);
        RunInfo run = instance.parseMiSecFile(misecPath);
        Assert.assertEquals("Incorrect Project Name", "Testdance_123to456", run.getStudyTitle());
        Assert.assertEquals("Incorrect Experiment Name", "TDHS_123to456", run.getExperimentName());
        Assert.assertEquals("Incorrect Workflow", "Resequencing", run.getWorkflowType());
        Assert.assertEquals("Incorrect Assay", "TruSeq DNA/RNA", run.getAssayType());
        
        Set<SampleInfo> samples = run.getLanes().iterator().next().getSamples();
        Assert.assertEquals("Incorrect number of samples", 6, samples.size());
//        SampleInfo sample = samples.get(0);
//        String[] archiveSample = new String[]{
//            "TCACAG", //barcode
//            "1", //lane
//            "TST1-002-1ARC", //name
//            "Homo sapiens", //organism
//            "TST1-002",//parent sample
//            "", //targeted resequencing
//            "", //template type
//            "", //tissue origin
//            "", //tissue preparation
//            "1", //region
//            "A"}; //tissue type
//        String[] biopsySample = new String[]{
//            "CGTAGT", //barcode
//            "1", //lane
//            "TST1-010-1BIO", //name
//            "Homo sapiens", //organism
//            "TST1-010",//parent sample
//            "", //targeted resequencing
//            "", //template type
//            "", //tissue origin
//            "", //tissue preparation
//            "1", //region
//            "P"
//        };
//        String[] bloodSample = new String[]{
//            "TTAGCG", //barcode
//            "1", //lane
//            "TST1-012-3BLD", //name
//            "Homo sapiens", //organism
//            "TST1-012",//parent sample
//            "", //targeted resequencing
//            "", //template type
//            "", //tissue origin
//            "Blood", //tissue preparation
//            "3", //region
//            "R"
//        };
//        assertSample(archiveSample, samples.get(0));
//        assertSample(biopsySample, samples.get(1));
//        assertSample(bloodSample, samples.get(2));



    }

//    private void assertSample(String[] sample, SampleInfo actualSample) {
//        Assert.assertEquals("Incorrect Barcode", sample[0], actualSample.getBarcode());
//        Assert.assertEquals("Incorrect Lane", sample[1], actualSample.getLane());
//        Assert.assertEquals("Incorrect Name",sample[2], actualSample.getName());
//        Assert.assertEquals("Incorrect Organism", sample[3], actualSample.getOrganism());
//        Assert.assertEquals("Incorrect Parent Sample", sample[4], actualSample.getParentSample());
//        Assert.assertEquals("Incorrect Targeted Resequencing", sample[5], actualSample.getTargetedResequencing());
//        Assert.assertEquals("Incorrect Template Type", sample[6], actualSample.getTemplateType());
//        Assert.assertEquals("Incorrect Tissue Origin", sample[7], actualSample.getTissueOrigin());
//        Assert.assertEquals("Incorrect Tissue Preparation", sample[8], actualSample.getTissuePreparation());
//        Assert.assertEquals("Incorrect Region", sample[9], actualSample.getTissueRegion());
//        Assert.assertEquals("Incorrect Tissue Type", sample[10], actualSample.getTissueType());
//    }
//    /**
//     * Test of parseMiSecData method, of class BatchMetadataInjection.
//     */
//    @Test
//    public void testParseMiSecData() throws Exception {
//        System.out.println("parseMiSecData");
//        BufferedReader freader = null;
//        BatchMetadataInjection instance = new BatchMetadataInjection();
//                instance.parseMiSecData(freader);
//    }
//
//    /**
//     * Test of parseMiSecHeader method, of class BatchMetadataInjection.
//     */
//    @Test
//    public void testParseMiSecHeader() throws Exception {
//        System.out.println("parseMiSecHeader");
//        BufferedReader freader = null;
//        BatchMetadataInjection instance = new BatchMetadataInjection();
//        Map expResult = null;
//        Map result = instance.parseMiSecHeader(freader);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}
