package io.seqware.pipeline.plugins;

import io.seqware.Engines;
import io.seqware.pipeline.api.Scheduler;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSpecBuilder;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.module.ReturnValue.ExitStatus;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import org.apache.commons.io.FileUtils;
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
    private final ArgumentAcceptingOptionSpec<String> workflowAccessionSpec;
    private final ArgumentAcceptingOptionSpec<String> parentAccessionsSpec;
    private final NonOptionArgumentSpec<String> nonOptionSpec;
    private final OptionSpecBuilder metadataWriteBackOffSpec;
    private final ArgumentAcceptingOptionSpec<String> outFile;

    public WorkflowScheduler() {
        super();
        parser.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");

        this.workflowAccessionSpec = parser
                .acceptsAll(
                        Arrays.asList("workflow-accession", "wa"),
                        "Required: The sw_accession of the workflow that this run of a workflow should be associated with (via the workflow_id in the workflow_run_table). Specify this or the workflow, version, and bundle.")
                .withRequiredArg().ofType(String.class).required();
        this.linkWorkflowRunToParentsSpec = parser
                .acceptsAll(
                        Arrays.asList("link-workflow-run-to-parents", "lwrp"),
                        "Optional: The sw_accession of the sequencer_run, lane, ius, processing, study, experiment, or sample (NOTE: only currently supports ius and lane) that should be linked to the workflow_run row created by this tool. This is optional but useful since it simplifies future queries on the metadb. Can be specified multiple times if there are multiple parents or comma-delimited with no spaces (or both).")
                .withRequiredArg().ofType(String.class).withValuesSeparatedBy(',');
        this.inputFilesSpec = parser
                .acceptsAll(
                        Arrays.asList(INPUT_FILES, "if"),
                        "One or more input files can be specified as sw_accessions for metadata tracking of input files."
                                + " Use commas to delimit a list of input files.").withRequiredArg().ofType(Long.class)
                .withValuesSeparatedBy(',');
        this.hostSpec = parser.acceptsAll(Arrays.asList("host", "ho"), "Used to schedule onto a specific host").withRequiredArg()
                .ofType(String.class);

        this.parentAccessionsSpec = createParentAccessionSpec(parser);
        this.iniFilesSpec = createIniFileSpec(parser);
        this.workflowEngineSpec = createWorkflowEngineSpec(parser);
        this.metadataWriteBackOffSpec = createMetadataWriteBackOffSpec(parser);
        this.nonOptionSpec = parser.nonOptions(OVERRIDE_INI_DESC);
        this.outFile = parser.acceptsAll(Arrays.asList("out"), "Optional: Will output a workflow-run by sw_accession").withRequiredArg();
    }

    public static final ArgumentAcceptingOptionSpec<String> createParentAccessionSpec(OptionParser parser) {
        return parser
                .acceptsAll(
                        Arrays.asList("parent-accessions", "pa"),
                        "Optional: Typically this is the sw_accession of the processing record that is the parent for this workflow e.g. whose file is used as the input. You can actually specify multiple parent accessions by using this parameter multiple times or providing a comma-delimited list, no space. You may want multiple parents when your workflow takes multiple input files. Most of the time the accession is from a processing row but can be an ius, lane, sequencer_run, study, experiment, or sample.")
                .withRequiredArg().ofType(String.class).withValuesSeparatedBy(',');
    }

    public static final OptionSpecBuilder createMetadataWriteBackOffSpec(OptionParser parser) {
        return parser
                .acceptsAll(
                        Arrays.asList("no-meta-db", "no-metadata"),
                        "Optional: a flag that prevents metadata writeback (which is done by default) by the WorkflowLauncher and that is subsequently passed to the called workflow which can use it to determine if they should write metadata at runtime on the cluster.");
    }

    public static final ArgumentAcceptingOptionSpec<String> createWorkflowEngineSpec(OptionParser parser) {
        return parser
                .accepts(
                        "workflow-engine",
                        "Optional: Specifies a workflow engine, one of: " + Engines.ENGINES_LIST + ". Defaults to "
                                + Engines.DEFAULT_ENGINE + ".").withRequiredArg().ofType(String.class).describedAs("Workflow Engine")
                .ofType(String.class);
    }

    public static final ArgumentAcceptingOptionSpec<String> createIniFileSpec(OptionParser parser) {
        return parser
                .acceptsAll(
                        Arrays.asList("ini-files", "i"),
                        "One or more ini files can be specified, these contain the parameters needed by the workflow template. Use commas to delimit a list of ini files.")
                .withRequiredArg().ofType(String.class).withValuesSeparatedBy(',');
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
        return "A plugin that lets you schedule installed workflow bundles.";
    }

    @Override
    public ReturnValue do_run() {
        Scheduler w = new Scheduler(metadata, config);
        Set<Integer> inputFiles;
        try {
            inputFiles = collectInputFiles();
            if (options.has(inputFilesSpec) && (inputFiles == null || inputFiles.isEmpty())) {
                Log.error("Error parsing provided input files");
                return new ReturnValue(ExitStatus.INVALIDARGUMENT);
            }
        } catch (Exception e) {
            Log.error("Error checking provided input files");
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
            ReturnValue ret = w.scheduleInstalledBundle(options.valueOf(workflowAccessionSpec), options.valuesOf(iniFilesSpec),
                    !options.has(metadataWriteBackOffSpec), options.valuesOf(parentAccessionsSpec),
                    options.valuesOf(linkWorkflowRunToParentsSpec), options.valuesOf(nonOptionSpec), host, engine, inputFiles);

            if (options.has(outFile)) {
                try {
                    java.io.File file = new java.io.File(options.valueOf(this.outFile));
                    FileUtils.write(file, String.valueOf(ret.getReturnValue()));
                } catch (IOException ex) {
                    return new ReturnValue(ExitStatus.FILENOTWRITABLE);
                }
            }
            return ret;

        } else {
            Log.error("I don't understand the combination of arguments you gave!");
            Log.info(this.get_syntax());
            return new ReturnValue(ExitStatus.INVALIDARGUMENT);
        }
    }

    private Set<Integer> collectInputFiles() throws Exception {
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
