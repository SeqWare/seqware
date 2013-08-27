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
package net.sourceforge.seqware.pipeline.plugins;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.metadata.MetadataWS;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.SequencerRunStatus;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.runtools.ConsoleAdapter;
import net.sourceforge.seqware.common.util.runtools.TestConsoleAdapter;
import net.sourceforge.seqware.common.util.testtools.BasicTestDatabaseCreator;
import static net.sourceforge.seqware.pipeline.plugins.PluginTest.metadata;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * Runs the tests for the Metadata plugin indicated on this
 * page:https://wiki.oicr.on.ca/x/Jga5Ag
 *
 * @author mtaschuk
 */
public class MetadataTest extends PluginTest {

    private ByteArrayOutputStream outStream = null;
    private ByteArrayOutputStream errStream = null;
    private Pattern swidPattern = Pattern.compile("SWID: ([\\d]+)");
    private Pattern errorPattern = Pattern.compile("ERROR|error|Error|FATAL|fatal|Fatal|WARN|warn|Warn");
    private PrintStream systemErr = System.err;
    private PrintStream systemOut = System.out;

    @Before
    @Override
    public void setUp() {
        super.setUp();        
        instance = new Metadata();
        instance.setMetadata(metadata);

        outStream = new ByteArrayOutputStream();
        errStream = new ByteArrayOutputStream();
        PrintStream pso = new PrintStream(outStream);
        PrintStream pse = new PrintStream(errStream) {

            @Override
            public PrintStream append(CharSequence csq) {
//                systemErr.append(csq);
                return super.append(csq);
            }

            @Override
            public void print(String s) {
//                systemErr.print(s);
                super.print(s);
            }
        };
        System.setOut(pso);
        System.setErr(pse);
    }

    @Override
    public void tearDown() {
        super.tearDown();
    }

    public MetadataTest() {
    }

    public String getOut() {
        return parsePrintStream(outStream);
    }

    public String getErr() {
        return parsePrintStream(errStream);
    }

