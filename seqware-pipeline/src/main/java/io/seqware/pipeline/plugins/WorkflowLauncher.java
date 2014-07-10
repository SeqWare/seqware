package io.seqware.pipeline.plugins;

import io.seqware.WorkflowRuns;
import io.seqware.common.model.WorkflowRunStatus;
import io.seqware.pipeline.api.WorkflowEngine;
import io.seqware.pipeline.api.WorkflowTools;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.pipeline.tools.RunLock;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.WorkflowDataModelFactory;
import net.sourceforge.seqware.pipeline.workflowV2.WorkflowV2Utility;
import org.openide.util.lookup.ServiceProvider;

/**
 * 
 * The WorkflowLauncher is responsible for launching scheduled workflows.
 * 
 * Previously, it was responsible for launching, scheduling, waiting, etc. This was the subject of refactoring in order to reduce this to
 * just launching of scheduled workflows.
 * 
 * @author boconnor
 */
@ServiceProvider(service = PluginInterface.class)
public class WorkflowLauncher extends Plugin {

    public static final String LAUNCH_SCHEDULED = "launch-scheduled";

    private String hostname;
    private final ArgumentAcceptingOptionSpec<String> launchScheduledSpec;
    private final ArgumentAcceptingOptionSpec<String> forceHostSpec;
    private final OptionSpecBuilder noRunSpec;

    public WorkflowLauncher() {
        super();
        parser.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");
        this.launchScheduledSpec = parser
                .acceptsAll(
                        Arrays.asList(LAUNCH_SCHEDULED, "ls"),
                        "Optional: If this parameter is given (which can optionally have a comma separated list of workflow run accessions) all the workflows that have been scheduled in the database will have their commands constructed and executed on this machine (thus launching those workflows). This command can only be run on a machine capable of submitting workflows (e.g. a cluster submission host!).")
                .withOptionalArg().withValuesSeparatedBy(',');
        this.forceHostSpec = parser
                .acceptsAll(
                        Arrays.asList(FileTools.FORCE_HOST, "fh"),
                        "If specified, the scheduled workflow will only be launched if this parameter value and the host field in the workflow run table match. This is a mechanism to target workflows to particular servers for launching.")
                .withRequiredArg();
        this.noRunSpec = parser.accepts("no-run",
                "Optional: Terminates the launch process immediately prior to running. Useful for debugging.");
    }

    /*
     * (non-Javadoc) @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#init()
     */
    @Override
    public ReturnValue init() {
        FileTools.LocalhostPair localhost = FileTools.getLocalhost(options);
        if (localhost.returnValue.getExitStatus() != ReturnValue.SUCCESS) {
            return (localhost.returnValue);
        } else {
            this.hostname = localhost.hostname;
        }
        return new ReturnValue();
    }

    /*
     * (non-Javadoc) @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#do_test()
     */
    @Override
    public ReturnValue do_test() {
        return new ReturnValue();
    }

    @Override
    public ReturnValue clean_up() {
        RunLock.release();
        return new ReturnValue();
    }

    @Override
    public String get_description() {
        return "A plugin that lets you launch scheduled workflow bundles.";
    }

    @Override
    public ReturnValue do_run() {
        RunLock.acquire();
        launchScheduledWorkflows();
        return new ReturnValue();

    }

    /**
     * Check whether a particular workflow run is valid on this host
     * 
     * @param wr
     * @return
     */
    private boolean isWorkflowRunValidByLocalhost(WorkflowRun wr) {
        // three conditions are
        // 1) we match with the localhost matching the host parameter in the
        // database
        final boolean localMatch = !options.has(forceHostSpec) && hostname.equals(wr.getHost());
        // 2) we match with the forcehost parameter with no parameters matching null
        // in the database
        final boolean forceHostNull = options.has(forceHostSpec) && !options.hasArgument(forceHostSpec) && wr.getHost() == null;
        // 3) we match with the forcehost parameter matching an actual value in the
        // database
        final boolean actualForceHostMatch = options.has(forceHostSpec) && hostname.equals(wr.getHost());
        return localMatch || forceHostNull || actualForceHostMatch;
    }

    /**
     * Grab valid scheduled workflows from the database and run them
     * 
     */
    private void launchScheduledWorkflows() {
        // then you are either launching all workflows scheduled in the DB
        // workflow_run table or just particular ones
        List<String> scheduledAccessions = options.valuesOf(launchScheduledSpec);

        // then need to loop over these and just launch those workflows or
        // launch all if accession not specified
        List<WorkflowRun> scheduledWorkflows = this.metadata.getWorkflowRunsByStatus(WorkflowRunStatus.submitted);

        Log.stdoutWithTime("Number of submitted workflows: " + scheduledWorkflows.size());

        for (WorkflowRun wr : scheduledWorkflows) {
            Log.stdout("Working Run: " + wr.getSwAccession());

            if (scheduledAccessions.isEmpty() && !isWorkflowRunValidByLocalhost(wr)) {
                Log.stdout("Skipping run due to host check: " + wr.getSwAccession());
                continue;
            }

            if (!scheduledAccessions.isEmpty() && !scheduledAccessions.contains(wr.getSwAccession().toString())) {
                Log.stdout("Skipping run due to accession check: " + wr.getSwAccession());
                continue;
            }

            // SEQWARE-1451
            // Workflow launcher totally dies one workflow freemarker run dies
            // let's just wrap and report these errors and fail onto the next one
            try {

                Log.stdout("Valid run by host check: " + wr.getSwAccession());
                WorkflowRun wrWithWorkflow = this.metadata.getWorkflowRunWithWorkflow(wr.getSwAccession().toString());
                boolean requiresNewLauncher = WorkflowV2Utility.requiresNewLauncher(wrWithWorkflow.getWorkflow());
                if (!requiresNewLauncher) {
                    Log.stdout("Launching via old launcher: " + wr.getSwAccession());
                    throw new RuntimeException("SeqWare no longer supports running Pegasus bundles");
                } else {
                    Log.stdout("Launching via new launcher: " + wr.getSwAccession());
                    launchNewWorkflow(options, config, metadata, wr.getWorkflowAccession(), wr.getSwAccession(), wr.getWorkflowEngine());
                }

            } catch (Exception e) {
                Log.fatal("Workflowrun launch with accession: " + wr.getSwAccession() + " failed", e);
            }
        }
    }

