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

import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowTools;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import org.openide.util.lookup.ServiceProvider;

/**
 * This plugin lets you monitor the status of running workflows and updates the
 * metadata object with their status.
 *
 * @author boconnor
 */
@ServiceProvider(service = PluginInterface.class)
public class WorkflowStatusChecker extends Plugin {

    ReturnValue ret = new ReturnValue();
    // NOTE: this is shared with WorkflowLauncher so only one can run at a time
    String appID = "net.sourceforge.seqware.pipeline.plugins.WorkflowStatusCheckerOrLauncher";

    public WorkflowStatusChecker() {
        super();
        parser.acceptsAll(Arrays.asList("status-cmd", "s"),
                "Optional: the Pegasus status command").withRequiredArg();
        parser.acceptsAll(Arrays.asList("workflow-run-accession", "wra"), "Optional: this will cause the program to only check the status of this particular workflow run.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("host"), "Optional: if specified, only workflow runs scheduled to the specified host will be checked.").withRequiredArg();

        ret.setExitStatus(ReturnValue.SUCCESS);
    }

    @Override
    public ReturnValue init() {

        // check to see if this code is already running, if so exit
        try {
            JUnique.acquireLock(appID);
        } catch (AlreadyLockedException e) {
            Log.error("I could not get a lock for " + appID
                    + " this most likely means the application is alredy running and this instance will exit!");
            ret.setExitStatus(ReturnValue.FAILURE);
        }
        return ret;

    }

    @Override
    public ReturnValue do_test() {
        return ret;
    }

    @Override
    public ReturnValue do_run() {

        ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);

        // lets you just check a given workflow without metadata
        if (options.has("status-cmd") && options.valueOf("status-cmd") != null) {

            ret = checkWorkflow((String) options.valueOf("status-cmd"));

        } else { // this checks workflows and writes their status back to the DB
            
            // get a list of running workflows
            List<WorkflowRun> runningWorkflows = this.metadata.getWorkflowRunsByStatus(metadata.RUNNING);
            runningWorkflows.addAll(this.metadata.getWorkflowRunsByStatus(metadata.PENDING));

            // loop over running workflows and check their status
            for (WorkflowRun wr : runningWorkflows) {

                boolean process = false;

                if (options.has("host") && options.valueOf("host") != null
                        && ((String) options.valueOf("host")).equals(wr.getHost())) {
                    process = true;
                }

                if (options.has("workflow-run-accession") && options.valueOf("workflow-run-accession") != null
                        && ((String) options.valueOf("workflow-run-accession")).equals(wr.getSwAccession().toString())) {
                    process = true;
                }

                if (!options.has("host") && !options.has("workflow-run-accession")) {
                    process = true;
                }

                if (process) {
                    ReturnValue currRet = checkWorkflow(wr.getStatusCmd());
                    if (currRet.getExitStatus() == ReturnValue.SUCCESS) {
                        this.metadata.update_workflow_run(wr.getWorkflowRunId(), wr.getCommand(), wr.getTemplate(), "completed",
                                wr.getStatusCmd(), wr.getCurrentWorkingDir(), wr.getDax(), wr.getIniFile(),
                                wr.getHost(), Integer.parseInt(currRet.getAttribute("currStep")),
                                Integer.parseInt(currRet.getAttribute("totalSteps")), currRet.getStderr(), currRet.getStdout());

                    } else if (currRet.getExitStatus() == ReturnValue.PROCESSING) {
                        this.metadata.update_workflow_run(wr.getWorkflowRunId(), wr.getCommand(), wr.getTemplate(), "running",
                                wr.getStatusCmd(), wr.getCurrentWorkingDir(), wr.getDax(), wr.getIniFile(),
                                wr.getHost(), Integer.parseInt(currRet.getAttribute("currStep")),
                                Integer.parseInt(currRet.getAttribute("totalSteps")),
                                currRet.getStderr(), currRet.getStdout());
                        
                    } else if (currRet.getExitStatus() == ReturnValue.FAILURE) {
                        Log.error("ERROR: problems watching workflow");
                        // need to save back to the DB if watching

                        this.metadata.update_workflow_run(wr.getWorkflowRunId(), wr.getCommand(), wr.getTemplate(), "failed",
                                wr.getStatusCmd(), wr.getCurrentWorkingDir(), wr.getDax(), wr.getIniFile(),
                                wr.getHost(), Integer.parseInt(currRet.getAttribute("currStep")),
                                Integer.parseInt(currRet.getAttribute("totalSteps")),
                                currRet.getStderr(), currRet.getStdout());

                        ret.setExitStatus(ReturnValue.FAILURE);
                        return (ret);
                    }
                }
            }
        }
        return ret;
    }

    @Override
    public ReturnValue clean_up() {
        return ret;
    }

    @Override
    public String get_description() {
        return "This plugin lets you monitor the status of running workflows and updates"
                + "the metadata object with their status.";
    }

    /**
     * This takes like 15 seconds per check!
     * 
     * @param statusCmd
     * @return 
     */
    private ReturnValue checkWorkflow(String statusCmd) {

        System.out.println("Checking the status");
        WorkflowTools workflowTools = new WorkflowTools();

        Pattern p = Pattern.compile("pegasus-status -l (\\S+)");
        Matcher m = p.matcher(statusCmd);
        String statusDir = null;
        if (m.find()) {
            statusDir = m.group(1);
        }
        ReturnValue ret = workflowTools.watchWorkflow(statusCmd, statusDir, 1);

        Log.info("OUT: " + ret.getStdout());
        Log.info("ERR: " + ret.getStderr());
        Log.info("STATUS: " + ret.getExitStatus());

        return (ret);

    }
}
