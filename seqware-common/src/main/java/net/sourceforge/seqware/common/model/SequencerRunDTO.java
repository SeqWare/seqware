package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.util.Date;
import java.util.SortedSet;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@XmlTransient
public class SequencerRunDTO implements Serializable {
	private static final long serialVersionUID = 3681328115923390568L;
	private Integer sequencerRunId;
	private Registration owner;
	private Platform platform;
	private String	name;
	private String	description;
	private String  instrumentName;
	private String  cycleDescriptor;
	private String  cycleSequence;
	private Integer cycleCount;
	private String	status;
	private String	cycles;
	private Integer refLane;
	private String	filePath;
	private String	readyToProcess;
	private Boolean pairedEnd;
	private String  pairedFilePath;
	private Boolean useIparIntensities;
	private String  colorMatrix;
	private String  colorMatrixCode;
    private Integer slideOneLaneCount;
    private Integer slideTwoLaneCount;
    private String  slideOneFilePath;
    private String  slideTwoFilePath;
    private String  flowSequence;
    private Integer flowCount;
    private String  runCenter;
	private Date	createTimestamp;
	private Date	updateTimestamp;

	private SortedSet<Processing>	processings;
	private SortedSet<Lane>			lanes;
	private Lane lane1, lane2, lane3, lane4, lane5, lane6, lane7, lane8;

	public SequencerRunDTO() {
		super();
		lane1 = new Lane();
		lane2 = new Lane();
		lane3 = new Lane();
		lane4 = new Lane();
		lane5 = new Lane();
		lane6 = new Lane();
		lane7 = new Lane();
		lane8 = new Lane();
	}
@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("sequencerRunId", getSequencerRunId())
			.append("name", getName())
			.toString();
	}
@Override
	public boolean equals(Object other) {
		if ( (this == other ) ) return true;
		if ( !(other instanceof SequencerRunDTO) ) return false;
		SequencerRunDTO castOther = (SequencerRunDTO) other;
		return new EqualsBuilder()
			.append(this.getName(), castOther.getName())
			.isEquals();
	}
