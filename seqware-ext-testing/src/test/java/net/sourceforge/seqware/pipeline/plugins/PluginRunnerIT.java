package net.sourceforge.seqware.pipeline.plugins;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import junit.framework.Assert;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.runner.PluginRunner;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.SerializationUtils;

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
/**
 *
 * @author dyuen
 */
public class PluginRunnerIT {

    private static File tempDir = null;
    private static Map<String, Integer> installedWorkflows = new HashMap<String, Integer>();
    private static Map<String, File> bundleLocations = new HashMap<String, File>();
    private final static boolean DEBUG_SKIP = true;

    @BeforeClass
    public static void createAndInstallArchetypes() throws IOException {
        File bundleFile = new File(System.getProperty("java.io.tmpdir"), "PluginRunnerIT_bundleLocations.bin");
        File installedWorkflowsFile = new File(System.getProperty("java.io.tmpdir"), "PluginRunnerIT_installedWorkflows.bin");
        if (DEBUG_SKIP) {
            if (bundleFile.exists() && installedWorkflowsFile.exists()) {
                byte[] bundleLocationsBinary = Files.toByteArray(bundleFile);
                byte[] installedWorkflowsBinary = Files.toByteArray(installedWorkflowsFile);
                bundleLocations = (Map<String, File>) SerializationUtils.deserialize(bundleLocationsBinary);
                installedWorkflows = (Map<String, Integer>) SerializationUtils.deserialize(installedWorkflowsBinary);
                return;
            }
        }
        
        tempDir = Files.createTempDir();
        Log.info("Trying to build and test archetypes at: " + tempDir.getAbsolutePath());
        PluginRunner it = new PluginRunner();
        String SEQWARE_VERSION = it.getClass().getPackage().getImplementationVersion();
        Assert.assertTrue("unable to detect seqware version", SEQWARE_VERSION != null);
        Log.info("SeqWare version detected as: " + SEQWARE_VERSION);

        // for all tests, we're going to need to create and install our basic archetypes
        String[] archetypes = {"java-workflow", "simplified-ftl-workflow", "legacy-ftl-workflow", "simple-legacy-ftl-workflow"};
        for (String archetype : archetypes) {
            String workflow = "seqware-archetype-" + archetype;
            // generate and install archetypes to local maven repo
            String command = "mvn archetype:generate -DarchetypeCatalog=local -Dpackage=com.seqware.github -DgroupId=com.github.seqware -DarchetypeArtifactId=" + workflow + " -Dversion=1.0-SNAPSHOT -DarchetypeGroupId=com.github.seqware -DartifactId=" + workflow + " -DworkflowDirectoryName=" + workflow + " -DworkflowName=" + workflow + " -DworkflowVersion=1.0-SNAPSHOT -B -Dgoals=install";
            String genOutput = PluginRunnerIT.runArbitraryCommand(command, 0, tempDir);
            Log.info(genOutput);
            // install the workflows to the database and record their information 
            File workflowDir = new File(tempDir, workflow);
            File targetDir = new File(workflowDir, "target");
            File bundleDir = new File(targetDir, "Workflow_Bundle_" + workflow + "_1.0-SNAPSHOT_SeqWare_" + SEQWARE_VERSION);
            
            bundleLocations.put(workflow, bundleDir);

            String installCommand = "-p net.sourceforge.seqware.pipeline.plugins.BundleManager -verbose -- -i -b " + bundleDir.getAbsolutePath();
            String installOutput = PluginRunnerIT.runSeqWareJar(installCommand, ReturnValue.SUCCESS);
            Log.info(installOutput);     
            
            String[] lines = installOutput.split(System.getProperty("line.separator"));
            for (String line : lines) {
                if (line.startsWith("WORKFLOW_ACCESSION:")){
                    String[] parts = line.split(" ");
                    int accession = Integer.valueOf(parts[parts.length-1]);
                    installedWorkflows.put(workflow, accession);
                    Log.info("Found workflow " + workflow + " with accession " + accession);
                }
            }
        }
        Assert.assertTrue("could not locate installed workflows", installedWorkflows.size() == archetypes.length);
        Assert.assertTrue("could not locate installed workflow paths", installedWorkflows.size() == bundleLocations.size());
        
        if (DEBUG_SKIP){
        // dump data to a permanent map just in case we want to re-run tests without waiting
            byte[] bundleLocationsBinary = SerializationUtils.serialize(bundleLocations);
            byte[] installedWorkflowsBinary = SerializationUtils.serialize(installedWorkflows);
            Files.write(bundleLocationsBinary, bundleFile);
            Files.write(installedWorkflowsBinary, installedWorkflowsFile);
        }
        
    }

