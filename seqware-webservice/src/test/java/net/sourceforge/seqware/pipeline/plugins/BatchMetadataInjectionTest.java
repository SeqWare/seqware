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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sourceforge.seqware.common.metadata.MetadataNoConnection;
import net.sourceforge.seqware.common.model.FileProvenanceParam;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.util.jsontools.JSONHelper;
import net.sourceforge.seqware.common.util.testtools.BasicTestDatabaseCreator;
import static net.sourceforge.seqware.pipeline.plugins.PluginTest.metadata;
import net.sourceforge.seqware.pipeline.plugins.batchmetadatainjection.LaneInfo;
import net.sourceforge.seqware.pipeline.plugins.batchmetadatainjection.ParseMiseqFile;
import net.sourceforge.seqware.pipeline.plugins.batchmetadatainjection.RunInfo;
import net.sourceforge.seqware.pipeline.plugins.batchmetadatainjection.SampleInfo;
import net.sourceforge.seqware.pipeline.plugins.batchmetadatainjection.TagValueUnit;
import net.sourceforge.seqware.pipeline.runner.PluginRunner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author mtaschuk
 * @author Raunaq Suri
 */
public class BatchMetadataInjectionTest extends ExtendedPluginTest {

    JSONHelper jsonHelper = new JSONHelper();
    private RunInfo originalObject, runInfo;
    List<Map<String, String>> fileReport;

    private final List<String> iusSwids = new ArrayList<>();
    private final List<String> laneSwids = new ArrayList<>();

    // Metadata object
    // Map<String, String> hm = ConfigTools.getSettings();
    // net.sourceforge.seqware.common.metadata.Metadata metadata = MetadataFactory.get(hm);

    private static String miseqPath = null;
    private static String inputJsonCorrect = null;
    private static String malformedJson = null;
    private static InputStream schema = null;

    private final String wfaccession = "2861";

    public BatchMetadataInjectionTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        BasicTestDatabaseCreator.resetDatabaseWithUsers();
        schema = BatchMetadataInjectionTest.class.getResourceAsStream("bmischema.json");
        miseqPath = BatchMetadataInjectionTest.class.getResource("SampleSheet.csv").getPath();
        inputJsonCorrect = BatchMetadataInjectionTest.class.getResource("input.json").getPath();
        malformedJson = BatchMetadataInjectionTest.class.getResource("malformedInput.json").getPath();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    @Override
    public void setUp() {
        instance = new BatchMetadataInjection();
        super.setUp();
    }

    /**
     * Tests to see if the json was validated correctly
     * 
     * @throws java.io.FileNotFoundException
     */
    @Test
    public void testValidJson() throws FileNotFoundException {
        Assert.assertFalse(jsonHelper.isJSONValid(schema, new FileInputStream(malformedJson)));
    }

    /**
     * Tests to see that the json was imported correctly
     * 
     * @throws IOException
     */

    @Test
    public void testImportJsonSequencerRun() throws IOException {
        System.out.println("Testing if importing a json file works");

        String[] bmiparams = { "--import-json-sequencer-run", inputJsonCorrect };

        PrintStream old = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        // Runs the Batch Metadata injection with the input json and importing a json run
        BatchMetadataInjection.main(bmiparams);
        String output = baos.toString();

        // Gets the IUSs from the stdout
        getStdOut(output);
        runFileLinkerPlugin(createFileLinkerFile(iusSwids), wfaccession);
        getData();
        writeToObjects();

        System.setOut(old);
        Assert.assertTrue("The JSON doesn't match", equals(originalObject, runInfo, inputJsonCorrect));
        System.out.println("The JSON matches");

    }

