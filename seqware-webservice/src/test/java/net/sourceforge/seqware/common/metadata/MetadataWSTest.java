/*
 * Copyright (C) 2011 SeqWare
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
package net.sourceforge.seqware.common.metadata;

import io.seqware.common.model.ProcessingStatus;
import io.seqware.common.model.WorkflowRunStatus;
import io.seqware.pipeline.SqwKeys;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import net.sourceforge.seqware.common.err.NotFoundException;
import net.sourceforge.seqware.common.factory.DBAccess;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.common.util.testtools.BasicTestDatabaseCreator;
import net.sourceforge.seqware.webservice.resources.tables.FileChildWorkflowRunsResource;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author mtaschuk
 */
public class MetadataWSTest {

    protected static Metadata instance;

    public static Metadata newTestMetadataInstance() {
        // if an alternative database is set, then we need to redirect to look at the defined REST URL
        Map<String, String> settings = ConfigTools.getSettings();
        if (useEmbeddedWebService(settings)) {
            return new MetadataWS("http://localhost:8889/seqware-webservice", "admin@admin.com", "admin");
        } else {
            return MetadataFactory.get(settings);
        }

    }

    public static boolean useEmbeddedWebService(Map<String, String> settings) {
        return !settings.containsKey(SqwKeys.BASIC_TEST_DB_HOST.getSettingKey());
    }

    private final Logger logger;

