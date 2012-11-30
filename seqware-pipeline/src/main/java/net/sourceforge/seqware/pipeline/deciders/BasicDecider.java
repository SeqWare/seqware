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
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.seqware.common.hibernate.FindAllTheFiles;
import net.sourceforge.seqware.common.hibernate.FindAllTheFiles.Header;
import net.sourceforge.seqware.common.metadata.MetadataDB;
import net.sourceforge.seqware.common.metadata.MetadataWS;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.decider.DeciderInterface;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.pipeline.workflow.Workflow;
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
    private ArrayList<String> iniFiles;
    private Boolean runNow = null;
    private Boolean skipStuff = null;
    private int launchMax = Integer.MAX_VALUE, launched = 0;
    private int rerunMax = 5;

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
        parser.acceptsAll(Arrays.asList("rerun-max"), "The maximum time to re-launch a workflowrun if failed. Default: 5.").withRequiredArg();
        ret.setExitStatus(ReturnValue.SUCCESS);
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
            Log.error("Must specify the workflow-acccession of the workflow to run");
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
            workflowAccessionsToCheck.add(workflowAccession);
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


        if (runNow == null) {
            if (options.has("schedule")) {
                runNow = false;
            } else {
                runNow = true;
            }
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
                rv = metadata.findFilesAssociatedWithAStudy(name);
                mappedFiles = separateFiles(rv, groupBy);
                launchWorkflows(mappedFiles);
                if (ret.getExitStatus() != ReturnValue.SUCCESS) {
                    break;
                }
            }
            return ret;
        } else if (options.has("study-name")) {
            String studyName = (String) options.valueOf("study-name");
            vals = metaws.findFilesAssociatedWithAStudy(studyName);
        } else if (options.has("sample-name")) {
            String sampleName = (String) options.valueOf("sample-name");
            vals = metaws.findFilesAssociatedWithASample(sampleName);
        } else if (options.has("sequencer-run-name")) {
            String runName = (String) options.valueOf("sequencer-run-name");
            vals = metaws.findFilesAssociatedWithASequencerRun(runName);
        } else {
            Log.error("Unknown option");
        }

        mappedFiles = separateFiles(vals, groupBy);
        ret = launchWorkflows(mappedFiles);
        return ret;
    }

    private ReturnValue launchWorkflows(Map<String, List<ReturnValue>> mappedFiles) {
        if (mappedFiles != null) {

            for (String key : mappedFiles.keySet()) {

                parentAccessionsToRun = new HashSet<String>();
                filesToRun = new HashSet<String>();
                workflowParentAccessionsToRun = new HashSet<String>();


                List<String> previousWorkflowRuns = new ArrayList<String>();

                //for each grouping (e.g. sample), iterate through the files
                List<ReturnValue> files = mappedFiles.get(key);
                for (ReturnValue file : files) {
                    String wfAcc = file.getAttribute(Header.WORKFLOW_SWA.getTitle());
//                    Log.debug(Header.WORKFLOW_SWA.getTitle() + ": WF accession is " + wfAcc);
                    // this sample has been run before
                    if (wfAcc != null && workflowAccessionsToCheck.contains(wfAcc)) {
                        previousWorkflowRuns.add(file.getAttribute(Header.WORKFLOW_RUN_SWA.getTitle()));
                    }

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
                    String parentAccessionString = commaSeparateMy(parentAccessionsToRun);
                    String fileString = commaSeparateMy(filesToRun);
                    Log.debug("FileString: " + fileString);
                    //check if this workflow has been run before
                    boolean rerun = rerunWorkflowRun(previousWorkflowRuns, filesToRun);



                    iniFiles = new ArrayList<String>();
                    iniFiles.add(createIniFile(fileString, parentAccessionString));


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
                    if (test || !rerun) {
                        //don't run, but report it
                        Log.debug("NOT RUNNING. test=" + test + " or !rerun=" + !rerun);
                        ret = do_summary();
                    } else if (launched++ < launchMax) {
                        //construct the INI and run it
                        Log.debug("RUNNING");
                        // setup workflow object
                        Workflow w = new Workflow(metadata, config);
                        if (runNow) {
                            ret = w.launchInstalledBundle(workflowAccession, null,
                                    iniFiles, metadataWriteback, new ArrayList(parentAccessionsToRun),
                                    new ArrayList(workflowParentAccessionsToRun), false, options.nonOptionArguments());
                        } else {
                            ret = w.scheduleInstalledBundle(workflowAccession, null,
                                    iniFiles, metadataWriteback, new ArrayList(parentAccessionsToRun),
                                    new ArrayList(workflowParentAccessionsToRun), false, options.nonOptionArguments());
                        }
                    } else {
                        Log.info("The maximum number of jobs has been launched. The next jobs will be launched when the decider runs again.");
                        ret.setExitStatus(ReturnValue.QUEUED);
                    }
                } else {
                    Log.debug("Why are we here, seriously!?");
                }

            }
        } else {
            Log.stdout("There are no files");
        }
        return ret;
    }

    /**
     * Returns true only if there are more files to run than have been run on
     * any workflow so far, or if the filesToRun have different filepaths than
     * those that have been run before.
     */
    public boolean rerunWorkflowRun(Collection<String> previousWorkflowRuns, Collection<String> filesToRun) {
        boolean rerun = true;
        int numberFailed=0;
        for (String workflowRunAcc : previousWorkflowRuns) {
            String report = metaws.getWorkflowRunReport(Integer.parseInt(workflowRunAcc));
            String[] lines = report.split("\n");
            String[] reportHeader = lines[0].split("\t");
            String[] data = lines[1].split("\t");
            Map<String, String> map = new TreeMap<String, String>();
            for (int i = 0; i < reportHeader.length; i++) {
                map.put(reportHeader[i].trim(), data[i].trim());
            }

            String ranOn = map.get("Input File Paths");
            String[] ranOnFiles = ranOn.split(",");
            if (!compareWorkflowRunFiles(ranOnFiles, filesToRun)) {
                Log.error("There are fewer files to run on in the database than were previously run on this sample!");
                Log.error("Files found to run in database: " + filesToRun.size());
                Log.error("But yet workflow run " + workflowRunAcc + " ran on " + ranOnFiles.length);
                rerun = false;
            }
            String status =map.get("Workflow Run Status");
            if (!"failed".equals(status)) {
                Log.debug("The status is " + status + " so not running this workflow");
                rerun=false;
            } else {
                numberFailed++;
            }            
        }
        
        if (numberFailed >= this.rerunMax) {
            Log.debug("This workflow has failed " + rerunMax + " times: not running");
            rerun=false;
        }
        return rerun;
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
    public boolean compareWorkflowRunFiles(String[] ranOnFiles, Collection<String> filesToRun) {
        boolean rerun;

        if (ranOnFiles.length < filesToRun.size()) {
            rerun = true;
        } else if (ranOnFiles.length == filesToRun.size()) {
            rerun = false;
            for (String file : ranOnFiles) {
                //checks to see if the files in filesToRun are different from those it has ran on previously
                if (!filesToRun.contains(file)) {
                    rerun = true;
                }
            }
        } else {
            rerun = false;
        }




        return rerun;
    }

    private void addFileToSets(ReturnValue file, FileMetadata fm, Collection<String> workflowParentAccessionsToRun,
            Collection<String> parentAccessionsToRun, Collection<String> filesToRun) {
        if (checkFileDetails(file, fm)) {
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
            Log.stdout(writer.getBuffer().toString().trim());
        } catch (IOException ex) {
            Log.error("Error printing file metadata", ex);
        }
    }

    protected String commaSeparateMy(Collection<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            if (sb.length() != 0) {
                sb.append(",");
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
        if (skipStuff) {
            for (String key : returnValue.getAttributes().keySet()) {
                if (key.contains("skip")) {
                    Log.warn("File SWID:" + fm.getDescription() + " path " + fm.getFilePath() + " is skipped: " + key + ">" + returnValue.getAttribute(key));
                    return false;
                }
            }
        }
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
            String currVal = r.getAttributes().get(groupBy);

            currVal = handleGroupByAttribute(currVal);

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
        StringBuilder command = new StringBuilder();
        command.append("\njava -jar seqware-pipeline-full.jar -p net.sourceforge.seqware.pipeline.plugins.WorkflowLauncher --").append(" ");
        command.append("--workflow-accession").append(" ").append(workflowAccession).append(" ");
        command.append("--ini-files").append(" ").append(commaSeparateMy(iniFiles)).append(" ");
        if (!metadataWriteback) {
            command.append("--no-metadata").append(" ");
        }
        command.append("--parent-accessions").append(" ").append(commaSeparateMy(parentAccessionsToRun)).append(" ");
        command.append("--link-workflow-run-to-parents").append(" ").append(commaSeparateMy(workflowParentAccessionsToRun)).append(" ");
        command.append(options.nonOptionArguments());
        command.append("\n");

        Log.stdout(command.toString());

        return ret;
    }

    @Override
    public String get_description() {
        String description = super.get_description();
        return description;
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
        ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);
        return ret;
    }

    /**
     * check all the return value, if one of the status is not failed, then
     * skip. if all failed and the number of failed less than rerunMax, return
     * true
     *
     * @param swAcc
     * @param files
     * @return
     */
    private boolean isProcessingOrMultipleFailed(List<ReturnValue> files) {

        return false;
    }
}