    /**
     * Test of parseMiseqFile method, of class BatchMetadataInjection.
     */
    @Test
    public void testParseMiseqFile() throws Exception {
        System.out.println("parseMiseqFile");
        HashMap<String, String> map = new HashMap<>();
        map.put("study_type", "4");
        map.put("library_type", "Type");
        map.put("library_source_template_type", "LSTT");
        map.put("targeted_resequencing", "TRS");
        map.put("tissue_origin", "TO");
        map.put("tissue_preparation", "TP");
        map.put("library_size_code", "12");

        ParseMiseqFile parseqinstance = new ParseMiseqFile(new MetadataNoConnection(), map, false);
        RunInfo run = parseqinstance.parseMiseqFile(miseqPath);
        Assert.assertEquals("Incorrect Project Name", "Testdance", run.getStudyTitle());
        Assert.assertEquals("Incorrect Experiment Name", "TDHS", run.getExperimentName());
        Assert.assertEquals("Incorrect Workflow", "Resequencing", run.getWorkflowType());
        Assert.assertEquals("Incorrect Assay", "TruSeq DNA/RNA", run.getAssayType());

        Set<SampleInfo> samples = run.getLanes().iterator().next().getSamples();
        Assert.assertEquals("Incorrect number of samples", 6, samples.size());
        // SampleInfo sample = samples.get(0);
        // String[] archiveSample = new String[]{
        // "TCACAG", //barcode
        // "1", //lane
        // "TST1-002-1ARC", //name
        // "Homo sapiens", //organism
        // "TST1-002",//parent sample
        // "", //targeted resequencing
        // "", //template type
        // "", //tissue origin
        // "", //tissue preparation
        // "1", //region
        // "A"}; //tissue type
        // String[] biopsySample = new String[]{
        // "CGTAGT", //barcode
        // "1", //lane
        // "TST1-010-1BIO", //name
        // "Homo sapiens", //organism
        // "TST1-010",//parent sample
        // "", //targeted resequencing
        // "", //template type
        // "", //tissue origin
        // "", //tissue preparation
        // "1", //region
        // "P"
        // };
        // String[] bloodSample = new String[]{
        // "TTAGCG", //barcode
        // "1", //lane
        // "TST1-012-3BLD", //name
        // "Homo sapiens", //organism
        // "TST1-012",//parent sample
        // "", //targeted resequencing
        // "", //template type
        // "", //tissue origin
        // "Blood", //tissue preparation
        // "3", //region
        // "R"
        // };
        // assertSample(archiveSample, samples.get(0));
        // assertSample(biopsySample, samples.get(1));
        // assertSample(bloodSample, samples.get(2));

    }

