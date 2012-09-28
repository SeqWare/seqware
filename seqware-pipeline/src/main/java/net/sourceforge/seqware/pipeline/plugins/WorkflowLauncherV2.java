/**
 * @author briandoconnor@gmail.com
 *
 * The WorkflowLauncher is responsible for launching workflows with or without
 * metadata writeback.
 *
 */
package net.sourceforge.seqware.pipeline.plugins;

import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo;
import net.sourceforge.seqware.pipeline.bundle.Bundle;
import net.sourceforge.seqware.pipeline.bundle.BundleInfo;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.pipeline.plugin.WorkflowPlugin;
import net.sourceforge.seqware.pipeline.workflow.BasicWorkflow;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowEngine;
import net.sourceforge.seqware.pipeline.workflowV2.WorkflowClassFinder;
import net.sourceforge.seqware.pipeline.workflowV2.model.Workflow2;
import net.sourceforge.seqware.pipeline.workflowV2.model.Workflow;
import net.sourceforge.seqware.pipeline.workflowV2.pegasus.PegasusWorkflowEngine;
import net.sourceforge.seqware.pipeline.workflowV2.pegasus.PegasusWorkflowEngine1;

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
    }

    public String get_description() {
    	return ("A plugin that lets you launch workflow bundles once you have installed them via the BundleManager.");
    }

    @Override
    public BasicWorkflow createWorkflow() {
    	return new PegasusWorkflowEngine1(metadata, config);
    }
    
    /*
     * (non-Javadoc) @see
     * net.sourceforge.seqware.pipeline.plugin.PluginInterface#do_run()
     */
    @Override
    public ReturnValue do_run() {

    	// set up workflow engine
    	AbstractWorkflowEngine engine = new PegasusWorkflowEngine();
    	
    	// load abstractWorkflowDataModel
    	AbstractWorkflowDataModel dataModel = this.loadDataModel();
    	engine.launchWorkflow(dataModel);
    	// parse workflowobjectmodel defined by workflow author
    	
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
			for (String t : tokens) {
			    parentAccessions.add(t);
			}
		    }
		}
	
		// link-workflow-run-to-parents
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
		// and want to pass in arguments on the command line rather than ini
		// file
		List<String> nonOptions = options.nonOptionArguments();
		Log.info("EXTRA OPTIONS: " + nonOptions.size());
	
		// THE MAIN ACTION HAPPENS HERE
		if (options.has("workflow-accession") && options.has("ini-files")) {
	
		    // then you're scheduling a workflow that has been installed
		    if (options.has("schedule")) {
			Log.info("You are scheduling a workflow to run by adding it to the metadb.");
			ret = w.scheduleInstalledBundle(
				(String) options.valueOf("workflow-accession"),
				(String) options.valueOf("workflow-run-accession"),
				iniFiles, metadataWriteback, parentAccessions,
				parentsLinkedToWR, false, nonOptions);
		    } else {
			// then your running locally but taking info saved in the
			// workflow table from the DB
			Log.info("You are running a workflow installed in the metadb on the local computer.");
			ret = w.launchInstalledBundle(
				(String) options.valueOf("workflow-accession"),
				(String) options.valueOf("workflow-run-accession"),
				iniFiles, metadataWriteback, parentAccessions,
				parentsLinkedToWR, options.has("wait"), nonOptions);
		    }
	
		} else if ((options.has("bundle") || options
			.has("provisioned-bundle-dir"))
			&& options.has("workflow")
			&& options.has("version") && options.has("ini-files")) {
	
		    // then your launching direclty and not something that has been
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
		    ret = w.launchBundle(workflow, version, metadataFile, bundlePath,
			    iniFiles, false, new ArrayList<String>(),
			    new ArrayList<String>(), options.has("wait"), nonOptions);
	
		} else if (options.has("launch-scheduled")) {
		    // check to see if this code is already running, if so exit
		    try {
			JUnique.acquireLock(appID);
		    } catch (AlreadyLockedException e) {
			Log.error(
				"I could not get a lock for "
					+ appID
					+ " this most likely means the application is alredy running and this instance will exit!",
				e);
			ret.setExitStatus(ReturnValue.FAILURE);
		    }
		    // LEFT OFF HERE, not sure if the workflow will come back from the
		    // web service!?
	
		    // then you are either launching all workflows scheduled in the DB
		    // workflow_run table or just particular ones
		    List<String> scheduledAccessions = (List<String>) options
			    .valuesOf("launch-scheduled");
	
		    // BIG ISSUE: HOW DO YOU GO FROM WORKFLOW_RUN BACK TO WORKFLOW VIA
		    // WEB SERVICE!?
	
		    // then need to loop over these and just launch those workflows or
		    // launch all if accession not specified
		    List<WorkflowRun> scheduledWorkflows = this.metadata
			    .getWorkflowRunsByStatus("submitted");
	
		    Log.stdout("Number of submitted workflows: "
			    + scheduledWorkflows.size());
	
		    for (WorkflowRun wr : scheduledWorkflows) {
			Log.stdout("Working Run: " + wr.getSwAccession());
			if (scheduledAccessions.isEmpty()
				|| (scheduledAccessions.size() > 0 && scheduledAccessions
					.contains(wr.getSwAccession().toString()))) {
			    if (!options.has("host")
				    || (options.has("host")
					    && options.valueOf("host") != null && options
					    .valueOf("host").equals(wr.getHost()))) {
				WorkflowRun wrWithWorkflow = this.metadata
					.getWorkflowRunWithWorkflow(wr.getSwAccession()
						.toString());
				w.launchScheduledBundle(wrWithWorkflow.getWorkflow()
					.getSwAccession().toString(), wr
					.getSwAccession().toString(),
					metadataWriteback, options.has("wait"));
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
    
    
    private WorkflowInfo setupWorkflowInfo() {
    	if ((options.has("bundle") || options
    			.has("provisioned-bundle-dir"))
    			&& options.has("workflow")
    			&& options.has("version") && options.has("ini-files")) {
		    // then your launching direclty and not something that has been
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
    		File metadataFileObj = null;
    		if (metadataFile != null) {
    		    metadataFileObj = new File(metadataFile);
    		}
    		// pull back information from metadata
    		Bundle bundleUtil = new Bundle(metadata, config);
    		Log.info("Bundle: " + bundlePath);
    		BundleInfo bundleInfo = bundleUtil.getBundleInfo(new File(bundlePath),
    			metadataFileObj);
    		for (WorkflowInfo wi : bundleInfo.getWorkflowInfo()) {

    		    Log.info("Workflow: " + wi.getName() + " Version: "
    			    + wi.getVersion());

    		    if (wi.getName().equals(workflow)
    			    && wi.getVersion().equals(version)) {

	    			Log.info("Match!");
	    			// then this is the workflow we need to run
	    			String bundleoutPath = bundleUtil.getOutputDir();	
	    			wi.setWorkflowDir(bundleoutPath);	
	    			return wi;
	    		 }
    		}
    	}
    	return null;
    }
    
    private AbstractWorkflowDataModel loadDataModel() {
    	AbstractWorkflowDataModel res = null;
    	//parse metadata.xml
    	WorkflowInfo wfi = this.setupWorkflowInfo();
    	String clazzPath = wfi.getClassesDir();
    	clazzPath = clazzPath.replaceFirst("\\$\\{workflow_bundle_dir\\}",
    			wfi.getWorkflowDir());
    	Log.info("CLASSPATH: " + clazzPath);
    	// get user defined classes
    	WorkflowClassFinder finder = new WorkflowClassFinder();
    	Class<?> clazz = finder.findFirstWorkflowClass(clazzPath);
    	if (null != clazz) {
    	    Log.debug("using java object");
    	    try {
	    		Object object = clazz.newInstance();
	    		res = (AbstractWorkflowDataModel) object;
    	    } catch (InstantiationException ex) {
    	    	Log.error(ex);
    	    } catch (IllegalAccessException ex) {
    	    	Log.error(ex);
    	    }  catch (SecurityException ex) {
    	    	Log.error(ex);
    	    } catch (IllegalArgumentException ex) {
    	    	Log.error(ex);
    	    } 
    	}
    	if(res == null)
    		return null;
    	//TODO should these set method defined as private and load the field using reflection?
    	//set command line options
    	res.setCmdOptions(new ArrayList<String>(Arrays.asList(this.params)));
    	//set workflowInfo
    	res.setWorkflowInfo(wfi);

    	//set Workflow
    	Workflow workflow = new Workflow();
    	res.setWorkflow(workflow);
		// figure out what was passed as params and make structs to pass to the
		// workflow layer
		// metadata
		boolean metadataWriteback = true;
		if (options.has("no-metadata") || options.has("no-meta-db")) {
		    metadataWriteback = false;
		}
		//this.setPrivateField(res, field, value);
		//res.setMetadataWriteBack(metadataWriteback);

    	return res;
    }
    
    private void setPrivateField(AbstractWorkflowDataModel dataModel, String field, Object value) {
    	
    }
}
