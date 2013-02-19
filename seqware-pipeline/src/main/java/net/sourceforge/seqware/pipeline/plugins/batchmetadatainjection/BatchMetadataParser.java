/*
 * Copyright (C) 2013 SeqWare
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
package net.sourceforge.seqware.pipeline.plugins.batchmetadatainjection;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import joptsimple.OptionException;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.model.*;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.runtools.ConsoleAdapter;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author mtaschuk
 */
public abstract class BatchMetadataParser {

    public enum Field {
        study_type, platform_id, organism_id, study_name, experiment_name,
        sequencer_run_name, library_strategy_accession, library_source_accession, library_selection_accession,
        library_source_template_type, tissue_origin, tissue_type, library_type, library_size_code, targeted_resequencing, tissue_preparation, run_file_path, barcode
    }
    private static String[] librarySourceTemplateTypeList = new String[]{"CH", "EX", "MR", "SM", "TR", "TS", "WG", "WT", "Other"};
    private static String[] targetedResequencingList = new String[]{"Agilent SureSelect 244k Array",
        "Agilent SureSelect All Exon G3362", "Agilent SureSelect ICGC/Sanger Exon",
        "AmpliSeq Cancer Panel v1", "AmpliSeq Comprehensive Cancer Panel",
        "Illumina TruSeq Exome", "Ion AmpliSeq Cancer Panel v1", "Nimblegen 2.1M Human Exome (21191)",
        "Nimblegen Human Exome v2.0", "Nimblegen ICGC Beta", "Nimblegen OICR Test (13668)",
        "TruSeq Amplicon - Cancer Panel", "Other"};
    private static String[] tissueOriginList = new String[]{"Br", "Cb", "Ep", "Ki", "Li", "Lu",
        "Lv", "Lx", "Ly", "Nk", "nn", "Oc", "Ov", "Pa", "Pr", "Sg", "Sp", "St", "Ta", "Tr", "Wm", "Other"};
    private static String[] tissuePreparationList = new String[]{"Blood", "FFPE", "Fresh Frozen", "Other"};
    private static String[] tissueTypeList = new String[]{"C", "M", "n", "P", "R", "X", "Other"};
    private static String[] libraryTypeList = new String[]{"SE", "PE", "MP", "Other"};
    private String lstety = "";
    private String tare = "";
    private String tior = "";
    private String tipr = "";
    private String tity = "";
    private String lity = "";
    private Integer lSize = 0;
    private Metadata metadata;
    protected final Map<String, String> fields;
    protected boolean interactive;

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
        while (choice == null || choice.isEmpty()) {
            choiceInt = ConsoleAdapter.getInstance().promptInteger(title, choiceInt);
            if (choiceInt == 0) { //no selection
            } else if (choiceInt == choices.length) {
                choice = ConsoleAdapter.getInstance().promptString("Please specify", null);
            } else if (choiceInt <= 0 || choiceInt > choices.length) {
                Log.stdout("Please choose from the given options.");
            } else {
                choice = choices[choiceInt - 1];
            }
        }
        return choice;
    }

    public BatchMetadataParser(Metadata metadata, Map<String, String> fields, boolean interactive) {
        this.metadata = metadata;
        this.fields = fields;
        this.interactive = interactive;
    }

    protected Integer findOrganismId(String organism) {
        Integer organismId = null;
        for (Organism o : metadata.getOrganisms()) {
            if (o.getName().equals(organism)) {
                organismId = o.getOrganismId();
            }
        }
        return organismId;
    }

    protected RunInfo generateRunInfo(String runName, String studyTitle,
            String experimentName, String filePath, int platformId, int studyType) {
        RunInfo runInfo = new RunInfo();


        runInfo.setStudyTitle(prompt("Study Title", studyTitle, Field.study_name));
        runInfo.setRunName(prompt("Sequencer Run Name", runName, Field.sequencer_run_name));
        runInfo.setExperimentName(prompt("Experiment Name", experimentName, Field.experiment_name));
        if (interactive && !fields.containsKey(Field.platform_id.toString())) {
            for (Platform p : metadata.getPlatforms()) {
                Log.stdout(p.toString());
            }
        }
        runInfo.setPlatformId(prompt("Platform accession", platformId, Field.platform_id));
        if (interactive && !fields.containsKey(Field.study_type.toString())) {
            for (StudyType p : metadata.getStudyTypes()) {
                Log.stdout(p.toString());
            }
        }
        runInfo.setStudyType(prompt("Study Type Accession", studyType, Field.study_type));
        runInfo.setRunFilePath(prompt("Sequencer run directory", filePath, Field.run_file_path));

        return runInfo;
    }

    protected LaneInfo generateLaneInfo(String laneNumber, int studyTypeAccession) {
        LaneInfo laneInfo = new LaneInfo();

        laneInfo.setLaneNumber(laneNumber);
        laneInfo.setLaneName(laneInfo.getLaneNumber());
        laneInfo.setLaneDescription(laneInfo.getLaneNumber());
        laneInfo.setLaneSkip(Boolean.FALSE);
        laneInfo.setLaneCycleDescriptor("");
        laneInfo.setStudyTypeAcc(studyTypeAccession);

        if (interactive && !fields.containsKey(Field.library_strategy_accession.toString())) {
            for (LibraryStrategy ls : metadata.getLibraryStrategies()) {
                Log.stdout(ls.toString());
            }
        }
        laneInfo.setLibraryStrategyAcc(prompt("Library Strategy Accession", 0, Field.library_strategy_accession));
        if (interactive && !fields.containsKey(Field.library_selection_accession.toString())) {
            for (LibrarySelection ls : metadata.getLibrarySelections()) {
                Log.stdout(ls.toString());
            }
        }
        laneInfo.setLibrarySelectionAcc(prompt("Library Selection Accession", 0, Field.library_selection_accession));
        if (interactive && !fields.containsKey(Field.library_source_accession.toString())) {
            for (LibrarySource ls : metadata.getLibrarySource()) {
                Log.stdout(ls.toString());
            }
        }
        laneInfo.setLibrarySourceAcc(prompt("Library Source Accession", 0, Field.library_source_accession));


        return laneInfo;
    }

    protected SampleInfo generateSampleInfo(String prettyName, String projectCode, String individualNumber,
            String librarySourceTemplateType, String tissueOrigin, String tissueType, String libraryType, String librarySizeCode,
            String barcode, int organismId, String targetedResequencing, String tissuePreparation) throws Exception {

        if (individualNumber == null || individualNumber.isEmpty() || projectCode == null || projectCode.isEmpty()) {
            throw new Exception("Every sample needs a project code and individual number: You gave me " + projectCode + " and " + individualNumber);
        }
        SampleInfo sa = new SampleInfo();
        sa.setIndividualNumber(individualNumber);
        sa.setProjectCode(projectCode);

        if (libraryType == null || libraryType.isEmpty()) {
            libraryType = prompt(prettyName, "Library Type", libraryTypeList, this.lity, Field.library_type);
            this.lity = libraryType;
        }
        sa.setLibraryType(libraryType);
        
        if (librarySourceTemplateType == null || librarySourceTemplateType.isEmpty()) {
            librarySourceTemplateType = prompt(prettyName, "Library Source Template Type", librarySourceTemplateTypeList, this.lstety, Field.library_source_template_type);
            this.lstety = librarySourceTemplateType;
        }
        sa.setLibrarySourceTemplateType(librarySourceTemplateType);
        
        if (targetedResequencing == null || targetedResequencing.isEmpty()) {
            targetedResequencing = prompt(prettyName, "Targeted Resequencing Type", targetedResequencingList, this.tare, Field.targeted_resequencing);
            this.tare = targetedResequencing;
        }
        sa.setTargetedResequencing(targetedResequencing);
        
        if (tissueOrigin == null || tissueOrigin.isEmpty()) {
            tissueOrigin = prompt(prettyName, "Tissue Origin", tissueOriginList, this.tior, Field.tissue_origin);
            if (tissueOrigin.isEmpty()) {
                tissueOrigin = "nn";
            } else {
                this.tior = tissueOrigin;
            }
        }
        sa.setTissueOrigin(tissueOrigin);
        
        
        if (tissuePreparation == null || tissuePreparation.isEmpty()) {
            tissuePreparation = prompt(prettyName, "Tissue Preparation", tissuePreparationList, this.tipr, Field.tissue_preparation);
            this.tipr = tissuePreparation;
        }
        sa.setTissuePreparation(tissuePreparation);
        
        if (tissueType == null || tissueType.isEmpty()) {
            tissueType = prompt(prettyName, "Tissue Type", tissueTypeList, this.tity, Field.tissue_type);
            if (tissueType.isEmpty()) {
                tissueType = "n";
            } else {
                this.tity = tissueType;
            }
        }
        sa.setTissueType(tissueType);
        
        if (librarySizeCode == null || librarySizeCode.isEmpty() || !StringUtils.isNumeric(librarySizeCode)) {
            Integer lSize = prompt("Library Size Code - a number code indicating the size of the band cut from the gel in base pairs", this.lSize, Field.library_size_code);
            if (lSize <= 0) {
                librarySizeCode = "nn";
            } else {
                this.lSize = lSize;
                librarySizeCode = lSize.toString();
            }
        }
        sa.setLibrarySizeCode(librarySizeCode);
        
        if (barcode == null || barcode.isEmpty()) {
            barcode = prompt("Barcode", "", Field.barcode);
        }
        sa.setBarcode(barcode);
        
        StringBuilder name = new StringBuilder();
        name.append(projectCode).append("_").append(individualNumber);
        name.append("_").append(tissueOrigin).append("_").append(tissueType).append("_").append(libraryType).append("_");
        name.append(librarySizeCode).append("_").append(librarySourceTemplateType);
        sa.setName(name.toString());

        if (organismId <= 0) {
            List<Organism> organisms = new ArrayList<Organism>(metadata.getOrganisms());
            for (int i = 0; i < organisms.size(); i++) {
                Log.stdout(i + " : " + organisms.get(i).toString());
            }
            organismId = prompt("Organism id", 31, Field.organism_id);
        }
        sa.setOrganismId(organismId);

        return sa;
    }

    protected int prompt(String description, int deflt, Field fieldName) throws OptionException {
        Log.debug("checking for field '" + fieldName.toString() + "'");
        if (fields.containsKey(fieldName.toString())) {
            return Integer.parseInt(fields.get(fieldName.toString()));
        } else if (!interactive) {
            if (deflt > 0) {
                return deflt;
            } else {
                throw new OptionException(fields.keySet(), new Exception("A value must be provided for " + fieldName.toString())) {
                };
            }
        } else {
            Integer i = ConsoleAdapter.getInstance().promptInteger(description, deflt);
            fields.put(fieldName.toString(), i.toString());
            return i;
        }
    }

    protected String prompt(String description, String deflt, Field fieldName) throws OptionException {
        Log.debug("checking for field '" + fieldName.toString() + "'");
        if (fields.containsKey(fieldName.toString())) {
            return fields.get(fieldName.toString());
        } else if (!interactive) {
            if (deflt != null && !deflt.trim().isEmpty()) {
                return deflt;
            } else {
                throw new OptionException(fields.keySet(), new Exception("A value must be provided for " + fieldName.toString())) {
                };
            }

        } else {
            String s = ConsoleAdapter.getInstance().promptString(description, deflt);
            fields.put(fieldName.toString(), s);
            return s;
        }
    }

    protected String prompt(String sampleName, String title, String[] choices, String deflt, Field fieldName) {
        Log.debug("checking for field '" + fieldName.toString() + "'");
        if (fields.containsKey(fieldName.toString())) {
            return fields.get(fieldName.toString());
        } else if (!interactive) {
            if (deflt != null && !deflt.trim().isEmpty()) {
                return deflt;
            } else {
                throw new OptionException(fields.keySet(), new Exception("A value must be provided for " + fieldName.toString())) {
                };
            }
        } else {
            String s = choiceOf(sampleName, title, choices, deflt);
            fields.put(fieldName.toString(), s);
            return s;
        }
    }
}