    // private void assertSample(String[] sample, SampleInfo actualSample) {
    // Assert.assertEquals("Incorrect Barcode", sample[0], actualSample.getBarcode());
    // Assert.assertEquals("Incorrect Lane", sample[1], actualSample.getLane());
    // Assert.assertEquals("Incorrect Name",sample[2], actualSample.getName());
    // Assert.assertEquals("Incorrect Organism", sample[3], actualSample.getOrganism());
    // Assert.assertEquals("Incorrect Parent Sample", sample[4], actualSample.getParentSample());
    // Assert.assertEquals("Incorrect Targeted Resequencing", sample[5], actualSample.getTargetedResequencing());
    // Assert.assertEquals("Incorrect Template Type", sample[6], actualSample.getTemplateType());
    // Assert.assertEquals("Incorrect Tissue Origin", sample[7], actualSample.getTissueOrigin());
    // Assert.assertEquals("Incorrect Tissue Preparation", sample[8], actualSample.getTissuePreparation());
    // Assert.assertEquals("Incorrect Region", sample[9], actualSample.getTissueRegion());
    // Assert.assertEquals("Incorrect Tissue Type", sample[10], actualSample.getTissueType());
    // }
    // /**
    // * Test of parseMiseqData method, of class BatchMetadataInjection.
    // */
    // @Test
    // public void testParseMiseqData() throws Exception {
    // System.out.println("parseMiseqData");
    // BufferedReader freader = null;
    // BatchMetadataInjection instance = new BatchMetadataInjection();
    // instance.parseMiseqData(freader);
    // }
    //
    // /**
    // * Test of parseMiseqHeader method, of class BatchMetadataInjection.
    // */
    // @Test
    // public void testParseMiseqHeader() throws Exception {
    // System.out.println("parseMiseqHeader");
    // BufferedReader freader = null;
    // BatchMetadataInjection instance = new BatchMetadataInjection();
    // Map expResult = null;
    // Map result = instance.parseMiseqHeader(freader);
    // assertEquals(expResult, result);
    // // TODO review the generated test code and remove the default call to fail.
    // fail("The test case is a prototype.");
    // }
    private void writeToObjects() throws IOException {

        net.sourceforge.seqware.common.model.Study study = metadata.getStudyByName(fileReport.get(0).get("Study Title"));

        net.sourceforge.seqware.common.model.SequencerRun sequencer = metadata.getSequencerRun(Integer.parseInt(fileReport.get(0).get(
                "Sequencer Run SWID")));
        net.sourceforge.seqware.common.model.Experiment experiment = metadata.getExperiment(Integer.parseInt(fileReport.get(0).get(
                "Experiment SWID")));
        // Sets study attributes
        runInfo = new RunInfo();
        runInfo.setStudyTitle(fileReport.get(0).get("Study Title").replaceAll("_", " "));

        runInfo.setStudyAttributes(getAttributes(fileReport.get(0).get("Study Attributes")));

        runInfo.setStudyCenterName(study.getCenterName());

        runInfo.setStudyCenterProject(study.getCenterProjectName());
        runInfo.setStudyDescription(study.getDescription());
        runInfo.setPairedEnd(true); // true for 99% of the cases
        runInfo.setRunSkip(Boolean.parseBoolean(fileReport.get(0).get("Skip")));

        // Can't get study type
        // Sets experiment info
        runInfo.setExperimentName(fileReport.get(0).get("Experiment Name").replaceAll("_", " "));
        runInfo.setExperimentAttributes(getAttributes(fileReport.get(0).get("Experiment Attributes")));
        runInfo.setExperimentDescription(experiment.getDescription());
        // Sets sequencer info
        runInfo.setRunName(fileReport.get(0).get("Sequencer Run Name"));
        runInfo.setRunAttributes(getAttributes(fileReport.get(0).get("Sequencer Run Attributes")));
        runInfo.setRunDescription(sequencer.getDescription());

        Set<LaneInfo> lanes = new HashSet<>();
        for (String laneSwid : laneSwids) {
            // Iterates through all the unique lanes
            Lane metadataLane = metadata.getLane(Integer.parseInt(laneSwid));
            LaneInfo lane = new LaneInfo();
            // Sets the required data
            lane.setLaneName(metadataLane.getName());
            lane.setLaneDescription(metadataLane.getDescription());
            lane.setLaneCycleDescriptor(metadataLane.getCycleDescriptor());
            lane.setLaneSkip(metadataLane.getSkip());
            lane.setLibrarySelectionAcc(metadataLane.getLibrarySelection().getLibrarySelectionId());
            lane.setLibrarySourceAcc(metadataLane.getLibrarySource().getLibrarySourceId());
            lane.setLibraryStrategyAcc(metadataLane.getLibraryStrategy().getLibraryStrategyId());
            Set<SampleInfo> samples = new HashSet<>();
            for (Map<String, String> fileReport1 : fileReport) {
                // Makes sure that the data is going to the right lane
                // Checks to make sure that the samples are from the correct lanes
                if (fileReport1.get("Lane SWID").equals(String.valueOf(laneSwid))) {
                    lane.setLaneNumber(fileReport1.get("Lane Number"));
                    lane.setLaneAttributes(getAttributes(fileReport1.get("Lane Attributes")));
                    SampleInfo sample = new SampleInfo();
                    sample.setBarcode(fileReport1.get("IUS Tag"));
                    sample.setName(fileReport1.get("Sample Name"));
                    sample.setSampleAttributes(getAttributes(fileReport1.get("Sample Attributes")));
                    sample.setIusAttributes(getAttributes(fileReport1.get("IUS Attributes")));
                    List<IUS> IUSes = metadata.getIUSFrom(Integer.parseInt(fileReport1.get("Sample SWID")));
                    sample.setIusDescription(IUSes.get(0).getDescription());
                    sample.setIusName(IUSes.get(0).getName());
                    sample.setIusSkip(IUSes.get(0).getSkip());
                    String[] sampleNameBrokenDown = sample.getName().split("_");
                    sample.setProjectCode(sampleNameBrokenDown[0]);
                    sample.setIndividualNumber(sampleNameBrokenDown[1]);
                    sample.setSampleDescription(metadata.getSampleByName(sample.getName()).get(0).getDescription());
                    // The Sample Attributes also contains other info which can be parsed
                    for (TagValueUnit unit : getAttributes(fileReport1.get("Sample Attributes"))) {
                        if (unit.getTag().matches(".*tissue_origin.*")) {
                            sample.setTissueOrigin(unit.getValue());

                        } else if (unit.getTag().matches(".*tissue_type.*")) {
                            sample.setTissueType(unit.getValue());

                        } else if (unit.getTag().matches(".*library_size_code.*")) {
                            sample.setLibrarySizeCode(unit.getValue());

                        } else if (unit.getTag().matches(".*library_source_template_type.*")) {
                            sample.setLibrarySourceTemplateType(unit.getValue());

                        } else if (unit.getTag().matches(".*library_type.*")) {
                            sample.setLibraryType(unit.getValue());

                        } else if (unit.getTag().matches(".*tissue_preparation.*")) {
                            sample.setTissuePreparation(unit.getValue());

                        } else if (unit.getTag().matches(".*targeted_resequencing.*")) {
                            sample.setTargetedResequencing(unit.getValue());

                        }
                    }
                    samples.add(sample);
                }
            }
            lane.setSamples(samples);
            lanes.add(lane);
        }
        runInfo.setLanes(lanes);
    }

