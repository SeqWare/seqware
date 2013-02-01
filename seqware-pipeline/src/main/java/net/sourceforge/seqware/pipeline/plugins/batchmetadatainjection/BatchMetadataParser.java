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

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.model.Organism;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.runtools.ConsoleAdapter;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author mtaschuk
 */
public abstract class BatchMetadataParser {

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
        choiceInt = ConsoleAdapter.getInstance().promptInteger(title, choiceInt);
        if (choiceInt == 0) { //no selection
        } else if (choiceInt == choices.length) {
            choice = ConsoleAdapter.getInstance().promptString("Please specify", null);
        } else if (choiceInt <= 0 || choiceInt > choices.length) {
            Log.stdout("Please choose from the given options.");
            choice = choiceOf(sampleName, title, choices, deflt);
        } else {
            choice = choices[choiceInt - 1];
        }
        return choice;
    }

    public BatchMetadataParser(Metadata metadata) {
        this.metadata = metadata;
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

    protected LaneInfo generateLaneInfo(String laneNumber, int studyTypeAccession) {
        LaneInfo laneInfo = new LaneInfo();

        laneInfo.setLaneNumber(laneNumber);
        laneInfo.setLaneName(laneInfo.getLaneNumber());
        laneInfo.setLaneDescription(laneInfo.getLaneNumber());
        laneInfo.setLaneSkip(Boolean.FALSE);
        laneInfo.setLaneCycleDescriptor("");
        laneInfo.setStudyTypeAcc(studyTypeAccession);

        return laneInfo;
    }

    protected SampleInfo generateSampleInfo(String prettyName, String projectCode, String individualNumber,
            String librarySourceTemplateType, String tissueOrigin, String tissueType, String libraryType, String librarySizeCode,
            String barcode, Integer organismId, String targetedResequencing, String tissuePreparation) throws Exception {

        if (individualNumber == null || individualNumber.isEmpty() || projectCode == null || projectCode.isEmpty()) {
            throw new Exception("Every sample needs a project code and individual number: You gave me " + projectCode + " and " + individualNumber);
        }
        SampleInfo sa = new SampleInfo();
        sa.setIndividualNumber(individualNumber);
        sa.setProjectCode(projectCode);

        if (libraryType == null || libraryType.isEmpty()) {
            libraryType = choiceOf(prettyName, "Library Type", libraryTypeList, this.lity);
            this.lity = libraryType;
        }
        if (librarySourceTemplateType == null || librarySourceTemplateType.isEmpty()) {
            librarySourceTemplateType = choiceOf(prettyName, "Library Source Template Type", librarySourceTemplateTypeList, this.lstety);
            this.lstety = librarySourceTemplateType;
        }
        if (targetedResequencing == null || targetedResequencing.isEmpty()) {
            targetedResequencing = choiceOf(prettyName, "Targeted Resequencing Type", targetedResequencingList, this.tare);
            this.tare = targetedResequencing;
        }
        if (tissueOrigin == null || tissueOrigin.isEmpty()) {
            tissueOrigin = choiceOf(prettyName, "Tissue Origin", tissueOriginList, this.tior);
            if (tissueOrigin.isEmpty()) {
                tissueOrigin = "nn";
            } else {
                this.tior = tissueOrigin;
            }
        }
        if (tissueType == null || tissuePreparation.isEmpty()) {
            tissuePreparation = choiceOf(prettyName, "Tissue Preparation", tissuePreparationList, this.tipr);
            this.tipr = tissuePreparation;
        }
        if (tissueType == null || tissueType.isEmpty()) {
            tissueType = choiceOf(prettyName, "Tissue Type", tissueTypeList, this.tity);

            if (tissueType.isEmpty()) {
                tissueType = "n";
            } else {
                this.tity = tissueType;

            }
        }
        if (librarySizeCode == null || librarySizeCode.isEmpty() || !StringUtils.isNumeric(librarySizeCode)) {
            Integer lSize = ConsoleAdapter.getInstance().promptInteger("Library Size Code - a number code indicating the size of the band cut from the gel in base pairs", this.lSize);
            if (lSize <= 0) {
                librarySizeCode = "nn";
            } else {
                this.lSize = lSize;
                librarySizeCode = lSize.toString();
            }
        }
        StringBuilder name = new StringBuilder();
        name.append(projectCode).append("_").append(individualNumber);
        name.append("_").append(tissueOrigin).append("_").append(tissueType).append("_").append(libraryType).append("_");
        name.append(librarySizeCode).append("_").append(librarySourceTemplateType);

        sa.setName(name.toString());
        sa.setTissueOrigin(tissueOrigin);
        sa.setTissuePreparation(tissuePreparation);
        sa.setTissueType(tissueType);


        if (barcode == null || barcode.isEmpty()) {
            barcode = ConsoleAdapter.getInstance().promptString("Barcode", "");
        }
        sa.setBarcode(barcode);

        if (organismId == null || organismId <= 0) {
            List<Organism> organisms = new ArrayList<Organism>(metadata.getOrganisms());
            for (int i = 0; i < organisms.size(); i++) {
                Log.stdout(i + " : " + organisms.get(i).toString());
            }
            organismId = ConsoleAdapter.getInstance().promptInteger("Organism id", 31);
        }
        sa.setOrganismId(organismId);

        return sa;
    }
}
