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
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

@XmlRootElement
/**
 * <p>SequencerRun class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SequencerRun implements Serializable, Comparable<SequencerRun>, PermissionsAware {

  private static final long serialVersionUID = 3681328115923390568L;
    private Integer sequencerRunId;
    private Registration owner;
    private Platform platform;
    private String name;
    private String description;
    private String instrumentName;
    private String cycleDescriptor;
    private String cycleSequence;
    private Integer cycleCount;
    private SequencerRunStatus status;
    private String cycles;
    private Integer refLane;
    private String filePath;
    // private String readyToProcess;
    private boolean process;
    private Boolean pairedEnd;
    private String pairedFilePath;
    private Boolean useIparIntensities;
    private String colorMatrix;
    private String colorMatrixCode;
    private Integer slideOneLaneCount;
    private Integer slideTwoLaneCount;
    private String slideOneFilePath;
    private String slideTwoFilePath;
    private String flowSequence;
    private Integer flowCount;
    private String runCenter;
    private Date createTimestamp;
    private Date updateTimestamp;
    private Integer swAccession;
    private int platformInt;
    private Integer processingCount;
    private Integer processedCount;
    private Integer errorCount;
    private Boolean skip;
    private String html;
    private Set<SequencerRunAttribute> sequencerRunAttributes = new TreeSet<SequencerRunAttribute>();
    private Set<Processing> processings;
    private SortedSet<Lane> lanes = new TreeSet<Lane>();
    // private Lane lane1, lane2, lane3, lane4, lane5, lane6, lane7, lane8;
    private String strRefLane;

    /**
     * <p>Constructor for SequencerRun.</p>
     */
    public SequencerRun() {
        super();
        /*
         * lane1 = new Lane(); lane2 = new Lane(); lane3 = new Lane(); lane4 =
         * new Lane(); lane5 = new Lane(); lane6 = new Lane(); lane7 = new
         * Lane(); lane8 = new Lane();
         */
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(SequencerRun that) {
        if (that == null) {
            return -1;
        }

        if (that.getSequencerRunId() == this.getSequencerRunId()) // when both names
        // are null
        {
            return 0;
        }

        if (that.getSequencerRunId() == null) {
            return -1; // when only the other name is null
        }
        return (that.getSequencerRunId().compareTo(this.getSequencerRunId()));
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("sequencerRunId", getSequencerRunId()).append("name", getName()).toString();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object other) {
        if ((this == other)) {
            return true;
        }
        if (!(other instanceof SequencerRun)) {
            return false;
        }
        SequencerRun castOther = (SequencerRun) other;
        return (new EqualsBuilder().append(this.getSequencerRunId(), castOther.getSequencerRunId()).isEquals());
        /*
         * return new EqualsBuilder() .append(this.getName(),
         * castOther.getName()) .isEquals();
         */
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getName()).toHashCode();
    }

    /**
     * <p>getAllProcessing.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<Processing> getAllProcessing() {
        Set<Processing> allProcessings = new TreeSet<Processing>();

        // add SequencerRun processing
        allProcessings.addAll(getProcessings());

        // add Lanes processings AND IUS processings
        for (Lane lane : getLanes()) {
            allProcessings.addAll(lane.getAllProcessings());
        }

        return allProcessings;
    }

    /**
     * <p>getErrorCnt.</p>
     *
     * @return a int.
     */
    public int getErrorCnt() {
        int errorCnt = 0;
        for (Processing proc : getAllProcessing()) {
            errorCnt += recursiveCountProcErrors(proc);
        }
        return (errorCnt);
    }

    private int recursiveCountProcErrors(Processing proc) {
        int errorCnt = 0;
        if (proc != null && ProcessingStatus.failed == proc.getStatus()) {
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
        for (Processing proc : getAllProcessing()) {
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
        for (Processing proc : getAllProcessing()) {
            cnt += recursiveCountProcessed(proc);
        }
        return (cnt);
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
     * <p>Getter for the field <code>process</code>.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    public boolean getProcess() {
        return process;
    }

    /**
     * <p>Setter for the field <code>process</code>.</p>
     *
     * @param process a {@link java.lang.Boolean} object.
     */
    public void setProcess(Boolean process) {
        this.process = process == null ? false : true;
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
     * <p>Getter for the field <code>sequencerRunId</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getSequencerRunId() {
        return sequencerRunId;
    }

    /**
     * <p>Setter for the field <code>sequencerRunId</code>.</p>
     *
     * @param sequencerRunId a {@link java.lang.Integer} object.
     */
    public void setSequencerRunId(Integer sequencerRunId) {
        this.sequencerRunId = sequencerRunId;
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
     * <p>Getter for the field <code>cycles</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getCycles() {
        return cycles;
    }

    /**
     * <p>Setter for the field <code>cycles</code>.</p>
     *
     * @param cycles a {@link java.lang.String} object.
     */
    public void setCycles(String cycles) {
        this.cycles = cycles;
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
     * <p>Getter for the field <code>filePath</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * <p>Setter for the field <code>filePath</code>.</p>
     *
     * @param filePath a {@link java.lang.String} object.
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * <p>Getter for the field <code>refLane</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getRefLane() {
        return refLane;
    }

    /**
     * <p>Setter for the field <code>refLane</code>.</p>
     *
     * @param refLane a {@link java.lang.Integer} object.
     */
    public void setRefLane(Integer refLane) {
        this.refLane = refLane;
    }

    /**
     * <p>Getter for the field <code>status</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public SequencerRunStatus getStatus() {
        return status;
    }

    /**
     * <p>Setter for the field <code>status</code>.</p>
     *
     * @param status a {@link java.lang.String} object.
     */
    public void setStatus(SequencerRunStatus status) {
        this.status = status;
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
     * <p>Getter for the field <code>processings</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<Processing> getProcessings() {
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
     * <p>Getter for the field <code>lanes</code>.</p>
     *
     * @return a {@link java.util.SortedSet} object.
     */
    public SortedSet<Lane> getLanes() {
        return lanes;
    }

    /**
     * <p>Setter for the field <code>lanes</code>.</p>
     *
     * @param lanes a {@link java.util.SortedSet} object.
     */
    public void setLanes(SortedSet<Lane> lanes) {
        this.lanes = lanes;
    }

    /*
     * public Lane getLane1() { return lane1; }
     *
     * public void setLane1(Lane lane1) { this.lane1 = lane1; }
     *
     * public Lane getLane2() { return lane2; }
     *
     * public void setLane2(Lane lane2) { this.lane2 = lane2; }
     *
     * public Lane getLane3() { return lane3; }
     *
     * public void setLane3(Lane lane3) { this.lane3 = lane3; }
     *
     * public Lane getLane4() { return lane4; }
     *
     * public void setLane4(Lane lane4) { this.lane4 = lane4; }
     *
     * public Lane getLane5() { return lane5; }
     *
     * public void setLane5(Lane lane5) { this.lane5 = lane5; }
     *
     * public Lane getLane6() { return lane6; }
     *
     * public void setLane6(Lane lane6) { this.lane6 = lane6; }
     *
     * public Lane getLane7() { return lane7; }
     *
     * public void setLane7(Lane lane7) { this.lane7 = lane7; }
     *
     * public Lane getLane8() { return lane8; }
     *
     * public void setLane8(Lane lane8) { this.lane8 = lane8; }
     */

    /**
     * <p>Getter for the field <code>pairedEnd</code>.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean getPairedEnd() {
        return pairedEnd;
    }

    /**
     * <p>Setter for the field <code>pairedEnd</code>.</p>
     *
     * @param pairedEnd a {@link java.lang.Boolean} object.
     */
    public void setPairedEnd(Boolean pairedEnd) {
        this.pairedEnd = pairedEnd;
    }

    /**
     * <p>Getter for the field <code>pairedFilePath</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPairedFilePath() {
        return pairedFilePath;
    }

    /**
     * <p>Setter for the field <code>pairedFilePath</code>.</p>
     *
     * @param pairedFilePath a {@link java.lang.String} object.
     */
    public void setPairedFilePath(String pairedFilePath) {
        this.pairedFilePath = pairedFilePath;
    }

    /**
     * <p>Getter for the field <code>useIparIntensities</code>.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean getUseIparIntensities() {
        return useIparIntensities;
    }

    /**
     * <p>Setter for the field <code>useIparIntensities</code>.</p>
     *
     * @param useIparIntensities a {@link java.lang.Boolean} object.
     */
    public void setUseIparIntensities(Boolean useIparIntensities) {
        this.useIparIntensities = useIparIntensities;
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
     * <p>Getter for the field <code>platform</code>.</p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.Platform} object.
     */
    public Platform getPlatform() {
        return platform;
    }

    /**
     * <p>Setter for the field <code>platform</code>.</p>
     *
     * @param platform a {@link net.sourceforge.seqware.common.model.Platform} object.
     */
    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    /**
     * <p>Getter for the field <code>instrumentName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getInstrumentName() {
        return instrumentName;
    }

    /**
     * <p>Setter for the field <code>instrumentName</code>.</p>
     *
     * @param instrumentName a {@link java.lang.String} object.
     */
    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
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
     * <p>Getter for the field <code>cycleSequence</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getCycleSequence() {
        return cycleSequence;
    }

    /**
     * <p>Setter for the field <code>cycleSequence</code>.</p>
     *
     * @param cycleSequence a {@link java.lang.String} object.
     */
    public void setCycleSequence(String cycleSequence) {
        this.cycleSequence = cycleSequence;
    }

    /**
     * <p>Getter for the field <code>cycleCount</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getCycleCount() {
        return cycleCount;
    }

    /**
     * <p>Setter for the field <code>cycleCount</code>.</p>
     *
     * @param cycleCount a {@link java.lang.Integer} object.
     */
    public void setCycleCount(Integer cycleCount) {
        this.cycleCount = cycleCount;
    }

    /**
     * <p>Getter for the field <code>colorMatrix</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getColorMatrix() {
        return colorMatrix;
    }

    /**
     * <p>Setter for the field <code>colorMatrix</code>.</p>
     *
     * @param colorMatrix a {@link java.lang.String} object.
     */
    public void setColorMatrix(String colorMatrix) {
        this.colorMatrix = colorMatrix;
    }

    /**
     * <p>Getter for the field <code>colorMatrixCode</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getColorMatrixCode() {
        return colorMatrixCode;
    }

    /**
     * <p>Setter for the field <code>colorMatrixCode</code>.</p>
     *
     * @param colorMatrixCode a {@link java.lang.String} object.
     */
    public void setColorMatrixCode(String colorMatrixCode) {
        this.colorMatrixCode = colorMatrixCode;
    }

    /**
     * <p>Getter for the field <code>slideOneLaneCount</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getSlideOneLaneCount() {
        return slideOneLaneCount;
    }

    /**
     * <p>Setter for the field <code>slideOneLaneCount</code>.</p>
     *
     * @param slideOneLaneCount a {@link java.lang.Integer} object.
     */
    public void setSlideOneLaneCount(Integer slideOneLaneCount) {
        this.slideOneLaneCount = slideOneLaneCount;
    }

    /**
     * <p>Getter for the field <code>slideTwoLaneCount</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getSlideTwoLaneCount() {
        return slideTwoLaneCount;
    }

    /**
     * <p>Setter for the field <code>slideTwoLaneCount</code>.</p>
     *
     * @param slideTwoLaneCount a {@link java.lang.Integer} object.
     */
    public void setSlideTwoLaneCount(Integer slideTwoLaneCount) {
        this.slideTwoLaneCount = slideTwoLaneCount;
    }

    /**
     * <p>Getter for the field <code>slideOneFilePath</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSlideOneFilePath() {
        return slideOneFilePath;
    }

    /**
     * <p>Setter for the field <code>slideOneFilePath</code>.</p>
     *
     * @param slideOneFilePath a {@link java.lang.String} object.
     */
    public void setSlideOneFilePath(String slideOneFilePath) {
        this.slideOneFilePath = slideOneFilePath;
    }

    /**
     * <p>Getter for the field <code>slideTwoFilePath</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSlideTwoFilePath() {
        return slideTwoFilePath;
    }

    /**
     * <p>Setter for the field <code>slideTwoFilePath</code>.</p>
     *
     * @param slideTwoFilePath a {@link java.lang.String} object.
     */
    public void setSlideTwoFilePath(String slideTwoFilePath) {
        this.slideTwoFilePath = slideTwoFilePath;
    }

    /**
     * <p>Getter for the field <code>flowSequence</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getFlowSequence() {
        return flowSequence;
    }

    /**
     * <p>Setter for the field <code>flowSequence</code>.</p>
     *
     * @param flowSequence a {@link java.lang.String} object.
     */
    public void setFlowSequence(String flowSequence) {
        this.flowSequence = flowSequence;
    }

    /**
     * <p>Getter for the field <code>flowCount</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getFlowCount() {
        return flowCount;
    }

    /**
     * <p>Setter for the field <code>flowCount</code>.</p>
     *
     * @param flowCount a {@link java.lang.Integer} object.
     */
    public void setFlowCount(Integer flowCount) {
        this.flowCount = flowCount;
    }

    /**
     * <p>Getter for the field <code>runCenter</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getRunCenter() {
        return runCenter;
    }

    /**
     * <p>Setter for the field <code>runCenter</code>.</p>
     *
     * @param runCenter a {@link java.lang.String} object.
     */
    public void setRunCenter(String runCenter) {
        this.runCenter = runCenter;
    }

    /**
     * <p>Getter for the field <code>platformInt</code>.</p>
     *
     * @return a int.
     */
    public int getPlatformInt() {
        return platformInt;
    }

    /**
     * <p>Setter for the field <code>platformInt</code>.</p>
     *
     * @param platformInt a int.
     */
    public void setPlatformInt(int platformInt) {
        this.platformInt = platformInt;
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
     * <p>Getter for the field <code>processingCount</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getProcessingCount() {
        return processingCount;
    }

    /**
     * <p>Setter for the field <code>processingCount</code>.</p>
     *
     * @param processingCount a {@link java.lang.Integer} object.
     */
    public void setProcessingCount(Integer processingCount) {
        this.processingCount = processingCount;
    }

    /**
     * <p>Getter for the field <code>processedCount</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getProcessedCount() {
        return processedCount;
    }

    /**
     * <p>Setter for the field <code>processedCount</code>.</p>
     *
     * @param processedCount a {@link java.lang.Integer} object.
     */
    public void setProcessedCount(Integer processedCount) {
        this.processedCount = processedCount;
    }

    /**
     * <p>Getter for the field <code>errorCount</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getErrorCount() {
        return errorCount;
    }

    /**
     * <p>Setter for the field <code>errorCount</code>.</p>
     *
     * @param errorCount a {@link java.lang.Integer} object.
     */
    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    /**
     * <p>Getter for the field <code>html</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getHtml() {
        return html;
    }

    /**
     * <p>Setter for the field <code>html</code>.</p>
     *
     * @param html a {@link java.lang.String} object.
     */
    public void setHtml(String html) {
        this.html = html;
    }

    /**
     * <p>Getter for the field <code>strRefLane</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getStrRefLane() {
        return strRefLane;
    }

    /**
     * <p>Setter for the field <code>strRefLane</code>.</p>
     *
     * @param strRefLane a {@link java.lang.String} object.
     */
    public void setStrRefLane(String strRefLane) {
        this.strRefLane = strRefLane;
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
            Log.debug("Setting skip="+skip+" in sequencer run " + getName() + " " + getSwAccession());
            this.skip = skip;
            for (Lane lane : lanes) {
                lane.setSkip(skip);
            }
        }
    }

    /**
     * <p>Getter for the field <code>sequencerRunAttributes</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    @XmlElementWrapper(name = "SequencerRunAttributes")
    @XmlElement(name = "SequencerRunLaneAttribute")
    public Set<SequencerRunAttribute> getSequencerRunAttributes() {
        return sequencerRunAttributes;
    }

    /**
     * <p>Setter for the field <code>sequencerRunAttributes</code>.</p>
     *
     * @param sequencerRunAttributes a {@link java.util.Set} object.
     */
    public void setSequencerRunAttributes(Set<SequencerRunAttribute> sequencerRunAttributes) {
        this.sequencerRunAttributes = sequencerRunAttributes;
    }

    /** {@inheritDoc} */
    @Override
    public boolean givesPermission(Registration registration) {
        Logger.getLogger(SequencerRun.class).info("Sequencer run always gives permission");
        return true;
    }
}
