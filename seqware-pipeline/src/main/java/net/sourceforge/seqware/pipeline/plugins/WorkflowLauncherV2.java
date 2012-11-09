/**
* @author briandoconnor@gmail.com
*
* The WorkflowLauncher is responsible for launching workflows with or without
* metadata writeback.
*
*/
package net.sourceforge.seqware.pipeline.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.pipeline.plugin.WorkflowPlugin;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowEngine;
import net.sourceforge.seqware.pipeline.workflowV2.WorkflowDataModelFactory;
import net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.OozieWorkflowEngine;
import net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.PegasusWorkflowEngine;
import org.openide.util.lookup.ServiceProvider;

/**
* @author yongliang ProviderFor(PluginInterface.class)
*
* TODO: validate at all the option below (especially
* link-parent-to-workflow-run) actually work!
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
	     parser
	        .accepts(
	            "workflow-engine",
	            "Optional: Specifies a workflow engine, we support Oozie and Pegasus. Default is Pegasus.")
	        .withRequiredArg().ofType(String.class).describedAs("Workflow Engine");
	     parser.accepts("status", "Optional: Get the workflow status by ID").withRequiredArg().ofType(String.class).describedAs("Job ID");
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

	     WorkflowDataModelFactory factory = new WorkflowDataModelFactory(options, config, params, metadata);
	     AbstractWorkflowDataModel dataModel = factory.getWorkflowDataModel();
	    
	     // set up workflow engine
	     AbstractWorkflowEngine engine = this.getWorkflowEngine(dataModel);
	 	 if(options.has("status")) {
	 		 Log.stdout("status: " + engine.getStatus((String)options.valueOf("status")));
		 	return new ReturnValue(ReturnValue.SUCCESS);
		 }
	     
	 	 ReturnValue retPegasus = engine.launchWorkflow(dataModel);
	     if(!Boolean.parseBoolean(dataModel.getConfigs().get("metadata"))) {
	     return retPegasus;
	     }
	     // metadataWriteback
	     String wra = dataModel.getConfigs().get("workflow-run-accession");
	    
	     if(wra==null || wra.isEmpty()) {
	     return retPegasus;
	     }
	     
	     int workflowrunId = Integer.parseInt(wra);
	     int workflowrunaccession = this.metadata.get_workflow_run_accession(workflowrunId);
	     //int workflowrun = this.metadata.get_workflow_run_id(workflowrunaccession);
	    
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
	
	     List<String> parentsLinkedToWR = new ArrayList<String>();
		 	if (options.has("link-workflow-run-to-parents")) {
			    List opts = options.valuesOf("link-workflow-run-to-parents");
			    for (Object opt : opts) {
				String[] tokens = ((String) opt).split(",");
				for (String t : tokens) {
				    parentsLinkedToWR.add(t);
				}
			    }
			}
		 	
		WorkflowRun wr = null;
		
		
		// need to figure out workflow_run_accession
		// need to link all the parents to this workflow run accession
		for (String parentLinkedToWR : parentsLinkedToWR) {
			try {
				this.metadata.linkWorkflowRunAndParent(workflowrunId,
				Integer.parseInt(parentLinkedToWR));
			} catch (Exception e) {
				Log.error(e.getMessage());
			}
		}
		
		// need to pull back the workflow run object since some fields may
		// already be set
		// and we need to use their values before writing back to the DB!
		wr = metadata.getWorkflowRun(workflowrunaccession);
	
	     if (retPegasus.getProcessExitStatus() != ReturnValue.SUCCESS
	    		 || statusCmd == null) {
	     // then something went wrong trying to call pegasus
			metadata.update_workflow_run(workflowrunId, dataModel.getTags().get("workflow_command"),
				dataModel.getTags().get("workflow_template"), "failed", statusCmd,
				dataModel.getWorkflowBundleDir(), "",
				"", wr.getHost(), 0, 0,
				retPegasus.getStderr(), retPegasus.getStdout());
	    
			return (retPegasus);
	     } else {
		     metadata.update_workflow_run(workflowrunId, dataModel.getTags().get("workflow_command"),
			     dataModel.getTags().get("workflow_template"), "completed",
			     statusCmd, dataModel.getWorkflowBundleDir(), "","", wr
			     .getHost(), 0, 0,
			     retPegasus.getStderr(), retPegasus.getStdout());
		     return ret;
	     }
    
    }
    
    private AbstractWorkflowEngine getWorkflowEngine(AbstractWorkflowDataModel dataModel) {
    	AbstractWorkflowEngine wfEngine = null;
    	String engine = dataModel.getWorkflow_engine();
    	if(engine == null || !engine.equals("oozie")) {
    		wfEngine = new PegasusWorkflowEngine();
    	} else {
    		wfEngine = new OozieWorkflowEngine(dataModel);
    	}
    	return wfEngine;
    }
}


