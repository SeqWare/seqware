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
import java.util.Properties;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import junit.framework.Assert;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.output.ByteArrayOutputStream;

/**
 *
 * @author dyuen
 */
public class ITUtility {
    
    /**
     * Run the new SeqWare simplified command-line script given a particular set of parameters and check for an 
     * expected return value.
     * 
     * @param parameters 
     * @param expectedReturnValue this will be checked via a JUnit assert
     * @param workingDir
     * @return
     * @throws IOException 
     */
    public static String runSeqwareCLI(String parameters, int expectedReturnValue, File workingDir) throws IOException{
        File script = retrieveCompiledSeqwareScript();
        script.setExecutable(true);
                
        if (workingDir == null){
            workingDir = Files.createTempDir();
            workingDir.deleteOnExit();
        }
 
        String line = "bash " + script.getAbsolutePath() + " " + parameters;
        String output = runArbitraryCommand(line, expectedReturnValue, workingDir);
        return output;
    }
    
    /**
     * Run the SeqWare jar given a particular set of parameters and check for an
     * expected return value.
     *
     * The beauty of this approach is that we can later move this out into its
     * own class, create an interface, and then we can reuse the same tests
     * running against our code directly rather than through a jar, so we can
     * compute code coverage and the like.
     *
     * @param parameters
     * @param expectedReturnValue
     * @param workingDir null, if caller does not care about the working directory
     * @return 
     * @throws IOException
     */
    public static String runSeqWareJar(String parameters, int expectedReturnValue, File workingDir) throws IOException {
        File jar = retrieveFullAssembledJar();
        
        Properties props = new Properties();
        props.load(ITUtility.class.getClassLoader().getResourceAsStream("project.properties"));
        String itCoverageAgent = (String) props.get("itCoverageAgent");
        
        if (workingDir == null){
            workingDir = Files.createTempDir();
            workingDir.deleteOnExit();
        }
        
        String line = "java "+itCoverageAgent+" -jar " + jar.getAbsolutePath() + " " + parameters;
        String output = runArbitraryCommand(line, expectedReturnValue, workingDir);
        return output;
    }

    /**
     *
     * @param seqTargetDir the value of seqTargetDir
     */
    private static File searchForFullJar(File seqTargetDir) {
        File targetFullJar = null;
        for (File files : seqTargetDir.listFiles()) {
            if (files.getName().contains("full") && !files.getName().contains("-qe-")) {
                targetFullJar = files;
            }
        }
        return targetFullJar;
    }

    private static boolean isRootOfSeqWare(File workingDirectory) {
        String[] list = workingDirectory.list();
        for (String file : list) {
            if (file.contains("seqware-distribution")) {
                return true;
            }
        }
        return false;
    }

    /**
     * This is the hackiest hack in the universe of hacks for getting the final
     * assembly jar so I can run tests.
     *
     * There has got to be a better way of getting the path of the assembled jar
     * via maven properties or some such. But I really need something to help me
     * run tests on production bundles
     *
     */
    protected static File retrieveFullAssembledJar() {
        String workingDir = System.getProperty("user.dir");
        File workingDirectory = new File(workingDir);
        File targetFullJar = searchForFullJar(workingDirectory);
        if (targetFullJar != null) {
            return targetFullJar;
        }
        while (!isRootOfSeqWare(workingDirectory)) {
            workingDirectory = workingDirectory.getParentFile();
        }
        File seqDistDir = new File(workingDirectory, "seqware-distribution");
        File seqTargetDir = new File(seqDistDir, "target");
        targetFullJar = searchForFullJar(seqTargetDir);
        return targetFullJar;
    }

    /**
     * Run an arbitrary command and check it against an expected return value
     *
     * @param line
     * @param expectedReturnValue
     * @param dir working directory, can be null if you don't want to change directories
     * @return
     * @throws IOException
     */
    public static String runArbitraryCommand(String line, int expectedReturnValue, File dir) throws IOException {
        Log.info("Running " + line);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CommandLine commandline = CommandLine.parse(line);
        DefaultExecutor exec = new DefaultExecutor();
        if (dir != null) {
            exec.setWorkingDirectory(dir);
        }
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
        exec.setStreamHandler(streamHandler);
        exec.setExitValue(expectedReturnValue);
        try {
            int exitValue = exec.execute(commandline);
            Assert.assertTrue("exit value for full jar with no params should be " + expectedReturnValue + " was " + exitValue, exitValue == expectedReturnValue);
            String output = outputStream.toString();
            return output;
        } catch (ExecuteException e) {
            Log.error("Execution failed with:");
            Log.error(outputStream.toString());
            throw e;
        }
    }

    public static String extractValueFrom(String listOutput, String key) {
        String[] lines = listOutput.split(System.getProperty("line.separator"));
        for (String line : lines) {
            if (line.startsWith(key)) {
                String[] parts = line.split(" ");
                return parts[parts.length - 1];
            }
        }
        return null;
    }

    public static String getLocalhost() {
        OptionParser parser = new OptionParser();
        OptionSet set = parser.parse("");
        String localhost = FileTools.getLocalhost(set).hostname;
        return localhost;
    }

    /**
     * Grab the location of the compiled seqware script.
     * 
     * This uses Maven system properties to pass variables from Maven into Java
     * 
     * @return script file
     */
    private static File retrieveCompiledSeqwareScript() {
        String property = System.getProperty("cliPath");
        File seqwareScript = new File(property);
        return seqwareScript;
    }
}
