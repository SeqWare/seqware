/*
 * Copyright (C) 2011 SeqWare
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
package net.sourceforge.seqware.pipeline.plugins;

import java.io.File;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.seqware.common.model.*;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.runtools.ConsoleAdapter;
import net.sourceforge.seqware.pipeline.modules.GenericMetadataSaver;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;

import org.openide.util.lookup.ServiceProvider;

/**
 * This plugin currently lets users creation a limited set of table rows in the
 * database from the command line. In the future we should expand this tool to
 * make it both more generic and to increase the number of tables that can be
 * added to. Here's a list of TODO items we should add at some point: * TODO:
 * ability to update rows in addition to creating and listing them * FIXME:
 * better support for lookup tables rather than just hard-coding
 *
 * @author Brian O'Connor <briandoconnor@gmail.com>
 * @version $Id: $Id
 */
@ServiceProvider(service = PluginInterface.class)
public class Metadata extends Plugin {

    ReturnValue ret = new ReturnValue();
    BufferedWriter bw = null;
    /**
     * Generic HashMap is extended here to not add keys when they are null.
     */
    HashMap<String, String> fields = new HashMap<String, String>() {

        @Override
        public String put(String key, String value) {
            if (value != null) {
                return super.put(key, value);
            } else {
                return null;
            }
        }
    };
    protected boolean interactive = false;
    // list of files
    ArrayList<FileMetadata> files = new ArrayList<FileMetadata>();

