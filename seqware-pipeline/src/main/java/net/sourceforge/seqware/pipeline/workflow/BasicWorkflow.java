package net.sourceforge.seqware.pipeline.workflow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.metadata.MetadataNoConnection;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.common.util.maptools.MapTools;
import net.sourceforge.seqware.common.util.runtools.RunTools;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowTools;
import net.sourceforge.seqware.pipeline.bundle.Bundle;
import net.sourceforge.seqware.pipeline.bundle.BundleInfo;
import net.sourceforge.seqware.pipeline.daxgenerator.Daxgenerator;
import net.sourceforge.seqware.pipeline.workflowV2.WorkflowEngineInterface1;

public abstract class BasicWorkflow implements WorkflowEngineInterface1 {

    protected ReturnValue ret = new ReturnValue();
    protected Metadata metadata = null;
    protected Map<String, String> config = null;
    protected String outputDir = null;
    protected ArrayList<File> filesArray = new ArrayList<File>();
    protected Bundle bundleUtil = null;
    protected int totalSteps = 0;
    protected int currStep = 0;
    protected int percentage = 0;
    protected WorkflowTools workflowTools = null;

    protected enum Job {

	setup, prejob, mainjob, postjob, cleanup, statcall, data;
    }

    public BasicWorkflow(Metadata metadata, Map<String, String> config) {
	super();
	this.metadata = metadata;
	this.config = config;
	this.bundleUtil = new Bundle(metadata, config);
    }

    private ReturnValue setup() {

	workflowTools = new WorkflowTools();
	ReturnValue retVal = new ReturnValue(ReturnValue.SUCCESS);
	// initialize globus authentication proxy
	ArrayList<String> theCommand = new ArrayList<String>();
	theCommand.add("bash");
	theCommand.add("-lc");
	theCommand.add("grid-proxy-init -valid 480:00");
	ReturnValue retProxy = RunTools.runCommand(theCommand
		.toArray(new String[0]));
	if (retProxy.getExitStatus() != ReturnValue.SUCCESS) {
	    Log.error("ERROR: can't init the globus proxy so terminating here, continuing but your workflow submissions will fail!");
	    return (retProxy);
	}

	return (retVal);
    }

    /**
     * This method just needs a sw_accession value from the workflow table and
     * an ini file(s) in order to launch a workflow. All needed info is pulled
     * from the workflow table which was populated when the workflow was
     * installed.
     * 
     * @param workflowAccession
     * @return
     */
    public ReturnValue launchInstalledBundle(String workflowAccession,
	    String workflowRunAccession, ArrayList<String> iniFiles,
	    boolean metadataWriteback, ArrayList<String> parentAccessions,
	    ArrayList<String> parentsLinkedToWR, boolean wait,
	    List<String> cmdLineOptions) {

	// do basic common setup tasks
	ReturnValue setupRet = setup();
	if (setupRet.getReturnValue() != ReturnValue.SUCCESS) {
	    return (setupRet);
	}

	Map<String, String> workflowMetadata = this.metadata
		.get_workflow_info(Integer.parseInt(workflowAccession));
	WorkflowInfo wi = parseWorkflowMetadata(workflowMetadata);

	return (runWorkflow(wi, workflowRunAccession, iniFiles,
		new HashMap<String, String>(), metadataWriteback,
		parentAccessions, parentsLinkedToWR, wait, cmdLineOptions));

    }

    /**
     * This method just needs a sw_accession value from the workflow table and
     * an ini file(s) in order to schedule a workflow. All needed info is pulled
     * from the workflow table which was populated when the workflow was
     * installed. Keep in mind this does not actually trigger anything, it just
     * schedules the workflow to run by adding to the workflow_run table. This
     * lets you run workflows on a different host from where this command line
     * tool is run but requires an external process to launch workflows that
     * have been scheduled.
     * 
     * @param workflowAccession
     * @return
     */
    public ReturnValue scheduleInstalledBundle(String workflowAccession,
	    String workflowRunAccession, ArrayList<String> iniFiles,
	    boolean metadataWriteback, ArrayList<String> parentAccessions,
	    ArrayList<String> parentsLinkedToWR, boolean wait,
	    List<String> cmdLineOptions) {

	ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);