    public String parsePrintStream(ByteArrayOutputStream stream) {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = null;
        try {
            ByteArrayInputStream inStream = new ByteArrayInputStream(stream.toByteArray());
            r = new BufferedReader(new InputStreamReader(inStream));

            String s = r.readLine();
            while (s != null) {
                s = s.trim();
                //remove any blank lines
                if (s.isEmpty()) {
                    s = r.readLine();
                    continue;
                }
                if (s.endsWith("[")) {
                    while (s != null && !s.contains("]")) {
                        sb.append(s);
                        s = r.readLine();
                    }
                }
                sb.append(s).append("\n");
                s = r.readLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(MetadataTest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (r != null) {
                try {
                    r.close();
                } catch (IOException ex) {
                    systemErr.println("Couldn't close System.out reader" + ex.getMessage());
                }
            }
        }

        return sb.toString();
    }

    private void checkErrors(String s) {
        Matcher matcher = errorPattern.matcher(s);
        systemErr.println("~~~~~~~~~~" + s);
        Assert.assertFalse("Output contains errors:" + s, matcher.find());
//        systemErr.println("~~~~~~~~~~"+matcher.group());

    }

    private void checkFields(Map<String, String> expectedFields) {
        String out = getOut();
        for (String s : out.split("\n")) {
            String[] tokens = s.split("\t");
            Assert.assertTrue("Unknown field exists: " + s, expectedFields.containsKey(tokens[0]));
            Assert.assertEquals("Field has different parameter type than expected", expectedFields.get(tokens[0]), tokens[1]);
        }
    }

    @Test
    public void testListAllTables() {
        systemErr.println("Test List all Tables\n");
        launchPlugin("--list-tables");
        String output = getOut();
        // fix up test to support basic workflow/run creation tools, see git commit 4862eaba7f3d7c7495155dc913ead745b544f358
        String[] tables = new String[]{"TableName", "study", "experiment", "sample", "ius", "lane", "sequencer_run", "workflow", "workflow_run"};
        LinkedList<String> stuff = new LinkedList(Arrays.asList(output.split("\n")));
        for (String table : tables) {
            int index = stuff.indexOf(table);
            if (index >= 0) {
                stuff.remove(index);
            } else {
                Assert.fail("Missing a table:" + table);
            }
        }
        while (!stuff.isEmpty()) {
            String s = stuff.poll();
            Assert.fail("There are extra tables listed: " + s);
        }
    }

    @Test
    public void testListStudyFields() {
        systemErr.println("Test List study fields");

        Map<String, String> expectedFields = new HashMap<String, String>();
        expectedFields.put("Field", "Type");
        expectedFields.put("title", "String");
        expectedFields.put("description", "String");
        expectedFields.put("center_name", "String");
        expectedFields.put("center_project_name", "String");
        expectedFields.put("study_type", "Integer");

        launchPlugin("--table", "study", "--list-fields");
        checkFields(expectedFields);
    }

    @Test
    public void testListExperimentFields() {
        systemErr.println("Test List experiment fields");

        Map<String, String> expectedFields = new HashMap<String, String>();
        expectedFields.put("Field", "Type");
        expectedFields.put("title", "String");
        expectedFields.put("description", "String");
        expectedFields.put("study_accession", "Integer");
        expectedFields.put("platform_id", "Integer");
        expectedFields.put("experiment_library_design_id", "Integer");
        expectedFields.put("experiment_spot_design_id", "Integer");


        launchPlugin("--table", "experiment", "--list-fields");
        checkFields(expectedFields);
    }

    @Test
    public void testListSampleFields() {
        systemErr.println("Test List sample fields");
        Map<String, String> expectedFields = new HashMap<String, String>();
        expectedFields.put("Field", "Type");
        expectedFields.put("title", "String");
        expectedFields.put("description", "String");
        expectedFields.put("experiment_accession", "Integer");
        expectedFields.put("parent_sample_accession", "Integer");
        expectedFields.put("organism_id", "Integer");

        launchPlugin("--table", "sample", "--list-fields");

        checkFields(expectedFields);
    }

    @Test
    public void testListSequencerRunFields() {
        systemErr.println("Test List sequencer run fields");

        Map<String, String> expectedFields = new HashMap<String, String>();
        expectedFields.put("Field", "Type");
        expectedFields.put("name", "String");
        expectedFields.put("description", "String");
        expectedFields.put("paired_end", "Boolean");
        expectedFields.put("skip", "Boolean");
        expectedFields.put("platform_accession", "Integer");
        expectedFields.put("file_path", "String");
        expectedFields.put("status", "String");



        launchPlugin("--table", "sequencer_run", "--list-fields");

        checkFields(expectedFields);
    }

    @Test
    public void testListLaneFields() {
        systemErr.println("Test List Lane fields");

        Map<String, String> expectedFields = new HashMap<String, String>();
        expectedFields.put("Field", "Type");
        expectedFields.put("name", "String");
        expectedFields.put("description", "String");
        expectedFields.put("cycle_descriptor", "String");
        expectedFields.put("skip", "Boolean");
        expectedFields.put("sequencer_run_accession", "Integer");
        expectedFields.put("study_type_accession", "Integer");
        expectedFields.put("library_strategy_accession", "Integer");
        expectedFields.put("library_selection_accession", "Integer");
        expectedFields.put("library_source_accession", "Integer");
        expectedFields.put("lane_number", "Integer");
        launchPlugin("--table", "lane", "--list-fields");

        checkFields(expectedFields);
    }

    @Test
    public void testListIUSFields() {
        systemErr.println("Test List IUS fields");

        Map<String, String> expectedFields = new HashMap<String, String>();
        expectedFields.put("Field", "Type");
        expectedFields.put("name", "String");
        expectedFields.put("description", "String");
        expectedFields.put("barcode", "String");
        expectedFields.put("skip", "Boolean");
        expectedFields.put("sample_accession", "Integer");
        expectedFields.put("lane_accession", "Integer");

        launchPlugin("--table", "ius", "--list-fields");

        checkFields(expectedFields);
    }

    @Test
    public void testMatcher() {
        String string = "[SeqWare Pipeline] ERROR [2012/11/01 15:53:51] | "
                + "MetadataWS.findObject with search string /288023 encountered error "
                + "Internal Server Error\nExperiment: null\nSWID: 6740";
        Matcher match = swidPattern.matcher(string);
        Assert.assertTrue(match.find());
        Assert.assertEquals("6740", match.group(1));
        match = errorPattern.matcher(string);
        Assert.assertTrue(match.find());
        Assert.assertEquals("ERROR", match.group(0));

    }
    private String studyAccession = null;

    @Test
    public void testCreateStudy() {
        launchPlugin("--table", "study", "--create",
                "--field", "title::alal" + System.currentTimeMillis(),
                "--field", "description::alal",
                "--field", "accession::1235",
                "--field", "center_name::oicr",
                "--field", "center_project_name::mine",
                "--field", "study_type::1");
        String s = getOut();
        studyAccession = getAndCheckSwid(s);
    }
    private String experimentAccession = null;

    @Test
    public void testCreateExperiment() {
        String sAcc = studyAccession;
        if (sAcc == null) {
            sAcc = "120";
        }

        launchPlugin("--table", "experiment", "--create",
                "--field", "study_accession::" + sAcc,
                "--field", "title::experimenttitle" + System.currentTimeMillis(),
                "--field", "description::\"Experiment Description\"",
                "--field", "platform_id::9");
        String s = getOut();
        experimentAccession = getAndCheckSwid(s);
    }
    private String sampleAccession = null;
    
    @Test
    public void testCreateExperimentWithLibraryDesignAndSpotDesign() {
        String sAcc = studyAccession;
        if (sAcc == null) {
            sAcc = "120";
        }

        launchPlugin("--table", "experiment", "--create",
                "--field", "study_accession::" + sAcc,
                "--field", "title::experimenttitle" + System.currentTimeMillis(),
                "--field", "description::\"Experiment Description\"",
                "--field", "platform_id::9",
                "--field", "experiment_spot_design_id::7",
                "--field", "experiment_library_design_id::8"
                );
        String s = getOut();
        String localExperimentAccession = getAndCheckSwid(s);
        // SEQWARE-1713 check that the two optional fields make it into the database
        BasicTestDatabaseCreator dbCreator = new BasicTestDatabaseCreator();
        Object[] runQuery = dbCreator.runQuery(new ArrayHandler(), "select experiment_library_design_id,experiment_spot_design_id from experiment WHERE sw_accession=?", Integer.valueOf(localExperimentAccession));
        Assert.assertTrue("optional values were incorrect", runQuery[0].equals(8) && runQuery[1].equals(7));
        // check that we can get them back via metadata methods as well
        Experiment e = metadata.getExperiment(Integer.valueOf(localExperimentAccession));
        Assert.assertTrue("could not retrieve optional fields via metadata", e.getExperimentLibraryDesign() != null && e.getExperimentSpotDesign() != null);
        Assert.assertTrue("optional fields via metadata were incorrect, found " + e.getExperimentLibraryDesign().getExperimentLibraryDesignId() + ":" + e.getExperimentSpotDesign().getExperimentSpotDesignId(), 
                e.getExperimentLibraryDesign().getExperimentLibraryDesignId() == 8 && e.getExperimentSpotDesign().getExperimentSpotDesignId() == 7);
    }

    @Test
    public void testCreateSampleWithExperiment() {
        String eAcc = experimentAccession;
        if (eAcc == null) {
            eAcc = "834";
        }

        launchPlugin("--table", "sample", "--create",
                "--field", "experiment_accession::" + eAcc,
                "--field", "title::sampletitle",
                "--field", "description::sampledescription",
                "--field", "organism_id::31");
        String s = getOut();
        sampleAccession = getAndCheckSwid(s);

        boolean foundIt = false;
        List<Sample> samples = metadata.getSamplesFrom(Integer.parseInt(eAcc));
        Assert.assertNotNull("There should be a sample!", samples);
        for (Sample sam : samples) {
            if (sam.getSwAccession().equals(Integer.parseInt(sampleAccession))) {
                foundIt = true;
            }
        }
        Assert.assertTrue("Did not find the sample attached to the experiment", foundIt);
        
        // SEQWARE-1716 : omitting the parent sample should result in a "production"-like root sample with a null parent in the sample hierarchy
        BasicTestDatabaseCreator dbCreator = new BasicTestDatabaseCreator();
        Object[] runQuery = dbCreator.runQuery(new ArrayHandler(), "select h.sample_id, h.parent_id, count(*) from sample s, sample_hierarchy h "
                + "WHERE s.sample_id = h.sample_id AND s.sw_accession=? GROUP BY h.sample_id, h.parent_id", Integer.valueOf(sampleAccession));
        Assert.assertTrue("parent values were incorrect", runQuery[0] != null && runQuery[1] == null);
        Assert.assertTrue("duplicate parents in sample hierarchy found, " + runQuery[2], runQuery[2].equals(1L));
    }
    
    @Test
    public void testCreateSampleWithExperimentWrongOrganismFail() {
        String eAcc = experimentAccession;
        if (eAcc == null) {
            eAcc = "834";
        }

        instance.setParams(Arrays.asList("--table", "sample", "--create",
                "--field", "experiment_accession::" + eAcc,
                "--field", "title::sampletitle",
                "--field", "description::sampledescription",
                "--field", "organism_id::100000"));

        checkExpectedIncorrectParameters();
    }

    @Test
    public void testCreateSampleWithParent() {
        String sAcc = "6193";

        launchPlugin("--table", "sample", "--create",
                "--field", "parent_sample_accession::" + sAcc,
                "--field", "title::sampletitle",
                "--field", "description::sampledescription",
                "--field", "organism_id::31");
        String s = getOut();
        sampleAccession = getAndCheckSwid(s);

        boolean foundIt = false;
        List<Sample> samples = metadata.getChildSamplesFrom(Integer.parseInt(sAcc));
        Assert.assertNotNull("There should be a sample!", samples);
        for (Sample sam : samples) {
            if (sam.getSwAccession().equals(Integer.parseInt(sampleAccession))) {
                foundIt = true;
            }
        }
        Assert.assertTrue("Did not find the sample attached to the parent sample", foundIt);

    }
    private String runAccession = null;

    @Test
    public void testCreateSequencerRun() {
        launchPlugin("--table", "sequencer_run", "--create",
                "--field", "name::SR" + System.currentTimeMillis(),
                "--field", "description::SRD",
                "--field", "platform_accession::20",
                "--field", "paired_end::true",
                "--field", "skip::false",
                "--field", "file_path::/home/user/mysequencerrun");
        String s = getOut();
        runAccession = getAndCheckSwid(s);
    }
    
       private String laneAccession = null;

    @Test
    public void testCreateSequencerRunWithStatus() {
        SequencerRunStatus funky_status = SequencerRunStatus.ready_to_process;
        launchPlugin("--table", "sequencer_run", "--create",
                "--field", "name::SR" + System.currentTimeMillis(),
                "--field", "description::SRD",
                "--field", "platform_accession::20",
                "--field", "paired_end::true",
                "--field", "skip::false",
                "--field", "file_path::/home/user/mysequencerrun",
                "--field", "status::"+funky_status.name());
        String s = getOut();
        String accession = getAndCheckSwid(s);
        
        // SEQWARE-1561 check that library strategy, selection, and source make it into the database
        BasicTestDatabaseCreator dbCreator = new BasicTestDatabaseCreator();
        Object[] runQuery = dbCreator.runQuery(new ArrayHandler(), "select status from sequencer_run WHERE sw_accession=?", Integer.valueOf(accession));
        Assert.assertTrue("status was incorrect", runQuery[0].equals(funky_status.name()));
        // check that we can get them back via metadata methods as well
        SequencerRun r = metadata.getSequencerRun(Integer.valueOf(accession));
        Assert.assertTrue("could not retrieve lane via metadata", r != null);
        Assert.assertTrue("could not retrieve lane status", r.getStatus().equals(funky_status));
    }


    @Test
    public void testCreateLane() {
        String rAcc = runAccession;
        if (rAcc == null) {
            rAcc = "4715";
        }

        launchPlugin("--table", "lane", "--create",
                "--field", "name::lane",
                "--field", "description::description",
                "--field", "cycle_descriptor::{F*120}{..}{R*120}",
                "--field", "sequencer_run_accession::" + rAcc,
                "--field", "library_strategy_accession::2",
                "--field", "study_type_accession::1",
                "--field", "library_selection_accession::3",
                "--field", "library_source_accession::4",
                "--field", "skip::false",
                "--field", "lane_number::1");
        String s = getOut();
        laneAccession = getAndCheckSwid(s);
        
        // SEQWARE-1561 check that library strategy, selection, and source make it into the database
        BasicTestDatabaseCreator dbCreator = new BasicTestDatabaseCreator();
        Object[] runQuery = dbCreator.runQuery(new ArrayHandler(), "select library_strategy, library_selection, library_source from lane WHERE sw_accession=?", Integer.valueOf(laneAccession));
        Assert.assertTrue("library columns were incorrect", runQuery[0].equals(2) && runQuery[1].equals(3) && runQuery[2].equals(4));
        // check that we can get them back via metadata methods as well
        Lane l = metadata.getLane(Integer.valueOf(laneAccession));
        Assert.assertTrue("could not retrieve lane via metadata", l != null);
        Assert.assertTrue("could not retrieve lane library values", l.getLibraryStrategy().getLibraryStrategyId() == 2);
        Assert.assertTrue("could not retrieve lane library values", l.getLibrarySelection().getLibrarySelectionId() == 3);
        Assert.assertTrue("could not retrieve lane library values", l.getLibrarySource().getLibrarySourceId() == 4);
    }
    
    @Test
    public void testCreateLaneWrongStudyTypeFail() {
        String rAcc = runAccession;
        if (rAcc == null) {
            rAcc = "4715";
        }

        instance.setParams(Arrays.asList("--table", "lane", "--create",
                "--field", "name::lane",
                "--field", "description::description",
                "--field", "cycle_descriptor::{F*120}{..}{R*120}",
                "--field", "sequencer_run_accession::" + rAcc,
                "--field", "library_strategy_accession::1",
                "--field", "study_type_accession::1000000",
                "--field", "library_selection_accession::1",
                "--field", "library_source_accession::1",
                "--field", "skip::false",
                "--field", "lane_number::1"));
        checkExpectedIncorrectParameters();
    }
    
    @Test
    public void testCreateLaneWrongLibraryStrategyFail() {
        String rAcc = runAccession;
        if (rAcc == null) {
            rAcc = "4715";
        }

        instance.setParams(Arrays.asList("--table", "lane", "--create",
                "--field", "name::lane",
                "--field", "description::description",
                "--field", "cycle_descriptor::{F*120}{..}{R*120}",
                "--field", "sequencer_run_accession::" + rAcc,
                "--field", "library_strategy_accession::10000",
                "--field", "study_type_accession::1",
                "--field", "library_selection_accession::1",
                "--field", "library_source_accession::1",
                "--field", "skip::false",
                "--field", "lane_number::1"));
        checkExpectedIncorrectParameters();
    }
    
    @Test
    public void testCreateLaneWrongLibrarySelectionFail() {
        String rAcc = runAccession;
        if (rAcc == null) {
            rAcc = "4715";
        }

        instance.setParams(Arrays.asList("--table", "lane", "--create",
                "--field", "name::lane",
                "--field", "description::description",
                "--field", "cycle_descriptor::{F*120}{..}{R*120}",
                "--field", "sequencer_run_accession::" + rAcc,
                "--field", "library_strategy_accession::1",
                "--field", "study_type_accession::1",
                "--field", "library_selection_accession::100000",
                "--field", "library_source_accession::1",
                "--field", "skip::false",
                "--field", "lane_number::1"));
        checkExpectedIncorrectParameters();
    }
    
    @Test
    public void testCreateLaneWrongLibrarySourceFail() {
        String rAcc = runAccession;
        if (rAcc == null) {
            rAcc = "4715";
        }

        instance.setParams(Arrays.asList("--table", "lane", "--create",
                "--field", "name::lane",
                "--field", "description::description",
                "--field", "cycle_descriptor::{F*120}{..}{R*120}",
                "--field", "sequencer_run_accession::" + rAcc,
                "--field", "library_strategy_accession::1",
                "--field", "study_type_accession::1",
                "--field", "library_selection_accession::1",
                "--field", "library_source_accession::100000",
                "--field", "skip::false",
                "--field", "lane_number::1"));
        checkExpectedIncorrectParameters();
    }

    @Test
    public void testCreateIUS() {
        String lAcc = laneAccession;
        if (lAcc == null) {
            lAcc = "4707";
        }
        String sAcc = sampleAccession;
        if (sAcc == null) {
            sAcc = "4760";
        }

        launchPlugin("--table", "ius", "--create",
                "--field", "name::ius",
                "--field", "description::des",
                "--field", "lane_accession::" + lAcc,
                "--field", "sample_accession::" + sAcc,
                "--field", "skip::false",
                "--field", "barcode::NoIndex");
        String s = getOut();
        getAndCheckSwid(s);

    }
    
    @Test
    public void testCreateWorkflowRun() {
        launchPlugin("--table", "workflow_run", "--create",
                "--field", "workflow_accession::4",
                "--field", "status::completed");
        String s = getOut();
        String swid = getAndCheckSwid(s);
        int integer = Integer.valueOf(swid);
        WorkflowRun workflowRun = metadata.getWorkflowRun(integer);
        Assert.assertTrue("could not find workflowRun", workflowRun != null && workflowRun.getSwAccession() == integer);
        
    }
    
    @Test
    public void testCreateWorkflowRunWrongWorkflowFail() {
        instance.setParams(Arrays.asList("--table", "workflow_run", "--create",
                "--field", "workflow_accession::100000",
                "--field", "status::completed"));
        String s = getOut();
        checkExpectedIncorrectParameters();
    }
    
    @Test
    public void testCreateWorkflowRunWithFiles() {
        launchPlugin("--table", "workflow_run", "--create",
                "--field", "workflow_accession::4",
                "--field", "status::completed",
                "--file","cool_algorithm1::adamantium/gzip::/datastore/adamantium.gz",
                "--file","hot_algorithm1::corbomite/gzip::/datastore/corbomite.gz");
        String s = getOut();
        String swid = getAndCheckSwid(s);
        int integer = Integer.valueOf(swid);
        // check that file records were created correctly and linked in properly, 0.13.13.6.x does not have access to TestDatabaseCreator, so 
        // let's try some workflow run reporter parsing
        WorkflowRun workflowRun = metadata.getWorkflowRun(integer);
        String workflowRunReport = ((MetadataWS)metadata).getWorkflowRunReport(integer);
        Assert.assertTrue("could not find workflowRun", workflowRun != null && workflowRun.getSwAccession() == integer);
        Assert.assertTrue("could not find files", workflowRunReport.contains("/datastore/adamantium.gz") && workflowRunReport.contains("/datastore/corbomite.gz"));
    }
    
    @Test
    public void testCreateWorkflowRunWithParentAccessions() {
        launchPlugin("--table", "workflow_run", "--create",
                "--field", "workflow_accession::4",
                "--field", "status::completed",
                "--parent-accession","834", // experiment
                "--parent-accession", "4765", // ius 
                "--parent-accession", "4707", // lane
                "--parent-accession", "4760", // sample
                "--parent-accession", "4715", // sequencer_run
                "--parent-accession", "120", //study
                "--parent-accession", "10" //processing
        );
        String s = getOut();
        String swid = getAndCheckSwid(s);
        int integer = Integer.valueOf(swid);
        // check that file records were created correctly and linked in properly, 0.13.13.6.x does not have access to TestDatabaseCreator, so 
        // let's try some workflow run reporter parsing
        WorkflowRun workflowRun = metadata.getWorkflowRun(integer);
        String workflowRunReport = metadata.getWorkflowRunReport(integer);
        Assert.assertTrue("could not find workflowRun", workflowRun != null && workflowRun.getSwAccession() == integer);
    }
    
    @Test
    public void testCreateFile() {
        final String algorithm = "kryptonite_algorithm1";
        final String type = "kryptonite_type1";
        final String meta_type = "kryptonite/gzip";
        final String file_path = "/datastore/kryptonite.gz";
        final String description = "glowing_metal";
        launchPlugin("--table", "file", "--create",
                "--file",type+"::"+meta_type+"::"+file_path + "::" + description,
                "--field", "algorithm::" + algorithm
                );
        String s = getOut();
        String swid = getAndCheckSwid(s);
        int integer = Integer.valueOf(swid); 
        BasicTestDatabaseCreator dbCreator = new BasicTestDatabaseCreator();
        Object[] runQuery = dbCreator.runQuery(new ArrayHandler(), "select f.sw_accession, p.algorithm from file f, processing p, processing_files pf WHERE f.file_id=pf.file_id AND pf.processing_id = p.processing_id AND p.sw_accession =?", Integer.valueOf(integer));
        int file_sw_accession = (Integer)runQuery[0];
        String dbAlgorithm = (String)runQuery[1];
        
        net.sourceforge.seqware.common.model.File file = metadata.getFile(file_sw_accession);
        Assert.assertTrue("could not find file", file != null && file.getSwAccession() == file_sw_accession);
        Assert.assertTrue("file values incorrect", file.getFilePath().equals(file_path) 
                && file.getMetaType().equals(meta_type) && file.getDescription().equals(description)
                && file.getType().equals(type));
        Assert.assertTrue("algorithm incorrect", dbAlgorithm.equals(algorithm));
    }
    
    @Test
    public void testCreateFileWithParentAccessions() {
        final String algorithm = "kryptonite_algorithm1";
        final String type = "cool_type1";
        final String meta_type = "adamantium/gzip";
        final String file_path = "/datastore/adamantium.gz";
        launchPlugin("--table", "file", "--create",
                "--file",type+"::"+meta_type+"::"+file_path,
                "--parent-accession","834", // experiment
                "--parent-accession", "4765", // ius 
                "--parent-accession", "4707", // lane
                "--parent-accession", "4760", // sample
                "--parent-accession", "4715", // sequencer_run
                "--parent-accession", "120", //study
                "--parent-accession", "10", //processing
                "--field", "algorithm::" + algorithm
        );
        String s = getOut();
        String swid = getAndCheckSwid(s);
        int integer = Integer.valueOf(swid);
        
        BasicTestDatabaseCreator dbCreator = new BasicTestDatabaseCreator();
        Object[] runQuery = dbCreator.runQuery(new ArrayHandler(), "select f.sw_accession, p.processing_id from file f, processing p, processing_files pf WHERE f.file_id=pf.file_id AND pf.processing_id = p.processing_id AND p.sw_accession =?", Integer.valueOf(integer));
        int file_sw_accession = (Integer)runQuery[0];
        int processing_id = (Integer)runQuery[1];

        net.sourceforge.seqware.common.model.File file = metadata.getFile(file_sw_accession);
        Assert.assertTrue("could not find file", file != null && file.getSwAccession() == file_sw_accession);
        Assert.assertTrue("file values incorrect", file.getFilePath().equals(file_path) && file.getMetaType().equals(meta_type));
        // tests to verify that parent-accession links are created properly
        runQuery = dbCreator.runQuery(new ArrayHandler(), "SELECT("
                + "(select count(*) from processing_experiments WHERE processing_id = ?),"
                + "(select count(*) from processing_files WHERE processing_id = ?),"
                + "(select count(*) from processing_ius WHERE processing_id = ?),"
                + "(select count(*) from processing_lanes WHERE processing_id = ?),"
                + "(select count(*) from processing_samples WHERE processing_id = ?),"
                + "(select count(*) from processing_sequencer_runs WHERE processing_id = ?),"
                + "(select count(*) from processing_studies WHERE processing_id = ?),"
                + "(select count(*) from processing_relationship WHERE child_id = ?)"
                + ")", Integer.valueOf(processing_id), Integer.valueOf(processing_id), Integer.valueOf(processing_id), Integer.valueOf(processing_id)
                , Integer.valueOf(processing_id), Integer.valueOf(processing_id), Integer.valueOf(processing_id), Integer.valueOf(processing_id));
        String result = runQuery[0].toString();
        Assert.assertTrue("parent links not created", result.equals("(1,1,1,1,1,1,1,1)"));

    }

    @Test
    public void testCreateWorkflowRunWithFilesAndAccessions() {
        launchPlugin("--table", "workflow_run", "--create",
                "--field", "workflow_accession::4",
                "--field", "status::completed",
                 "--parent-accession","834", // experiment
                "--parent-accession", "4765", // ius 
                "--parent-accession", "4707", // lane
                "--parent-accession", "4760", // sample
                "--parent-accession", "4715", // sequencer_run
                "--parent-accession", "120", //study
                "--parent-accession", "10", //processing
                "--file","cool_algorithm1::adamantium/gzip::/datastore/adamantium.gz",
                "--file","hot_algorithm1::corbomite/gzip::/datastore/corbomite.gz");
        String s = getOut();
        String swid = getAndCheckSwid(s);
        int integer = Integer.valueOf(swid);
        // check that file records were created correctly and linked in properly, 0.13.13.6.x does not have access to TestDatabaseCreator, so 
        // let's try some workflow run reporter parsing
        WorkflowRun workflowRun = metadata.getWorkflowRun(integer);
        String workflowRunReport = ((MetadataWS)metadata).getWorkflowRunReport(integer);
        Assert.assertTrue("could not find workflowRun", workflowRun != null && workflowRun.getSwAccession() == integer);
        Assert.assertTrue("could not find files", workflowRunReport.contains("/datastore/adamantium.gz") && workflowRunReport.contains("/datastore/corbomite.gz"));
    }
    
    
    
    @Test
    public void testCreateWorkflow() {
        launchPlugin("--table", "workflow", "--create",
                "--field", "name::CalculateMeaningOfLife",
                "--field", "version::1",
                "--field", "description::'Workflow that simulates the Earth'"
                );
        String s = getOut();
        String swid = getAndCheckSwid(s);
        int integer = Integer.valueOf(swid);
        Workflow workflow = metadata.getWorkflow(integer);
        Assert.assertTrue("could not find workflow", workflow != null && workflow.getSwAccession() == integer);
        
    }
    
    
    
    @Test
    public void testCreateSampleWithExperimentFail() {
        instance.setParams(Arrays.asList("--table", "sample", "--create",
                "--field", "experiment_accession::100000",
                "--field", "title::sampletitle",
                "--field", "description::sampledescription",
                "--field", "organism_id::31"));
        checkExpectedIncorrectParameters();
    }

    @Test
    public void testCreateSampleWithParentFail() {
        instance.setParams(Arrays.asList("--table", "sample", "--create",
                "--field", "parent_sample_accession::100000" ,
                "--field", "title::sampletitle",
                "--field", "description::sampledescription",
                "--field", "organism_id::31"));
        checkExpectedIncorrectParameters();
    }

    //////////////////////////////////////////////////Negative tests
    // re-enabled to test SEQWARE-1331
    @Test
    public void testCreateSampleNoExperimentFail() {
        String eAcc = "8350";

        instance.setParams(Arrays.asList("--table", "sample", "--create",
                "--field", "experiment_accession::" + eAcc,
                "--field", "title::sampletitle",
                "--field", "description::sampledescription",
                "--field", "organism_id::31"));
        checkExpectedIncorrectParameters();
    }

    @Test
    public void testCreateSampleNoParentFail() {
        String eAcc = "8350";

        instance.setParams(Arrays.asList("--table", "sample", "--create",
                "--field", "parent_sample_accession::" + eAcc,
                "--field", "title::sampletitle",
                "--field", "description::sampledescription",
                "--field", "organism_id::31"));
        checkExpectedIncorrectParameters();
    }

    @Test
    public void testCreateSampleNoAccessionFail() {
        instance.setParams(Arrays.asList("--table", "sample", "--create",
                "--field", "title::sampletitle",
                "--field", "description::sampledescription",
                "--field", "organism_id::31"));
        checkExpectedIncorrectParameters();
    }

    // See SEQWARE-1374
    @Test
    public void testCreateStudyFail() {
        instance.setParams(Arrays.asList("--table", "study", "--create",
                "--field", "title::alal" + System.currentTimeMillis(),
                "--field", "description::alal",
                "--field", "accession::1235",
                "--field", "center_name::oicr",
                "--field", "center_project_name::mine",
                "--field", "study_type::42"));
        checkExpectedIncorrectParameters();
    }
    // See SEQWARE-1374
    @Test
    public void testCreateExperimentFail() {
        String sAcc = "120";

        instance.setParams(Arrays.asList("--table", "experiment", "--create",
                "--field", "study_accession::" + sAcc,
                "--field", "title::experimenttitle" + System.currentTimeMillis(),
                "--field", "description::\"Experiment Description\"",
                "--field", "platform_id::42"));
        checkExpectedIncorrectParameters();
    }
    // See SEQWARE-1374
    @Test
    public void testCreateSequencerRunFail() {
        instance.setParams(Arrays.asList("--table", "sequencer_run", "--create",
                "--field", "name::SR" + System.currentTimeMillis(),
                "--field", "description::SRD",
                "--field", "platform_accession::20000",
                "--field", "paired_end::true",
                "--field", "skip::false",
                "--field", "file_path::/funky/filepath"
                ));
        checkExpectedIncorrectParameters();
    }
    @Test
    public void testCreateLaneFail() {
        String rAcc = "20000";

        instance.setParams(Arrays.asList("--table", "lane", "--create",
                "--field", "name::lane",
                "--field", "description::description",
                "--field", "cycle_descriptor::{F*120}{..}{R*120}",
                "--field", "sequencer_run_accession::" + rAcc,
                "--field", "library_strategy_accession::1",
                "--field", "study_type_accession::1",
                "--field", "library_selection_accession::1",
                "--field", "library_source_accession::1",
                "--field", "skip::false",
                "--field", "lane_number::1"));
        checkExpectedIncorrectParameters();
    }

    @Test
    public void testCreateIUSWrongLaneFail() {
        // set an invalid lAcc 
        String lAcc = "20000";
        String sAcc = sampleAccession;
        if (sAcc == null) {
            sAcc = "4760";
        }


        instance.setParams(Arrays.asList("--table", "ius", "--create",
                "--field", "name::ius",
                "--field", "description::des",
                "--field", "lane_accession::" + lAcc,
                "--field", "sample_accession::" + sAcc,
                "--field", "skip::false",
                "--field", "barcode::NoIndex"));
        checkExpectedIncorrectParameters();
    }

    @Test
    public void testCreateIUSWrongSampleFail() {
        String lAcc = laneAccession;
        if (lAcc == null) {
            lAcc = "4707";
        }
        // set an invalid accession
        String sAcc = "20000";
        

        instance.setParams(Arrays.asList("--table", "ius", "--create",
                "--field", "name::ius",
                "--field", "description::des",
                "--field", "lane_accession::" + lAcc,
                "--field", "sample_accession::" + sAcc,
                "--field", "skip::false",
                "--field", "barcode::NoIndex"));
        checkExpectedIncorrectParameters();
    }
    ////////////////////////////////////////////////////////////////////////////
    /////Test interactive components
    ////////////////////////////////////////////////////////////////////////////

    @Test
    public void testInteractiveCreateStudy() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("Is this information correct", "y");
        params.put("title", "alal" + System.currentTimeMillis());
        params.put("description", "alal");
        params.put("accession", "1235");
        params.put("center_name", "oicr");
        params.put("center_project_name", "mine");
        params.put("study_type", "1");
        TestConsoleAdapter.initializeTestInstance().setLine(params);

        launchPlugin("--table", "study", "--create",
                //                "--field", "title::alal" + System.currentTimeMillis(),
                //                "--field", "description::alal",
                //                "--field", "accession::1235",
                //                "--field", "center_name::oicr",
                //                "--field", "center_project_name::mine",
                //                "--field", "study_type::1", 
                "--interactive");
        String s = getOut();
        studyAccession = getAndCheckSwid(s);
        Assert.assertTrue(ConsoleAdapter.getInstance() instanceof TestConsoleAdapter);
    }

    @Test
    public void testInteractiveCreateExperiment() {

        String sAcc = studyAccession;
        if (sAcc == null) {
            sAcc = "120";
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("Is this information correct", "y");
        params.put("study_accession", sAcc);
        params.put("title", "experimenttitle" + System.currentTimeMillis());
        params.put("description", "\"Experiment Description\"");
        params.put("platform_id", "9");
        TestConsoleAdapter.initializeTestInstance().setLine(params);


        launchPlugin("--table", "experiment", "--create",
                //                "--field", "study_accession::" + sAcc,
                //                "--field", "title::experimenttitle" + System.currentTimeMillis(),
                //                "--field", "description::\"Experiment Description\"",
                //                "--field", "platform_id::9", 
                "--interactive");
        String s = getOut();
        experimentAccession = getAndCheckSwid(s);
    }

    @Test
    public void testInteractiveCreateSample() {
        String eAcc = experimentAccession;
        if (eAcc == null) {
            eAcc = "834";
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("Is this information correct", "y");
        params.put("experiment_accession", eAcc);
        params.put("parent_sample_accession", "");
        params.put("description", "sampledescription");
        params.put("organism_id", "31");
        params.put("title", "sampletitle");
        TestConsoleAdapter.initializeTestInstance().setLine(params);

        launchPlugin("--table", "sample", "--create",
                //                "--field", "experiment_accession::" + eAcc,
                //                "--field", "title::sampletitle",
                //                "--field", "description::sampledescription",
                //                "--field", "organism_id::31", 
                "--interactive");
        String s = getOut();
        sampleAccession = getAndCheckSwid(s);
    }

    @Test
    public void testInteractiveCreateSequencerRun() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("Is this information correct", "y");
        params.put("name", "SR" + System.currentTimeMillis());
        params.put("description", "SRD");
        params.put("platform_accession", "20");
        params.put("paired_end", "true");
        params.put("skip", "false");
        params.put("file_path","/home/user/mysequencerrun");
        TestConsoleAdapter.initializeTestInstance().setLine(params);

        launchPlugin("--table", "sequencer_run", "--create",
                //                "--field", "name::SR" + System.currentTimeMillis(),
                //                "--field", "description::SRD",
                //                "--field", "platform_accession::20",
                //                "--field", "paired_end::true",
                //                "--field", "skip::false", 
                "--interactive");
        String s = getOut();
        runAccession = getAndCheckSwid(s);
    }

    @Test
    public void testInteractiveCreateLane() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("Is this information correct", "y");
        TestConsoleAdapter.initializeTestInstance().setLine(params);

        String rAcc = runAccession;
        if (rAcc == null) {
            rAcc = "4715";
        }

        params.put("name", "lane");
        params.put("description", "description");
        params.put("cycle_descriptor", "{F*120}{..}{R*120}");
        params.put("sequencer_run_accession", rAcc);
        params.put("library_strategy_accession", "1");
        params.put("study_type_accession", "1");
        params.put("library_selection_accession", "1");
        params.put("library_source_accession", "1");
        params.put("skip", "false");
        params.put("lane_number", "2");

        launchPlugin("--table", "lane", "--create",
                //                "--field", "name::lane",
                //                "--field", "description::description",
                //                "--field", "cycle_descriptor::{F*120}{..}{R*120}",
                //                "--field", "sequencer_run_accession::" + rAcc,
                //                "--field", "library_strategy_accession::1",
                //                "--field", "study_type_accession::1",
                //                "--field", "library_selection_accession::1",
                //                "--field", "library_source_accession::1",
                //                "--field", "skip::false", 
                "--interactive");
        String s = getOut();
        laneAccession = getAndCheckSwid(s);
    }

    @Test
    public void testInteractiveCreateIUS() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("Is this information correct", "y");
        TestConsoleAdapter.initializeTestInstance().setLine(params);

        String lAcc = laneAccession;
        if (lAcc == null) {
            lAcc = "4707";
        }
        String sAcc = sampleAccession;
        if (sAcc == null) {
            sAcc = "4760";
        }

        params.put("name", "ius");
        params.put("description", "des");
        params.put("lane_accession", lAcc);
        params.put("sample_accession", sAcc);
        params.put("skip", "false");
        params.put("barcode", "NoIndex");

        launchPlugin("--table", "ius", "--create",
                //                "--field", "name::ius",
                //                "--field", "description::des",
                //                "--field", "lane_accession::" + lAcc,
                //                "--field", "sample_accession::" + sAcc,
                //                "--field", "skip::false",
                //                "--field", "barcode::NoIndex", 
                "--interactive");
        String s = getOut();
        getAndCheckSwid(s);

    }

