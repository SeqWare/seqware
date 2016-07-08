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

import io.seqware.cli.Main;
import io.seqware.pipeline.Utility;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.plugins.ExtendedTestDatabaseCreator;
import net.sourceforge.seqware.pipeline.plugins.ITUtility;
import net.sourceforge.seqware.pipeline.runner.PluginRunner;
import org.apache.commons.exec.environment.EnvironmentUtils;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Run a helloworld using whitestar.
 *
 * @author dyuen
 */
public class WhiteStarLT {

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @BeforeClass
    public static void setupWhiteStarTest() {
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers();
    }

    @AfterClass
    public static void teardownWhiteStarTest() {

    }

    @Test
    public void testWhiteStarStandardWorkflow() throws Exception {
        Path createTempFile = createSettingsFile("whitestar", "inmemory");
        createAndRunWorkflow(createTempFile, false);

    }

    @Test
    public void testWhiteStarParallelWorkflow() throws Exception {
        Path createTempFile = createSettingsFile("whitestar-parallel", "inmemory");
        createAndRunWorkflow(createTempFile, false);
    }

    static void createAndRunWorkflow(Path settingsFile, boolean metadata) throws Exception {
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

        Map environment = EnvironmentUtils.getProcEnvironment();
        environment.put("SEQWARE_SETTINGS", settingsFile.toAbsolutePath().toString());

        // save system environment variables
        Map<String, String> env = System.getenv();
        try {
            // override for launching
            Utility.set(environment);
            List<String> cmd = new ArrayList<>();
            cmd.add("bundle");
            cmd.add("launch");
            cmd.add("--dir");
            cmd.add(bundleDir.getAbsolutePath());
            if (!metadata) {
                cmd.add("--no-metadata");
            }
            Main.main(cmd.toArray(new String[cmd.size()]));
        } finally {
            Utility.set(env);
        }

    }

    static Path createSettingsFile(String engine, String metadataMethod) throws IOException {
        // override seqware settings file
        List<String> whiteStarProperties = new ArrayList<>();
        whiteStarProperties.add("SW_METADATA_METHOD=" + metadataMethod);
        whiteStarProperties.add("SW_REST_USER=admin@admin.com");
        whiteStarProperties.add("SW_REST_PASS=admin");
        whiteStarProperties.add("SW_ADMIN_REST_URL=fubar");
        whiteStarProperties.add("SW_DEFAULT_WORKFLOW_ENGINE=" + engine);
        whiteStarProperties.add("OOZIE_WORK_DIR=/tmp");
        // use this if running locally via mvn tomcat7:run
        whiteStarProperties.add("SW_REST_URL=http://localhost:8889/seqware-webservice");
        // use this in our regression testing framework
        // whiteStarProperties.add("SW_REST_URL=http://master:8080/SeqWareWebService");
        whiteStarProperties.add("OOZIE_SGE_THREADS_PARAM_FORMAT=-pe serial ${threads}");
        whiteStarProperties.add("OOZIE_SGE_MAX_MEMORY_PARAM_FORMAT=-l h_vmem=${maxMemory}M");
        Path createTempFile = Files.createTempFile("whitestar", "properties");
        FileUtils.writeLines(createTempFile.toFile(), whiteStarProperties);
        return createTempFile;
    }

}
