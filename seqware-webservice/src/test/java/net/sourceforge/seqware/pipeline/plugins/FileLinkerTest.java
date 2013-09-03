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

import java.util.List;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.util.testtools.BasicTestDatabaseCreator;
import static net.sourceforge.seqware.pipeline.plugins.PluginTest.metadata;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.lang.StringUtils;
import org.junit.*;

/**
 * Runs the tests for the FileLinker
 * @author dyuen
 */
public class FileLinkerTest extends ExtendedPluginTest {

    @BeforeClass
    public static void beforeClass(){
        BasicTestDatabaseCreator.resetDatabaseWithUsers();
    }
    
    @Before
    @Override
    public void setUp() {
        instance = new FileLinker();
        super.setUp();
    }
    
    public FileLinkerTest() {
    }

    @Test
    public void testFileLinkerNormal() {
        String path = FileLinkerTest.class.getResource("file_linker_test.txt").getPath();

        launchPlugin("--workflow-accession", "4", "--file-list-file", path);

        String s = getOut();
        String firstWorkflowRun = getAndCheckSwid(s);

        BasicTestDatabaseCreator dbCreator = new BasicTestDatabaseCreator();
        List<Object[]> runQuery = dbCreator.runQuery(new ArrayListHandler(), "select f.file_path, f.meta_type, i.sw_accession from workflow_run r, processing p, processing_files pf, file f, processing_ius pi, ius i\n"
                + "WHERE \n"
                + "r.sw_accession = ?\n"
                + "AND p.workflow_run_id = r.workflow_run_id\n"
                + "AND p.processing_id = pf.processing_id\n"
                + "AND pf.file_id = f.file_id\n"
                + "AND p.processing_id = pi.processing_id \n"
                + "AND pi.ius_id = i.ius_id\n"
                + "ORDER BY f.sw_accession\n"
                + "; ", Integer.valueOf(firstWorkflowRun));
        Assert.assertTrue("first file values were incorrect", runQuery.get(0)[0].equals("funky_file1.gz") && runQuery.get(0)[1].equals("txt"));
        Assert.assertTrue("second file values were incorrect", runQuery.get(1)[0].equals("funky_file2.gz") && runQuery.get(1)[1].equals("txt"));
        Assert.assertTrue("ius links  were correct", runQuery.get(0)[2].equals(4765) && runQuery.get(1)[2].equals(4765));
        // check that we can get them back via metadata methods as well
        WorkflowRun workflowRun = metadata.getWorkflowRunWithWorkflow(firstWorkflowRun);
        Assert.assertTrue("could not retrieve optional fields via metadata", workflowRun.getSwAccession().equals(Integer.valueOf(firstWorkflowRun)));
        Assert.assertTrue("parent workflow incorrect", workflowRun.getWorkflow().getSwAccession() == 4);
        String workflowRunReport = metadata.getWorkflowRunReport(Integer.valueOf(firstWorkflowRun));
        // look for files using the gross metadata way
        Assert.assertTrue("files could not be found using the metadata workflow run report", 
                workflowRunReport.contains("funky_file1.gz") && workflowRunReport.contains("funky_file2.gz"));
        s = s.split("\n")[1];
        int secondWorkflowRun = Integer.valueOf(getAndCheckSwid(s));
        workflowRunReport = metadata.getWorkflowRunReport(secondWorkflowRun);
        Assert.assertTrue("second set of files could not be found using the metadata workflow run report", 
                workflowRunReport.contains("abc1.gz") && workflowRunReport.contains("abc2.gz"));
    }
    
    @Test
    public void testFileLinkerComma() {
        String path = FileLinkerTest.class.getResource("file_linker_test_comma.txt").getPath();

        launchPlugin("--workflow-accession", "4", "--file-list-file", path, "--csv-separator",",");

        String s = getOut();
        int firstWorkflowRun = Integer.valueOf(getAndCheckSwid(s));

        String workflowRunReport = metadata.getWorkflowRunReport(firstWorkflowRun);
        // look for files using the gross metadata way
        Assert.assertTrue("files could not be found using the metadata workflow run report", 
                workflowRunReport.contains("cfunky_file1.gz") && workflowRunReport.contains("cfunky_file2.gz"));
        s = s.split("\n")[1];
        int secondWorkflowRun = Integer.valueOf(getAndCheckSwid(s));
        workflowRunReport = metadata.getWorkflowRunReport(secondWorkflowRun);
        Assert.assertTrue("second set of files could not be found using the metadata workflow run report", 
                workflowRunReport.contains("cabc1.gz") && workflowRunReport.contains("cabc2.gz"));
    }
    
    @Test
    public void checkRepeatedFileImportFail(){
        String path = FileLinkerTest.class.getResource("file_linker_test2.txt").getPath();
        launchPlugin("--workflow-accession", "4", "--file-list-file", path);
        launchPlugin("--workflow-accession", "4", "--file-list-file", path);
        String s = getOut();
        Assert.assertTrue("should be no file imports possible", s.contains("SWID") && StringUtils.countMatches(s, "Ignored file") == 4);
    }
}
