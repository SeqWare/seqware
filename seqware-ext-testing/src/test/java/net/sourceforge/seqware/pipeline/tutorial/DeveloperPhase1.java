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
package net.sourceforge.seqware.pipeline.tutorial;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.plugins.ITUtility;
import net.sourceforge.seqware.pipeline.plugins.PluginRunnerET;
import net.sourceforge.seqware.pipeline.runner.PluginRunner;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Build and install a bundle, used by both the User tutorial and the Developer tutorial
 *
 * @author dyuen
 */
public class DeveloperPhase1 {

    public static final String WORKFLOW = "Workflow";
    public static File BundleDir = null;
    public static File BuildDir = null;
    public static File JavaClient = null;

    @BeforeClass
    public static void testListAvailableWorkflowsAndTheirParameters() throws IOException {
        PluginRunnerET pit = new PluginRunnerET();
        PluginRunnerET.clearStaticVariables();
        PluginRunner it = new PluginRunner();
        String SEQWARE_VERSION = it.getClass().getPackage().getImplementationVersion();
        Assert.assertTrue("unable to detect seqware version", SEQWARE_VERSION != null);
        Log.info("SeqWare version detected as: " + SEQWARE_VERSION);

        // for all tests, we're going to need to create and install our basic archetypes
        String[] archetypes = { "java-workflow" };
        PluginRunnerET.buildAndInstallArchetypes(archetypes, SEQWARE_VERSION, false, false);

        // list workflows and ensure that the workflow is installed
        List<Integer> accessions = new ArrayList<>();
        accessions.addAll(PluginRunnerET.getInstalledWorkflows().values());
        Assert.assertTrue("one accession expected", accessions.size() == 1);
        AccessionMap.accessionMap.put(WORKFLOW, accessions.get(0).toString());

        // ensure that a couple of the files we talk about in the tutorial exist
        File bundleDir = PluginRunnerET.getBundleLocations().get("seqware-archetype-java-workflow").getParentFile().getParentFile();
        Log.info("Looking for files in the bundle dir at " + bundleDir.getAbsolutePath());
        File pomXML = new File(bundleDir, "pom.xml");
        Assert.assertTrue("pom.xml does not exist", pomXML.exists());
        File properties = new File(bundleDir, "workflow.properties");
        Assert.assertTrue("workflow.properties does not exist", properties.exists());
        File metadata = new File(bundleDir.getAbsolutePath() + File.separatorChar + "workflow", "metadata.xml");
        Assert.assertTrue("metadata.xml does not exist at " + metadata.getAbsolutePath(), metadata.exists());
        File workflowClientJava = new File(bundleDir.getAbsolutePath() + File.separatorChar + "src" + File.separatorChar + "main"
                + File.separatorChar + "java" + File.separatorChar + "com" + File.separatorChar + "seqware" + File.separatorChar + "github"
                + File.separatorChar, "seqwarearchetypejavaworkflowWorkflow.java");
        Assert.assertTrue("java client does not exist at " + workflowClientJava.getAbsolutePath(), workflowClientJava.exists());

        // allocate needed items for future tests
        BundleDir = bundleDir;
        BuildDir = findTargetBundleDir(bundleDir);
        JavaClient = workflowClientJava;

    }

    public static File findTargetBundleDir(File projectDir) {
        File targetDir = new File(projectDir, "target");
        for (File f : targetDir.listFiles()) {
            if (f.isDirectory() && f.getName().startsWith("Workflow_Bundle_")) {
                return f;
            }
        }
        throw new RuntimeException("Could not locate target/WorkflowBundle_* directory");
    }

    @Test
    public void testModifyingTheWorkflow() throws IOException {
        Log.info("Editing java client at " + JavaClient.getAbsolutePath());
        List<String> readLines = FileUtils.readLines(JavaClient);
        // edit lines to match tutorial changes
        boolean linesAdded = false;
        for (int i = 0; i < readLines.size(); i++) {
            if (readLines.get(i).contains("copyJob2.addFile(outputFile);")) {
                readLines.remove(i);
                readLines.add(i,
                        "\nJob dateJob = this.getWorkflow().createBashJob(\"bash_date\");\ndateJob.setCommand(\"date >> dir1/output\");"
                                + "\ndateJob.addParent(copyJob2);\ndateJob.addFile(outputFile); ");
                linesAdded = true;
            }
        }
        Assert.assertTrue("lines were not added", linesAdded);
        // write back modified lines
        FileUtils.writeLines(JavaClient, readLines, false);
        // build and install modified bundle
        File buildDir = BundleDir;
        Log.info("build dir detected as " + buildDir.getAbsolutePath());
        String command = "mvn install";
        String genOutput = ITUtility.runArbitraryCommand(command, 0, buildDir);
    }
}
