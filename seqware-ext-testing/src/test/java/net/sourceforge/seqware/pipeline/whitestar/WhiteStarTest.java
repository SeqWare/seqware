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
package net.sourceforge.seqware.pipeline.whitestar;

import io.seqware.cli.Main;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.plugins.ITUtility;
import net.sourceforge.seqware.pipeline.runner.PluginRunner;
import org.apache.commons.exec.environment.EnvironmentUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Run a helloworld using whitestar.
 * 
 * @author dyuen
 */
public class WhiteStarTest {

    @Test
    public void testWorkflow() throws Exception {
        // override seqware settings file
        List<String> whiteStarProperties = new ArrayList<>();
        whiteStarProperties.add("SW_METADATA_METHOD=inmemory");
        whiteStarProperties.add("SW_REST_USER=fubar");
        whiteStarProperties.add("SW_REST_PASS=fubar");
        whiteStarProperties.add("SW_ADMIN_REST_URL=fubar");
        whiteStarProperties.add("SW_DEFAULT_WORKFLOW_ENGINE=whitestar");
        whiteStarProperties.add("OOZIE_WORK_DIR=/tmp");

        Path createTempFile = Files.createTempFile("whitestar", "properties");
        FileUtils.writeLines(createTempFile.toFile(), whiteStarProperties);

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
        environment.put("SEQWARE_SETTINGS", createTempFile.toAbsolutePath().toString());
        String runSeqwareCLI = ITUtility.runSeqwareCLI(" bundle launch  --no-metadata --dir " + bundleDir.getAbsolutePath(),
                ReturnValue.SUCCESS, tempDir.toFile(), environment);

        // save system environment variables
        Map<String, String> env = System.getenv();

        // override for launching
        WhiteStarTest.set(environment);
        Main.main(new String[] { "bundle", "launch", "--no-metadata", "--dir", bundleDir.getAbsolutePath() });
        WhiteStarTest.set(env);

        System.out.println(runSeqwareCLI);
    }

    /**
     * Evil https://stackoverflow.com/questions/318239/how-do-i-set-environment-variables-from-java
     * 
     * @param newenv
     * @throws Exception
     */
    public static void set(Map<String, String> newenv) throws Exception {
        Class[] classes = Collections.class.getDeclaredClasses();
        Map<String, String> env = System.getenv();
        for (Class cl : classes) {
            if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                Field field = cl.getDeclaredField("m");
                field.setAccessible(true);
                Object obj = field.get(env);
                Map<String, String> map = (Map<String, String>) obj;
                map.clear();
                map.putAll(newenv);
            }
        }
    }

}
