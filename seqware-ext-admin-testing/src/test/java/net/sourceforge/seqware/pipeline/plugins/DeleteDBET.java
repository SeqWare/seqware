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

import io.seqware.common.model.WorkflowRunStatus;
import net.sourceforge.seqware.common.module.ReturnValue;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * These tests support command-line tools found in the SeqWare User Tutorial, in this case, DeleteDB
 * 
 * @author dyuen
 */
public class DeleteDBET {
    private final ExtendedTestDatabaseCreator dbCreator = new ExtendedTestDatabaseCreator();

    @BeforeClass
    public static void resetDatabase() {
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers();
    }

    /**
     * Test various forms of invalid parameters SEQWARE-1678
     * 
     * @throws java.io.IOException
     */
    @Test
    public void testInvalidParameters() throws IOException {
        // invalid value
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.deletion.DeletionDB "
                + "-- --ius 4789 --key funky_key --funky_change_value";
        ITUtility.runSeqWareJar(listCommand, ReturnValue.INVALIDARGUMENT, null);
        // key with no valid value
        listCommand = "-p net.sourceforge.seqware.pipeline.plugins.deletion.DeletionDB " + "-- --r 1000000";
        ITUtility.runSeqWareJar(listCommand, ReturnValue.INVALIDPARAMETERS, null);
    }

    @Test
    public void testNormalWorkflowRunDelete() throws IOException {
        unblockWorkflowRuns();
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.deletion.DeletionDB " + "-- --r 6698";
        String output = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        File keyFile = getAndCheckForKeyFile(output);
        Assert.assertTrue("key file is empty", keyFile.exists());
        listCommand = "-p net.sourceforge.seqware.pipeline.plugins.deletion.DeletionDB " + "-- --r 6698 -k " + keyFile.getAbsolutePath();
        ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        // double check that the workflow run has actually been deleted
        Object[] runQuery = dbCreator.runQuery(new ArrayHandler(), "SELECT * FROM workflow_run WHERE sw_accession=6698");
        Assert.assertTrue("workflow run not deleted", runQuery == null);
        // reset database when testing successful deletes
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers();
    }

    @Test
    public void testNormalLaneTargetWorkflowRunDelete() throws IOException {
        unblockWorkflowRuns();
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.deletion.DeletionDB " + "-- --r 4764";
        String output = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        File keyFile = getAndCheckForKeyFile(output);
        Assert.assertTrue("key file is empty", keyFile.exists());
        listCommand = "-p net.sourceforge.seqware.pipeline.plugins.deletion.DeletionDB " + "-- --r 4764 -k " + keyFile.getAbsolutePath();
        ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        // double check that the workflow run has actually been deleted
        Object[] runQuery = dbCreator.runQuery(new ArrayHandler(),
                "SELECT * FROM workflow_run WHERE sw_accession=6683 OR sw_accession = 6684");
        Assert.assertTrue("workflow run not deleted", runQuery == null);
        // reset database when testing successful deletes
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers();
    }

    @Test
    public void testNormalSequencerRunTargetWorkflowRunDelete() throws IOException {
        unblockWorkflowRuns();
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.deletion.DeletionDB " + "-- --r 4715";
        String output = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        File keyFile = getAndCheckForKeyFile(output);
        Assert.assertTrue("key file is empty", keyFile.exists());
        listCommand = "-p net.sourceforge.seqware.pipeline.plugins.deletion.DeletionDB " + "-- --r 4715 -k " + keyFile.getAbsolutePath();
        ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        // double check that the workflow run has actually been deleted
        Object[] runQuery = dbCreator
                .runQuery(
                        new ArrayHandler(),
                        "SELECT * FROM workflow_run WHERE sw_accession=872 OR sw_accession = 882 OR sw_accession = 5657 OR sw_accession = 6683 OR sw_accession = 6684");
        Assert.assertTrue("workflow runs not deleted", runQuery == null);
        runQuery = dbCreator
                .runQuery(new ArrayHandler(),
                        "SELECT * FROM file WHERE sw_accession=881 OR sw_accession = 1963 OR sw_accession = 1978 OR sw_accession = 2139 OR sw_accession = 2160");
        Assert.assertTrue("files not deleted", runQuery == null);
        // reset database when testing successful deletes
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers();
    }

    @Test
    public void testBlockedWorkflowRunDelete() throws IOException {
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.deletion.DeletionDB " + "-- --r 4715";
        String output = ITUtility.runSeqWareJar(listCommand, ReturnValue.INVALIDPARAMETERS, null);
    }

    @Test
    public void testFailureDueToEmptyKeyFile() throws IOException {
        File createTempFile = File.createTempFile("deletion", "keyFile");
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.deletion.DeletionDB " + "-- --r 6691 -k " + createTempFile;
        ITUtility.runSeqWareJar(listCommand, ReturnValue.INVALIDFILE, null);
    }

