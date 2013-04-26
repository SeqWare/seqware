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

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import junit.framework.Assert;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.plugins.ITUtility;
import net.sourceforge.seqware.pipeline.plugins.PluginRunnerIT;
import net.sourceforge.seqware.pipeline.runner.PluginRunner;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * Do all tests that can be concurrently done in the admin tutorial
 *
 * @author dyuen
 */
public class AdminPhase1 {
    
    private static File separateTempDir = Files.createTempDir();

    @Test
    public void testPackageBundleAndInstallSeparately() throws IOException {

        separateTempDir = Files.createTempDir();
        Log.info("Trying to package archetype at: " + separateTempDir.getAbsolutePath());
        PluginRunner it = new PluginRunner();
        String SEQWARE_VERSION = it.getClass().getPackage().getImplementationVersion();
        Assert.assertTrue("unable to detect seqware version", SEQWARE_VERSION != null);
        Log.info("SeqWare version detected as: " + SEQWARE_VERSION);

        File packageDir = Files.createTempDir();

        // for this test, we're going to create, install and package just one archetype. Doing more seems redundant for this test
        String[] archetypes = {"java-workflow"/*, "simplified-ftl-workflow", "legacy-ftl-workflow", "simple-legacy-ftl-workflow"*/};
        for (String archetype : archetypes) {
            String workflow = "seqware-archetype-" + archetype;
            // generate and install archetypes to local maven repo
            String command = "mvn archetype:generate -DarchetypeCatalog=local -Dpackage=com.seqware.github -DgroupId=com.github.seqware -DarchetypeArtifactId=" + workflow + " -Dversion=1.0-SNAPSHOT -DarchetypeGroupId=com.github.seqware -DartifactId=" + workflow + " -DworkflowDirectoryName=" + workflow + " -DworkflowName=" + workflow + " -DworkflowVersion=1.0-SNAPSHOT -B -Dgoals=install";
            String genOutput = ITUtility.runArbitraryCommand(command, 0, separateTempDir);
            Log.info(genOutput);
            // install the workflows to the database and record their information 
            File workflowDir = new File(separateTempDir, workflow);
            File targetDir = new File(workflowDir, "target");
            File bundleDir = new File(targetDir, "Workflow_Bundle_" + workflow + "_1.0-SNAPSHOT_SeqWare_" + SEQWARE_VERSION);

            String packageCommand = "-p net.sourceforge.seqware.pipeline.plugins.BundleManager -verbose -- -b " + packageDir + " -p " + bundleDir.getAbsolutePath();
            String packageOutput = ITUtility.runSeqWareJar(packageCommand, ReturnValue.SUCCESS);
            Log.info(packageOutput);

            // locate the zip bundle and then install it
            File zippedBundle = new File(packageDir, bundleDir.getName() + ".zip");
            Assert.assertTrue("zipped bundle " + zippedBundle.getAbsolutePath() + ".zip", zippedBundle.exists());

            String installCommand = "-p net.sourceforge.seqware.pipeline.plugins.BundleManager -verbose -- -b " + zippedBundle + " -i";
            String installOutput = ITUtility.runSeqWareJar(installCommand, ReturnValue.SUCCESS);
            Log.info(installOutput);
        }
        
        FileUtils.deleteDirectory(packageDir);
    }

    @Test
    public void testLaunchScheduled() throws IOException {
        // launch-scheduled
        String schedCommand = "-p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher -- --launch-scheduled";
        String schedOutput = ITUtility.runSeqWareJar(schedCommand, ReturnValue.SUCCESS);
        Log.info(schedOutput);
    }

    @Test
    public void testMonitoring() throws IOException {
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.WorkflowStatusChecker -- --tp 1000";
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS);
        Log.info(listOutput);
    }
    
    // later we will test/add utilities for wrapping cancel and rescue workflows
    
    @AfterClass
    public static void cleanup() throws IOException{
        FileUtils.deleteDirectory(separateTempDir);
    }
}
