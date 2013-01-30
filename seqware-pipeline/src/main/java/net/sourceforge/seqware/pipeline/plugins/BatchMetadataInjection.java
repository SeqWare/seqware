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

import java.io.*;
import java.io.File;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openide.util.lookup.ServiceProvider;
import java.util.HashSet;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.common.model.*;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.plugins.BatchMetadataInjection.SampleInfo;

/**
 * <p>BatchImport class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
@ServiceProvider(service = PluginInterface.class)
public class BatchMetadataInjection extends Metadata {

    private ReturnValue ret = new ReturnValue();
    private String[] librarySourceTemplateType = new String[]{"CH", "EX", "MR", "SM", "TR","TS", "WG","WT", "Other"};
    private String[] targetedResequencing = new String[]{"Agilent SureSelect 244k Array", 
        "Agilent SureSelect All Exon G3362", "Agilent SureSelect ICGC/Sanger Exon", 
        "AmpliSeq Cancer Panel v1", "AmpliSeq Comprehensive Cancer Panel",
        "Illumina TruSeq Exome", "Ion AmpliSeq Cancer Panel v1", "Nimblegen 2.1M Human Exome (21191)", 
    "Nimblegen Human Exome v2.0", "Nimblegen ICGC Beta", "Nimblegen OICR Test (13668)", 
    "TruSeq Amplicon - Cancer Panel", "Other"};
    private String[] tissueOrigin = new String[]{"Br", "Cb", "Ep","Ki","Li","Lu",
        "Lv","Lx","Ly","Nk","nn","Oc", "Ov","Pa","Pr","Sg","Sp", "St","Ta","Tr", "Wm", "Other"};
    private String[] tissuePreparation = new String[] {"Blood", "FFPE", "Fresh Frozen", "Other"};
    private String[] tissueRegion = new String[]{"1","2","3","4","5","6","7","8"};
    private String[] tissueType = new String[]{"C","M","n","P","R","X","Other"};
    
            
    
    protected String choiceOf(String sampleName, String title, String[] choices) {
        String choice = "";
        Log.stdout("For sample "+sampleName+", choose one of the following for "+title+" or press enter to skip:");
        for (int i=1;i<=choices.length;i++)
        {
            Log.stdout(i+" : " +choices[i-1]);
        }
        int choiceInt = promptInteger(title, 0);
        if (choiceInt==0) { //no selection
        } else if (choiceInt==choices.length) {
            choice = promptString("Please specify :", null);
        } else {
            choice = choices[choiceInt-1];
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
            RunInfo run = parseMiSecFile(filepath);
            try {
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

        Map<String, List<SampleInfo>> lanes = new HashMap<String, List<SampleInfo>>();
        for (SampleInfo info : run.getSamples()) {
            List<SampleInfo> samples = lanes.get(info.getLane());
            if (samples == null) {
                samples = new ArrayList<SampleInfo>();
            }
            samples.add(info);
	    lanes.put(info.getLane(), samples);
        }

        List<Lane> existingLanes = metadata.getLanesFrom(sequencerRunAccession);
        if (existingLanes !=null && !existingLanes.isEmpty()) {
            Boolean yorn = promptBoolean("This sequencer run already has " + existingLanes.size() + " lanes. Continue?", Boolean.FALSE);
            if (yorn.equals(Boolean.FALSE)) {
                throw new Exception("Sequencer run " + sequencerRunAccession + " already has lanes.");
            }
        }


        int studyAccession = retrieveStudy(run);
        int experimentAccession = retrieveExperiment(run, studyAccession);
        List<Sample> parentSamples = metadata.getSamplesFrom(experimentAccession);

	Log.warn("study: " + studyAccession + " exp: " + experimentAccession + " run: " + sequencerRunAccession);
        for (String lane : lanes.keySet()) {
            int laneAccession = createLane(lane, sequencerRunAccession);

            for (SampleInfo barcode : lanes.get(lane)) {
                Integer parentSampleAcc = retrieveParentSampleAccession(parentSamples, barcode, experimentAccession);
                Log.warn("lane: " + laneAccession + " parent sample: "+parentSampleAcc); 
                int tissueTypeSampleAcc = retrieveTissueTypeSampleAccession(parentSampleAcc, barcode);
		Log.warn("tissue type sample: " + tissueTypeSampleAcc);
		//get the library sample
		int librarySampleNameAcc = createSample(barcode.getName(),0,tissueTypeSampleAcc, barcode.getOrganism());
               Log.warn("library sample: " + librarySampleNameAcc);
		String tety = barcode.getTemplateType();
		String tare = barcode.getTargetedResequencing();
		String tior = barcode.getTissueOrigin();
		String tipr = barcode.getTissuePreparation();
		String tire = barcode.getTissueRegion();
		String tity = barcode.getTissueType();
		if (tety.isEmpty()){
			tety = choiceOf(barcode.getName(),"Library Source Template Type", librarySourceTemplateType);
		}
		if (tare.isEmpty()) {
			tare = choiceOf(barcode.getName(),"Targeted Resequencing Type", targetedResequencing);
		}
		if (tior.isEmpty()) {
			tior = choiceOf(barcode.getName(),"Tissue Origin",tissueOrigin);
		}
		if (tipr.isEmpty()) {
			tipr = choiceOf(barcode.getName(), "Tissue Preparation", tissuePreparation);
		}
		if (tire.isEmpty()) {
			tire = choiceOf(barcode.getName(), "Tissue Region", tissueRegion);
		}
		if (tity.isEmpty()) {
			tity = choiceOf(barcode.getName(),"Tissue Type", tissueType);
		}

		metadata.annotateSample(librarySampleNameAcc, new SampleAttribute(0,null,"geo_library_source_template_type",tety), null);
		metadata.annotateSample(librarySampleNameAcc, new SampleAttribute(0,null,"geo_targeted_resequencing",tare),null);
		metadata.annotateSample(librarySampleNameAcc, new SampleAttribute(0,null,"geo_tissue_origin",tior),null);
                metadata.annotateSample(librarySampleNameAcc, new SampleAttribute(0,null,"geo_tissue_preparation",tipr),null);
		metadata.annotateSample(librarySampleNameAcc, new SampleAttribute(0,null,"geo_tissue_region", tire),null);
		metadata.annotateSample(librarySampleNameAcc, new SampleAttribute(0,null,"geo_tissue_type", tity),null);

		 int barcodeAcc = createIUS(laneAccession, librarySampleNameAcc, barcode.getBarcode(), null);

            }
        }
        return ret;
    }

    private int retrieveTissueTypeSampleAccession(Integer parentSampleAcc, SampleInfo barcode) {
        //get the tissue type sample if it exists, otherwise create it
        int tissueTypeSampleAcc =0;
        List<Sample> children = metadata.getChildSamplesFrom(parentSampleAcc);
        for (Sample s: children) {
                if (s.getTitle().equals(barcode.getTissueType())) {
                        tissueTypeSampleAcc = s.getSwAccession();
                }
        }
        if (tissueTypeSampleAcc == 0) {
                tissueTypeSampleAcc = createSample(barcode.getTissueType(), 0,
                                        parentSampleAcc, barcode.getOrganism());
        }
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
            parentSampleAcc = createSample(barcode.getParentSample(),
                    experimentAccession, 0, barcode.getOrganism());
        }
        return parentSampleAcc;
    }

    private int createIUS(int laneAccession, int sampleAccession, String name, String barcode) {
	Log.stdout("-------Creating a new IUS---------");

	fields.clear();
	fields.put("lane_accession", String.valueOf(laneAccession));
	fields.put("sample_accession", String.valueOf(sampleAccession));
	fields.put("name", name);
	fields.put("description", name);
	fields.put("skip", "false");
        fields.put("barcode", barcode);

	printDefaults();
	interactive=true;
	ReturnValue rv = addIUS();
	
	return Integer.parseInt(rv.getAttribute("sw_accession"));

    }

    private int createSample(String name, int experimentAccession, int parentSampleAccession, String organismId) {
        Log.stdout("--------Creating a new sample---------");

        fields.clear();
        fields.put("experiment_accession", String.valueOf(experimentAccession));
	fields.put("parent_sample_accession", String.valueOf(parentSampleAccession));
        fields.put("organism_id", organismId);
        fields.put("title", name);
        fields.put("description", name);

        printDefaults();
        interactive = true;
        ReturnValue rv = addSample();

        return Integer.parseInt(rv.getAttribute("sw_accession"));
    }


//    private int retrieveExperiment(RunInfo run, int studyAccession) {
//    }
    private int createLane(String laneName, int sequencerRunAccession) {
        Log.stdout("--------Creating a new lane---------");

        fields.clear();
        fields.put("skip", "false");
        fields.put("lane_number", laneName);
        fields.put("name", laneName);
        fields.put("description", laneName);
        fields.put("sequencer_run_accession", String.valueOf(sequencerRunAccession));

        printDefaults();
        interactive = true;
        ReturnValue rv = addLane();


	LaneAttribute la = new LaneAttribute();
	la.setTag("geo_lane");
	la.setValue(fields.get("lane_number"));
	metadata.annotateLane(Integer.parseInt(rv.getAttribute("sw_accession")), la, null); 
        return Integer.parseInt(rv.getAttribute("sw_accession"));
    }

    private int createRun(RunInfo run) throws Exception {

	Log.stdout("Creating a new sequencer run.");
        String runName = promptString("name", run.getRunName());
        for (SequencerRun sr : metadata.getAllSequencerRuns()) {
            if (sr.getName().equals(runName)) {
                Log.stdout("Using existing sequencer run:" + sr.getName() + " accession " + sr.getSwAccession());
                return sr.getSwAccession();
            }
        }
        Log.stdout("--------Creating a new sequencer run---------");
        fields.clear();
        fields.put("platform_accession", "26");
        fields.put("skip", "false");
        fields.put("paired_end", "true");
        fields.put("name", runName);
        fields.put("description", runName);
        printDefaults();
        interactive = true;
        ReturnValue rv = addSequencerRun();

        return Integer.parseInt(rv.getAttribute("sw_accession"));
    }

    private void printDefaults() {
        Log.stdout("Defaults:");
        for (String s : fields.keySet()) {
            Log.stdout(s + " : " + fields.get(s));
        }
        Log.stdout("You will have the opportunity to change these values.");
    }

    private int retrieveExperiment(RunInfo run, int studyAccession) throws Exception {
        List<Experiment> experiments = metadata.getExperimentsFrom(studyAccession);
        Integer experimentAccession = null;
        if (experiments != null && !experiments.isEmpty()) {
            Log.stdout("Please use one of the following experiments:");
            for (Experiment e : experiments) {
                Log.stdout("\t" + e.getTitle());
            }
        }
        String experimentName = promptString("Experiment name", run.getExperimentName());
        if (experiments!=null)
	for (Experiment ex : experiments) {
            if (ex.getTitle().equals(experimentName)) {
                experimentAccession = ex.getSwAccession();
            }
        }
        if (experimentAccession == null) {
            if (experiments == null || experiments.isEmpty()) {
                Log.stdout("--------Creating a new experiment---------");

                fields.clear();
                fields.put("study_accession", studyAccession + "");
                fields.put("platform_id", "26");
                fields.put("title", experimentName);
                fields.put("description", experimentName);

                printDefaults();
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
        return experimentAccession;
    }

    private int retrieveStudy(RunInfo run) {
        List<Study> studies = metadata.getAllStudies();
        Integer studyAccession = null;
        String studyName = promptString("Study name", run.getStudyTitle());
        for (Study st : studies) {
            if (st.getTitle().equals(studyName)) {
                studyAccession = st.getSwAccession();
            }
        }
        if (studyAccession == null) {
            Log.stdout("--------Creating a new study---------");

            fields.clear();
            fields.put("title", studyName);
            fields.put("description", studyName);
            fields.put("center_name", "Ontario Institute for Cancer Research");
            fields.put("center_project_name", studyName);

            printDefaults();
            interactive = true;
            ReturnValue rv = addStudy();
            studyAccession = Integer.parseInt(rv.getAttribute("sw_accession"));
        }
        return studyAccession;
    }

    protected RunInfo parseMiSecFile(String filepath) {
        RunInfo run = null;
        File file = new File(filepath);
        try {
            BufferedReader freader = new BufferedReader(new FileReader(file));
            run = parseMiSecHeader(freader, filepath);

            List<SampleInfo> samples = parseMiSecData(freader);
            freader.close();

            run.setSamples(samples);

        } catch (FileNotFoundException e) {
            Log.error(filepath, e);
            ret.setExitStatus(ReturnValue.FILENOTREADABLE);
        } catch (IOException ex) {
            Log.error(filepath, ex);
            ret.setExitStatus(ReturnValue.FILENOTREADABLE);
        }
        return run;
    }

    protected List<SampleInfo> parseMiSecData(BufferedReader freader) throws IOException, NumberFormatException {
        List<SampleInfo> samples = new ArrayList<SampleInfo>();

        String line = freader.readLine(); //Discard header
        while ((line = freader.readLine()) != null) {
            String[] args = line.split(",");
            String[] sampleInfo = args[0].split("-");
            SampleInfo info = new SampleInfo();
            info.setName(args[0]);
            info.setParentSample(sampleInfo[0] + "-" + sampleInfo[1]);
            info.setBarcode(args[5]);
            info.setLane("1");
            info.setOrganism(args[8].split("\\\\")[0].replace('_', ' '));
            //info.setTargetedResequencing();
            if (sampleInfo[2].contains("BLD")) {
                info.setTissueType("R");
                info.setTissuePreparation("Blood");
            } else if (sampleInfo[2].contains("BIO")) {
                info.setTissueType("P");
            } else if (sampleInfo[2].contains("ARC")) {
                info.setTissueType("A");
            } else {
                Log.stdout("Unknown tissue type");
            }

            info.setTissueRegion(sampleInfo[2].substring(0, 1));
            //info.setTissueOrigin();
            samples.add(info);

        }
        return samples;
    }

    protected RunInfo parseMiSecHeader(BufferedReader freader, String fileName) throws IOException {
        String line = null;
        RunInfo runInfo = new RunInfo();

        Map<String, String> headerInfo = new HashMap<String, String>();
        while (!(line = freader.readLine()).startsWith("[Data]")) {
            if (!line.startsWith("[")) {
                String[] args = line.split(",");
                if (args.length >= 2) {
                    headerInfo.put(args[0].trim(), args[1].trim());
                }
            }
        }
        String[] bits = fileName.split(File.separator);
        runInfo.setRunName(bits[bits.length - 2]);
        runInfo.setStudyTitle(headerInfo.get("Project Name").split("_")[0]);
        runInfo.setExperimentName(headerInfo.get("Experiment Name").split("_")[0]);
        runInfo.setWorkflowType(headerInfo.get("Workflow"));
        runInfo.setAssayType(headerInfo.get("Assay"));

        return runInfo;
    }

    protected class RunInfo {

        private String studyTitle;
        private String runName;
        private List<SampleInfo> samples = null;
        private String experimentName;
        private String workflowType;
        private String assayType;

        /**
         * Get the value of assayType
         *
         * @return the value of assayType
         */
        public String getAssayType() {
            return assayType;
        }

        /**
         * Set the value of assayType
         *
         * @param assayType new value of assayType
         */
        public void setAssayType(String assayType) {
            this.assayType = assayType;
        }

        /**
         * Get the value of workflowType
         *
         * @return the value of workflowType
         */
        public String getWorkflowType() {
            return workflowType;
        }

        /**
         * Set the value of workflowType
         *
         * @param workflowType new value of workflowType
         */
        public void setWorkflowType(String workflowType) {
            this.workflowType = workflowType;
        }

        /**
         * Get the value of experimentName
         *
         * @return the value of experimentName
         */
        public String getExperimentName() {
            return experimentName;
        }

        /**
         * Set the value of experimentName
         *
         * @param experimentName new value of experimentName
         */
        public void setExperimentName(String experimentName) {
            this.experimentName = experimentName;
        }

        /**
         * Get the value of runName
         *
         * @return the value of runName
         */
        public String getRunName() {
            return runName;
        }

        /**
         * Set the value of runName
         *
         * @param runName new value of runName
         */
        public void setRunName(String runName) {
            this.runName = runName;
        }

        /**
         * Get the value of studyTitle
         *
         * @return the value of studyTitle
         */
        public String getStudyTitle() {
            return studyTitle;
        }

        /**
         * Set the value of studyTitle
         *
         * @param studyTitle new value of studyTitle
         */
        public void setStudyTitle(String studyTitle) {
            this.studyTitle = studyTitle;
        }

        public List<SampleInfo> getSamples() {
            return samples;
        }

        public void setSamples(List<SampleInfo> samples) {
            this.samples = samples;
        }

        @Override
        public String toString() {
            String string = "RunInfo{" + "studyTitle=" + studyTitle + ", runName=" + runName;
            for (SampleInfo sample : samples) {
                string += sample.toString() + "\n";
            }
            string += '}';
            return string;
        }
    }

    protected class SampleInfo {

        private String blank = "";
        private String name = blank;
        private String tissueType = blank;
        private String tissueRegion = blank;
        private String tissueOrigin = blank;
        private String tissuePreparation = blank;
        private String targetedResequencing = blank;
        private String templateType = blank;
        private String lane = blank;
        private String barcode = blank;
        private String organism = blank;
        private String parentSample = blank;

        /**
         * Get the value of parentSample
         *
         * @return the value of parentSample
         */
        public String getParentSample() {
            return parentSample;
        }

        /**
         * Set the value of parentSample
         *
         * @param parentSample new value of parentSample
         */
        public void setParentSample(String parentSample) {
            this.parentSample = parentSample;
        }

        /**
         * Get the value of organism
         *
         * @return the value of organism
         */
        public String getOrganism() {
            return organism;
        }

        /**
         * Set the value of organism
         *
         * @param organism new value of organism
         */
        public void setOrganism(String organism) {
            for (Organism o : metadata.getOrganisms()) {
                if (o.getName().equals(organism)) {
                    this.organism = String.valueOf(o.getOrganismId());
                }
            }
            if (this.organism.isEmpty()) {
                this.organism = organism;
            }
        }

        /**
         * Get the value of barcode
         *
         * @return the value of barcode
         */
        public String getBarcode() {
            return barcode;
        }

        /**
         * Set the value of barcode
         *
         * @param barcode new value of barcode
         */
        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }

        /**
         * Get the value of lane
         *
         * @return the value of lane
         */
        public String getLane() {
            return lane;
        }

        /**
         * Set the value of lane
         *
         * @param lane new value of lane
         */
        public void setLane(String lane) {
            this.lane = lane;
        }

        /**
         * Get the value of templateType
         *
         * @return the value of templateType
         */
        public String getTemplateType() {
            return templateType;
        }

        /**
         * Set the value of templateType
         *
         * @param templateType new value of templateType
         */
        public void setTemplateType(String templateType) {
            this.templateType = templateType;
        }

        /**
         * Get the value of targetedResequencing
         *
         * @return the value of targetedResequencing
         */
        public String getTargetedResequencing() {
            return targetedResequencing;
        }

        /**
         * Set the value of targetedResequencing
         *
         * @param targetedResequencing new value of targetedResequencing
         */
        public void setTargetedResequencing(String targetedResequencing) {
            this.targetedResequencing = targetedResequencing;
        }

        /**
         * Get the value of tissuePreparation
         *
         * @return the value of tissuePreparation
         */
        public String getTissuePreparation() {
            return tissuePreparation;
        }

        /**
         * Set the value of tissuePreparation
         *
         * @param tissuePreparation new value of tissuePreparation
         */
        public void setTissuePreparation(String tissuePreparation) {
            this.tissuePreparation = tissuePreparation;
        }

        /**
         * Get the value of tissueOrigin
         *
         * @return the value of tissueOrigin
         */
        public String getTissueOrigin() {
            return tissueOrigin;
        }

        /**
         * Set the value of tissueOrigin
         *
         * @param tissueOrigin new value of tissueOrigin
         */
        public void setTissueOrigin(String tissueOrigin) {
            this.tissueOrigin = tissueOrigin;
        }

        /**
         * Get the value of tissueRegion
         *
         * @return the value of tissueRegion
         */
        public String getTissueRegion() {
            return tissueRegion;
        }

        /**
         * Set the value of tissueRegion
         *
         * @param tissueRegion new value of tissueRegion
         */
        public void setTissueRegion(String tissueRegion) {
            this.tissueRegion = tissueRegion;
        }

        /**
         * Get the value of tissueType
         *
         * @return the value of tissueType
         */
        public String getTissueType() {
            return tissueType;
        }

        /**
         * Set the value of tissueType
         *
         * @param tissueType new value of tissueType
         */
        public void setTissueType(String tissueType) {
            this.tissueType = tissueType;
        }

        /**
         * Get the value of name
         *
         * @return the value of name
         */
        public String getName() {
            return name;
        }

        /**
         * Set the value of name
         *
         * @param name new value of name
         */
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "SampleInfo{" + "\n\tname=" + name + " \n\ttissueType=" + tissueType
                    + " \n\ttissueRegion=" + tissueRegion + " \n\ttissueOrigin=" + tissueOrigin
                    + " \n\ttissuePreparation=" + tissuePreparation + " \n\ttargetedResequencing=" + targetedResequencing
                    + " \n\ttemplateType=" + templateType + " \n\tlane=" + lane
                    + " \n\tbarcode=" + barcode + " \n\torganism=" + organism + '}';
        }
    }
}
