/*
 * Copyright (C) 2012 SeqWare
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * Runs the tests for SymLinkFileReporterTest on this page: https://wiki.oicr.on.ca/x/Jga5Ag
 * @author mtaschuk
 */
public class SymLinkFileReporterTest extends PluginTest {

    private String outputFilename = "test";
    private String fullOutputFilename = outputFilename + ".csv";
    private String studyDir = "AbcCo_Exome_Sequencing-120/-/Exome_ABC015069_Test_2-4783";
    private String sampleDir = "./-/Exome_ABC015069_Test_2-4783";
    private String[] allDirs = new String[]{"ABC019534_Nimblegen_data-6548", "AbcCo_Tumour_Sequencing-4758", "AbcCo_Exome_Sequencing-120", "MixingExperiment-6144"};

    @Before
    @Override
    public void setUp() {
        super.setUp();
        instance = new SymLinkFileReporter();
        instance.setMetadata(metadata);
    }

    public SymLinkFileReporterTest() {
    }

    @Test
    public void testGetSequencerRunFiles() {
        launchPlugin("--output-filename", outputFilename, "--sequencer-run", "SRKDKJKLFJKLJ90040", "--no-links");
        examineFile(fullOutputFilename, 76, 35, 10);
    }
    
    
    @Test
    public void testGetStudyFilesAndSymlinks() {
        launchPlugin("--output-filename", outputFilename, "--study", "AbcCo_Exome_Sequencing", 
                "--no-links");
        examineFile(fullOutputFilename, 18, 35, 6);
//        examineDirectory(13, studyDir);
    }

    @Test
    public void testGetStudyFilesAndSymlinksExplicitly() {
        launchPlugin("--output-filename", outputFilename, "--study", "AbcCo_Exome_Sequencing", 
                "--no-links",
                "--link", "s");

        examineFile(fullOutputFilename, 18, 35, 6);
//        examineDirectory(13, studyDir);
    }

    @Test
    public void testGetStudyFilesOnlyBamFiles() {
        launchPlugin("--output-filename", outputFilename, "--study", "AbcCo_Exome_Sequencing", 
                "--no-links",
                "--file-type", "application/bam");

        examineFile(fullOutputFilename, 2, 35, 1);
//        examineDirectory(1, studyDir);
    }

    @Test
    public void testGetStudyFilesMakePhysicalLinks() {
        launchPlugin("--output-filename", outputFilename, "--study", "AbcCo_Exome_Sequencing", 
                "--no-links",
                "--link", "P");

        examineFile(fullOutputFilename, 18, 35, 6);
//        examineDirectory(0, studyDir);
    }

    @Test
    public void testGetStudyFilesProductionFormat() {
        launchPlugin("--output-filename", outputFilename, "--study", "AbcCo_Exome_Sequencing", 
                "--no-links",
                "--prod-format");
        examineFile(fullOutputFilename, 18, 35, 6);
//        examineDirectory(4, "AbcCo_Exome_Sequencing-120/-/null/null");
    }

    @Test
    public void testGetStudyFilesNoLinks() {
        launchPlugin("--output-filename", outputFilename, "--study", "AbcCo_Exome_Sequencing", "--no-links");

        examineFile(fullOutputFilename, 18, 35, 6);
        Assert.assertFalse("The symlinks directory was created even though it was specified no-links", new File(studyDir).exists());
    }

    @Test
    public void testGetStudyFilesShowAllWorkflowRuns() {
        launchPlugin("--output-filename", outputFilename, "--study", "AbcCo_Exome_Sequencing", 
                "--no-links",
                "--show-failed-and-running", "--show-status");

        examineFile(fullOutputFilename, 18, 36, 7);
//        examineDirectory(13, studyDir);
    }

