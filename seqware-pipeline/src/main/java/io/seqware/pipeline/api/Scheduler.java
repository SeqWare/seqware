package io.seqware.pipeline.api;

import io.seqware.common.model.WorkflowRunStatus;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.module.ReturnValue.ExitStatus;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.Rethrow;
import net.sourceforge.seqware.common.util.maptools.MapTools;
import net.sourceforge.seqware.common.util.maptools.ReservedIniKeys;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo;

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

        Map<String, String> workflowMetadata = this.metadata.get_workflow_info(Integer.parseInt(workflowAccession));
        WorkflowInfo wi = parseWorkflowMetadata(workflowMetadata);
        return scheduleWorkflow(wi, iniFiles, metadataWriteback, parentAccessions, parentsLinkedToWR, cmdLineOptions, scheduledHost,
                workflowEngine, inputFiles);

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
            Set<Integer> inputFiles) {

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

            workflowRunId = this.metadata.add_workflow_run(workflowAccession);
            int workflowRunAccessionInt = this.metadata.get_workflow_run_accession(workflowRunId);
            String workflowRunAccession = Integer.toString(workflowRunAccessionInt);

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
                    Log.error("Could not link workflow run to its parents " + parentsLinkedToWR.toString());
                    Rethrow.rethrow(e);
                }
            }

            this.metadata.update_workflow_run(workflowRunId, wi.getCommand(), wi.getTemplatePath(), WorkflowRunStatus.submitted, null,
                    null, null, mapBuffer.toString(), scheduledHost, null, null, workflowEngine, inputFiles);

            ReturnValue ret = new ReturnValue();
            ret.setReturnValue(Integer.valueOf(workflowRunAccession));
            return ret;

        } else {
            Log.error("you can't schedule a workflow run unless you have metadata writeback turned on.");
            return new ReturnValue(ExitStatus.METADATAINVALIDIDCHAIN);
        }
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

}