    private Set<TagValueUnit> getAttributes(String data) {
        // Splits the strings into multiple key value pairs
        if ("".equals(data)) {
            return null;
        }
        Set<TagValueUnit> attributes = new HashSet<>();
        String[] differentAttributes = data.split(";");

        for (String attribute : differentAttributes) {
            String[] tagValuePair = attribute.split("=");
            TagValueUnit unit = new TagValueUnit();
            unit.setTag(tagValuePair[0].replaceAll(".*\\.", ""));
            unit.setValue(tagValuePair[1]);
            attributes.add(unit);
        }
        return attributes;

    }

    private void getData() {

        Map<FileProvenanceParam, List<String>> fileProvenanceParams = new EnumMap<>(
                FileProvenanceParam.class);
        fileProvenanceParams.put(FileProvenanceParam.ius, iusSwids);

        metadata.fileProvenanceReportTrigger();
        fileReport = metadata.fileProvenanceReport(fileProvenanceParams);
    }

    private void getStdOut(String output) {
        // Captures the required data from standard output that was given
        String[] outputLines = output.split("\n");

        for (String s : outputLines) {
            // Gets the IUS swids
            if (s.matches("Created IUS with SWID: [0-9]*")) {

                iusSwids.add(parseSWID(s));
            } else if (s.matches("Created lane with SWID: [0-9]*")) {
                // gets the lane swids
                laneSwids.add(parseSWID(s));
            }

        }
    }

    private String createFileLinkerFile(List<String> swids) throws IOException {
        String fileName = "fileLinkerFile";
        String header = "sequencer_run,sample,lane,ius_sw_accession,file_status,mime_type,file\n";
        File fileLinkerFile = new File(fileName);

        // Starts writing to file
        FileUtils.writeStringToFile(fileLinkerFile, header);
        fileLinkerFile.deleteOnExit();

        // Creates the random files
        String randomName = RandomStringUtils.random(5, true, false);
        int count = 0;
        for (String s : swids) {
            count++;

            File file = new File(FileUtils.getTempDirectoryPath() + "/" + randomName + count + ".txt");
            file.createNewFile();
            FileUtils.write(file, "Hello World!");
            String line = ".,.,.," + String.valueOf(s) + ",.,.," + FileUtils.getTempDirectoryPath() + "/" + randomName + count + ".txt\n";

            FileUtils.writeStringToFile(fileLinkerFile, line, true);
            file.deleteOnExit();
        }

        return fileLinkerFile.getCanonicalPath();

    }

    private void runFileLinkerPlugin(String fileLinkerPath, String wfaccession) throws IOException {

        String[] fileLinkerParams = { "--file-list-file", fileLinkerPath, "--workflow-accession", wfaccession, "--csv-separator", "," };
        PluginRunner p = new PluginRunner();
        List<String> a = new ArrayList<>();
        a.add("--plugin");
        a.add(FileLinker.class.getCanonicalName());
        a.add("--");
        a.addAll(Arrays.asList(fileLinkerParams));
        System.out.println(Arrays.deepToString(a.toArray()));

        p.run(a.toArray(new String[a.size()]));

    }

    private String parseSWID(String output) {
        String swid;

        swid = output.substring(output.indexOf(':') + 2);

        return swid;

    }

