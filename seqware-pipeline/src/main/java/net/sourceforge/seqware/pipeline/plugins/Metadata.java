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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.seqware.common.model.*;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.runtools.ConsoleAdapter;
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
    HashMap<String, String> fields = new HashMap<String, String>();
    private boolean interactive = false;

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
            for (String table : new String[]{"study", "experiment", "sample", "sequencer_run", "ius", "lane"}) {
                print(table + "\n");
            }
        } else if (options.has("table") && options.has("list")) {
            // list the table's contents
        } else if (options.has("table") && options.has("list-fields")) {
            // list the fields for this table
            ret = (listFields((String) options.valueOf("table")));
            return ret;
        } else if (options.has("table") && options.has("create") && (options.has("field") || options.has("interactive"))) {

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
    private ReturnValue listFields(String table) {
        ReturnValue rv = new ReturnValue(ReturnValue.SUCCESS);
        if ("study".equals(table)) {
            List<StudyType> studyTypes = this.metadata.getStudyTypes();
            print("Field\tType\tPossible_Values\ntitle\tString\ndescription\tString\naccession\tString\ncenter_name\tString\ncenter_project_name\tString\nstudy_type\tInteger\t[");
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
        } else if ("sample".equals(table)) {
            print("Field\tType\tPossible_Values\ntitle\tString\ndescription\tString\nexperiment_accession\tInteger\norganism_id\tInteger\t[\n");
            List<Organism> objs = this.metadata.getOrganisms();
            for (Organism obj : objs) {
                print(obj.getOrganismId() + ": " + obj.getName() + ", ");
            }
            print("]\n");
        } else if ("sequencer_run".equals(table)) {
            print("Field\tType\tPossible_Values\nname\tString\ndescription\tString\npaired_end\tBoolean\t[true, false]\nskip\tBoolean\t[true, false]\nplatform_accession\tInteger\t[");
            List<Platform> platforms = this.metadata.getPlatforms();
            for (Platform obj : platforms) {
                print(obj.getPlatformId() + ": " + obj.getName() + " " + obj.getInstrumentModel() + ", ");
            }
            print("]\n");
        } else if ("lane".equals(table)) {
            print("Field\tType\tPossible_Values\nname\tString\ndescription\tString\ncycle_descriptor\tString\t[e.g. {F*120}{..}{R*120}]\nskip\tBoolean\t[true, false]\nsequencer_run_accession\tInteger\nstudy_type_accession\tInteger\t[");
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

        } else {
            Log.error("This tool does not know how to list the fields for the " + table + " table.");
        }
        return (rv);
    }

    /**
     *
     * @return ReturnValue
     */
    private ReturnValue addStudy() {
        String[] necessaryFields = {"title", "description", "center_name", "center_project_name", "study_type"};
        if (interactive) {
            promptForStudy(necessaryFields);
        }
        // check to make sure we have what we need
        ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);
        if (checkFields(necessaryFields)) {
            // create a new study!
            ret = metadata.addStudy(fields.get("title"), fields.get("description"),
                    null, null, fields.get("center_name"), fields.get("center_project_name"),
                    Integer.parseInt(fields.get("study_type")));
            print("SWID: " + ret.getAttribute("sw_accession"));
        } else {
            Log.error("You need to supply title, description, accession, center_name, and center_project_name for the study table along with an integer for study_type [1: Whole Genome Sequencing, 2: Metagenomics, 3: Transcriptome Analysis, 4: Resequencing, 5: Epigenetics, 6: Synthetic Genomics, 7: Forensic or Paleo-genomics, 8: Gene Regulation Study, 9: Cancer Genomics, 10: Population Genomics, 11: Other]. Alternatively, enable --interactive mode.");
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        }
        return (ret);
    }

    /**
     *
     * @return ReturnValue
     */
    private ReturnValue addExperiment() {
        String[] necessaryFields = {"study_accession", "platform_id", "title", "description"};
        // check to make sure we have what we need
        ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);
        if (interactive) {
            promptForExperiment(necessaryFields);
        }
        if (checkFields(necessaryFields)) {
            // create a new experiment
            ret = metadata.addExperiment(Integer.parseInt(fields.get("study_accession")), Integer.parseInt(fields.get("platform_id")), fields.get("description"), fields.get("title"));
            print("SWID: " + ret.getAttribute("sw_accession"));

        } else {
            Log.error("You need to supply study_accession (reported if you create a study using this tool), title, and description for the experiment table along with an integer for platform_id [9: Illumina Genome Analyzer II, 20: Illumina HiSeq 2000, 26: Illumina MiSeq]. Alternatively, enable --interactive mode.");
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        }
        return (ret);
    }

    /**
     *
     * @return ReturnValue
     */
    private ReturnValue addSample() {
        String[] necessaryFields = {"experiment_accession", "organism_id", "title", "description"};
        // check to make sure we have what we need
        ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);

        if (interactive) {
            promptForSample(necessaryFields);
        }

        if (checkFields(necessaryFields)) {
            // create a new sample
            ret = metadata.addSample(Integer.parseInt(fields.get("experiment_accession")), Integer.parseInt(fields.get("organism_id")), fields.get("description"), fields.get("title"));
            print("SWID: " + ret.getAttribute("sw_accession"));

        } else {
            Log.error("You need to supply experiment_accession (reported if you create an experiment using this tool), title, and description for the sample table along with an integer for organism_id [31: Homo sapiens]. Alternatively, enable --interactive mode.");
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        }
        return (ret);
    }

    /**
     *
     * @return ReturnValue
     *
     */
    private ReturnValue addSequencerRun() {
        String[] necessaryFields = {"platform_accession", "name", "description", "paired_end", "skip"};
        // check to make sure we have what we need
        ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);
        if (interactive) {
            promptForSequencerRun(necessaryFields);
        }

        if (checkFields(necessaryFields)) {
            // create a new experiment
            ret = metadata.addSequencerRun(Integer.parseInt(fields.get("platform_accession")), fields.get("name"), fields.get("description"), "true".equalsIgnoreCase(fields.get("paired_end")), "true".equalsIgnoreCase(fields.get("skip")));

            print("SWID: " + ret.getAttribute("sw_accession"));

        } else {
            Log.error("You need to supply name, description, platform_accession [see platform lookup], and 'true' or 'false' for paired_end and skip. Alternatively, enable --interactive mode.");
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        }
        return (ret);
    }

    private ReturnValue addLane() {
        String[] necessaryFields = {"sequencer_run_accession", "study_type_accession",
            "library_strategy_accession", "library_selection_accession", "library_source_accession",
            "name", "description", "cycle_descriptor", "skip"};
        // check to make sure we have what we need
        ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);

        if (interactive) {
            promptForLane(necessaryFields);
        }

        if (checkFields(necessaryFields)) {
            // create a new experiment
            ret = metadata.addLane(Integer.parseInt(fields.get("sequencer_run_accession")), Integer.parseInt(fields.get("study_type_accession")), Integer.parseInt(fields.get("library_strategy_accession")), Integer.parseInt(fields.get("library_selection_accession")), Integer.parseInt(fields.get("library_source_accession")), fields.get("name"), fields.get("description"), fields.get("cycle_descriptor"), "true".equalsIgnoreCase(fields.get("skip")));

            print("SWID: " + ret.getAttribute("sw_accession"));

        } else {
            Log.error("You need to supply name, description, cycle_descriptor [e.g. {F*120}{..}{R*120}], sequencer_run_accession, study_type_accession, library_strategy_accession, library_selection_accession, library_source_accession and 'true' or 'false' for skip. Alternatively, enable --interactive mode.");
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        }
        return (ret);
    }

    private ReturnValue addIUS() {
        String[] necessaryFields = {"lane_accession", "sample_accession", "name", "description", "skip"};
        // check to make sure we have what we need
        ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);

        if (interactive) {
            promptForIUS(necessaryFields);
        }

        //Integer laneId, Integer sampleId, String name, String description, String cycleDescriptor, boolean skip
        if (checkFields(necessaryFields)) {
            //allow barcode to be empty

            // create a new experiment
            ret = metadata.addIUS(Integer.parseInt(fields.get("lane_accession")), Integer.parseInt(fields.get("sample_accession")), fields.get("name"), fields.get("description"), fields.get("barcode"), "true".equalsIgnoreCase(fields.get("skip")));

            print("SWID: " + ret.getAttribute("sw_accession"));

        } else {
            Log.error("You need to supply name, description, lane_accession, sample_accession, barcode and 'true' or 'false' for skip. Alternatively, enable --interactive mode.");
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        }
        return (ret);
    }

    /**
     * {@inheritDoc}
     */
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
    private ReturnValue parseFields() {
        ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);
        List<?> valuesOf = options.valuesOf("field");
        for (Object value : valuesOf) {
            String[] t = value.toString().split("::");
            if (t.length == 2) {
                fields.put(t[0], t[1]);
                Log.info("  Field: " + t[0] + " value " + t[1]);
            }
        }
        return (ret);
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

    private void closeBufferWriter() {
        try {
            if (this.bw != null) {
                this.bw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkFields(String[] fs) {
        boolean allPresent = true;
        for (String s : fs) {
            if (!fields.containsKey(s)) {
                allPresent = false;
            }
        }
        return allPresent;
    }

    ////////////////////////////////////////////////////////////////////////////
    ///// Interactive code
    ////////////////////////////////////////////////////////////////////////////
    private void promptForStudy(String[] necessaryFields) {
        Log.stdout("---Create a study---");
        if (!fields.containsKey("study_type")) {
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

    private void promptForSample(String[] necessaryFields) {
        Log.stdout("---Create a sample---");
        if (!fields.containsKey("organism_id")) {
            for (Organism o : metadata.getOrganisms()) {
                Log.stdout(o.toString());
            }
            promptInteger("organism_id", 31);
        }
        promptForFields(necessaryFields);
        if (!fieldsConfirmed(necessaryFields)) {
            promptForSample(necessaryFields);
        }
    }

    private void promptForSequencerRun(String[] necessaryFields) {
        Log.stdout("---Create a sequencer run---");
        if (!fields.containsKey("platform_accession")) {
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

    private void promptForExperiment(String[] necessaryFields) {
        Log.stdout("---Create an experiment---");
        if (!fields.containsKey("platform_id")) {
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

    private void promptForLane(String[] necessaryFields) {
        Log.stdout("---Create a lane---");
        if (!fields.containsKey("sequencer_run_accession")) {
            promptInteger("sequencer_run_accession", null);
        }
        if (!fields.containsKey("study_type_accession")) {
            for (StudyType st : metadata.getStudyTypes()) {
                Log.stdout(st.toString());
            }
            promptInteger("study_type_accession", 4);
        }
        if (!fields.containsKey("library_strategy_accession")) {
            for (LibraryStrategy st : metadata.getLibraryStrategies()) {
                Log.stdout(st.toString());
            }
            promptInteger("library_strategy_accession", null);
        }
        if (!fields.containsKey("library_selection_accession")) {
            for (LibrarySelection st : metadata.getLibrarySelections()) {
                Log.stdout(st.toString());
            }
            promptInteger("library_selection_accession", null);
        }
        if (!fields.containsKey("library_source_accession")) {
            for (LibrarySource st : metadata.getLibrarySource()) {
                Log.stdout(st.toString());
            }
            promptInteger("library_source_accession", null);
        }
        if (!fields.containsKey("skip")) {
            promptBoolean("skip", false);
        }
        promptForFields(necessaryFields);
        if (!fieldsConfirmed(necessaryFields)) {
            promptForLane(necessaryFields);
        }
    }

    private void promptForIUS(String[] necessaryFields) {
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

    private void promptForFields(String[] fs) {
        for (String s : fs) {
            if (!fields.containsKey(s)) {
                promptString(s, null);
            }
        }
    }

    protected String promptString(String string, String deflt) {
        String title = null;

        String prompt = string + (deflt == null ? ":" : "[" + deflt + "] :");
        int counter=0;
        while (title == null && counter++ < 10) {
            title = ConsoleAdapter.getInstance().readLine(prompt);
            if (title.trim().isEmpty()) {
                title = deflt;
            }
        }
        fields.put(string, title);
        return title;
    }

    protected Integer promptInteger(String string, Integer deflt) {
        Integer title = null;
        String prompt = string + (deflt == null ? ":" : "[" + deflt + "] :");
        int counter=0;
        while (title == null && counter++ < 10) {
            String line = ConsoleAdapter.getInstance().readLine(prompt);
            if (line.trim().isEmpty()) {
                title = deflt;
            } else {
                try {
                    title = Integer.parseInt(line);
                } catch (NumberFormatException e) {
                    Log.stdout(string + " must be an integer.");
                }
            }
        }
        fields.put(string, title.toString());
        return title;
    }

    protected Boolean promptBoolean(String string, Boolean deflt) {
        Boolean title = null;
        String prompt = string + (deflt == null ? ":" : "[" + deflt + "] :");
        int counter=0;
        while (title == null && counter++ < 10) {
            String line = ConsoleAdapter.getInstance().readLine(prompt);
            if (line.trim().isEmpty()) {
                title = deflt;
            } else {
                try {
                    title = Boolean.parseBoolean(line);
                } catch (NumberFormatException e) {
                    Log.stdout(string + " must be true or false.");
                }

            }
        }
        fields.put(string, title.toString());
        return title;
    }

    protected boolean fieldsConfirmed(String[] necessaryFields) {
        for (String s : necessaryFields) {
            Log.stdout(s + " : " + fields.get(s));
        }

        String confirm = ConsoleAdapter.getInstance().readLine("Is this information correct? [y/n] :");
        System.out.println("result: " + confirm);
        if (confirm.trim().toLowerCase().equals("y") || confirm.trim().toLowerCase().equals("yes")) {
            return true;
        } else {
            fields.clear();
            parseFields();
            return false;
        }
    }
}