    @Test
    public void testGetStudyFilesAndSymlinksWithDuplicates() {
        launchPlugin("--output-filename", outputFilename, "--study", "AbcCo_Exome_Sequencing", 
                "--no-links",
                "--duplicates");
        examineFile(fullOutputFilename, 22, 35, 6);
//        examineDirectory(13, studyDir);
    }

    /////////////////////////////////////////////////DUMP ALL
    @Test
    public void testGetAllFilesAndSymlinks() {
        launchPlugin("--output-filename", outputFilename, "--dump-all", 
                "--no-links");
        examineFile(fullOutputFilename, 118, 35, 6);
//        examineDirectory(13, studyDir);
    }

    @Test
    public void testGetAllFilesAndSymlinksExplicitly() {
        launchPlugin("--output-filename", outputFilename, "--dump-all", 
                "--no-links",
                "--link", "s");
        examineFile(fullOutputFilename, 118, 35, 6);
//        examineDirectory(13, studyDir);
    }

    @Test
    public void testGetAllFilesAndSymlinksOnlyBamFiles() {
        launchPlugin("--output-filename", outputFilename, "--dump-all", 
                "--no-links",
                "--file-type", "application/bam");
        examineFile(fullOutputFilename, 18, 35, 6);
//        examineDirectory(1, studyDir);
    }

    @Test
    public void testGetAllFilesAndSymlinksProductionFormat() {
        launchPlugin("--output-filename", outputFilename, "--dump-all", 
                "--no-links",
                "--prod-format");
        examineFile(fullOutputFilename, 118, 35, 6);
//        for (String dir : allDirs) {
//            examineDirectory(4, dir + "/-/null/null");
//        }

    }

    @Test
    public void testGetAllFilesAndPhysicalLinks() {
        launchPlugin("--output-filename", outputFilename, "--dump-all", 
                "--no-links",
                "--link", "P");
        examineFile(fullOutputFilename, 118, 35, 6);
//        examineDirectory(0, studyDir);
    }

    @Test
    public void testGetAllFilesNoLinks() {
        launchPlugin("--output-filename", outputFilename, "--dump-all", "--no-links");

        examineFile(fullOutputFilename, 118, 35, 6);
        Assert.assertFalse("The symlinks directory was created even though it was specified no-links", new File(studyDir).exists());
    }

    @Test
    public void testGetAllFilesAndSymlinksShowAllWorkflowRuns() {
        launchPlugin("--output-filename", outputFilename, "--dump-all", 
                "--no-links",
                "--show-failed-and-running", "--show-status");

        examineFile(fullOutputFilename, 125, 36, 7);
//        examineDirectory(13, studyDir);
    }

    @Test
    public void testGetAllFilesAndSymlinksWithDuplicates() {
        launchPlugin("--output-filename", outputFilename, "--dump-all", "--no-links","--duplicates");
        examineFile(fullOutputFilename, 129, 35, 6);
//        examineDirectory(13, studyDir);
    }

///////////////////////////////////////////////SAMPLE
    @Test
    public void testGetSampleFilesAndSymlinks() {
        launchPlugin("--output-filename", outputFilename, "--sample", "Exome_ABC015069_Test_2", "--no-links");
        examineFile(fullOutputFilename, 18, 35, 10);
//        examineDirectory(13, sampleDir);
    }

    @Test
    public void testGetSampleFilesAndSymlinksExplicitly() {
        launchPlugin("--output-filename", outputFilename, "--sample", "Exome_ABC015069_Test_2", 
                "--no-links",
                "--link", "s");

        examineFile(fullOutputFilename, 18, 35, 10);
//        examineDirectory(13, sampleDir);
    }

    @Test
    public void testGetSampleFilesOnlyBamFiles() {
        launchPlugin("--output-filename", outputFilename, "--sample", "Exome_ABC015069_Test_2", 
                "--no-links",
                "--file-type", "application/bam");

        examineFile(fullOutputFilename, 2, 35, 5);
//        examineDirectory(1, sampleDir);
    }

