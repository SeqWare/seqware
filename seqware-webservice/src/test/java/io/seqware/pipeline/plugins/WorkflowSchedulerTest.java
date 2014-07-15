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
package io.seqware.pipeline.plugins;

import io.seqware.Engines;
import io.seqware.common.model.WorkflowRunStatus;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.common.util.maptools.MapTools;
import net.sourceforge.seqware.common.util.maptools.ReservedIniKeys;
import net.sourceforge.seqware.common.util.testtools.BasicTestDatabaseCreator;
import net.sourceforge.seqware.pipeline.plugins.ExtendedPluginTest;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Runs the tests for the FileLinker
 * 
 * @author dyuen
 */
public class WorkflowSchedulerTest extends ExtendedPluginTest {

    @BeforeClass
    public static void beforeClass() {
        BasicTestDatabaseCreator.resetDatabaseWithUsers();
    }

    @Before
    @Override
    public void setUp() {
        instance = new WorkflowScheduler();
        super.setUp();
    }

    @Test
    public void testNormalSchedule() {
        launchPlugin("--workflow-accession", "2860", "--host", FileTools.getLocalhost(null).hostname);

        String s = getOut();
        String firstWorkflowRun = getAndCheckSwid(s);

        BasicTestDatabaseCreator dbCreator = new BasicTestDatabaseCreator();
        List<Object[]> runQuery = dbCreator.runQuery(new ArrayListHandler(), "select r.status, r.workflow_id from workflow_run r\n"
                + "WHERE \n" + "r.sw_accession = ?\n" + "; ", Integer.valueOf(firstWorkflowRun));
        Assert.assertTrue("schedule workflow is incorrect " + runQuery.get(0)[0].toString() + " " + runQuery.get(0)[1].toString(),
                runQuery.get(0)[0].equals(WorkflowRunStatus.submitted.toString()) && runQuery.get(0)[1].equals(15));
    }

    @Test
    public void testOozieWorkflowEngine() {
        launchPlugin("--workflow-accession", "2860", "--host", FileTools.getLocalhost(null).hostname, "--workflow-engine",
                Engines.TYPES.oozie.toString());

        String s = getOut();
        String firstWorkflowRun = getAndCheckSwid(s);

        BasicTestDatabaseCreator dbCreator = new BasicTestDatabaseCreator();
        List<Object[]> runQuery = dbCreator.runQuery(new ArrayListHandler(),
                "select r.status, r.workflow_id, r.workflow_engine from workflow_run r\n" + "WHERE \n" + "r.sw_accession = ?\n" + "; ",
                Integer.valueOf(firstWorkflowRun));
        Assert.assertTrue("schedule workflow is incorrect " + runQuery.get(0)[0].toString() + " " + runQuery.get(0)[1].toString(),
                runQuery.get(0)[0].equals(WorkflowRunStatus.submitted.toString()) && runQuery.get(0)[1].equals(15));
        Assert.assertTrue("schedule workflow engine is incorrect " + runQuery.get(0)[2].toString(),
                runQuery.get(0)[2].equals(Engines.TYPES.oozie.toString()));
    }

    @Test
    public void testOozieSGEWorkflowEngine() {
        launchPlugin("--workflow-accession", "2860", "--host", FileTools.getLocalhost(null).hostname, "--workflow-engine",
                Engines.TYPES.oozie_sge.toString());

        String s = getOut();
        String firstWorkflowRun = getAndCheckSwid(s);

        BasicTestDatabaseCreator dbCreator = new BasicTestDatabaseCreator();
        List<Object[]> runQuery = dbCreator.runQuery(new ArrayListHandler(),
                "select r.status, r.workflow_id, r.workflow_engine from workflow_run r\n" + "WHERE \n" + "r.sw_accession = ?\n" + "; ",
                Integer.valueOf(firstWorkflowRun));
        Assert.assertTrue("schedule workflow is incorrect " + runQuery.get(0)[0].toString() + " " + runQuery.get(0)[1].toString(),
                runQuery.get(0)[0].equals(WorkflowRunStatus.submitted.toString()) && runQuery.get(0)[1].equals(15));
        Assert.assertTrue("schedule workflow engine is incorrect " + runQuery.get(0)[2].toString(),
                runQuery.get(0)[2].equals(Engines.TYPES.oozie_sge.toString()));
    }

