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

import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

/**
 * These tests support command-line tools found in the SeqWare User Tutorial, in this case, ProvisionFiles
 * 
 * @author dyuen
 */
public class ProvisionFilesET {

    public static final String SETTINGS_PREFIX_KEY = "IT_DATASTORE_PREFIX";
    public static final String DEFAULT_DATASTORE_PREFIX = "/datastore/";

    private static String getDatastorePrefix() {
        try {
            Map<String, String> settings = ConfigTools.getSettings();
            if (settings.containsKey(SETTINGS_PREFIX_KEY)) {
                return settings.get(SETTINGS_PREFIX_KEY);
            }
        } catch (Exception e) {
            Log.fatal("Could not read .seqware/settings, this will likely crash extended integration tests", e);
        }
        return DEFAULT_DATASTORE_PREFIX;
    }

    @BeforeClass
    public static void resetDatabase() {
        ExtendedTestDatabaseCreator.resetDatabaseWithUsers();
    }

    /**
     * This test provisions a file in with random input and checks on the accession writing
     * 
     * @throws IOException
     */
    @Test
    public void testProvisionFileIn_RandomInput() throws IOException {
        provisionFileWithRandomInput("10");
    }

    /**
     * Provision a file into SeqWare and then provision it back out
     * 
     * @throws IOException
     */
    @Test
    public void provisionInAndOut() throws IOException {
        File provisionedFile = this.provisionFileWithRandomInput("10");
        File tempDir = FileUtils.getTempDirectory();

        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.ModuleRunner -- --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles "
                + " --no-metadata " + " -- -i" + provisionedFile.getAbsolutePath() + " -o " + tempDir.getAbsolutePath();
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        Log.info(listOutput);
        File retrievedFile = new File(tempDir.getAbsoluteFile(), provisionedFile.getName());

        Assert.assertTrue("file locations do not differ: " + retrievedFile.getAbsolutePath() + "vs" + provisionedFile.getAbsolutePath(),
                !retrievedFile.getAbsolutePath().equals(provisionedFile.getAbsolutePath()));
        Assert.assertTrue("file contents not the same",
                FileUtils.readFileToString(retrievedFile).equals(FileUtils.readFileToString(provisionedFile)));
    }

    public File provisionFileWithRandomInput(String sampleAccession) throws IOException {
        // create a random new file and check that the file we want to provision exists
        File inputFile = File.createTempFile("input", "out");
        final String content = "This is a funky funky test file";
        FileUtils.write(inputFile, content);

        File metadataFile = File.createTempFile("metadata", "out");

        Random generator = new Random();
        String random = String.valueOf(generator.nextInt());
        String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.ModuleRunner -- --module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles "
                + "--metadata-output-file-prefix "
                + getDatastorePrefix()
                + " --metadata-parent-accession "
                + sampleAccession
                + " --metadata-processing-accession-file  "
                + metadataFile.getAbsolutePath()
                + " -- -im text::text/plain::"
                + inputFile.getAbsolutePath() + " -o " + getDatastorePrefix() + " --force-copy";
        String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
        Log.info(listOutput);
        // check that file was ended up being provisioned correctly
        File provisioned = new File(getDatastorePrefix() + inputFile.getName());
        Assert.assertTrue("file did not end up in final location", provisioned.exists());
        String contents = FileUtils.readFileToString(provisioned).trim();
        Assert.assertTrue("file contents not as expected", contents.equals(content));

        // check on metadata file
        String metadataContent = FileUtils.readFileToString(metadataFile).trim();
        try {
            int processingInt = Integer.parseInt(metadataContent);
            Log.info("provisioned file as sw_accession: " + processingInt);
        } catch (NumberFormatException e) {
            Assert.assertTrue("did not output metadata sw_accession properly", false);
        }
        return provisioned;
    }

}
