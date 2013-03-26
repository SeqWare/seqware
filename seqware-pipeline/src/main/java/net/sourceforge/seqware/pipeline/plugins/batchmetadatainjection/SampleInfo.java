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

import java.util.HashSet;
import java.util.Set;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.IUSAttribute;
import net.sourceforge.seqware.common.model.SampleAttribute;

/**
 *
 * @author mtaschuk
 */
public class SampleInfo {

    private String blank = "";
    //sample required
    private String projectCode = blank;
    private String individualNumber = blank;
    private String name = blank;
    private String tissueType = blank;
    private String tissueOrigin = blank;
//    private String templateType = blank;
    private String librarySizeCode;
    private String organismId;
    private String librarySourceTemplateType;
    private String parentSample = blank;
    //required: determine pairedEnd from libraryType
    private String libraryType = blank;
    private Boolean pairedEnd;
    //sample optional
    private String tissuePreparation = blank;
    private String targetedResequencing = blank;
    private String sampleDescription;
    //ius    
    private String barcode = blank;
    private String iusName;
    private String iusDescription;
    private boolean iusSkip = false;
    private Set<SampleAttribute> sampleAttributes;
    private Set<IUSAttribute> iusAttributes;

    public String getIndividualNumber() {
        return individualNumber;
    }

    public void setIndividualNumber(String individualNumber) {
        this.individualNumber = individualNumber;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectName) {
        this.projectCode = projectName;
    }

    /**
     * Get the value of iusSkip
     *
     * @return the value of iusSkip
     */
    public boolean getIusSkip() {
        return iusSkip;
    }

    /**
     * Set the value of iusSkip
     *
     * @param iusSkip new value of iusSkip
     */
    public void setIusSkip(boolean iusSkip) {
        this.iusSkip = iusSkip;
    }

    /**
     * Get the value of iusDescription
     *
     * @return the value of iusDescription
     */
    public String getIusDescription() {
        return iusDescription;
    }

    /**
     * Set the value of iusDescription
     *
     * @param iusDescription new value of iusDescription
     */
    public void setIusDescription(String iusDescription) {
        this.iusDescription = iusDescription;
    }

    /**
     * Get the value of iusName
     *
     * @return the value of iusName
     */
    public String getIusName() {
        return iusName;
    }

    /**
     * Set the value of iusName
     *
     * @param iusName new value of iusName
     */
    public void setIusName(String iusName) {
        this.iusName = iusName;
    }

    /**
     * Get the value of sampleDescription
     *
     * @return the value of sampleDescription
     */
    public String getSampleDescription() {
        return sampleDescription;
    }

    /**
     * Set the value of sampleDescription
     *
     * @param sampleDescription new value of sampleDescription
     */
    public void setSampleDescription(String sampleDescription) {
        this.sampleDescription = sampleDescription;
    }

    /**
     * Get the value of librarySourceTemplateType
     *
     * @return the value of librarySourceTemplateType
     */
    public String getLibrarySourceTemplateType() {
        return librarySourceTemplateType;
    }

    /**
     * Set the value of librarySourceTemplateType sets the
     * 'geo_library_source_template_type' attribute.
     *
     * @param librarySourceTemplateType new value of librarySourceTemplateType
     */
    public void setLibrarySourceTemplateType(String librarySourceTemplateType) {
        setSampleAttribute("geo_library_source_template_type", librarySourceTemplateType);
        this.librarySourceTemplateType = librarySourceTemplateType;
    }

    public String getLibrarySizeCode() {
        return librarySizeCode;
    }

    /**
     * Sets the library size code and sets the 'geo_library_size_code'
     * attribute.
     *
     * @param librarySizeCode
     */
    public void setLibrarySizeCode(String librarySizeCode) {
        setSampleAttribute("geo_library_size_code", String.valueOf(librarySizeCode));
        this.librarySizeCode = librarySizeCode;
    }

    public String getLibraryType() {
        return libraryType;
    }

    /**
     * Sets the library type and sets the 'geo_library_type' attribute .
     *
     * @param libraryType
     */
    public void setLibraryType(String libraryType) {
        setSampleAttribute("geo_library_type", String.valueOf(librarySizeCode));
        this.libraryType = libraryType;
    }

    public String getOrganismId() {
        return organismId;
    }

    /**
     * Sets the organismId, as long as the organism id is over 0.
     *
     * @param organismId
     */
    public void setOrganismId(int organismId) {
        if (organismId > 0) {
            this.organismId = String.valueOf(organismId);
        }
    }

    public Boolean getPairedEnd() {
        return pairedEnd;
    }

    public void setPairedEnd(Boolean pairedEnd) {
        this.pairedEnd = pairedEnd;
    }

    /**
     * Get the value of iusAttributes
     *
     * @return the value of iusAttributes
     */
    public Set<IUSAttribute> getIusAttributes() {
        if (iusAttributes == null) {
            iusAttributes = new HashSet<IUSAttribute>();
        }
        return iusAttributes;
    }

