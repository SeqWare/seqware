package net.sourceforge.seqware.pipeline.plugins;

import com.google.common.io.Files;
import io.seqware.pipeline.SqwKeys;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Assert;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.pipeline.runner.PluginRunner;
import org.apache.commons.io.FileUtils;
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
public class PluginRunnerET {

    private static File tempDir = null;

    private static Map<String, Integer> installedWorkflows = new HashMap<>();
    private static Map<String, File> bundleLocations = new HashMap<>();
    private static final List<Integer> launchedWorkflowRuns = new ArrayList<>();
    private static final boolean DEBUG_SKIP = false;
    private static final int PARENT = 4707;

    public static Map<String, Integer> getInstalledWorkflows() {
        return installedWorkflows;
    }

    /**
     * Returns a map of workflow artifactIds to their bundle path.
     * 
     * @return
     */
    public static Map<String, File> getBundleLocations() {
        return bundleLocations;
    }

    public static List<Integer> getLaunchedWorkflowRuns() {
        return launchedWorkflowRuns;
    }

    @BeforeClass
    public static void createAndInstallArchetypes() throws IOException {
        clearStaticVariables();

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
        createSharedTempDir();

        Log.info("Trying to build and test archetypes at: " + tempDir.getAbsolutePath());
        PluginRunner it = new PluginRunner();
        String SEQWARE_VERSION = it.getClass().getPackage().getImplementationVersion();
        Assert.assertTrue("unable to detect seqware version", SEQWARE_VERSION != null);
        Log.info("SeqWare version detected as: " + SEQWARE_VERSION);

        // for all tests, we're going to need to create and install our basic archetypes
        // String[] archetypes = {"java-workflow", "simplified-ftl-workflow", "legacy-ftl-workflow", "simple-legacy-ftl-workflow"};
        // starting with the 1.0.x series we are deprecating the FTL workflows and the Pegasus backend so skip testing them
        // we are now only testing Java workflows on the Oozie-* backends
        String[] archetypes = { "java-workflow" };
        buildAndInstallArchetypes(archetypes, SEQWARE_VERSION, true, true);
        Assert.assertTrue("could not locate installed workflows", installedWorkflows.size() == archetypes.length);
        Assert.assertTrue("could not locate installed workflow paths", installedWorkflows.size() == bundleLocations.size());

        if (DEBUG_SKIP) {
            // dump data to a permanent map just in case we want to re-run tests without waiting
            byte[] bundleLocationsBinary = SerializationUtils.serialize(bundleLocations);
            byte[] installedWorkflowsBinary = SerializationUtils.serialize(installedWorkflows);
            Files.write(bundleLocationsBinary, bundleFile);
            Files.write(installedWorkflowsBinary, installedWorkflowsFile);
        }
        // SEQWARE-1684 - Try to keep workflow bundle size under 8G limit
        Log.stderr(PluginRunnerET.class.getName() + " Cleaning up " + tempDir.getAbsolutePath());
    }

    @AfterClass
    public static void cleanup() throws IOException {
        monitorAndClean(true);
    }

