/*
 * Copyright (C) 2011 SeqWare
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
package net.sourceforge.seqware.pipeline.plugins;

import io.seqware.Engines;
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;

import java.io.File;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.WorkflowRunStatus;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.common.util.filetools.FileTools.LocalhostPair;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowTools;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object.OozieJob;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.WorkflowAction;
import org.apache.oozie.client.WorkflowJob;
import org.openide.util.lookup.ServiceProvider;

/**
 * This plugin lets you monitor the status of running workflows and updates the
 * metadata object with their status.
 * 
 * @author boconnor
 * @version $Id: $Id
 */
@ServiceProvider(service = PluginInterface.class)
public class WorkflowStatusChecker extends Plugin {
    public static final String WORKFLOW_RUN_ACCESSION = "workflow-run-accession";
  private ReturnValue classReturnValue = new ReturnValue();
  // NOTE: this is shared with WorkflowLauncher so only one can run at a time
  public static final String appID = "net.sourceforge.seqware.pipeline.plugins.WorkflowStatusCheckerOrLauncher";
  private static final String metadata_sync = "synch_for_metadata";
  // variables for use in the app
  private String hostname = null;
  private String username = null;

  /**
   * <p>
   * Constructor for WorkflowStatusChecker.
   * </p>
   */
  public WorkflowStatusChecker() {
    super();
    parser.acceptsAll(Arrays.asList("status-cmd", "s"),
                      "Optional: the Pegasus status command, if you specify this option the command will be run, potentially displaying the summarized/parsed errors, but the database will not be updated.").withRequiredArg();
    parser.acceptsAll(Arrays.asList(WORKFLOW_RUN_ACCESSION, "wra"),
                      "Optional: this will cause the program to only check the status of workflow run(s). For multiple runs, comma-separate with no spaces").withRequiredArg().withValuesSeparatedBy(',').ofType(Integer.class);
    parser.acceptsAll(Arrays.asList("workflow-accession", "wa"),
                      "Optional: this will cause the program to only check the status of workflow runs that are this type of workflow.").withRequiredArg();
    parser.acceptsAll(Arrays.asList("force-host", "fh"),
                      "Optional: if specified, workflow runs scheduled to this specified host will be checked even if this is not the current host (a dangerous option).").withRequiredArg();
    parser.acceptsAll(Arrays.asList("check-failed", "cf"),
                      "Optional: if specified, workflow runs that have previously failed will be re-checked.");
    parser.acceptsAll(Arrays.asList("threads-in-thread-pool", "tp"),
                      "Optional: this will determine the number of threads to run with. Default: 1").withRequiredArg().ofType(Integer.class);
    //SEQWARE-1732 custom lock ID
    parser.acceptsAll(Arrays.asList("dynamic-lock-file", "dlf"),
                      "Optional: This option is discouraged and unsupported. A unique lock-file based on the SW_REST_URL is generated. "
            + "This allows multiple workflow status checkers and workflow launchers to run at the same time, "
            + "but could lead to a corrupt state in the metadb");

    classReturnValue.setExitStatus(ReturnValue.SUCCESS);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReturnValue init() {

    // check to see if this code is already running, if so exit
    try {
        String lock = appID;
        if (options.has("dynamic-lock-file")){
            Log.stderr("Using a dynamic-lock-file based on your SW_REST_URL. This mode is unsupported.");
            lock = ConfigTools.getSettings().get("SW_REST_URL");
        } 
        JUnique.acquireLock(lock);
    } catch (AlreadyLockedException e) {
      Log.error("I could not get a lock for " + appID
          + " this most likely means the application is already running and this instance will exit!");
      classReturnValue.setExitStatus(ReturnValue.FAILURE);
    }

    // bail out if failed
    if (classReturnValue.getExitStatus() != ReturnValue.SUCCESS) {
      return (classReturnValue);
    }
    LocalhostPair localhost = FileTools.getLocalhost(options);
    // returnValue can be null if we use forcehost
    if (localhost.returnValue != null && localhost.returnValue.getExitStatus() != ReturnValue.SUCCESS) {
      return (localhost.returnValue);
    } else {
      this.hostname = localhost.hostname;
    }

    // figure out the username
    if (this.config.get("SW_REST_USER") == null || "".equals(this.config.get("SW_REST_USER"))) {
      Log.error("You must define SW_REST_USER in your SeqWare settings file!");
      classReturnValue.setExitStatus(ReturnValue.FAILURE);
    }
    this.username = this.config.get("SW_REST_USER");

    classReturnValue.setExitStatus(ReturnValue.SUCCESS);

    return classReturnValue;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReturnValue do_test() {
    return classReturnValue;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReturnValue do_run() {

    ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);

    // lets you just check a given workflow without metadata
    if (options.has("status-cmd") && options.valueOf("status-cmd") != null) {

      ret = checkWorkflow((String) options.valueOf("status-cmd"));

    } else { // this checks workflows and writes their status back to the DB

      // get a list of running workflows
      List<WorkflowRun> runningWorkflows = this.metadata.getWorkflowRunsByStatus(WorkflowRunStatus.running);
      runningWorkflows.addAll(this.metadata.getWorkflowRunsByStatus(WorkflowRunStatus.pending));
      runningWorkflows.addAll(this.metadata.getWorkflowRunsByStatus(WorkflowRunStatus.submitted_cancel));
      runningWorkflows.addAll(this.metadata.getWorkflowRunsByStatus(WorkflowRunStatus.submitted_retry));
      if (options.has("check-failed")) {
        runningWorkflows.addAll(this.metadata.getWorkflowRunsByStatus(WorkflowRunStatus.failed));
      }

      // setup thread pool
      ExecutorService pool; // Executors.newFixedThreadPool(4);
      if (options.has("threads-in-thread-pool")) {
        int threads = (Integer) options.valueOf("threads-in-thread-pool");
        if (threads <= 0) {
          Log.fatal("Inappropriate number of threads selected");
          ret = new ReturnValue(ReturnValue.FAILURE);
          return ret;
        }
        pool = Executors.newFixedThreadPool(threads);
      } else {
        pool = Executors.newSingleThreadExecutor();
      }

      List<Future<?>> futures = new ArrayList<Future<?>>(runningWorkflows.size());
      // loop over running workflows and check their status
      for (WorkflowRun wr : runningWorkflows) {
        futures.add(pool.submit(new CheckerThread(wr)));
      }
      for (Future<?> future : futures) {
        try {
          future.get();
        } catch (InterruptedException ex) {
          Log.fatal(ex);
        } catch (ExecutionException ex) {
          Log.fatal(ex);
        }
      }

      pool.shutdown();
    }
    return ret;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReturnValue clean_up() {
    return classReturnValue;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String get_description() {
    return "This plugin lets you monitor the status of running workflows and updates "
        + "the metadata object with their status.  Keep in mind a few things: 1) if the status command is specified no data "
        + "will be saved to the DB, this tool is just useful for gathering error reports, 2) status commands that are malformed "
        + "or whose status directory is not present on the filesystem will be skipped and an error noted, 3) by default every running or unknown "
        + "workflow_run in the database will be checked if they are owned by the username in your .seqware/settings file "
        + "and the hostname is the same as 'hostname --long', and 4) you can force the checking of workflows with a particular "
        + "host value but be careful with that.";
  }

  /**
   * This takes like 15 seconds per check!
   * 
   * @param statusCmd
   * @return
   */
  private ReturnValue checkWorkflow(String statusCmd) {

    Log.info("Checking the status using " + statusCmd);
    WorkflowTools workflowTools = new WorkflowTools();
    String statusDir = findStatusDir(statusCmd);
    ReturnValue ret = workflowTools.watchWorkflow(statusCmd, statusDir, 1);
    Log.info("OUT: " + ret.getStdout());
    Log.info("ERR: " + ret.getStderr());
    Log.info("STATUS: " + ret.getExitStatus());

    return (ret);

  }

  private String findStatusDir(String statusCmd) {

    String statusDir = null;
    if (statusCmd == null || "".equals(statusCmd)) {
      return (statusDir);
    }
    Pattern p = Pattern.compile("pegasus-status -l (\\S+)");
    Matcher m = p.matcher(statusCmd);

    if (m.find()) {
      statusDir = m.group(1);
    }
    return (statusDir);

  }

  protected Metadata getMetadata() {
    return metadata;
  }

  private final class CheckerThread implements Runnable {

    private final WorkflowRun wr;

    protected CheckerThread(WorkflowRun wr) {
      this.wr = wr;
    }

    @Override
    public void run() {

      Log.info("ownerUserName: " + wr.getOwnerUserName());
      Log.info("workflowAccession: " + wr.getWorkflowAccession());
      Log.info("workflowRunID: " + wr.getWorkflowRunId());

      // check that this workflow run matches the specified workflow if provided
      if (options.has("workflow-accession") && options.valueOf("workflow-accession") != null
          && !((String) options.valueOf("workflow-accession")).equals(wr.getWorkflowAccession().toString())) {
        return;
      }

      // check if this workflow run accession matches if provided
      if (options.has(WORKFLOW_RUN_ACCESSION)) {
          List<Integer> valuesOf = (List<Integer>) options.valuesOf(WORKFLOW_RUN_ACCESSION);
          if(!valuesOf.contains(wr.getSwAccession())){
              return;
          }
      }

      // check the host is either overridden or this is the same host the
      // workflow was launched from
      if (options.has("force-host") && options.valueOf("force-host") != null
          && !((String) options.valueOf("force-host")).equals(wr.getHost())) {
        return;
      } else if (!options.has("force-host") && WorkflowStatusChecker.this.hostname != null
          && !WorkflowStatusChecker.this.hostname.equals(wr.getHost())) {
        return;
      }

      // check the rest API username from SeqWare settings is the same username
      // in the DB
      if (WorkflowStatusChecker.this.username == null || wr.getOwnerUserName() == null
          || !WorkflowStatusChecker.this.username.equals(wr.getOwnerUserName())) {
        return;
      }

          if (Engines.isOozie(wr.getWorkflowEngine())) {
              checkOozie();
          } else {
              checkPegasus();
          }
      }

    private void checkOozie() {
      try {
        OozieClient oc = new OozieClient((String) config.get("OOZIE_URL"));
        String jobId = wr.getStatusCmd();
        if (jobId == null){
          handlePreLaunch(); return;
        }

        WorkflowJob wfJob = oc.getJobInfo(jobId);
        if (wfJob == null){
          throw new IllegalStateException("No Oozie job found for WorkflowRun: swid="+wr.getSwAccession()+" oozie-id="+jobId);
        }

        WorkflowRunStatus curSqwStatus = wr.getStatus();
        WorkflowRunStatus nextSqwStatus;
        
        if (curSqwStatus == null){
          nextSqwStatus = convertOozieToSeqware(wfJob.getStatus());
        } else{
          switch(curSqwStatus){
          case submitted_cancel:
          {
            switch (wfJob.getStatus()) {
            case PREP:
            case RUNNING:
            case SUSPENDED:
              // Note: here we treat SUSPENDED as running, so that it can be killed
              oc.kill(jobId);
              nextSqwStatus = WorkflowRunStatus.cancelled;
              break;
            default:
              // Let others propagate as normal
              nextSqwStatus = convertOozieToSeqware(wfJob.getStatus());
            }
            break;
          }
          case submitted_retry:
          {
            switch(wfJob.getStatus()){
            case SUSPENDED:
              oc.resume(jobId);
              nextSqwStatus = WorkflowRunStatus.pending;
              break;
            case FAILED:
            case KILLED:
              Properties conf = getCurrentConf(wfJob);
              conf.setProperty(OozieClient.RERUN_FAIL_NODES, "true");
              oc.reRun(jobId, conf);
              nextSqwStatus = WorkflowRunStatus.pending;
              break;
            default:
              // Let others propagate as normal
              nextSqwStatus = convertOozieToSeqware(wfJob.getStatus());
            }
            break;
          }
          default:
            nextSqwStatus = convertOozieToSeqware(wfJob.getStatus());
          }
        }

        String err;
        String out;

        if (wr.getWorkflowEngine().equals("oozie-sge")) {
          File dir = OozieJob.scriptsDir(wr.getCurrentWorkingDir());
          Set<String> extIds = sgeIds(wfJob);
          out = sgeConcat(sgeFiles(SGE_OUT_FILE, dir, extIds));
          err = sgeConcat(sgeFiles(SGE_ERR_FILE, dir, extIds));
        } else {
          StringBuilder sb = new StringBuilder();
          for (WorkflowAction action : wfJob.getActions()) {
            if (action.getErrorMessage() != null) {
              sb.append(MessageFormat.format("   Name: {0} Type: {1} ErrorMessage: {2}\n", action.getName(),
                                             action.getType(), action.getErrorMessage()));
            }
          }
          out = "";
          err = sb.toString();
        }

        synchronized (metadata_sync) {
          wr.setStatus(nextSqwStatus);
          wr.setStdErr(err);
          wr.setStdOut(out);
          WorkflowStatusChecker.this.metadata.updateWorkflowRun(wr);
        }
      } catch (RuntimeException e) {
        throw e;
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    
    private void handlePreLaunch(){
      switch(wr.getStatus()){
      case submitted_cancel:
        // run cancelled before launching
        wr.setStatus(WorkflowRunStatus.cancelled);
        synchronized (metadata_sync) {
          WorkflowStatusChecker.this.metadata.updateWorkflowRun(wr);
        }
        break;
      case submitted_retry:
        // retrying a pre-launch cancellation
        wr.setStatus(WorkflowRunStatus.submitted);
        synchronized (metadata_sync) {
          WorkflowStatusChecker.this.metadata.updateWorkflowRun(wr);
        }
        break;
      default:
          throw new IllegalStateException("No Oozie job ID found for WorkflowRun: swid="+wr.getSwAccession()+" status="+wr.getStatus().name());
      }
    }

    @SuppressWarnings("deprecation")
    private Properties getCurrentConf(WorkflowJob wfJob){
      /*
       * Why this method is needed:
       * 
       * To rerun an oozie job, one must pass in a Properties instance.
       * 
       * The current conf of a WorkflowJob is only exposed via getConf() which
       * does not return a Properties instance, but rather a String of XML.
       * 
       * The XML is not of a Properties, but rather of a hadoop Configuration!
       * 
       * A hadoop Configuration instance cannot be loaded from a String, but
       * only from resources or an input stream.
       * 
       * Further, a hadoop Configuration instance does not expose a public
       * method for obtaining a Properties representation.
       * 
       * It does expose an iterator of Map.Entry objects (which is internally
       * obtained from a Properties instance!).
       * 
       * It'd be swell if these guys could just pick one representation, or at
       * least an easy way to convert between them.
       */
      Configuration conf = new Configuration(false);
      conf.addResource(new StringBufferInputStream(wfJob.getConf()));
      Properties props = new Properties();
      for(Map.Entry<String, String> e : conf){
        props.setProperty(e.getKey(), e.getValue());
      }
      return props;
    }

    private WorkflowRunStatus convertOozieToSeqware(WorkflowJob.Status oozieStatus){
      WorkflowRunStatus sqwStatus;
      /*
       * There's no analog to SUSPENDED on the seware side, treating as failed so it can be picked up for retry
       */
      switch (oozieStatus) {
      case PREP:
      case RUNNING:
      case SUSPENDED:
        sqwStatus = WorkflowRunStatus.running;
        break;
      case FAILED:
        sqwStatus = WorkflowRunStatus.failed;
        break;
      case KILLED:
        sqwStatus = WorkflowRunStatus.cancelled;
        break;
      case SUCCEEDED:
        sqwStatus = WorkflowRunStatus.completed;
        break;
      default:
        throw new RuntimeException("Unexpected oozie status value: " + oozieStatus);
      }
      return sqwStatus;
    }

    private void checkPegasus() {
      if (wr.getStatus() != null) {
        switch(wr.getStatus()){
        case submitted_cancel:
        case submitted_retry:
          // This should be prevented from ever happening on the submit-side.
          throw new RuntimeException("cancel/retry not supported with pegasus engine.");
        default: // continue
        }
      }
      
      // check the owner of the status dir
      boolean dirOwner = true;
      String statusDir = findStatusDir(wr.getStatusCmd());
      if (statusDir != null && !FileTools.isFileOwner(statusDir)) {
        dirOwner = false;
        Log.info("You don't own the status directory: " + wr.getStatusCmd());
      } else if (statusDir == null) {
        dirOwner = false;
        Log.info("The status directory can't be parsed!: " + wr.getStatusCmd());
      }

      if (dirOwner) {
        ReturnValue currRet = checkWorkflow(wr.getStatusCmd());
        if (currRet.getExitStatus() == ReturnValue.SUCCESS) {
          synchronized (metadata_sync) {
            WorkflowStatusChecker.this.metadata.update_workflow_run(wr.getWorkflowRunId(), wr.getCommand(),
                                                                    wr.getTemplate(), WorkflowRunStatus.completed, wr.getStatusCmd(),
                                                                    wr.getCurrentWorkingDir(), wr.getDax(),
                                                                    wr.getIniFile(), wr.getHost(), currRet.getStderr(),
                                                                    currRet.getStdout(), wr.getWorkflowEngine(), wr.getInputFileAccessions());
          }

        } else if (currRet.getExitStatus() == ReturnValue.PROCESSING) {
          synchronized (metadata_sync) {
            WorkflowStatusChecker.this.metadata.update_workflow_run(wr.getWorkflowRunId(), wr.getCommand(),
                                                                    wr.getTemplate(), WorkflowRunStatus.running, wr.getStatusCmd(),
                                                                    wr.getCurrentWorkingDir(), wr.getDax(),
                                                                    wr.getIniFile(), wr.getHost(), currRet.getStderr(),
                                                                    currRet.getStdout(), wr.getWorkflowEngine(), wr.getInputFileAccessions());
          }

        } else if (currRet.getExitStatus() == ReturnValue.FAILURE) {
          Log.error("WORKFLOW FAILURE: this workflow has failed and this status will be saved to the DB.");
          synchronized (metadata_sync) {
            WorkflowStatusChecker.this.metadata.update_workflow_run(wr.getWorkflowRunId(), wr.getCommand(),
                                                                    wr.getTemplate(), WorkflowRunStatus.failed, wr.getStatusCmd(),
                                                                    wr.getCurrentWorkingDir(), wr.getDax(),
                                                                    wr.getIniFile(), wr.getHost(), currRet.getStderr(),
                                                                    currRet.getStdout(), wr.getWorkflowEngine(), wr.getInputFileAccessions());
          }
        } else if (currRet.getExitStatus() == ReturnValue.UNKNOWN) {
          Log.error("ERROR: the workflow status has returned UNKNOWN, this is typically if the workflow status command points"
              + "to a non-existant directory or a directory that is not writable or owned by you. No information will be saved to the"
              + "DB since the workflow state cannot be determined!");

        }
      }
    }

  }

  private static final Pattern SGE_OUT_FILE = Pattern.compile(".+\\.o(\\d+)");
  private static final Pattern SGE_ERR_FILE = Pattern.compile(".+\\.e(\\d+)");

  private static SortedMap<Integer, File> sgeFiles(Pattern p, File dir, final Set<String> extIds) {
    SortedMap<Integer, File> idFiles = new TreeMap<Integer, File>();
    for (File f : dir.listFiles()) {
      Matcher m = p.matcher(f.getName());
      if (m.find()) {
        String id = m.group(1);
        if (extIds.contains(id)) {
          idFiles.put(Integer.parseInt(id), f);
        }
      }
    }
    return idFiles;
  }

  private static final Pattern SGE_FILE = Pattern.compile("(.+)\\.[eo]\\d+");
  private static String sgeConcat(SortedMap<Integer, File> idFiles) {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<Integer, File> e : idFiles.entrySet()) {
      File f = e.getValue();
      
      Matcher m = SGE_FILE.matcher(f.getName());
      m.find();
      String jobName = m.group(1);
      
      sb.append("-----------------------------------------------------------------------");
      sb.append("\nJob Name: ");
      sb.append(jobName);
      sb.append("\nJob ID:   ");
      sb.append(e.getKey());
      sb.append("\nFile:     ");
      sb.append(f.getAbsolutePath());
      sb.append("\nUpdated:  ");
      sb.append(new Date(f.lastModified()));
      sb.append("\nContents:\n");
      try {
        sb.append(FileUtils.readFileToString(f));
      } catch (IOException ex) {
        sb.append(" *** ERROR READING FILE: ");
        sb.append(ex.getMessage());
        sb.append(" ***");
      }
      if (sb.charAt(sb.length()-1) != '\n'){
        sb.append("\n");
      }
      sb.append("-----------------------------------------------------------------------\n\n");
    }
    return sb.toString();
  }
  
  private static Set<String> sgeIds(WorkflowJob wf){
    List<WorkflowAction> actions = wf.getActions();
    final Set<String> extIds = new HashSet<String>();
    for (WorkflowAction a : actions) {
      String extId = a.getExternalId();
      if (a != null) {
        extIds.add(extId);
      }
    }
    return extIds;
  }

}
