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
package net.sourceforge.seqware.pipeline.decider_tutorial;

import com.google.common.io.Files;
import io.seqware.cli.Main;
import java.io.File;
import java.io.IOException;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.plugins.ExtendedTestDatabaseCreator;
import net.sourceforge.seqware.pipeline.plugins.ITUtility;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * These tests support the tutorial for BasicDeciders
 *
 * Causes issues with Travis-CI?
 * 
 * @author dyuen
 */
public class BasicDeciderTutorialET {

    @BeforeClass
    public static void resetDatabase() {
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers(false);
    }

    @Test
    @Ignore("see https://github.com/SeqWare/seqware/issues/324")
    public void runThroughTutorial() throws IOException {
        // create some top level metadata
        Main main = new Main();
        String SEQWARE_VERSION = main.getClass().getPackage().getImplementationVersion();
        Assert.assertTrue("unable to detect seqware version", SEQWARE_VERSION != null);
        Log.info("SeqWare version detected as: " + SEQWARE_VERSION);

        String listCommand = " create study --title 'Study1' --description 'This is a test description' --accession 'InternalID123' --center-name 'SeqWare' --center-project-name 'SeqWare Test Project' --study-type 4";
        String listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, null);
        Assert.assertTrue("Study not created", listOutput.contains("Created study with SWID"));

