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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.module.ReturnValue;
import static net.sourceforge.seqware.pipeline.plugins.PluginTest.metadata;
import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * Runs the tests for the GenericMetadataSaver plugin 
 * 
 * @author dyuen
 */
public class GenericMetadataSaverTest extends PluginTest {

    private ByteArrayOutputStream outStream = null;
    private ByteArrayOutputStream errStream = null;
    private Pattern swidPattern = Pattern.compile("SWID: ([\\d]+)");
    private Pattern errorPattern = Pattern.compile("ERROR|error|Error|FATAL|fatal|Fatal|WARN|warn|Warn");
    private PrintStream systemErr = System.err;
    private PrintStream systemOut = System.out;

    @Before
    @Override
    public void setUp() {
        super.setUp();        
        instance = new Metadata();
        instance.setMetadata(metadata);

        outStream = new ByteArrayOutputStream();
        errStream = new ByteArrayOutputStream();
        PrintStream pso = new PrintStream(outStream);
        PrintStream pse = new PrintStream(errStream) {

            @Override
            public PrintStream append(CharSequence csq) {
//                systemErr.append(csq);
                return super.append(csq);
            }

            @Override
            public void print(String s) {
//                systemErr.print(s);
                super.print(s);
            }
        };
        System.setOut(pso);
        System.setErr(pse);
    }

    @Override
    public void tearDown() {
        super.tearDown();
    }

    public GenericMetadataSaverTest() {
    }

    private String getOut() {
        return parsePrintStream(outStream);
    }

    private String getErr() {
        return parsePrintStream(errStream);
    }

    private String parsePrintStream(ByteArrayOutputStream stream) {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = null;
        try {
            ByteArrayInputStream inStream = new ByteArrayInputStream(stream.toByteArray());
            r = new BufferedReader(new InputStreamReader(inStream));

            String s = r.readLine();
            while (s != null) {
                s = s.trim();
                //remove any blank lines
                if (s.isEmpty()) {
                    s = r.readLine();
                    continue;
                }
                if (s.endsWith("[")) {
                    while (s != null && !s.contains("]")) {
                        sb.append(s);
                        s = r.readLine();
                    }
                }
                sb.append(s).append("\n");
                s = r.readLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(GenericMetadataSaverTest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (r != null) {
                try {
                    r.close();
                } catch (IOException ex) {
                    systemErr.println("Couldn't close System.out reader" + ex.getMessage());
                }
            }
        }

        return sb.toString();
    }

    private void checkErrors(String s) {
        Matcher matcher = errorPattern.matcher(s);
        systemErr.println("~~~~~~~~~~" + s);
        Assert.assertFalse("Output contains errors:" + s, matcher.find());
//        systemErr.println("~~~~~~~~~~"+matcher.group());

    }

    private void checkFields(Map<String, String> expectedFields) {
        String out = getOut();
        for (String s : out.split("\n")) {
            String[] tokens = s.split("\t");
            Assert.assertTrue("Unknown field exists: " + s, expectedFields.containsKey(tokens[0]));
            Assert.assertEquals("Field has different parameter type than expected", expectedFields.get(tokens[0]), tokens[1]);
        }
    }

    

    @Test
    public void testMatcher() {
        String string = "[SeqWare Pipeline] ERROR [2012/11/01 15:53:51] | "
                + "MetadataWS.findObject with search string /288023 encountered error "
                + "Internal Server Error\nExperiment: null\nSWID: 6740";
        Matcher match = swidPattern.matcher(string);
        Assert.assertTrue(match.find());
        Assert.assertEquals("6740", match.group(1));
        match = errorPattern.matcher(string);
        Assert.assertTrue(match.find());
        Assert.assertEquals("ERROR", match.group(0));

    }
    
    @Test
    public void testCreateWorkflowRunWrongWorkflowFail() {
        instance.setParams(Arrays.asList("--table", "workflow_run", "--create",
                "--field", "workflow_accession::100000",
                "--field", "status::completed"));
        String s = getOut();
        checkExpectedIncorrectParameters();
    }
    
    @Test
    public void testCreateWorkflowRunWithFiles() {
        launchPlugin("--table", "workflow_run", "--create",
                "--field", "workflow_accession::4",
                "--field", "status::completed",
                "--file","cool_algorithm1::adamantium/gzip::/datastore/adamantium.gz",
                "--file","hot_algorithm1::corbomite/gzip::/datastore/corbomite.gz");
        String s = getOut();
        String swid = getAndCheckSwid(s);
        int integer = Integer.valueOf(swid);
        // check that file records were created correctly and linked in properly, 0.13.13.6.x does not have access to TestDatabaseCreator, so 
        // let's try some workflow run reporter parsing
        WorkflowRun workflowRun = metadata.getWorkflowRun(integer);
        String workflowRunReport = metadata.getWorkflowRunReport(integer);
        Assert.assertTrue("could not find workflowRun", workflowRun != null && workflowRun.getSwAccession() == integer);
        Assert.assertTrue("could not find files", workflowRunReport.contains("/datastore/adamantium.gz") && workflowRunReport.contains("/datastore/corbomite.gz"));
    }

    ////////////////////////////////////////////////////////////////////////////
    private String getAndCheckSwid(String s) throws NumberFormatException {
        Matcher match = swidPattern.matcher(s);
        Assert.assertTrue("SWID not found in output.", match.find());
        String swid = match.group(1);
        Assert.assertFalse("The SWID was empty", swid.trim().isEmpty());
        Integer.parseInt(swid.trim());
        return swid;
    }
    @Rule
    public TestRule watchman = new TestWatcher() {
        //This doesn't catch logs that are sent to Log4J

        @Override
        protected void succeeded(Description d) {
            // do not fail on tests that intend on failing
            if (!d.getMethodName().endsWith("Fail")) {
                checkErrors(getErr());
                checkErrors(getOut());
            }
        }
    };

    /**
     * Run an instance with an error and/or failure expected
     */
    private void checkExpectedFailure() {
        checkReturnValue(ReturnValue.SUCCESS, instance.parse_parameters());
        checkReturnValue(ReturnValue.SUCCESS, instance.init());
        checkReturnValue(ReturnValue.FAILURE, instance.do_run());
    }

    /**
     * Run an instance with incorrect parameters expected.
     */
    private void checkExpectedIncorrectParameters() {
        checkReturnValue(ReturnValue.SUCCESS, instance.parse_parameters());
        checkReturnValue(ReturnValue.SUCCESS, instance.init());
        checkReturnValue(ReturnValue.INVALIDPARAMETERS, instance.do_run());
    }
}
