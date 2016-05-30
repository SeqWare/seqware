package io.seqware.pipeline.api;

import io.seqware.common.model.WorkflowRunStatus;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.Rethrow;
import net.sourceforge.seqware.common.util.maptools.MapTools;
import net.sourceforge.seqware.common.util.maptools.ReservedIniKeys;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;

/**
 * This class performs the actual work of scheduling a workflow.
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class Scheduler {

    /**
     *
     */
    protected Metadata metadata = null;
    protected Map<String, String> config = null;

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
    public Scheduler(Metadata metadata, Map<String, String> config) {
        super();
        this.metadata = metadata;
        this.config = config;
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
     * @param iniFiles
     * @param metadataWriteback
     * @param parentAccessions
     * @param parentsLinkedToWR
     * @param inputFiles
     *            the value of inputFiles
     * @param workflowEngine
     * @param scheduledHost
     * @param cmdLineOptions
     * @return
     */
    public ReturnValue scheduleInstalledBundle(String workflowAccession, List<String> iniFiles, boolean metadataWriteback,
            List<String> parentAccessions, List<String> parentsLinkedToWR, List<String> cmdLineOptions, String scheduledHost,
            String workflowEngine, Set<Integer> inputFiles) {

        return scheduleInstalledBundle(workflowAccession, iniFiles, metadataWriteback, parentAccessions, parentsLinkedToWR, cmdLineOptions,
                scheduledHost, workflowEngine, inputFiles, false);

    }

    // Yes, adding workflowEngine as a param makes no sense given that this class *is* a
    // WorkflowEngine, but since this method is being called directly from WorkflowPlugin.doOldRun(), and
    // *isn't* in the WorkflowEngine interface, I'm disinclined to begin fixing
    // things to conform to what I can only guess is the design of these
    // interfaces/classes.

    /**
     * {@inheritDoc}
     * 
     * This method just needs a sw_accession value from the workflow table and an ini file(s) in order to schedule a workflow.All needed
     * info is pulled from the workflow table which was populated when the workflow was installed. Keep in mind this does not actually
     * trigger anything, it just schedules the workflow to run by adding to the workflow_run table. This lets you run workflows on a
     * different host from where this command line tool is run but requires an external process to launch workflows that have been
     * scheduled.
     * 
     * @param workflowAccession
     * @param iniFiles
     * @param metadataWriteback
     * @param parentAccessions
     * @param parentsLinkedToWR
     * @param cmdLineOptions
     * @param scheduledHost
     * @param workflowEngine
     * @param inputFiles
     *            the value of inputFiles
     * @param allowMissingVars
     *            for backwards compatibility with 1.0
     * @return the net.sourceforge.seqware.common.module.ReturnValue
     */
    public ReturnValue scheduleInstalledBundle(String workflowAccession, List<String> iniFiles, boolean metadataWriteback,
            List<String> parentAccessions, List<String> parentsLinkedToWR, List<String> cmdLineOptions, String scheduledHost,
            String workflowEngine, Set<Integer> inputFiles, boolean allowMissingVars) {
        Map<String, String> workflowMetadata = this.metadata.get_workflow_info(Integer.parseInt(workflowAccession));
        WorkflowInfo wi = parseWorkflowMetadata(workflowMetadata);
        return scheduleWorkflow(wi, iniFiles, metadataWriteback, parentAccessions, parentsLinkedToWR, cmdLineOptions, scheduledHost,
                workflowEngine, inputFiles, allowMissingVars);
    }

    /**
     * 
     * @param wi
     * @param workflowRunAccession
     * @param iniFiles
     * @param metadataWriteback
     * @param parentAccessions
     * @param parentsLinkedToWR
     * @param cmdLineOptions
     * @param scheduledHost
     * @param workflowEngine
     * @param inputFiles
     * @return
     */
    private ReturnValue scheduleWorkflow(WorkflowInfo wi, List<String> iniFiles, boolean metadataWriteback, List<String> parentAccessions,
            List<String> parentsLinkedToWR, List<String> cmdLineOptions, String scheduledHost, String workflowEngine,
            Set<Integer> inputFiles, boolean allowMissingVars) {

        // keep this id handy
        int workflowRunId = 0;
        // will be handed off to the template layer
        Map<String, String> map = new HashMap<>();
        // populate our reserved ini keys
        map.put(ReservedIniKeys.WORKFLOW_BUNDLE_DIR.getKey(), wi.getWorkflowDir());
        // int workflowAccession = 0;
        // starts with assumption of no metadata writeback
        map.put(ReservedIniKeys.METADATA.getKey(), "no-metadata");
        map.put(ReservedIniKeys.PARENT_ACCESSION.getKey(), "0");
        map.put(ReservedIniKeys.PARENT_UNDERSCORE_ACCESSIONS.getKey(), "0");
        // my new preferred variable name
        map.put(ReservedIniKeys.PARENT_DASH_ACCESSIONS.getKey(), "0");
        map.put(ReservedIniKeys.WORKFLOW_RUN_ACCESSION_UNDERSCORES.getKey(), "0");
        // my new preferred variable name
        map.put(ReservedIniKeys.WORKFLOW_RUN_ACCESSION_DASHED.getKey(), "0");
        // load up default ini values from installed workflow
        Map<String, String> defaultIniConfig = this.loadIniConfigs(wi.getWorkflowAccession());
        map.putAll(defaultIniConfig);
        // if we're doing metadata writeback will need to parameterize the
        // workflow correctly
        if (metadataWriteback) {
            // tells the workflow it should save its metadata
            map.put(ReservedIniKeys.METADATA.getKey(), "metadata");
        }
        /* Load ini (thus ensuring it exists) prior to writing to the DB. */
        for (String currIniFile : iniFiles) {
            MapTools.ini2Map(currIniFile, map);
        }
        MapTools.cli2Map(cmdLineOptions.toArray(new String[cmdLineOptions.size()]), map);
        substituteParentAccessions(parentAccessions, map);
        // perform variable substituion on any bundle path variables
        Log.info("Attempting to substitute workflow_bundle_dir " + wi.getWorkflowDir());
        map = MapTools.expandVariables(map, MapTools.providedMap(wi.getWorkflowDir(), wi.getWorkflowSqwVersion()), allowMissingVars);
        // create the final ini for upload to the web service
        StringBuilder mapBuffer = new StringBuilder();
        for (Entry<String, String> entry : map.entrySet()) {
            Log.info("KEY: " + entry.getKey() + " VALUE: " + entry.getValue());
            // Log.error(key+"="+map.get(key));
            mapBuffer.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        // need to figure out workflow_run_accession
        int workflowAccession = wi.getWorkflowAccession();
        // create the workflow_run row if it doesn't exist
        workflowRunId = this.metadata.add_workflow_run(workflowAccession);
        int workflowRunAccessionInt = this.metadata.get_workflow_run_accession(workflowRunId);
        String workflowRunAccession = Integer.toString(workflowRunAccessionInt);
        map.put(ReservedIniKeys.WORKFLOW_RUN_ACCESSION_UNDERSCORES.getKey(), workflowRunAccession);
        // my new preferred variable name
        map.put(ReservedIniKeys.WORKFLOW_RUN_ACCESSION_DASHED.getKey(), workflowRunAccession);
        Log.stdout("Created workflow run with SWID: " + workflowRunAccession);
        // need to link all the parents to this workflow run accession
        // this is actually linking them in the DB
        if (!parentsLinkedToWR.isEmpty()) {
            int[] parentsAsArray = new int[parentsLinkedToWR.size()];
            for (int i = 0; i < parentsLinkedToWR.size(); i++) {
                parentsAsArray[i] = Integer.parseInt(parentsLinkedToWR.get(i));
            }

            try {
                this.metadata.linkWorkflowRunAndParent(workflowRunId, parentsAsArray);
            } catch (Exception e) {
                Log.error("Could not link workflow run to its parents " + parentsLinkedToWR.toString());
                throw Rethrow.rethrow(e);
            }
        }

        this.metadata.update_workflow_run(workflowRunId, wi.getCommand(), wi.getTemplatePath(), WorkflowRunStatus.submitted, null, null,
                null, mapBuffer.toString(), scheduledHost, null, null, workflowEngine, inputFiles);
        ReturnValue ret = new ReturnValue();
        ret.setReturnValue(Integer.parseInt(workflowRunAccession));
        return ret;
    }

    /**
     * Merge the parent accession parameters provided and insert them into the ini file
     * 
     * @param parentAccessions
     * @param map
     */
    private void substituteParentAccessions(List<String> parentAccessions, Map<String, String> map) {
        StringBuilder parentAccessionsStr = new StringBuilder();
        boolean first = true;

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
    }

    /**
     * @param workflowAccession
     * @param bundlePath
     * @return
     */
    private Map<String, String> loadIniConfigs(Integer workflowAccession) {
        assert (workflowAccession != null);
        // the map
        HashMap<String, String> map = new HashMap<>();
        Log.info("loading ini files from DB");
        // iterate over all the generic default params
        // these params are created when a workflow is installed
        SortedSet<WorkflowParam> workflowParams = this.metadata.getWorkflowParams(workflowAccession.toString());
        for (WorkflowParam param : workflowParams) {
            // SEQWARE-1909 - for installed workflows, interpret a null default as blank
            map.put(param.getKey(), param.getDefaultValue() == null ? "" : param.getDefaultValue());
        }

        // FIXME: this needs to be implemented otherwise portal submitted won't
        // work!
        // now iterate over the params specific for this workflow run
        // this is where the SeqWare Portal will populate parameters for
        // a scheduled workflow
        /*
         * workflowParams = this.metadata.getWorkflowRunParams(workflowRunAccession); for(WorkflowParam param : workflowParams) {
         * map.put(param.getKey(), param.getValue()); }
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

        // allow the command line options to override options in the map
        // Parse command line options for additional configuration. Note that we
        // do it last so it takes precedence over the INI
        // if we always schedule, we never override here
        // MapTools.cli2Map(params, map);

        return map;
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
        wi.setWorkflowSqwVersion(m.get("seqware_version"));
        return wi;
    }

}
