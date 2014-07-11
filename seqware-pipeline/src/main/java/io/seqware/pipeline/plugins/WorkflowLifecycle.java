package io.seqware.pipeline.plugins;

import com.google.common.collect.Lists;
import static io.seqware.pipeline.plugins.WorkflowScheduler.OVERRIDE_INI_DESC;
import static io.seqware.pipeline.plugins.WorkflowScheduler.validateEngineString;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionSpecBuilder;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.pipeline.plugins.BundleManager;
import net.sourceforge.seqware.pipeline.plugins.WorkflowStatusChecker;
import net.sourceforge.seqware.pipeline.runner.PluginRunner;
import net.sourceforge.seqware.pipeline.runner.PluginRunner.ExitException;
import org.apache.commons.io.FileUtils;
import org.openide.util.lookup.ServiceProvider;

/**
 * 
 * The WorkflowLifecycle is responsible for performing aggregations of tasks.
 * 
 * Specifically, it will install a bundle, schedule a bundle, launch the bundle, watch it, and status check it thus replicating the current
 * lifecycle of WorkflowLauncher in a modular fashion for testing and development purposes.
 * 
 * @author dyuen
 */
@ServiceProvider(service = PluginInterface.class)
public class WorkflowLifecycle extends Plugin {

    private final NonOptionArgumentSpec<String> nonOptionSpec;
    private final ArgumentAcceptingOptionSpec<String> workflowNameSpec;
    private final ArgumentAcceptingOptionSpec<String> workflowVersionSpec;
    private final ArgumentAcceptingOptionSpec<String> bundleDirSpec;
    private final OptionSpecBuilder metadataWriteBackOffSpec;
    private final OptionSpecBuilder waitSpec;
    private final ArgumentAcceptingOptionSpec<String> iniFilesSpec;
    private final ArgumentAcceptingOptionSpec<String> workflowEngineSpec;
    private String workflowRunAccession = null;
    private String workflowAccession = null;
    private final ArgumentAcceptingOptionSpec<Integer> workflowAccessionSpec;

    public WorkflowLifecycle() {
        super();
        parser.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");
        this.workflowNameSpec = parser
                .acceptsAll(
                        Arrays.asList("workflow", "w"),
                        "The name of the workflow to run. This must be used in conjunction with a version and bundle. Alternatively you can use a workflow-accession in place of all three for installed workflows.")
                .withRequiredArg().ofType(String.class);
        this.workflowVersionSpec = parser
                .acceptsAll(Arrays.asList("version", "v", "workflow-version"),
                        "The workflow version to be used. You can specify this or the workflow-accession of an already installed bundle.")
                .requiredIf(workflowNameSpec).withRequiredArg();
        this.bundleDirSpec = this.parser
                .acceptsAll(Arrays.asList("bundle", "b", "provisioned-bundle-dir"),
                        "The path to an unzipped bundle. Specify a name and version as well if the bundle contains multiple workflows.")
                .requiredIf(workflowNameSpec).withRequiredArg();

        this.workflowAccessionSpec = this.parser
                .acceptsAll(Arrays.asList("workflow-accession"),
                        "The accession for an installed workflow, must be provided unless a bundle is.")
                .requiredUnless(workflowNameSpec, workflowVersionSpec, bundleDirSpec).withRequiredArg().ofType(Integer.class);

        this.waitSpec = parser
                .acceptsAll(
                        Arrays.asList("wait"),
                        "Optional: a flag that indicates the launcher should launch a workflow then monitor it's progress, waiting for it to exit, and returning 0 if everything is OK, non-zero if there are errors. This is useful for testing or if something else is calling the WorkflowLauncher. Without this option the launcher will immediately return with a 0 return value regardless if the workflow ultimately works.");
        this.iniFilesSpec = WorkflowScheduler.createIniFileSpec(parser);
        this.workflowEngineSpec = WorkflowScheduler.createWorkflowEngineSpec(parser);
        this.metadataWriteBackOffSpec = WorkflowScheduler.createMetadataWriteBackOffSpec(parser);
        this.nonOptionSpec = parser.nonOptions(OVERRIDE_INI_DESC);

    }

    /*
     */
    @Override
    public ReturnValue init() {
        if (options.has(workflowEngineSpec)) {
            return validateEngineString(options.valueOf(workflowEngineSpec));
        }
        return new ReturnValue(ReturnValue.ExitStatus.SUCCESS);
    }

    /*
     */
    @Override
    public ReturnValue do_test() {
        return new ReturnValue();
    }

    @Override
    public ReturnValue clean_up() {
        return new ReturnValue();
    }

    @Override
    public String get_description() {
        return "A plugin that lets you (install)/schedule/launch/watch/status check workflows in one fell swoop";
    }

