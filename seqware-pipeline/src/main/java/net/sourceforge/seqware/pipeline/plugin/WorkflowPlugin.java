/**
 * @author briandoconnor@gmail.com
 *
 * The WorkflowPlugin is responsible for launching workflows with or without
 * metadata writeback.
 *
 * rules for command construction cd $cwd && $command --workflow-accession
 * $workflow_accession --workflow-run-accession $workflow_run_accession
 * --parent-accessions $parent_accessions --ini-files $temp_file --wait &
 *
 */
package net.sourceforge.seqware.pipeline.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import joptsimple.OptionSet;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.WorkflowRunStatus;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.pipeline.tools.RunLock;
import net.sourceforge.seqware.pipeline.workflow.BasicWorkflow;
import net.sourceforge.seqware.pipeline.workflow.Workflow;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowEngine;
import net.sourceforge.seqware.pipeline.workflowV2.WorkflowDataModelFactory;
import net.sourceforge.seqware.pipeline.workflowV2.WorkflowV2Utility;
import net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.OozieWorkflowEngine;
import net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object.OozieJob;
import net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.PegasusWorkflowEngine;

import org.openide.util.lookup.ServiceProvider;

/**
 * 
 * TODO: validate at all the option below (especially
 * link-parent-to-workflow-run) actually work!
 * 
 * @author boconnor
 */
@ServiceProvider(service = PluginInterface.class)
public class WorkflowPlugin extends Plugin {

  public static final String FORCE_HOST = "force-host";
  public static final String HOST = "host";
  public static final String SCHEDULE = "schedule";
  public static final String LAUNCH_SCHEDULED = "launch-scheduled";
  public static final String WAIT = "wait";
  public static final String INPUT_FILES = "input-files";

  protected ReturnValue ret = new ReturnValue();
  // NOTE: this is shared with WorkflowStatusChecker so only one can run at a
  // time
  protected String appID = "net.sourceforge.seqware.pipeline.plugins.WorkflowStatusCheckerOrLauncher";
  private String hostname;

