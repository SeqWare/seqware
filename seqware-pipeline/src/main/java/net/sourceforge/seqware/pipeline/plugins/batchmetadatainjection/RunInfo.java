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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sourceforge.seqware.common.model.ExperimentAttribute;
import net.sourceforge.seqware.common.model.LaneAttribute;
import net.sourceforge.seqware.common.model.SequencerRunAttribute;
import net.sourceforge.seqware.common.model.StudyAttribute;

/**
 *
 * @author mtaschuk
 */
public class RunInfo {

    //study
    private String studyTitle;
    private String studyDescription;
    private String studyCenterName;
    private String studyCenterProject;
    //study and lane
    private String studyType;
    //Used for experiment and for sequencer run
    private String platformId;
    //sequencer_run
    private String runName;
    
    //experiment
    private String experimentName;
    private String workflowType;
    private String assayType;
    //samples, lanes, barcodes
    private List<SampleInfo> samples = null;
    private Set<ExperimentAttribute> experimentAttributes;
    private Set<StudyAttribute> studyAttributes;
    private Set<SequencerRunAttribute> runAttributes;

    private Set<LaneInfo> lanes;

    private boolean runSkip=false;
    private boolean pairedEnd=true;
    private String runDescription;
    private String runFilePath;
    private String experimentDescription;
    
    /**
     * Get the value of runFilePath
     *
     * @return the value of runFilePath
     */
    public String getRunFilePath() {
        return runFilePath;
    }

    /**
     * Set the value of runFilePath
     *
     * @param runFilePath new value of runFilePath
     */
    public void setRunFilePath(String runFilePath) {
        this.runFilePath = runFilePath;
    }

    /**
     * Get the value of experimentDescription
     *
     * @return the value of experimentDescription
     */
    public String getExperimentDescription() {
        return experimentDescription;
    }

    /**
     * Set the value of experimentDescription
     *
     * @param experimentDescription new value of experimentDescription
     */
    public void setExperimentDescription(String experimentDescription) {
        this.experimentDescription = experimentDescription;
    }

    /**
     * Get the value of runDescription
     *
     * @return the value of runDescription
     */
    public String getRunDescription() {
        return runDescription;
    }

    /**
     * Set the value of runDescription
     *
     * @param runDescription new value of runDescription
     */
    public void setRunDescription(String runDescription) {
        this.runDescription = runDescription;
    }

    /**
     * Get the value of pairedEnd
     *
     * @return the value of pairedEnd
     */
    public boolean isPairedEnd() {
        return pairedEnd;
    }

    /**
     * Set the value of pairedEnd
     *
     * @param pairedEnd new value of pairedEnd
     */
    public void setPairedEnd(boolean pairedEnd) {
        this.pairedEnd = pairedEnd;
    }

    /**
     * Get the value of runSkip
     *
     * @return the value of runSkip
     */
    public boolean getRunSkip() {
        return runSkip;
    }

    /**
     * Set the value of runSkip
     *
     * @param runSkip new value of runSkip
     */
    public void setRunSkip(boolean runSkip) {
        this.runSkip = runSkip;
    }

    
    /**
     * Get the value of lanes
     *
     * @return the value of lanes
     */
    public Set<LaneInfo> getLanes() {
        if (lanes == null)
            lanes = new HashSet<LaneInfo>();
        return lanes;
    }

    /**
     * Set the value of lanes
     *
     * @param lanes new value of lanes
     */
    public void setLanes(Set<LaneInfo> lanes) {
        this.lanes = lanes;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(int platformId) {
        this.platformId = String.valueOf(platformId);
    }

    public String getStudyType() {
        return studyType;
    }

    public void setStudyType(int studyType) {
        this.studyType = String.valueOf(studyType);
    }

    /**
     * Get the value of runAttributes
     *
     * @return the value of runAttributes
     */
    public Set<SequencerRunAttribute> getRunAttributes() {
        if (runAttributes == null) {
            runAttributes = new HashSet<SequencerRunAttribute>();
        }
        return runAttributes;
    }

    public void addRunAttribute(String tag, String value) {
        SequencerRunAttribute sra = new SequencerRunAttribute();
        sra.setTag(tag);
        sra.setValue(value);
        getRunAttributes().add(sra);
    }

    /**
     * Set the value of runAttributes
     *
     * @param runAttributes new value of runAttributes
     */
    public void setRunAttributes(Set<SequencerRunAttribute> runAttributes) {
        this.runAttributes = runAttributes;
    }

    /**
     * Get the value of studyAttributes
     *
     * @return the value of studyAttributes
     */
    public Set<StudyAttribute> getStudyAttributes() {
        if (studyAttributes == null) {
            studyAttributes = new HashSet<StudyAttribute>();
        }
        return studyAttributes;
    }

    public void addStudyAttribute(String tag, String value) {
        StudyAttribute sa = new StudyAttribute();
        sa.setTag(tag);
        sa.setValue(value);
        getStudyAttributes().add(sa);
    }

    /**
     * Set the value of studyAttributes
     *
     * @param studyAttributes new value of studyAttributes
     */
    public void setStudyAttributes(Set<StudyAttribute> studyAttributes) {
        this.studyAttributes = studyAttributes;
    }

    /**
     * Get the value of experimentAttributes
     *
     * @return the value of experimentAttributes
     */
    public Set<ExperimentAttribute> getExperimentAttributes() {
        if (experimentAttributes == null) {
            experimentAttributes = new HashSet<ExperimentAttribute>();
        }
        return experimentAttributes;
    }

    /**
     * Set the value of experimentAttributes
     *
     * @param experimentAttributes new value of experimentAttributes
     */
    public void setExperimentAttributes(Set<ExperimentAttribute> experimentAttributes) {
        this.experimentAttributes = experimentAttributes;
    }

    /**
     * Get the value of studyCenterProject
     *
     * @return the value of studyCenterProject
     */
    public String getStudyCenterProject() {
        return studyCenterProject;
    }

    /**
     * Set the value of studyCenterProject
     *
     * @param studyCenterProject new value of studyCenterProject
     */
    public void setStudyCenterProject(String studyCenterProject) {
        this.studyCenterProject = studyCenterProject;
    }

    /**
     * Get the value of studyCenterName
     *
     * @return the value of studyCenterName
     */
    public String getStudyCenterName() {
        return studyCenterName;
    }

    /**
     * Set the value of studyCenterName
     *
     * @param studyCenterName new value of studyCenterName
     */
    public void setStudyCenterName(String studyCenterName) {
        this.studyCenterName = studyCenterName;
    }

    /**
     * Get the value of studyDescription
     *
     * @return the value of studyDescription
     */
    public String getStudyDescription() {
        return studyDescription;
    }

    /**
     * Set the value of studyDescription
     *
     * @param studyDescription new value of studyDescription
     */
    public void setStudyDescription(String studyDescription) {
        this.studyDescription = studyDescription;
    }

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
