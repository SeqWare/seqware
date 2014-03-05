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

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.seqware.common.model.*;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.runtools.ConsoleAdapter;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.pipeline.plugins.batchmetadatainjection.*;
import net.sourceforge.seqware.pipeline.runner.PluginRunner;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.openide.util.lookup.ServiceProvider;

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
    private boolean interactive = false;

    //private boolean createStudy = false;
    /**
     * <p>Constructor for AttributeAnnotator.</p>
     */
    public BatchMetadataInjection() {
        super();
        parser.accepts("miseq-sample-sheet", "The location of the Miseq Sample Sheet").withRequiredArg();
        parser.accepts("new", "Create a new study from scratch. Used instead of miseq-sample-sheet");
        parser.accepts("interactive", "Optional: turn on interactive input ");
        parser.accepts("record", "Optional: saves information about the injection in a text file").withOptionalArg();
        parser.accepts("import-json-sequencer-run", "The location of the RunInfo json file to import.").withOptionalArg();
        parser.accepts("export-json-sequencer-run", "The location to export the RunInfo json output file.").withOptionalArg();
        parser.accepts("validate-json-sequencer-run", "The location of the RunInfo json file to validate.").withOptionalArg();
        ret.setExitStatus(ReturnValue.SUCCESS);
        names = new HashMap<Integer, String>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue init() {

        interactive = options.has("interactive");

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
        if (options.has("list-fields")) {
            for (BatchMetadataParser.Field field : BatchMetadataParser.Field.values()) {
                Log.stdout(field.toString());
            }
        } else {
            RunInfo run = null;
            if (options.has("field")) {
                parseFields();
            }
            if (options.has("miseq-sample-sheet")) {
                parseFields();
                String filepath = (String) options.valueOf("miseq-sample-sheet");
                ParseMiseqFile MiseqParser = new ParseMiseqFile(metadata, (Map<String, String>) fields.clone(), interactive);
                try {
                    run = MiseqParser.parseMiseqFile(filepath);
                    inject(run);

                } catch (Exception ex) {
                    Log.error("The run could not be imported.", ex);
                }
            } else if (options.has("new")) {
                try {
                    parseFields();
                    CreateFromScratch create = new CreateFromScratch(metadata, (Map<String, String>) fields.clone(), interactive);
                    run = create.getRunInfo();
                    inject(run);
                } catch (Exception ex) {
                    Log.error("The run could not be imported.", ex);
                }
            } else if (options.has("import-json-sequencer-run")) {

                String jsonFileInputPath = (String) options.valueOf("import-json-sequencer-run");

                if (jsonFileInputPath == null) {
                    Log.error("Import sequencer run file path missing");
                    ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
                    return ret;
                }

                //Read json file, validate it, and deserialize it to RunInfo
                try {
                    run = jsonToRunInfo(jsonFileInputPath);
                } catch (IOException ioe) {
                    Log.error("Could not parse JSON input file: " + jsonFileInputPath, ioe);
                    ret.setExitStatus(ReturnValue.INVALIDFILE);
                    return ret;
                }

                //Inject the RunInfo data into the seqware db
                try {
                    inject(run);
                } catch (Exception e) {
                    Log.error("The run could not be imported.", e);
                    ret.setExitStatus(ReturnValue.FAILURE);
                    return ret;
                }
            } else if (options.has("validate-json-sequencer-run")) {
                String jsonFileInputPath = (String) options.valueOf("validate-json-sequencer-run");

                if (jsonFileInputPath == null) {
                    Log.error("Validate sequencer run file path missing");
                    ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
                    return ret;
                }

                //If conversion to RunInfo object is successfull, then json file validated against RunInfo schema
                try {
                    jsonToRunInfo(jsonFileInputPath);
                } catch (IOException ioe) {
                    Log.error("Could not parse JSON input file: " + jsonFileInputPath, ioe);
                    ret.setExitStatus(ReturnValue.INVALIDFILE);
                    return ret;
                }
                Log.stdout("JSON sequencer run file is valid.");
                return ret;
            } else {
                Log.stdout("Combination of parameters not recognized!");
                Log.stdout(this.get_syntax());
                ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            }
            if (options.has("record") && run != null) {
                Object o = options.valueOf("record");
                String filename;
                if (o != null) {
                    filename = (String) o;
                } else {
                    filename = run.getRunName() + System.currentTimeMillis();
                }
                try {
                    FileWriter writer = new FileWriter(filename);
                    run.print(writer, metadata);
                    writer.flush();
                    writer.close();
                } catch (IOException ex) {
                    Log.warn("Error while writing to the record file " + filename, ex);
                }
            }
            if (options.has("export-json-sequencer-run")) {
                Object o = options.valueOf("export-json-sequencer-run");
                if (o == null) {
                    Log.error("Export sequencer run file path missing");
                    ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
                    return ret;
                }

                ObjectMapper mapper = new ObjectMapper();
                mapper.setSerializationInclusion(Inclusion.NON_NULL);
                ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
                String jsonRunInfo;
                try {
                    jsonRunInfo = writer.writeValueAsString(run);
                    //jsonRunInfo = mapper.generateJsonSchema(RunInfo.class).toString();
                } catch (IOException ioe) {
                    Log.error("Could not convert RunInfo object to json", ioe);
                    ret.setExitStatus(ReturnValue.FAILURE);
                    return ret;
        }

                java.io.File jsonFileOutputPath = new java.io.File((String) o);
                try {
                    Files.write(jsonRunInfo, jsonFileOutputPath, Charsets.UTF_8);
                } catch (IOException ioe) {
                    Log.error("Could not write JSON output file: " + jsonFileOutputPath, ioe);
                    ret.setExitStatus(ReturnValue.FAILURE);
        return ret;
    }
            }
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
        if (existingLanes != null && !existingLanes.isEmpty() && interactive) {
            Boolean yorn = ConsoleAdapter.getInstance().promptBoolean("This sequencer run already has " + existingLanes.size() + " lanes. Continue?", Boolean.TRUE);
            if (yorn.equals(Boolean.FALSE)) {
                throw new Exception("Sequencer run " + sequencerRunAccession + " already has lanes.");
            }
        }

        int studyAccession = retrieveStudy(run);
        int experimentAccession = retrieveExperiment(run, studyAccession);
        

        Log.debug("study: " + studyAccession + " exp: " + experimentAccession + " run: " + sequencerRunAccession);
        for (LaneInfo lane : lanes) {
            Log.stdout("\nCreating lane "+lane.getLaneNumber());
            int laneAccession = createLane(lane, sequencerRunAccession);

            for (SampleInfo barcode : lane.getSamples()) {
                List<Sample> parentSamples = metadata.getSamplesFrom(experimentAccession);
                Integer parentSampleAcc = retrieveParentSampleAccession(parentSamples, barcode, experimentAccession);

                Log.debug("lane: " + laneAccession + " parent sample: " + parentSampleAcc);

                int tissueTypeSampleAcc = retrieveTissueTypeSampleAccession(parentSampleAcc, barcode, experimentAccession);

                Log.debug("tissue type sample: " + tissueTypeSampleAcc);

                int librarySampleNameAcc = createLibrarySample(barcode, tissueTypeSampleAcc, experimentAccession);

                int barcodeAcc = createIUS(barcode, laneAccession, librarySampleNameAcc);

            }
        }
        return ret;
    }

    private int createLibrarySample(SampleInfo sample, int tissueTypeSampleAcc, int experimentAccession) throws Exception {

        //get the library sample
        int librarySampleNameAcc = createSample(sample.getName(), sample.getSampleDescription(),
                experimentAccession, tissueTypeSampleAcc, sample.getOrganismId(), true);

        List<Sample> children = metadata.getChildSamplesFrom(tissueTypeSampleAcc);
        for (Sample s : children) {
            Log.debug("Created and" + s.toString());
        }
        if (!sample.getSampleAttributes().isEmpty()) {

            Set<SampleAttribute> sa = null;
            try {
                sa = convertTagValueUnitSetToAttributeSet(sample.getSampleAttributes(), SampleAttribute.class);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.exit(1);
        }
            metadata.annotateSample(librarySampleNameAcc, sa);
        }
        children = metadata.getChildSamplesFrom(tissueTypeSampleAcc);
        for (Sample s : children) {
            Log.debug("annotated and" + s.toString());
        }
        names.put(librarySampleNameAcc, sample.getName());
        recordEdge("Sample", tissueTypeSampleAcc, "Sample", librarySampleNameAcc);

        return librarySampleNameAcc;
    }

    private int retrieveTissueTypeSampleAccession(Integer parentSampleAcc, SampleInfo barcode, int experimentAccession) throws Exception {
        //get the tissue type sample if it exists, otherwise create it
        int tissueTypeSampleAcc = 0;
        String name = barcode.getParentSample() + "_" +barcode.getTissueOrigin()+"_"+ barcode.getTissueType();
        List<Sample> children = metadata.getChildSamplesFrom(parentSampleAcc);
        if (children != null) {
            for (Sample s : children) {
                if (s.getTitle().equals(name)) {
                    tissueTypeSampleAcc = s.getSwAccession();
                }
            }
        }
        if (tissueTypeSampleAcc == 0) {
            tissueTypeSampleAcc = createSample(name, "", experimentAccession,
                    parentSampleAcc, barcode.getOrganismId(), false);
        }
        names.put(tissueTypeSampleAcc, name);
        recordEdge("Sample", parentSampleAcc, "Sample", tissueTypeSampleAcc);

        return tissueTypeSampleAcc;
    }

    private Integer retrieveParentSampleAccession(List<Sample> parentSamples, SampleInfo sample, int experimentAccession) throws Exception {
        //get the parent sample if it exists, otherwise create it
        Integer parentSampleAcc = null;
        if (parentSamples != null && !parentSamples.isEmpty()) {
            for (Sample pSample : parentSamples) {
                if (pSample.getName().equals(sample.getParentSample())) {
                    parentSampleAcc = pSample.getSwAccession();
                }
            }
        }
        if (parentSampleAcc == null) {
            parentSampleAcc = createSample(sample.getParentSample(), "",
                    experimentAccession, 0, sample.getOrganismId(), false);
        }
        names.put(parentSampleAcc, sample.getParentSample());
        recordEdge("Experiment", experimentAccession, "Sample", parentSampleAcc);

        return parentSampleAcc;
    }

    private int createIUS(SampleInfo barcode, int laneAccession, int sampleAccession) throws Exception {
        Log.stdout("\nCreating barcode "+barcode.getBarcode());
        fields.clear();
        fields.put("lane_accession", String.valueOf(laneAccession));
        fields.put("sample_accession", String.valueOf(sampleAccession));
        fields.put("name", barcode.getIusName());
        fields.put("description", barcode.getIusDescription());
        fields.put("skip", String.valueOf(barcode.getIusSkip()));
        fields.put("barcode", barcode.getBarcode());

        //printDefaults();
//        interactive = true;
        ReturnValue rv = addIUS();
        Integer swAccession = getSwAccession(rv);

        if (!barcode.getIusAttributes().isEmpty()) {

            Set<IUSAttribute> ia = null;
            try {
                ia = convertTagValueUnitSetToAttributeSet(barcode.getIusAttributes(), IUSAttribute.class);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.exit(1);
        }
            metadata.annotateIUS(swAccession, ia);
        }

        names.put(swAccession, barcode.getBarcode());

        recordEdge("Lane", laneAccession, "IUS", swAccession);
        recordEdge("Sample", sampleAccession, "IUS", swAccession);

        return swAccession;

    }

    private int createSample(String name, String description, int experimentAccession, int parentSampleAccession, String organismId, boolean interactive) throws Exception {
        Log.stdout("\nCreating sample "+name);
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

        return getSwAccession(rv);
    }

//    private int retrieveExperiment(RunInfo run, int studyAccession) {
//    }
    private int createLane(LaneInfo lane, int sequencerRunAccession) throws Exception {

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
//        interactive = true;
        ReturnValue rv = addLane();
        Integer swAccession = getSwAccession(rv);

        if (!lane.getLaneAttributes().isEmpty()) {

            Set<LaneAttribute> la = null;
            try {
                la = convertTagValueUnitSetToAttributeSet(lane.getLaneAttributes(), LaneAttribute.class);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.exit(1);
        }
            metadata.annotateLane(swAccession, la);
        }

        names.put(swAccession, lane.getLaneName());
        recordEdge("Sequencer Run", sequencerRunAccession, "Lane", swAccession);

        return swAccession;
    }

    private int createRun(RunInfo run) throws Exception {
        Integer swAccession = null;
        Log.stdout("\nRetrieving sequencer run "+run.getRunName());
        List<SequencerRun> runs = metadata.getAllSequencerRuns();
        if (runs != null) {
            for (SequencerRun sr : runs) {
                if (run.getRunName().equals(sr.getName())) {
                    Log.stdout("Using existing sequencer run:" + sr.getName() + " accession " + sr.getSwAccession());
                    swAccession = sr.getSwAccession();
                    break;
                }
            }
        }
        if (swAccession == null) {
            fields.clear();
            fields.put("platform_accession", run.getPlatformId());
            fields.put("skip", String.valueOf(run.getRunSkip()));
            fields.put("paired_end", String.valueOf(run.isPairedEnd()));
            fields.put("name", run.getRunName());
            fields.put("description", run.getRunDescription());
            fields.put("file_path", run.getRunFilePath());
//        printDefaults();
//            interactive = true;
            ReturnValue rv = addSequencerRun();
            swAccession = getSwAccession(rv);
        }

        if (!run.getRunAttributes().isEmpty()) {
            Set<SequencerRunAttribute> ra = null;
            try {
                ra = convertTagValueUnitSetToAttributeSet(run.getRunAttributes(), SequencerRunAttribute.class);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.exit(1);
        }
            metadata.annotateSequencerRun(swAccession, ra);
        }

        names.put(swAccession, run.getRunName());
        return swAccession;
    }

    private int retrieveExperiment(RunInfo run, int studyAccession) throws Exception {
        Log.stdout("\nRetrieving experiments for "+run.getStudyTitle());
        List<Experiment> experiments = metadata.getExperimentsFrom(studyAccession);
        Integer experimentAccession = null;
        if (experiments != null && !experiments.isEmpty()) {
            Log.stdout("Please use one of the following experiments:");
            for (Experiment e : experiments) {
                Log.stdout("\t" + e.getTitle());
            }
        }
        if (experiments != null) {
            for (Experiment ex : experiments) {
                if (ex.getTitle().equals(run.getExperimentName())) {
                    Log.stdout("Using existing experiment:" + ex.getName() + " accession " + ex.getSwAccession());
                    experimentAccession = ex.getSwAccession();
                }
            }
        }
        if (experimentAccession == null) {
            if (experiments == null || experiments.isEmpty()) {
                Log.stdout("\nAdding experiment "+run.getExperimentName());

                fields.clear();
                fields.put("study_accession", String.valueOf(studyAccession));
                fields.put("platform_id", run.getPlatformId());
                fields.put("title", run.getExperimentName());
                fields.put("description", run.getExperimentDescription());

//                printDefaults();
//                interactive = true;
                ReturnValue rv = addExperiment();
                experimentAccession = getSwAccession(rv);
            } else {
                Log.stdout("This tool does not support creating new experiments when experiments already exist.");
                Log.stdout("You can create a new experiment for study " + studyAccession + " using the Metadata plugin.");
                Log.stdout("e.g. java -jar seqware-distribution-full.jar -p net.sourceforge.seqware.pipeline.plugins.Metadata -- --create --table experiment");
                throw new Exception("This tool does not support creating new experiments when experiments already exist.");
            }
        }

        if (!run.getExperimentAttributes().isEmpty()) {
            Set<ExperimentAttribute> ea = null;
            try {
                ea = convertTagValueUnitSetToAttributeSet(run.getExperimentAttributes(), ExperimentAttribute.class);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.exit(1);
        }
            metadata.annotateExperiment(experimentAccession, ea);
        }

        names.put(experimentAccession, run.getExperimentName());
        recordEdge("Study", studyAccession, "Experiment", experimentAccession);

        return experimentAccession;
    }

    private int retrieveStudy(RunInfo run) throws Exception {
        Log.stdout("\nRetrieving study "+run.getStudyTitle());
        List<Study> studies = metadata.getAllStudies();
        Integer studyAccession = null;
        for (Study st : studies) {
            if (st.getTitle().equals(run.getStudyTitle())) {
                Log.stdout("Using existing study:" + st.getTitle() + " accession " + st.getSwAccession());
                studyAccession = st.getSwAccession();
            }
        }
        if (studyAccession == null) {
            fields.clear();
            fields.put("title", run.getStudyTitle());
            fields.put("description", run.getStudyDescription());
            fields.put("center_name", run.getStudyCenterName());
            fields.put("center_project_name", run.getStudyCenterProject());
            fields.put("study_type", run.getStudyType());

//            printDefaults();
//            interactive = true;
            ReturnValue rv = addStudy();
            studyAccession = getSwAccession(rv);
        }

        if (!run.getStudyAttributes().isEmpty()) {
            Set<StudyAttribute> sa = null;
            try {
                sa = convertTagValueUnitSetToAttributeSet(run.getStudyAttributes(), StudyAttribute.class);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.exit(1);
        }
            metadata.annotateStudy(studyAccession, sa);
        }

        names.put(studyAccession, run.getStudyTitle());
        return studyAccession;
    }

    private void recordEdge(String type1, Integer accession1, String type2, Integer accession2) {
        whatWeDid.append("\n\t\"").append(type1).append(" ").append(names.get(accession1)).append("\\n").append(accession1);
        whatWeDid.append("\" -> \"").append(type2).append(" ").append(names.get(accession2)).append("\\n").append(accession2).append("\"");
    }

    private int getSwAccession(ReturnValue rv) throws Exception {
        String swa = rv.getAttribute("sw_accession");
        if (swa != null && !swa.isEmpty()) {
            return Integer.parseInt(swa);
        } else {
            throw new Exception("No accession was returned");
        }
    }
    
    private RunInfo jsonToRunInfo(String filePath) throws IOException {       
        java.io.File jsonFileInputPath = new java.io.File(filePath);
        String jsonRunInfo = Files.toString(jsonFileInputPath, Charsets.UTF_8);
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        
        return mapper.readValue(jsonRunInfo, RunInfo.class);
}

    private <SeqwareObjectType, AttributeType extends Attribute<SeqwareObjectType>> Set<AttributeType> convertTagValueUnitSetToAttributeSet(Set<TagValueUnit> tagValueUnits, Class<AttributeType> attributeTypeClass)
            throws InstantiationException, IllegalAccessException {

        Set<AttributeType> attributes = new HashSet<AttributeType>();

        for (TagValueUnit tvu : tagValueUnits) {
            AttributeType at = attributeTypeClass.newInstance();
            at.setTag(tvu.getTag());
            at.setValue(tvu.getValue());
            at.setUnit(tvu.getUnit());
            attributes.add(at);
        }

        return attributes;
    }

    public static void main(String[] args) {

        System.out.println(Arrays.asList(args));

//        BatchMetadataInjection b = new BatchMetadataInjection();
//        b.setParams(Arrays.asList(args));
        PluginRunner p = new PluginRunner();
        List<String> a = new ArrayList<String>();
        a.add("--plugin");
        a.add(BatchMetadataInjection.class.getCanonicalName());
        a.add("--");
        a.addAll(Arrays.asList(args));
        System.out.println(Arrays.deepToString(a.toArray()));

        p.run(a.toArray(new String[a.size()]));
//        b.parse_parameters();
//        b.init();
//        b.do_test();
//        b.do_run();
//        b.clean_up();
        //b.setParams(Arrays.asList("import-run-info", "test"));

    }
}
