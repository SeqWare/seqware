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

import java.lang.String;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openide.util.lookup.ServiceProvider;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.common.model.*;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.runtools.ConsoleAdapter;
import net.sourceforge.seqware.pipeline.plugins.batchmetadatainjection.LaneInfo;
import net.sourceforge.seqware.pipeline.plugins.batchmetadatainjection.ParseMisecFile;
import net.sourceforge.seqware.pipeline.plugins.batchmetadatainjection.RunInfo;
import net.sourceforge.seqware.pipeline.plugins.batchmetadatainjection.SampleInfo;

/**
 * <p>BatchImport class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
@ServiceProvider(service = PluginInterface.class)
public class BatchMetadataInjection extends Metadata {

    private ReturnValue ret = new ReturnValue();
    private StringBuffer whatWeDid = new StringBuffer();
    private Map<Integer, String> names;

    protected String choiceOf(String sampleName, String title, String[] choices, String deflt) {
        String choice = "";
        int choiceInt = 0;
        Log.stdout("\nFor sample " + sampleName + ", choose one of the following for " + title + " or press enter to skip:");
        for (int i = 1; i <= choices.length; i++) {
            Log.stdout(i + " : " + choices[i - 1]);
            if (deflt.equals(choices[i - 1])) {
                choiceInt = i;
            }
        }
        choiceInt = promptInteger(title, choiceInt);
        if (choiceInt == 0) { //no selection
        } else if (choiceInt == choices.length) {
            choice = promptString("Please specify :", null);
        } else if (choiceInt <= 0 || choiceInt > choices.length) {
            Log.stdout("Please choose from the given options.");
            choice = choiceOf(sampleName, title, choices, deflt);
        } else {
            choice = choices[choiceInt - 1];
        }
        return choice;
    }

    //private boolean createStudy = false;
    /**
     * <p>Constructor for AttributeAnnotator.</p>
     */
    public BatchMetadataInjection() {
        super();
        parser.accepts("misec-sample-sheet", "the location of the MiSec Sample Sheet").withRequiredArg();
        ret.setExitStatus(ReturnValue.SUCCESS);
        names = new HashMap<Integer, String>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue init() {
        if (options.has("create") && options.has("table")) {
        }
        if (options.has("table") && options.has("list-fields")) {
            // list the fields for this table
            ret = (listFields((String) options.valueOf("table")));
            return ret;
        }
        whatWeDid.append("digraph dag {");

        return ret;
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
        if (options.has("misec-sample-sheet")) {
            String filepath = (String) options.valueOf("misec-sample-sheet");
            ParseMisecFile parser = new ParseMisecFile(metadata);
            try {
                RunInfo run = parser.parseMiSecFile(filepath);
                inject(run);
            } catch (Exception ex) {
                Log.error("The run could not be imported.", ex);
            }
        } else {
            Log.stdout("Combination of parameters not recognized!");
            Log.stdout(this.get_syntax());
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);

        }
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue clean_up() {
        whatWeDid.append("\n}");
        System.out.println(whatWeDid.toString());
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String get_description() {
        return "Import objects into the database using different file formats.";
    }

    private ReturnValue inject(RunInfo run) throws Exception {
        int sequencerRunAccession = createRun(run);

        Set<LaneInfo> lanes = run.getLanes();

        List<Lane> existingLanes = metadata.getLanesFrom(sequencerRunAccession);
        if (existingLanes != null && !existingLanes.isEmpty()) {
            Boolean yorn = promptBoolean("This sequencer run already has " + existingLanes.size() + " lanes. Continue?", Boolean.TRUE);
            if (yorn.equals(Boolean.FALSE)) {
                throw new Exception("Sequencer run " + sequencerRunAccession + " already has lanes.");
            }
        }

        int studyAccession = retrieveStudy(run);
        int experimentAccession = retrieveExperiment(run, studyAccession);
        List<Sample> parentSamples = metadata.getSamplesFrom(experimentAccession);

        Log.debug("study: " + studyAccession + " exp: " + experimentAccession + " run: " + sequencerRunAccession);
        for (LaneInfo lane : lanes) {
            int laneAccession = createLane(lane, sequencerRunAccession);

            for (SampleInfo barcode : lane.getSamples()) {
                Integer parentSampleAcc = retrieveParentSampleAccession(parentSamples, barcode, experimentAccession);

                Log.debug("lane: " + laneAccession + " parent sample: " + parentSampleAcc);

                int tissueTypeSampleAcc = retrieveTissueTypeSampleAccession(parentSampleAcc, barcode);

                Log.debug("tissue type sample: " + tissueTypeSampleAcc);

                int librarySampleNameAcc = createLibrarySample(barcode, tissueTypeSampleAcc);

                int barcodeAcc = createIUS(barcode, laneAccession, librarySampleNameAcc);

            }
        }
        return ret;
    }

    private int createLibrarySample(SampleInfo sample, int tissueTypeSampleAcc) {

        //get the library sample
        int librarySampleNameAcc = createSample(sample.getName(), sample.getSampleDescription(),
                0, tissueTypeSampleAcc, sample.getOrganismId(), true);

        if (!sample.getSampleAttributes().isEmpty()) {
            metadata.annotateSample(librarySampleNameAcc, sample.getSampleAttributes());
        }

        names.put(librarySampleNameAcc, sample.getName());
        recordEdge("Sample", tissueTypeSampleAcc, "Sample", librarySampleNameAcc);

        return librarySampleNameAcc;
    }

    private int retrieveTissueTypeSampleAccession(Integer parentSampleAcc, SampleInfo barcode) {
        //get the tissue type sample if it exists, otherwise create it
        int tissueTypeSampleAcc = 0;
        String name = barcode.getParentSample() + "_" + barcode.getTissueType();
        List<Sample> children = metadata.getChildSamplesFrom(parentSampleAcc);
        if (children != null) {
            for (Sample s : children) {
                if (s.getTitle().equals(name)) {
                    tissueTypeSampleAcc = s.getSwAccession();
                }
            }
        }
        if (tissueTypeSampleAcc == 0) {
            tissueTypeSampleAcc = createSample(name, "", 0,
                    parentSampleAcc, barcode.getOrganismId(), false);
        }
        names.put(tissueTypeSampleAcc, name);
        recordEdge("Sample", parentSampleAcc, "Sample", tissueTypeSampleAcc);

        return tissueTypeSampleAcc;
    }

    private Integer retrieveParentSampleAccession(List<Sample> parentSamples, SampleInfo barcode, int experimentAccession) {
        //get the parent sample if it exists, otherwise create it
        Integer parentSampleAcc = null;
        if (parentSamples != null && !parentSamples.isEmpty()) {
            for (Sample pSample : parentSamples) {
                if (pSample.getName().equals(barcode.getParentSample())) {
                    parentSampleAcc = pSample.getSwAccession();
                }
            }
        }
        if (parentSampleAcc == null) {
            parentSampleAcc = createSample(barcode.getParentSample(), "",
                    experimentAccession, 0, barcode.getOrganismId(), false);
        }
        names.put(parentSampleAcc, barcode.getParentSample());
        recordEdge("Experiment", experimentAccession, "Sample", parentSampleAcc);

        return parentSampleAcc;
    }

    private int createIUS(SampleInfo barcode, int laneAccession, int sampleAccession) {
        Log.stdout("\n-------Creating a new IUS---------");

        fields.clear();
        fields.put("lane_accession", String.valueOf(laneAccession));
        fields.put("sample_accession", String.valueOf(sampleAccession));
        fields.put("name", barcode.getIusName());
        fields.put("description", barcode.getIusDescription());
        fields.put("skip", String.valueOf(barcode.getIusSkip()));
        fields.put("barcode", barcode.getBarcode());

        //printDefaults();
        interactive = true;
        ReturnValue rv = addIUS();
        Integer swAccession = Integer.parseInt(rv.getAttribute("sw_accession"));

        if (!barcode.getIusAttributes().isEmpty()) {
            metadata.annotateIUS(swAccession, barcode.getIusAttributes());
        }

        names.put(swAccession, barcode.getBarcode());

        recordEdge("Lane", laneAccession, "IUS", swAccession);
        recordEdge("Sample", sampleAccession, "IUS", swAccession);

        return swAccession;

    }

    private int createSample(String name, String description, int experimentAccession, int parentSampleAccession, String organismId, boolean interactive) {
        Log.stdout("\n--------Creating a new sample---------");

        fields.clear();
        fields.put("experiment_accession", String.valueOf(experimentAccession));
        fields.put("parent_sample_accession", String.valueOf(parentSampleAccession));
        fields.put("organism_id", organismId);
        fields.put("title", name);
        fields.put("description", description);

        this.interactive = interactive;

//        if (interactive) {
//            printDefaults();
//        }
        ReturnValue rv = addSample();

        return Integer.parseInt(rv.getAttribute("sw_accession"));
    }

//    private int retrieveExperiment(RunInfo run, int studyAccession) {
//    }
    private int createLane(LaneInfo lane, int sequencerRunAccession) {
        Log.stdout("\n--------Creating a new lane---------");

        fields.clear();
        fields.put("skip", lane.getLaneSkip().toString());
        fields.put("lane_number", lane.getLaneNumber());
        fields.put("name", lane.getLaneName());
        fields.put("description", lane.getLaneDescription());
        fields.put("sequencer_run_accession", String.valueOf(sequencerRunAccession));
        fields.put("library_strategy_accession", lane.getLibraryStrategyAcc());
        fields.put("library_selection_accession", lane.getLibrarySelectionAcc());
        fields.put("library_source_accession", lane.getLibrarySourceAcc());
        fields.put("cycle_descriptor", lane.getLaneCycleDescriptor());
        fields.put("study_type_accession", lane.getStudyTypeAcc());

//        printDefaults();
        interactive = true;
        ReturnValue rv = addLane();
        Integer swAccession = Integer.parseInt(rv.getAttribute("sw_accession"));

        if (!lane.getLaneAttributes().isEmpty()) {
            metadata.annotateLane(swAccession, lane.getLaneAttributes());
        }

        names.put(swAccession, lane.getLaneName());
        recordEdge("Sequencer Run", sequencerRunAccession, "Lane", swAccession);

        return swAccession;
    }

    private int createRun(RunInfo run) throws Exception {
        Integer swAccession = null;
        Log.stdout("\n-------------Retrieving sequencer run-----------");
        String runName = ConsoleAdapter.getInstance().promptString("name", run.getRunName());
        List<SequencerRun> runs = metadata.getAllSequencerRuns();
        if (runs != null) {
            for (SequencerRun sr : runs) {
                if (runName.equals(sr.getName())) {
                    Log.stdout("Using existing sequencer run:" + sr.getName() + " accession " + sr.getSwAccession());
                    swAccession = sr.getSwAccession();
                }
            }
        }
        if (swAccession == null) {
            Log.stdout("\n--------Creating a new sequencer run---------");
            fields.clear();
            fields.put("platform_accession", run.getPlatformId());
            fields.put("skip", String.valueOf(run.getRunSkip()));
            fields.put("paired_end", String.valueOf(run.isPairedEnd()));
            fields.put("name", runName);
            fields.put("description", run.getRunDescription());
//        printDefaults();
            interactive = true;
            ReturnValue rv = addSequencerRun();
            swAccession = Integer.parseInt(rv.getAttribute("sw_accession"));
        }

        if (!run.getRunAttributes().isEmpty()) {
            metadata.annotateSequencerRun(swAccession, run.getRunAttributes());
        }

        names.put(swAccession, runName);
        return swAccession;
    }

    private int retrieveExperiment(RunInfo run, int studyAccession) throws Exception {
        Log.stdout("\n--------Retrieving experiments---------");
        List<Experiment> experiments = metadata.getExperimentsFrom(studyAccession);
        Integer experimentAccession = null;
        if (experiments != null && !experiments.isEmpty()) {
            Log.stdout("Please use one of the following experiments:");
            for (Experiment e : experiments) {
                Log.stdout("\t" + e.getTitle());
            }
        }
        String experimentName = ConsoleAdapter.getInstance().promptString("Experiment name", run.getExperimentName());
        if (experiments != null) {
            for (Experiment ex : experiments) {
                if (ex.getTitle().equals(experimentName)) {
                    experimentAccession = ex.getSwAccession();
                }
            }
        }
        if (experimentAccession == null) {
            if (experiments == null || experiments.isEmpty()) {
                Log.stdout("\n--------Creating a new experiment---------");

                fields.clear();
                fields.put("study_accession", String.valueOf(studyAccession));
                fields.put("platform_id", run.getPlatformId());
                fields.put("title", run.getExperimentName());
                fields.put("description", run.getExperimentDescription());

//                printDefaults();
                interactive = true;
                ReturnValue rv = addExperiment();
                experimentAccession = Integer.parseInt(rv.getAttribute("sw_accession"));
            } else {
                Log.stdout("This tool does not support creating new experiments when experiments already exist.");
                Log.stdout("You can create a new experiment for study " + studyAccession + " using the Metadata plugin.");
                Log.stdout("e.g. java -jar seqware-distribution-full.jar -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --create --table experiment");
                throw new Exception("This tool does not support creating new experiments when experiments already exist.");
            }
        }

        if (!run.getExperimentAttributes().isEmpty()) {
            metadata.annotateExperiment(experimentAccession, run.getExperimentAttributes());
        }

        names.put(experimentAccession, experimentName);
        recordEdge("Study", studyAccession, "Experiment", experimentAccession);

        return experimentAccession;
    }

    private int retrieveStudy(RunInfo run) {
        Log.stdout("\n--------Retrieving studies---------");
        List<Study> studies = metadata.getAllStudies();
        Integer studyAccession = null;
        String studyName = ConsoleAdapter.getInstance().promptString("Study name", run.getStudyTitle());
        for (Study st : studies) {
            if (st.getTitle().equals(studyName)) {
                studyAccession = st.getSwAccession();
            }
        }
        if (studyAccession == null) {
            Log.stdout("\n--------Creating a new study---------");

            fields.clear();
            fields.put("title", studyName);
            fields.put("description", run.getStudyDescription());
            fields.put("center_name", run.getStudyCenterName());
            fields.put("center_project_name", run.getStudyCenterProject());
            fields.put("study_type", run.getStudyType());

//            printDefaults();
            interactive = true;
            ReturnValue rv = addStudy();
            studyAccession = Integer.parseInt(rv.getAttribute("sw_accession"));
        }

        if (!run.getStudyAttributes().isEmpty()) {
            metadata.annotateStudy(studyAccession, run.getStudyAttributes());
        }

        names.put(studyAccession, studyName);
        return studyAccession;
    }

    private void recordEdge(String type1, Integer accession1, String type2, Integer accession2) {
        whatWeDid.append("\n\t\"").append(type1).append(" ").append(names.get(accession1)).append("\\n").append(accession1);
        whatWeDid.append("\" -> \"").append(type2).append(" ").append(names.get(accession2)).append("\\n").append(accession2).append("\"");
    }
}