  public WorkflowPlugin() {
    super();
    /*
     * You should specify --workflow --version and --bundle or
     * --workflow-accession since the latter will use the database to find all
     * the needed info
     */
    parser.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");
    parser.acceptsAll(Arrays.asList("parent-accessions", "pa"),
                      "Optional: Typically this is the sw_accession of the processing record that is the parent for this workflow e.g. whose file is used as the input. You can actually specify multiple parent accessions by using this parameter multiple times or providing a comma-delimited list, no space. You may want multiple parents when your workflow takes multiple input files. Most of the time the accession is from a processing row but can be an ius, lane, sequencer_run, study, experiment, or sample.").withRequiredArg();
    parser.acceptsAll(Arrays.asList("workflow-accession", "wa"),
                      "Optional: The sw_accession of the workflow that this run of a workflow should be associated with (via the workflow_id in the workflow_run_table). Specify this or the workflow, version, and bundle.").withRequiredArg();
    parser.acceptsAll(Arrays.asList(SCHEDULE, "s"),
                      "Optional: If this, the workflow-accession, and ini-files are all specified this will cause the workflow to be scheduled in the workflow run table rather than directly run. Useful if submitting the workflow to a remote server.");
    parser.acceptsAll(Arrays.asList(LAUNCH_SCHEDULED, "ls"),
                      "Optional: If this parameter is given (which can optionally have a comma separated list of workflow run accessions) all the workflows that have been scheduled in the database will have their commands constructed and executed on this machine (thus launching those workflows). This command can only be run on a machine capable of submitting workflows (e.g. a cluster submission host!). If you're submitting a workflow remotely you want to use the --schedule option instead.").withOptionalArg();
    parser.acceptsAll(Arrays.asList("workflow-run-accession", "wra"),
                      "Optional: The sw_accession of an existing workflow_run that should be used. This row is pre-created when another job schedules a workflow run by partially populating a workflow_run row and setting the status to 'scheduled'. If this is not specified then a new workflow_run row will be created. Specify this in addition to a workflow-accession.").withRequiredArg();
    parser.acceptsAll(Arrays.asList("workflow", "w"),
                      "The name of the workflow to run. This must be used in conjunction with a version and bundle. Alternatively you can use a workflow-accession in place of all three for installed workflows.").withRequiredArg();
    parser.acceptsAll(Arrays.asList("version", "v", "workflow-version"),
                      "The workflow version to be used. You can specify this or the workflow-accession of an already installed bundle.").withRequiredArg();
    parser.acceptsAll(Arrays.asList("bundle", "b", "provisioned-bundle-dir"),
                      "The path to a bundle zip file. You can specify this or the workflow-accession of an already installed bundle.").withRequiredArg();
    parser.acceptsAll(Arrays.asList("link-workflow-run-to-parents", "lwrp"),
                      "Optional: The sw_accession of the sequencer_run, lane, ius, processing, study, experiment, or sample (NOTE: only currently supports ius and lane) that should be linked to the workflow_run row created by this tool. This is optional but useful since it simplifies future queries on the metadb. Can be specified multiple times if there are multiple parents or comma-delimited with no spaces (or both).").withRequiredArg();
    parser.acceptsAll(Arrays.asList("ini-files", "i"),
                      "One or more ini files can be specified, these contain the parameters needed by the workflow template. Use commas without space to delimit a list of ini files.").withRequiredArg();
    parser.acceptsAll(Arrays.asList(INPUT_FILES, "if"),
                      "One or more input files can be specified as sw_accessions. Use commas without space to delimit a list of input files.").withRequiredArg();
    parser.acceptsAll(Arrays.asList("no-meta-db", "no-metadata"),
                      "Optional: a flag that prevents metadata writeback (which is done by default) by the WorkflowLauncher and that is subsequently passed to the called workflow which can use it to determine if they should write metadata at runtime on the cluster.");
    parser.acceptsAll(Arrays.asList(WAIT),
                      "Optional: a flag that indicates the launcher should launch a workflow then monitor it's progress, waiting for it to exit, and returning 0 if everything is OK, non-zero if there are errors. This is useful for testing or if something else is calling the WorkflowLauncher. Without this option the launcher will immediately return with a 0 return value regardless if the workflow ultimately works.");
    parser.acceptsAll(Arrays.asList("metadata", "m"), "Specify the path to the metadata.xml file.").withRequiredArg();
    parser.acceptsAll(Arrays.asList(HOST, "ho"),
                      "Used only in combination with --schedule to schedule onto a specific host").withRequiredArg();
    parser.acceptsAll(Arrays.asList(FORCE_HOST, "fh"),
                      "If specified, the scheduled workflow will only be launched if this parameter value and the host field in the workflow run table match. This is a mechanism to target workflows to particular servers for launching.").withRequiredArg();
    // options ported over from WorkflowLauncherV2
    parser.accepts("metadata-output-file-prefix",
                   "Optional: Specifies a path to prepend to every file returned by the module. Useful for dealing when staging files back.").withRequiredArg().ofType(String.class).describedAs("Path to prepend to each file location.");
    parser.accepts("metadata-output-dir",
                   "Optional: Specifies a path to prepend to every file returned by the module. Useful for dealing when staging files back.").withRequiredArg().ofType(String.class).describedAs("Path to prepend to each file location.");
    parser.accepts("workflow-engine",
                   "Optional: Specifies a workflow engine, one of: "+ENGINES_LIST+". Defaults to "+DEFAULT_ENGINE+".").withRequiredArg().ofType(String.class).describedAs("Workflow Engine");
    parser.accepts("no-run",
                   "Optional: Terminates the launch process immediately prior to running. Useful for debugging.");

    ret.setExitStatus(ReturnValue.SUCCESS);
  }