    private static String determineBundlePath(OptionSet options, Integer workflowAccession, Metadata metadata) {
        Map<String, String> metaInfo;
        Log.info("factory attempting to find bundle");
        if (workflowAccession != null) {
            Log.info("factory attempting to find bundle from DB");
            // this execution path is hacked in for running from the database and can be refactored into BasicWorkflow
            metaInfo = metadata.get_workflow_info(workflowAccession);
            // we've found out the bundle location as of this point
            // we need to grab the current_working_dir out
            // use it to follow the same method determining a bundle path like below, the WorkflowV2Utility.parseMetaInfo does the
            // substitution instead of BasicWorkflow in
            // Yong's code
            return metaInfo.get("current_working_dir");
        } else {
            Log.info("factory attempting to find bundle from options");
            return WorkflowV2Utility.determineRelativeBundlePath(options);
        }
    }

    /**
     * Separating out the launching of a new workflow. This way, we can eventually refactor this to the Workflow object.
     * 
     * @param options
     * @param config
     * @param metadata
     * @param workflowAccession
     * @param workflowRunAccession
     * @param workflowEngineString
     * @return
     */
    private ReturnValue launchNewWorkflow(OptionSet options, Map<String, String> config, Metadata metadata, int workflowAccession,
            int workflowRunAccession, String workflowEngineString) {
        Log.info("launching new workflow");
        ReturnValue localRet = new ReturnValue();
        AbstractWorkflowDataModel dataModel;
        try {
            final WorkflowDataModelFactory factory = new WorkflowDataModelFactory(config, metadata);
            String bundlePath = determineBundlePath(options, workflowAccession, metadata);

            dataModel = factory.getWorkflowDataModel(bundlePath, workflowAccession, workflowRunAccession, workflowEngineString);
            if (workflowEngineString != null) {
                dataModel.setWorkflow_engine(workflowEngineString);
            } else {
                // maintain consistency between the two ways of accessing the engine value
                workflowEngineString = dataModel.getWorkflow_engine();
            }
        } catch (Exception e) {
            Log.fatal("Exception constructing data model, failing workflow " + workflowRunAccession, e);
            WorkflowRuns.failWorkflow(workflowRunAccession);
            localRet.setExitStatus(ReturnValue.FAILURE);
            return localRet;
        }

        Log.info("constructed dataModel");

        // set up workflow engine
        WorkflowEngine engine = WorkflowTools.getWorkflowEngine(dataModel, config);

        engine.prepareWorkflow(dataModel);
        if (options.has(noRunSpec)) {
            return new ReturnValue(ReturnValue.SUCCESS);
        }

        Log.info("Running the workflow");
        ReturnValue localReturn = engine.runWorkflow();

        Log.info("Completing metadata tracking of workflow run");

        // metadataWriteback
        String wra = dataModel.getWorkflow_run_accession();

        if (wra == null || wra.isEmpty()) {
            return localReturn;
        }

        // int workflowrunId = Integer.parseInt(wra);
        int workflowrunaccession = Integer.parseInt(wra); // metadata.get_workflow_run_accession(workflowrunId);
        int workflowrunId = metadata.get_workflow_run_id(workflowrunaccession);
        // need to pull back the workflow run object since some fields may
        // already be set
        // and we need to use their values before writing back to the DB!
        WorkflowRun wr = metadata.getWorkflowRun(workflowrunaccession);

        String workflowRunToken = engine.getLookupToken();

        if (localReturn.getProcessExitStatus() != ReturnValue.SUCCESS || workflowRunToken == null) {
            // then something went wrong trying to call the workflow engine
            metadata.update_workflow_run(workflowrunId, dataModel.getTags().get("workflow_command"),
                    dataModel.getTags().get("workflow_template"), WorkflowRunStatus.failed, workflowRunToken, engine.getWorkingDirectory(),
                    wr.getDax(), wr.getIniFile(), wr.getHost(), localReturn.getStderr(), localReturn.getStdout(),
                    dataModel.getWorkflow_engine(), wr.getInputFileAccessions());
            return localReturn;
        } else {
            // determine status based on object model
            metadata.update_workflow_run(workflowrunId, dataModel.getTags().get("workflow_command"),
                    dataModel.getTags().get("workflow_template"), WorkflowRunStatus.pending, workflowRunToken,
                    engine.getWorkingDirectory(), wr.getDax(), wr.getIniFile(), wr.getHost(), localReturn.getStderr(),
                    localReturn.getStdout(), dataModel.getWorkflow_engine(), wr.getInputFileAccessions());
            return localRet;
        }
    }
}
