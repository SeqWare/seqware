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

import java.io.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sourceforge.seqware.common.metadata.MetadataWS;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 *
 * @author mtaschuk
 */
public class SymLinkFileReporterTest {

    private static net.sourceforge.seqware.common.metadata.Metadata metadata;
    private SymLinkFileReporter instance;
    private String outputFilename = "test";
    private String fullOutputFilename = outputFilename + ".csv";

    public SymLinkFileReporterTest() {
    }
    @Rule
    public TestRule watchman = new TestWatcher() {

        @Override
        protected void failed(Throwable e, Description d) {
            String newName = outputFilename + "_failed" + d.getMethodName() + ".csv";
            File file = new File(fullOutputFilename);
            file.renameTo(new File(newName));
            Log.error(d + ": CSV report moved to " + newName);
        }

        @Override
        protected void succeeded(Description d) {
            File file = new File(fullOutputFilename);
            Assert.assertTrue("Could not delete file " + fullOutputFilename, file.delete());
        }
    };

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        metadata = new MetadataWS();
        metadata.init("http://localhost:8889/seqware-webservice", "admin@admin.com", "admin");

        instance = new SymLinkFileReporter();
        instance.setMetadata(metadata);


    }

    @After
    public void tearDown() {
        metadata.clean_up();

    }

    @Test
    public void testGetStudyFiles() {
        instance.setParams(Arrays.asList("--output-filename", outputFilename, "--no-links", "--study", "AbcCo_Exome_Sequencing"));
        checkReturnValue(ReturnValue.SUCCESS, instance.parse_parameters());
        checkReturnValue(ReturnValue.SUCCESS, instance.init());
        checkReturnValue(ReturnValue.SUCCESS, instance.do_run());
        examineFile(fullOutputFilename, 18, 34, 6);
    }

    @Test
    public void testGetStudyBamFiles() {
        instance.setParams(Arrays.asList("--output-filename", outputFilename, "--no-links", "--study", "AbcCo_Exome_Sequencing", "--file-type", "application/bam"));
        checkReturnValue(ReturnValue.SUCCESS, instance.parse_parameters());
        checkReturnValue(ReturnValue.SUCCESS, instance.init());
        checkReturnValue(ReturnValue.SUCCESS, instance.do_run());
        examineFile(fullOutputFilename, 2, 34, 1);
    }

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
                int columns = line.split("\t").length;
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

    private void checkReturnValue(int expected, ReturnValue rv) {
        Assert.assertEquals("Get study files and symlinks did not exit successfully.", expected, rv.getExitStatus());
    }
}