    public static void buildAndInstallArchetypes(String[] archetypes, String SEQWARE_VERSION, boolean testListing, boolean deleteBundles)
            throws IOException, NumberFormatException {
        for (String archetype : archetypes) {
            String workflow = "seqware-archetype-" + archetype;
            String workflowName = workflow.replace("-", "");
            // generate and install archetypes to local maven repo
            String command = "mvn archetype:generate -DarchetypeCatalog=local -Dpackage=com.seqware.github -DgroupId=com.github.seqware -DarchetypeArtifactId="
                    + workflow
                    + " -Dversion=1.0-SNAPSHOT -DarchetypeGroupId=com.github.seqware -DartifactId="
                    + workflow
                    + " -Dworkflow-name=" + workflowName + " -B -Dgoals=install";
            String genOutput = ITUtility.runArbitraryCommand(command, 0, tempDir);
            Log.info(genOutput);
            // install the workflows to the database and record their information
            File workflowDir = new File(tempDir, workflow);
            File targetDir = new File(workflowDir, "target");
            final String workflow_name = "Workflow_Bundle_" + workflowName + "_1.0-SNAPSHOT_SeqWare_" + SEQWARE_VERSION;
            File bundleDir = new File(targetDir, workflow_name);

            bundleLocations.put(workflow, bundleDir);

            // zip up the bundles first if we want the bundle locations to survive
            String zipCommand = "--plugin net.sourceforge.seqware.pipeline.plugins.BundleManager -- --path-to-package "
                    + bundleDir.getAbsolutePath() + " --bundle " + tempDir.getAbsolutePath();
            String zipOutput = ITUtility.runSeqWareJar(zipCommand, ReturnValue.SUCCESS, null);
            Log.info(zipOutput);

            String installCommand = "-p net.sourceforge.seqware.pipeline.plugins.BundleManager -- -i -b " + tempDir.getAbsolutePath()
                    + File.separatorChar + workflow_name + ".zip";
            String installOutput = ITUtility.runSeqWareJar(installCommand, ReturnValue.SUCCESS, null);
            Log.info(installOutput);

            int accession = ITUtility.extractSwid(installOutput);
            installedWorkflows.put(workflow, accession);
            Log.info("Found workflow " + workflow + " with accession " + accession);

            if (testListing) {
                Log.info("Attempting to list " + workflow);
                String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.BundleManager -- -l -b " + bundleDir.getAbsolutePath();
                String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
                Log.info(listOutput);
            }
            if (deleteBundles) {
                Log.info("Attempting to delete bundle after install " + workflow);
                FileUtils.deleteDirectory(bundleDir);
            }
        }
    }

    public static void clearStaticVariables() {
        // clean-up static variables
        installedWorkflows.clear();
        bundleLocations.clear();
        launchedWorkflowRuns.clear();
        createSharedTempDir();
    }

    public static void monitorAndClean(boolean monitor) throws IOException {
        // testing monitoring one more time
        for (int launchedWorkflowRun : launchedWorkflowRuns) {
            Log.info("Attempting to monitor " + launchedWorkflowRun);
            String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.WorkflowStatusChecker -- --workflow-run-accession "
                    + launchedWorkflowRun;
            String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
            Log.info(listOutput);
        }

        if (!DEBUG_SKIP) {
            FileUtils.deleteDirectory(tempDir);
        }

        clearStaticVariables();
    }

    private static void createSharedTempDir() {
        // need to create in a shared location for seqware installs with multiple nodes
        // tempDir = Files.createTempDir();
        String parentDir = ConfigTools.getSettings().get(SqwKeys.OOZIE_WORK_DIR.getSettingKey());
        tempDir = new File(parentDir, String.valueOf(Math.abs(new Random().nextInt())));
        tempDir.mkdir();
    }

    @Test
    public void testExportParameters() throws IOException {
        Map<String, File> iniParams = exportWorkflowInis();
        Assert.assertTrue("Loaded correct number of ini files", iniParams.size() == installedWorkflows.size());
    }

    @Test
    public void testScheduleAndLaunch() throws IOException {
        Map<String, File> iniParams = exportWorkflowInis();
        String localhost = ITUtility.getLocalhost();
        Log.info("Attempting to schedule on host: " + localhost);
        Map<String, Integer> wr_accessions = new HashMap<>();

        for (Entry<String, Integer> e : installedWorkflows.entrySet()) {
            Log.info("Attempting to schedule " + e.getKey());
            String workflowPath = iniParams.get(e.getKey()).getAbsolutePath();
            String accession = Integer.toString(installedWorkflows.get(e.getKey()));

            String listCommand = "-p io.seqware.pipeline.plugins.WorkflowScheduler -- --ini-files " + workflowPath
                    + " --workflow-accession " + accession + " --parent-accessions " + PARENT + " --host " + localhost;
            String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
            Log.info(listOutput);

            int wr_accession = ITUtility.extractSwid(listOutput);
            wr_accessions.put(e.getKey(), wr_accession);
            launchedWorkflowRuns.add(wr_accession);
            Log.info("Scheduled workflow " + e.getKey() + " with accession " + wr_accession);
        }

        // launch-scheduled
        String schedCommand = "-p io.seqware.pipeline.plugins.WorkflowLauncher -- --launch-scheduled";
        String schedOutput = ITUtility.runSeqWareJar(schedCommand, ReturnValue.SUCCESS, null);
        Log.info(schedOutput);

        try {
            Log.info("Wait for launches to settle ");
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
        }

        // testing monitoring
        for (Entry<String, Integer> e : wr_accessions.entrySet()) {
            Log.info("Attempting to monitor " + e.getKey());
            String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.WorkflowStatusChecker -- --workflow-run-accession "
                    + e.getValue();
            String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
            Log.info(listOutput);
        }
    }

