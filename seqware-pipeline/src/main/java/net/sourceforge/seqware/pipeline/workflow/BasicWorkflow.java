package net.sourceforge.seqware.pipeline.workflow;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.model.WorkflowRunStatus;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.maptools.MapTools;
import net.sourceforge.seqware.common.util.maptools.ReservedIniKeys;
import net.sourceforge.seqware.common.util.runtools.RunTools;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo;
import net.sourceforge.seqware.pipeline.bundle.Bundle;
import net.sourceforge.seqware.pipeline.daxgenerator.Daxgenerator;
import net.sourceforge.seqware.pipeline.workflowV2.WorkflowEngine;

/**
 * <p>
 * Abstract BasicWorkflow class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public abstract class BasicWorkflow implements WorkflowEngine {

    protected ReturnValue ret = new ReturnValue();
    protected Metadata metadata = null;
    protected Map<String, String> config = null;
    protected String outputDir = null;
    protected ArrayList<File> filesArray = new ArrayList<>();
    protected Bundle bundleUtil = null;
    protected int totalSteps = 0;
    protected int currStep = 0;
    protected int percentage = 0;

    public static ReturnValue gridProxyInit() {
        // initialize globus authentication proxy
        ArrayList<String> theCommand = new ArrayList<>();
        theCommand.add("bash");
        theCommand.add("-lc");
        theCommand.add("grid-proxy-init -valid 480:00");
        ReturnValue retProxy = RunTools.runCommand(theCommand.toArray(new String[0]));
        return retProxy;
    }

    protected enum Job {

        setup, prejob, mainjob, postjob, cleanup, statcall, data;
    }

    /**
     * <p>
     * Constructor for BasicWorkflow.
     * </p>
     * 
     * @param metadata
     *            a {@link net.sourceforge.seqware.common.metadata.Metadata} object.
     * @param config
     *            a {@link java.util.Map} object.
     */
    public BasicWorkflow(Metadata metadata, Map<String, String> config) {
        super();
        this.metadata = metadata;
        this.config = config;
        this.bundleUtil = new Bundle(metadata, config);
    }

    private ReturnValue setup() {

        ReturnValue retVal = new ReturnValue(ReturnValue.SUCCESS);

        ReturnValue retProxy = gridProxyInit();
        if (retProxy.getExitStatus() != ReturnValue.SUCCESS) {
            Log.error("ERROR: can't init the globus proxy so terminating here, continuing but your workflow submissions will fail!");
            return (retProxy);
        }

        return (retVal);
    }

    /**
     * {@inheritDoc}
     * 
     * This method just needs a sw_accession value from the workflow table and an ini file(s) in order to schedule a workflow. All needed
     * info is pulled from the workflow table which was populated when the workflow was installed. Keep in mind this does not actually
     * trigger anything, it just schedules the workflow to run by adding to the workflow_run table. This lets you run workflows on a
     * different host from where this command line tool is run but requires an external process to launch workflows that have been
     * scheduled.
     * 
     */
    @Override
    public ReturnValue scheduleInstalledBundle(String workflowAccession, String workflowRunAccession, ArrayList<String> iniFiles,
            boolean metadataWriteback, ArrayList<String> parentAccessions, ArrayList<String> parentsLinkedToWR, boolean wait,
            List<String> cmdLineOptions) {

        return scheduleInstalledBundle(workflowAccession, workflowRunAccession, iniFiles, metadataWriteback, parentAccessions,
                parentsLinkedToWR, wait, cmdLineOptions, null, null, null);
    }

    // Yes, adding workflowEngine as a param makes no sense given that this class *is* a
    // WorkflowEngine, but since this method is being called directly from WorkflowPlugin.doOldRun(), and
    // *isn't* in the WorkflowEngine interface, I'm disinclined to begin fixing
    // things to conform to what I can only guess is the design of these
    // interfaces/classes.
    /**
     * {@inheritDoc}
     * 
     * This method just needs a sw_accession value from the workflow table and an ini file(s) in order to schedule a workflow. All needed
     * info is pulled from the workflow table which was populated when the workflow was installed. Keep in mind this does not actually
     * trigger anything, it just schedules the workflow to run by adding to the workflow_run table. This lets you run workflows on a
     * different host from where this command line tool is run but requires an external process to launch workflows that have been
     * scheduled.
     * 
     * @param workflowAccession
     * @param workflowRunAccession
     * @param iniFiles
     * @param metadataWriteback
     * @param parentAccessions
     * @param parentsLinkedToWR
     * @param inputFiles
     *            the value of inputFiles
     * @param workflowEngine
     * @param scheduledHost
     * @param cmdLineOptions
     * @param wait
     * @return
     */
    public ReturnValue scheduleInstalledBundle(String workflowAccession, String workflowRunAccession, ArrayList<String> iniFiles,
            boolean metadataWriteback, ArrayList<String> parentAccessions, ArrayList<String> parentsLinkedToWR, boolean wait,
            List<String> cmdLineOptions, String scheduledHost, String workflowEngine, Set<Integer> inputFiles) {
        ReturnValue localRet = new ReturnValue(ReturnValue.SUCCESS);

        Map<String, String> workflowMetadata = this.metadata.get_workflow_info(Integer.parseInt(workflowAccession));
        WorkflowInfo wi = parseWorkflowMetadata(workflowMetadata);
        scheduleWorkflow(wi, workflowRunAccession, iniFiles, metadataWriteback, parentAccessions, parentsLinkedToWR, wait, cmdLineOptions,
                scheduledHost, workflowEngine, inputFiles);

        return localRet;
    }

    /**
     * 
     * @param wi
     * @param workflowRunAccession
     * @param iniFiles
     * @param metadataWriteback
     * @param parentAccessions
     * @param parentsLinkedToWR
     * @param wait
     * @param cmdLineOptions
     * @param scheduledHost
     * @param workflowEngine
     * @param inputFiles
     * @return
     */
    private ReturnValue scheduleWorkflow(WorkflowInfo wi, String workflowRunAccession, ArrayList<String> iniFiles,
            boolean metadataWriteback, ArrayList<String> parentAccessions, ArrayList<String> parentsLinkedToWR, boolean wait,
            List<String> cmdLineOptions, String scheduledHost, String workflowEngine, Set<Integer> inputFiles) {

        // keep this id handy
        int workflowRunId = 0;

        // will be handed off to the template layer
        HashMap<String, String> map = new HashMap<>();

        // replace the workflow bundle dir in the file paths of ini files
        boolean first = true;
        StringBuilder iniFilesStr = new StringBuilder();
        for (String iniFile : iniFiles) {
            String newIniFiles = iniFile.replaceAll("\\$\\{workflow_bundle_dir\\}", wi.getWorkflowDir());
            if (first) {
                first = false;
                iniFilesStr.append(newIniFiles);
            } else {
                iniFilesStr.append(",").append(newIniFiles);
            }
        }

        map.put(ReservedIniKeys.WORKFLOW_BUNDLE_DIR.getKey(), wi.getWorkflowDir());
        // int workflowAccession = 0;
        StringBuilder parentAccessionsStr = new StringBuilder();

        // starts with assumption of no metadata writeback
        map.put(ReservedIniKeys.METADATA.getKey(), "no-metadata");
        map.put(ReservedIniKeys.PARENT_ACCESSION.getKey(), "0");
        map.put(ReservedIniKeys.PARENT_UNDERSCORE_ACCESSIONS.getKey(), "0");
        // my new preferred variable name
        map.put(ReservedIniKeys.PARENT_DASH_ACCESSIONS.getKey(), "0");
        map.put(ReservedIniKeys.WORKFLOW_RUN_ACCESSION_UNDERSCORES.getKey(), "0");
        // my new preferred variable name
        map.put(ReservedIniKeys.WORKFLOW_RUN_ACCESSION_DASHED.getKey(), "0");

        // if we're doing metadata writeback will need to parameterize the
        // workflow correctly
        if (metadataWriteback) {

            // tells the workflow it should save its metadata
            map.put(ReservedIniKeys.METADATA.getKey(), "metadata");

            first = true;

            // make parent accession string
            Log.info("ARRAY SIZE: " + parentAccessions.size());
            for (String id : parentAccessions) {
                if (first) {
                    first = false;
                    parentAccessionsStr.append(id);
                } else {
                    parentAccessionsStr.append(",").append(id);
                }
            }

            // check to make sure it contains something, save under various
            // names
            if (parentAccessionsStr.length() > 0) {
                map.put(ReservedIniKeys.PARENT_ACCESSION.getKey(), parentAccessionsStr.toString());
                map.put(ReservedIniKeys.PARENT_UNDERSCORE_ACCESSIONS.getKey(), parentAccessionsStr.toString());
                // my new preferred variable name
                map.put(ReservedIniKeys.PARENT_DASH_ACCESSIONS.getKey(), parentAccessionsStr.toString());
            }

            /* Load ini (thus ensuring it exists) prior to writing to the DB. */
            String[] iniCompleteFilePaths = iniFilesStr.toString().isEmpty() ? new String[] {} : iniFilesStr.toString().split(",");
            for (String currIniFile : iniCompleteFilePaths) {
                MapTools.ini2Map(currIniFile, map);
            }
            MapTools.cli2Map(cmdLineOptions.toArray(new String[0]), map);
            StringBuilder mapBuffer = new StringBuilder();
            for (String key : map.keySet()) {
                Log.info("KEY: " + key + " VALUE: " + map.get(key));
                // Log.error(key+"="+map.get(key));
                mapBuffer.append(key).append("=").append(map.get(key)).append("\n");
            }

            // need to figure out workflow_run_accession
            int workflowAccession = wi.getWorkflowAccession();
            // create the workflow_run row if it doesn't exist
            if (workflowRunAccession == null) {
                workflowRunId = this.metadata.add_workflow_run(workflowAccession);
                int workflowRunAccessionInt = this.metadata.get_workflow_run_accession(workflowRunId);
                workflowRunAccession = new Integer(workflowRunAccessionInt).toString();
            } else { // if the workflow_run row exists get the workflow_run_id
                workflowRunId = this.metadata.get_workflow_run_id(Integer.parseInt(workflowRunAccession));
            }
            map.put(ReservedIniKeys.WORKFLOW_RUN_ACCESSION_UNDERSCORES.getKey(), workflowRunAccession);
            // my new preferred variable name
            map.put(ReservedIniKeys.WORKFLOW_RUN_ACCESSION_DASHED.getKey(), workflowRunAccession);
            Log.stdout("Created workflow run with SWID: " + workflowRunAccession);

            // need to link all the parents to this workflow run accession
            // this is actually linking them in the DB
            for (String parentLinkedToWR : parentsLinkedToWR) {
                try {
                    this.metadata.linkWorkflowRunAndParent(workflowRunId, Integer.parseInt(parentLinkedToWR));
                } catch (Exception e) {
                    Log.error(e.getMessage());
                }
            }

            this.metadata.update_workflow_run(workflowRunId, wi.getCommand(), wi.getTemplatePath(), WorkflowRunStatus.submitted, null,
                    null, null, mapBuffer.toString(), scheduledHost, null, null, workflowEngine, inputFiles);

        } else {
            Log.error("you can't schedule a workflow run unless you have metadata writeback turned on.");
            ret.setExitStatus(ReturnValue.METADATAINVALIDIDCHAIN);
        }

        return ret;

    }

    /**
     * Turns a simple hash into a WorkflowInfo object, making it easier to use with a common workflow launcher
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
        wi.setWorkflowClass(m.get("workflow_class"));
        wi.setWorkflowEngine(m.get("workflow_engine"));
        wi.setWorkflowType(m.get("workflow_type"));
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
     * This is a pretty key method. It takes the wi object, checks to see if the workflow is available and, if not, sets it up from the
     * archive location it will also be responsible for correctly setting the workflow_bundle_dir in the wi object if it has changed and it
     * will go through each field and update the ${workflow_bundle_dir} variable to be a real path (which makes some of the calls elsewhere
     * to replaceWBD redundant but harmless).
     * 
     * @param wi
     */
    private ReturnValue provisionBundleAndUpdateWorkflowInfo(WorkflowInfo wi) {

        ReturnValue localRet = new ReturnValue(ReturnValue.SUCCESS);

        // first, see if the workflow bundle dir is actually there, I'm assuming
        // if it is then I don't need to provision it
        // FIXME: in the future we should do a more robust check
        File workflowBundleDir = null;
        if (wi.getWorkflowDir() != null) {
            workflowBundleDir = new File(wi.getWorkflowDir());
        }

        // if any of these are true then need to reprovision workflow
        if (workflowBundleDir == null || !workflowBundleDir.exists() || !workflowBundleDir.isDirectory() || !workflowBundleDir.canRead()) {

            // find the perm loc
            String permLoc = wi.getPermBundleLocation();
            String newWorkflowBundleDir = getAndProvisionBundle(permLoc);

            // if its null then something went wrong
            if (newWorkflowBundleDir == null) {
                localRet.setExitStatus(ReturnValue.FAILURE);
                Log.error("Unable to provision the bundle from: " + permLoc);
                return localRet;
            }

            // now update the variables to replace ${workflow_bundle_dir}
            wi.setWorkflowDir(newWorkflowBundleDir);
            wi.setConfigPath(replaceWBD(wi.getConfigPath(), newWorkflowBundleDir));
            wi.setTemplatePath(replaceWBD(wi.getTemplatePath(), newWorkflowBundleDir));
        }

        return localRet;
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
        ReturnValue localRet;
        if (permLoc.startsWith("s3://")) {
            localRet = bundle.unpackageBundleFromS3(permLoc);
        } else {
            localRet = bundle.unpackageBundle(new File(permLoc));
        }
        if (localRet != null) {
            return localRet.getAttribute("outputDir");
        }
        return result;
    }

    /**
     * reads a map and tries to find the parent accessions, the result is de-duplicated.
     * 
     * @param map
     * @return
     */
    public static ArrayList<String> parseParentAccessions(Map<String, String> map) {
        ArrayList<String> results = new ArrayList<>();
        HashMap<String, String> resultsDeDup = new HashMap<>();

        for (String key : map.keySet()) {
            if (ReservedIniKeys.PARENT_ACCESSION.getKey().equals(key) || ReservedIniKeys.PARENT_UNDERSCORE_ACCESSIONS.getKey().equals(key)
                    || ReservedIniKeys.PARENT_DASH_ACCESSIONS.getKey().equals(key)) {
                resultsDeDup.put(map.get(key), "null");
            }
        }

        for (String accession : resultsDeDup.keySet()) {
            results.add(accession);
        }

        // for hotfix 0.13.6.3
        // GATK reveals an issue where parent_accession is setup with a correct list
        // of accessions while parent-accessions and parent_accessions are set to 0
        // when the three are mushed together, the rogue zero is transferred to
        // parent_accession and causes it to crash the workflow
        // I'm going to allow a single 0 in case (god forbid) some workflow relies
        // upon this, but otherwise a 0 should not occur in a list of valid
        // parent_accessions
        if (results.contains("0") && results.size() > 1) {
            results.remove("0");
        }

        return (results);
    }

    /**
     * <p>
     * prepareData.
     * </p>
     * 
     * @param wi
     *            a {@link net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo} object.
     * @param workflowRunAccession
     *            a {@link java.lang.String} object.
     * @param iniFiles
     *            a {@link java.util.ArrayList} object.
     * @param preParsedIni
     *            a {@link java.util.Map} object.
     * @param metadataWriteback
     *            a boolean.
     * @param parentAccessions
     *            a {@link java.util.ArrayList} object.
     * @return a {@link java.util.Map} object.
     */
    protected Map<String, String> prepareData(WorkflowInfo wi, String workflowRunAccession, ArrayList<String> iniFiles,
            Map<String, String> preParsedIni, boolean metadataWriteback, ArrayList<String> parentAccessions) {
        Map<String, String> map = new HashMap<>();
        StringBuilder parentAccessionsStr = new StringBuilder();
        // merge what came into this program to the map object
        if (preParsedIni != null && !preParsedIni.isEmpty()) {
            map.putAll(preParsedIni);
        }
        // update this in the map
        map.put(ReservedIniKeys.WORKFLOW_BUNDLE_DIR.getKey(), wi.getWorkflowDir());
        // starts with assumption of no metadata writeback
        // GOTCHA: this is why you always need to specify the parentAccessions
        // array
        map.put(ReservedIniKeys.METADATA.getKey(), "no-metadata");
        map.put(ReservedIniKeys.PARENT_ACCESSION.getKey(), "0");
        map.put(ReservedIniKeys.PARENT_UNDERSCORE_ACCESSIONS.getKey(), "0");
        // my new preferred variable name
        map.put(ReservedIniKeys.PARENT_DASH_ACCESSIONS.getKey(), "0");
        map.put(ReservedIniKeys.WORKFLOW_RUN_ACCESSION_UNDERSCORES.getKey(), "0");
        // my new preferred variable name
        map.put(ReservedIniKeys.WORKFLOW_RUN_ACCESSION_DASHED.getKey(), "0");
        // if we're doing metadata writeback will need to parameterize the
        // workflow correctly
        // corrects the file paths for all the iniFiles
        // boolean first = true;

        if (metadataWriteback) {

            // tells the workflow it should save its metadata
            map.put(ReservedIniKeys.METADATA.getKey(), "metadata");

            // figure out the unique list of parent accessions that were passed
            // in
            boolean first = true;
            Log.info("ARRAY SIZE: " + parentAccessions.size());
            HashMap<String, String> uniqParentAccessions = new HashMap<>();
            for (String id : parentAccessions) {
                uniqParentAccessions.put(id, "null");
            }
            for (String id : uniqParentAccessions.keySet()) {
                if (first) {
                    first = false;
                    parentAccessionsStr.append(id);
                } else {
                    parentAccessionsStr.append(",").append(id);
                }
            }

            // if this contains something override the value of "0"
            if (parentAccessionsStr.length() > 0) {
                Log.stdout("PARENT ACCESSIONS: " + parentAccessionsStr);
                map.put(ReservedIniKeys.PARENT_ACCESSION.getKey(), parentAccessionsStr.toString());
                map.put(ReservedIniKeys.PARENT_UNDERSCORE_ACCESSIONS.getKey(), parentAccessionsStr.toString());
                // my new preferred variable name
                map.put(ReservedIniKeys.PARENT_DASH_ACCESSIONS.getKey(), parentAccessionsStr.toString());
            }

            // need to figure out workflow_run_accession
            map.put(ReservedIniKeys.WORKFLOW_RUN_ACCESSION_UNDERSCORES.getKey(), workflowRunAccession);
            // my new preferred variable name
            map.put(ReservedIniKeys.WORKFLOW_RUN_ACCESSION_DASHED.getKey(), workflowRunAccession);

        }
        // done with metadata writeback variables

        // have to pass in the cluster name
        map.put("seqware_cluster", config.get("SW_CLUSTER"));

        return map;
    }

    /**
     * <p>
     * generateDaxFile.
     * </p>
     * 
     * @param wi
     *            a {@link net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo} object.
     * @param dax
     *            a {@link java.io.File} object.
     * @param iniFilesStr
     *            a {@link java.lang.String} object.
     * @param map
     *            a {@link java.util.Map} object.
     * @param cmdLineOptions
     *            a {@link java.util.List} object.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    protected ReturnValue generateDaxFile(WorkflowInfo wi, File dax, String iniFilesStr, Map<String, String> map,
            List<String> cmdLineOptions) {
        Daxgenerator daxGen = new Daxgenerator();
        String template = wi.getTemplatePath();
        template = replaceWBD(template, wi.getWorkflowDir());
        Log.stdout("TEMPLATE FILE: " + template);

        Log.stdout("CREATING DAX IN: " + dax.getAbsolutePath());
        ReturnValue daxReturn = daxGen.processTemplate(iniFilesStr.toString().split(","), template, dax.getAbsolutePath(), map,
                cmdLineOptions.toArray(new String[0]));

        return daxReturn;
    }
}