    @Test
    public void testParentAccessions() {
        launchPlugin("--workflow-accession", "2860", "--host", FileTools.getLocalhost(null).hostname, "--parent-accessions", "4765,4789");

        String s = getOut();
        String firstWorkflowRun = getAndCheckSwid(s);

        BasicTestDatabaseCreator dbCreator = new BasicTestDatabaseCreator();
        List<Object[]> runQuery = dbCreator.runQuery(new ArrayListHandler(),
                "select r.status, r.workflow_id, r.ini_file from workflow_run r\n" + "WHERE \n" + "r.sw_accession = ?\n" + "; ",
                Integer.valueOf(firstWorkflowRun));
        Assert.assertTrue("schedule workflow is incorrect " + runQuery.get(0)[0].toString() + " " + runQuery.get(0)[1].toString(),
                runQuery.get(0)[0].equals(WorkflowRunStatus.submitted.toString()) && runQuery.get(0)[1].equals(15));
        String iniFile = runQuery.get(0)[2].toString();
        Map<String, String> iniMap = MapTools.iniString2Map(iniFile);
        testIniKey(iniMap, ReservedIniKeys.PARENT_DASH_ACCESSIONS.getKey(), "4765");
        testIniKey(iniMap, ReservedIniKeys.PARENT_DASH_ACCESSIONS.getKey(), "4789");
        testIniKey(iniMap, ReservedIniKeys.PARENT_ACCESSION.getKey(), "4765");
        testIniKey(iniMap, ReservedIniKeys.PARENT_ACCESSION.getKey(), "4789");
        testIniKey(iniMap, ReservedIniKeys.PARENT_UNDERSCORE_ACCESSIONS.getKey(), "4765");
        testIniKey(iniMap, ReservedIniKeys.PARENT_UNDERSCORE_ACCESSIONS.getKey(), "4789");
    }

    private void testIniKey(Map<String, String> iniMap, String key, String value) {
        Assert.assertTrue(key + " incorrect", iniMap.get(key).contains(value));
    }

    @Test
    public void testLinkWorkflowRunToParents() {
        launchPlugin("--workflow-accession", "2860", "--host", FileTools.getLocalhost(null).hostname, "--link-workflow-run-to-parents",
                "4765,4789");

        String s = getOut();
        String firstWorkflowRun = getAndCheckSwid(s);

        BasicTestDatabaseCreator dbCreator = new BasicTestDatabaseCreator();
        List<Object[]> runQuery = dbCreator.runQuery(new ArrayListHandler(),
                "select r.status, r.workflow_id, r.ini_file from workflow_run r\n" + "WHERE \n" + "r.sw_accession = ?\n" + "; ",
                Integer.valueOf(firstWorkflowRun));
        Assert.assertTrue("schedule workflow is incorrect " + runQuery.get(0)[0].toString() + " " + runQuery.get(0)[1].toString(),
                runQuery.get(0)[0].equals(WorkflowRunStatus.submitted.toString()) && runQuery.get(0)[1].equals(15));
        List<Object[]> runQuery2 = dbCreator
                .runQuery(
                        new ArrayListHandler(),
                        "select i.sw_accession from ius_workflow_runs ir join workflow_run r on ir.workflow_run_id=r.workflow_run_id join ius i on i.ius_id=ir.ius_id where r.sw_accession = ? order by sw_accession;",
                        Integer.valueOf(firstWorkflowRun));
        Assert.assertTrue("ius links are incorrect " + runQuery2.get(0)[0].toString() + " " + runQuery2.get(1)[0].toString(),
                runQuery2.get(0)[0].equals(4765) && runQuery2.get(1)[0].equals(4789));
    }

    @Test
    public void testInputFiles() {
        launchPlugin("--workflow-accession", "2860", "--host", FileTools.getLocalhost(null).hostname, "--input-files", "835,838");

        String s = getOut();
        String firstWorkflowRun = getAndCheckSwid(s);

        BasicTestDatabaseCreator dbCreator = new BasicTestDatabaseCreator();
        List<Object[]> runQuery = dbCreator.runQuery(new ArrayListHandler(),
                "select r.status, r.workflow_id, r.ini_file from workflow_run r\n" + "WHERE \n" + "r.sw_accession = ?\n" + "; ",
                Integer.valueOf(firstWorkflowRun));
        Assert.assertTrue("schedule workflow is incorrect " + runQuery.get(0)[0].toString() + " " + runQuery.get(0)[1].toString(),
                runQuery.get(0)[0].equals(WorkflowRunStatus.submitted.toString()) && runQuery.get(0)[1].equals(15));
        WorkflowRun workflowRun = metadata.getWorkflowRun(Integer.valueOf(firstWorkflowRun));
        Assert.assertTrue("input file links incorrect", workflowRun.getInputFileAccessions().contains(835)
                && workflowRun.getInputFileAccessions().contains(838));
    }