    @Test
    public void testGetSampleFilesMakePhysicalLinks() {
        launchPlugin("--output-filename", outputFilename, "--sample", "Exome_ABC015069_Test_2", 
                "--no-links",
                "--link", "P");

        examineFile(fullOutputFilename, 18, 35, 10);
//        examineDirectory(0, sampleDir);
    }

    @Test
    public void testGetSampleFilesProductionFormat() {
        launchPlugin("--output-filename", outputFilename, "--sample", "Exome_ABC015069_Test_2", 
                "--no-links",
                "--prod-format");
        examineFile(fullOutputFilename, 18, 35, 10);
//        examineDirectory(4, "./-/null/null");
    }

    @Test
    public void testGetSampleFilesNoLinks() {
        launchPlugin("--output-filename", outputFilename, "--sample", "Exome_ABC015069_Test_2", "--no-links");

        examineFile(fullOutputFilename, 18, 35, 10);
        Assert.assertFalse("The symlinks directory was created even though it was specified no-links", new File(sampleDir).exists());
    }

    @Test
    public void testGetSampleFilesShowAllWorkflowRuns() {
        launchPlugin("--output-filename", outputFilename, "--sample", "Exome_ABC015069_Test_2", 
                "--no-links",
                "--show-failed-and-running", "--show-status");

        examineFile(fullOutputFilename, 18, 36, 11);
//        examineDirectory(13, sampleDir);
    }

    @Test
    public void testGetSampleFilesAndSymlinksWithDuplicates() {
        launchPlugin("--output-filename", outputFilename, "--sample", "Exome_ABC015069_Test_2", 
                "--no-links",
                "--duplicates");
        examineFile(fullOutputFilename, 22, 35, 10);
//        examineDirectory(13, sampleDir);
    }
/////////////////////////////////////////////NEGATIVE TESTS, see https://jira.oicr.on.ca/browse/SEQWARE-1332

//    @Test
//    public void testGetStudyFilesWrongStudyName() {
//        launchPlugin("--output-filename", outputFilename, "--no-links", "--study", "ASCBDK");
//        checkReturnValue(ReturnValue.SUCCESS, instance.parse_parameters());
//        checkReturnValue(ReturnValue.SUCCESS, instance.init());
//        checkReturnValue(ReturnValue.INVALIDARGUMENT, instance.do_run());
//        examineFile(fullOutputFilename, 1, 35, 10);
//    }
//
//    @Test
//    public void testGetSampleFilesWrongSampleName() {
//        instance.setParams(Arrays.asList("--output-filename", outputFilename, "--no-links", "--sample", "ASCBDK"));
//        checkReturnValue(ReturnValue.SUCCESS, instance.parse_parameters());
//        checkReturnValue(ReturnValue.SUCCESS, instance.init());
//        checkReturnValue(ReturnValue.INVALIDARGUMENT, instance.do_run());
//        examineFile(fullOutputFilename, 1, 35, 10);
//    }
//
//    @Test
//    public void testGetFilesWithStudySampleNameDumpAll() {
//        instance.setParams(Arrays.asList("--output-filename", outputFilename, "--no-links", "--study",
//                "AbcCo_Exome_Sequencing", "--sample", "Exome_ABC015069_Test_2", "--dump-all"));
//        checkReturnValue(ReturnValue.SUCCESS, instance.parse_parameters());
//        checkReturnValue(ReturnValue.SUCCESS, instance.init());
//        checkReturnValue(ReturnValue.INVALIDPARAMETERS, instance.do_run());
//        examineFile(fullOutputFilename, 1, 35, 10);
//    }
//
//    @Test
//    public void testGetSampleFilesWrongLinkType() {
//        instance.setParams(Arrays.asList("--output-filename", outputFilename, "--no-links", "--sample",
//                "Exome_ABC015069_Test_2", "--link", "Z"));
//        checkReturnValue(ReturnValue.SUCCESS, instance.parse_parameters());
//        checkReturnValue(ReturnValue.SUCCESS, instance.init());
//        checkReturnValue(ReturnValue.INVALIDPARAMETERS, instance.do_run());
//        examineFile(fullOutputFilename, 1, 35, 10);
//    }
    @Test
    public void testGetSampleFilesBadlySpelledParameter() {
        instance.setParams(Arrays.asList("--output-filename", outputFilename, "--no-links", "--sample",
                "Exome_ABC015069_Test_2", "--dplicates"));
        checkReturnValue(ReturnValue.INVALIDARGUMENT, instance.parse_parameters());
        Assert.assertFalse("Output file should not exist.", new File(fullOutputFilename).exists());
    }