    @Test
    public void testLaunchingWithoutWait() throws IOException {
        Map<String, File> iniParams = exportWorkflowInis();
        String localhost = ITUtility.getLocalhost();
        Log.info("Attempting to launch without wait on host: " + localhost);
        Map<String, Integer> wr_accessions = new HashMap<>();

        for (Entry<String, Integer> e : installedWorkflows.entrySet()) {
            Log.info("Attempting to launch " + e.getKey());
            String workflowPath = iniParams.get(e.getKey()).getAbsolutePath();
            String accession = Integer.toString(installedWorkflows.get(e.getKey()));

            String listCommand = "-p io.seqware.pipeline.plugins.WorkflowLifecycle -- --ini-files " + workflowPath
                    + " --workflow-accession " + accession + " --parent-accessions " + PARENT;
            String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
            Log.info(listOutput);
        }
    }

    @Test
    public void testLaunchingWithWait() throws IOException {
        Map<String, File> iniParams = exportWorkflowInis();
        String localhost = ITUtility.getLocalhost();
        Log.info("Attempting to launch with wait on host: " + localhost);
        Map<String, Integer> wr_accessions = new HashMap<>();

        for (Entry<String, Integer> e : installedWorkflows.entrySet()) {
            Log.info("Attempting to launch " + e.getKey());
            String workflowPath = iniParams.get(e.getKey()).getAbsolutePath();
            String accession = Integer.toString(installedWorkflows.get(e.getKey()));

            String listCommand = "-p io.seqware.pipeline.plugins.WorkflowLifecycle -- --ini-files " + workflowPath
                    + " --workflow-accession " + accession + " --parent-accessions " + PARENT + " --wait";
            String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
            Log.info(listOutput);
        }
    }

    @Test
    public void testLaunchingWithWaitAndNoMetadata() throws IOException {
        Map<String, File> iniParams = exportWorkflowInis();
        String localhost = ITUtility.getLocalhost();
        Log.info("Attempting to launch with wait on host: " + localhost);
        Map<String, Integer> wr_accessions = new HashMap<>();

        for (Entry<String, Integer> e : installedWorkflows.entrySet()) {
            Log.info("Attempting to launch " + e.getKey());
            String workflowPath = iniParams.get(e.getKey()).getAbsolutePath();
            String accession = Integer.toString(e.getValue());

            String listCommand = "-p io.seqware.pipeline.plugins.WorkflowLifecycle -- --ini-files " + workflowPath
                    + " --workflow-accession " + accession + " --no-metadata --parent-accessions " + PARENT + " --wait";
            String listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
            Log.info(listOutput);
        }
    }

    public static void main(String[] args) throws IOException {
        PluginRunnerET it = new PluginRunnerET();
        List<Integer> list = new ArrayList<>();
        for (String acc : args) {
            try {
                Integer accInt = Integer.valueOf(acc);
                list.add(accInt);
            } catch (NumberFormatException e) {
                Log.stdout("NumberFormatException, skipped acc");
            }
        }
        it.testLatestWorkflowsInternal(list);
    }

    @Test
    public void testLatestWorkflows() throws IOException {
        testLatestWorkflowsInternal(new ArrayList<Integer>());
    }

    private Map<String, File> exportWorkflowInis() throws IOException {
        Map<String, File> iniParams = new HashMap<>();
        for (Entry<String, Integer> e : installedWorkflows.entrySet()) {
            File workflowIni = exportINIFile(e.getKey(), e.getValue(), false);
            iniParams.put(e.getKey(), workflowIni);
        }
        return iniParams;
    }

