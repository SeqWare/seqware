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
import java.io.File;
import java.io.IOException;
import junit.framework.Assert;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.StringUtils;

/**
 * These tests support the BasicDecider. Many tests are already in the BasicDeciderTest class(es). 
 * This will restrict itself to some basic sanity checking and testing of the generated archetype
 *
 * @author dyuen
 */
public class BasicDeciderET {
    
    @BeforeClass
    public static void resetDatabase() {
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers();
    }

    @Test
    public void runBasicDecider() throws IOException {
        String listCommand = "-p net.sourceforge.seqware.pipeline.deciders.BasicDecider -- --all --wf-accession 6685 --parent-wf-accessions 4767 --test"; 
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        Log.info(listOutput);
        Assert.assertTrue("expected to see 3 launches, found " + StringUtils.countOccurrencesOf(listOutput, "java -jar") , StringUtils.countOccurrencesOf(listOutput, "java -jar") == 3);
    }
    
    @Test
    public void createDeciderFromArchetype() throws IOException {
        File createTempDir = Files.createTempDir();
        // generate , build and install the decider archetype
        String command = "mvn archetype:generate -DarchetypeCatalog=local -Dpackage=com.seqware.github -DgroupId=com.github.seqware "
                + "-DarchetypeArtifactId=seqware-archetype-decider -Dversion=1.0-SNAPSHOT -DarchetypeGroupId=com.github.seqware "
                + "-DartifactId=seqware-archetype-decider -DworkflowDirectoryName=seqware-archetype-decider "
                + "-DworkflowName=seqware-archetype-decider -DworkflowVersion=1.0-SNAPSHOT -B -Dgoals=install";
        String genOutput = ITUtility.runArbitraryCommand(command, 0, createTempDir);
        Log.info(genOutput);
        // run the decider
        File seqwareJar = ITUtility.retrieveFullAssembledJar();
        String SEQWARE_VERSION = new ReturnValue().getClass().getPackage().getImplementationVersion();
        command = "java -cp "+createTempDir.getAbsolutePath()+"/seqware-archetype-decider/target/Decider_1.0-SNAPSHOT_seqware-archetype-decider_1.0_SeqWare_"+SEQWARE_VERSION+".jar:"
                + seqwareJar.getAbsolutePath() 
                + " net.sourceforge.seqware.pipeline.runner.PluginRunner -p com.seqware.github.HelloWorldDecider -- --all --wf-accession 6685 --parent-wf-accessions 4767 --test";
        genOutput = ITUtility.runArbitraryCommand(command, 0, createTempDir);
        Log.info(genOutput);
        Assert.assertTrue("expected to see 1 launches, found " + StringUtils.countOccurrencesOf(genOutput, "java -jar") , StringUtils.countOccurrencesOf(genOutput, "java -jar") == 1);
    }
    
}
