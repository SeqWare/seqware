package io.seqware.pipeline.plugins;

import io.seqware.Engines;
import io.seqware.pipeline.api.Scheduler;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.module.ReturnValue.ExitStatus;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import org.openide.util.lookup.ServiceProvider;

/**
 * The Workflow Scheduler is only responsible for scheduling workflows.
 * 
 * This is a fork of the WorkflowLauncher intended to de-tangle the functions of launching, scheduling, waiting, etc.
 * 
 * @author dyuen
 */
@ServiceProvider(service = PluginInterface.class)
public class WorkflowScheduler extends Plugin {

    public static final String INPUT_FILES = "input-files";
    private final ArgumentAcceptingOptionSpec<String> workflowEngineSpec;
    private final ArgumentAcceptingOptionSpec<String> hostSpec;
    private final ArgumentAcceptingOptionSpec<Long> inputFilesSpec;
    private final ArgumentAcceptingOptionSpec<String> iniFilesSpec;
    private final ArgumentAcceptingOptionSpec<String> linkWorkflowRunToParentsSpec;
    private final ArgumentAcceptingOptionSpec<String> reuseWorkflowRunAccessionSpec;
    private final ArgumentAcceptingOptionSpec<String> workflowAccessionSpec;
    private final ArgumentAcceptingOptionSpec<String> parentAccessionsSpec;
    private final NonOptionArgumentSpec<String> nonOptionSpec;

    public WorkflowScheduler() {
        super();
        parser.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");
        this.parentAccessionsSpec = parser
                .acceptsAll(
                        Arrays.asList("parent-accessions", "pa"),
                        "Optional: Typically this is the sw_accession of the processing record that is the parent for this workflow e.g. whose file is used as the input. You can actually specify multiple parent accessions by using this parameter multiple times or providing a comma-delimited list, no space. You may want multiple parents when your workflow takes multiple input files. Most of the time the accession is from a processing row but can be an ius, lane, sequencer_run, study, experiment, or sample.")
                .withRequiredArg().ofType(String.class).withValuesSeparatedBy(',');
        this.workflowAccessionSpec = parser
                .acceptsAll(
                        Arrays.asList("workflow-accession", "wa"),
                        "Optional: The sw_accession of the workflow that this run of a workflow should be associated with (via the workflow_id in the workflow_run_table). Specify this or the workflow, version, and bundle.")
                .withRequiredArg().ofType(String.class);
        this.reuseWorkflowRunAccessionSpec = parser
                .acceptsAll(
                        Arrays.asList("workflow-run-accession", "wra"),
                        "Optional: The sw_accession of an existing workflow_run that should be used. This row is pre-created when another job schedules a workflow run by partially populating a workflow_run row and setting the status to 'scheduled'. If this is not specified then a new workflow_run row will be created. Specify this in addition to a workflow-accession.")
                .withRequiredArg().ofType(String.class);
        this.linkWorkflowRunToParentsSpec = parser
                .acceptsAll(
                        Arrays.asList("link-workflow-run-to-parents", "lwrp"),
                        "Optional: The sw_accession of the sequencer_run, lane, ius, processing, study, experiment, or sample (NOTE: only currently supports ius and lane) that should be linked to the workflow_run row created by this tool. This is optional but useful since it simplifies future queries on the metadb. Can be specified multiple times if there are multiple parents or comma-delimited with no spaces (or both).")
                .withRequiredArg().ofType(String.class).withValuesSeparatedBy(',');
        this.iniFilesSpec = parser
                .acceptsAll(
                        Arrays.asList("ini-files", "i"),
                        "One or more ini files can be specified, these contain the parameters needed by the workflow template. Use commas to delimit a list of ini files.")
                .withRequiredArg().ofType(String.class).withValuesSeparatedBy(',');
        this.inputFilesSpec = parser
                .acceptsAll(
                        Arrays.asList(INPUT_FILES, "if"),
                        "One or more input files can be specified as sw_accessions for metadata tracking of input files."
                                + " Use commas to delimit a list of input files.").withRequiredArg().ofType(Long.class)
                .withValuesSeparatedBy(',');
        this.hostSpec = parser.acceptsAll(Arrays.asList("host", "ho"), "Used to schedule onto a specific host").withRequiredArg()
                .ofType(String.class);
        this.workflowEngineSpec = parser
                .accepts(
                        "workflow-engine",
                        "Optional: Specifies a workflow engine, one of: " + Engines.ENGINES_LIST + ". Defaults to "
                                + Engines.DEFAULT_ENGINE + ".").withRequiredArg().ofType(String.class)
                .describedAs("Workflow Engine").ofType(String.class);
        this.nonOptionSpec = parser.nonOptions(OVERRIDE_INI_DESC);
    }