    public void testLatestWorkflowsInternal(List<Integer> accessions) throws IOException {
        String output = ITUtility.runSeqWareJar("-p net.sourceforge.seqware.pipeline.plugins.BundleManager -- --list-installed",
                ReturnValue.SUCCESS, null);
        Assert.assertTrue("output should include installed workflows", output.contains("INSTALLED WORKFLOWS"));
        Map<String, WorkflowInfo> latestWorkflows = new HashMap<>();
        String[] lines = output.split(System.getProperty("line.separator"));
        for (String line : lines) {
            String[] lineParts = line.split("\t");
            try {
                int workflow_accession = Integer.valueOf(lineParts[3]);
                String workflowName = lineParts[0];
                String path = lineParts[lineParts.length - 2];
                if (path.equals("null")) {
                    continue;
                }
                WorkflowInfo wi = new WorkflowInfo(workflow_accession, path, workflowName, lineParts[1]);

                // TODO: check that the permanent workflow actually exists, if not warn and skip
                File fileAtPath = new File(path);
                if (!fileAtPath.exists()) {
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
                 * do nothing and skip this line of the BundleManager output
                 */
            }
        }
        // setup thread pool
        ExecutorService threadPool = Executors.newFixedThreadPool(latestWorkflows.size());
        CompletionService<String> pool = new ExecutorCompletionService<>(threadPool);
        for (Entry<String, WorkflowInfo> e : latestWorkflows.entrySet()) {
            System.out.println("Testing " + e.getKey() + " " + e.getValue().sw_accession);

            // if we have an accession list, skip accessions that are not in it
            if (accessions.size() > 0) {
                Integer acc = e.getValue().sw_accession;
                if (!accessions.contains(acc)) {
                    System.out.println("Skipping " + e.getKey() + " " + e.getValue().sw_accession + " due to accession list");
                    continue;
                }
            }

            StringBuilder params = new StringBuilder();
            params.append("--bundle ").append(e.getValue().path).append(" ");
            params.append("--version ").append(e.getValue().version).append(" ");
            params.append("--wait ");
            params.append("--workflow ").append(e.getValue().name);
            File tempFile = File.createTempFile(e.getValue().name, ".out");
            pool.submit(new TestingThread(params.toString(), tempFile));
        }
        for (Entry<String, WorkflowInfo> e : latestWorkflows.entrySet()) {
            try {
                pool.take().get();
            } catch (InterruptedException | ExecutionException ex) {
                Log.error(ex);
            }
        }
        threadPool.shutdown();
    }

    public File exportINIFile(String name, Integer accession, boolean newCLI) throws IOException {
        String listOutput;
        if (newCLI) {
            Log.info("Attempting to export parameters for  " + name);
            File workflowIni = File.createTempFile("workflow", "ini");
            String listCommand = " workflow ini --accession " + accession + " --out " + workflowIni.getAbsolutePath();
            ITUtility.runSeqwareCLI(listCommand, ReturnValue.SUCCESS, null);
            // new command line does not go to stdout
            listOutput = FileUtils.readFileToString(workflowIni);
        } else {
            Log.info("Attempting to export parameters for  " + name);
            String listCommand = "-p net.sourceforge.seqware.pipeline.plugins.BundleManager -- --list-workflow-params --workflow-accession "
                    + accession;
            listOutput = ITUtility.runSeqWareJar(listCommand, ReturnValue.SUCCESS, null);
            Log.info(listOutput);
        }
        // go through output and dump out the workflow.ini
        File workflowIni = File.createTempFile("workflow", "ini");
        String[] lines = listOutput.split(System.getProperty("line.separator"));
        try (PrintWriter out = new PrintWriter(new FileWriter(workflowIni))) {
            for (String line : lines) {
                if (line.startsWith("-") || line.startsWith("=") || line.startsWith("$") || line.startsWith("Running Plugin")
                        || line.startsWith("Setting Up Plugin")) {
                    continue;
                }
                out.println(line);
            }
        }
        return workflowIni;
    }

    public class WorkflowInfo {

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

    private final class TestingThread implements Callable<String> {

        private final String command;
        private final File output;

        protected TestingThread(String command, File output) {
            this.command = command;
            this.output = output;
        }

        @Override
        public String call() {
            try {
                String tOutput = ITUtility.runSeqWareJar("-p io.seqware.pipeline.plugins.WorkflowLifecycle -- " + command,
                        ReturnValue.SUCCESS, null);
                Log.error(command + " completed, writing output to " + output.getAbsolutePath());
                FileUtils.write(output, tOutput);
                return tOutput;
            } catch (IOException ex) {
                Log.error("IOException while running " + command, ex);
                return null;
            }
        }

    }
}
