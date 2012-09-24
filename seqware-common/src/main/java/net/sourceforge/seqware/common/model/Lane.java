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

    public Lane() {
        super();
    }

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

    @Override
    public String toString() {
        return "Lane{" + "laneId=" + laneId + ", laneIndex=" + laneIndex + ", sequencerRun=" + sequencerRun + ", sample="
                + sample + ", name=" + name + ", cycleDescriptor=" + cycleDescriptor + ", sampleCode=" + sampleCode
                + ", description=" + description + ", organism=" + organism + ", sampleType=" + sampleType + ", tags=" + tags
                + ", regions=" + regions + ", skipTxt=" + skipTxt + ", skip=" + skip + ", owner=" + owner
                + ", createTimestamp=" + createTimestamp + ", updateTimestamp=" + updateTimestamp + ", swAccession="
                + swAccession + ", isSelected=" + isSelected + ", isHasFile=" + isHasFile + '}';
    }

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
    public int getErrorCnt() {
        int errorCnt = 0;
        for (Processing proc : getAllProcessings()) {
            errorCnt += recursiveCountProcErrors(proc);
        }
        return (errorCnt);
    }

    private int recursiveCountProcErrors(Processing proc) {
        int errorCnt = 0;
        if (proc != null && ("failed".equals(proc.getStatus()))) {
            errorCnt++;
        }
        for (Processing childProc : proc.getChildren()) {
            errorCnt += recursiveCountProcErrors(childProc);
        }
        return (errorCnt);
    }

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
                && (proc.getStatus().toLowerCase().contains("running") || proc.getStatus().toLowerCase().contains("pending"))) {
            runCnt++;
        }
        for (Processing childProc : proc.getChildren()) {
            runCnt += recursiveCountProcRunning(childProc);
        }
        return (runCnt);
    }

    public int getProcessedCnt() {
        int cnt = 0;
        for (Processing proc : getAllProcessings()) {
            cnt += recursiveCountProcessed(proc);
        }
        return (cnt);
    }

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
        if (proc != null && (proc.getStatus().toLowerCase().contains("success"))) {
            runCnt++;
        }
        for (Processing childProc : proc.getChildren()) {
            runCnt += recursiveCountProcessed(childProc);
        }
        return (runCnt);
    }

    public SortedSet<IUS> getIus() {
        return ius;
    }

    public SortedSet<IUS> getIUS() {
        return ius;
    }

    public void setIUS(SortedSet<IUS> ius) {
        this.ius = ius;
    }

    public Integer getLaneIndex() {
        return laneIndex;
    }

    public void setLaneIndex(Integer laneIndex) {
        this.laneIndex = laneIndex;
    }

    public String getCycleDescriptor() {
        return cycleDescriptor;
    }

    public void setCycleDescriptor(String cycleDescriptor) {
        this.cycleDescriptor = cycleDescriptor;
    }

    public Integer getSwAccession() {
        return swAccession;
    }

    public void setSwAccession(Integer swAccession) {
        this.swAccession = swAccession;
    }

    public Set<Processing> getProcessings() {
        /*
         * Set<Processing> pr = new TreeSet<Processing>(); SortedSet<IUS> setIUS
         * = getIUS(); for (IUS ius : setIUS) { pr.addAll(ius.getProcessings());
         * } return pr;
         */
        return processings;
    }

    public void setProcessings(Set<Processing> processings) {
        this.processings = processings;
    }

    public void setProcessingsForView(Set<Processing> processings) {
        IUS newIUS = new IUS();
        newIUS.setProcessings(processings);
        newIUS.setIusId(this.getLaneId());
        SortedSet<IUS> i = new TreeSet<IUS>();
        i.add(newIUS);
        this.setIUS(i);
    }

    public void addProcessing(Processing processing) {
        this.getProcessings().add(processing);
        // processing.getLanes().add(this);
    }

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

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public Date getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Date createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public Date getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Date updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getJsonEscapeDescription() {
        return JsonUtil.forJSON(description);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getRegions() {
        return regions;
    }

    public void setRegions(String regions) {
        this.regions = regions;
    }

    public Integer getLaneId() {
        return laneId;
    }

    public void setLaneId(Integer laneId) {
        this.laneId = laneId;
    }

    public String getName() {
        return name;
    }

    public String getJsonEscapeName() {
        return JsonUtil.forJSON(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganism() {
        return organism;
    }

    public void setOrganism(String organism) {
        this.organism = organism;
    }

    public String getSampleType() {
        return sampleType;
    }

    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }

    /*
     * public String getSampleName() { return sampleName; }
     *
     * public void setSampleName(String sampleName) { this.sampleName =
     * sampleName; }
     */
    public String getSampleCode() {
        return sampleCode;
    }

    public void setSampleCode(String sampleCode) {
        this.sampleCode = sampleCode;
    }

    public String getSkipTxt() {
        if (skip == null) {
            return "";
        } else if (skip) {
            return "Y";
        } else {
            return "";
        }
    }

    public void setSkipTxt(String skip) {
        if (skip == null) {
            this.skip = false;
        } else if ("Y".equals(skip.toUpperCase().trim())) {
            this.skip = true;
        } else {
            this.skip = false;
        }
    }

    public Boolean getSkip() {
        return skip;
    }

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

    public SequencerRun getSequencerRun() {
        return sequencerRun;
    }

    public void setSequencerRun(SequencerRun sequencerRun) {
        this.sequencerRun = sequencerRun;
    }

    public Registration getOwner() {
        return owner;
    }

    public void setOwner(Registration owner) {
        this.owner = owner;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    public Boolean getIsHasFile() {
        return isHasFile;
    }

    public void setIsHasFile(Boolean isHasFile) {
        this.isHasFile = isHasFile;
    }

    public Set<WorkflowRun> getWorkflowRuns() {
        return workflowRuns;
    }

    public void setWorkflowRuns(Set<WorkflowRun> workflowRuns) {
        this.workflowRuns = workflowRuns;
    }

    public Set<LaneLink> getLaneLinks() {
        return laneLinks;
    }

    public void setLaneLinks(Set<LaneLink> laneLinks) {
        this.laneLinks = laneLinks;
    }

    @XmlElementWrapper(name = "LaneAttributes")
    @XmlElement(name = "LaneAttribute")
    public Set<LaneAttribute> getLaneAttributes() {
        return laneAttributes;
    }

    public void setLaneAttributes(Set<LaneAttribute> laneAttributes) {
        this.laneAttributes = laneAttributes;
    }

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
}
