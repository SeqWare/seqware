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
    private boolean iusSkip=false;
    
    
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
     * Set the value of librarySourceTemplateType
     *
     * @param librarySourceTemplateType new value of librarySourceTemplateType
     */
    public void setLibrarySourceTemplateType(String librarySourceTemplateType) {
        addSampleAttribute("geo_library_source_template_type", librarySourceTemplateType);
        this.librarySourceTemplateType = librarySourceTemplateType;
    }

    public String getLibrarySizeCode() {
        return librarySizeCode;
    }

    public void setLibrarySizeCode(int librarySizeCode) {
        this.librarySizeCode = String.valueOf(librarySizeCode);
    }


    public String getLibraryType() {
        return libraryType;
    }

    public void setLibraryType(String libraryType) {
        this.libraryType = libraryType;
    }

    public String getOrganismId() {
        return organismId;
    }

    public void setOrganismId(int organismId) {
        this.organismId = String.valueOf(organismId);
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

    public void addIUSAttribute(String tag, String value) {
        IUSAttribute ia = new IUSAttribute();
        ia.setTag(tag);
        ia.setValue(value);
        getIusAttributes().add(ia);
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

    public void addSampleAttribute(String tag, String value) {
        SampleAttribute sa = new SampleAttribute();
        sa.setTag(tag);
        sa.setValue(value);
        getSampleAttributes().add(sa);
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
     * Set the value of targetedResequencing
     *
     * @param targetedResequencing new value of targetedResequencing
     */
    public void setTargetedResequencing(String targetedResequencing) {
        addSampleAttribute("geo_targeted_resequencing", targetedResequencing);
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
        addSampleAttribute("geo_tissue_preparation", tissuePreparation);
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
        addSampleAttribute("geo_tissue_origin", tissueOrigin);
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
     * Set the value of tissueType
     *
     * @param tissueType new value of tissueType
     */
    public void setTissueType(String tissueType) {
        addSampleAttribute("geo_tissue_type", tissueType);
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

}