    /**
     * Adds a new ius attribute if the tag does not exist for this ius, or
     * changes the value of an existing ius attribute.
     *
     * @param tag the key of the attribute
     * @param value the value of the attribute
     */
    public void setIusAttribute(String tag, String value) {
        IUSAttribute sa = null;
        //look for the existing attribute
        for (IUSAttribute s : getIusAttributes()) {
            if (s.getTag().equals(tag.trim())) {
                sa = s;
                break;
            }
        }
        //if we are unsetting the sample attribute, remove it from the list.
        if (value == null && sa != null) {
            getIusAttributes().remove(sa);
            return;
        }
        //create a new one if it doesn't exist
        if (sa == null) {
            sa = new IUSAttribute();
            getIusAttributes().add(sa);
        }
        sa.setTag(tag.trim());
        sa.setValue(value.trim());
    }

    /**
     * Set the value of iusAttributes
     *
     * @param iusAttributes new value of iusAttributes
     */
    public void setIusAttributes(Set<IUSAttribute> iusAttributes) {
        this.iusAttributes = iusAttributes;
    }

    /**
     * Get the value of sampleAttributes
     *
     * @return the value of sampleAttributes
     */
    public Set<SampleAttribute> getSampleAttributes() {
        if (sampleAttributes == null) {
            sampleAttributes = new HashSet<SampleAttribute>();
        }
        return sampleAttributes;
    }

    /**
     * Adds a new sample attribute if the tag does not exist for this sample, or
     * changes the value of an existing sample attribute.
     *
     * @param tag the key of the attribute
     * @param value the value of the attribute
     */
    public void setSampleAttribute(String tag, String value) {
        SampleAttribute sa = null;
        //look for the existing attribute
        for (SampleAttribute s : getSampleAttributes()) {
            if (s.getTag().equals(tag.trim())) {
                sa = s;
                break;
            }
        }
        //if we are unsetting the sample attribute, remove it from the list.
        if (value == null) {
            if (sa != null) {
                getSampleAttributes().remove(sa);
            }
            return;
        }
        if (sa == null) {
            sa = new SampleAttribute();
            getSampleAttributes().add(sa);
        }
        sa.setTag(tag.trim());
        sa.setValue(value.trim());
    }

    /**
     * Set the value of sampleAttributes
     *
     * @param sampleAttributes new value of sampleAttributes
     */
    public void setSampleAttributes(Set<SampleAttribute> sampleAttributes) {
        this.sampleAttributes = sampleAttributes;
    }

    public SampleInfo() {
    }

    /**
     * Get the value of parentSample
     *
     * @return the value of parentSample
     */
    public String getParentSample() {
        return new StringBuilder().append(projectCode).append("_").append(individualNumber).toString();
    }

//    /**
//     * Set the value of parentSample
//     *
//     * @param parentSample new value of parentSample
//     */
//    public void setParentSample(String parentSample) {
//        this.parentSample = parentSample;
//    }
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
     * Get the value of targetedResequencing
     *
     * @return the value of targetedResequencing
     */
    public String getTargetedResequencing() {
        return targetedResequencing;
    }

    /**
     * Set the value of targetedResequencing and sets the
     * 'geo_targeted_resequencing' attribute.
     *
     * @param targetedResequencing new value of targetedResequencing
     */
    public void setTargetedResequencing(String targetedResequencing) {
        setSampleAttribute("geo_targeted_resequencing", targetedResequencing);
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
     * Set the value of tissuePreparation, and sets the 'geo_tissue_preparation'
     * attribute.
     *
     * @param tissuePreparation new value of tissuePreparation
     */
    public void setTissuePreparation(String tissuePreparation) {
        setSampleAttribute("geo_tissue_preparation", tissuePreparation);
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
     * Set the value of tissueOrigin and sets the 'geo_tissue_origin' attribute.
     *
     * @param tissueOrigin new value of tissueOrigin
     */
    public void setTissueOrigin(String tissueOrigin) {
        setSampleAttribute("geo_tissue_origin", tissueOrigin);
        this.tissueOrigin = tissueOrigin;
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
     * Set the value of tissueType and sets the 'geo_tissue_type' attribute.
     *
     * @param tissueType new value of tissueType
     */
    public void setTissueType(String tissueType) {
        setSampleAttribute("geo_tissue_type", tissueType);
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
        return "SampleInfo{" + "projectCode=" + projectCode+ "\n\t individualNumber=" + individualNumber+ "\n\t name=" + name+ "\n\t tissueType=" + tissueType+ "\n\t tissueOrigin=" + tissueOrigin+ "\n\t librarySizeCode=" + librarySizeCode+ "\n\t organismId=" + organismId+ "\n\t librarySourceTemplateType=" + librarySourceTemplateType+ "\n\t parentSample=" + parentSample+ "\n\t libraryType=" + libraryType+ "\n\t pairedEnd=" + pairedEnd+ "\n\t tissuePreparation=" + tissuePreparation+ "\n\t targetedResequencing=" + targetedResequencing+ "\n\t sampleDescription=" + sampleDescription+ "\n\t barcode=" + barcode+ "\n\t iusName=" + iusName+ "\n\t iusDescription=" + iusDescription+ "\n\t iusSkip=" + iusSkip+ "\n\t sampleAttributes=" + sampleAttributes+ "\n\t iusAttributes=" + iusAttributes + '}';
    }
}
