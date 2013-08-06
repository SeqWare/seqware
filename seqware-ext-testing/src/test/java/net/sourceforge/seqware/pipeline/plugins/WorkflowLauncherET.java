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

import java.io.File;
import java.io.IOException;
import junit.framework.Assert;
import net.sourceforge.seqware.common.module.ReturnValue;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * These tests support command-line tools in this case, WorkflowLauncher
 *
 * @author dyuen
 */
public class WorkflowLauncherET {
    
    private ExtendedTestDatabaseCreator dbCreator = new ExtendedTestDatabaseCreator();

    @BeforeClass
    public static void resetDatabase() {
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers();
    }

    @Test
    public void runInvalidParameters() throws IOException {
        // this should not NullPointerException SEQWARE-1646
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher "
                + "-- --no-metadata --provisioned-bundle-dir /random/funky/dir --workflow Test --version 1.0-SNAPSHOT --ini-files /.mounts/labs/PDE/private/mtaschuk/tmp/Test/target/Workflow_Bundle_Test_1.0-SNAPSHOT_SeqWare_0.13.6.6/Workflow_Bundle_Test/1.0-SNAPSHOT/config/workflow.ini --metadata-output-dir /u/mtaschuk --metadata-file-output-prefix TestTest";
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.INVALIDARGUMENT, null);
        Assert.assertTrue("output contains exception", !listOutput.contains("Exception"));

        // this should not NullPointerException SEQWARE-1516
        listCommand = "-p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher "
                + "-- --wa 1 --test ";
        listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.INVALIDARGUMENT, null);
        Assert.assertTrue("output contains exception", !listOutput.contains("Exception"));
    }

    @Test
    public void scheduleWithOneInputFile() throws IOException {
        File workflowIni = File.createTempFile("workflow", "ini");
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher "
                + "-- --workflow-accession 6594 --schedule --ini-file "+workflowIni.getAbsolutePath()+" --host seqware --input-files 835";
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        Assert.assertTrue("output contains exception", !listOutput.contains("Exception"));
        String extractValueFrom = ITUtility.extractValueFrom(listOutput, "WORKFLOW_RUN ACCESSION:");
        int wr_accession = Integer.valueOf(extractValueFrom);
       
        Object[] runQuery = dbCreator.runQuery(new ArrayHandler(), "select count(*) from workflow_run r, workflow_run_input_files j WHERE r.workflow_run_id=j.workflow_run_id AND r.sw_accession=?", wr_accession);
        Assert.assertTrue("number of added parameters incorrect", runQuery.length == 1 && (Long)runQuery[0] == 1L);
    }
    
    @Test
    public void scheduleWithManyInputFiles() throws IOException {
        File workflowIni = File.createTempFile("workflow", "ini");
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher "
                + "-- --workflow-accession 6594 --schedule --ini-file "+workflowIni.getAbsolutePath()+" --host seqware --input-files 835,838,866,867,870";
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        Assert.assertTrue("output contains exception", !listOutput.contains("Exception"));
        String extractValueFrom = ITUtility.extractValueFrom(listOutput, "WORKFLOW_RUN ACCESSION:");
        int wr_accession = Integer.valueOf(extractValueFrom);
       
        Object[] runQuery = dbCreator.runQuery(new ArrayHandler(), "select count(*) from workflow_run r, workflow_run_input_files j WHERE r.workflow_run_id=j.workflow_run_id AND r.sw_accession=?", wr_accession);
        Assert.assertTrue("number of added parameters incorrect", runQuery.length == 1 && (Long)runQuery[0] == 5L);
    }

    @Test
    public void scheduleWithInvalidInputFiles() throws IOException {
        File workflowIni = File.createTempFile("workflow", "ini");
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher "
                + "-- --workflow-accession 6594 --schedule --ini-file "+workflowIni.getAbsolutePath()+" --host seqware --input-files 835,foobar";
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.INVALIDARGUMENT, null);
        Assert.assertTrue("output contains exception", !listOutput.contains("Exception"));
    }

    @Test
    public void scheduleWithNonExistentInputFiles() throws IOException {
        File workflowIni = File.createTempFile("workflow", "ini");
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher "
                + "-- --workflow-accession 6594 --schedule --ini-file "+workflowIni.getAbsolutePath()+" --host seqware --input-files 10000,11000";
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.INVALIDARGUMENT, null);
        Assert.assertTrue("output contains exception", !listOutput.contains("Exception"));
    }
}