    @AfterClass
    public static void cleanup() throws IOException {
        if (!DEBUG_SKIP){
            tempDir.deleteOnExit();
        }
    }

    private static File searchForFullJar(File seqTargetDir) {
        File targetFullJar = null;
        for (File files : seqTargetDir.listFiles()) {
            if (files.getName().contains("full") && !files.getName().contains("-qe-")) {
                targetFullJar = files;
            }
        }
        return targetFullJar;
    }
    
    @Test
    public void testListingBundles() throws IOException {
        for(Entry<String, File> e : bundleLocations.entrySet()){
            Log.info("Attempting to list " + e.getKey());
            String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.BundleManager -- -l -b " + e.getValue().getAbsolutePath();
            String listOutput = PluginRunnerIT.runSeqWareJar(listCommand, ReturnValue.SUCCESS);
            Log.info(listOutput);     
        }
    }
    
    @Test 
    public void testExportParameters() throws IOException{
        Map<String, File> iniParams = exportWorkflowInis();
        Assert.assertTrue("Loaded correct number of ini files", iniParams.size() == installedWorkflows.size());
    }
    
    @Test
    public void testScheduleAndLaunch() throws IOException{
        Map<String, File> iniParams = exportWorkflowInis();
       
    }

    @Test
    public void testBasicPluginRunner() throws IOException {
        String output = runSeqWareJar("", ReturnValue.INVALIDARGUMENT);
        Assert.assertTrue("output should include usage and no Exceptions", output.contains("Syntax:") && !output.contains("Exception"));
    }

    @Test
    public void testBasicMetadataRetrieval() throws IOException {
        String output = runSeqWareJar(" -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --list-tables", ReturnValue.SUCCESS);
        Assert.assertTrue("output should include table names", output.contains("TableName") && output.contains("study") && output.contains("experiment"));
    }
    
    public static void main(String[] args) throws IOException {
        PluginRunnerIT it = new PluginRunnerIT();
        it.testLatestWorkflows();
    }

    @Test
    public void testLatestWorkflows() throws IOException {
        String output = runSeqWareJar("-p net.sourceforge.seqware.pipeline.plugins.BundleManager -- --list-installed", ReturnValue.SUCCESS);
        Assert.assertTrue("output should include installed workflows", output.contains("INSTALLED WORKFLOWS"));
        Map<String, WorkflowInfo> latestWorkflows = new HashMap<String, WorkflowInfo>();
        String[] lines = output.split(System.getProperty("line.separator"));
        for (String line : lines) {
            String[] lineParts = line.split("\t");
            try {
                int workflow_accession = Integer.valueOf(lineParts[3]);
                String workflowName = lineParts[0];
                String path = lineParts[4];
                if (path.equals("null")) {
                    continue;
                }
                WorkflowInfo wi = new WorkflowInfo(workflow_accession, path, workflowName, lineParts[1]);
                
                //TODO: check that the permanent workflow actually exists, if not warn and skip
                File fileAtPath = new File(path);
                if (!fileAtPath.exists()){
                    Log.warn("Skipping " + workflowName + ":" + workflow_accession + " , bundle path does not exist at " + path);
                    continue;
                }
                
                if (!latestWorkflows.containsKey(workflowName)) {
                    latestWorkflows.put(workflowName, wi);
                } else {
                    // contained
                    int old = latestWorkflows.get(workflowName).sw_accession;
                    if (workflow_accession > old) {
                        latestWorkflows.put(workflowName, wi);
                    }
                }
            } catch (Exception e) {
                /**
                 * do nothing
                 */
            }
        }
        // list the newest workflows that we encountered
        // go ahead and test them 
        for (Entry<String, WorkflowInfo> e : latestWorkflows.entrySet()) {
            System.out.println("Testing " + e.getKey() + " " + e.getValue().sw_accession);
            StringBuilder params = new StringBuilder();
            params.append("--bundle ").append(e.getValue().path).append(" ");
            params.append("--version ").append(e.getValue().version).append(" ");
            params.append("--test ");
            String tOutput = runSeqWareJar("-p net.sourceforge.seqware.pipeline.plugins.BundleManager -- " + params.toString(), ReturnValue.SUCCESS);
            System.out.println(tOutput);
        }
    }