    public MetadataWSTest() {
        logger = Logger.getLogger(MetadataWSTest.class);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        instance = newTestMetadataInstance();
        BasicTestDatabaseCreator.resetDatabaseWithUsers();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        instance.clean_up();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of addWorkflow method, of class MetadataWS. dyuen asks: Why was this commented out?
     */
    @Test
    public void testAddWorkflow() {
        logger.info("addWorkflow");
        String name = "GATKRecalibrationAndVariantCalling";
        String version = "1.3.16";
        String description = "GATKRecalibrationAndVariantCalling";
        String baseCommand = "java -jar /u/seqware/provisioned-bundles/sqwprod/"
                + "Workflow_Bundle_GATKRecalibrationAndVariantCalling_1.2.29_"
                + "SeqWare_0.10.0/GATKRecalibrationAndVariantCalling/1.x.x/lib/"
                + "seqware-pipeline-0.10.0.jar --plugin net.sourceforge.seqware."
                + "pipeline.plugins.WorkflowLauncher -- --bundle /u/seqware/"
                + "provisioned-bundles/sqwprod/Workflow_Bundle_GATKRecalibration"
                + "AndVariantCalling_1.2.29_SeqWare_0.10.0 --workflow GATK" + "RecalibrationAndVariantCalling --version 1.3.16";
        java.io.File configFile = null, templateFile = null;
        try {
            configFile = new java.io.File(MetadataWSTest.class.getResource("GATKRecalibrationAndVariantCalling_1.3.16.ini").toURI());
            templateFile = new java.io.File(MetadataWSTest.class.getResource("GATKRecalibrationAndVariantCalling_1.3.16.ftl").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        java.io.File provisionDir = new java.io.File("/u/seqware/provisioned-bundles"
                + "/sqwprod/Workflow_Bundle_GATKRecalibrationAndVariantCalling_" + "1.2.29_SeqWare_0.10.0/");
        int expResult = ReturnValue.SUCCESS;
        ReturnValue result = instance.addWorkflow(name, version, description, baseCommand, configFile.getAbsolutePath(),
                templateFile.getAbsolutePath(), provisionDir.getAbsolutePath(), true, "", false, null, null, null, null);
        Assert.assertEquals(expResult, result.getExitStatus());

        // test certain properties of the workflow parameters in relation to SEQWARE-1444
        String workflow_id = result.getAttribute("sw_accession");
        Workflow workflow = instance.getWorkflow(Integer.valueOf(workflow_id));
        Assert.assertTrue("workflow retrieved is invalid", workflow.getWorkflowId() == result.getReturnValue());
        SortedSet<WorkflowParam> workflowParams = instance.getWorkflowParams(workflow_id);
        Assert.assertTrue("invalid number of workflow params retrieved", workflowParams.size() == 33);
        // check out the values of some long values
        for (WorkflowParam param : workflowParams) {
            switch (param.getKey()) {
                case "bam_inputs":
                    Assert.assertTrue(
                            "bam_inputs invalid",
                            param.getDefaultValue()
                                    .equals("${workflow_bundle_dir}/GATKRecalibrationAndVariantCalling/1.x.x/data/test/PCSI0022P.val.bam,${workflow_bundle_dir}/GATKRecalibrationAndVariantCalling/1.x.x/data/test/PCSI0022R.val.bam,${workflow_bundle_dir}/GATKRecalibrationAndVariantCalling/1.x.x/data/test/PCSI0022X.val.bam,${workflow_bundle_dir}/GATKRecalibrationAndVariantCalling/1.x.x/data/test/PCSI0022C.val.bam"));
                    break;
                case "chr_sizes":
                    Assert.assertTrue(
                            "chr_sizes invalid",
                            param.getDefaultValue()
                                    .equals("chr1:249250621,chr2:243199373,chr3:198022430,chr4:191154276,chr5:180915260,chr6:171115067,chr7:159138663,chr8:146364022,chr9:141213431,chr10:135534747,chr11:135006516,chr12:133851895,chr13:115169878,chr14:107349540,chr15:102531392,chr16:90354753,chr17:81195210,chr18:78077248,chr19:59128983,chr20:63025520,chr21:48129895,chr22:51304566,chrX:155270560,chrY:59373566,chrM:16571"));
                    break;
            }
        }
    }

    /**
     * Test of addWorkflow method, of class MetadataWS with a novoalign.ini.
     */
    @Test
    public void testAddNovoAlignWorkflow() {
        logger.info("addWorkflow");
        String name = "novoalign";
        String version = "0.13.6.2";
        String description = "Novoalign";
        String baseCommand = "java -jar /u/seqware/provisioned-bundles/sqwprod/"
                + "Workflow_Bundle_GATKRecalibrationAndVariantCalling_1.2.29_"
                + "SeqWare_0.10.0/GATKRecalibrationAndVariantCalling/1.x.x/lib/"
                + "seqware-pipeline-0.10.0.jar --plugin net.sourceforge.seqware."
                + "pipeline.plugins.WorkflowLauncher -- --bundle /u/seqware/"
                + "provisioned-bundles/sqwprod/Workflow_Bundle_GATKRecalibration"
                + "AndVariantCalling_1.2.29_SeqWare_0.10.0 --workflow GATK" + "RecalibrationAndVariantCalling --version 1.3.16";
        java.io.File configFile = null, templateFile = null;
        try {
            configFile = new java.io.File(MetadataWSTest.class.getResource("novoalign.ini").toURI());
            templateFile = new java.io.File(MetadataWSTest.class.getResource("GATKRecalibrationAndVariantCalling_1.3.16.ftl").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        java.io.File provisionDir = new java.io.File("/u/seqware/provisioned-bundles"
                + "/sqwprod/Workflow_Bundle_GATKRecalibrationAndVariantCalling_" + "1.2.29_SeqWare_0.10.0/");
        int expResult = ReturnValue.SUCCESS;
        ReturnValue result = instance.addWorkflow(name, version, description, baseCommand, configFile.getAbsolutePath(),
                templateFile.getAbsolutePath(), provisionDir.getAbsolutePath(), true, "", false, null, null, null, null);
        Assert.assertEquals(expResult, result.getExitStatus());

        // test certain properties of the workflow parameters in relation to SEQWARE-1444
        String workflow_id = result.getAttribute("sw_accession");
        Workflow workflow = instance.getWorkflow(Integer.valueOf(workflow_id));
        Assert.assertTrue("workflow retrieved is invalid", workflow.getWorkflowId() == result.getReturnValue());
        SortedSet<WorkflowParam> workflowParams = instance.getWorkflowParams(workflow_id);
        Assert.assertTrue("invalid number of workflow params retrieved", workflowParams.size() == 34);
        // check out the values of some suspicious values
        for (WorkflowParam param : workflowParams) {
            switch (param.getKey()) {
                case "colorspace":
                    Assert.assertTrue("colorspace invalid", param.getDefaultValue().equals("0"));
                    break;
                case "novoalign_r1_adapter_trim":
                    Assert.assertTrue("novoalign_r1_adapter_trim invalid",
                            param.getDefaultValue().equals("-a AGATCGGAAGAGCGGTTCAGCAGGAATGCCGAGACCG"));
                    break;
            }
        }

    }

    /**
     * Test of add_empty_processing_event method, of class MetadataWS.
     */
    @Test
    public void testAdd_empty_processing_event() {
        Date beforeDate = new Timestamp(System.currentTimeMillis());
        logger.info("add_empty_processing_event");
        int[] parentIDs = { 3, 5 };
        ReturnValue result = instance.add_empty_processing_event(parentIDs);
        Assert.assertEquals("Adding empty processing event was unsuccessful: ", 0, result.getExitStatus());
        testTimestamp("select max(create_tstmp) from processing " + "where processing_id in "
                + "(select distinct child_id from processing_relationship " + "where parent_id IN (3,5))", "max", beforeDate);

    }

    protected void testTimestamp(String sql, final String colname, Date beforeDate) {
        logger.debug(sql);
        try {
            Date date = DBAccess.get().executeQuery(sql, new ResultSetHandler<Date>() {
                @Override
                public Date handle(ResultSet rs) throws SQLException {
                    if (rs.next()) {
                        return rs.getTimestamp(colname);
                    } else {
                        return null;
                    }
                }
            });

            if (date != null) {
                if (date.before(beforeDate)) {
                    logger.debug("before " + beforeDate.toString());
                    logger.debug("update " + date.toString());
                    Assert.fail("Update failed.");
                }

            } else {
                Assert.fail("No rows in ResultSet");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Assert.fail("SQL Exception");
        } finally {
            DBAccess.close();
        }
    }

    protected void testCount(String sql, int expectedCount) {
        logger.debug(sql);
        try {
            int count = DBAccess.get().executeQuery(sql, new ResultSetHandler<Integer>() {
                @Override
                public Integer handle(ResultSet rs) throws SQLException {
                    if (rs.next()) {
                        return rs.getInt("count");
                    } else {
                        return 0;
                    }
                }
            });

            if (count > 0) {
                Assert.assertEquals("Expected count is not the same:" + expectedCount + "!=<" + count, true, (expectedCount <= count));

            } else {
                Assert.fail("No rows in ResultSet");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            Assert.fail("SQL Exception");
        } finally {
            DBAccess.close();
        }
    }

    /**
     * Test of add_empty_processing_event_by_parent_accession method, of class MetadataWS.
     */
    @Test
    public void testAdd_empty_processing_event_by_parent_accession() {
        logger.info("add_empty_processing_event_by_parent_accession");
        Date beforeDate = new Timestamp(System.currentTimeMillis());
        int[] parentAccessions = { 4707, 4765 };
        int expResult = ReturnValue.SUCCESS;
        ReturnValue result = instance.add_empty_processing_event_by_parent_accession(parentAccessions);
        Assert.assertEquals(expResult, result.getExitStatus());
        testTimestamp("select max(create_tstmp) from processing " + "where processing_id in "
                + "(select distinct processing_id from processing_lanes pl, lane l " + "where l.sw_accession = 4707);", "max", beforeDate);
        testTimestamp("select max(create_tstmp) from processing " + "where processing_id in "
                + "(select distinct processing_id from processing_ius pl, ius l " + "where l.sw_accession = 4765);", "max", beforeDate);
    }

    /**
     * Test of add_task_group method, of class MetadataWS.
     */
    // @Test
    public void testAdd_task_group() {
        logger.info("add_task_group");
        Date beforeDate = new Timestamp(System.currentTimeMillis());
        int[] parentIDs = { 5, 7 };
        int[] childIDs = { 10, 12 };
        String algorithm = "metadataWSTest";
        String description = "Testing the MetadataWS";
        int expResult = 0;
        ReturnValue result = instance.add_task_group(parentIDs, childIDs, algorithm, description);
        Assert.assertNotSame(expResult, result.getReturnValue());
        testTimestamp("select max(create_tstmp), task_group from processing " + "where processing_id in "
                + "(select distinct child_id from processing_relationship " + "where parent_id IN (5,7)) " + "AND processing_id in "
                + "(select distinct parent_id from processing_relationship " + "where child_id IN (10,12)) GROUP BY task_group", "max",
                beforeDate);
    }

    /**
     * Test of add_workflow_run method, of class MetadataWS.
     */
    // @Test
    public void testAdd_workflow_run() {
        logger.info("add_workflow_run");
        Date beforeDate = new Timestamp(System.currentTimeMillis());
        int workflowAccession = 2860;
        int expResult = 0;
        int result = instance.add_workflow_run(workflowAccession);
        Assert.assertNotSame(expResult, result);
        testTimestamp("select max(create_tstmp) from workflow_run " + "where workflow_id = 15", "max", beforeDate);
    }

    /**
     * Test of add_workflow_run_ancestor method, of class MetadataWS.
     */
    // @Test
    public void testAdd_workflow_run_ancestor() {
        logger.info("add_workflow_run_ancestor");
        Date beforeDate = new Timestamp(System.currentTimeMillis());
        // int workflowRunAccession = 862;//double-checked now...//but clearly not because it's still not finding it
        // int workflowRunAccession = 64;// what the hell!
        int workflowRunAccession = 862;
        int processingId = 4921;
        instance.add_workflow_run_ancestor(workflowRunAccession, processingId);
        testTimestamp("select update_tstmp from processing " + "where ancestor_workflow_run_id = 22 and processing_id=4921",
                "update_tstmp", beforeDate);

    }

    /**
     * Test of associate_processing_event_with_parents_and_child method, of class MetadataWS.
     */
    // @Test
    public void testAssociate_processing_event_with_parents_and_child() {
        logger.info("associate_processing_event_with_parents_and_child");
        int processingID = 773;
        int[] parentIDs = { 16, 18 };
        int[] childIDs = { 20, 22 };
        int expResult = ReturnValue.SUCCESS;
        ReturnValue result = instance.associate_processing_event_with_parents_and_child(processingID, parentIDs, childIDs);
        Assert.assertEquals(expResult, result.getExitStatus());
        testCount("select count(*) from processing_relationship " + "where (parent_id=773 AND child_id IN (20,22)) "
                + "OR (child_id=12 AND parent_id IN (16,18));", 4);
    }

    /**
     * Test of get_workflow_info method, of class MetadataWS.
     */
    // @Test
    public void testGet_workflow_info() {
        logger.info("get_workflow_info");
        int workflowAccession = 2861;
        String dbName = "GzippedFastqQualityReportAndFilter";
        Map result = instance.get_workflow_info(workflowAccession);
        Assert.assertEquals(dbName, result.get("name"));
    }

    /**
     * Test of get_workflow_run_accession method, of class MetadataWS.
     */
    // @Test
    public void testGet_workflow_run_accession() {
        logger.info("get_workflow_run_accession");
        int workflowRunId = 23;
        int expResult = 863;
        int result = instance.get_workflow_run_accession(workflowRunId);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of get_workflow_run_id method, of class MetadataWS.
     */
    // @Test
    public void testGet_workflow_run_id() {
        logger.info("get_workflow_run_id");
        int workflowRunAccession = 863;
        int expResult = 23;
        int result = instance.get_workflow_run_id(workflowRunAccession);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of linkWorkflowRunAndParent method, of class MetadataWS.
     * 
     * @throws java.lang.Exception
     */
    // @Test
    public void testLinkWorkflowRunAndParent() throws Exception {
        logger.info("linkWorkflowRunAndParent");
        int workflowRunId = 24;
        int parentAccession = 4789;
        boolean expResult = true;
        boolean result = instance.linkWorkflowRunAndParent(workflowRunId, parentAccession);
        Assert.assertEquals(expResult, result);
        testCount("select count(*) from ius_workflow_runs where ius_id = 5 and workflow_run_id = 24", 1);
    }

    /**
     * Test of mapProcessingIdToAccession method, of class MetadataWS.
     */
    // @Test
    public void testMapProcessingIdToAccession() {
        logger.info("mapProcessingIdToAccession");
        int processingId = 26;
        int expResult = 48;
        int result = instance.mapProcessingIdToAccession(processingId);
        Assert.assertEquals(expResult, result);
    }

    /**
     * Test of processing_event_to_task_group method, of class MetadataWS.
     */
    // @Test
    public void testProcessing_event_to_task_group() {
        logger.info("processing_event_to_task_group");
        int processingID = 4923;
        int[] parentIDs = { 30, 32 };
        int[] childIDs = { 34, 36 };
        String algorithm = "MetadataWS testProcessing_event_to_task_group ";
        String description = "testProcessing_event_to_task_group";
        int expResult = ReturnValue.SUCCESS;
        ReturnValue result = instance.processing_event_to_task_group(processingID, parentIDs, childIDs, algorithm, description);
        Assert.assertEquals(expResult, result.getExitStatus());
        testCount("select count(*) from processing_relationship " + "where (parent_id=4923 AND child_id IN (34,36)) "
                + "OR (child_id=4923 AND parent_id IN (30,32));", 4);
    }

    /**
     * Test of update_processing_event method, of class MetadataWS.
     */
    // @Test
    public void testUpdate_processing_event() {
        logger.info("update_processing_event");
        Date beforeDate = new Timestamp(System.currentTimeMillis());
        int processingID = 4939;
        ReturnValue retval = new ReturnValue();
        retval.setExitStatus(ReturnValue.FREEMARKEREXCEPTION);
        retval.setAlgorithm("algo testUpdate_processing_event()");
        retval.setParameters("parameters testUpdate_processing_event()");
        retval.setProcessExitStatus(ReturnValue.DBCOULDNOTDISCONNECT);
        int expResult = ReturnValue.SUCCESS;
        ReturnValue result = instance.update_processing_event(processingID, retval);
        Assert.assertEquals(expResult, result.getExitStatus());
        testTimestamp("select update_tstmp from processing " + "where processing_id=4939 "
                + "and process_exit_status=81 and algorithm='algo testUpdate_processing_event()' " + "and status = 'success'",
                "update_tstmp", beforeDate);
    }

    /**
     * Test of update_processing_status method, of class MetadataWS.
     */
    // @Test
    public void testUpdate_processing_status() {
        logger.info("update_processing_status");
        Date beforeDate = new Timestamp(System.currentTimeMillis());
        int processingID = 5247;
        ProcessingStatus status = ProcessingStatus.success;
        int expResult = ReturnValue.SUCCESS;
        ReturnValue result = instance.update_processing_status(processingID, status);
        Assert.assertEquals(expResult, result.getExitStatus());
        testTimestamp("select update_tstmp from processing " + "where processing_id=5247 " + "and status='testUpdate_processing_status()'",
                "update_tstmp", beforeDate);
    }

    /**
     * Test of update_processing_workflow_run method, of class MetadataWS.
     */
    // @Test
    public void testUpdate_processing_workflow_run() {
        logger.info("update_processing_workflow_run");
        Date beforeDate = new Timestamp(System.currentTimeMillis());
        int processingID = 5395;
        int workflowRunAccession = 872;
        int expResult = ReturnValue.SUCCESS;
        ReturnValue result = instance.update_processing_workflow_run(processingID, workflowRunAccession);
        Assert.assertEquals(expResult, result.getExitStatus());
        testTimestamp("select update_tstmp from processing " + "where processing_id=5395 and workflow_run_id=24;", "update_tstmp",
                beforeDate);

    }

    /**
     * Test of update_workflow_run method, of class MetadataWS.
     */
    // @Test
    public void testUpdate_workflow_run() {
        logger.info("update_workflow_run");
        Date beforeDate = new Timestamp(System.currentTimeMillis());
        int workflowRunId = 32;
        String pegasusCmd = "test pegasus cmd";
        String workflowTemplate = "test template";
        WorkflowRunStatus status = WorkflowRunStatus.completed;
        String statusCmd = "test command";
        String workingDirectory = "test working dir";
        String dax = "test dax";
        String ini = "test ini";
        String host = "localhost";
        String testEngine = "test engine";
        int expResult = 2862;
        ReturnValue result = instance.update_workflow_run(workflowRunId, pegasusCmd, workflowTemplate, status, statusCmd, workingDirectory,
                dax, ini, host, null, null, testEngine, null);
        Assert.assertEquals(expResult, result.getReturnValue());
        testTimestamp("select update_tstmp from workflow_run " + "where workflow_run_id=32;", "update_tstmp", beforeDate);
    }

    // @Test
    public void testListInstalledWorkflow() {
        logger.info("listInstalledWorkflows");
        instance.listInstalledWorkflows();
    }

    // @Test
    public void testUpdateWorkflow() {
        Date beforeDate = new Timestamp(System.currentTimeMillis());
        logger.info("updateWorkflow");
        ReturnValue ret = instance.updateWorkflow(15, "http://testtest");
        Assert.assertEquals("Did not return with a success ReturnValue", ReturnValue.SUCCESS, ret.getExitStatus());
        testTimestamp("select update_tstmp from workflow " + "where workflow_id=15", "update_tstmp", beforeDate);
    }

    // @Test
    public void testGetWorkflowAccession() {
        logger.info("getWorkflowAccession");
        int accession = instance.getWorkflowAccession("FastqQualityReportAndFilter", "0.10.1");
        Assert.assertNotSame("Accession not found", -1, accession);
        Assert.assertEquals("Incorrect accession found", 2860, accession);
    }

    @Test
    public void testGetFile() {
        logger.info("testGetFile");
        File file = instance.getFile(4761);
        Assert.assertEquals("The file cannot be found (or the file path is wrong for some reason).",
                "s3://abcco.analysis/sample_data/Sample_Tumour/simulated_1.fastq.gz", file.getFilePath());
    }

    /**
     * Find candidates for this test via "select * from File f, Processing_files pf, Processing pp, Processing_relationship pr, Processing
     * pc , Workflow_Run wr WHERE f.file_id=pf.file_id AND pf.processing_id=pp.processing_id AND pp.processing_id=pr.parent_id AND
     * pc.processing_id=pr.child_id AND pc.workflow_run_id = wr.workflow_run_id ORDER BY f.sw_accession;"
     */
    @Test
    public void testGetWorkflowRunsRelatedToFile_basic() {
        logger.info("testGetWorkflowRunsRelatedToFile_basic");
        List<Integer> files = new ArrayList<>();
        boolean exceptionThrown = false;
        List<WorkflowRun> result;
        try {
            result = instance.getWorkflowRunsAssociatedWithFiles(files,
                    FileChildWorkflowRunsResource.SEARCH_TYPE.CHILDREN_VIA_PROCESSING_RELATIONSHIP.toString());
        } catch (NotFoundException nfe) {
            exceptionThrown = true;
        }
        Assert.assertTrue("exception not thrown on invalid input", exceptionThrown);
        files.add(-1);
        exceptionThrown = false;
        try {
            result = instance.getWorkflowRunsAssociatedWithFiles(files,
                    FileChildWorkflowRunsResource.SEARCH_TYPE.CHILDREN_VIA_PROCESSING_RELATIONSHIP.toString());
        } catch (NotFoundException nfe) {
            exceptionThrown = true;
        }
        Assert.assertTrue("exception not thrown on invalid object", exceptionThrown);
        files.clear();
        files.add(835);
        result = instance.getWorkflowRunsAssociatedWithFiles(files,
                FileChildWorkflowRunsResource.SEARCH_TYPE.CHILDREN_VIA_PROCESSING_RELATIONSHIP.toString());
        Assert.assertTrue("basic call failed", result.size() == 1 && result.get(0).getSwAccession() == 862);
    }

    @Test
    public void testGetWorkflowRunsRelatedToFile_multipleFiles() {
        logger.info("testGetWorkflowRunsRelatedToFile_multipleFiles");
        List<Integer> files = new ArrayList<>();
        files.add(835);
        files.add(838);
        List<WorkflowRun> result = instance.getWorkflowRunsAssociatedWithFiles(files,
                FileChildWorkflowRunsResource.SEARCH_TYPE.CHILDREN_VIA_PROCESSING_RELATIONSHIP.toString());
        Assert.assertTrue("multiple file call failed", result.size() == 2);
        Assert.assertTrue("multiple file call failed", result.get(0).getSwAccession() == 862 || result.get(0).getSwAccession() == 863);
        Assert.assertTrue("multiple file call failed", result.get(1).getSwAccession() == 862 || result.get(1).getSwAccession() == 863);
    }

    /**
     * Unfortunately, the next two tests do not seem to have appropriate candidates in the test database for now as checked via select
     * f.sw_accession, wr.workflow_run_id, wr.sw_accession, wr2.workflow_run_id, wr2.sw_accession from File f, Processing_files pf,
     * Processing pp, Workflow_Run wr, ius_workflow_runs iwr, ius i, ius_workflow_runs iwr2, workflow_run wr2 WHERE f.file_id=pf.file_id AND
     * pf.processing_id=pp.processing_id AND (pp.ancestor_workflow_run_id=wr.workflow_run_id OR pp.workflow_run_id=wr.workflow_run_id) AND
     * wr.workflow_run_id=iwr.workflow_run_id AND iwr.ius_id = i.ius_id AND iwr2.ius_id = i.ius_id AND wr2.workflow_run_id =
     * iwr2.workflow_run_id ORDER BY f.sw_accession;
     * 
     * select f.sw_accession, wr.workflow_run_id, wr.sw_accession, wr2.workflow_run_id, wr2.sw_accession from File f, Processing_files pf,
     * Processing pp, Workflow_Run wr, lane_workflow_runs iwr, lane i, lane_workflow_runs iwr2, workflow_run wr2 WHERE f.file_id=pf.file_id
     * AND pf.processing_id=pp.processing_id AND (pp.ancestor_workflow_run_id=wr.workflow_run_id OR pp.workflow_run_id=wr.workflow_run_id)
     * AND wr.workflow_run_id=iwr.workflow_run_id AND iwr.lane_id = i.lane_id AND iwr2.lane_id = i.lane_id AND wr2.workflow_run_id =
     * iwr2.workflow_run_id ORDER BY f.sw_accession;
     */
    @Test
    public void testGetWorkflowRunsRelatedToFile_viaIUS() {
        logger.info("testGetWorkflowRunsRelatedToFile_IUS");
        List<Integer> files = new ArrayList<>();
        files.add(835);
        files.add(838);
        List<WorkflowRun> result = instance.getWorkflowRunsAssociatedWithFiles(files,
                FileChildWorkflowRunsResource.SEARCH_TYPE.CHILDREN_VIA_IUS_WORKFLOW_RUN.toString());
        Assert.assertTrue("testGetWorkflowRunsRelatedToFile_IUS failed", result.isEmpty());
    }

    @Test
    public void testGetWorkflowRunsRelatedToFile_viaLane() {
        logger.info("testGetWorkflowRunsRelatedToFile_lane");
        List<Integer> files = new ArrayList<>();
        files.add(835);
        files.add(838);
        List<WorkflowRun> result = instance.getWorkflowRunsAssociatedWithFiles(files,
                FileChildWorkflowRunsResource.SEARCH_TYPE.CHILDREN_VIA_LANE_WORKFLOW_RUN.toString());
        Assert.assertTrue("testGetWorkflowRunsRelatedToFile_lane failed", result.isEmpty());
    }

    @Test
    public void testGetAllSequencerRuns() {
        Log.info("testGetAllSequencerRuns");
        List<SequencerRun> runs = instance.getAllSequencerRuns();
        Assert.assertFalse("There are no sequencer runs!", runs.isEmpty());
    }

    @Test
    public void testGetLanesFrom() {
        Log.info("testGetLanesFrom");
        List<Lane> lanes = instance.getLanesFrom(4715);
        Assert.assertFalse("There are no lanes for sequencer run!", lanes.isEmpty());
    }

    @Test
    public void testGetIUSFromLane() {
        Log.info("testGetIUSFromLane");
        List<IUS> iuses = instance.getIUSFrom(4764);
        Assert.assertFalse(iuses.isEmpty());
    }

    @Test
    public void testGetIUSFromSample() {
        Log.info("testGetIUSFromSample");
        List<IUS> iuses = instance.getIUSFrom(4783);
        Assert.assertFalse(iuses.isEmpty());
    }

    @Test
    public void testGetExperimentsFrom() {
        Log.info("testGetExperimentsFrom");
        List<Experiment> experiments = instance.getExperimentsFrom(120);
        Assert.assertFalse(experiments.isEmpty());
    }

    @Test
    public void testGetSamplesFromExperiment() {
        Log.info("testGetSamplesFromExperiment");
        List<Sample> samples = instance.getSamplesFrom(6157);
        Assert.assertFalse(samples.isEmpty());
    }

    @Test
    public void testGetChildSamplesFrom() {
        Log.info("testGetChildSamplesFrom");
        List<Sample> samples = instance.getChildSamplesFrom(1940);
        Assert.assertFalse(samples.isEmpty());
    }

    @Test
    public void testGetParentSamplesFrom() {
        Log.info("testGetParentSamplesFrom");
        List<Sample> samples = instance.getParentSamplesFrom(1944);
        Assert.assertFalse(samples.isEmpty());
    }

    @Test
    public void testGetInputFilesForExistingWorkflowRun() {
        WorkflowRun workflowRun = instance.getWorkflowRun(4735);
        Assert.assertTrue("workflow run with no parents should have empty set", workflowRun.getInputFileAccessions().isEmpty());
    }

    @Test
    public void testUpdateWorkflowRunWithInputFiles() {
        BasicTestDatabaseCreator.resetDatabaseWithUsers();
        final int wr_sw_accession = 6603;
        WorkflowRun wr = instance.getWorkflowRun(wr_sw_accession);
        // should already be blank
        instance.update_workflow_run(wr.getWorkflowRunId(), wr.getCommand(), wr.getTemplate(), wr.getStatus(), wr.getStatusCmd(),
                wr.getCurrentWorkingDir(), wr.getDax(), wr.getIniFile(), wr.getHost(), wr.getStdOut(), wr.getStdErr(),
                wr.getWorkflowEngine(), new HashSet<Integer>());
        wr = instance.getWorkflowRun(wr_sw_accession);
        Assert.assertTrue("empty input file set should blank", wr.getInputFileAccessions().isEmpty());
        // try a empty set and asking for it back
        wr.getInputFileAccessions().clear();
        instance.update_workflow_run(wr.getWorkflowRunId(), wr.getCommand(), wr.getTemplate(), wr.getStatus(), wr.getStatusCmd(),
                wr.getCurrentWorkingDir(), wr.getDax(), wr.getIniFile(), wr.getHost(), wr.getStdOut(), wr.getStdErr(),
                wr.getWorkflowEngine(), wr.getInputFileAccessions());
        wr = instance.getWorkflowRun(wr_sw_accession);
        Assert.assertTrue("nulled  input file set  should be blank", wr.getInputFileAccessions().isEmpty());
        final int f1_sw_accession = 835;
        wr.getInputFileAccessions().add(f1_sw_accession);
        final int f2_sw_accession = 838;
        wr.getInputFileAccessions().add(f2_sw_accession);
        final int f3_sw_accession = 866;
        wr.getInputFileAccessions().add(f3_sw_accession);
        instance.update_workflow_run(wr.getWorkflowRunId(), wr.getCommand(), wr.getTemplate(), wr.getStatus(), wr.getStatusCmd(),
                wr.getCurrentWorkingDir(), wr.getDax(), wr.getIniFile(), wr.getHost(), wr.getStdOut(), wr.getStdErr(),
                wr.getWorkflowEngine(), wr.getInputFileAccessions());
        wr = instance.getWorkflowRun(wr_sw_accession);
        Assert.assertTrue("updated  input file set  should be size 3, was " + wr.getInputFileAccessions().size(), wr
                .getInputFileAccessions().size() == 3);
        final int f4_sw_accession = 867;
        wr.getInputFileAccessions().add(f4_sw_accession);
        instance.update_workflow_run(wr.getWorkflowRunId(), wr.getCommand(), wr.getTemplate(), wr.getStatus(), wr.getStatusCmd(),
                wr.getCurrentWorkingDir(), wr.getDax(), wr.getIniFile(), wr.getHost(), wr.getStdOut(), wr.getStdErr(),
                wr.getWorkflowEngine(), wr.getInputFileAccessions());
        wr = instance.getWorkflowRun(wr_sw_accession);
        Assert.assertTrue("updated  input file set  should be size 4, was " + wr.getInputFileAccessions().size(), wr
                .getInputFileAccessions().size() == 4);
    }

    @Test
    public void getDirectFilesAssociatedWithWorkflowRuns() {
        BasicTestDatabaseCreator.resetDatabaseWithUsers();

        List<Integer> files = new ArrayList<>();
        // try getting nothing
        List<WorkflowRun> result = instance.getWorkflowRunsAssociatedWithInputFiles(files);
        Assert.assertTrue("should have been no files", result.isEmpty());
        final int workflow_run1 = 6480;

        // build required file structures
        WorkflowRun wr = instance.getWorkflowRun(workflow_run1);
        Assert.assertTrue("nulled  input file set  should be blank", wr.getInputFileAccessions().isEmpty());
        final int f1_sw_accession = 1963;
        wr.getInputFileAccessions().add(f1_sw_accession);
        final int f2_sw_accession = 1978;
        wr.getInputFileAccessions().add(f2_sw_accession);
        final int f3_sw_accession = 2139;
        wr.getInputFileAccessions().add(f3_sw_accession);
        instance.update_workflow_run(wr.getWorkflowRunId(), wr.getCommand(), wr.getTemplate(), wr.getStatus(), wr.getStatusCmd(),
                wr.getCurrentWorkingDir(), wr.getDax(), wr.getIniFile(), wr.getHost(), wr.getStdOut(), wr.getStdErr(),
                wr.getWorkflowEngine(), wr.getInputFileAccessions());
        // add a couple more files to a different workflow_run
        final int f4_sw_accession = 2160;
        final int workflow_run2 = 6603;
        wr = instance.getWorkflowRun(workflow_run2);
        wr.getInputFileAccessions().add(f1_sw_accession);
        wr.getInputFileAccessions().add(f4_sw_accession);
        instance.update_workflow_run(wr.getWorkflowRunId(), wr.getCommand(), wr.getTemplate(), wr.getStatus(), wr.getStatusCmd(),
                wr.getCurrentWorkingDir(), wr.getDax(), wr.getIniFile(), wr.getHost(), wr.getStdOut(), wr.getStdErr(),
                wr.getWorkflowEngine(), wr.getInputFileAccessions());
        // try a file accession that both workflow runs should have
        files.add(f1_sw_accession);
        result = instance.getWorkflowRunsAssociatedWithInputFiles(files);
        Assert.assertTrue("should have been 2 workflow runs, found " + result.size(), result.size() == 2);
        Assert.assertTrue("incorrect workflow runs found", result.get(0).getSwAccession() == workflow_run1);
        Assert.assertTrue("incorrect workflow runs found", result.get(1).getSwAccession() == workflow_run2);
        files.clear();
        // try a file accession that only the latter workflow run should have
        files.add(f4_sw_accession);
        result = instance.getWorkflowRunsAssociatedWithInputFiles(files);
        Assert.assertTrue("should have been 1 file, found " + result.size(), result.size() == 1);
        Assert.assertTrue("incorrect workflow runs found", result.get(0).getSwAccession() == workflow_run2);
        // try both file accessions, should get both back (allows for partial matching)
        files.add(f1_sw_accession);
        result = instance.getWorkflowRunsAssociatedWithInputFiles(files);
        Assert.assertTrue("should have been 2 workflow runs, found " + result.size(), result.size() == 2);
        Assert.assertTrue("incorrect workflow runs found", result.get(0).getSwAccession() == workflow_run1);
        Assert.assertTrue("incorrect workflow runs found", result.get(1).getSwAccession() == workflow_run2);
        // go back and check that the workflow runs include this input file
        for (WorkflowRun r : result) {
            Assert.assertTrue("input files for the returned workflow runs should include f1 or f4",
                    r.getInputFileAccessions().contains(f1_sw_accession) || r.getInputFileAccessions().contains(f4_sw_accession));
        }
    }

}