    @Test
    public void testDefaultIniFromBundle() {
        launchPlugin("--workflow-accession", "2861", "--host", FileTools.getLocalhost(null).hostname);

        String s = getOut();
        String firstWorkflowRun = getAndCheckSwid(s);

        BasicTestDatabaseCreator dbCreator = new BasicTestDatabaseCreator();
        List<Object[]> runQuery = dbCreator.runQuery(new ArrayListHandler(),
                "select r.status, r.workflow_id, r.ini_file from workflow_run r\n" + "WHERE \n" + "r.sw_accession = ?\n" + "; ",
                Integer.valueOf(firstWorkflowRun));
        Assert.assertTrue("schedule workflow is incorrect " + runQuery.get(0)[0].toString() + " " + runQuery.get(0)[1].toString(),
                runQuery.get(0)[0].equals(WorkflowRunStatus.submitted.toString()) && runQuery.get(0)[1].equals(16));
        WorkflowRun workflowRun = metadata.getWorkflowRun(Integer.valueOf(firstWorkflowRun));
        // check that default keys are present
        Map<String, String> baseMap = MapTools.iniString2Map(workflowRun.getIniFile());
        Assert.assertTrue("base map is missing variables",
                baseMap.containsKey("min_qual_score") && baseMap.containsKey("inputs_read_1") && baseMap.containsKey("inputs_read_2")
                        && baseMap.containsKey("cat") && baseMap.containsKey("output_prefix") && baseMap.containsKey("output_dir")
                        && baseMap.containsKey("min_percent_bases"));
        Assert.assertTrue("base map has incorrect values", baseMap.get("min_qual_score").equals("20") && baseMap.get("cat").equals("zcat")
                && baseMap.get("min_percent_bases").equals("90"));
    }

    @Test
    public void testLeftToRightOverrideByIniFiles() throws IOException {
        String[] iniFileContents1 = { "min_qual_score=30", "min_percent_bases=90", "cat=dog" };
        String[] iniFileContents2 = { "min_qual_score=40", "min_percent_bases=100" };
        String[] iniFileContents3 = { "min_qual_score=50" };
        File ini1 = File.createTempFile("ini", "ini");
        File ini2 = File.createTempFile("ini", "ini");
        File ini3 = File.createTempFile("ini", "ini");
        ini1.deleteOnExit();
        ini2.deleteOnExit();
        ini3.deleteOnExit();
        FileUtils.writeLines(ini1, Arrays.asList(iniFileContents1));
        FileUtils.writeLines(ini2, Arrays.asList(iniFileContents2));
        FileUtils.writeLines(ini3, Arrays.asList(iniFileContents3));

        launchPlugin("--workflow-accession", "2861", "--host", FileTools.getLocalhost(null).hostname, "--ini-files", ini1.getAbsolutePath()
                + "," + ini2.getAbsolutePath() + "," + ini3.getAbsolutePath());

        String s = getOut();
        String firstWorkflowRun = getAndCheckSwid(s);

        BasicTestDatabaseCreator dbCreator = new BasicTestDatabaseCreator();
        List<Object[]> runQuery = dbCreator.runQuery(new ArrayListHandler(),
                "select r.status, r.workflow_id, r.ini_file from workflow_run r\n" + "WHERE \n" + "r.sw_accession = ?\n" + "; ",
                Integer.valueOf(firstWorkflowRun));
        Assert.assertTrue("schedule workflow is incorrect " + runQuery.get(0)[0].toString() + " " + runQuery.get(0)[1].toString(),
                runQuery.get(0)[0].equals(WorkflowRunStatus.submitted.toString()) && runQuery.get(0)[1].equals(16));
        WorkflowRun workflowRun = metadata.getWorkflowRun(Integer.valueOf(firstWorkflowRun));
        // check that default keys are present
        Map<String, String> baseMap = MapTools.iniString2Map(workflowRun.getIniFile());
        Assert.assertTrue("overridden map is missing variables",
                baseMap.containsKey("min_qual_score") && baseMap.containsKey("inputs_read_1") && baseMap.containsKey("inputs_read_2")
                        && baseMap.containsKey("cat") && baseMap.containsKey("output_prefix") && baseMap.containsKey("output_dir")
                        && baseMap.containsKey("min_percent_bases"));
        Assert.assertTrue(
                "overridden map has incorrect values",
                baseMap.get("min_qual_score").equals("50") && baseMap.get("cat").equals("dog")
                        && baseMap.get("min_percent_bases").equals("100") && baseMap.get("output_dir").equals("results"));
    }

