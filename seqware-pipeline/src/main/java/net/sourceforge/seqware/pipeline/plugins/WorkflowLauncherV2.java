/**
 * @author briandoconnor@gmail.com
 *
 * The WorkflowLauncher is responsible for launching workflows with or without
 * metadata writeback.
 *
 */
package net.sourceforge.seqware.pipeline.plugins;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.maptools.MapTools;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowTools;
import net.sourceforge.seqware.pipeline.bundle.Bundle;
import net.sourceforge.seqware.pipeline.bundle.BundleInfo;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.pipeline.plugin.WorkflowPlugin;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowEngine;
import net.sourceforge.seqware.pipeline.workflowV2.WorkflowClassFinder;
import net.sourceforge.seqware.pipeline.workflowV2.WorkflowDataModelFactory;
import net.sourceforge.seqware.pipeline.workflowV2.WorkflowXmlParser;
import net.sourceforge.seqware.pipeline.workflowV2.model.Workflow;
import net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.PegasusWorkflowEngine;
import net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.StringUtils;

import org.openide.util.lookup.ServiceProvider;

/**
 * @author yongliang ProviderFor(PluginInterface.class)
 * 
 *         TODO: validate at all the option below (especially
 *         link-parent-to-workflow-run) actually work!
 */
@ServiceProvider(service = PluginInterface.class)
public class WorkflowLauncherV2 extends WorkflowPlugin {

    public WorkflowLauncherV2() {
    	super();
    	parser
        .accepts(
            "metadata-output-file-prefix",
            "Optional: Specifies a path to prepend to every file returned by the module. Useful for dealing when staging files back.")
        .withRequiredArg().ofType(String.class).describedAs("Path to prepend to each file location.");
    	parser
        .accepts(
            "metadata-output-dir",
            "Optional: Specifies a path to prepend to every file returned by the module. Useful for dealing when staging files back.")
        .withRequiredArg().ofType(String.class).describedAs("Path to prepend to each file location.");
    }

