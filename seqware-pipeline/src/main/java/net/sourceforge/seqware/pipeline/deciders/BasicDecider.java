/*
 * Copyright (C) 2012 SeqWare
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
package net.sourceforge.seqware.pipeline.deciders;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.seqware.common.hibernate.FindAllTheFiles;
import net.sourceforge.seqware.common.hibernate.FindAllTheFiles.Header;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.metadata.MetadataDB;
import net.sourceforge.seqware.common.metadata.MetadataWS;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.common.util.filetools.FileTools.LocalhostPair;
import net.sourceforge.seqware.pipeline.decider.DeciderInterface;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.pipeline.runner.PluginRunner;
import net.sourceforge.seqware.pipeline.tools.SetOperations;
import org.apache.commons.lang.StringUtils;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author mtaschuk
 */
@ServiceProvider(service = PluginInterface.class)
public class BasicDecider extends Plugin implements DeciderInterface {

    protected ReturnValue ret = new ReturnValue();
    private MetadataWS metaws;
    private Header header = Header.FILE_SWA;
    private Set<String> parentWorkflowAccessions = new TreeSet<String>();
    private Set<String> workflowAccessionsToCheck = new TreeSet<String>();
    private List<String> metaTypes = null;
    private Boolean forceRunAll = null;
    private Boolean test = null;
    private String workflowAccession = null;
    protected Random random = new Random(System.currentTimeMillis());
    private Boolean metadataWriteback = null;
    private Collection<String> parentAccessionsToRun;
    private Collection<String> filesToRun;
    private Collection<String> workflowParentAccessionsToRun;
    private Set<String> studyReporterOutput;
    private ArrayList<String> iniFiles;
    private Boolean runNow = null;
    private Boolean skipStuff = null;
    private int launchMax = Integer.MAX_VALUE, launched = 0;
    private int rerunMax = 5;
    private String host = null;
    private boolean legacy_0_13_6_5 = false;

