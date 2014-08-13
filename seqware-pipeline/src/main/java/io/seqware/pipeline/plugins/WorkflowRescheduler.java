package io.seqware.pipeline.plugins;

import com.google.common.collect.Lists;
import io.seqware.Engines;
import io.seqware.common.model.WorkflowRunStatus;
import io.seqware.pipeline.SqwKeys;
import io.seqware.pipeline.api.Scheduler;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import joptsimple.ArgumentAcceptingOptionSpec;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.module.ReturnValue.ExitStatus;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import org.apache.commons.io.FileUtils;
import org.openide.util.lookup.ServiceProvider;

/**
 * The Workflow Rescheduler can schedule a new workflow based on the configuration of a previously launched workflow.
 * 
 * This will typically be used to re-schedule failed workflow runs that should be re-run totally from scratch. A new workflow will be
 * re-scheduled using the same parameters that a specified workflow-run used.
 * 
 * @author dyuen
 * @version 1.1.0
 */
@ServiceProvider(service = PluginInterface.class)
public class WorkflowRescheduler extends Plugin {

    public static final String INPUT_FILES = "input-files";
    private final ArgumentAcceptingOptionSpec<String> workflowEngineSpec;
    private final ArgumentAcceptingOptionSpec<String> hostSpec;
    private final ArgumentAcceptingOptionSpec<String> outFileSpec;
    private final ArgumentAcceptingOptionSpec<String> workflowRunSpec;

    public WorkflowRescheduler() {
        super();
        parser.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");
        this.hostSpec = parser.acceptsAll(Arrays.asList("host", "ho"), "Used to schedule onto a specific host").withRequiredArg()
                .ofType(String.class);
        this.workflowRunSpec = parser
                .acceptsAll(Arrays.asList("workflow-run", "wr"),
                        "Required: specify workflow-run(s) by swid, comma-delimited, to re-schedule").withRequiredArg().required()
                .withValuesSeparatedBy(",");
        this.workflowEngineSpec = WorkflowScheduler.createWorkflowEngineSpec(parser);
        this.outFileSpec = parser.acceptsAll(Arrays.asList("out"), "Optional: Will output a workflow-run by sw_accession")
                .withRequiredArg();
    }

    private String getEngineParam() {
        String engine = options.valueOf(workflowEngineSpec);
        if (engine == null) {
            engine = config.get(SqwKeys.SW_DEFAULT_WORKFLOW_ENGINE.getSettingKey());
        }
        if (engine == null) {
            engine = Engines.DEFAULT_ENGINE;
        }

        return engine;
    }

    @Override
    public ReturnValue init() {
        if (options.has(workflowEngineSpec)) {
            return validateEngineString(options.valueOf(workflowEngineSpec));
        }
        return new ReturnValue(ExitStatus.SUCCESS);
    }

    public static ReturnValue validateEngineString(String engine) {
        if (!Engines.ENGINES.contains(engine)) {
            Log.error("Invalid workflow-engine value. Must be one of: " + Engines.ENGINES_LIST);
            return new ReturnValue(ExitStatus.INVALIDARGUMENT);
        }
        return new ReturnValue(ExitStatus.SUCCESS);
    }

    /*
     */
    @Override
    public ReturnValue do_test() {
        return new ReturnValue(ExitStatus.SUCCESS);
    }

    @Override
    public ReturnValue clean_up() {
        return new ReturnValue(ExitStatus.SUCCESS);
    }

    @Override
    public String get_description() {
        return "A plugin that lets you re-schedule previously launched workflow runs.";
    }

    @Override
    public ReturnValue do_run() {
        Scheduler w = new Scheduler(metadata, config);
        try {
            File outputFile = null;
            if (options.has(this.outFileSpec)) {
                outputFile = new File(options.valueOf(this.outFileSpec));
            }

            if (options.has(workflowRunSpec)) {

                List<String> workflowRunSWIDs = options.valuesOf(this.workflowRunSpec);
                for (String runSWID : workflowRunSWIDs) {

                    WorkflowRun oldWorkflowRun = metadata.getWorkflowRun(Integer.valueOf(runSWID));
                    // extract a workflow ini from the previous run
                    String iniFile = oldWorkflowRun.getIniFile();
                    Path tempFile = Files.createTempFile("workflow", "ini");
                    Log.debug("Writing previous ini to " + tempFile.toString());
                    Files.write(tempFile, Lists.newArrayList(iniFile), Charset.defaultCharset());
                    // parent accessions should be already present in ini, so we do not need to extract this
                    // create a new workflow run
                    int newWorkflowRunID = this.metadata.add_workflow_run(oldWorkflowRun.getWorkflowAccession());
                    // this translation here is ugly, do we still need to do this?
                    int workflowRunAccessionInt = this.metadata.get_workflow_run_accession(newWorkflowRunID);
                    WorkflowRun newWorkflowRun = metadata.getWorkflowRun(workflowRunAccessionInt);

                    // here, we could reverse-engineer parent links and re-create them
                    // but I'd rather just deprecate direct links from workflow runs to lane and ius though

                    // have the old workflow run mimic the new one and upload for re-launching
                    oldWorkflowRun.setWorkflowRunId(newWorkflowRunID);
                    oldWorkflowRun.setSwAccession(workflowRunAccessionInt);
                    oldWorkflowRun.setCreateTimestamp(newWorkflowRun.getCreateTimestamp());
                    oldWorkflowRun.setStatus(WorkflowRunStatus.submitted);
                    // null out stuff that doesn't make sense to copy over
                    oldWorkflowRun.setStatusCmd(null);
                    oldWorkflowRun.setCurrentWorkingDir(null);
                    oldWorkflowRun.setDax(null);
                    oldWorkflowRun.setStdOut(null);
                    oldWorkflowRun.setStdErr(null);

                    // override host and engine if needed
                    if (options.has(hostSpec)) {
                        String host = options.valueOf(hostSpec);
                        oldWorkflowRun.setHost(host);
                    }
                    if (options.has(workflowEngineSpec)) {
                        String engine = getEngineParam();
                        oldWorkflowRun.setWorkflowEngine(engine);
                    }

                    Log.info("You are re-scheduling workflow-run " + runSWID + " to " + workflowRunAccessionInt);
                    this.metadata.updateWorkflowRun(oldWorkflowRun);

                    if (options.has(outFileSpec)) {
                        FileUtils.write(outputFile, String.valueOf(newWorkflowRun) + "\n", true);
                    }
                }

            } else {
                Log.error("I don't understand the combination of arguments you gave!");
                Log.info(this.get_syntax());
                return new ReturnValue(ExitStatus.INVALIDARGUMENT);
            }
        } catch (IOException ex) {
            return new ReturnValue(ExitStatus.FILENOTWRITABLE);
        }
        return new ReturnValue();
    }
}