    @Test
    public void testLeftToRightOverrideByCLI() throws IOException {
        String[] iniFileContents1 = { "min_qual_score=30", "min_percent_bases=90", "cat=dog" };
        String[] iniFileContents2 = { "min_qual_score=40", "min_percent_bases=100" };
        String[] iniFileContents3 = { "min_qual_score=50" };
        File ini1 = File.createTempFile("ini", "ini");
        File ini2 = File.createTempFile("ini", "ini");
        File ini3 = File.createTempFile("ini", "ini");
        ini1.deleteOnExit();
        ini2.deleteOnExit();
        ini3.deleteOnExit();
        FileUtils.writeLines(ini1, Arrays.asList(iniFileContents1));
        FileUtils.writeLines(ini2, Arrays.asList(iniFileContents2));
        FileUtils.writeLines(ini3, Arrays.asList(iniFileContents3));

        launchPlugin("--workflow-accession", "2861", "--host", FileTools.getLocalhost(null).hostname, "--ini-files", ini1.getAbsolutePath()
                + "," + ini2.getAbsolutePath() + "," + ini3.getAbsolutePath(), "--", "--output_dir", "zebra", "--min_qual_score", "0");

        String s = getOut();
        String firstWorkflowRun = getAndCheckSwid(s);

        BasicTestDatabaseCreator dbCreator = new BasicTestDatabaseCreator();
        List<Object[]> runQuery = dbCreator.runQuery(new ArrayListHandler(),
                "select r.status, r.workflow_id, r.ini_file from workflow_run r\n" + "WHERE \n" + "r.sw_accession = ?\n" + "; ",
                Integer.valueOf(firstWorkflowRun));
        Assert.assertTrue("schedule workflow is incorrect " + runQuery.get(0)[0].toString() + " " + runQuery.get(0)[1].toString(),
                runQuery.get(0)[0].equals(WorkflowRunStatus.submitted.toString()) && runQuery.get(0)[1].equals(16));
        WorkflowRun workflowRun = metadata.getWorkflowRun(Integer.valueOf(firstWorkflowRun));
        // check that default keys are present
        Map<String, String> baseMap = MapTools.iniString2Map(workflowRun.getIniFile());
        Assert.assertTrue("overridden map is missing variables",
                baseMap.containsKey("min_qual_score") && baseMap.containsKey("inputs_read_1") && baseMap.containsKey("inputs_read_2")
                        && baseMap.containsKey("cat") && baseMap.containsKey("output_prefix") && baseMap.containsKey("output_dir")
                        && baseMap.containsKey("min_percent_bases"));
        Assert.assertTrue(
                "overridden map has incorrect values",
                baseMap.get("min_qual_score").equals("0") && baseMap.get("cat").equals("dog")
                        && baseMap.get("min_percent_bases").equals("100") && baseMap.get("output_dir").equals("zebra"));
    }

    @Test
    public void testMetadataOff() {
        launchPlugin("--workflow-accession", "2860", "--host", FileTools.getLocalhost(null).hostname, "--no-metadata");

        String s = getOut();
        String firstWorkflowRun = getAndCheckSwid(s);

        BasicTestDatabaseCreator dbCreator = new BasicTestDatabaseCreator();
        List<Object[]> runQuery = dbCreator.runQuery(new ArrayListHandler(),
                "select r.status, r.workflow_id, r.workflow_engine from workflow_run r\n" + "WHERE \n" + "r.sw_accession = ?\n" + "; ",
                Integer.valueOf(firstWorkflowRun));
        Assert.assertTrue("schedule workflow is incorrect " + runQuery.get(0)[0].toString() + " " + runQuery.get(0)[1].toString(),
                runQuery.get(0)[0].equals(WorkflowRunStatus.submitted.toString()) && runQuery.get(0)[1].equals(15));
        WorkflowRun workflowRun = metadata.getWorkflowRun(Integer.valueOf(firstWorkflowRun));
        // check that default keys are present
        Map<String, String> baseMap = MapTools.iniString2Map(workflowRun.getIniFile());
        Assert.assertTrue("base map is metadata", baseMap.containsKey("metadata"));
        Assert.assertTrue("base map has incorrect values", baseMap.get("metadata").equals("no-metadata"));
    }
}
