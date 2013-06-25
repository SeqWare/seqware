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
import java.util.Random;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.metadb.util.TestDatabaseCreator;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * These tests support command-line tools found in the SeqWare User Tutorial,
 * in this case, GenericMetadataSaver
 *
 * @author dyuen
 */
public class GenericMetadataSaverIT {
    
    @BeforeClass
    public static void resetDatabase() {
        TestDatabaseCreator.resetDatabaseWithUsers();
    }

    /**
     * This tests saves generic metadata for a file on the local VM instead of
     * provisioning it.
     *
     * @throws IOException
     */
    @Test
    public void testGenericMetadataSaver() throws IOException {
        saveGenericMetadataFileForSample("10");
    }

    public String saveGenericMetadataFileForSample(String sampleAccession) throws IOException {
        File createTempDir = Files.createTempDir();
        // create a random new file and check that the file we want to save metadata about exists 
        File inputFile = File.createTempFile("input", "out");
        final String content = "This is a funky funky test file";
        FileUtils.write(inputFile, content);

        Random generator = new Random();
        String random = String.valueOf(generator.nextInt());
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.ModuleRunner -- --module net.sourceforge.seqware.pipeline.modules.GenericMetadataSaver "
                + " --metadata-parent-accession  "+sampleAccession
                + " -- --gms-output-file text::text/plain::" + inputFile.getAbsolutePath()
                + " --gms-algorithm UploadText --gms-suppress-output-file-check";
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, createTempDir);
        Log.info(listOutput);
        return listOutput;
    }
    
    
}
