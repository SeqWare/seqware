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
import java.util.Map;
import java.util.Set;
import net.sourceforge.seqware.common.model.LaneAttribute;

/**
 *
 * @author mtaschuk
 */
public class LaneInfo {

    private String laneNumber = null;
    private String laneName = null;
    private String laneDescription = null;
    private String laneCycleDescriptor = null;
    private Boolean laneSkip = false;
    private String libraryStrategyAcc = null;
    private String librarySourceAcc = null;
    private String librarySelectionAcc = null;
    private String studyTypeAcc = null;
    private Set<SampleInfo> samples;
    private Set<LaneAttribute> laneAttributes;

    /**
     * Get the value of studyTypeAcc
     *
     * @return the value of studyTypeAcc
     */
    public String getStudyTypeAcc() {
        return studyTypeAcc;
    }

    /**
     * Set the value of studyTypeAcc
     *
     * @param studyTypeAcc new value of studyTypeAcc
     */
    public void setStudyTypeAcc(int studyTypeAcc) {
        this.studyTypeAcc = String.valueOf(studyTypeAcc);
    }

    public Set<LaneAttribute> getLaneAttributes() {
        if (laneAttributes == null) {
            laneAttributes = new HashSet<LaneAttribute>();
        }
        return laneAttributes;
    }

    /**
     * Adds a new lane attribute if the tag does not exist for this lane, or
     * changes the value of an existing lane attribute.
     *
     * @param tag the key of the attribute
     * @param value the value of the attribute
     */
    public void setLaneAttribute(String tag, String value) {

        LaneAttribute sa = null;
        for (LaneAttribute s : getLaneAttributes()) {
            if (s.getTag().equals(tag.trim())) {
                sa = s;
                break;
            }
        }
        //if we are unsetting the lane attribute, remove it from the list.
        if (value == null && sa !=null ) {
            getLaneAttributes().remove(sa);
            return;
        }
        if (sa == null) {
            sa = new LaneAttribute();
            getLaneAttributes().add(sa);
        }
        sa.setTag(tag.trim());
        sa.setValue(value.trim());
    }

    public void setLaneAttributes(Set<LaneAttribute> laneAttributes) {
        this.laneAttributes = laneAttributes;
    }

    /**
     * Get the value of librarySelectionAcc
     *
     * @return the value of librarySelectionAcc
     */
    public String getLibrarySelectionAcc() {
        return librarySelectionAcc;
    }

    /**
     * Set the value of librarySelectionAcc
     *
     * @param librarySelectionAcc new value of librarySelectionAcc
     */
    public void setLibrarySelectionAcc(int librarySelectionAcc) {
        this.librarySelectionAcc = String.valueOf(librarySelectionAcc);
    }

    /**
     * Get the value of librarySourceAcc
     *
     * @return the value of librarySourceAcc
     */
    public String getLibrarySourceAcc() {
        return librarySourceAcc;
    }

    /**
     * Set the value of librarySourceAcc
     *
     * @param librarySourceAcc new value of librarySourceAcc
     */
    public void setLibrarySourceAcc(int librarySourceAcc) {
        this.librarySourceAcc = String.valueOf(librarySourceAcc);
    }

    /**
     * Get the value of libraryStrategyAcc
     *
     * @return the value of libraryStrategyAcc
     */
    public String getLibraryStrategyAcc() {
        return libraryStrategyAcc;
    }

    /**
     * Set the value of libraryStrategyAcc
     *
     * @param libraryStrategyAcc new value of libraryStrategyAcc
     */
    public void setLibraryStrategyAcc(int libraryStrategyAcc) {
        this.libraryStrategyAcc = String.valueOf(libraryStrategyAcc);
    }

    /**
     * Get the value of samples
     *
     * @return the value of samples
     */
    public Set<SampleInfo> getSamples() {
        if (samples == null) {
            samples = new HashSet<SampleInfo>();
        }
        return samples;
    }

    /**
     * Set the value of samples
     *
     * @param samples new value of samples
     */
    public void setSamples(Set<SampleInfo> samples) {
        this.samples = samples;
    }

    /**
     * Get the value of laneSkip
     *
     * @return the value of laneSkip
     */
    public Boolean getLaneSkip() {
        return laneSkip;
    }

    /**
     * Set the value of laneSkip
     *
     * @param laneSkip new value of laneSkip
     */
    public void setLaneSkip(Boolean laneSkip) {
        this.laneSkip = laneSkip;
    }

    /**
     * Get the value of laneCycleDescriptor
     *
     * @return the value of laneCycleDescriptor
     */
    public String getLaneCycleDescriptor() {
        return laneCycleDescriptor;
    }

    /**
     * Set the value of laneCycleDescriptor
     *
     * @param laneCycleDescriptor new value of laneCycleDescriptor
     */
    public void setLaneCycleDescriptor(String laneCycleDescriptor) {
        this.laneCycleDescriptor = laneCycleDescriptor;
    }

    /**
     * Get the value of laneDescription
     *
     * @return the value of laneDescription
     */
    public String getLaneDescription() {
        return laneDescription;
    }

    /**
     * Set the value of laneDescription
     *
     * @param laneDescription new value of laneDescription
     */
    public void setLaneDescription(String laneDescription) {
        this.laneDescription = laneDescription;
    }

    /**
     * Get the value of laneName
     *
     * @return the value of laneName
     */
    public String getLaneName() {
        return laneName;
    }

    /**
     * Set the value of laneName
     *
     * @param laneName new value of laneName
     */
    public void setLaneName(String laneName) {
        this.laneName = laneName;
    }

    /**
     * Get the value of laneNumber
     *
     * @return the value of laneNumber
     */
    public String getLaneNumber() {
        return laneNumber;
    }

    /**
     * Set the value of laneNumber, and sets the 'geo_lane' attribute.
     *
     * @param laneNumber new value of laneNumber
     */
    public void setLaneNumber(String laneNumber) {
        setLaneAttribute("geo_lane", laneNumber);
        this.laneNumber = laneNumber;
    }

    @Override
    public String toString() {
        return "LaneInfo{" + "\n\tlaneNumber=" + laneNumber+ "\n\t laneCycleDescriptor=" + laneCycleDescriptor+ "\n\t libraryStrategyAcc=" + libraryStrategyAcc+ "\n\t librarySourceAcc=" + librarySourceAcc+ "\n\t librarySelectionAcc=" + librarySelectionAcc+ "\n\t studyTypeAcc=" + studyTypeAcc + '}';
    }
    
    
}