	Map<String, String> workflowMetadata = this.metadata
		.get_workflow_info(Integer.parseInt(workflowAccession));
	WorkflowInfo wi = parseWorkflowMetadata(workflowMetadata);
	scheduleWorkflow(wi, workflowRunAccession, iniFiles, metadataWriteback,
		parentAccessions, parentsLinkedToWR, wait, cmdLineOptions);

	return (ret);
    }

    /**
     * This method just needs a sw_accession value from the workflow_run table
     * to launch a workflow. All needed info is pulled from the workflow_run
     * table which was populated when the workflow was scheduled.
     * 
     * @param workflowAccession
     * @return
     */
    public ReturnValue launchScheduledBundle(String workflowAccession,
	    String workflowRunAccession, boolean metadataWriteback, boolean wait) {

	// do basic common setup tasks
	ReturnValue setupRet = setup();
	if (setupRet.getReturnValue() != ReturnValue.SUCCESS) {
	    return (setupRet);
	}

	// not sure if the workflow can be accessed via workflow_run, is it
	// defined?
	WorkflowRun wr = this.metadata
		.getWorkflowRunWithWorkflow(workflowRunAccession);
	Log.stdout("Workflow Run " + wr.getSwAccession());
	Log.stdout("Workflow: " + wr.getWorkflow().getSwAccession());
	Map<String, String> workflowMetadata = this.metadata
		.get_workflow_info(wr.getWorkflow().getSwAccession());
	WorkflowInfo wi = parseWorkflowMetadata(workflowMetadata);

	return (runScheduledWorkflow(wi, workflowRunAccession,
		metadataWriteback, wait));

    }

    /**
     * FIXME: need to add metadata writeback
     * 
     * @param workflow
     * @param version
     * @param iniFiles
     * @param bundle
     * @return
     */
    public ReturnValue launchBundle(String workflow, String version,
	    String metadataFile, String bundle, ArrayList<String> iniFiles,
	    boolean metadataWriteback, ArrayList<String> parentAccessions,
	    ArrayList<String> parentsLinkedToWR, boolean wait,
	    List<String> cmdLineOptions) {

	// do basic common setup tasks
	ReturnValue setupRet = setup();
	if (setupRet.getReturnValue() != ReturnValue.SUCCESS) {
	    return (setupRet);
	}

	File metadataFileObj = null;
	if (metadataFile != null) {
	    metadataFileObj = new File(metadataFile);
	}
	// pull back information from metadata
	Log.info("Bundle: " + bundle);
	BundleInfo bundleInfo = bundleUtil.getBundleInfo(new File(bundle),
		metadataFileObj);

	boolean found = false;

	for (WorkflowInfo wi : bundleInfo.getWorkflowInfo()) {

	    Log.info("Workflow: " + wi.getName() + " Version: "
		    + wi.getVersion());

	    if (wi.getName().equals(workflow)
		    && wi.getVersion().equals(version)) {

		Log.info("Match!");
		found = true;

		try {

		    // then this is the workflow we need to run
		    String bundlePath = bundleUtil.getOutputDir();

		    wi.setWorkflowDir(bundlePath);

		    ret = runWorkflow(wi, null, iniFiles,
			    new HashMap<String, String>(), metadataWriteback,
			    parentAccessions, parentsLinkedToWR, wait,
			    cmdLineOptions);

		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

		break;
	    }
	}

	if (!found) {
	    Log.error("Couldn't find a workflow matching " + workflow
		    + " and version " + version);
	    ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
	}

	return (ret);
    }

    /**
     * The chief goal of this method is to take all the ini info that's stored
     * in the db, correctly aggregate it, and then trigger the workflow
     * programmatically.
     * 
     * TODO - wi object needs to have perm location added to it
     * 
     * @param wi
     * @param workflowRunAccession
     * @param metadataWriteback
     * @param wait
     * @return
     */
    private ReturnValue runScheduledWorkflow(WorkflowInfo wi,
	    String workflowRunAccession, boolean metadataWriteback, boolean wait) {

	// the return value to use
	ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);

	// get the workflow run
	WorkflowRun wr = this.metadata
		.getWorkflowRunWithWorkflow(workflowRunAccession);

	// the map
	HashMap<String, String> map = new HashMap<String, String>();

	// iterate over all the generic default params
	// these params are created when a workflow is installed
	SortedSet<WorkflowParam> workflowParams = this.metadata
		.getWorkflowParams(wr.getWorkflow().getSwAccession().toString());
	for (WorkflowParam param : workflowParams) {
	    map.put(param.getKey(), param.getValue());
	}

	// FIXME: this needs to be implemented otherwise portal submitted won't
	// work!
	// now iterate over the params specific for this workflow run
	// this is where the SeqWare Portal will populate parameters for
	// a scheduled workflow
	/*
	 * workflowParams =
	 * this.metadata.getWorkflowRunParams(workflowRunAccession);
	 * for(WorkflowParam param : workflowParams) { map.put(param.getKey(),
	 * param.getValue()); }
	 */

	// Workflow Runs that are scheduled by the web service don't populate
	// their
	// params into the workflow_run_params table but, instead, directly
	// write
	// to the ini field.
	// FIXME: the web service should just use the same approach as the
	// Portal
	// and this will make it more robust to pass in the
	// parent_processing_accession
	// via the DB rather than ini_file field
	map.putAll(MapTools.iniString2Map(wr.getIniFile()));

	// will need to pull out the parent accessions since these have already
	// been set
	// and if they aren't passed in specifically they are reset
	// FIXME: going in and out of a metadata file is messy and error prone
	ArrayList<String> parentAccessions = parseParentAccessions(map);

	// don't need to pass these in since they were already updated in the DB
	// at schedule time
	// ArrayList<String> parentsLinkedToWR = parseParentsLinkedToWR(map);

	ret = runWorkflow(wi, workflowRunAccession, new ArrayList<String>(),
		map, metadataWriteback, parentAccessions,
		new ArrayList<String>(), wait, new ArrayList<String>());

	return (ret);
    }

    /**
     * This method needs to ensure the workflow has been downloaded, if not, it
     * needs to provision it it also should take a map object too so it doesn't
     * need to read ini files in order to work.
     * 
     * TODO: make sure all workflow_bundle_dir subs happen
     * 
     */
    private ReturnValue runWorkflow(WorkflowInfo wi,
	    String workflowRunAccession, ArrayList<String> iniFiles,
	    HashMap<String, String> preParsedIni, boolean metadataWriteback,
	    ArrayList<String> parentAccessions,
	    ArrayList<String> parentsLinkedToWR, boolean wait,
	    List<String> cmdLineOptions) {

	// keep this id handy
	int workflowRunId = 0;
	int workflowRunAccessionInt = 0;
	int workflowAccession = 0;

	// the return value to use
	ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);
	boolean first = true;
	StringBuffer iniFilesStr = new StringBuffer();
	for (String iniFile : iniFiles) {
	    String newIniFile = replaceWBD(iniFile, wi.getWorkflowDir());
	    if (first) {
		first = false;
		iniFilesStr.append(newIniFile);
	    } else {
		iniFilesStr.append("," + newIniFile);
	    }
	}
	// this method takes the wi object, checks to see if the workflow is
	// available and, if not, sets it up from the archive location
	// it will also be responsible for correctly setting the
	// workflow_bundle_dir in the wi object if it has changed
	// and it will go through each field and update the
	// ${workflow_bundle_dir} variable to be a real path (which
	// makes some of the calls below to replaceWBD redundant but harmless
	if (provisionBundleAndUpdateWorkflowInfo(wi).getExitStatus() != ReturnValue.SUCCESS) {
	    ret.setExitStatus(ReturnValue.FAILURE);
	    Log.error("Problem getting workflow bundle");
	    return (ret);
	}

	Map<String, String> map = this.prepareData(wi, workflowRunAccession,
		iniFiles, preParsedIni, metadataWriteback, parentAccessions);
	// if we're doing metadata writeback will need to parameterize the
	// workflow correctly
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

	} else // no metadata connection, probably not needed!
	{
	    Log.info("Toggling to use no metadata connection");
	    metadata.clean_up();
	    metadata = new MetadataNoConnection();
	}

	// done with metadata writeback variables

	// pull back the template ftl file for this workflow and version
	String template = wi.getTemplatePath();
	template = replaceWBD(template, wi.getWorkflowDir());
	Log.stdout("TEMPLATE FILE: " + template);

	// pass in ini files and run the dax process
	Log.stdout("INI FILES: " + iniFilesStr.toString());

	// now create the DAX
	File dax;

	try {
	    dax = FileTools.createFileWithUniqueName(new File("/tmp"), "dax");
	} catch (IOException e) {
	    e.printStackTrace();
	    ret.setExitStatus(ReturnValue.FAILURE);
	    ret.setStderr("Can't write DAX file! " + e.getMessage());
	    return (ret);
	}

	Log.stdout("CREATING DAX IN: " + dax.getAbsolutePath());

	ReturnValue daxReturn = this.generateDaxFile(wi, dax,
		iniFilesStr.toString(), map, cmdLineOptions);

	if (daxReturn.getExitStatus() != ReturnValue.SUCCESS) {
	    ret.setExitStatus(daxReturn.getExitStatus());
	    return (ret);
	}

	// after running the daxGen.processTemplate above the map should be
	// filled in with all the ini key/values
	// save this and the DAX to the database
	StringBuffer mapBuffer = new StringBuffer();
	for (String key : map.keySet()) {
	    if (key != null && map.get(key.toString()) != null) {
		Log.stdout("  KEY: " + key + " VALUE: "
			+ map.get(key.toString()).toString());
	    }
	    mapBuffer.append(key + "=" + map.get(key) + "\n");
	}

	// read the DAX into a string buffer
	StringBuffer daxBuffer = new StringBuffer();
	try {
	    BufferedReader daxReader = new BufferedReader(new FileReader(dax));
	    String line = daxReader.readLine();
	    while (line != null) {
		daxBuffer.append(line);
		daxBuffer.append("\n");
		line = daxReader.readLine();
	    }
	    daxReader.close();
	} catch (Exception e) {
	    e.printStackTrace();
	    ret.setExitStatus(ReturnValue.FAILURE);
	    ret.setStderr("ERROR: Can't read DAX file! " + e.getMessage());
	    return (ret);
	}

	// create the submission of the DAX to Pegasus
	String pegasusCmd = "pegasus-plan -Dpegasus.user.properties="
		+ config.get("SW_PEGASUS_CONFIG_DIR") + "/properties --dax "
		+ dax.getAbsolutePath() + " --dir " + config.get("SW_DAX_DIR")
		+ " -o " + config.get("SW_CLUSTER") + " --force --submit -s "
		+ config.get("SW_CLUSTER");

	// run the pegasus submission
	Log.stdout("SUBMITTING TO PEGASUS: " + pegasusCmd);
	ArrayList<String> theCommand = new ArrayList<String>();
	theCommand.add("bash");
	theCommand.add("-lc");
	theCommand.add(pegasusCmd);
	ReturnValue retPegasus = RunTools.runCommand(theCommand
		.toArray(new String[0]));

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

	// need to pull back the workflow run object since some fields may
	// already be set
	// and we need to use their values before writing back to the DB!
	WorkflowRun wr = metadata.getWorkflowRun(workflowRunAccessionInt);

	// return if not successful
	if (retPegasus.getProcessExitStatus() != ReturnValue.SUCCESS
		|| statusCmd == null) {
	    // then something went wrong trying to call pegasus
	    if (metadataWriteback) {
		metadata.update_workflow_run(workflowRunId, wi.getCommand(),
			wi.getTemplatePath(), "failed", statusCmd,
			wi.getWorkflowDir(), daxBuffer.toString(),
			mapBuffer.toString(), wr.getHost(), 0, 0,
			retPegasus.getStderr(), retPegasus.getStdout());
	    }
	    return (retPegasus);
	}

	// now save to the DB
	if (metadataWriteback) {
	    metadata.update_workflow_run(workflowRunId, wi.getCommand(),
		    wi.getTemplatePath(), "pending", statusCmd,
		    wi.getWorkflowDir(), daxBuffer.toString(),
		    mapBuffer.toString(), wr.getHost(), 0, 0,
		    retPegasus.getStderr(), retPegasus.getStdout());
	}

	// if the user passes in --wait then hang around until the workflow
	// finishes or fails
	// periodically checking the status in a robust way
	boolean success = true;
	if (wait) {
	    success = false;

	    // now parse out the return status from the pegasus tool
	    ReturnValue watchedResult = null;
	    if (statusCmd != null && statusDir != null) {
		watchedResult = this.workflowTools.watchWorkflow(statusCmd,
			statusDir);
	    }

	    if (watchedResult.getExitStatus() == ReturnValue.SUCCESS) {
		success = true;
		if (metadataWriteback) {
		    metadata.update_workflow_run(workflowRunId,
			    wi.getCommand(), wi.getTemplatePath(), "completed",
			    statusCmd, wi.getWorkflowDir(), daxBuffer
				    .toString(), mapBuffer.toString(), wr
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
			    statusCmd, wi.getWorkflowDir(), daxBuffer
				    .toString(), mapBuffer.toString(), wr
				    .getHost(), Integer.parseInt(watchedResult
				    .getAttribute("currStep")), Integer
				    .parseInt(watchedResult
					    .getAttribute("totalSteps")),
			    watchedResult.getStderr(), watchedResult
				    .getStdout());
		}
		ret.setExitStatus(ReturnValue.FAILURE);
		return (ret);
	    }
	}

	if (retPegasus.getExitStatus() != ReturnValue.SUCCESS || !success) {
	    Log.error("ERROR: failure with running the pegasus command");
	    // I previously saved this state to the DB so no need to do that
	    // here
	    ret.setExitStatus(ReturnValue.FAILURE);
	    return (ret);
	}

	return (ret);
    }

    private ReturnValue scheduleWorkflow(WorkflowInfo wi,
	    String workflowRunAccession, ArrayList<String> iniFiles,
	    boolean metadataWriteback, ArrayList<String> parentAccessions,
	    ArrayList<String> parentsLinkedToWR, boolean wait,
	    List<String> cmdLineOptions) {

	// keep this id handy
	int workflowRunId = 0;

	// will be handed off to the template layer
	HashMap<String, String> map = new HashMap<String, String>();

	// replace the workflow bundle dir in the file paths of ini files
	boolean first = true;
	StringBuffer iniFilesStr = new StringBuffer();
	for (String iniFile : iniFiles) {
	    String newIniFiles = iniFile.replaceAll(
		    "\\$\\{workflow_bundle_dir\\}", wi.getWorkflowDir());
	    if (first) {
		first = false;
		iniFilesStr.append(newIniFiles);
	    } else {
		iniFilesStr.append("," + newIniFiles);
	    }
	}

	map.put("workflow_bundle_dir", wi.getWorkflowDir());
	int workflowAccession = 0;
	StringBuffer parentAccessionsStr = new StringBuffer();

	// starts with assumption of no metadata writeback
	map.put("metadata", "no-metadata");
	map.put("parent_accession", "0");
	map.put("parent_accessions", "0");
	// my new preferred variable name
	map.put("parent-accessions", "0");
	map.put("workflow_run_accession", "0");
	// my new preferred variable name
	map.put("workflow-run-accession", "0");

	// if we're doing metadata writeback will need to parameterize the
	// workflow correctly
	if (metadataWriteback) {

	    // tells the workflow it should save its metadata
	    map.put("metadata", "metadata");

	    first = true;

	    // make parent accession string
	    Log.info("ARRAY SIZE: " + parentAccessions.size());
	    for (String id : parentAccessions) {
		if (first) {
		    first = false;
		    parentAccessionsStr.append(id);
		} else {
		    parentAccessionsStr.append("," + id);
		}
	    }

	    // check to make sure it contains something, save under various
	    // names
	    if (parentAccessionsStr.length() > 0) {
		map.put("parent_accession", parentAccessionsStr.toString());
		map.put("parent_accessions", parentAccessionsStr.toString());
		// my new preferred variable name
		map.put("parent-accessions", parentAccessionsStr.toString());
	    }

	    // need to figure out workflow_run_accession
	    workflowAccession = wi.getWorkflowAccession();
	    // create the workflow_run row if it doesn't exist
	    if (workflowRunAccession == null) {
		workflowRunId = this.metadata
			.add_workflow_run(workflowAccession);
		int workflowRunAccessionInt = this.metadata
			.get_workflow_run_accession(workflowRunId);
		workflowRunAccession = new Integer(workflowRunAccessionInt)
			.toString();
	    } else { // if the workflow_run row exists get the workflow_run_id
		workflowRunId = this.metadata.get_workflow_run_id(Integer
			.parseInt(workflowRunAccession));
	    }
	    map.put("workflow_run_accession", workflowRunAccession);
	    // my new preferred variable name
	    map.put("workflow-run-accession", workflowRunAccession);
	    Log.stdout("WORKFLOW_RUN ACCESSION: " + workflowRunAccession);

	    // need to link all the parents to this workflow run accession
	    // this is actually linking them in the DB
	    for (String parentLinkedToWR : parentsLinkedToWR) {
		try {
		    this.metadata.linkWorkflowRunAndParent(workflowRunId,
			    Integer.parseInt(parentLinkedToWR));
		} catch (Exception e) {
		    Log.error(e.getMessage());
		}
	    }

	    /*
	     * At this point metadata objects have been created in the DB. The
	     * next step is to prepare an ini file and submit to the metadata
	     * layer to save in the workflow_run table. Just fill the map object
	     * with the contents and then write out to disk
	     */
	    String[] iniCompleteFilePaths = iniFilesStr.toString().split(",");
	    for (String currIniFile : iniCompleteFilePaths) {
		MapTools.ini2Map(currIniFile, map);
	    }
	    MapTools.cli2Map(cmdLineOptions.toArray(new String[0]), map);
	    // make a single string from the map
	    StringBuffer mapBuffer = new StringBuffer();
	    for (String key : map.keySet()) {
		Log.info("KEY: " + key + " VALUE: " + map.get(key));
		// Log.error(key+"="+map.get(key));
		mapBuffer.append(key + "=" + map.get(key) + "\n");
	    }

	    this.metadata.update_workflow_run(workflowRunId, wi.getCommand(),
		    wi.getTemplatePath(), "submitted", null,
		    wi.getWorkflowDir(), null, mapBuffer.toString(), null, 0,
		    0, null, null);

	} else {
	    Log.error("you can't schedule a workflow run unless you have metadata writeback turned on.");
	    ret.setExitStatus(ReturnValue.METADATAINVALIDIDCHAIN);
	}

	return (ret);

    }

    /**
     * Turns a simple hash into a WorkflowInfo object, making it easier to use
     * with a common workflow launcher
     * 
     * @param workflowMetadata
     * @return
     */
    private WorkflowInfo parseWorkflowMetadata(Map<String, String> m) {
	WorkflowInfo wi = new WorkflowInfo();
	wi.setCommand(m.get("cmd"));
	wi.setName(m.get("name"));
	wi.setDescription(m.get("description"));
	wi.setVersion(m.get("version"));
	wi.setConfigPath(m.get("base_ini_file"));
	wi.setWorkflowDir(m.get("current_working_dir"));
	wi.setTemplatePath(m.get("workflow_template"));
	wi.setWorkflowAccession(Integer.parseInt(m.get("workflow_accession")));
	wi.setPermBundleLocation(m.get("permanent_bundle_location"));
	return (wi);
    }

    /**
     * a simple method to replace the ${workflow_bundle_dir} variable
     * 
     * @param input
     * @param wbd
     * @return
     */
    private String replaceWBD(String input, String wbd) {
	return (input.replaceAll("\\$\\{workflow_bundle_dir\\}", wbd));
    }

    /**
     * This is a pretty key method. It takes the wi object, checks to see if the
     * workflow is available and, if not, sets it up from the archive location
     * it will also be responsible for correctly setting the workflow_bundle_dir
     * in the wi object if it has changed and it will go through each field and
     * update the ${workflow_bundle_dir} variable to be a real path (which makes
     * some of the calls elsewhere to replaceWBD redundant but harmless).
     * 
     * @param wi
     */
    private ReturnValue provisionBundleAndUpdateWorkflowInfo(WorkflowInfo wi) {

	ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);

	// first, see if the workflow bundle dir is actually there, I'm assuming
	// if it is then I don't need to provision it
	// FIXME: in the future we should do a more robust check
	File workflowBundleDir = null;
	if (wi.getWorkflowDir() != null) {
	    workflowBundleDir = new File(wi.getWorkflowDir());
	}

	// if any of these are true then need to reprovision workflow
	if (workflowBundleDir == null || !workflowBundleDir.exists()
		|| !workflowBundleDir.isDirectory()
		|| !workflowBundleDir.canRead()) {

	    // find the perm loc
	    String permLoc = wi.getPermBundleLocation();
	    String newWorkflowBundleDir = getAndProvisionBundle(permLoc);

	    // if its null then something went wrong
	    if (newWorkflowBundleDir == null) {
		ret.setExitStatus(ReturnValue.FAILURE);
		Log.error("Unable to provision the bundle from: " + permLoc);
		return (ret);
	    }

	    // now update the variables to replace ${workflow_bundle_dir}
	    wi.setWorkflowDir(newWorkflowBundleDir);
	    wi.setConfigPath(replaceWBD(wi.getConfigPath(),
		    newWorkflowBundleDir));
	    wi.setTemplatePath(replaceWBD(wi.getTemplatePath(),
		    newWorkflowBundleDir));

	}

	return (ret);
    }

    /**
     * will either copy or download from S3, unzip, and return unzip location
     * 
     * @param permLoc
     * @return
     */
    private String getAndProvisionBundle(String permLoc) {
	String result = null;
	Bundle bundle = new Bundle(this.metadata, this.config);
	ReturnValue ret = null;
	if (permLoc.startsWith("s3://")) {
	    ret = bundle.unpackageBundleFromS3(permLoc);
	} else {
	    ret = bundle.unpackageBundle(new File(permLoc));
	}
	if (ret != null) {
	    return (ret.getAttribute("outputDir"));
	}
	return (result);
    }

    /**
     * reads a map and tries to find the parent accessions, the result is
     * de-duplicated.
     * 
     * @param map
     * @return
     */
    private ArrayList<String> parseParentAccessions(Map<String, String> map) {
	ArrayList<String> results = new ArrayList<String>();
	HashMap<String, String> resultsDeDup = new HashMap<String, String>();

	for (String key : map.keySet()) {
	    if ("parent_accession".equals(key)
		    || "parent_accessions".equals(key)
		    || "parent-accessions".equals(key)) {
		resultsDeDup.put(map.get(key), "null");
	    }
	}

	for (String accession : resultsDeDup.keySet()) {
	    results.add(accession);
	}

	return (results);
    }

    protected Map<String, String> prepareData(WorkflowInfo wi,
	    String workflowRunAccession, ArrayList<String> iniFiles,
	    Map<String, String> preParsedIni, boolean metadataWriteback,
	    ArrayList<String> parentAccessions) {
	Map<String, String> map = new HashMap<String, String>();
	StringBuilder parentAccessionsStr = new StringBuilder();
	// merge what came into this program to the map object
	if (preParsedIni != null && !preParsedIni.isEmpty()) {
	    map.putAll(preParsedIni);
	}
	// update this in the map
	map.put("workflow_bundle_dir", wi.getWorkflowDir());
	// starts with assumption of no metadata writeback
	// GOTCHA: this is why you always need to specify the parentAccessions
	// array
	map.put("metadata", "no-metadata");
	map.put("parent_accession", "0");
	map.put("parent_accessions", "0");
	// my new preferred variable name
	map.put("parent-accessions", "0");
	map.put("workflow_run_accession", "0");
	// my new preferred variable name
	map.put("workflow-run-accession", "0");
	// if we're doing metadata writeback will need to parameterize the
	// workflow correctly
	// corrects the file paths for all the iniFiles
	boolean first = true;

	if (metadataWriteback) {

	    // tells the workflow it should save its metadata
	    map.put("metadata", "metadata");

	    // figure out the unique list of parent accessions that were passed
	    // in
	    first = true;
	    Log.info("ARRAY SIZE: " + parentAccessions.size());
	    HashMap<String, String> uniqParentAccessions = new HashMap<String, String>();
	    for (String id : parentAccessions) {
		uniqParentAccessions.put(id, "null");
	    }
	    for (String id : uniqParentAccessions.keySet()) {
		if (first) {
		    first = false;
		    parentAccessionsStr.append(id);
		} else {
		    parentAccessionsStr.append("," + id);
		}
	    }

	    // if this contains something override the value of "0"
	    if (parentAccessionsStr.length() > 0) {
		Log.stdout("PARENT ACCESSIONS: " + parentAccessionsStr);
		map.put("parent_accession", parentAccessionsStr.toString());
		map.put("parent_accessions", parentAccessionsStr.toString());
		// my new preferred variable name
		map.put("parent-accessions", parentAccessionsStr.toString());
	    }

	    // need to figure out workflow_run_accession
	    map.put("workflow_run_accession", workflowRunAccession);
	    // my new preferred variable name
	    map.put("workflow-run-accession", workflowRunAccession);

	}
	// done with metadata writeback variables

	// have to pass in the cluster name
	map.put("seqware_cluster", config.get("SW_CLUSTER"));

	return map;
    }

    protected ReturnValue generateDaxFile(WorkflowInfo wi, File dax,
	    String iniFilesStr, Map<String, String> map,
	    List<String> cmdLineOptions) {
	Daxgenerator daxGen = new Daxgenerator();
	String template = wi.getTemplatePath();
	template = replaceWBD(template, wi.getWorkflowDir());
	Log.stdout("TEMPLATE FILE: " + template);

	Log.stdout("CREATING DAX IN: " + dax.getAbsolutePath());
	ReturnValue daxReturn = daxGen.processTemplate(iniFilesStr.toString()
		.split(","), template, dax.getAbsolutePath(), map,
		cmdLineOptions.toArray(new String[0]));

	return daxReturn;
    }
}
