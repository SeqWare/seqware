package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import net.sourceforge.seqware.common.security.PermissionsAware;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.jsontools.JsonUtil;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

@XmlRootElement
/**
 * <p>Lane class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class Lane implements Serializable, Comparable<Lane>, PermissionsAware {

    private static final long serialVersionUID = 5681328115923390568L;
    private Integer laneId;
    private Integer laneIndex;
    private SequencerRun sequencerRun;
    private Sample sample;
    private String name;
    private String cycleDescriptor;
    // private String sampleName;
    private String sampleCode;
    private String description;
    private String organism;
    private String sampleType;
    private String tags;
    private String regions;
    private String skipTxt;
    private Boolean skip;
    private Registration owner;
    private Date createTimestamp;
    private Date updateTimestamp;
    private Set<Processing> processings = new TreeSet<Processing>();
    private Integer swAccession;
    private SortedSet<IUS> ius = new TreeSet<IUS>();
    private Set<WorkflowRun> workflowRuns = new TreeSet<WorkflowRun>();
    private Set<LaneLink> laneLinks = new TreeSet<LaneLink>();
    private Set<LaneAttribute> laneAttributes = new TreeSet<LaneAttribute>();
    private Boolean isSelected = false;
    private Boolean isHasFile = false;
    private StudyType studyType;
    private LibraryStrategy libraryStrategy;
    private LibrarySelection librarySelection;
    private LibrarySource librarySource;
    

    /**
     * <p>Constructor for Lane.</p>
     */
    public Lane() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(Lane that) {
        if (that == null || getLaneId() == null) {
            return -1;
        }

        if (that.getLaneId() == this.getLaneId()) // when both names are null
        {
            return 0;
        }

        if (that.getLaneId() == null) {
            return -1; // when only the other name is null
        }
        return (that.getLaneId().compareTo(this.getLaneId()));
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Lane{" + "laneId=" + laneId + ", laneIndex=" + laneIndex + ", sequencerRun=" + sequencerRun + ", sample="
                + sample + ", name=" + name + ", cycleDescriptor=" + cycleDescriptor + ", sampleCode=" + sampleCode
                + ", description=" + description + ", organism=" + organism + ", sampleType=" + sampleType + ", tags=" + tags
                + ", regions=" + regions + ", skipTxt=" + skipTxt + ", skip=" + skip + ", owner=" + owner
                + ", createTimestamp=" + createTimestamp + ", updateTimestamp=" + updateTimestamp + ", swAccession="
                + swAccession + ", isSelected=" + isSelected + ", isHasFile=" + isHasFile + '}';
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object other) {
        if ((this == other)) {
            return true;
        }
        if (!(other instanceof Lane)) {
            return false;
        }
        Lane castOther = (Lane) other;
        return new EqualsBuilder().append(this.getLaneId(), castOther.getLaneId()).isEquals();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getLaneId()).toHashCode();
    }

    /*
     * public int compareTo(Lane that) { if(that == null || getSwAccession() ==
     * null) return -1;
     *
     * if(that.getSwAccession() == this.getSwAccession()) // when both names are
     * null return 0;
     *
     * if(that.getSwAccession() == null) return -1; // when only the other name
     * is null
     *
     * return(that.getSwAccession().compareTo(this.getSwAccession())); }
     *
     * public String toString() { return new ToStringBuilder(this)
     * .append("laneId", getLaneId()) .toString(); }
     *
     * public boolean equals(Object other) { if ( (this == other ) ) return
     * true; if ( !(other instanceof Lane) ) return false; Lane castOther =
     * (Lane) other; return new EqualsBuilder() .append(this.getSwAccession(),
     * castOther.getSwAccession()) .isEquals(); }
     *
     * public int hashCode() { return new HashCodeBuilder()
     * .append(getSwAccession()) .toHashCode(); }
     */
    /**
     * <p>getErrorCnt.</p>
     *
     * @return a int.
     */
    public int getErrorCnt() {
        int errorCnt = 0;
        for (Processing proc : getAllProcessings()) {
            errorCnt += recursiveCountProcErrors(proc);
        }
        return (errorCnt);
    }

    private int recursiveCountProcErrors(Processing proc) {
        int errorCnt = 0;
        if (proc != null && (ProcessingStatus.failed == proc.getStatus())) {
            errorCnt++;
        }
        for (Processing childProc : proc.getChildren()) {
            errorCnt += recursiveCountProcErrors(childProc);
        }
        return (errorCnt);
    }

    /**
     * <p>getProcessingCnt.</p>
     *
     * @return a int.
     */
    public int getProcessingCnt() {
        int cnt = 0;
        for (Processing proc : getAllProcessings()) {
            cnt += recursiveCountProcRunning(proc);
        }
        return (cnt);
    }

    private int recursiveCountProcRunning(Processing proc) {
        int runCnt = 0;
        if (proc != null
                && (proc.getStatus() == ProcessingStatus.running || proc.getStatus() == ProcessingStatus.pending)) {
            runCnt++;
        }
        for (Processing childProc : proc.getChildren()) {
            runCnt += recursiveCountProcRunning(childProc);
        }
        return (runCnt);
    }

    /**
     * <p>getProcessedCnt.</p>
     *
     * @return a int.
     */
    public int getProcessedCnt() {
        int cnt = 0;
        for (Processing proc : getAllProcessings()) {
            cnt += recursiveCountProcessed(proc);
        }
        return (cnt);
    }

    /**
     * <p>getAllProcessings.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<Processing> getAllProcessings() {
        Set<Processing> allProcessing = new TreeSet<Processing>();
        allProcessing.addAll(getProcessings());
        if (getIUS() != null) {
            for (IUS i : getIUS()) {
                allProcessing.addAll(i.getProcessings());
            }
        }
        return allProcessing;
    }

    private int recursiveCountProcessed(Processing proc) {
        int runCnt = 0;
        if (proc != null && (proc.getStatus() == ProcessingStatus.success)) {
            runCnt++;
        }
        for (Processing childProc : proc.getChildren()) {
            runCnt += recursiveCountProcessed(childProc);
        }
        return (runCnt);
    }

    /**
     * <p>Getter for the field <code>ius</code>.</p>
     *
     * @return a {@link java.util.SortedSet} object.
     */
    public SortedSet<IUS> getIus() {
        return ius;
    }

    /**
     * <p>getIUS.</p>
     *
     * @return a {@link java.util.SortedSet} object.
     */
    public SortedSet<IUS> getIUS() {
        return ius;
    }

    /**
     * <p>setIUS.</p>
     *
     * @param ius a {@link java.util.SortedSet} object.
     */
    public void setIUS(SortedSet<IUS> ius) {
        this.ius = ius;
    }

    /**
     * <p>Getter for the field <code>laneIndex</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getLaneIndex() {
        return laneIndex;
    }

    /**
     * <p>Setter for the field <code>laneIndex</code>.</p>
     *
     * @param laneIndex a {@link java.lang.Integer} object.
     */
    public void setLaneIndex(Integer laneIndex) {
        this.laneIndex = laneIndex;
    }

    /**
     * <p>Getter for the field <code>cycleDescriptor</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getCycleDescriptor() {
        return cycleDescriptor;
    }

    /**
     * <p>Setter for the field <code>cycleDescriptor</code>.</p>
     *
     * @param cycleDescriptor a {@link java.lang.String} object.
     */
    public void setCycleDescriptor(String cycleDescriptor) {
        this.cycleDescriptor = cycleDescriptor;
    }

    /**
     * <p>Getter for the field <code>swAccession</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getSwAccession() {
        return swAccession;
    }

    /**
     * <p>Setter for the field <code>swAccession</code>.</p>
     *
     * @param swAccession a {@link java.lang.Integer} object.
     */
    public void setSwAccession(Integer swAccession) {
        this.swAccession = swAccession;
    }

    /**
     * <p>Getter for the field <code>processings</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<Processing> getProcessings() {
        /*
         * Set<Processing> pr = new TreeSet<Processing>(); SortedSet<IUS> setIUS
         * = getIUS(); for (IUS ius : setIUS) { pr.addAll(ius.getProcessings());
         * } return pr;
         */
        return processings;
    }

    /**
     * <p>Setter for the field <code>processings</code>.</p>
     *
     * @param processings a {@link java.util.Set} object.
     */
    public void setProcessings(Set<Processing> processings) {
        this.processings = processings;
    }

    /**
     * <p>setProcessingsForView.</p>
     *
     * @param processings a {@link java.util.Set} object.
     */
    public void setProcessingsForView(Set<Processing> processings) {
        IUS newIUS = new IUS();
        newIUS.setProcessings(processings);
        newIUS.setIusId(this.getLaneId());
        SortedSet<IUS> i = new TreeSet<IUS>();
        i.add(newIUS);
        this.setIUS(i);
    }

    /**
     * <p>addProcessing.</p>
     *
     * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
     */
    public void addProcessing(Processing processing) {
        this.getProcessings().add(processing);
        // processing.getLanes().add(this);
    }

    /**
     * <p>getSamples.</p>
     *
     * @return a {@link java.util.SortedSet} object.
     */
    public SortedSet<Sample> getSamples() {
        SortedSet<Sample> samples = new TreeSet<Sample>();
        SortedSet<IUS> setIUS = getIUS();
        for (IUS i : setIUS) {
            if (i.getSample() != null) {
                samples.add(i.getSample());
            }
        }
        return samples;
    }

    /**
     * <p>Getter for the field <code>sample</code>.</p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    public Sample getSample() {
        Sample firstSample = null;
        SortedSet<IUS> setIUS = getIUS();
        for (IUS i : setIUS) {
            if (i.getSample() != null) {
                firstSample = i.getSample();
                break;
            }
        }
        return firstSample;
        // return sample;
    }

    /**
     * <p>Setter for the field <code>sample</code>.</p>
     *
     * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    public void setSample(Sample sample) {
        this.sample = sample;
    }

    /**
     * <p>Getter for the field <code>createTimestamp</code>.</p>
     *
     * @return a {@link java.util.Date} object.
     */
    public Date getCreateTimestamp() {
        return createTimestamp;
    }

    /**
     * <p>Setter for the field <code>createTimestamp</code>.</p>
     *
     * @param createTimestamp a {@link java.util.Date} object.
     */
    public void setCreateTimestamp(Date createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    /**
     * <p>Getter for the field <code>updateTimestamp</code>.</p>
     *
     * @return a {@link java.util.Date} object.
     */
    public Date getUpdateTimestamp() {
        return updateTimestamp;
    }

    /**
     * <p>Setter for the field <code>updateTimestamp</code>.</p>
     *
     * @param updateTimestamp a {@link java.util.Date} object.
     */
    public void setUpdateTimestamp(Date updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    /**
     * <p>Getter for the field <code>serialVersionUID</code>.</p>
     *
     * @return a long.
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * <p>getJsonEscapeDescription.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getJsonEscapeDescription() {
        return JsonUtil.forJSON(description);
    }

    /**
     * <p>Getter for the field <code>description</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDescription() {
        return description;
    }

    /**
     * <p>Setter for the field <code>description</code>.</p>
     *
     * @param description a {@link java.lang.String} object.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * <p>Getter for the field <code>tags</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTags() {
        return tags;
    }

    /**
     * <p>Setter for the field <code>tags</code>.</p>
     *
     * @param tags a {@link java.lang.String} object.
     */
    public void setTags(String tags) {
        this.tags = tags;
    }

    /**
     * <p>Getter for the field <code>regions</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getRegions() {
        return regions;
    }

    /**
     * <p>Setter for the field <code>regions</code>.</p>
     *
     * @param regions a {@link java.lang.String} object.
     */
    public void setRegions(String regions) {
        this.regions = regions;
    }

    /**
     * <p>Getter for the field <code>laneId</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getLaneId() {
        return laneId;
    }

    /**
     * <p>Setter for the field <code>laneId</code>.</p>
     *
     * @param laneId a {@link java.lang.Integer} object.
     */
    public void setLaneId(Integer laneId) {
        this.laneId = laneId;
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>getJsonEscapeName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getJsonEscapeName() {
        return JsonUtil.forJSON(name);
    }

    /**
     * <p>Setter for the field <code>name</code>.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>organism</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getOrganism() {
        return organism;
    }

    /**
     * <p>Setter for the field <code>organism</code>.</p>
     *
     * @param organism a {@link java.lang.String} object.
     */
    public void setOrganism(String organism) {
        this.organism = organism;
    }

    /**
     * <p>Getter for the field <code>sampleType</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSampleType() {
        return sampleType;
    }

    /**
     * <p>Setter for the field <code>sampleType</code>.</p>
     *
     * @param sampleType a {@link java.lang.String} object.
     */
    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }

    /*
     * public String getSampleName() { return sampleName; }
     *
     * public void setSampleName(String sampleName) { this.sampleName =
     * sampleName; }
     */
    /**
     * <p>Getter for the field <code>sampleCode</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSampleCode() {
        return sampleCode;
    }

    /**
     * <p>Setter for the field <code>sampleCode</code>.</p>
     *
     * @param sampleCode a {@link java.lang.String} object.
     */
    public void setSampleCode(String sampleCode) {
        this.sampleCode = sampleCode;
    }


    /**
     * <p>Getter for the field <code>skip</code>.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean getSkip() {
        return skip;
    }

    /**
     * <p>Setter for the field <code>skip</code>.</p>
     *
     * @param skip a {@link java.lang.Boolean} object.
     */
    public void setSkip(Boolean skip) {
        if (skip != null && this.skip!=skip) {
            Log.debug("Skipping lane " + getSwAccession());
            this.skip = skip;
            if (ius != null) {
                for (IUS i : ius) {
                    i.setSkip(skip);
                }
            }
        }
    }

    /**
     * <p>Getter for the field <code>sequencerRun</code>.</p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
     */
    public SequencerRun getSequencerRun() {
        return sequencerRun;
    }

    /**
     * <p>Setter for the field <code>sequencerRun</code>.</p>
     *
     * @param sequencerRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
     */
    public void setSequencerRun(SequencerRun sequencerRun) {
        this.sequencerRun = sequencerRun;
    }

    /**
     * <p>Getter for the field <code>owner</code>.</p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.Registration} object.
     */
    public Registration getOwner() {
        return owner;
    }

    /**
     * <p>Setter for the field <code>owner</code>.</p>
     *
     * @param owner a {@link net.sourceforge.seqware.common.model.Registration} object.
     */
    public void setOwner(Registration owner) {
        this.owner = owner;
    }

    /**
     * <p>Getter for the field <code>isSelected</code>.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean getIsSelected() {
        return isSelected;
    }

    /**
     * <p>Setter for the field <code>isSelected</code>.</p>
     *
     * @param isSelected a {@link java.lang.Boolean} object.
     */
    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    /**
     * <p>Getter for the field <code>isHasFile</code>.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean getIsHasFile() {
        return isHasFile;
    }

    /**
     * <p>Setter for the field <code>isHasFile</code>.</p>
     *
     * @param isHasFile a {@link java.lang.Boolean} object.
     */
    public void setIsHasFile(Boolean isHasFile) {
        this.isHasFile = isHasFile;
    }

    /**
     * <p>Getter for the field <code>workflowRuns</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<WorkflowRun> getWorkflowRuns() {
        return workflowRuns;
    }

    /**
     * <p>Setter for the field <code>workflowRuns</code>.</p>
     *
     * @param workflowRuns a {@link java.util.Set} object.
     */
    public void setWorkflowRuns(Set<WorkflowRun> workflowRuns) {
        this.workflowRuns = workflowRuns;
    }

    /**
     * <p>Getter for the field <code>laneLinks</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<LaneLink> getLaneLinks() {
        return laneLinks;
    }

    /**
     * <p>Setter for the field <code>laneLinks</code>.</p>
     *
     * @param laneLinks a {@link java.util.Set} object.
     */
    public void setLaneLinks(Set<LaneLink> laneLinks) {
        this.laneLinks = laneLinks;
    }

    /**
     * <p>Getter for the field <code>laneAttributes</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    @XmlElementWrapper(name = "LaneAttributes")
    @XmlElement(name = "LaneAttribute")
    public Set<LaneAttribute> getLaneAttributes() {
        return laneAttributes;
    }

    /**
     * <p>Setter for the field <code>laneAttributes</code>.</p>
     *
     * @param laneAttributes a {@link java.util.Set} object.
     */
    public void setLaneAttributes(Set<LaneAttribute> laneAttributes) {
        this.laneAttributes = laneAttributes;
    }

    /** {@inheritDoc} */
    @Override
    public boolean givesPermission(Registration registration) {
        boolean hasPermission = true;

        if (sample != null) {
            hasPermission = sample.givesPermission(registration);
        }

        // if one of the IUSes doesn't have permission, we can't touch this Lane
        // object...
        if (ius != null && !ius.isEmpty()) {
            for (IUS i : ius) {
                if (!i.givesPermission(registration)) {
                    hasPermission = false;
                }
            }
        }

        // this object is orphaned, but does the person own it?
        if (sample == null && ius == null) {
            if (registration.equals(this.owner) || registration.isLIMSAdmin()) {
                Logger.getLogger(Lane.class).warn("Modifying Orphan Lane: " + toString());
                hasPermission = true;
            } else if (owner == null) {
                Logger.getLogger(Lane.class).warn("Orphan Lane has no owner! Allowing modifications: " + toString());
                hasPermission = true;
            } else {
                Logger.getLogger(Lane.class).warn("Not modifying Orphan Lane: " + toString());
                hasPermission = false;
            }
        }

        if (!hasPermission) {
            Logger.getLogger(Lane.class).info("Lane does not give permission");
            throw new SecurityException("User " + registration.getEmailAddress() + " does not have permission to modify "
                    + toString());
        }
        return hasPermission;

    }

  public StudyType getStudyType() {
    return studyType;
  }

  public void setStudyType(StudyType studyType) {
    this.studyType = studyType;
  }

  public LibraryStrategy getLibraryStrategy() {
    return libraryStrategy;
  }

  public void setLibraryStrategy(LibraryStrategy libraryStrategy) {
    this.libraryStrategy = libraryStrategy;
  }

  public LibrarySelection getLibrarySelection() {
    return librarySelection;
  }

  public void setLibrarySelection(LibrarySelection librarySelection) {
    this.librarySelection = librarySelection;
  }

  public LibrarySource getLibrarySource() {
    return librarySource;
  }

  public void setLibrarySource(LibrarySource librarySource) {
    this.librarySource = librarySource;
  }
    
    
}
