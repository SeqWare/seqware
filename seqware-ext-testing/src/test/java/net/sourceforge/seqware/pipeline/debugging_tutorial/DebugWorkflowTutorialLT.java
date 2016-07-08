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
package net.sourceforge.seqware.pipeline.debugging_tutorial;

import com.google.common.io.Files;
import io.seqware.cli.Main;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.plugins.ExtendedTestDatabaseCreator;
import net.sourceforge.seqware.pipeline.plugins.ITUtility;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * These tests support the tutorial for debugging workflows.
 *
 * We will verify that error reporting in the WorkflowLauncher and stderr come back as expected.
 *
 * @author dyuen
 */
public class DebugWorkflowTutorialLT {

    @Test
    public void runThroughFirstFailAtLaunchTutorial() throws IOException {
        // here we test that the first error is properly propagated into the database and reported
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers(false);

        Main main = new Main();
        String SEQWARE_VERSION = main.getClass().getPackage().getImplementationVersion();
        Assert.assertTrue("unable to detect seqware version", SEQWARE_VERSION != null);
        Log.info("SeqWare version detected as: " + SEQWARE_VERSION);

        File tempDir = Files.createTempDir();

        // create the workflow
        String command = "mvn archetype:generate -DarchetypeCatalog=local -Dpackage=com.github.seqware"
                + " -DgupId=com.github.seqware -DarchetypeArtifactId=seqware-archetype-java-workflow "
                + "-Dversion=1.0-SNAPSHOT -DarchetypeGroupId=com.github.seqware -DartifactId=BuggyWorkflow "
                + "-Dworkflow-name=BuggyWorkflow -B";
        String genOutput = ITUtility.runArbitraryCommand(command, 0, tempDir);
        Log.info(genOutput);

        // Replace contents of WorkflowClient from both workflows with code from tutorial
        String tarTemplatePath = DebugWorkflowTutorialLT.class.getResource("FailLaunch.template").getPath();
        // determine existing file paths
        File tarTarget = new File(tempDir, "BuggyWorkflow/src/main/java/com/github/seqware/BuggyWorkflowWorkflow.java");

        // replace tutorial files
        Files.copy(new File(tarTemplatePath), tarTarget);

        // rebuild bundles
        command = "mvn clean install";
        genOutput = ITUtility.runArbitraryCommand(command, 0, new File(tempDir, "BuggyWorkflow"));
        Log.info(genOutput);
        // package bundles
        String listCommand = " bundle launch --dir BuggyWorkflow/target/Workflow_Bundle_BuggyWorkflow_1.0-SNAPSHOT_SeqWare_"
                + SEQWARE_VERSION + "/";
        String listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.RUNNERERR, tempDir);
        Assert.assertTrue("Launch did not fail as expected", listOutput.contains("java.lang.ArithmeticException: / by zero"));

        // clean-up on the way out
        tempDir.deleteOnExit();
    }

    @Test
    public void runThroughFirstFailAtRuntimeTutorial() throws IOException {
        // here we test that the first error is properly propagated into the database and reported
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers(false);

        Main main = new Main();
        String SEQWARE_VERSION = main.getClass().getPackage().getImplementationVersion();
        Assert.assertTrue("unable to detect seqware version", SEQWARE_VERSION != null);
        Log.info("SeqWare version detected as: " + SEQWARE_VERSION);

        File tempDir = Files.createTempDir();

        // create the workflow
        String command = "mvn archetype:generate -DarchetypeCatalog=local -Dpackage=com.github.seqware"
                + " -DgupId=com.github.seqware -DarchetypeArtifactId=seqware-archetype-java-workflow "
                + "-Dversion=1.0-SNAPSHOT -DarchetypeGroupId=com.github.seqware -DartifactId=BuggyWorkflow "
                + "-Dworkflow-name=BuggyWorkflow -B";
        String genOutput = ITUtility.runArbitraryCommand(command, 0, tempDir);
        Log.info(genOutput);

        // Replace contents of WorkflowClient from both workflows with code from tutorial
        String tarTemplatePath = DebugWorkflowTutorialLT.class.getResource("FailRun.template").getPath();
        // determine existing file paths
        File tarTarget = new File(tempDir, "BuggyWorkflow/src/main/java/com/github/seqware/BuggyWorkflowWorkflow.java");

        // replace tutorial files
        Files.copy(new File(tarTemplatePath), tarTarget);

        // rebuild bundles
        command = "mvn clean install";
        genOutput = ITUtility.runArbitraryCommand(command, 0, new File(tempDir, "BuggyWorkflow"));
        Log.info(genOutput);
        // package bundles
        String listCommand = " bundle launch --dir BuggyWorkflow/target/Workflow_Bundle_BuggyWorkflow_1.0-SNAPSHOT_SeqWare_"
                + SEQWARE_VERSION + "/";
        String listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.FAILURE, tempDir);
        Log.stderr(listOutput);
        Assert.assertTrue("Workflow did not fail as expected",
                listOutput.contains("The method 'do_run' exited abnormally so the Runner will terminate here!"));

        listCommand = " workflow-run propagate-statuses";
        listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, tempDir);
        Assert.assertTrue("Statuses not refreshed", listOutput.contains("Propagated workflow engine statuses"));

        // check whether the error output contains the expected error
        listCommand = " workflow-run stderr --accession 2";
        listOutput = ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, tempDir);
        Log.stderr(listOutput);
        Assert.assertTrue("Did not create error output", listOutput.contains("Created file 2.err"));

        // cat stderr and check for error
        command = "cat 2.err";
        genOutput = ITUtility.runArbitraryCommand(command, 0, tempDir);
        Log.stderr(genOutput);
        Assert.assertTrue("Did not see error in error output, saw: " + genOutput, genOutput.contains("missing operand"));

        // clean-up on the way out
        tempDir.deleteOnExit();
    }
}
