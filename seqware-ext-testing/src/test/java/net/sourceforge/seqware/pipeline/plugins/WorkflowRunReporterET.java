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

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * These tests support command-line tools found in the SeqWare User Tutorial, in this case, WorkflowRunReporter
 *
 * @author dyuen
 */
public class WorkflowRunReporterET {

    @BeforeClass
    public static void resetDatabase() {
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers();
    }

    @Test
    public void runWorkflowRunReporter() throws IOException {
        File createTempDir = Files.createTempDir();
        String randomString = UUID.randomUUID().toString();
        File testOutFile = new File(createTempDir, randomString + ".txt");
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter " + "-- --output-filename "
                + testOutFile.getName() + " --workflow-run-accession 6698";
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, createTempDir);
        Log.info(listOutput);
        File retrievedFile = new File(createTempDir, testOutFile.getName());
        Assert.assertTrue("output file does not exist", retrievedFile.exists());
        List<String> readLines = FileUtils.readLines(testOutFile);
        Assert.assertTrue("incorrect number of lines", readLines.size() == 2);
        long checksumCRC32 = FileUtils.checksumCRC32(testOutFile);
        Assert.assertTrue("incorrect output checksum" + checksumCRC32, checksumCRC32 == 255117433L);
    }

    @Test
    public void runInvalidParameters() throws IOException {
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter "
                + "-- --boogly parameters --workflow-run-accession 6698";
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.INVALIDARGUMENT, null);

    }

    @Test
    public void runInvalidIO() throws IOException {
        File createTempDir = Files.createTempDir();
        String randomString = UUID.randomUUID().toString();
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter " + "-- --output-filename "
                + createTempDir.getAbsolutePath() + " --workflow-run-accession 6698";
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.FILENOTREADABLE, null);
    }

    @Test
    public void runFirstDate() throws IOException {
        File createTempDir = Files.createTempDir();
        String randomString = UUID.randomUUID().toString();
        File testOutFile = new File(createTempDir, randomString + ".txt");
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter " + "-- --output-filename "
                + testOutFile.getName() + " --workflow-accession 2861 --time-period 2012-01-01 ";
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, createTempDir);
        Log.info(listOutput);
        File retrievedFile = new File(createTempDir, testOutFile.getName());
        Assert.assertTrue("output file does not exist", retrievedFile.exists());
        List<String> readLines = FileUtils.readLines(testOutFile);
        Assert.assertTrue("incorrect number of lines ", readLines.size() == 7);
        long checksumCRC32 = FileUtils.checksumCRC32(testOutFile);
        // former is for Java 7, latter is for Java 8, Looks like we didn't solve the sorting problem
        Assert.assertTrue("incorrect output checksum " + checksumCRC32 + " " + FileUtils.readFileToString(retrievedFile),
                checksumCRC32 == 3873030870L || checksumCRC32 == 2196935470L);
    }

    @Test
    public void runBothDates() throws IOException {
        File createTempDir = Files.createTempDir();
        String randomString = UUID.randomUUID().toString();
        File testOutFile = new File(createTempDir, randomString + ".txt");
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter " + "-- --output-filename "
                + testOutFile.getName() + " --workflow-accession 2861 --time-period 2012-01-01:2012-01-15 ";
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, createTempDir);
        Log.info(listOutput);
        File retrievedFile = new File(createTempDir, testOutFile.getName());
        Assert.assertTrue("output file does not exist", retrievedFile.exists());
        List<String> readLines = FileUtils.readLines(testOutFile);
        Assert.assertTrue("incorrect number of lines ", readLines.size() == 4);
        long checksumCRC32 = FileUtils.checksumCRC32(testOutFile);
        // former is for Java 7, latter is for Java 8, Looks like we didn't solve the sorting problem
        Assert.assertTrue("incorrect output checksum " + checksumCRC32 + " " + FileUtils.readFileToString(retrievedFile),
                checksumCRC32 == 562223107L || checksumCRC32 == 4072825873L);
    }

    @Test
    public void noWorkflowSpecified() throws IOException {
        // SEQWARE-1674
        File createTempDir = Files.createTempDir();
        String randomString = UUID.randomUUID().toString();
        File testOutFile = new File(createTempDir, randomString + ".txt");
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter " + "-- --output-filename "
                + testOutFile.getName() + " --time-period 2012-01-01:2012-01-15 ";
        ITUtility.runSeqWareJar(listCommand, ReturnValue.INVALIDPARAMETERS, createTempDir);
    }

    @Test
    public void invalidWorkflowAcession() throws IOException {
        File createTempDir = Files.createTempDir();
        String randomString = UUID.randomUUID().toString();
        File testOutFile = new File(createTempDir, randomString + ".txt");
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter " + "-- --output-filename "
                + testOutFile.getName() + " --workflow-accession 10000 ";
        ITUtility.runSeqWareJar(listCommand, ReturnValue.INVALIDPARAMETERS, createTempDir);
    }

    @Test
    public void invalidWorkflowRunAccession() throws IOException {
        File createTempDir = Files.createTempDir();
        String randomString = UUID.randomUUID().toString();
        File testOutFile = new File(createTempDir, randomString + ".txt");
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter " + "-- --output-filename "
                + testOutFile.getName() + " --workflow-run-accession 10000 ";
        ITUtility.runSeqWareJar(listCommand, ReturnValue.INVALIDPARAMETERS, createTempDir);
    }

    @Test
    public void invalidDateRange() throws IOException {
        File createTempDir = Files.createTempDir();
        String randomString = UUID.randomUUID().toString();
        File testOutFile = new File(createTempDir, randomString + ".txt");
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter " + "-- --output-filename "
                + testOutFile.getName() + " --workflow-run-accession 2861 --time-period 2013:01:01 ";
        ITUtility.runSeqWareJar(listCommand, ReturnValue.INVALIDPARAMETERS, createTempDir);
    }

    @Test
    public void emptyDateRange() throws IOException {
        File createTempDir = Files.createTempDir();
        String randomString = UUID.randomUUID().toString();
        File testOutFile = new File(createTempDir, randomString + ".txt");
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter " + "-- --output-filename "
                + testOutFile.getName() + " --workflow-accession 2861 --time-period 2014-01-01";
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, createTempDir);
        File retrievedFile = new File(createTempDir, testOutFile.getName());
        Assert.assertTrue("output file does not exist", retrievedFile.exists());
        List<String> readLines = FileUtils.readLines(testOutFile);
        Assert.assertTrue("incorrect number of lines ", readLines.size() == 1);
        long checksumCRC32 = FileUtils.checksumCRC32(testOutFile);
        Assert.assertTrue("incorrect output checksum " + checksumCRC32, checksumCRC32 == 1649363086L);
    }

    @Test
    public void stdOutAndstdErrSameTime() throws IOException {
        // SEQWARE-1527
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter "
                + "-- --wr-stderr --wr-stdout --workflow-run-accession 6698";
        ITUtility.runSeqWareJar(listCommand, ReturnValue.INVALIDPARAMETERS, null);
    }
}
