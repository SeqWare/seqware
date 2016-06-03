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
package net.sourceforge.seqware.pipeline.modules;

import com.google.common.base.Splitter;
import com.google.common.io.Files;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugins.ExtendedTestDatabaseCreator;
import net.sourceforge.seqware.pipeline.plugins.ITUtility;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;

/**
 * These tests support command-line tools found in the SeqWare User Tutorial, in this case, GenericCommandRunner
 * 
 * @author dyuen
 */
public class GenericCommandRunnerET {

    private final ExtendedTestDatabaseCreator dbCreator = new ExtendedTestDatabaseCreator();

    @BeforeClass
    public static void resetDatabase() {
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers();
    }

    @Test
    public void testGenericCommandRunner() throws IOException {
        File createTempDir = Files.createTempDir();

        Random generator = new Random();
        String random = String.valueOf(generator.nextInt());
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.ModuleRunner "
                + "-- --module net.sourceforge.seqware.pipeline.modules.GenericCommandRunner  "
                + "--  --gcr-algorithm test --gcr-command ls";
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, createTempDir);
        Splitter splitter = Splitter.on(System.getProperty("line.separator")).trimResults();
        for (String line : splitter.split(listOutput)) {
            if (line.equals("bash -lc ls")) {
                /** success, found the command */
                return;
            }
        }
        Assert.assertTrue("did not find command in output", false);
    }

    @Test
    public void testProvisionTwice() throws IOException {
        File createTempDir = com.google.common.io.Files.createTempDir();
        final Path tempFile = java.nio.file.Files.createTempFile("test", "test");
        tempFile.toFile().createNewFile();

        Random generator = new Random();
        String listCommand = "net.sourceforge.seqware.pipeline.runner.Runner " + "--metadata "
                + "--metadata-workflow-run-ancestor-accession " + "4419139 " + "--metadata-processing-accession-file "
                + createTempDir.getAbsolutePath() + "/s4419139_pfo_5_accession "
                + "--metadata-processing-accession-file-lock "
                + createTempDir.getAbsolutePath() + "/s4419139_pfo_5_accession.lock " + "--module "
                + "net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles " + "-- " + "--input-file-metadata "
                + "pfo::txt/plain::" + tempFile.toFile().getAbsolutePath() + " " + "--output-file "
                + "./seqware-results/CheckProvision_1.0-SNAPSHOT/19614578/dir1/output " + "--force-copy " + "--annotation-file "
                + createTempDir.getAbsolutePath() + "/generated-scripts/s4419139_pfo_5.annotations.tsv";

        Object[] runQuerya = dbCreator.runQuery(new ArrayHandler(), "select count(*) from processing");
        Object[] runQueryb = dbCreator.runQuery(new ArrayHandler(), "select count(*) from file");

        String listOutput = ITUtility.runSeqWareJarDirect(listCommand, ReturnValue.SUCCESS, createTempDir);
        // check the number of provision and file records
        Object[] runQuery1 = dbCreator.runQuery(new ArrayHandler(), "select count(*) from processing");
        Object[] runQuery2 = dbCreator.runQuery(new ArrayHandler(), "select count(*) from file");
        // the number of provision and file records should have gone up by one
        runQuerya[0] = (long)runQuerya[0] + 1;
        runQueryb[0] = (long)runQueryb[0] + 1;
        Assert.assertTrue("processing did not change" , Arrays.equals(runQuery1, runQuerya));
        Assert.assertTrue("processing did not change" , Arrays.equals(runQuery2, runQueryb));

        listOutput = ITUtility.runSeqWareJarDirect(listCommand, ReturnValue.SUCCESS, createTempDir);
        // the number of provision and file records should not have changed
        Object[] runQuery3 = dbCreator.runQuery(new ArrayHandler(), "select count(*) from processing");
        Object[] runQuery4 = dbCreator.runQuery(new ArrayHandler(), "select count(*) from file");
        Assert.assertTrue("processing changed" , Arrays.equals(runQuery1, runQuery3));
        Assert.assertTrue("processing changed" , Arrays.equals(runQuery2, runQuery4));
    }

    @Test
    public void testGCRStdout() throws IOException {
        File createTempDir = Files.createTempDir();

        Random generator = new Random();
        String random = String.valueOf(generator.nextInt());
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.ModuleRunner "
                + "-- --module net.sourceforge.seqware.pipeline.modules.GenericCommandRunner  "
                + "--  --gcr-algorithm test --gcr-command echo Hello World --gcr-stdout";
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, createTempDir);
        Splitter splitter = Splitter.on(System.getProperty("line.separator")).trimResults();
        for (String line : splitter.split(listOutput)) {
            if (line.equals("Hello World")) {
                /** success, found the expected output */
                return;
            }
        }

        Assert.assertTrue("did not find expected output in output", false);

    }

    @Test
    public void testGCRStderr() throws IOException {
        File createTempDir = Files.createTempDir();

        Random generator = new Random();
        String random = String.valueOf(generator.nextInt());
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.ModuleRunner "
                + "-- --module net.sourceforge.seqware.pipeline.modules.GenericCommandRunner  "
                + "--  --gcr-algorithm test --gcr-command ls /home/abcdef --gcr-stderr";
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.PROGRAMFAILED, createTempDir);
        Splitter splitter = Splitter.on(System.getProperty("line.separator")).trimResults();
        for (String line : splitter.split(listOutput)) {
            if (line.contains("cannot access")) {
                /** success, found the expected error output */
                return;
            }
        }

        Assert.assertTrue("did not find expected error in output", false);

    }

}