    public String get_description() {
    	return ("A plugin that lets you launch workflow bundles once you have installed them via the BundleManager.");
    }

  
    /*
     * (non-Javadoc) @see
     * net.sourceforge.seqware.pipeline.plugin.PluginInterface#do_run()
     */
    @Override
    public ReturnValue do_run() {

    	// set up workflow engine
    	AbstractWorkflowEngine engine = new PegasusWorkflowEngine();
    	WorkflowDataModelFactory factory = new WorkflowDataModelFactory(options, config, params);
    	AbstractWorkflowDataModel dataModel = factory.getWorkflowDataModel();
    	
    	ReturnValue retPegasus = engine.launchWorkflow(dataModel);
    	// metadataWriteback
    	// figure out the status command
    	String stdOut = retPegasus.getStdout();
    	Pattern p = Pattern.compile("(pegasus-status -l \\S+)");
    	Matcher m = p.matcher(stdOut);
    	String statusCmd = null;
    	if (m.find()) {
    	    statusCmd = m.group(1);
    	}

    	// look for the status directory
    	p = Pattern.compile("pegasus-status -l (\\S+)");
    	m = p.matcher(stdOut);
    	String statusDir = null;
    	if (m.find()) {
    	    statusDir = m.group(1);
    	}
    	
/*    	// keep this id handy
    	int workflowRunId = 0;
    	int workflowRunAccessionInt = 0;
    	int workflowAccession = 0;
    	String workflowRunAccession = null;
    	List<String> parentsLinkedToWR = new ArrayList<String>();
    	WorkflowInfo wi = dataModel.getWorkflowInfo();
		// metadata
		boolean metadataWriteback = true;
		if (options.has("no-metadata") || options.has("no-meta-db")) {
		    metadataWriteback = false;
		}
		// if we're doing metadata writeback will need to parameterize the
		// workflow correctly
		WorkflowRun wr = null;
		if (metadataWriteback) {

		    // need to figure out workflow_run_accession
		    workflowAccession = wi.getWorkflowAccession();
		    // create the workflow_run row if it doesn't exist
		    if (workflowRunAccession == null) {
				workflowRunId = this.metadata
					.add_workflow_run(workflowAccession);
				workflowRunAccessionInt = this.metadata
					.get_workflow_run_accession(workflowRunId);
				workflowRunAccession = new Integer(workflowRunAccessionInt)
					.toString();
		    } else { // if the workflow_run row exists get the workflow_run_id
				workflowRunId = this.metadata.get_workflow_run_id(Integer
					.parseInt(workflowRunAccession));
				workflowRunAccessionInt = this.metadata
					.get_workflow_run_accession(workflowRunId);
		    }

		    // need to link all the parents to this workflow run accession
		    for (String parentLinkedToWR : parentsLinkedToWR) {
				try {
				    this.metadata.linkWorkflowRunAndParent(workflowRunId,
					    Integer.parseInt(parentLinkedToWR));
				} catch (Exception e) {
				    Log.error(e.getMessage());
				}
		    }

	    	// need to pull back the workflow run object since some fields may
	    	// already be set
	    	// and we need to use their values before writing back to the DB!
	    	wr = metadata.getWorkflowRun(workflowRunAccessionInt);
		}
    	if (retPegasus.getProcessExitStatus() != ReturnValue.SUCCESS
    			|| statusCmd == null) {
    		    // then something went wrong trying to call pegasus
    		    if (metadataWriteback) {
	    			metadata.update_workflow_run(workflowRunId, wi.getCommand(),
	    				wi.getTemplatePath(), "failed", statusCmd,
	    				wi.getWorkflowDir(), "",
	    				"", wr.getHost(), 0, 0,
	    				retPegasus.getStderr(), retPegasus.getStdout());
    		    }
    		    return (retPegasus);
    		}
    	
    	// wait
    	// if the user passes in --wait then hang around until the workflow
    	// finishes or fails
    	// periodically checking the status in a robust way
    	boolean success = true;
    	if (dataModel.isWait()) {
    	    success = false;

    	    // now parse out the return status from the pegasus tool
    	    ReturnValue watchedResult = null;
    	    if (statusCmd != null && statusDir != null) {
				WorkflowTools workflowTools = new WorkflowTools();			
				watchedResult = workflowTools.watchWorkflow(statusCmd,
					statusDir);
    	    }

    	    if (watchedResult.getExitStatus() == ReturnValue.SUCCESS) {
	    		success = true;
	    		if (metadataWriteback) {
	    		    metadata.update_workflow_run(workflowRunId,
	    			    wi.getCommand(), wi.getTemplatePath(), "completed",
	    			    statusCmd, wi.getWorkflowDir(), "","", wr
	    				    .getHost(), Integer.parseInt(watchedResult
	    				    .getAttribute("currStep")), Integer
	    				    .parseInt(watchedResult
	    					    .getAttribute("totalSteps")),
	    			    retPegasus.getStderr(), retPegasus.getStdout());
	    		}
	
    	    } else if (watchedResult.getExitStatus() == ReturnValue.FAILURE) {
	    		Log.error("ERROR: problems watching workflow");
	    		// need to save back to the DB if watching
	    		if (metadataWriteback) {
	    		    metadata.update_workflow_run(workflowRunId,
	    			    wi.getCommand(), wi.getTemplatePath(), "failed",
	    			    statusCmd, wi.getWorkflowDir(), "", "", wr
	    				    .getHost(), Integer.parseInt(watchedResult
	    				    .getAttribute("currStep")), Integer
	    				    .parseInt(watchedResult
	    					    .getAttribute("totalSteps")),
	    			    watchedResult.getStderr(), watchedResult
	    				    .getStdout());
	    		}
	    		ret.setExitStatus(ReturnValue.FAILURE);
    	    }
    	}
    */	
		return ret;
    }
    
    
}
