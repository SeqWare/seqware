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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sourceforge.seqware.common.util.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 *
 * @author mtaschuk
 */
public class MetadataTest extends PluginTest {

    private String results = "metadata_results.txt";

    @Before
    @Override
    public void setUp() {
        super.setUp();
        instance = new Metadata();
        instance.setMetadata(metadata);

        PipedInputStream pipeIn = null;
        try {
            PipedOutputStream pipeOut = new PipedOutputStream();
            pipeIn = new PipedInputStream(pipeOut);
            PrintStream ps = new PrintStream(pipeOut);
            System.setOut(ps);
        } catch (IOException ex) {
            Log.fatal("IOException " + results, ex);
        } finally {
            try {
                pipeIn.close();
            } catch (IOException ex) {
                Logger.getLogger(MetadataTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public MetadataTest() {
    }

    @Test
    public void testListAllTables() {
        launchPlugin("--list-tables");
        examineFile(results, 6, 0, 0);
    }

    @Test
    public void testListStudyFields() {
        launchPlugin("--table", "study", "--list-fields");
        Assert.fail();
    }

    @Test
    public void testListExperimentFields() {
        launchPlugin("--table", "experiment", "--list-fields");
        Assert.fail();
    }

    @Test
    public void testListSampleFields() {
        launchPlugin("--table", "sample", "--list-fields");
        Assert.fail();
    }

    @Test
    public void testListSequencerRunFields() {
        launchPlugin("--table", "sequencer_run", "--list-fields");
        Assert.fail();
    }

    @Test
    public void testListLaneFields() {
        launchPlugin("--table", "lane", "--list-fields");
        Assert.fail();
    }

    @Test
    public void testListIUSFields() {
        launchPlugin("--table", "ius", "--list-fields");
        Assert.fail();
    }

    @Test
    public void testMatcher() {
        String string = "SWID: 12345";
        Matcher match = Pattern.compile("SWID: ([\\d]+)").matcher(string);
        System.out.println(match.find());

    }

    ////////////////////////////////////////////////////////////////////////////
    /**
     * Checks the file to see if it has the expected number of rows, columns,
     * and has less nulls per line than the expected maximum.
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
    @Rule
    public TestRule watchman = new TestWatcher() {

        @Override
        protected void failed(Throwable e, Description d) {
            String newName = results + "_failed" + d.getMethodName() + ".csv";
            File file = new File(results);
            if (file.exists()) {
                file.renameTo(new File(newName));
            }
            Log.error(d + ": text report moved to " + newName);
        }

        @Override
        protected void succeeded(Description d) {
            File file = new File(results);
            if (file.exists()) {
                Assert.assertTrue("Could not delete file " + results, file.delete());
            }

        }
    };
}