    public BasicDecider() {
        super();
        parser.acceptsAll(Arrays.asList("wf-accession"), "The workflow accession of the workflow").withRequiredArg();
        parser.acceptsAll(Arrays.asList("study-name"), "Full study name. One of sample-name, study-name, sequencer-run-name or all is required.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("sample-name"), "Full sample name. One of sample-name, study-name, sequencer-run-name or all is required.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("sequencer-run-name"), "Full sequencer run name. One of sample-name, study-name, sequencer-run-name or all is required.").withRequiredArg();
        parser.accepts("all", "Run everything. One of sample-name, study-name, sequencer-run-name or all is required.");
        parser.acceptsAll(Arrays.asList("group-by"), "Optional: Group by one of the headings in FindAllTheFiles. Default: FILE_SWA. One of LANE_SWA or IUS_SWA.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("parent-wf-accessions"), "The workflow accessions of the parent workflows, comma-separated with no spaces. May also specify the meta-type.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("meta-types"), "The comma-separated meta-type(s) of the files to run this workflow with. Alternatively, use parent-wf-accessions.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("check-wf-accessions"), "The comma-separated, no spaces, workflow accessions of the workflow that perform the same function (e.g. older versions). Any files that have been processed with these workflows will be skipped.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("force-run-all"), "Forces the decider to run all matches regardless of whether they've been run before or not");
        parser.acceptsAll(Arrays.asList("test"),
                "Testing mode. Prints the INI files to standard out and does not submit the workflow.");
        parser.acceptsAll(Arrays.asList("no-meta-db", "no-metadata"),
                "Optional: a flag that prevents metadata writeback (which is done "
                + "by default) by the Decider and that is subsequently "
                + "passed to the called workflow which can use it to determine if "
                + "they should write metadata at runtime on the cluster.");
        parser.acceptsAll(Arrays.asList("schedule"), "Schedule this workflow to be run rather than running it immediately. See also: --run");
        parser.acceptsAll(Arrays.asList("run"), "Run this workflow now. This is the default behaviour. See also: --schedule");
        parser.acceptsAll(Arrays.asList("ignore-skip-flag"), "Ignores any 'skip' flags on lanes, IUSes, sequencer runs, samples, etc. Use caution.");
        parser.acceptsAll(Arrays.asList("launch-max"), "The maximum number of jobs to launch at once. Default: infinite.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("rerun-max"), "The maximum number of times to re-launch a workflowrun if failed. Default: 5.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("host", "ho"), "Used only in combination with --schedule to schedule onto a specific host. If not provided, the default is the local host").withRequiredArg();
        parser.acceptsAll(Arrays.asList("legacy-0-13-6-5"),"Legacy mode, used to communicate with 0.13.6.5-era web services. WARNING: This will disable the ability to disable re-launch based on previous failed runs.");
        ret.setExitStatus(ReturnValue.SUCCESS);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String get_description() {
        return "The decider from which all other deciders came";
    }

    @Override
    /**
     * This method is intended to be called AFTER any implementing class's init
     * method.
     */
    public ReturnValue init() {

        if (!(options.has("study-name")
                ^ options.has("sample-name")
                ^ options.has("sequencer-run-name")
                ^ options.has("all"))) {
            Log.stdout(this.get_syntax());
            Log.error("Please provide one of sample-name, study-name, sequencer-run-name or all");
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        }

        try {
            ResourceBundle rb = PropertyResourceBundle.getBundle("decider");
            String parents = rb.getString("parent-workflow-accessions");
            String checks = rb.getString("check-wf-accessions");
            String wfa = rb.getString("workflow-accession");
            if (wfa != null && !wfa.trim().isEmpty()) {
                this.setWorkflowAccession(wfa);
            }
            if (parents != null && !parents.trim().isEmpty()) {
                List<String> pas = Arrays.asList(parents.split(","));
                this.setParentWorkflowAccessions(new TreeSet(pas));
            }
            if (checks != null && !checks.trim().isEmpty()) {
                List<String> cwa = Arrays.asList(checks.split(","));
                this.setWorkflowAccessionsToCheck(new TreeSet(cwa));

            }
        } catch (MissingResourceException e) {
            Log.debug("No decider resource found: ", e);
        }

        //Group-by allows you to group processing events based on one characteristic.
        //Normally, this allows you to run on a group of samples (for example, all
        //of the IUS-level BAM files). The default is no grouping, so the workflow
        //will be run independently on every file it finds
        if (options.has("group-by")) {
            String headerString = (String) options.valueOf("group-by");
            try {
                header = Header.valueOf(headerString);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                StringBuilder sb = new StringBuilder();
                sb.append("group-by attribute must be one of the following: \n");
                for (Header h : Header.values()) {
                    sb.append("\t").append(h.name()).append("\n");

                }
                Log.stdout(sb.toString());
                ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            }
        }

        if (options.has("wf-accession")) {
            workflowAccession = (String) options.valueOf("wf-accession");
        } else if (workflowAccession == null) {
            Log.error("Must specify the workflow-accession of the workflow to run");
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        }

        // Check for filtering on the files. Either parent workflow accessions
        // or file meta-types, or both
        boolean hasFilter = false;
        if (options.has("parent-wf-accessions")) {
            String pas = (String) options.valueOf("parent-wf-accessions");
            for (String p : pas.split(",")) {
                parentWorkflowAccessions.add(p.trim());
                hasFilter = true;
            }
        }
        if (options.has("meta-types")) {
            String mt = (String) options.valueOf("meta-types");
            metaTypes = Arrays.asList(mt.split(","));
            hasFilter = true;
        }



        if (!hasFilter && parentWorkflowAccessions.isEmpty() && metaTypes == null) {
            Log.error("You must run a decider with parent-wf-accessions or meta-types (or both).");
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        }


        //Repeat-checking options. if present, check-wf-accessions will check to
        //see if the samples have been run through a particular workflow before.
        //These workflows will not be launched again
        //Optionally you can force the decider to re-run all possibilities in
        //the database with force-run-all.
        if (options.has("check-wf-accessions")) {

            String pas = (String) options.valueOf("check-wf-accessions");
            Log.debug("Pas = " + pas);
            if (pas.contains(",")) {
                for (String p : pas.split(",")) {
                    workflowAccessionsToCheck.add(p.trim());
                }
            } else {
                workflowAccessionsToCheck.add(pas.trim());
            }
            //Separate out this logic
            //workflowAccessionsToCheck.add(workflowAccession);
        }
        if (forceRunAll == null) {
            forceRunAll = options.has("force-run-all");
        }

        //test turns off all of the submission functions and just prints to stdout
        if (test == null) {
            test = options.has("test");
        }
        if (test) {
            StringWriter writer = new StringWriter();
            try {
                FindAllTheFiles.printHeader(writer, true);
                Log.stdout(writer.toString());
            } catch (IOException ex) {
                Log.error(ex);
            }
        }

        if (skipStuff == null) {
            skipStuff = !options.has("ignore-skip-flag");
        }


        if (metadataWriteback == null) {
            metadataWriteback = !(options.has("no-metadata") || options.has("no-meta-db"));
        }
        
        if (options.has("legacy-0-13-6-5")){
            legacy_0_13_6_5 = true;
        }

        if (runNow == null) {
            if (options.has("schedule")) {
                runNow = false;
            } else {
                runNow = true;
            }
        }

        LocalhostPair localhostPair = FileTools.getLocalhost(options);
        String localhost = localhostPair.hostname;
        if (host == null) {
            if (options.has("host") || options.has("ho")) {
                host = (String) options.valueOf("host");
            } else {
                host = localhost;
            }
        }
        if (localhostPair.returnValue.getExitStatus() != ReturnValue.SUCCESS && host == null) {
            Log.error("Could not determine localhost: Return value " + localhostPair.returnValue.getExitStatus());
            Log.error("Please supply it on the command line with --host");
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        } else if (!host.equals(localhost)) {
            Log.warn("The localhost and the scheduling host are not the same: " + localhost + " and " + host + ". Proceeding anyway.");
        }

        if (options.has("launch-max")) {
            try {
                launchMax = Integer.parseInt(options.valueOf("launch-max").toString());
            } catch (NumberFormatException e) {
                Log.error("The launch-max parameter must be an integer. Unparseable integer: " + options.valueOf("launch-max").toString());
                ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            }
        }

        if (options.has("rerun-max")) {
            try {
                rerunMax = Integer.parseInt(options.valueOf("rerun-max").toString());
            } catch (NumberFormatException e) {
                Log.error("The rerun-max parameter must be an integer. Unparseable integer: " + options.valueOf("rerun-max").toString());
                ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            }
        }

        if (workflowAccession == null || "".equals(workflowAccession)) {
            Log.error("The wf-accession must be defined.");
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        }

        if (metadata instanceof MetadataDB) {
            metaws = new MetadataWS();
            ret = metaws.init(config.get("SW_REST_URL"), config.get("SW_REST_USER"), config.get("SW_REST_PASS"));
        } else if (metadata instanceof MetadataWS) {
            metaws = (MetadataWS) metadata;
            //metadb = new MetadataDB();
            //String connection = "jdbc:postgresql://" + config.get("SW_DB_SERVER") + "/" + config.get("SW_DB");
            //ret = metadb.init(connection, config.get("SW_DB_USER"), config.get("SW_DB_PASS"));
        } else {
            ret.setExitStatus(ReturnValue.DBCOULDNOTINITIALIZE);
        }
        return ret;
    }

    @Override
    public ReturnValue do_test() {
        return ReturnValue.featureNotImplemented();
    }

    @Override
    public ReturnValue do_run() {
        String groupBy = header.getTitle();
        Map<String, List<ReturnValue>> mappedFiles = null;
        List<ReturnValue> vals = null;

        if (options.has("all")) {
            List<ReturnValue> rv;
            List<Study> studies = metadata.getAllStudies();
            for (Study study : studies) {
                String name = study.getTitle();
                Log.stdout("Retrieving study " + name);
                rv = metadata.findFilesAssociatedWithAStudy(name, true);
                mappedFiles = separateFiles(rv, groupBy);
                ret = launchWorkflows(mappedFiles);
                if (ret.getExitStatus() != ReturnValue.SUCCESS) {
                    break;
                }
            }
            return ret;
        } else if (options.has("study-name")) {
            String studyName = (String) options.valueOf("study-name");
            vals = metaws.findFilesAssociatedWithAStudy(studyName, true);
        } else if (options.has("sample-name")) {
            String sampleName = (String) options.valueOf("sample-name");
            vals = metaws.findFilesAssociatedWithASample(sampleName, true);
        } else if (options.has("sequencer-run-name")) {
            String runName = (String) options.valueOf("sequencer-run-name");
            vals = metaws.findFilesAssociatedWithASequencerRun(runName, true);
        } else {
            Log.error("Unknown option");
        }

        mappedFiles = separateFiles(vals, groupBy);
        ret = launchWorkflows(mappedFiles);
        return ret;
    }

    private ReturnValue launchWorkflows(Map<String, List<ReturnValue>> mappedFiles) {
        if (mappedFiles != null) {

            for (Entry<String, List<ReturnValue>> entry : mappedFiles.entrySet()) {
                Log.info("Considering key:" + entry.getKey());

                parentAccessionsToRun = new HashSet<String>();
                filesToRun = new HashSet<String>();
                workflowParentAccessionsToRun = new HashSet<String>();
                studyReporterOutput = new HashSet<String>();

                //for each grouping (e.g. sample), iterate through the files
                List<ReturnValue> files = entry.getValue();
                Log.info("key:" + entry.getKey() + " consists of " + files.size() + " files");
                List<Integer> fileSWIDs = new ArrayList<Integer>();
                
                for (ReturnValue file : files) {
                    String wfAcc = file.getAttribute(Header.WORKFLOW_SWA.getTitle());
                    String fileSWID = file.getAttribute(Header.FILE_SWA.getTitle());
                    fileSWIDs.add(Integer.valueOf(fileSWID));
                    Log.debug(Header.WORKFLOW_SWA.getTitle() + ": WF accession is " + wfAcc);

                    //if there is no parent accessions, or if the parent accession is correct
                    //this makes an assumption that if the wfAcc is null then the parentWorkflowAccessions will be empty
                    //and thus we are able to find files of a particular metatype with no wfAcc
                    if (parentWorkflowAccessions.isEmpty() || (wfAcc != null && parentWorkflowAccessions.contains(wfAcc))) {

                        //check for each file if the metatype is correct (if it exists), 
                        //or just add it
                        for (FileMetadata fm : file.getFiles()) {
                            if (metaTypes != null) {
                                if (metaTypes.contains(fm.getMetaType())) {
                                    addFileToSets(file, fm, workflowParentAccessionsToRun,
                                            parentAccessionsToRun, filesToRun);
                                }
                            } else {
                                addFileToSets(file, fm, workflowParentAccessionsToRun,
                                        parentAccessionsToRun, filesToRun);
                            }
                        }

                    }
                }//end iterate through files
                

                if (!parentAccessionsToRun.isEmpty() && !filesToRun.isEmpty() && !workflowParentAccessionsToRun.isEmpty()) {
                    final String parentAccessionString = commaSeparateMy(parentAccessionsToRun);
                    final String fileString = commaSeparateMy(filesToRun);
                    Log.debug("FileString: " + fileString);
                    // SEQWARE-1773 short-circuit this with forceRunAll to ensure that sample fingerprinting workflow launches
                    boolean rerun = forceRunAll || rerunWorkflowRun(filesToRun, fileSWIDs);

                    // SEQWARE-1728 - move creation of ini to launches (and test launches) to conserve disk space 
                    iniFiles = new ArrayList<String>();

                    ReturnValue newRet = this.doFinalCheck(fileString, parentAccessionString);
                    if (newRet.getExitStatus() != ReturnValue.SUCCESS) {
                        Log.warn("Final check failed. Return value was: " + newRet.getExitStatus());
                        rerun = false;
                    }

                    if (forceRunAll) {
                        Log.debug("Forcing the running of this workflow because --force-run-all was enabled");
                        rerun = true;
                    }
                    //if we're in testing mode or we don't want to rerun and we don't want to force the re-processing
                    if (test || !rerun){
                        // we need to simplify the logic and make it more readable here for testing
                        if (rerun){
                            iniFiles.add(createIniFile(fileString, parentAccessionString));
                            for(String line: studyReporterOutput){
                                Log.stdout(line);
                            }
                            Log.debug("NOT RUNNING (but would have ran). test=" + test + " or !rerun=" + !rerun);
                            reportLaunch();
                            // SEQWARE-1642 - output to stdout only whether a decider would launch
                            ret = do_summary();
                            launched++;
                        } else{
                             for(String line: studyReporterOutput){
                                Log.debug(line);
                            }
                            Log.debug("NOT RUNNING (and would not have ran). test=" + test + " or !rerun=" + !rerun);
                        }
                    } else if (launched < launchMax) {
                        iniFiles.add(createIniFile(fileString, parentAccessionString));
                        launched++;
                        //construct the INI and run it
                        for (String line : studyReporterOutput) {
                            Log.stdout(line);
                        }
                        Log.debug("RUNNING");
//                        // setup workflow object
//                        Workflow w = new Workflow(metadata, config);
//                        if (runNow) {
//                            ret = w.launchInstalledBundle(workflowAccession, null,
//                                    iniFiles, metadataWriteback, new ArrayList(parentAccessionsToRun),
//                                    new ArrayList(workflowParentAccessionsToRun), false, options.nonOptionArguments());
//                        } else {
//                            ret = w.scheduleInstalledBundle(workflowAccession, null,
//                                    iniFiles, metadataWriteback, new ArrayList(parentAccessionsToRun),
//                                    new ArrayList(workflowParentAccessionsToRun), false, options.nonOptionArguments());
//                        }
                        //construct the INI and run it                     
                        ArrayList<String> runArgs = constructCommand();
                        PluginRunner.main(runArgs.toArray(new String[runArgs.size()]));
                        Log.stdout("Launching.");
                        do_summary();
                        
                    } 
                    // separate this out so that it is reachable when in --test
                    if (launched >= launchMax) {
                        Log.info("The maximum number of jobs has been "+(runNow?"launched":"scheduled")+". The next jobs will be launched when the decider runs again.");
                        ret.setExitStatus(ReturnValue.QUEUED);
                        // SEQWARE-1666 - short-circuit and exit when the maximum number of jobs have been launched
                        return ret;
                    }
                } else {
                    Log.debug("Cannot run: parentAccessions: "+parentAccessionsToRun.size() + " filesToRun: "+ filesToRun.size()+ " workflowParentAccessions: "+ workflowParentAccessionsToRun.size());
                }

            }
        } else {
            Log.stdout("There are no files");
        }
        return ret;
    }

    protected ArrayList<String> constructCommand() {
        ArrayList<String> runArgs = new ArrayList<String>();
        runArgs.add("--plugin");
        runArgs.add("net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher");
        runArgs.add("--");
        runArgs.add("--workflow-accession");
        runArgs.add(workflowAccession);
        runArgs.add("--ini-files");
        runArgs.add(commaSeparateMy(iniFiles));
        if (!metadataWriteback) {
            runArgs.add("--no-metadata");
        }
        runArgs.add("--parent-accessions");
        runArgs.add(commaSeparateMy(parentAccessionsToRun));
        runArgs.add("--link-workflow-run-to-parents");
        runArgs.add(commaSeparateMy(workflowParentAccessionsToRun));
        if (!runNow) {
            runArgs.add("--schedule");
        }
        runArgs.add("--host");
        runArgs.add(host);

        runArgs.add("--");
        for (String s : options.nonOptionArguments()) {
            runArgs.add(s);
        }
        return runArgs;
    }

    /**
     * Returns true only if there are more files to run than have been run on
     * any workflow so far, or if the filesToRun have different filepaths than
     * those that have been run before.
     * @param filesToRun
     * @param fileSWIDs
     * @return 
     */
    protected boolean rerunWorkflowRun(final Collection<String> filesToRun, List<Integer> fileSWIDs) {
        
        boolean rerun;
        List<Boolean> failures = new ArrayList<Boolean>();
        List<WorkflowRun> runs1 = produceAccessionListWithFileList(fileSWIDs, "CHILDREN_VIA_PROCESSING_RELATIONSHIP");
        rerun = processWorkflowRuns(filesToRun, failures, runs1);
        if (!rerun){
            Log.debug("This workflow has failed to launch based on workflow runs found via Processing");
            return rerun;
        }
        List<WorkflowRun> runs2 = produceAccessionListWithFileList(fileSWIDs, "CHILDREN_VIA_IUS_WORKFLOW_RUN");
        rerun = processWorkflowRuns(filesToRun, failures, runs2);
        if (!rerun){
            Log.debug("This workflow has failed to launch based on workflow runs found via IUS");
            return rerun;
        }
        List<WorkflowRun> runs3 = produceAccessionListWithFileList(fileSWIDs, "CHILDREN_VIA_LANE_WORKFLOW_RUN");
        rerun = processWorkflowRuns(filesToRun, failures, runs3);
        if (!rerun){
            Log.debug("This workflow has failed to launch based on workflow runs found via Lane");
            return rerun;
        }
        if (failures.size() >= this.rerunMax) {
            Log.debug("This workflow has failed " + rerunMax + " times: not running");
            rerun = false;
        }
        return rerun;
    }
    
    /**
     * Determines whether a workflow run completed, failed, or other (submitted, pending, etc.)
     * @param workflowRunAcc
     * @return 
     */
    protected PREVIOUS_RUN_STATUS determineStatus(int workflowRunAcc){
        String generateStatus = this.generateStatus(workflowRunAcc);
        if (generateStatus.equals(Metadata.COMPLETED)){
            return PREVIOUS_RUN_STATUS.COMPLETED;
        } else if (generateStatus.equals(Metadata.FAILED)){
            return PREVIOUS_RUN_STATUS.FAILED;
        } else{
            return PREVIOUS_RUN_STATUS.OTHER;
        }
    }
    
    /**
     * Returns true if the filesToRun are totally contained by the files associated with the 
     * files in a given workflowRunAcc
     * @param workflowRunAcc accession of the workflow run to compare against
     * @param filesToRun the files to check to see if they are contained by the past run
     * @return 
     */
    protected boolean isToRunContained(int workflowRunAcc, Collection<String> filesToRun) {
        List<String> ranOnList = getListOfFiles(workflowRunAcc);
        Log.info("Files to run: " + StringUtils.join(filesToRun,','));
        // use set operations to be more explicit about our cases
        Set<String> setToRun = new HashSet<String>(filesToRun);
        Set<String> setHasRun = new HashSet<String>(ranOnList);
        return SetOperations.isSuperset(setHasRun, setToRun);
    }
    
    /**
     * Tests if the files from the workflow run (workflowRunAcc) are the same as
     * those found in the database (filesToRun). True if the filesToRun has more
     * files than the workflow run. True if the filesToRun and the workflow run
     * have the same number of files but with different filepaths. False if the
     * filesToRun and the workflow run have the same number of files with the
     * same file paths. False and prints an error message if there are more
     * files in the workflow run than in the filesToRun.
     */
    protected FILE_STATUS compareWorkflowRunFiles(int workflowRunAcc, Collection<String> filesToRun) {
        List<String> ranOnList = getListOfFiles(workflowRunAcc);
        Log.info("Files to run: " + StringUtils.join(filesToRun,','));
        Log.info("Files has run: " + StringUtils.join(ranOnList,','));

        // use set operations to be more explicit about our cases
        Set<String> setToRun = new HashSet<String>(filesToRun);
        Set<String> setHasRun = new HashSet<String>(ranOnList);
        if (setToRun.equals(setHasRun)){
            return FILE_STATUS.SAME_FILES;
        }
        if (SetOperations.isSubset(setHasRun, setToRun)){
            return FILE_STATUS.PAST_SUBSET_OR_INTERSECTION;
        }
        if (SetOperations.isSuperset(setHasRun, setToRun)){
            return FILE_STATUS.PAST_SUPERSET;
        }
        if (SetOperations.intersection(setToRun, setHasRun).size() > 0){
            return FILE_STATUS.PAST_SUBSET_OR_INTERSECTION;
        }
        return FILE_STATUS.DISJOINT_SETS;
    }

    private void addFileToSets(ReturnValue file, FileMetadata fm, Collection<String> workflowParentAccessionsToRun,
            Collection<String> parentAccessionsToRun, Collection<String> filesToRun) {
        if (checkFileDetails(file, fm)) {
            if (skipStuff) {
                for (String key : file.getAttributes().keySet()) {
                    if (key.contains("skip")) {
                        Log.warn("File SWID:" + fm.getDescription() + " path " + fm.getFilePath() + " is skipped: " + key + ">" + file.getAttribute(key));
                        return;
                    }
                }
            }
            if (test) {
                printFileMetadata(file, fm);
            }

            filesToRun.add(fm.getFilePath());
            parentAccessionsToRun.add(file.getAttribute(Header.PROCESSING_SWID.getTitle()));

            String swid = file.getAttribute(Header.IUS_SWA.getTitle());
            if (swid == null || swid.trim().isEmpty()) {
                swid = file.getAttribute(Header.LANE_SWA.getTitle());
            }
            workflowParentAccessionsToRun.add(swid);

        }
    }

    protected void printFileMetadata(ReturnValue file, FileMetadata fm) {
        String studyName = (String) options.valueOf("study-name");
        try {
            StringWriter writer = new StringWriter();
            FindAllTheFiles.print(writer, file, studyName, true, fm);
            studyReporterOutput.add(writer.getBuffer().toString().trim());
        } catch (IOException ex) {
            Log.error("Error printing file metadata", ex);
        }
    }

    protected String commaSeparateMy(Collection<String> list) {
        return separateMy(list, ",");
    }

    protected String spaceSeparateMy(Collection<String> list) {
        return separateMy(list, " ");
    }

    private String separateMy(Collection<String> list, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            if (sb.length() != 0) {
                sb.append(delimiter);
            }
            sb.append(s);
        }
        return sb.toString();
    }