  public static final String ENGINES_LIST = "pegasus, oozie, oozie-sge";
  public static final String DEFAULT_ENGINE = "pegasus";
  public static final Set<String> ENGINES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(ENGINES_LIST.split(", "))));
  
  private String getEngineParam(){
    String engine = (String) options.valueOf("workflow-engine");
    if (engine == null)
      engine = config.get("SW_DEFAULT_WORKFLOW_ENGINE");
    if (engine == null)
      engine = DEFAULT_ENGINE;

    return engine;
  }
  
  /*
   * (non-Javadoc) @see
   * net.sourceforge.seqware.pipeline.plugin.PluginInterface#init()
   */
  @Override
  public ReturnValue init() {
    FileTools.LocalhostPair localhost = FileTools.getLocalhost(options);
    if (localhost.returnValue.getExitStatus() != ReturnValue.SUCCESS) {
      return (localhost.returnValue);
    } else {
      this.hostname = localhost.hostname;
    }
    
    if (options.has("workflow-engine")){
      if (!ENGINES.contains(options.valueOf("workflow-engine"))){
        Log.error("Invalid workflow-engine value. Must be one of: "+ENGINES_LIST);
        ret.setExitStatus(ReturnValue.INVALIDARGUMENT);
        return ret;
      }
    }

    // wrong assumption here I think, --host is possible even when not
    // scheduling or waiting
    // if (options.has(HOST) && (!options.has(SCHEDULE) && !options.has(WAIT)))
    // {
    // ret.setExitStatus(ReturnValue.INVALIDARGUMENT);
    // return ret;
    // }

    ret.setExitStatus(ReturnValue.SUCCESS);
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

  /**
   * This is the original run method from the old Workflow launcher. This still
   * does scheduling since the new workflow launcher does not understand
   * scheduling.
   * 
   * @return
   */
  public ReturnValue doOldRun() {

    /*
     * 
     * TODO: need to be able to pass in the workflow_run metadata!!!!
     */

    // setup workflow object
    BasicWorkflow w = this.createWorkflow();

    // figure out what was passed as params and make structs to pass to the
    // workflow layer
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
          parentAccessions.addAll(Arrays.asList(tokens));
      }
    }

    // link-workflow-run-to-parents
    ArrayList<String> parentsLinkedToWR = new ArrayList<String>();
    if (options.has("link-workflow-run-to-parents")) {
      List opts = options.valuesOf("link-workflow-run-to-parents");
      for (Object opt : opts) {
        String[] tokens = ((String) opt).split(",");
          parentsLinkedToWR.addAll(Arrays.asList(tokens));
      }
    }

    // ini-files
    ArrayList<String> iniFiles = new ArrayList<String>();
    if (options.has("ini-files")) {
      List opts = options.valuesOf("ini-files");
      for (Object opt : opts) {
        String[] tokens = ((String) opt).split(",");
          iniFiles.addAll(Arrays.asList(tokens));
      }
    }
    
    Set<Integer> inputFiles = WorkflowPlugin.collectInputFiles(options, metadata);
    if (options.has(INPUT_FILES) && (inputFiles == null || inputFiles.isEmpty())){
        Log.error("Error parsing provided input files");
        ret.setExitStatus(ReturnValue.INVALIDARGUMENT);
        return ret;
    }    

    // extra params, these will be passed directly to the FTL layer
    // so you can use this to override key/values from the ini files
    // very useful if you're calling the workflow from another system
    // and want to pass in arguments on the command line rather than ini
    // file
    List<String> nonOptions = options.nonOptionArguments();
    Log.info("EXTRA OPTIONS: " + nonOptions.size());

    // THE MAIN ACTION HAPPENS HERE
    if (options.has("workflow-accession") && options.has("ini-files")) {

      // then you're scheduling a workflow that has been installed
      if (options.has(SCHEDULE)) {
        if (!options.has(HOST)) {
          Log.error("host parameter is required when scheduling");
          Log.info(this.get_syntax());
          ret.setExitStatus(ReturnValue.INVALIDARGUMENT);
          return ret;
        }
        String host = (String) options.valueOf(HOST);
        String engine = getEngineParam();
        Log.info("You are scheduling a workflow to run on " + host + " by adding it to the metadb.");
        ret = w.scheduleInstalledBundle((String) options.valueOf("workflow-accession"),
                                        (String) options.valueOf("workflow-run-accession"), iniFiles,
                                        metadataWriteback, parentAccessions, parentsLinkedToWR, false, nonOptions, host, engine, inputFiles);
      } else {
        // then your running locally but taking info saved in the
        // workflow table from the DB
        Log.info("You are running a workflow installed in the metadb on the local computer.");
        ret = w.launchInstalledBundle((String) options.valueOf("workflow-accession"),
                                      (String) options.valueOf("workflow-run-accession"), iniFiles, metadataWriteback,
                                      parentAccessions, parentsLinkedToWR, options.has(WAIT), nonOptions, inputFiles);
      }

    } else if ((options.has("bundle") || options.has("provisioned-bundle-dir")) && options.has("workflow")
        && options.has("version") && options.has("ini-files")) {

      // then your launching directly and not something that has been
      // installed
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

      // NOTE: this overrides options to process with metadata writeback
      // since this is not supported for bundle running!
      ret = w.launchBundle(workflow, version, metadataFile, bundlePath, iniFiles, false, new ArrayList<String>(),
                           new ArrayList<String>(), options.has(WAIT), nonOptions, inputFiles);

    } else if (options.has(LAUNCH_SCHEDULED)) {
      RunLock.acquire();
      launchScheduledWorkflows(w, metadataWriteback);
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

  @Override
  public String get_description() {
    return "A plugin that lets you launch workflow bundles once you have installed them via the BundleManager.";
  }

  protected BasicWorkflow createWorkflow() {
    return new Workflow(metadata, config);
  }

  @Override
  public ReturnValue do_run() {
    ReturnValue oldReturnValue = null;
    // ensure that scheduling is done in conjunction with a host
    if (options.has(SCHEDULE) || options.has(LAUNCH_SCHEDULED)) {
      // this needs cleanup, but if we want to schedule just defer to the old
      // launcher
      // we also need to handle scheduled runs that are relevant to the new
      // launcher
      oldReturnValue = doOldRun();
    } else if (options.has("workflow-accession") && options.has("ini-files")) {
      // then your running locally but taking info saved in the
      // workflow table from the DB
      ret = launchSingleWorkflow(true);
    } else if ((options.has("bundle") || options.has("provisioned-bundle-dir")) && options.has("workflow")
        && options.has("version") && options.has("ini-files")) {
      ret = launchSingleWorkflow(false);
    } else {
      Log.error("I don't understand the combination of arguments you gave!");
      Log.info(this.get_syntax());
      ret.setExitStatus(ReturnValue.INVALIDARGUMENT);
    }

    if (oldReturnValue != null && oldReturnValue.getReturnValue() != ReturnValue.SUCCESS) {
      ret = oldReturnValue;
    }
    return ret;

  }

  public static AbstractWorkflowEngine getWorkflowEngine(AbstractWorkflowDataModel dataModel, Map<String, String> config) {
    AbstractWorkflowEngine wfEngine = null;
    String engine = dataModel.getWorkflow_engine();
    if (engine == null || engine.equalsIgnoreCase("pegasus")) {
      wfEngine = new PegasusWorkflowEngine();
    } else if (engine.equalsIgnoreCase("oozie")) {
      wfEngine = new OozieWorkflowEngine(dataModel, false, null, null);
    } else if (engine.equalsIgnoreCase("oozie-sge")) {
      String threadsSgeParamFormat = config.get("OOZIE_SGE_THREADS_PARAM_FORMAT");
      String maxMemorySgeParamFormat = config.get("OOZIE_SGE_MAX_MEMORY_PARAM_FORMAT");
      if (threadsSgeParamFormat == null) {
        System.err.println("WARNING: No entry in settings for OOZIE_SGE_THREADS_PARAM_FORMAT, omitting threads option from qsub. Fix by providing the format of qsub threads option, using the '"+OozieJob.SGE_THREADS_PARAM_VARIABLE+"' variable.");
      }
      if (maxMemorySgeParamFormat == null) {
        System.err.println("WARNING: No entry in settings for OOZIE_SGE_MAX_MEMORY_PARAM_FORMAT, omitting max-memory option from qsub. Fix by providing the format of qsub max-memory option, using the '"+OozieJob.SGE_MAX_MEMORY_PARAM_VARIABLE+"' variable.");
      }
      wfEngine = new OozieWorkflowEngine(dataModel, true, threadsSgeParamFormat, maxMemorySgeParamFormat);
    } else {
      throw new IllegalArgumentException("Unknown workflow engine: " + engine);
    }
    return wfEngine;
  }

  /**
   * Processes a single workflow
   * 
   * @param readFromDB
   *          read bundle from the database
   * @return
   * @throws NumberFormatException
   */
  public ReturnValue launchSingleWorkflow(boolean readFromDB) {
    boolean newLauncherRequired = true;
    Integer workflowAccession = null;
    try {
      if (readFromDB) {
        String valueOf = (String) options.valueOf("workflow-accession");
        workflowAccession = Integer.valueOf(valueOf);
        net.sourceforge.seqware.common.model.Workflow workflow = this.metadata.getWorkflow(workflowAccession);
        newLauncherRequired = WorkflowV2Utility.requiresNewLauncher(workflow);
      } else {
        newLauncherRequired = WorkflowV2Utility.requiresNewLauncher(options);
      }
    } catch (Exception e) {
      // this is ugly, clean this up during integration test work
      Log.error("Error parsing provided bundle", e);
      Log.info(this.get_syntax());
      ret.setExitStatus(ReturnValue.INVALIDARGUMENT);
      return ret;
    }
    if (!newLauncherRequired) {
      return doOldRun();
    }
    return launchNewWorkflow(options, config, params, metadata, workflowAccession, null, getEngineParam());
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
    final boolean localMatch = !options.has(FORCE_HOST) && hostname.equals(wr.getHost());
    // 2) we match with the forcehost parameter with no parameters matching null
    // in the database
    final boolean forceHostNull = options.has(FORCE_HOST) && !options.hasArgument(FORCE_HOST) && wr.getHost() == null;
    // 3) we match with the forcehost parameter matching an actual value in the
    // database
    final boolean actualForceHostMatch = options.has(FORCE_HOST) && hostname.equals(wr.getHost());
    return localMatch || forceHostNull || actualForceHostMatch;
  }

  /**
   * Grab valid scheduled workflows from the database and run them
   * 
   * @param w
   * @param metadataWriteback
   */
  private void launchScheduledWorkflows(BasicWorkflow w, boolean metadataWriteback) {
    // LEFT OFF HERE, not sure if the workflow will come back from the
    // web service!?

    // then you are either launching all workflows scheduled in the DB
    // workflow_run table or just particular ones
    List<String> scheduledAccessions = (List<String>) options.valuesOf(LAUNCH_SCHEDULED);

    // BIG ISSUE: HOW DO YOU GO FROM WORKFLOW_RUN BACK TO WORKFLOW VIA
    // WEB SERVICE!?

    // then need to loop over these and just launch those workflows or
    // launch all if accession not specified
    List<WorkflowRun> scheduledWorkflows = this.metadata.getWorkflowRunsByStatus(WorkflowRunStatus.submitted);

    Log.stdout("Number of submitted workflows: " + scheduledWorkflows.size());

    for (WorkflowRun wr : scheduledWorkflows) {
      Log.stdout("Working Run: " + wr.getSwAccession());

      if (scheduledAccessions.isEmpty()
          || (scheduledAccessions.size() > 0 && scheduledAccessions.contains(wr.getSwAccession().toString()))) {

        boolean validWorkflowRunByHost = isWorkflowRunValidByLocalhost(wr);

        // SEQWARE-1451
        // Workflow launcher totally dies one workflow freemarker run dies
        // let's just wrap and report these errors and fail onto the next one
        try {

          if (validWorkflowRunByHost) {
            Log.stdout("Valid run by host check: " + wr.getSwAccession());
            WorkflowRun wrWithWorkflow = this.metadata.getWorkflowRunWithWorkflow(wr.getSwAccession().toString());
            boolean requiresNewLauncher = WorkflowV2Utility.requiresNewLauncher(wrWithWorkflow.getWorkflow());
            if (!requiresNewLauncher) {
              Log.stdout("Launching via old launcher: " + wr.getSwAccession());
              w.launchScheduledBundle(wrWithWorkflow.getWorkflow().getSwAccession().toString(),
                                      wr.getSwAccession().toString(), metadataWriteback, options.has(WAIT));
            } else {
              Log.stdout("Launching via new launcher: " + wr.getSwAccession());
              WorkflowPlugin.launchNewWorkflow(options, config, params, metadata, wr.getWorkflowAccession(),
                                               wr.getSwAccession(), wr.getWorkflowEngine());
            }
          } else {
            Log.stdout("Invalid run by host check: " + wr.getSwAccession());
          }

        } catch (Exception e) {
          Log.fatal("Workflowrun launch with accession: " + wr.getSwAccession() + " failed", e);
        }
      }
    }
  }

  /**
   * Separating out the launching of a new workflow. This way, we can eventually
   * refactor this to the Workflow object.
   * 
   * @param options
   * @param config
   * @param params
   * @param metadata
   * @param workflowAccession
   * @return
   */
  public static ReturnValue launchNewWorkflow(OptionSet options, Map<String, String> config, String[] params,
                                              Metadata metadata, Integer workflowAccession, Integer workflowRunAccession, String workflowEngine) {
    Log.info("launching new workflow");
    boolean scheduled = workflowRunAccession != null;
    ReturnValue ret = new ReturnValue();
    AbstractWorkflowDataModel dataModel;
    try {
      final WorkflowDataModelFactory factory = new WorkflowDataModelFactory(options, config, params, metadata);
      dataModel = factory.getWorkflowDataModel(workflowAccession, workflowRunAccession);
      if (workflowEngine != null){
        dataModel.setWorkflow_engine(workflowEngine);
      } else {
        // maintain consistency between the two ways of accessing the engine value
        workflowEngine = dataModel.getWorkflow_engine();
      }
    } catch (Exception e) {
      Log.fatal(e, e);
      ret.setExitStatus(ReturnValue.INVALIDARGUMENT);
      return ret;
    }

    Log.info("constructed dataModel");

    // set up workflow engine
    AbstractWorkflowEngine engine = WorkflowPlugin.getWorkflowEngine(dataModel, config);

    engine.prepareWorkflow(dataModel);
    if (options.has("no-run")) {
      return new ReturnValue(ReturnValue.SUCCESS);
    }

    ReturnValue retPegasus = engine.runWorkflow();
    if (!dataModel.isMetadataWriteBack()) {
      return retPegasus;
    }

    Log.info("attempting metadata writeback");

    // metadataWriteback
    String wra = dataModel.getWorkflow_run_accession();

    if (wra == null || wra.isEmpty()) {
      return retPegasus;
    }

    // int workflowrunId = Integer.parseInt(wra);
    int workflowrunaccession = Integer.parseInt(wra); // metadata.get_workflow_run_accession(workflowrunId);
    int workflowrunId = metadata.get_workflow_run_id(workflowrunaccession);

    List<String> parentsLinkedToWR = new ArrayList<String>();
    if (options.has("link-workflow-run-to-parents")) {
      List opts = options.valuesOf("link-workflow-run-to-parents");
      for (Object opt : opts) {
        String[] tokens = ((String) opt).split(",");
          parentsLinkedToWR.addAll(Arrays.asList(tokens));
      }
    }

    WorkflowRun wr = null;

    // need to figure out workflow_run_accession
    // need to link all the parents to this workflow run accession
    for (String parentLinkedToWR : parentsLinkedToWR) {
      try {
        metadata.linkWorkflowRunAndParent(workflowrunId, Integer.parseInt(parentLinkedToWR));
      } catch (Exception e) {
        Log.error(e.getMessage());
      }
    }
       
    Set<Integer> inputFiles = collectInputFiles(options, metadata);
    if (options.has(INPUT_FILES) && (inputFiles == null || inputFiles.isEmpty())){
        Log.error("Error parsing provided input files");
        ret.setExitStatus(ReturnValue.INVALIDARGUMENT);
        return ret;
    }    

    // need to pull back the workflow run object since some fields may
    // already be set
    // and we need to use their values before writing back to the DB!
    wr = metadata.getWorkflowRun(workflowrunaccession);

    String workflowRunToken = engine.getLookupToken();

    String host = scheduled && !options.has(HOST) ? wr.getHost() : (String)options.valueOf(HOST);

    if (retPegasus.getProcessExitStatus() != ReturnValue.SUCCESS || workflowRunToken == null) {
      // then something went wrong trying to call pegasus
      metadata.update_workflow_run(workflowrunId, dataModel.getTags().get("workflow_command"),
                                   dataModel.getTags().get("workflow_template"), WorkflowRunStatus.failed, workflowRunToken,
                                   engine.getWorkingDirectory(), "", "", host,
                                   retPegasus.getStderr(), retPegasus.getStdout(), dataModel.getWorkflow_engine(), inputFiles);

      return retPegasus;
    } else {
      // determine status based on object model
      WorkflowRunStatus status = dataModel.isWait() ? WorkflowRunStatus.completed : WorkflowRunStatus.pending;
      metadata.update_workflow_run(workflowrunId, dataModel.getTags().get("workflow_command"),
                                   dataModel.getTags().get("workflow_template"), status, workflowRunToken,
                                   engine.getWorkingDirectory(), "", "", host,
                                   retPegasus.getStderr(), retPegasus.getStdout(), dataModel.getWorkflow_engine(), inputFiles);
      return ret;
    }
  }
  
    private static Set<Integer> collectInputFiles(OptionSet options, Metadata metadata) {
        Set<Integer> inputFiles = null;
        if (options.has(INPUT_FILES)) {
            try {
                List<String> files = (List<String>) options.valuesOf(INPUT_FILES);
                inputFiles = new HashSet<Integer>();
                for (String file : files) {
                    String[] tokens = ((String) file).split(",");
                    for (String token : tokens) {
                        File file1 = metadata.getFile(Integer.valueOf(token));
                        if (file1 == null) {
                            inputFiles = null;
                            Log.error("Input file not found, please check your sw_accession");
                            break;
                        }
                        inputFiles.add(file1.getSwAccession());
                    }
                }
            } catch (Exception e) {
                inputFiles = null;
            }
        }
        return inputFiles;
    }
}
