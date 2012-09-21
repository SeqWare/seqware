/**
 * @author briandoconnor@gmail.com
 *
 * The WorkflowLauncher is responsible for launching workflows with or without
 * metadata writeback.
 *
 * rules for command construction cd $cwd && $command --workflow-accession
 * $workflow_accession --workflow-run-accession $workflow_run_accession
 * --parent-accessions $parent_accessions --ini-files $temp_file --wait &
 *
 */
package net.sourceforge.seqware.pipeline.plugins;

import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.sourceforge.seqware.common.model.WorkflowRun;

import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.workflow.Workflow;

import org.openide.util.lookup.ServiceProvider;

/**
 * @author boconnor ProviderFor(PluginInterface.class)
 *
 * TODO: validate at all the option below (especially
 * link-parent-to-workflow-run) actually work!
 */
@ServiceProvider(service = PluginInterface.class)
public class WorkflowLauncher extends Plugin {

    ReturnValue ret = new ReturnValue();
    // NOTE: this is shared with WorkflowStatusChecker so only one can run at a time
    String appID = "net.sourceforge.seqware.pipeline.plugins.WorkflowStatusCheckerOrLauncher";

    public WorkflowLauncher() {
        super();
        /*
         * You should specify --workflow --version and --bundle or
         * --workflow-accession since the latter will use the database to find
         * all the needed info
         */
        parser.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");
        parser.acceptsAll(Arrays.asList("parent-accessions", "pa"), "Optional: Typically this is the sw_accession of the processing record that is the parent for this workflow e.g. whose file is used as the input. You can actually specify multiple parent accessions by using this parameter multiple times or providing a comma-delimited list, no space. You may want multiple parents when your workflow takes multiple input files. Most of the time the accession is from a processing row but can be an ius, lane, sequencer_run, study, experiment, or sample.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("workflow-accession", "wa"), "Optional: The sw_accession of the workflow that this run of a workflow should be associated with (via the workflow_id in the workflow_run_table). Specify this or the workflow, version, and bundle.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("schedule", "s"), "Optional: If this, the workflow-accession, and ini-files are all specified this will cause the workflow to be scheduled in the workflow run table rather than directly run. Useful if submitting the workflow to a remote server.");
        parser.acceptsAll(Arrays.asList("launch-scheduled", "ls"), "Optional: If this parameter is given (which can optionally have a comma separated list of workflow run accessions) all the workflows that have been scheduled in the database will have their commands constructed and executed on this machine (thus launching those workflows). This command can only be run on a machine capable of submitting workflows (e.g. a cluster submission host!). If you're submitting a workflow remotely you want to use the --schedule option instead.").withOptionalArg();
        parser.acceptsAll(Arrays.asList("workflow-run-accession", "wra"), "Optional: The sw_accession of an existing workflow_run that should be used. This row is pre-created when another job schedules a workflow run by partially populating a workflow_run row and setting the status to 'scheduled'. If this is not specified then a new workflow_run row will be created. Specify this in addition to a workflow-accession.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("workflow", "w"), "The name of the workflow to run. This must be used in conjunction with a version and bundle. Alternatively you can use a workflow-accession in place of all three for installed workflows.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("version", "v", "workflow-version"), "The workflow version to be used. You can specify this or the workflow-accession of an already installed bundle.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("bundle", "b", "provisioned-bundle-dir"), "The path to a bundle zip file. You can specify this or the workflow-accession of an already installed bundle.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("link-workflow-run-to-parents", "lwrp"), "Optional: The sw_accession of the sequencer_run, lane, ius, processing, study, experiment, or sample (NOTE: only currently supports ius and lane) that should be linked to the workflow_run row created by this tool. This is optional but useful since it simplifies future queries on the metadb. Can be specified multiple times if there are multiple parents or comma-delimited with no spaces (or both).").withRequiredArg();
        parser.acceptsAll(Arrays.asList("ini-files", "i"), "One or more ini files can be specified, these contain the parameters needed by the workflow template. Use commas without space to delimit a list of ini files.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("no-meta-db", "no-metadata"), "Optional: a flag that prevents metadata writeback (which is done by default) by the WorkflowLauncher and that is subsequently passed to the called workflow which can use it to determine if they should write metadata at runtime on the cluster.");
        parser.acceptsAll(Arrays.asList("wait"), "Optional: a flag that indicates the launcher should launch a workflow then monitor it's progress, waiting for it to exit, and returning 0 if everything is OK, non-zero if there are errors. This is useful for testing or if something else is calling the WorkflowLauncher. Without this option the launcher will immediately return with a 0 return value regardless if the workflow ultimately works.");
        parser.acceptsAll(Arrays.asList("metadata", "m"), "Specify the path to the metadata.xml file.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("host"), "If specified, the scheduled workflow will only be launched if this parameter value and the host field in the workflow run table match. This is a mechanism to target workflows to particular servers for launching.").withRequiredArg();
        ret.setExitStatus(ReturnValue.SUCCESS);
    }


    /*
     * (non-Javadoc) @see
     * net.sourceforge.seqware.pipeline.plugin.PluginInterface#init()
     */
    @Override
    public ReturnValue init() {

        return ret;
    }

    /*
     * (non-Javadoc) @see
     * net.sourceforge.seqware.pipeline.plugin.PluginInterface#do_test()
     */
    @Override
    public ReturnValue do_test() {
        // TODO Auto-generated method stub
        return ret;
    }

    /*
     * (non-Javadoc) @see
     * net.sourceforge.seqware.pipeline.plugin.PluginInterface#do_run()
     */
    @Override
    public ReturnValue do_run() {

        /*
         *
         * TODO: need to be able to pass in the workflow_run metadata!!!!
         *
         */

        // setup workflow object
        Workflow w = new Workflow(metadata, config);

        // figure out what was passed as params and make structs to pass to the workflow layer
        // metadata
        boolean metadataWriteback = true;
        if (options.has("no-metadata") || options.has("no-meta-db")) {
            metadataWriteback = false;
        }

        // parent accessions
        ArrayList<String> parentAccessions = new ArrayList<String>();
        if (options.has("parent-accessions")) {
            List opts = options.valuesOf("parent-accessions");
            for (Object opt : opts) {
                String[] tokens = ((String) opt).split(",");
                for (String t : tokens) {
                    parentAccessions.add(t);
                }
            }
        }

        //link-workflow-run-to-parents
        ArrayList<String> parentsLinkedToWR = new ArrayList<String>();
        if (options.has("link-workflow-run-to-parents")) {
            List opts = options.valuesOf("link-workflow-run-to-parents");
            for (Object opt : opts) {
                String[] tokens = ((String) opt).split(",");
                for (String t : tokens) {
                    parentsLinkedToWR.add(t);
                }
            }
        }

        // ini-files
        ArrayList<String> iniFiles = new ArrayList<String>();
        if (options.has("ini-files")) {
            List opts = options.valuesOf("ini-files");
            for (Object opt : opts) {
                String[] tokens = ((String) opt).split(",");
                for (String t : tokens) {
                    iniFiles.add(t);
                }
            }
        }

        // extra params, these will be passed directly to the FTL layer
        // so you can use this to override key/values from the ini files
        // very useful if you're calling the workflow from another system
        // and want to pass in arguments on the command line rather than ini file
        List<String> nonOptions = options.nonOptionArguments();
        Log.info("EXTRA OPTIONS: " + nonOptions.size());

        // THE MAIN ACTION HAPPENS HERE
        if (options.has("workflow-accession") && options.has("ini-files")) {

            // then you're scheduling a workflow that has been installed
            if (options.has("schedule")) {
                Log.info("You are scheduling a workflow to run by adding it to the metadb.");
                ret = w.scheduleInstalledBundle((String) options.valueOf("workflow-accession"), (String) options.valueOf("workflow-run-accession"), iniFiles, metadataWriteback, parentAccessions, parentsLinkedToWR, false, nonOptions);
            } else {
                // then your running locally but taking info saved in the workflow table from the DB
                Log.info("You are running a workflow installed in the metadb on the local computer.");
                ret = w.launchInstalledBundle((String) options.valueOf("workflow-accession"), (String) options.valueOf("workflow-run-accession"), iniFiles, metadataWriteback, parentAccessions, parentsLinkedToWR, options.has("wait"), nonOptions);
            }

        } else if ((options.has("bundle") || options.has("provisioned-bundle-dir"))
                && options.has("workflow") && options.has("version") && options.has("ini-files")) {

            // then your launching direclty and not something that has been installed
            Log.info("FYI: You are running the workflow without metadata writeback since you are running directly from a bundle zip file or directory.");
            // then run the workflow specified
            String bundlePath = "";
            if (options.has("bundle")) {
                bundlePath = (String) options.valueOf("bundle");
            } else {
                bundlePath = (String) options.valueOf("provisioned-bundle-dir");
            }
            Log.info("Bundle Path: " + bundlePath);
            String workflow = (String) options.valueOf("workflow");
            String version = (String) options.valueOf("version");
            String metadataFile = (String) options.valueOf("metadata");

            // NOTE: this overrides options to process with metadata writeback since this is not supported for bundle running!
            ret = w.launchBundle(workflow, version, metadataFile, bundlePath, iniFiles, false, new ArrayList<String>(), new ArrayList<String>(), options.has("wait"), nonOptions);

        } else if (options.has("launch-scheduled")) {
            // check to see if this code is already running, if so exit
            try {
                JUnique.acquireLock(appID);
            } catch (AlreadyLockedException e) {
                Log.error("I could not get a lock for " + appID
                        + " this most likely means the application is alredy running and this instance will exit!",e);
                ret.setExitStatus(ReturnValue.FAILURE);
            }
            // LEFT OFF HERE, not sure if the workflow will come back from the web service!?

            // then you are either launching all workflows scheduled in the DB workflow_run table or just particular ones
            List<String> scheduledAccessions = (List<String>) options.valuesOf("launch-scheduled");

            // BIG ISSUE: HOW DO YOU GO FROM WORKFLOW_RUN BACK TO WORKFLOW VIA WEB SERVICE!?

            // then need to loop over these and just launch those workflows or launch all if accession not specified
            List<WorkflowRun> scheduledWorkflows = this.metadata.getWorkflowRunsByStatus("submitted");

            Log.stdout("Number of submitted workflows: " + scheduledWorkflows.size());

            for (WorkflowRun wr : scheduledWorkflows) {
                Log.stdout("Working Run: " + wr.getSwAccession());
                if (scheduledAccessions.isEmpty() || (scheduledAccessions.size() > 0 && scheduledAccessions.contains(wr.getSwAccession().toString()))) {
                    if (!options.has("host") || (options.has("host") && options.valueOf("host") != null && options.valueOf("host").equals(wr.getHost()))) {
                        WorkflowRun wrWithWorkflow = this.metadata.getWorkflowRunWithWorkflow(wr.getSwAccession().toString());
                        w.launchScheduledBundle(wrWithWorkflow.getWorkflow().getSwAccession().toString(), wr.getSwAccession().toString(), metadataWriteback, options.has("wait"));
                    }
                }
            }


        } else {
            Log.error("I don't understand the combination of arguments you gave!");
            Log.info(this.get_syntax());
            ret.setExitStatus(ReturnValue.INVALIDARGUMENT);
        }

        return ret;
    }

    /*
     * (non-Javadoc) @see
     * net.sourceforge.seqware.pipeline.plugin.PluginInterface#clean_up()
     */
    @Override
    public ReturnValue clean_up() {
        // TODO Auto-generated method stub
        return ret;
    }

    public String get_description() {
        return ("A plugin that lets you launch workflow bundles once you have installed them via the BundleManager.");
    }
}