    private String createIniFile(String commaSeparatedFilePaths, String commaSeparatedParentAccessions) {
        String iniPath = "";

        Map<String, String> iniFileMap = new TreeMap<String, String>();
        SortedSet<WorkflowParam> wps = metaws.getWorkflowParams(workflowAccession);
        for (WorkflowParam param : wps) {
            iniFileMap.put(param.getKey(), param.getDefaultValue());
        }

        Map<String, String> iniParameters = modifyIniFile(commaSeparatedFilePaths, commaSeparatedParentAccessions);

        for (String param : iniParameters.keySet()) {
            iniFileMap.put(param, iniParameters.get(param));
        }

        PrintWriter writer = null;
        File file = null;
        try {
            file = File.createTempFile("" + random.nextInt(), ".ini");
            writer = new PrintWriter(new FileWriter(file), true);

            for (String key : iniFileMap.keySet()) {
                StringBuilder sb = new StringBuilder();
                sb.append(key).append("=").append(iniFileMap.get(key));
                writer.println(sb.toString());
            }

        } catch (IOException ex) {
            Logger.getLogger(BasicDecider.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        if (file != null) {
            iniPath = file.getAbsolutePath();
        }
        return iniPath;

    }

    /**
     * Performs any additional checks on the file before adding it to the list
     * of files to incorporate. This method should be extended for future
     * deciders for custom behaviour. You can also pull any details out of the
     * file metadata here.
     *
     * @param returnValue The ReturnValue representing the Processing event. May
     * have one or more files. The attributes table contains the information
     * from FindAllTheFiles.Header.
     * @param fm the particular file that will be added
     * @return true if the file can be added to the list, false otherwise
     */
    protected boolean checkFileDetails(ReturnValue returnValue, FileMetadata fm) {
        return true;
    }

    protected Map<String, String> modifyIniFile(String commaSeparatedFilePaths, String commaSeparatedParentAccessions) {
        Map<String, String> iniFileMap = new TreeMap<String, String>();
        iniFileMap.put("input_files", commaSeparatedFilePaths);
        return iniFileMap;
    }

    protected String handleGroupByAttribute(String attribute) {
        return attribute;
    }

//    protected
    public Map<String, List<ReturnValue>> separateFiles(List<ReturnValue> vals, String groupBy) {
        //get files from study
        Map<String, List<ReturnValue>> map = new HashMap<String, List<ReturnValue>>();

        //group files according to the designated header (e.g. sample SWID)
        for (ReturnValue r : vals) {
            // SEQWARE-1809 ensure that deciders only use input from completed workflow runs
            String status = r.getAttribute(FindAllTheFiles.WORKFLOW_RUN_STATUS);
            if (status == null || !status.equals("completed")){
                continue;
            }
            
            String currVal = r.getAttributes().get(groupBy);
            
            if (currVal != null){
                currVal = handleGroupByAttribute(currVal);
            }

            List<ReturnValue> vs = map.get(currVal);
            if (vs == null) {
                vs = new ArrayList<ReturnValue>();
            }
            vs.add(r);
            map.put(currVal, vs);
        }
        return map;

    }

    @Override
    public ReturnValue clean_up() {
        return ReturnValue.featureNotImplemented();
    }

    @Override
    public ReturnValue do_summary() {
        String command = do_summary_command();
        Log.stdout(command);
        return ret;
    }

    public Boolean getForceRunAll() {
        return forceRunAll;
    }

    public void setForceRunAll(Boolean forceRunAll) {
        this.forceRunAll = forceRunAll;
    }

    /**
     * use getGroupingStrategy
     *
     * @return
     */
    @Deprecated
    public Header getHeader() {
        return header;
    }

    public Header getGroupingStrategy() {
        return this.header;
    }

    /**
     * use setGroupingStrategy
     *
     * @param header
     */
    @Deprecated
    public void setHeader(Header header) {
        this.header = header;
    }

    public void setGroupingStrategy(Header strategy) {
        this.header = strategy;
    }

    public List<String> getMetaType() {
        return metaTypes;
    }

    public void setMetaType(List<String> metaType) {
        this.metaTypes = metaType;
    }

    public Boolean getMetadataWriteback() {
        return metadataWriteback;
    }

    public void setMetadataWriteback(Boolean metadataWriteback) {
        this.metadataWriteback = metadataWriteback;
    }

    public Set<String> getParentWorkflowAccessions() {
        return parentWorkflowAccessions;
    }

    public void setParentWorkflowAccessions(Set<String> parentWorkflowAccessions) {
        this.parentWorkflowAccessions = parentWorkflowAccessions;
    }

    public Boolean getTest() {
        return test;
    }

    public void setTest(Boolean test) {
        this.test = test;
    }

    public String getWorkflowAccession() {
        return workflowAccession;
    }

    public void setWorkflowAccession(String workflowAccession) {
        this.workflowAccession = workflowAccession;
    }

    public Set<String> getWorkflowAccessionsToCheck() {
        return workflowAccessionsToCheck;
    }

    public void setWorkflowAccessionsToCheck(Set<String> workflowAccessions) {
        this.workflowAccessionsToCheck = workflowAccessions;
    }

    /**
     * allow to user to do the final check and decide to run or cancel the
     * decider e.g. check if all files are present
     *
     * @param commaSeparatedFilePaths
     * @param commaSeparatedParentAccessions
     * @return
     */
    protected ReturnValue doFinalCheck(String commaSeparatedFilePaths, String commaSeparatedParentAccessions) {
        ReturnValue checkReturnValue = new ReturnValue(ReturnValue.SUCCESS);
        return checkReturnValue;
    }

    /**
     * Report an actual launch of a workflow for testing purpose
     * @return false iff we don't actually want to launch
     */
    protected boolean reportLaunch() {
        return true;
    }

    private Map<String, String> generateWorkflowRunMap(int workflowRunAcc) {
        String report = metaws.getWorkflowRunReport(workflowRunAcc);
        String[] lines = report.split("\n");
        String[] reportHeader = lines[0].split("\t");
        String[] data = lines[1].split("\t");
        Map<String, String> map = new TreeMap<String, String>();
        for (int i = 0; i < reportHeader.length; i++) {
            map.put(reportHeader[i].trim(), data[i].trim());
        }
        return map;
    }
    
    
    public void setMetaws(MetadataWS metaws) {
        this.metaws = metaws;
    }

    protected String generateStatus(int workflowRunAcc) {
        Map<String, String> map = generateWorkflowRunMap(workflowRunAcc);
        String status = map.get("Workflow Run Status");
        Log.debug("Status is " + status);
        return status;
    }

    /**
     * We now use the guideline that we only count failures when they occur on the same number of files
     * (with the same paths)
     * @param fileStatus
     * @param previousStatus
     * @return 
     */
    protected static boolean isCountAsFail(FILE_STATUS fileStatus, PREVIOUS_RUN_STATUS previousStatus) {
        return (fileStatus == FILE_STATUS.SAME_FILES && previousStatus == PREVIOUS_RUN_STATUS.FAILED);
    }

    /**
     * See https://wiki.oicr.on.ca/display/SEQWARE/BasicDecider+logic
     * @param fileStatus
     * @param previousStatus
     * @return
     */
    protected static boolean isDoRerun(FILE_STATUS fileStatus, PREVIOUS_RUN_STATUS previousStatus) {
        Log.info("Considering match with " + fileStatus.name() + " status:" + previousStatus.name());
        boolean strangeCondition = fileStatus == FILE_STATUS.PAST_SUPERSET && previousStatus == PREVIOUS_RUN_STATUS.FAILED;
        if (strangeCondition){
            Log.stderr("****** Workflow run has more files in the past but failed. We will try to re-run, but you should investigate!!!! *******");
        }
        boolean doRerun = true;
        if (fileStatus == FILE_STATUS.PAST_SUBSET_OR_INTERSECTION){
            if (previousStatus == PREVIOUS_RUN_STATUS.OTHER){
                doRerun = false;     
            } else{
                doRerun = true;
            }
        }
        else if (fileStatus == FILE_STATUS.SAME_FILES || fileStatus == FILE_STATUS.PAST_SUPERSET){ 
             if (previousStatus == PREVIOUS_RUN_STATUS.FAILED){
                doRerun = true;
             } else{
                doRerun = false;
             }
        } 
        return doRerun;
    }

    private List<WorkflowRun> produceAccessionListWithFileList(List<Integer> fileSWIDs, String searchType) {
        // find relevant workflow runs for this group of files
        List<WorkflowRun> wrFiles1 = this.metadata.getWorkflowRunsAssociatedWithFiles(fileSWIDs, searchType);
        Log.debug("Found " + wrFiles1.size() + " workflow runs via " + searchType);
        return wrFiles1;
    }

    /**
     * For a given set of file SWIDs in filesToRun, we will count up the number of previous workflow runs that 
     * failed and return whether or not we think the workflow should be rerun
     * @param filesToRun
     * @param failures
     * @param previousWorkflowRuns
     * @return 
     */
    private boolean processWorkflowRuns(Collection<String> filesToRun, List<Boolean> failures, List<WorkflowRun> previousWorkflowRuns) {
        int count = 0; 
        boolean rerun = true;
        for (WorkflowRun previousWorkflowRun : previousWorkflowRuns) {
            count++;
            // only consider previous runs of the same workflow
            if (workflowAccession.equals(previousWorkflowRun.getWorkflowAccession().toString())) {
                FILE_STATUS fileStatus = compareWorkflowRunFiles(previousWorkflowRun.getSwAccession(), filesToRun);
                Log.info("Workflow run " + previousWorkflowRun.getSwAccession() + " has a file status of " + fileStatus);
                PREVIOUS_RUN_STATUS previousStatus = determineStatus(previousWorkflowRun.getSwAccession());
                Log.info("Workflow run " + previousWorkflowRun.getSwAccession() + " has a status of " + previousStatus);
                
                boolean countAsFail = isCountAsFail(fileStatus, previousStatus);
                boolean doRerun = isDoRerun(fileStatus, previousStatus);
                
                if (countAsFail){
                    Log.info("Workflow run " + previousWorkflowRun.getSwAccession() + " counted as a failure with a file status of " + fileStatus);
                    Log.info("The failing run was workflow_run " + count  + "/" + previousWorkflowRuns.size() + " out of " + previousWorkflowRuns.size());
                    failures.add(true);
                }
                if (!doRerun){
                    Log.info("Workflow run " + previousWorkflowRun.getSwAccession() + " blocking re-run with a status of: " +previousStatus+"  file status of: " + fileStatus);
                    Log.info("The blocking run was workflow_run " + count + "/" + previousWorkflowRuns.size() + " out of " + previousWorkflowRuns.size());
                    rerun = false;
                    break;
                }
            } else if (this.workflowAccessionsToCheck.contains(previousWorkflowRun.getWorkflowAccession().toString())){
                Log.debug("Workflow run " + previousWorkflowRun.getWorkflowAccession() + " has a workflow "+previousWorkflowRun.getWorkflowAccession()+" on the list of workflow accessions to check");
                // we will check whether all the files to run are contained within the previous run of the workflow, if so we will not re-run
                FILE_STATUS fileStatus = compareWorkflowRunFiles(previousWorkflowRun.getSwAccession(), filesToRun);
                Log.info("Workflow run " + previousWorkflowRun.getSwAccession() + " has a file status of " + fileStatus);
                if (this.isToRunContained(previousWorkflowRun.getSwAccession(), filesToRun)){
                    Log.info("Previous workflow run contained the all of the files that we want to run");
                    rerun = false;
                }         
            } else{
                Log.info("Workflow run "  + previousWorkflowRun.getSwAccession() + " was neither a workflow to check nor a previous run of " +workflowAccession+" , ignored");
            }
        }
        return rerun;
    }

    /**
     * Given a workflowRunAcc returns a list of file paths that were used in that 
     * run
     * @param workflowRunAcc
     * @param filesToRun
     * @return 
     */
    private List<String> getListOfFiles(int workflowRunAcc) {
        Map<String, String> map = generateWorkflowRunMap(workflowRunAcc);
        String ranOnString = map.get("Immediate Input File Meta-Types");
        if (legacy_0_13_6_5){
            ranOnString = map.get("Input File Meta-Types");
        }
        List<String> ranOnList = Arrays.asList(ranOnString.split(","));
        List<Integer> indices = new ArrayList<Integer>();
        for (int i=0;i<ranOnList.size(); i++)
        {
            if (metaTypes.contains(ranOnList.get(i).trim())) {
                indices.add(i);
            }
        }
        ranOnString = map.get("Immediate Input File Paths");
        if (legacy_0_13_6_5){
            ranOnString = map.get("Input File Paths");
        }
        String[] ranOnArr = ranOnString.split(",");
        ranOnList = new ArrayList<String>();
        for (Integer i:indices) {
            ranOnList.add(ranOnArr[i].trim());
            Log.trace("Adding item: " + ranOnArr[i]);
        }
        Log.debug("Got list of files: " + StringUtils.join(ranOnList,','));
        return ranOnList;
    }

    private String do_summary_command() {
        StringBuilder command = new StringBuilder();
        // SEQWARE-1612 Change test command to actual jar name
        String SEQWARE_VERSION = this.metadata.getClass().getPackage().getImplementationVersion();
        command.append("\njava -jar seqware-distribution-").append(SEQWARE_VERSION).append("-full.jar ");
        command.append(spaceSeparateMy(constructCommand()));
        command.append("\n");
        return command.toString();
    }
    
    /**
     * These file statuses reflect the discussion at  
     * https://wiki.oicr.on.ca/display/SEQWARE/BasicDecider+logic     
     */
    protected enum FILE_STATUS{
        /**
         * Two sets of files have no relationship
         */
        DISJOINT_SETS,
        /**
         * Two sets of files partially overlap
         * i.e. intersection and subset (set of files in the past was smaller)
         */
        PAST_SUBSET_OR_INTERSECTION,
        /**
         * the same files are found at the same paths
         */
        SAME_FILES,
        /**
         * The set of files in the past was strictly larger than the current
         * files under consideration
         */
        PAST_SUPERSET
    }
    
    /**
     * We care about three types of status, an outright fail, other (pending, running, submitted, etc.), and completed
     */
    protected enum PREVIOUS_RUN_STATUS{
        FAILED,
        OTHER,
        COMPLETED
    }
}