    @Override
    public ReturnValue do_run() {
        try {
            File tempBundleFile = File.createTempFile("bundle_manager", "out");
            tempBundleFile.deleteOnExit();
            File tempSchedulerFile = File.createTempFile("scheduler", "out");
            tempSchedulerFile.deleteOnExit();

            if (!options.has(workflowAccessionSpec)) {
                // install the workflow
                runBundleManagerPlugin(options.valueOf(this.bundleDirSpec), tempBundleFile);
            } else {
                // otherwise simulate a workflow installed by placing a sw_accession in the bundle_manager output file
                FileUtils.write(tempBundleFile, String.valueOf(options.valueOf(workflowAccessionSpec)));
            }
            // schedule the workflow
            runWorkflowSchedulerPlugin(tempBundleFile, tempSchedulerFile);
            // launch the workflow
            runWorkflowLauncherPlugin(tempSchedulerFile);
            // watch the workflow
            if (options.has(this.waitSpec)) {
                runWatcherPlugin();
            }
            // watch the workflow if necessary
        } catch (IOException e) {
            throw new ExitException(ReturnValue.FILENOTWRITABLE);
        } finally {
            runStatusCheckerPlugin();
        }
        return new ReturnValue();
    }

    private void runBundleManagerPlugin(String bundlePath, File outFile) {
        String[] bundleManagerParams = { "--install-dir-only", "--bundle", bundlePath, "--out", outFile.getAbsolutePath() };
        runPlugin(BundleManager.class, bundleManagerParams);
    }

    private void runWatcherPlugin() {
        String[] watcherParams = { "--workflow-run-accession", workflowRunAccession };
        runPlugin(WorkflowWatcher.class, watcherParams);
    }

    private void runStatusCheckerPlugin() {
        if (this.workflowRunAccession == null) {
            return;
        }
        String[] statusCheckerParams = { "--workflow-run-accession", workflowRunAccession };
        runPlugin(WorkflowStatusChecker.class, statusCheckerParams);
    }

    private void runWorkflowSchedulerPlugin(File outFile, File tempSchedulerFile) throws IOException {
        // if there is only one workflow in the file, use it. Otherwise ask for name and version
        List<String> readLines = FileUtils.readLines(outFile);
        this.workflowAccession = null;
        if (readLines.size() == 1) {
            workflowAccession = readLines.get(0);
        } else {
            for (String accession : readLines) {
                Workflow workflow = metadata.getWorkflow(Integer.valueOf(accession));
                if (workflow.getName().equals(options.valueOf(this.workflowNameSpec))
                        && workflow.getVersion().equals(options.valueOf(this.workflowVersionSpec))) {
                    workflowAccession = accession;
                }
            }
            if (workflowAccession == null) {
                Log.fatal("Unexpected output from installer " + readLines.toString());
                throw new ExitException(ReturnValue.FAILURE);
            }
        }

        String[] schedulerParams = { "--workflow-accession", workflowAccession, "--host", FileTools.getLocalhost(options).hostname,
                "--out", tempSchedulerFile.getAbsolutePath() };
        List<String> totalParams = Lists.newArrayList(schedulerParams);
        if (options.has(this.iniFilesSpec)) {
            for (String val : options.valuesOf(this.iniFilesSpec)) {
                totalParams.add("--" + this.iniFilesSpec.options().iterator().next());
                totalParams.add(val);
            }
        }
        if (options.has(this.metadataWriteBackOffSpec)) {
            totalParams.add("--" + this.metadataWriteBackOffSpec.options().iterator().next());
        }
        if (options.has(this.nonOptionSpec)) {
            for (String val : options.valuesOf(this.nonOptionSpec)) {
                totalParams.add(val);
            }
        }
        runPlugin(WorkflowScheduler.class, schedulerParams);
    }

    private void runWorkflowLauncherPlugin(File outFile) throws IOException {
        // if there is only one workflow in the file, use it. Otherwise ask for name and version
        List<String> readLines = FileUtils.readLines(outFile);
        this.workflowRunAccession = null;
        if (readLines.size() == 1) {
            workflowRunAccession = readLines.get(0);
        } else {
            Log.fatal("Unexpected output from scheduler " + readLines.toString());
            throw new ExitException(ReturnValue.FAILURE);
        }

        String[] schedulerParams = { "--launch-scheduled", workflowRunAccession };
        runPlugin(WorkflowLauncher.class, schedulerParams);
    }

    private void runPlugin(Class plugin, String[] params) {
        PluginRunner p = new PluginRunner();
        List<String> a = new ArrayList<>();
        a.add("--plugin");
        a.add(plugin.getCanonicalName());
        a.add("--");
        a.addAll(Arrays.asList(params));
        Log.stdout(Arrays.deepToString(a.toArray()));
        p.run(a.toArray(new String[a.size()]));
    }
}
