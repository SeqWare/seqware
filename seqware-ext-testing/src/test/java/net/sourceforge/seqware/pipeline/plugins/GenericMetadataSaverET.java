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
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * These tests support command-line tools found in the SeqWare User Tutorial, in this case, GenericMetadataSaver
 * 
 * @author dyuen
 */
public class GenericMetadataSaverET {

    @BeforeClass
    public static void resetDatabase() {
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers();
    }

    /**
     * This tests saves generic metadata for a file on the local VM instead of provisioning it.
     * 
     * @throws IOException
     */
    @Test
    public void testGenericMetadataSaverOldCLI() throws IOException {
        saveGenericMetadataFileForSample("10", false);
    }

    public String saveGenericMetadataFileForSample(String sampleAccession, boolean cli) throws IOException {
        File createTempDir = Files.createTempDir();
        // create a random new file and check that the file we want to save metadata about exists
        File inputFile = File.createTempFile("input", "out");
        final String content = "This is a funky funky test file";
        FileUtils.write(inputFile, content, StandardCharsets.UTF_8);

        Random generator = new Random();
        String random = String.valueOf(generator.nextInt());
        String listOutput = runOldCommand(sampleAccession, inputFile, createTempDir);
        Log.info(listOutput);
        return listOutput;
    }

    protected String runOldCommand(String sampleAccession, File inputFile, File createTempDir) throws IOException {
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.ModuleRunner -- --module net.sourceforge.seqware.pipeline.modules.GenericMetadataSaver "
                + " --metadata-parent-accession  "
                + sampleAccession
                + " -- --gms-output-file text::text/plain::"
                + inputFile.getAbsolutePath() + " --gms-algorithm UploadText --gms-suppress-output-file-check";
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, createTempDir);
        return listOutput;
    }

}
