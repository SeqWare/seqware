/*
 * Copyright (C) 2014 SeqWare
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
package io.seqware.pipeline.whitestar;

import io.seqware.pipeline.plugins.WorkflowLifecycle;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.testtools.BasicTestDatabaseCreator;
import net.sourceforge.seqware.pipeline.plugins.ITUtility;
import net.sourceforge.seqware.pipeline.plugins.PluginTest;
import net.sourceforge.seqware.pipeline.runner.PluginRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Run a helloworld using whitestar.
 *
 * @author dyuen
 */
public class WhiteStarWithMetadataTest extends PluginTest {

    @BeforeClass
    public static void beforeClass() {
        BasicTestDatabaseCreator.resetDatabaseWithUsers();
    }

    @Before
    @Override
    public void setUp() {
        instance = new WorkflowLifecycle();
        super.setUp();
    }

    @Test
    public void testWhiteStarStandardWorkflow() throws Exception {
        createAndRunWorkflow("whitestar");

    }

    @Test
    public void testWhiteStarParallelWorkflow() throws Exception {
        createAndRunWorkflow("whitestar-parallel");
    }

    protected void createAndRunWorkflow(String engine) throws Exception, IOException {
        // create a helloworld
        Path tempDir = Files.createTempDirectory("tempTestingDirectory");
        PluginRunner it = new PluginRunner();
        String SEQWARE_VERSION = it.getClass().getPackage().getImplementationVersion();
        Assert.assertTrue("unable to detect seqware version", SEQWARE_VERSION != null);
        Log.info("SeqWare version detected as: " + SEQWARE_VERSION);
        String archetype = "java-workflow";
        String workflow = "seqware-archetype-" + archetype;
        String workflowName = workflow.replace("-", "");
        // generate and install archetypes to local maven repo
        String command = "mvn archetype:generate -DarchetypeCatalog=local -Dpackage=com.seqware.github -DgroupId=com.github.seqware -DarchetypeArtifactId="
                + workflow
                + " -Dversion=1.0-SNAPSHOT -DarchetypeGroupId=com.github.seqware -DartifactId="
                + workflow
                + " -Dworkflow-name="
                + workflowName + " -B -Dgoals=install";

        String genOutput = ITUtility.runArbitraryCommand(command, 0, tempDir.toFile());
        Log.info(genOutput);
        // install the workflows to the database and record their information
        File workflowDir = new File(tempDir.toFile(), workflow);
        File targetDir = new File(workflowDir, "target");
        final String workflow_name = "Workflow_Bundle_" + workflowName + "_1.0-SNAPSHOT_SeqWare_" + SEQWARE_VERSION;
        File bundleDir = new File(targetDir, workflow_name);

        launchPlugin("--wait", "--bundle", bundleDir.getAbsolutePath(), "--workflow", "seqwarearchetypejavaworkflow", "--version",
                "1.0-SNAPSHOT", "--ini-files", bundleDir.getAbsolutePath()
                        + "/Workflow_Bundle_seqwarearchetypejavaworkflow/1.0-SNAPSHOT/config/seqwarearchetypejavaworkflowWorkflow.ini",
                "--workflow-engine", engine);

    }

}
