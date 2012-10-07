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
    	WorkflowDataModelFactory factory = new WorkflowDataModelFactory(metadata, options, config, params);
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
    
    private void loadMatadataXml() {
    	
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
    	if(res == null) {
    		Log.debug("using FTL");
    		String ftlPath = wfi.getTemplatePath();
    		WorkflowXmlParser xmlparser = new WorkflowXmlParser();
    		return null;
    	}
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
		res.setMetadataWriteBack(metadataWriteback);
		//set conifg, pass the config files to Map<String,String>, also put the .settings to Map<String,String>
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
		Map<String,String> map = new HashMap<String,String>();
		for(String ini: iniFiles) {
			Log.stdout("  INI FILE: "+ini);
            if ((new File(ini)).exists()) {
            	MapTools.ini2Map(ini, map);
            }
		}
		// allow the command line options to override options in the map
        // Parse command line options for additional configuration. Note that we
        // do it last so it takes precedence over the INI
		MapTools.cli2Map(this.params, map);
		
		map.put("workflow_bundle_dir",res.getWorkflowBundleDir());
      
		//set name
        res.setName(map.get("workflow"));
        //set version
        res.setVersion(map.get("version"));
		//set metadata
        //set base dir
        String basedir = res.getWorkflowBundleDir() + File.separator + "Workflow_Bundle_"+res.getName()+ File.separator + res.getVersion();
		map.put("basedir", basedir);
		// Expand variables in the map
        MapTools.mapExpandVariables(map);
        
        this.addExtraConfigs(map);
        Map<String,String> newMap = this.resolveMap(map);
        res.setConfigs(newMap);
        //call the tmplate methods
        try {
        	Method m = clazz.getDeclaredMethod("setupFiles");
        	m.invoke(res);
/*        	m = clazz.getDeclaredMethod("setupWorkflow");
        	m.invoke(res);
        	m = clazz.getDeclaredMethod("setupEnvironment");
        	m.invoke(res);*/
			m = clazz.getDeclaredMethod("buildWorkflow");
			m.invoke(res);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

        res.setWait(options.has("wait"));
    	return res;
    }
    
    
    private void addExtraConfigs(Map<String,String> map) {
        //set date
        // magic variables always set
        Date date = new Date();
        map.put("date", date.toString());

        //set random
        Random rand = new Random(System.currentTimeMillis());
        int randInt = rand.nextInt(100000000);
        map.put("random", ""+randInt);
        //copy some properties from .settings to configs
        map.put("SW_PEGASUS_CONFIG_DIR", config.get("SW_PEGASUS_CONFIG_DIR"));
        map.put("SW_DAX_DIR", config.get("SW_DAX_DIR"));
        map.put("SW_CLUSTER", config.get("SW_CLUSTER"));
    }
    
    private Map<String, String> resolveMap(Map<String, String> input) {
		Map<String, String> result = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : input.entrySet()) {
		    String value = entry.getValue();
		    if (StringUtils.hasVariable(value)) {
			value = StringUtils.replace(value, input);
		    }
		    result.put(entry.getKey(), value);
		}
		return result;
    }
}