    /**
     * Checks the links directory to see if it exists and if it has the expected
     * number of links inside.
     *
     * @param expectedLinks
     * @param dirName
     */
    private void examineDirectory(int expectedLinks, String dirName) {
        File dir = new File(dirName);
        Assert.assertTrue("The symlink directory does not exist", dir.exists());
        Assert.assertEquals("The number of produced links is not correct", expectedLinks, dir.listFiles().length);
    }

    /**
     * Checks the CSV file to see if it has the expected number of rows,
     * columns, and has less nulls per line than the expected maximum.
     *
     * @param filename The filename of the CSV file
     * @param expectedRowCount the expected number of rows
     * @param expectedColumnCount the expected number of columns
     * @param expectedMaxNulls the maximum 'null' values allowed per line
     */
    private void examineFile(String filename, int expectedRowCount, int expectedColumnCount, int expectedMaxNulls) {
        int lines = 0;
        Pattern nullPatt = Pattern.compile("null");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));

            String line = reader.readLine();

            while (line != null) {
                //count lines
                lines++;

                //check number of columns
                int columns = line.split("\t", -1).length;
                Assert.assertEquals("The number of columns is different than expected on line " + lines, expectedColumnCount, columns);

                Matcher nullMatcher = nullPatt.matcher(line);
                int nulls = 0;
                while (nullMatcher.find()) {
                    nulls++;
                }
                Assert.assertTrue("The number of nulls at " + nulls + " is greater than expected " + expectedMaxNulls + " on line " + lines,
                        nulls <= expectedMaxNulls);


                line = reader.readLine();
            }
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(SymLinkFileReporterTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        Assert.assertEquals("The number of rows is different than expected", expectedRowCount, lines);
    }
    @Rule
    public TestRule watchman = new TestWatcher() {

        @Override
        protected void failed(Throwable e, Description d) {
            String newName = outputFilename + "_failed" + d.getMethodName() + ".csv";
            File file = new File(fullOutputFilename);
            if (file.exists()) {
                file.renameTo(new File(newName));
            }
            Log.error(d + ": CSV report moved to " + newName);

            for (String dirName : allDirs) {
                renameDir(dirName, d);
            }
            renameDir("./-", d);

        }

        private void renameDir(String dirName, Description d) {
            File dir = new File(dirName);

            if (dir.exists()) {
                String newDir = dir.getName() + "_failed" + d.getMethodName();
                dir.renameTo(new File(newDir));
            }
        }

        @Override
        protected void succeeded(Description d) {
            File file = new File(fullOutputFilename);
            if (file.exists()) {
                Assert.assertTrue("Could not delete file " + fullOutputFilename, file.delete());
            }

            for (String dirName : allDirs) {
                deleteDir(dirName);
            }
            deleteDir("./-");

        }

        private void deleteDir(String dirName) {
            File dir = new File(dirName);
            if (dir.exists()) {
                try {
                    FileUtils.deleteDirectory(dir);
                } catch (IOException ex) {
                    Log.error("Could not delete dir " + dirName, ex);
//                    Assert.fail("Could not delete directory " + dirName);
                }
            }
        }
    };
}