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
import io.seqware.Reports;
import java.io.File;
import java.io.IOException;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * These tests support the BasicDecider. Many tests are already in the BasicDeciderTest class(es). This will restrict itself to some basic
 * sanity checking and testing of the generated archetype
 * 
 * @author dyuen
 */
public class BasicDeciderET {

    @BeforeClass
    public static void resetDatabase() {
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers();
        Reports.triggerProvenanceReport();
    }

    @Test
    public void runBasicDecider() throws IOException {
        String listCommand = "-p net.sourceforge.seqware.pipeline.deciders.BasicDecider -- --all --wf-accession 6685 --parent-wf-accessions 4767 --test";
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        Log.info(listOutput);
        Assert.assertTrue("expected to see 3 launches, found " + StringUtils.countMatches(listOutput, "java -jar"),
                StringUtils.countMatches(listOutput, "java -jar") == 3);
    }

    @Test
    public void createDeciderFromArchetype() throws IOException {
        File createTempDir = Files.createTempDir();
        // generate , build and install the decider archetype
        String command = "mvn archetype:generate -DarchetypeCatalog=local -Dpackage=com.github.seqware -DgroupId=com.github.seqware "
                + "-DarchetypeArtifactId=seqware-archetype-decider -Dversion=1.0-SNAPSHOT -DarchetypeGroupId=com.github.seqware "
                + "-DartifactId=decider-HelloWorld -Dworkflow-name=HelloWorld " + "-B -Dgoals=install";
        String genOutput = ITUtility.runArbitraryCommand(command, 0, createTempDir);
        Log.info(genOutput);
        // run the decider
        File seqwareJar = ITUtility.retrieveFullAssembledJar();
        String SEQWARE_VERSION = ReturnValue.class.getPackage().getImplementationVersion();
        command = "java -cp "
                + createTempDir.getAbsolutePath()
                + "/decider-HelloWorld/target/Decider_1.0-SNAPSHOT_HelloWorld_1.0_SeqWare_"
                + SEQWARE_VERSION
                + ".jar:"
                + seqwareJar.getAbsolutePath()
                + " net.sourceforge.seqware.pipeline.runner.PluginRunner -p com.github.seqware.HelloWorldDecider -- --all --wf-accession 6685 --parent-wf-accessions 4767 --test";
        genOutput = ITUtility.runArbitraryCommand(command, 0, createTempDir);
        Log.info(genOutput);
        Assert.assertTrue("expected to see 1 launches, found " + StringUtils.countMatches(genOutput, "java -jar"),
                StringUtils.countMatches(genOutput, "java -jar") == 1);
    }

}