    /**
     * <p>Constructor for Metadata.</p>
     */
    public Metadata() {
        super();
        parser.acceptsAll(Arrays.asList("list-tables", "lt"), "Optional: if provided will list out the tables this tools knows how to read and/or write to.");
        parser.acceptsAll(Arrays.asList("table", "t"), "Required: the table you are interested in reading or writing.").withRequiredArg();
        //parser.acceptsAll(Arrays.asList("list", "l"), "Optional: if provided will list out the table rows currently in the MetaDB your settings point to.");
        parser.acceptsAll(Arrays.asList("output-file", "of"), "Optional: if provided along with the --list or --list-tables options this will cause the output list of rows/tables to be written to the file specified rather than stdout.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("list-fields", "lf"), "Optional: if provided along with the --table option this will list out the fields for that table and their type.");
        parser.acceptsAll(Arrays.asList("field", "f"), "Optional: the field you are interested in writing. This is encoded as '<field_name>::<value>', you should use single quotes when the value includes spaces. You supply multiple --field arguments for a given table insert.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("file"), "Optional: one file option can be specified when you create a file, one or more --file options can be specified when you create a workflow_run. This is encoded as '<algorithm>::<file-meta-type>::<file-path>', you should use single quotes when the value includes spaces.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("parent-accession"), "Optional: one or more --parent-accession options can be specified when you create a workflow_run.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("create", "c"), "Optional: indicates you want to create a new row, must supply --table and all the required --field params.");
        parser.accepts("interactive", "Optional: Interactively prompt for fields during creation");
        ret.setExitStatus(ReturnValue.SUCCESS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue init() {
        interactive = options.has("interactive");
        parseFiles();
        return parseFields();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue do_test() {
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue do_run() {

        // setup output file
        if (options.has("output-file")) {
            try {
                bw = new BufferedWriter(new FileWriter(new File((String) options.valueOf("output-file"))));
            } catch (IOException ex) {
                bw = null;
                Log.error(null, ex);
            }
        }

        // parse any fields into hash

        if (options.has("list-tables")) {

            print("TableName\n");
            for (String table : new String[]{"study", "experiment", "sample", "sequencer_run", "ius", "lane", "workflow", "workflow_run"}) {
                print(table + "\n");
            }
        } else if (options.has("table") && options.has("list")) {
            // list the table's contents
        } else if (options.has("table") && options.has("list-fields")) {
            // list the fields for this table
            ret = (listFields((String) options.valueOf("table")));
            return ret;
        } else if (options.has("table") && options.has("create") && 
                (options.has("field") || options.has("interactive"))) {

            // create a row with these fields
            if ("study".equals((String) options.valueOf("table"))) {
                ret = addStudy();
                return ret;

            } else if ("experiment".equals((String) options.valueOf("table"))) {
                ret = addExperiment();
                return ret;

            } else if ("sample".equals((String) options.valueOf("table"))) {
                ret = addSample();
                return ret;

            } else if ("sequencer_run".equals((String) options.valueOf("table"))) {
                ret = addSequencerRun();
                return ret;

            } else if ("lane".equals((String) options.valueOf("table"))) {
                ret = addLane();
                return ret;

            } else if ("ius".equals((String) options.valueOf("table"))) {
                ret = addIUS();
                return ret;

            } else if ("workflow".equals((String) options.valueOf("table"))) {
                ret = addWorkflow();
                return ret;

            }  else if ("workflow_run".equals((String) options.valueOf("table"))) {
                ret = addWorkflowRun();
                return ret;
            } else if ("file".equals((String) options.valueOf("table"))) {
                ret = addFile();
                return ret;
            } else {
                Log.error("This tool does not know how to save to the " + options.valueOf("table") + " table.");
            }

        } else {
            println("Combination of parameters not recognized!");
            println(this.get_syntax());
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        }
        return ret;
    }

    /**
     * list the fields available to set
     */
    protected ReturnValue listFields(String table) {
        final String fileDescription = "\nThis takes one file encoded as --file type::file-meta-type::file-path[::description] \n";
        ReturnValue rv = new ReturnValue(ReturnValue.SUCCESS);
        if ("study".equals(table)) {
            List<StudyType> studyTypes = this.metadata.getStudyTypes();
            print("Field\tType\tPossible_Values\ntitle\tString\ndescription\tString\ncenter_name\tString\ncenter_project_name\tString\nstudy_type\tInteger\t[");
            for (StudyType st : studyTypes) {
                print(st.getStudyTypeId() + ": " + st.getName() + ", ");
            }
            //"1: Whole Genome Sequencing, 2: Metagenomics, 3: Transcriptome Analysis, 4: Resequencing, 5: Epigenetics, 6: Synthetic Genomics, 7: Forensic or Paleo-genomics, 8: Gene Regulation Study, 9: Cancer Genomics, 10: Population Genomics, 11: Other"
            print("]\n");
        } else if ("experiment".equals(table)) {
            print("Field\tType\tPossible_Values\ntitle\tString\ndescription\tString\nstudy_accession\tInteger\nplatform_id\tInteger\t[");
            List<Platform> platforms = this.metadata.getPlatforms();
            for (Platform obj : platforms) {
                print(obj.getPlatformId() + ": " + obj.getName() + " " + obj.getInstrumentModel() + ", ");
            }
            print("]\n");
            print("experiment_library_design_id\tInteger\t[");
            List<ExperimentLibraryDesign> elds = this.metadata.getExperimentLibraryDesigns();
            for (ExperimentLibraryDesign obj : elds) {
                print(obj.getExperimentLibraryDesignId()+ ": " + obj.getName() + ", ");
            }
            print("]\n");
            print("experiment_spot_design_id\tInteger\t[");
            List<ExperimentSpotDesign> esds = this.metadata.getExperimentSpotDesigns();
            for (ExperimentSpotDesign obj : esds) {
                print(obj.getExperimentSpotDesignId()+ ": " + obj.getReadSpec()+ ", ");
            }
            print("]\n");
        } else if ("sample".equals(table)) {
            print("Field\tType\tPossible_Values\ntitle\tString\ndescription\tString\nexperiment_accession\tInteger\nparent_sample_accession\tInteger\norganism_id\tInteger\t[\n");
            List<Organism> objs = this.metadata.getOrganisms();
            for (Organism obj : objs) {
                print(obj.getOrganismId() + ": " + obj.getName() + ", ");
            }
            print("]\n");
        } else if ("sequencer_run".equals(table)) {
            print("Field\tType\tPossible_Values\nname\tString\ndescription\tString\npaired_end\tBoolean\t[true, false]\nskip\tBoolean\t[true, false]\nfile_path\tString\nstatus\tString\nplatform_accession\tInteger\t[");
            List<Platform> platforms = this.metadata.getPlatforms();
            for (Platform obj : platforms) {
                print(obj.getPlatformId() + ": " + obj.getName() + " " + obj.getInstrumentModel() + ", ");
            }
            print("]\n");
        } else if ("lane".equals(table)) {
            print("Field\tType\tPossible_Values\nname\tString\ndescription\tString\ncycle_descriptor\tString\t[e.g. {F*120}{..}{R*120}]\nskip\tBoolean\t[true, false]\nsequencer_run_accession\tInteger\nlane_number\tInteger\nstudy_type_accession\tInteger\t[");
            List<Platform> platforms = this.metadata.getPlatforms();
            for (Platform obj : platforms) {
                print(obj.getPlatformId() + ": " + obj.getName() + " " + obj.getInstrumentModel() + ", ");
            }
            print("]\nlibrary_strategy_accession\tInteger\t[");
            List<LibraryStrategy> objs = this.metadata.getLibraryStrategies();
            for (LibraryStrategy obj : objs) {
                print(obj.getLibraryStrategyId() + ": " + obj.getName() + " " + obj.getDescription() + ", ");
            }
            print("]\nlibrary_selection_accession\tInteger\t[");
            List<LibrarySelection> libSelections = this.metadata.getLibrarySelections();
            for (LibrarySelection obj : libSelections) {
                print(obj.getLibrarySelectionId() + ": " + obj.getName() + " " + obj.getDescription() + ", ");
            }
            print("]\nlibrary_source_accession\tInteger\t[");
            List<LibrarySource> libSources = this.metadata.getLibrarySource();
            for (LibrarySource obj : libSources) {
                print(obj.getLibrarySourceId() + ": " + obj.getName() + " " + obj.getDescription() + ", ");
            }
            print("]\n");
        } else if ("ius".equals(table)) {
            print("Field\tType\tPossible_Values\nname\tString\ndescription\tString\nbarcode\tString\nskip\tBoolean\t[true, false]\nsample_accession\tInteger\nlane_accession\tInteger\n");

        } else if ("workflow".equals(table)) {
            print("Field\tType\tPossible_Values\nname\tString\nversion\tString\ndescription\tString\n");
        }  else if ("workflow_run".equals(table)) {
            print("Field\tType\tPossible_Values\nworkflow_accession\tInteger\nstatus\tString\t[completed, failed]\nstdout\tString\nstderr\tString\n");
            print(fileDescription);
            print("\nThis also takes one or more --parent-accession options.\n");
            print("\nThis command will result in one workflow_run, one processing tied to the parents specified, and n files attached to that processing event.\n");
        }  else if ("file".equals(table)) {
            print("Field\tType\tPossible_Values\nalgorithm\tString\n"); 
            print(fileDescription);
            print("\nThis also takes one or more --parent-accession options.\n");
        } else {
            Log.error("This tool does not know how to list the fields for the " + table + " table.");
        }
        return (rv);
    }

     /**
     *
     * @return ReturnValue
     */
    protected ReturnValue addWorkflow() {
        String[] necessaryFields = {"name", "version", "description"};
        if (interactive) {
            print("Unfortunately interactive mode is not supported for adding workflows.");
        }
        // check to make sure we have what we need
        ReturnValue localRet = new ReturnValue(ReturnValue.SUCCESS);
        if (checkFields(necessaryFields)) {
            // create a new workflow!
            localRet = metadata.addWorkflow(fields.get("name"), fields.get("version"), fields.get("description"), null, null, null, null, false, null, false, null, null, null);
            print("SWID: " + localRet.getAttribute("sw_accession"));
        } else {
            Log.error("You need to supply name, version, and description for the workflow table.");
            localRet.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        }
        return (localRet);
    }

    /**
     *
     * @return ReturnValue
     */
    protected ReturnValue addWorkflowRun() {
        String[] necessaryFields = {"workflow_accession", "status"};
        if (interactive) {
            print("Unfortunately interactive mode is not supported for adding workflow runs and files.");
        }
        // check to make sure we have what we need
        ReturnValue localRet = new ReturnValue(ReturnValue.SUCCESS);
        if (checkFields(necessaryFields)) {
            // parent accessions
            int[] parents = parseParentAccessions();
            // if we have parent accessions, check that they're valid right up front
            if(metadata.getViaParentAccessions(parents).contains(null)) {
                    Log.error("parent accession invalid.");
                    localRet.setExitStatus(ReturnValue.INVALIDPARAMETERS);
                    return localRet;
            }


            // create a new workflow!
            int workflowRunId = metadata.add_workflow_run(Integer.parseInt(fields.get("workflow_accession")));
            if (workflowRunId == 0){
                Log.error("Workflow_accession invalid.");
                localRet.setExitStatus(ReturnValue.INVALIDPARAMETERS);
                return localRet;
            }
            int workflowRunAccession = metadata.get_workflow_run_accession(workflowRunId);
            print("SWID: " + workflowRunAccession);

            // create and update processing
            ReturnValue metaret = metadata.add_empty_processing_event_by_parent_accession(parents);
            if (metaret.getExitStatus() != ReturnValue.SUCCESS) {
                Log.error("Parent_accessions invalid.");
                localRet.setExitStatus(ReturnValue.INVALIDPARAMETERS);
                return localRet;
            }
            int processingId = metaret.getReturnValue();
            ReturnValue newRet = new ReturnValue();
            newRet.setFiles(this.files);
            //newRet.setExitStatus(fields.get("status"));

            metadata.update_processing_event(processingId, newRet);

            // LEFT OFF WITH: need to link process with workflow_run
            metadata.update_processing_workflow_run(processingId, workflowRunAccession);
            //SEQWARE-1692 - need to update workflow with the status
            WorkflowRun wr = metadata.getWorkflowRun(workflowRunAccession);
            String statusField = fields.get("status");
            WorkflowRunStatus status = statusField == null ? null : WorkflowRunStatus.valueOf(statusField);
            wr.setStatus(status);
            wr.setStdOut(fields.get("stdout"));
            wr.setStdErr(fields.get("stderr"));
            metadata.update_workflow_run(wr.getWorkflowRunId(), wr.getCommand(), wr.getTemplate(), wr.getStatus(),
                    wr.getStatusCmd(), wr.getCurrentWorkingDir(), wr.getDax(), wr.getIniFile(),
                    wr.getHost(), wr.getStdErr(), wr.getStdOut(), wr.getWorkflowEngine(), wr.getInputFileAccessions());

        } else {
            Log.error("You need to supply workflow_accession and status for the workflow_run table.");
            localRet.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        }

        return (localRet);
    }
    
    /**
     *
     * @return ReturnValue
     */
    protected ReturnValue addFile() {
        String[] necessaryFields = {"algorithm"};
        if (interactive) {
            print("Unfortunately interactive mode is not supported for adding files.");
        }
        // check to make sure we have what we need
        ReturnValue localRet = new ReturnValue(ReturnValue.SUCCESS);
        if (checkFields(necessaryFields)) {
            // parent accessions
            int[] parents = parseParentAccessions();
            // if we have parent accessions, check that they're valid right up front
            if (metadata.getViaParentAccessions(parents).contains(null)) {
                Log.error("parent accession invalid.");
                localRet.setExitStatus(ReturnValue.INVALIDPARAMETERS);
                return localRet;
            }
            if (this.files.size() != 1) {
                Log.error("incorrect number of files.");
                localRet.setExitStatus(ReturnValue.INVALIDPARAMETERS);
                return localRet;
            }
            // create a new file!
            ReturnValue processingEventRet = metadata.add_empty_processing_event_by_parent_accession(parents);
            if (processingEventRet.getExitStatus() != ReturnValue.SUCCESS) {
                Log.error("Error creating processing event.");
                localRet.setExitStatus(ReturnValue.FAILURE);
                return localRet;
            }
            int procID = processingEventRet.getReturnValue();

            ReturnValue newRet = new ReturnValue();
            newRet.setFiles(this.files);
            newRet.setAlgorithm(fields.get("algorithm"));
            // send up the files via ReturnValue (ewww)
            metadata.update_processing_event(procID, newRet);
            int mapProcessingIdToAccession = metadata.mapProcessingIdToAccession(procID);
            print("SWID: " + mapProcessingIdToAccession);

        } else {
            Log.error("You need to supply workflow_accession and status for the workflow_run table.");
            localRet.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        }

        return (localRet);
    }
    
    /**
     *
     * @return ReturnValue
     */
    protected ReturnValue addStudy() {
        String[] necessaryFields = {"title", "description", "center_name", "center_project_name", "study_type"};
        if (interactive) {
            promptForStudy(necessaryFields);
        }
        // check to make sure we have what we need
        ReturnValue localRet = new ReturnValue(ReturnValue.SUCCESS);
        if (checkFields(necessaryFields)) {
            // create a new study!
            localRet = metadata.addStudy(fields.get("title"), fields.get("description"), fields.get("center_name"), fields.get("center_project_name"),
                    Integer.parseInt(fields.get("study_type")));
            if (localRet.getReturnValue() == ReturnValue.INVALIDPARAMETERS){
                print ("Invalid parameters, please check your id values");
                return localRet;
            }
            print("SWID: " + localRet.getAttribute("sw_accession"));
        } else {
            printErrorMessage(necessaryFields, null);
            //Log.error("You need to supply title, description, accession, center_name, and center_project_name for the study table along with an integer for study_type [1: Whole Genome Sequencing, 2: Metagenomics, 3: Transcriptome Analysis, 4: Resequencing, 5: Epigenetics, 6: Synthetic Genomics, 7: Forensic or Paleo-genomics, 8: Gene Regulation Study, 9: Cancer Genomics, 10: Population Genomics, 11: Other]. Alternatively, enable --interactive mode.");
            localRet.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        }
        return (localRet);
    }

    /**
     *
     * @return ReturnValue
     */
    protected ReturnValue addExperiment() {
        String[] necessaryFields = {"study_accession", "platform_id", "title", "description"};
        final String experiment_library_design_id = "experiment_library_design_id";
        final String experiment_spot_design_id = "experiment_spot_design_id";
        String[] optionalFields = {experiment_library_design_id, experiment_spot_design_id};
        // check to make sure we have what we need
        ReturnValue localRet = new ReturnValue(ReturnValue.SUCCESS);
        if (interactive) {
            promptForExperiment(necessaryFields);
        }
        
         for (String s: optionalFields){
            if (fields.get(s) == null){
                fields.put(s, null);
            }
         }
        
        if (checkFields(necessaryFields) && nullOrLessThanZero(experiment_library_design_id) && nullOrLessThanZero(experiment_spot_design_id)){
            // check for valid platform id
            final int platformId = Integer.parseInt(fields.get("platform_id"));
            // create a new experiment
            localRet = metadata.addExperiment(Integer.parseInt(fields.get("study_accession")), platformId, fields.get("description"), fields.get("title"), parseNullOrInteger(experiment_library_design_id), parseNullOrInteger(experiment_spot_design_id));
            if (localRet.getReturnValue() == ReturnValue.INVALIDPARAMETERS){
                print ("Invalid parameters, please check your id values");
                return localRet;
            }
            print("SWID: " + localRet.getAttribute("sw_accession"));

        } else {
            printErrorMessage(necessaryFields, null);
            //Log.error("You need to supply study_accession (reported if you create a study using this tool), title, and description for the experiment table along with an integer for platform_id [9: Illumina Genome Analyzer II, 20: Illumina HiSeq 2000, 26: Illumina MiSeq]. Alternatively, enable --interactive mode.");
            localRet.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        }
        return (localRet);
    }

    /**
     *
     * @return ReturnValue
     */
    protected ReturnValue addSample() {
        String[] necessaryFields = {"organism_id", "title", "description"};
        String[] optionalFields = {"experiment_accession", "parent_sample_accession"};
        // check to make sure we have what we need
        ReturnValue localRet = new ReturnValue(ReturnValue.SUCCESS);

        if (interactive) {
            promptForSample(necessaryFields);
        }

        for (String s: optionalFields){
            if (fields.get(s) == null){
                fields.put(s, "0");
            }
        }
        
        if (checkFields(necessaryFields)
                && (notNullOrLessThanZero("experiment_accession") || notNullOrLessThanZero("parent_sample_accession"))) {
            // check for valid organism id (accession is blank and should not confuse with sw_accession)
            final int organismId = Integer.parseInt(fields.get("organism_id"));
            // create a new sample 
            localRet = metadata.addSample(Integer.parseInt(fields.get("experiment_accession")),
                    Integer.parseInt(fields.get("parent_sample_accession")), organismId,
                    fields.get("description"), fields.get("title"));
            if (localRet.getReturnValue() == ReturnValue.INVALIDPARAMETERS){
                print ("Invalid parameters, please check your id values");
                return localRet;
            }
            print("SWID: " + localRet.getAttribute("sw_accession"));

        } else {
            printErrorMessage(necessaryFields, optionalFields);
            //Log.error("You need to supply experiment_accession (reported if you create an experiment using this tool), title, and description for the sample table along with an integer for organism_id [31: Homo sapiens]. Alternatively, enable --interactive mode.");
            localRet.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        }
        return localRet;
    }
    
    
    private Integer parseNullOrInteger(String fieldName) {
        String field = fields.get(fieldName);
        if (field == null) {
           return null;
        } else {
           return Integer.valueOf(field);
        }
    }
    
    private boolean nullOrLessThanZero(String fieldName) {
        boolean usable = true;
        String field = fields.get(fieldName);
        if (field == null) {
            usable = true;
        } else if (Integer.parseInt(field) <= 0) {
            usable = false;
        }
        return usable;
    }
    
    private boolean notNullOrLessThanZero(String fieldName) {
        boolean usable = true;
        String field = fields.get(fieldName);
        if (field == null) {
            usable = false;
        } else if (Integer.parseInt(field) <= 0) {
            usable = false;
        }
        return usable;
    }

    /**
     *
     * @return ReturnValue
     *
     */
    protected ReturnValue addSequencerRun() {
        String[] necessaryFields = {"platform_accession", "name", "description", "paired_end", "skip", "file_path"};
        String[] optionalFields = {"status"};
        // check to make sure we have what we need
        ReturnValue localRet = new ReturnValue(ReturnValue.SUCCESS);
        if (interactive) {
            promptForSequencerRun(necessaryFields);
        }
        
        for (String s: optionalFields){
            if (fields.get(s) == null){
                fields.put(s, null);
            }
        }

        if (checkFields(necessaryFields)) {
            // create a new experiment
            localRet = metadata.addSequencerRun(Integer.parseInt(fields.get("platform_accession")), 
                    fields.get("name"), fields.get("description"), "true".equalsIgnoreCase(fields.get("paired_end")),
                    "true".equalsIgnoreCase(fields.get("skip")), fields.get("file_path"),
                    fields.get("status") == null ? null : SequencerRunStatus.valueOf(fields.get("status")));

            print("SWID: " + localRet.getAttribute("sw_accession"));

        } else {
            printErrorMessage(necessaryFields, optionalFields);
            //Log.error("You need to supply name, description, platform_accession [see platform lookup], the complete file path of the run, and 'true' or 'false' for paired_end and skip. Alternatively, enable --interactive mode.");
            localRet.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        }
        return (localRet);
    }

    protected ReturnValue addLane() {
        String[] necessaryFields = {"sequencer_run_accession", "study_type_accession",
            "library_strategy_accession", "library_selection_accession", "library_source_accession",
            "name", "description", "cycle_descriptor", "lane_number", "skip"};
        // check to make sure we have what we need
        ReturnValue localRet = new ReturnValue(ReturnValue.SUCCESS);

        if (interactive) {
            promptForLane(necessaryFields);
        }

        if (checkFields(necessaryFields)) {
            // note, study type has no accession in the database
            final int studyTypeId = Integer.parseInt(fields.get("study_type_accession"));
            // create a new experiment
            localRet = metadata.addLane(Integer.parseInt(fields.get("sequencer_run_accession")), studyTypeId, Integer.parseInt(fields.get("library_strategy_accession")), Integer.parseInt(fields.get("library_selection_accession")), Integer.parseInt(fields.get("library_source_accession")), fields.get("name"), fields.get("description"), fields.get("cycle_descriptor"), "true".equalsIgnoreCase(fields.get("skip")), Integer.parseInt(fields.get("lane_number")));
            if (localRet.getReturnValue() == ReturnValue.INVALIDPARAMETERS){
                print ("Invalid parameters, please check your id values");
                return localRet;
            }
            print("SWID: " + localRet.getAttribute("sw_accession"));

        } else {
            printErrorMessage(necessaryFields, null);
            //Log.error("You need to supply name, description, cycle_descriptor [e.g. {F*120}{..}{R*120}], sequencer_run_accession, study_type_accession, library_strategy_accession, library_selection_accession, library_source_accession and 'true' or 'false' for skip. Alternatively, enable --interactive mode.");
            localRet.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        }
        return localRet;
    }

    protected ReturnValue addIUS() {
        String[] necessaryFields = {"lane_accession", "sample_accession", "name", "description", "skip", "barcode"};
        // check to make sure we have what we need
        ReturnValue localRet = new ReturnValue(ReturnValue.SUCCESS);

        if (interactive) {
            promptForIUS(necessaryFields);
        }

        //Integer laneId, Integer sampleId, String name, String description, String cycleDescriptor, boolean skip
        if (checkFields(necessaryFields)) {
            //allow barcode to be empty

            // create a new experiment
            localRet = metadata.addIUS(Integer.parseInt(fields.get("lane_accession")), Integer.parseInt(fields.get("sample_accession")), fields.get("name"), fields.get("description"), fields.get("barcode"), "true".equalsIgnoreCase(fields.get("skip")));
            if (localRet.getReturnValue() == ReturnValue.INVALIDPARAMETERS){
                print ("Invalid parameters, please check your id values");
                return localRet;
            }
            print("SWID: " + localRet.getAttribute("sw_accession"));

        } else {
            printErrorMessage(necessaryFields, null);
            //Log.error("You need to supply name, description, lane_accession, sample_accession, barcode and 'true' or 'false' for skip. Alternatively, enable --interactive mode.");
            localRet.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        }
        return (localRet);
    }

    private void printErrorMessage(String[] requiredFields, String[] optionalFields) {
        StringBuilder sb = new StringBuilder();
        sb.append("Insufficient fields supplied.");
        sb.append("\nRequired fields:");
        for (String s : requiredFields) {
            sb.append("\n\t").append(s);
        }
        if (optionalFields!=null) {
            sb.append("\nOptional fields:");
            for (String s: optionalFields) {
                sb.append("\n\t").append(s);
            }
        }
        sb.append("\nFor more information about these options, run with the --list-fields switch.");
        sb.append("\nAlternatively, enable --interactive mode.");
        Log.error(sb.toString());
        Log.stdout("You supplied:");
        printFields();
    }
   

    /**
     * {@inheritDoc}
     */
    @Override
    public void print(String string) {
        if (bw != null) {
            try {
                bw.write(string);
            } catch (IOException ex) {
                Log.error(null, ex);
            }
        } else {
            System.out.println(string);
        }
    }

    /**
     *
     * @return ReturnValue
     */
    protected ReturnValue parseFields() {
        ReturnValue localRet = new ReturnValue(ReturnValue.SUCCESS);
        List<?> valuesOf = options.valuesOf("field");
        for (Object value : valuesOf) {
            String[] t = value.toString().split("::");
            if (t.length == 2) {
                fields.put(t[0], t[1]);
                Log.info("  Field: " + t[0] + " value " + t[1]);
            }
        }
        return (localRet);
    }
    
    protected ReturnValue parseFiles() {
        ReturnValue localRet = new ReturnValue(ReturnValue.SUCCESS);
        List<?> valuesOf = options.valuesOf("file");
        for (Object value : valuesOf) {
            FileMetadata f = Metadata.fileString2FileMetadata(value.toString());
            if (f != null) {
                files.add(f);
            } else {
                print("You need to encode the file as '<type>::<file-meta-type>::<file-path>'\n");
            }
        }
        return (localRet);
    }
    
        
    protected int[] parseParentAccessions() {
      
      List<?> valuesOf = options.valuesOf("parent-accession");
      ArrayList<Integer> parents = new ArrayList<Integer>();
      for (Object value : valuesOf) {
        parents.add(Integer.parseInt(value.toString()));
      }
      int[] localRet = new int[parents.size()];
      for (int i=0; i< localRet.length; i++) {
        localRet[i] = parents.get(i).intValue();
      }
      return(localRet);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue clean_up() {
        this.closeBufferWriter();
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String get_description() {
        return "This plugin lets you list, read, and write to a collection of tables in the underlying MetaDB. "
                + "This makes it easier to automate the creation of entities in the database which can be used as "
                + "parents for file uploads and triggered workflows.";
    }

    protected void closeBufferWriter() {
        try {
            if (this.bw != null) {
                this.bw.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkFields(String[] fs) {
        boolean allPresent = true;
        for (String s : fs) {
            if (!fields.containsKey(s)) {
                allPresent = false;
                Log.debug(s + " <null>");
            } else {
                Log.debug(s + " " + fields.get(s));
            }
        }
        return allPresent;
    }

    private void printFields() {
        for (String s : fields.keySet()) {
            Log.stdout(s + "=" + fields.get(s));
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    ///// Interactive code
    ////////////////////////////////////////////////////////////////////////////
    protected void promptForStudy(String[] necessaryFields) {
        Log.stdout("---Create a study---");
        if (!fields.containsKey("study_type")) {
            System.out.println();
            for (StudyType st : metadata.getStudyTypes()) {
                Log.stdout(st.toString());
            }
            promptInteger("study_type", 4);
        }
        promptForFields(necessaryFields);
        if (!fieldsConfirmed(necessaryFields)) {
            promptForStudy(necessaryFields);
        }
    }

    protected void promptForSample(String[] necessaryFields) {
        Log.stdout("---Create a sample---");

        int checkMe;
        if (!fields.containsKey("experiment_accession")) {
            promptInteger("experiment_accession", 0);
        }
        if (!fields.containsKey("parent_sample_accession")) {
            promptInteger("parent_sample_accession", 0);
        }

        checkMe = Integer.parseInt(fields.get("experiment_accession")) + Integer.parseInt(fields.get("parent_sample_accession"));

        if (!fields.containsKey("organism_id")) {
            System.out.println();
            for (Organism o : metadata.getOrganisms()) {
                Log.stdout(o.toString());
            }
            promptInteger("organism_id", 31);
        }

        promptForFields(necessaryFields);

        if (!fieldsConfirmed(necessaryFields) || checkMe <= 0) {
            if (checkMe <= 0) {
                Log.stdout("You must provide experiment_accession and/or parent_sample_accession.");
            }
            promptForSample(necessaryFields);
        }
    }

    protected void promptForSequencerRun(String[] necessaryFields) {
        Log.stdout("---Create a sequencer run---");
        if (!fields.containsKey("platform_accession")) {
            System.out.println();
            for (Platform p : metadata.getPlatforms()) {
                Log.stdout(p.toString());
            }
            promptInteger("platform_accession", 20);
        }
        if (!fields.containsKey("paired_end")) {
            promptBoolean("paired_end", true);
        }
        if (!fields.containsKey("skip")) {
            promptBoolean("skip", false);
        }
        promptForFields(necessaryFields);
        if (!fieldsConfirmed(necessaryFields)) {
            promptForSequencerRun(necessaryFields);
        }

    }

    protected void promptForExperiment(String[] necessaryFields) {
        Log.stdout("---Create an experiment---");
        if (!fields.containsKey("platform_id")) {
            System.out.println();
            for (Platform p : metadata.getPlatforms()) {
                Log.stdout(p.toString());
            }
            promptInteger("platform_id", 20);
        }
        promptForFields(necessaryFields);
        if (!fieldsConfirmed(necessaryFields)) {
            promptForExperiment(necessaryFields);
        }

    }

    protected void promptForLane(String[] necessaryFields) {
        Log.stdout("---Create a lane---");
        if (!fields.containsKey("sequencer_run_accession")) {
            promptInteger("sequencer_run_accession", null);
        }
        if (!fields.containsKey("study_type_accession")) {
            System.out.println();
            for (StudyType st : metadata.getStudyTypes()) {
                Log.stdout(st.toString());
            }
            promptInteger("study_type_accession", 4);
        }
        if (!fields.containsKey("library_strategy_accession")) {
            System.out.println();
            for (LibraryStrategy st : metadata.getLibraryStrategies()) {
                Log.stdout(st.toString());
            }
            promptInteger("library_strategy_accession", null);
        }
        if (!fields.containsKey("library_selection_accession")) {
            System.out.println();
            for (LibrarySelection st : metadata.getLibrarySelections()) {
                Log.stdout(st.toString());
            }
            promptInteger("library_selection_accession", null);
        }
        if (!fields.containsKey("library_source_accession")) {
            System.out.println();
            for (LibrarySource st : metadata.getLibrarySource()) {
                Log.stdout(st.toString());
            }
            promptInteger("library_source_accession", null);
        }

        if (!fields.containsKey("lane_number")) {
            promptInteger("lane_number", 1);
        }

        if (!fields.containsKey("skip")) {
            promptBoolean("skip", false);
        }
        promptForFields(necessaryFields);
        if (!fieldsConfirmed(necessaryFields)) {
            promptForLane(necessaryFields);
        }
    }

    protected void promptForIUS(String[] necessaryFields) {
        Log.stdout("---Create a IUS/barcode---");
        if (!fields.containsKey("lane_accession")) {
            promptInteger("lane_accession", null);
        }
        if (!fields.containsKey("sample_accession")) {
            promptInteger("sample_accession", null);
        }
        if (!fields.containsKey("skip")) {
            promptBoolean("skip", false);
        }
        promptForFields(necessaryFields);
        if (!fieldsConfirmed(necessaryFields)) {
            promptForIUS(necessaryFields);
        }
    }

    protected void promptForFields(String[] fs) {
        for (String s : fs) {
            if (!fields.containsKey(s)) {
                promptString(s, null);
            }
        }
    }

    protected String promptString(String string, String deflt) {
        String title = ConsoleAdapter.getInstance().promptString(string, deflt);
        fields.put(string, title);
        return title;
    }

    protected Integer promptInteger(String string, Integer deflt) {
        Integer title = ConsoleAdapter.getInstance().promptInteger(string, deflt);
        fields.put(string, title.toString());
        return title;
    }

    protected Boolean promptBoolean(String string, Boolean deflt) {
        Boolean title = ConsoleAdapter.getInstance().promptBoolean(string, deflt);
        fields.put(string, title.toString());
        return title;
    }

    protected boolean fieldsConfirmed(String[] necessaryFields) {
        for (String s : necessaryFields) {
            Log.stdout(s + " : " + fields.get(s));
        }

        String confirm = ConsoleAdapter.getInstance().readLine("Is this information correct? [y/n] :");
        //System.out.println("result: " + confirm);
        if (confirm.trim().toLowerCase().equals("y") || confirm.trim().toLowerCase().equals("yes") || confirm.trim().isEmpty()) {
            return true;
        } else {
            fields.clear();
            parseFields();
            return false;
        }
    }
    
    public static FileMetadata fileString2FileMetadata(String fileString) {
        FileMetadata fm = null;
        String[] tokens = fileString.split("::");
        if (tokens.length > 0) {
            fm = new FileMetadata();
            fm.setType(tokens[0]);
            if (tokens.length > 1) {
                fm.setMetaType(tokens[1]);
                if (tokens.length > 2) {
                    fm.setFilePath(tokens[2]);
                    if (tokens.length > 3) {
                        fm.setDescription(tokens[3]);
                    }
                }
            }
        }
        return fm;
    }
}