    @Test
    public void testFailureDueToCorruptKeyFile() throws IOException {
        File createTempFile = File.createTempFile("deletion", "keyFile");
        FileUtils.write(createTempFile, "crap data", StandardCharsets.UTF_8);
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.deletion.DeletionDB " + "-- --r 6691 -k " + createTempFile;
        ITUtility.runSeqWareJar(listCommand, ReturnValue.INVALIDFILE, null);
    }

    @Test
    public void testFailureDueToTooMuchContentInKeyFile() throws IOException {
        String path = DeleteDBET.class.getResource("tooBig.keyFile").getPath();
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.deletion.DeletionDB " + "-- --r 2862 --k " + path;
        ITUtility.runSeqWareJar(listCommand, ReturnValue.INVALIDPARAMETERS, null);
        // double-check that content was not deleted
        Object[] runQuery = dbCreator.runQuery(new ArrayHandler(), "SELECT * FROM workflow_run WHERE sw_accession=2862");
        Assert.assertTrue("workflow runs deleted despite invalid keyfile", runQuery != null);
    }

    @Test
    public void testFailureDueToTooLittleContentInKeyFile() throws IOException {
        String path = DeleteDBET.class.getResource("tooBig.keyFile").getPath();
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.deletion.DeletionDB " + "-- --r 2862 --k " + path;
        ITUtility.runSeqWareJar(listCommand, ReturnValue.INVALIDPARAMETERS, null);
        // double-check that content was not deleted
        Object[] runQuery = dbCreator.runQuery(new ArrayHandler(), "SELECT * FROM workflow_run WHERE sw_accession=2862");
        Assert.assertTrue("workflow runs deleted despite invalid keyfile", runQuery != null);
    }

    @Test
    public void checkThatCascadeIsDirectedToChildWorkflowRuns() throws IOException {
        // 862 cascades to 872,882,5657,etc.
        // deletion of 872 should not affect 862 but should affect following workflow runs
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.deletion.DeletionDB " + "-- --r 872";
        String output = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        File keyFile = getAndCheckForKeyFile(output);
        Assert.assertTrue("key file is empty", keyFile.exists());
        listCommand = "-p net.sourceforge.seqware.pipeline.plugins.deletion.DeletionDB " + "-- --r 872 -k " + keyFile.getAbsolutePath();
        ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        // double check that the workflow run has actually been deleted
        Object[] runQuery = dbCreator.runQuery(new ArrayHandler(),
                "SELECT * FROM workflow_run WHERE sw_accession=872 OR sw_accession = 882 OR sw_accession = 5657");
        Assert.assertTrue("workflow runs not deleted", runQuery == null);
        runQuery = dbCreator.runQuery(new ArrayHandler(), "SELECT * FROM workflow_run WHERE sw_accession=862");
        Assert.assertTrue("parent workflow run was deleted", runQuery != null && runQuery.length > 0);
        // reset database when testing successful deletes
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers();
    }

    @Test
    public void testNormalWorkflowRunDeleteWithExplicitFile() throws IOException {
        unblockWorkflowRuns();
        File createTempFile = File.createTempFile("deletion", "keyFile");
        createTempFile.delete(); // ensure file does not exist
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.deletion.DeletionDB " + "-- --r 6691 --out " + createTempFile;
        String output = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        File keyFile = getAndCheckForKeyFile(output);
        Assert.assertTrue("key file is empty or is not the correct size", keyFile.exists());
        listCommand = "-p net.sourceforge.seqware.pipeline.plugins.deletion.DeletionDB " + "-- --r 6691 -k " + keyFile.getAbsolutePath();
        ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        // double check that the workflow run has actually been deleted
        Object[] runQuery = dbCreator.runQuery(new ArrayHandler(), "SELECT * FROM workflow_run WHERE sw_accession=6691");
        Assert.assertTrue("workflow run not deleted", runQuery == null);
        // reset database when testing successful deletes
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers();
    }

    protected static Pattern keyFilePattern = Pattern.compile("Key File written to (.*)");

    private static File getAndCheckForKeyFile(String s) throws NumberFormatException {
        Matcher match = keyFilePattern.matcher(s);
        Assert.assertTrue("The file was not found in output.", match.find());
        String swid = match.group(1);
        Assert.assertFalse("The Filename was empty", swid.trim().isEmpty());
        File file = new File(swid);
        return file;
    }

    /**
     * For testing, unblock all workflow runs
     */
    private void unblockWorkflowRuns() {
        dbCreator.runUpdate("update workflow_run set status='" + WorkflowRunStatus.completed.name() + "'");
    }
}
