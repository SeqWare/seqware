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
import java.util.List;
import java.util.UUID;
import junit.framework.Assert;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import org.apache.commons.io.FileUtils;
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
        
    }
    
}