@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getName())
			.toHashCode();
	}

	public int getErrorCnt() {
		int errorCnt = 0;
		for (Processing proc : processings) {
			if (proc.getStatus().contains("error")) {
				errorCnt++;
			}
		}
		return(errorCnt);
	}

	public int getProcessingCnt() {
		int errorCnt = 0;
		for (Processing proc : processings) {
			if (proc.getStatus().endsWith("ing")) {
				errorCnt++;
			}
		}
		return(errorCnt);
	}

	public Integer getSequencerRunId() {
		return sequencerRunId;
	}

	public void setSequencerRunId(Integer sequencerRunId) {
		this.sequencerRunId = sequencerRunId;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Date getCreateTimestamp() {
		return createTimestamp;
	}

	public void setCreateTimestamp(Date createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public String getCycles() {
		return cycles;
	}

	public void setCycles(String cycles) {
		this.cycles = cycles;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Integer getRefLane() {
		return refLane;
	}

	public void setRefLane(Integer refLane) {
		this.refLane = refLane;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getUpdateTimestamp() {
		return updateTimestamp;
	}

	public void setUpdateTimestamp(Date updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SortedSet<Processing> getProcessings() {
		return processings;
	}

	public void setProcessings(SortedSet<Processing> processings) {
		this.processings = processings;
	}

	public SortedSet<Lane> getLanes() {
		return lanes;
	}

	public void setLanes(SortedSet<Lane> lanes) {
		this.lanes = lanes;
	}

	public Lane getLane1() {
		return lane1;
	}

	public void setLane1(Lane lane1) {
		this.lane1 = lane1;
	}

	public Lane getLane2() {
		return lane2;
	}

	public void setLane2(Lane lane2) {
		this.lane2 = lane2;
	}

	public Lane getLane3() {
		return lane3;
	}

	public void setLane3(Lane lane3) {
		this.lane3 = lane3;
	}

	public Lane getLane4() {
		return lane4;
	}

	public void setLane4(Lane lane4) {
		this.lane4 = lane4;
	}

	public Lane getLane5() {
		return lane5;
	}

	public void setLane5(Lane lane5) {
		this.lane5 = lane5;
	}

	public Lane getLane6() {
		return lane6;
	}

	public void setLane6(Lane lane6) {
		this.lane6 = lane6;
	}

	public Lane getLane7() {
		return lane7;
	}

	public void setLane7(Lane lane7) {
		this.lane7 = lane7;
	}

	public Lane getLane8() {
		return lane8;
	}

	public void setLane8(Lane lane8) {
		this.lane8 = lane8;
	}

	public String getReadyToProcess() {
		return readyToProcess;
	}

	public void setReadyToProcess(String readyToProcess) {
		this.readyToProcess = readyToProcess;
	}

    public Boolean getPairedEnd() {
        return pairedEnd;
    }

    public void setPairedEnd(Boolean pairedEnd) {
        this.pairedEnd = pairedEnd;
    }

    public String getPairedFilePath() {
        return pairedFilePath;
    }

    public void setPairedFilePath(String pairedFilePath) {
        this.pairedFilePath = pairedFilePath;
    }

    public Boolean getUseIparIntensities() {
        return useIparIntensities;
    }

    public void setUseIparIntensities(Boolean useIparIntensities) {
        this.useIparIntensities = useIparIntensities;
    }

    public Registration getOwner() {
        return owner;
    }

    public void setOwner(Registration owner) {
        this.owner = owner;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }

    public String getCycleDescriptor() {
        return cycleDescriptor;
    }

    public void setCycleDescriptor(String cycleDescriptor) {
        this.cycleDescriptor = cycleDescriptor;
    }

    public String getCycleSequence() {
        return cycleSequence;
    }

    public void setCycleSequence(String cycleSequence) {
        this.cycleSequence = cycleSequence;
    }

    public Integer getCycleCount() {
        return cycleCount;
    }

    public void setCycleCount(Integer cycleCount) {
        this.cycleCount = cycleCount;
    }

    public String getColorMatrix() {
        return colorMatrix;
    }

    public void setColorMatrix(String colorMatrix) {
        this.colorMatrix = colorMatrix;
    }

    public String getColorMatrixCode() {
        return colorMatrixCode;
    }

    public void setColorMatrixCode(String colorMatrixCode) {
        this.colorMatrixCode = colorMatrixCode;
    }

    public Integer getSlideOneLaneCount() {
        return slideOneLaneCount;
    }

    public void setSlideOneLaneCount(Integer slideOneLaneCount) {
        this.slideOneLaneCount = slideOneLaneCount;
    }

    public Integer getSlideTwoLaneCount() {
        return slideTwoLaneCount;
    }

    public void setSlideTwoLaneCount(Integer slideTwoLaneCount) {
        this.slideTwoLaneCount = slideTwoLaneCount;
    }

    public String getSlideOneFilePath() {
        return slideOneFilePath;
    }

    public void setSlideOneFilePath(String slideOneFilePath) {
        this.slideOneFilePath = slideOneFilePath;
    }

    public String getSlideTwoFilePath() {
        return slideTwoFilePath;
    }

    public void setSlideTwoFilePath(String slideTwoFilePath) {
        this.slideTwoFilePath = slideTwoFilePath;
    }

    public String getFlowSequence() {
        return flowSequence;
    }

    public void setFlowSequence(String flowSequence) {
        this.flowSequence = flowSequence;
    }

    public Integer getFlowCount() {
        return flowCount;
    }

    public void setFlowCount(Integer flowCount) {
        this.flowCount = flowCount;
    }

    public String getRunCenter() {
        return runCenter;
    }

    public void setRunCenter(String runCenter) {
        this.runCenter = runCenter;
    }
}
