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
package net.sourceforge.seqware.pipeline.regression_testing;

import com.google.common.io.Files;
import io.seqware.cli.Main;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.plugins.ExtendedTestDatabaseCreator;
import net.sourceforge.seqware.pipeline.plugins.ITUtility;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * This tests a workflow that skips files (and should not create File entries in the database).
 * 
 * @author dyuen
 */
public class SkippingFilesWorkflowLT {

    @Test
    public void runSEQWARE2039() throws IOException {
        // here we test that the first error is properly propagated into the database and reported
        final ExtendedTestDatabaseCreator dbCreator = new ExtendedTestDatabaseCreator();
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers(false);

        Main main = new Main();
        String SEQWARE_VERSION = main.getClass().getPackage().getImplementationVersion();
        Assert.assertTrue("unable to detect seqware version", SEQWARE_VERSION != null);
        Log.info("SeqWare version detected as: " + SEQWARE_VERSION);

        File tempDir = Files.createTempDir();

        // create the workflow
        String command = "mvn archetype:generate -DarchetypeCatalog=local -Dpackage=io.seqware"
                + " -DgupId=io.seqware -DarchetypeArtifactId=seqware-archetype-java-workflow "
                + "-Dversion=1.0-SNAPSHOT -DarchetypeGroupId=com.github.seqware -DartifactId=Skipping "
                + "-Dworkflow-name=Skipping -B";
        String genOutput = ITUtility.runArbitraryCommand(command, 0, tempDir);
        Log.info(genOutput);

        // Replace contents of WorkflowClient from both workflows with code from tutorial
        String workflowJavaPath = SkippingFilesWorkflowLT.class.getResource("seqware2039.template").getPath();
        // determine existing file paths
        File targetJavaPath = new File(tempDir, "Skipping/src/main/java/io/seqware/SkippingWorkflow.java");

        // replace workflow file
        Files.copy(new File(workflowJavaPath), targetJavaPath);

        // store number of file records and processing records
        long numProcessing = (Long)dbCreator.runQuery(new ArrayHandler(), "SELECT count(*) from Processing where algorithm = 'ProvisionFiles'")[0];
        long numFiles = (Long)dbCreator.runQuery(new ArrayHandler(), "SELECT count(*) from File")[0];


        // rebuild bundles
        command = "mvn clean install";
        genOutput = ITUtility.runArbitraryCommand(command, 0, new File(tempDir, "Skipping"));
        Log.info(genOutput);
        // run bundle and don't error
        String listCommand = " bundle launch --dir Skipping/target/Workflow_Bundle_Skipping_1.0-SNAPSHOT_SeqWare_"
                + SEQWARE_VERSION + "/";
        String listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, tempDir);
        System.out.println(listOutput);
        Assert.assertTrue("Launch did not succeed", listOutput.contains("completed"));

        // processing should go up by one more than files
        long numProcessing2 = (Long)dbCreator.runQuery(new ArrayHandler(), "SELECT count(*) from Processing where algorithm = 'ProvisionFiles'")[0];
        long numFiles2 = (Long)dbCreator.runQuery(new ArrayHandler(), "SELECT count(*) from File")[0];
        Assert.assertTrue("number of file records is unexpected, " + (numFiles2 - numFiles), numFiles2 - numFiles == 2);
        Assert.assertTrue("number of processing records is unexpected, " +  (numProcessing2 - numProcessing), numProcessing2 - numProcessing == 4);

        // clean-up on the way out
        tempDir.deleteOnExit();
    }

}