    @Test
    public void testPromptString() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("test-default", "");
        params.put("test-value", "value");
        TestConsoleAdapter.initializeTestInstance().setLine(params);
        Assert.assertNull(((Metadata) instance).promptString("test-default", null));
        Assert.assertEquals("Failed while testing for default", "", ((Metadata) instance).promptString("test-default", ""));
        Assert.assertEquals("Failed while testing for default", "default", ((Metadata) instance).promptString("test-default", "default"));
        Assert.assertEquals("Failed while testing for a value", "value", ((Metadata) instance).promptString("test-value", "default"));
        systemOut.println(getOut());
    }

    @Test
    public void testPromptBoolean() {
        //can test the error checking by extending the TestConsoleAdapter to
        //change on the second query and then checking for the presence of error
        //in the output. But, I'm slightly too lazy for that right now.
        Map<String, String> params = new HashMap<String, String>();
        params.put("test-default", "");
        params.put("test-value", "false");
        TestConsoleAdapter.initializeTestInstance().setLine(params);
        Assert.assertTrue("Failed while testing for default", ((Metadata) instance).promptBoolean("test-default", Boolean.TRUE));
        Assert.assertFalse("Failed while testing for a value", ((Metadata) instance).promptBoolean("test-value", Boolean.TRUE));
        systemOut.println(getOut());
    }

    @Test
    public void testPromptInteger() {
        //can test the error checking by extending the TestConsoleAdapter to
        //change on the second query and then checking for the presence of error
        //in the output. But, I'm slightly too lazy for that right now.
        Map<String, String> params = new HashMap<String, String>();
        params.put("test-default", "");
        params.put("test-value", "10");
        TestConsoleAdapter.initializeTestInstance().setLine(params);
        Assert.assertTrue("Failed while testing for default", 5 == ((Metadata) instance).promptInteger("test-default", 5));
        Assert.assertTrue("Failed while testing for a value", 10 == ((Metadata) instance).promptInteger("test-value", 5));
        systemOut.println(getOut());

    }

    ////////////////////////////////////////////////////////////////////////////
    private String getAndCheckSwid(String s) throws NumberFormatException {
        Matcher match = swidPattern.matcher(s);
        Assert.assertTrue("SWID not found in output.", match.find());
        String swid = match.group(1);
        Assert.assertFalse("The SWID was empty", swid.trim().isEmpty());
        Integer.parseInt(swid.trim());
        return swid;
    }
    @Rule
    public TestRule watchman = new TestWatcher() {
        //This doesn't catch logs that are sent to Log4J

        @Override
        protected void succeeded(Description d) {
            // do not fail on tests that intend on failing
            if (!d.getMethodName().endsWith("Fail")) {
                checkErrors(getErr());
                checkErrors(getOut());
            }
        }
    };

    /**
     * Run an instance with an error and/or failure expected
     */
    private void checkExpectedFailure() {
        checkReturnValue(ReturnValue.SUCCESS, instance.parse_parameters());
        checkReturnValue(ReturnValue.SUCCESS, instance.init());
        checkReturnValue(ReturnValue.FAILURE, instance.do_run());
    }

    /**
     * Run an instance with incorrect parameters expected.
     */
    private void checkExpectedIncorrectParameters() {
        checkReturnValue(ReturnValue.SUCCESS, instance.parse_parameters());
        checkReturnValue(ReturnValue.SUCCESS, instance.init());
        checkReturnValue(ReturnValue.INVALIDPARAMETERS, instance.do_run());
    }
}