    public static final String OVERRIDE_INI_DESC = "Override ini options on the command after the separator \"--\" with pairs of \"--<key> <value>\"";

    private String getEngineParam() {
        String engine = options.valueOf(workflowEngineSpec);
        if (engine == null) {
            engine = config.get("SW_DEFAULT_WORKFLOW_ENGINE");
        }
        if (engine == null) {
            engine = Engines.DEFAULT_ENGINE;
        }

        return engine;
    }

    @Override
    public ReturnValue init() {
        FileTools.LocalhostPair localhost = FileTools.getLocalhost(options);
        if (localhost.returnValue.getExitStatus() != ReturnValue.SUCCESS) {
            return (localhost.returnValue);
        }

        if (options.has(workflowEngineSpec)) {
            if (!Engines.ENGINES.contains((String) options.valueOf(workflowEngineSpec))) {
                Log.error("Invalid workflow-engine value. Must be one of: " + Engines.ENGINES_LIST);
                return new ReturnValue(ExitStatus.INVALIDARGUMENT);
            }
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
        return "A plugin that lets you schedule installed workflow bundles.";
    }

    @Override
    public ReturnValue do_run() {
        Scheduler w = new Scheduler(metadata, config);

        // parent accessions
        List<String> parentAccessions = options.valuesOf(parentAccessionsSpec);

        // link-workflow-run-to-parents
        List<String> parentsLinkedToWR = options.valuesOf(linkWorkflowRunToParentsSpec);

        // ini-files
        List<String> iniFiles = options.valuesOf(iniFilesSpec);

        Set<Integer> inputFiles = collectInputFiles();
        if (options.has(inputFilesSpec) && (inputFiles == null || inputFiles.isEmpty())) {
            Log.error("Error parsing provided input files");
            return new ReturnValue(ExitStatus.INVALIDARGUMENT);
        }

        // extra params, these will be passed directly to the data model layer
        // so you can use this to override key/values from the ini files
        // very useful if you're calling the workflow from another system
        // and want to pass in arguments on the command line rather than ini
        // file
        List<String> nonOptions = options.valuesOf(nonOptionSpec);
        Log.info("EXTRA OPTIONS: " + nonOptions.size());

        // THE MAIN ACTION HAPPENS HERE
        if (options.has(workflowAccessionSpec)) {

            // then you're scheduling a workflow that has been installed
            if (!options.has(hostSpec)) {
                Log.error("host parameter is required when scheduling");
                Log.info(this.get_syntax());
                return new ReturnValue(ExitStatus.INVALIDARGUMENT);
            }
            String host = options.valueOf(hostSpec);
            String engine = getEngineParam();
            Log.info("You are scheduling a workflow to run on " + host + " by adding it to the metadb.");
            return w.scheduleInstalledBundle(options.valueOf(workflowAccessionSpec), options.valueOf(reuseWorkflowRunAccessionSpec),
                    iniFiles, true, parentAccessions, parentsLinkedToWR, nonOptions, host, engine, inputFiles);

        } else {
            Log.error("I don't understand the combination of arguments you gave!");
            Log.info(this.get_syntax());
            return new ReturnValue(ExitStatus.INVALIDARGUMENT);
        }
    }

    private Set<Integer> collectInputFiles() {
        Set<Integer> inputFiles = null;
        if (options.has(inputFilesSpec)) {
            List<Long> fileAccessions = options.valuesOf(inputFilesSpec);
            inputFiles = new HashSet<>();
            for (Long fileAccession : fileAccessions) {
                File file1 = metadata.getFile(fileAccession.intValue());
                if (file1 == null) {
                    inputFiles = null;
                    Log.error("Input file not found, please check your sw_accession " + fileAccession);
                    break;
                }
                inputFiles.add(file1.getSwAccession());
            }
        }
        return inputFiles;
    }
}