        listCommand = " create experiment --title 'New Test Experiment' --description 'This is a test description' --platform-id 26 --study-accession 1";
        listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, null);
        Assert.assertTrue("Experiment not created", listOutput.contains("Created experiment with SWID"));

        listCommand = " create sample --title 'New Test Sample' --description 'This is a test description' --organism-id 26 --experiment-accession 2";
        listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, null);
        Assert.assertTrue("Sample not created", listOutput.contains("Created sample with SWID"));

        // make an arbitrary datafile for input
        File tempDir = Files.createTempDir();
        File tempFile = File.createTempFile("input", "txt", tempDir);

        // create some more top level metadata in order to link up the input file

        listCommand = " create sequencer-run --description description --file-path file_path --name name --paired-end paired_end --platform-accession 26  --skip false";
        listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, null);
        Assert.assertTrue("Sequencer run not created", listOutput.contains("Created sequencer run with SWID"));

        listCommand = " create lane --sequencer-run-accession 4 --study-type-accession 4 --cycle-descriptor cycle_descriptor --description description --lane-number 1 --library-selection-accession 25 --library-source-accession 5 --library-strategy-accession 21 --name name --skip false";
        listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, null);
        Assert.assertTrue("Lane not created", listOutput.contains("Created lane with SWID"));

        listCommand = " create ius --barcode barcode --description description --lane-accession 5 --name name --sample-accession  3 --skip false";
        listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, null);
        Assert.assertTrue("IUS not created", listOutput.contains("Created IUS with SWID"));

        // create workflow and check its accession
        listCommand = " create workflow --name FileImport --version 1.0 --description description";
        listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, null);
        Assert.assertTrue("Workflow with accession 7 not created" + listOutput,
                listOutput.contains("Created workflow 'FileImport' version 1.0 with SWID: 7"));

        listCommand = " create workflow-run  --workflow-accession 7 --file imported_file::text/plain::" + tempFile.getAbsolutePath()
                + " --parent-accession 5 --parent-accession 6";
        listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, null);
        Assert.assertTrue("Workflow run not created", listOutput.contains("Created workflow run with SWID"));
        Assert.assertTrue("Processing not created", listOutput.contains("Created processing with SWID"));

        // create the two workflows
        String command = "mvn archetype:generate -DarchetypeCatalog=local -Dpackage=com.github.seqware -DgroupId=com.github.seqware -DarchetypeArtifactId="
                + "seqware-archetype-java-workflow"
                + " -Dversion=1.0-SNAPSHOT -DarchetypeGroupId=com.github.seqware -DartifactId="
                + "Tar"
                + " -Dworkflow-name=Tar -B -Dgoals=install";
        String genOutput = ITUtility.runArbitraryCommand(command, 0, tempDir);
        Log.info(genOutput);
        command = "mvn archetype:generate -DarchetypeCatalog=local -Dpackage=com.github.seqware -DgroupId=com.github.seqware -DarchetypeArtifactId="
                + "seqware-archetype-java-workflow"
                + " -Dversion=1.0-SNAPSHOT -DarchetypeGroupId=com.github.seqware -DartifactId="
                + "GZ"
                + " -Dworkflow-name=GZ -B -Dgoals=install";
        genOutput = ITUtility.runArbitraryCommand(command, 0, tempDir);
        Log.info(genOutput);

        // Replace contents of WorkflowClient from both workflows with code from tutorial
        String tarTemplatePath = BasicDeciderTutorialET.class.getResource("TarWorkflow.template").getPath();
        String gzTemplatePath = BasicDeciderTutorialET.class.getResource("GZWorkflow.template").getPath();
        String workflowIniTemplatePath = BasicDeciderTutorialET.class.getResource("workflowini.template").getPath();
        // determine existing file paths
        File tarTarget = new File(tempDir, "Tar/src/main/java/com/github/seqware/TarWorkflow.java");
        File gzTarget = new File(tempDir, "GZ/src/main/java/com/github/seqware/GZWorkflow.java");
        File iniTarget1 = new File(tempDir, "Tar/workflow/config/TarWorkflow.ini");
        File iniTarget2 = new File(tempDir, "GZ/workflow/config/TGZWorkflow.ini");

        // replace tutorial files
        Files.copy(new File(tarTemplatePath), tarTarget);
        Files.copy(new File(gzTemplatePath), gzTarget);
        Files.copy(new File(workflowIniTemplatePath), iniTarget1);
        Files.copy(new File(workflowIniTemplatePath), iniTarget2);
        // rebuild bundles
        command = "mvn clean install";
        genOutput = ITUtility.runArbitraryCommand(command, 0, new File(tempDir, "Tar"));
        Log.info(genOutput);
        genOutput = ITUtility.runArbitraryCommand(command, 0, new File(tempDir, "GZ"));
        Log.info(genOutput);
        // package bundles
        listCommand = " bundle package --dir Tar/target/Workflow_Bundle_Tar_1.0-SNAPSHOT_SeqWare_" + SEQWARE_VERSION + "/";
        listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, tempDir);
        Assert.assertTrue("Bundle not packaged", listOutput.contains("Bundle has been packaged to"));
        listCommand = " bundle package --dir GZ/target/Workflow_Bundle_GZ_1.0-SNAPSHOT_SeqWare_" + SEQWARE_VERSION + "/";
        listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, tempDir);
        Assert.assertTrue("Bundle not packaged", listOutput.contains("Bundle has been packaged to"));
        // install bundles
        listCommand = " bundle install --zip Workflow_Bundle_Tar_1.0-SNAPSHOT_SeqWare_" + SEQWARE_VERSION + ".zip ";
        listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, tempDir);
        Assert.assertTrue("Bundle not installed", listOutput.contains("Bundle Has Been Installed"));
        Assert.assertTrue("Accession not 13", listOutput.contains("13"));

        listCommand = " bundle install --zip Workflow_Bundle_GZ_1.0-SNAPSHOT_SeqWare_" + SEQWARE_VERSION + ".zip";
        listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, tempDir);
        Assert.assertTrue("Bundle not installed", listOutput.contains("Bundle Has Been Installed"));
        Assert.assertTrue("Accession not 14", listOutput.contains("14"));

        // refresh the files report and schedule the first workflow
        listCommand = " files refresh";
        listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, null);
        Assert.assertTrue("Files report not refreshed", listOutput.contains("Triggered provenance report"));
        // assumes that workflow accession is 13
        listCommand = " -p net.sourceforge.seqware.pipeline.deciders.BasicDecider -- --all --meta-types text/plain --wf-accession 13";
        listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        Assert.assertTrue("Decider didn't schedule a workflow run", listOutput.contains("Created workflow run with SWID: 15"));

        // refresh the files report and schedule the first workflow
        listCommand = " workflow-run launch-scheduled ";
        listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, null);
        Assert.assertTrue("Workflow run not launched", listOutput.contains("Submitted Oozie job"));
        // wait for the workflow to finish, assumes that the accession is 15 from the decider
        listCommand = " workflow-run watch --accession 15";
        ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, null);

        listCommand = " workflow-run propagate-statuses";
        listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, null);
        Assert.assertTrue("Statuses not refreshed", listOutput.contains("Propagated workflow engine statuses"));

        // refresh the files report and schedule the second workflow
        listCommand = " files refresh";
        listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, null);
        Assert.assertTrue("Files report not refreshed", listOutput.contains("Triggered provenance report"));
        // assumes that workflow accession is 14
        listCommand = " -p net.sourceforge.seqware.pipeline.deciders.BasicDecider -- --all --meta-types application/x-tar --wf-accession 14";
        listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        Assert.assertTrue("Decider didn't schedule a workflow run", listOutput.contains("Created workflow run with SWID: 21"));

        // refresh the files report and schedule the first workflow
        listCommand = " workflow-run launch-scheduled ";
        listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, null);
        Assert.assertTrue("Workflow run not launched", listOutput.contains("Submitted Oozie job"));
        // wait for the workflow to finish, assumes that the accession is 21 from the decider
        listCommand = " workflow-run watch --accession 21";
        ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, null);

        // clean-up on the way out
        tempFile.deleteOnExit();
        tempDir.deleteOnExit();
    }
}
