package io.seqware.pipeline.plugins;

import io.seqware.Engines;
import java.util.Arrays;
import joptsimple.NonOptionArgumentSpec;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import org.openide.util.lookup.ServiceProvider;

/**
 * 
 * The WorkflowLifecycle is responsible for performing aggregations of tasks.
 * 
 * Specifically, it will install a bundle, schedule a bundle, launch the bundle, and watch it thus replicating the current lifecycle of
 * WorkflowLauncher in a modular fashion for testing and development purposes.
 * 
 * @author dyuen
 */
@ServiceProvider(service = PluginInterface.class)
public class WorkflowLifecycle extends Plugin {

    public static final String FORCE_HOST = "force-host";
    public static final String HOST = "host";
    public static final String LAUNCH_SCHEDULED = "launch-scheduled";
    public static final String WAIT = "wait";
    public static final String INPUT_FILES = "input-files";

    protected ReturnValue ret = new ReturnValue();
    // NOTE: this is shared with WorkflowStatusChecker so only one can run at a
    // time
    protected String appID = "net.sourceforge.seqware.pipeline.plugins.WorkflowStatusCheckerOrLauncher";
    private String hostname;
    private final NonOptionArgumentSpec<String> nonOptionSpec;

    public WorkflowLifecycle() {
        super();
        /*
         * You should specify --workflow --version and --bundle or --workflow-accession since the latter will use the database to find all
         * the needed info
         */
        parser.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");
        parser.acceptsAll(
                Arrays.asList("workflow-accession", "wa"),
                "Optional: The sw_accession of the workflow that this run of a workflow should be associated with (via the workflow_id in the workflow_run_table). Specify this or the workflow, version, and bundle.")
                .withRequiredArg();
        parser.acceptsAll(
                Arrays.asList("workflow", "w"),
                "The name of the workflow to run. This must be used in conjunction with a version and bundle. Alternatively you can use a workflow-accession in place of all three for installed workflows.")
                .withRequiredArg();
        parser.acceptsAll(Arrays.asList("version", "v", "workflow-version"),
                "The workflow version to be used. You can specify this or the workflow-accession of an already installed bundle.")
                .withRequiredArg();
        parser.acceptsAll(Arrays.asList("bundle", "b", "provisioned-bundle-dir"),
                "The path to a bundle zip file. You can specify this or the workflow-accession of an already installed bundle.")
                .withRequiredArg();
        parser.acceptsAll(
                Arrays.asList("ini-files", "i"),
                "One or more ini files can be specified, these contain the parameters needed by the workflow template. Use commas without space to delimit a list of ini files.")
                .withRequiredArg();
        parser.acceptsAll(Arrays.asList(INPUT_FILES, "if"),
                "One or more input files can be specified as sw_accessions. Use commas without space to delimit a list of input files.")
                .withRequiredArg();
        parser.acceptsAll(
                Arrays.asList("no-meta-db", "no-metadata"),
                "Optional: a flag that prevents metadata writeback (which is done by default) by the WorkflowLauncher and that is subsequently passed to the called workflow which can use it to determine if they should write metadata at runtime on the cluster.");
        parser.acceptsAll(
                Arrays.asList(WAIT),
                "Optional: a flag that indicates the launcher should launch a workflow then monitor it's progress, waiting for it to exit, and returning 0 if everything is OK, non-zero if there are errors. This is useful for testing or if something else is calling the WorkflowLauncher. Without this option the launcher will immediately return with a 0 return value regardless if the workflow ultimately works.");
        parser.acceptsAll(
                Arrays.asList(FORCE_HOST, "fh"),
                "If specified, the scheduled workflow will only be launched if this parameter value and the host field in the workflow run table match. This is a mechanism to target workflows to particular servers for launching.")
                .withRequiredArg();
        parser.accepts("workflow-engine",
                "Optional: Specifies a workflow engine, one of: " + Engines.ENGINES_LIST + ". Defaults to " + Engines.DEFAULT_ENGINE + ".")
                .withRequiredArg().ofType(String.class).describedAs("Workflow Engine");
        parser.accepts("no-run", "Optional: Terminates the launch process immediately prior to running. Useful for debugging.");
        this.nonOptionSpec = parser.nonOptions(WorkflowScheduler.OVERRIDE_INI_DESC);

        ret.setExitStatus(ReturnValue.SUCCESS);
    }

    /*
     */
    @Override
    public ReturnValue init() {
        return new ReturnValue();
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
        return "A plugin that lets you install/schedule/launch/watch workflows in one fell swoop";
    }

    @Override
    public ReturnValue do_run() {
        return new ReturnValue();
    }

}