    private Map<String, File> exportWorkflowInis() throws IOException {
        Map<String, File> iniParams = new HashMap<String, File>();
        for(Entry<String, Integer> e : installedWorkflows.entrySet()){
            Log.info("Attempting to export parameters for  " + e.getKey());
            String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.BundleManager -- --list-workflow-params --workflow-accession " + e.getValue();
            String listOutput = PluginRunnerIT.runSeqWareJar(listCommand, ReturnValue.SUCCESS);
            Log.info(listOutput);     
            // go through output and dump out the workflow.ini
            File workflowIni = File.createTempFile("workflow", "ini");
            String[] lines = listOutput.split(System.getProperty("line.separator"));
            PrintWriter out = new PrintWriter(new FileWriter(workflowIni)); 
            
            for (String line : lines) {
                if (line.startsWith("-") || line.startsWith("=") || line.startsWith("$") || line.startsWith("Running Plugin") || line.startsWith("Setting Up Plugin")){
                    continue;
                } 
                out.println(line);
            }
            out.close();
            iniParams.put(e.getKey(), workflowIni);
        }
        return iniParams;
    }

    private class WorkflowInfo {

        public int sw_accession;
        public String path;
        public String name;
        public String version;

        public WorkflowInfo(int sw_accession, String path, String name, String version) {
            this.sw_accession = sw_accession;
            this.path = path;
            this.name = name;
            this.version = version;
        }
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
     * @return
     * @throws IOException
     */
    public static String runSeqWareJar(String parameters, int expectedReturnValue) throws IOException {
        File jar = retrieveFullAssembledJar();
        String line = "java -jar " + jar.getAbsolutePath() + " " + parameters;
        String output = runArbitraryCommand(line, expectedReturnValue, null);
        return output;
    }

    /**
     * This is the hackiest hack in the universe of hacks for getting the final
     * assembly jar so I can run tests.
     *
     * There has got to be a better way of getting the path of the assembled jar
     * via maven properties or some such. But I really need something to help me
     * run tests on production bundles
     */
    public static File retrieveFullAssembledJar() {

        // this does not work, it gets the pipeline jar instead of the full jar
//        File jarFile  = PluginRunnerIT.getCodeSource(PluginRunner.class);
//        Log.info("Jarfile was located at " + jarFile.getAbsolutePath());

        String workingDir = System.getProperty("user.dir");
        File workingDirectory = new File(workingDir);
        File targetFullJar = searchForFullJar(workingDirectory);
        if (targetFullJar != null){
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
     * Method returns code source of given class. This is URL of classpath
     * folder, zip or jar file. If code source is unknown, returns null (for
     * example, for classes java.io.*).
     *
     * Edited from
     * http://asolntsev.blogspot.ca/2008/03/how-to-find-which-jar-file-contains.html
     * This is extremely close to what we need to identify the full.jar, however
     * right now it returns the pipeline jar in the maven repo rather than the
     * full jar, which I suspect is an issue with how I've defined dependencies
     * in Maven rather than a issueJava.
     *
     * @param clazz For example, java.sql.SQLException.class
     * @return for example, "file:/C:/jdev10/jdev/mywork/classes/" or
     * "file:/C:/works/projects/classes12.zip"
     */
    public static File getCodeSource(Class clazz) {
        if (clazz == null
                || clazz.getProtectionDomain() == null
                || clazz.getProtectionDomain().getCodeSource() == null
                || clazz.getProtectionDomain().getCodeSource().getLocation() == null) // This typically happens for system classloader
        // (java.lang.* etc. classes)
        {
            Log.error("Could not access protection domainon " + clazz.getName());
            return null;
        }

        URI uri = null;
        try {
            uri = clazz.getProtectionDomain().getCodeSource().getLocation().toURI();
        } catch (URISyntaxException ex) {
            Log.error("Could not determine location of " + clazz.getName());
            return null;
        }
        if (uri != null) {
            File file = new File(uri);
            return file;
        }
        Log.error("Could not translate URI location of " + clazz.getName());
        return null;
    }

    /**
     * Run an arbitrary command and check it against an expected return value
     *
     * @param line
     * @param expectedReturnValue
     * @param dir
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
}