    /**
     * Compares all values in the json to make sure that they are equal
     * 
     * @param original
     * @param actual
     * @return If the jsons match or not
     */
    private boolean equals(RunInfo original, RunInfo actual, String jsonName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        original = mapper.readValue(new File(jsonName), RunInfo.class);

        EqualsBuilder equal = new EqualsBuilder();

        // Study stuff
        equal.append(original.getStudyTitle(), actual.getStudyTitle());
        equal.append(original.getStudyDescription(), actual.getStudyDescription());
        equal.append(original.getStudyCenterName(), actual.getStudyCenterName());
        equal.append(original.getStudyCenterProject(), actual.getStudyCenterProject());

        // Sequencer stuff
        equal.append(original.getRunName(), actual.getRunName());
        equal.append(original.getRunSkip(), actual.getRunSkip());
        equal.append(original.getRunDescription(), actual.getRunDescription());

        List<TagValueUnit> originalRunAttributes = new ArrayList<>(original.getRunAttributes());
        List<TagValueUnit> actualRunAttributes = new ArrayList<>(actual.getRunAttributes());

        Collections.sort(originalRunAttributes);
        Collections.sort(actualRunAttributes);

        Iterator<TagValueUnit> iterRunAttrOrig = originalRunAttributes.iterator();
        Iterator<TagValueUnit> iterRunAttrAct = actualRunAttributes.iterator();
        while (iterRunAttrAct.hasNext() && iterRunAttrOrig.hasNext()) {
            TagValueUnit unitOrig = iterRunAttrOrig.next();
            TagValueUnit unitAct = iterRunAttrAct.next();
            equal.append(unitAct.getTag(), unitOrig.getTag());
            equal.append(unitAct.getValue(), unitOrig.getValue());
        }

        // Experiment stuff
        equal.append(original.getExperimentDescription(), actual.getExperimentDescription());
        equal.append(original.getExperimentName(), actual.getExperimentName());

        List<TagValueUnit> originalExperimentAttributes = new ArrayList<>(original.getExperimentAttributes());
        List<TagValueUnit> actualExperimentAttributes = new ArrayList<>(actual.getExperimentAttributes());

        Collections.sort(originalExperimentAttributes);
        Collections.sort(actualExperimentAttributes);

        Iterator<TagValueUnit> iterExperimentAttrOrig = originalExperimentAttributes.iterator();
        Iterator<TagValueUnit> iterExperimentAttrAct = actualExperimentAttributes.iterator();
        while (iterExperimentAttrAct.hasNext() && iterExperimentAttrOrig.hasNext()) {
            TagValueUnit unitOrig = iterExperimentAttrAct.next();
            TagValueUnit unitAct = iterExperimentAttrOrig.next();
            equal.append(unitAct.getTag(), unitOrig.getTag());
            equal.append(unitAct.getValue(), unitOrig.getValue());
        }

        // Lanes
        List<LaneInfo> originalLanes = new ArrayList<>(original.getLanes());
        List<LaneInfo> actualLanes = new ArrayList<>(actual.getLanes());

        Collections.sort(originalLanes);
        Collections.sort(actualLanes);

        Iterator<LaneInfo> iterActualLane = actualLanes.iterator();
        Iterator<LaneInfo> iterOriginalLane = originalLanes.iterator();

        // Adds all the lane info
        while (iterActualLane.hasNext() && iterOriginalLane.hasNext()) {
            LaneInfo originalLane = iterOriginalLane.next();
            LaneInfo actualLane = iterActualLane.next();
            equal.append(originalLane.getLaneName(), actualLane.getLaneName());
            equal.append(originalLane.getLaneNumber(), actualLane.getLaneNumber());
            equal.append(originalLane.getLaneDescription(), actualLane.getLaneDescription());
            equal.append(originalLane.getLaneCycleDescriptor(), actualLane.getLaneCycleDescriptor());
            equal.append(originalLane.getLaneSkip(), actualLane.getLaneSkip());
            equal.append(originalLane.getLibrarySelectionAcc(), actualLane.getLibrarySelectionAcc());
            equal.append(originalLane.getLibrarySourceAcc(), actualLane.getLibrarySourceAcc());
            equal.append(originalLane.getLibraryStrategyAcc(), actualLane.getLibraryStrategyAcc());

            // Adds all the lane attributes
            List<TagValueUnit> originalLaneAttributes = new ArrayList<>(originalLane.getLaneAttributes());
            List<TagValueUnit> actualLaneAttributes = new ArrayList<>(actualLane.getLaneAttributes());
            Collections.sort(originalLaneAttributes);
            Collections.sort(actualLaneAttributes);

            Iterator<TagValueUnit> iterOrigLaneAttr = originalLaneAttributes.iterator();
            Iterator<TagValueUnit> iterActualLaneAttr = actualLaneAttributes.iterator();

            while (iterOrigLaneAttr.hasNext() && iterActualLaneAttr.hasNext()) {
                TagValueUnit unitOrig = iterOrigLaneAttr.next();
                TagValueUnit unitActual = iterActualLaneAttr.next();

                equal.append(unitOrig.getTag(), unitActual.getTag());
                equal.append(unitOrig.getValue(), unitActual.getValue());
            }

            // Now to check samples
            List<SampleInfo> originalSamples = new ArrayList<>(originalLane.getSamples());
            List<SampleInfo> actualSamples = new ArrayList<>(actualLane.getSamples());

            Collections.sort(actualSamples);
            Collections.sort(originalSamples);

            Iterator<SampleInfo> iterSampleOrig = originalSamples.iterator();
            Iterator<SampleInfo> iterSampleActual = actualSamples.iterator();

            while (iterSampleActual.hasNext() && iterSampleOrig.hasNext()) {
                SampleInfo originalSample = iterSampleOrig.next();
                SampleInfo actualSample = iterSampleActual.next();

                equal.append(originalSample.getProjectCode(), actualSample.getProjectCode());
                equal.append(originalSample.getIndividualNumber(), actualSample.getIndividualNumber());
                equal.append(originalSample.getName(), actualSample.getName());
                equal.append(originalSample.getTissueType(), actualSample.getTissueType());
                equal.append(originalSample.getTissueOrigin(), actualSample.getTissueOrigin());
                equal.append(originalSample.getLibrarySizeCode(), actualSample.getLibrarySizeCode());
                equal.append(originalSample.getLibrarySourceTemplateType(), actualSample.getLibrarySourceTemplateType());
                equal.append(originalSample.getLibraryType(), actualSample.getLibraryType());
                equal.append(originalSample.getParentSample(), actualSample.getParentSample());
                equal.append(originalSample.getTissuePreparation(), actualSample.getTissuePreparation());
                equal.append(originalSample.getTargetedResequencing(), actualSample.getTargetedResequencing());
                equal.append(originalSample.getSampleDescription(), actualSample.getSampleDescription());
                equal.append(originalSample.getBarcode(), actualSample.getBarcode());
                equal.append(originalSample.getIusName(), actualSample.getIusName());
                equal.append(originalSample.getIusDescription(), actualSample.getIusDescription());
                equal.append(originalSample.getIusSkip(), actualSample.getIusSkip());

                List<TagValueUnit> originalSampleAttributes = new ArrayList<>(originalSample.getSampleAttributes());
                List<TagValueUnit> actualSampleAttributes = new ArrayList<>(actualSample.getSampleAttributes());

                Collections.sort(originalSampleAttributes);
                Collections.sort(actualSampleAttributes);

                Iterator<TagValueUnit> iterOrigSampleAttr = originalSampleAttributes.iterator();
                Iterator<TagValueUnit> iterActualSampleAttr = actualSampleAttributes.iterator();

                while (iterOrigSampleAttr.hasNext() && iterActualSampleAttr.hasNext()) {
                    TagValueUnit unitOrig = iterOrigSampleAttr.next();
                    TagValueUnit unitActual = iterActualSampleAttr.next();
                    equal.append(unitOrig.getTag(), unitActual.getTag());
                    equal.append(unitOrig.getValue(), unitActual.getValue());
                }

                List<TagValueUnit> originalIUSAttributes = new ArrayList<>(originalSample.getIusAttributes());
                List<TagValueUnit> actualIUSAttributes = new ArrayList<>(originalSample.getIusAttributes());

                Collections.sort(originalIUSAttributes);
                Collections.sort(actualIUSAttributes);

                Iterator<TagValueUnit> iterOrigIUSAttr = originalIUSAttributes.iterator();
                Iterator<TagValueUnit> iterActualIUSIterator = actualIUSAttributes.iterator();

                while (iterOrigIUSAttr.hasNext() && iterActualIUSIterator.hasNext()) {
                    TagValueUnit unitOrig = iterOrigSampleAttr.next();
                    TagValueUnit unitActual = iterActualSampleAttr.next();
                    equal.append(unitOrig.getTag(), unitActual.getTag());
                    equal.append(unitOrig.getValue(), unitActual.getValue());
                }
            }
        }

        return equal.isEquals();

    }

}
