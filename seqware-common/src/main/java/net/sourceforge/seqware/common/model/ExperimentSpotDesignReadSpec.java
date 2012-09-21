package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class ExperimentSpotDesignReadSpec implements Serializable, Comparable<ExperimentSpotDesignReadSpec> {
	/**
	 * LEFT OFF WITH: FINSIH THIS OBJECT
	 */
	private static final long serialVersionUID = 17328392935613893L;
	private Integer experimentSpotDesignReadSpecId;
	private ExperimentSpotDesign experimentSpotDesign;
	private Integer readIndex;
	private String readLabel;
	private String	readClass;
	private String	readType;
	private Integer  baseCoord;
  private Integer  cycleCoord;
  private Integer  length;
	// FIXME: what is this!?  Is this where I would store tags!?
	private String expectedBaseCall;
	
	public ExperimentSpotDesignReadSpec() {
		super();
	}
	
    @Override
	public int compareTo(ExperimentSpotDesignReadSpec that) {
		if(that == null)
			return -1;

		if(that.getExperimentSpotDesignReadSpecId() == this.getExperimentSpotDesignReadSpecId())	// when both names are null
			return 0;

		if(that.getExperimentSpotDesignReadSpecId() == null)
			return -1;							// when only the other name is null

		return(that.getExperimentSpotDesignReadSpecId().compareTo(this.getExperimentSpotDesignReadSpecId()));
	}

    @Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("experimentSpotDesignReadSpecId", getExperimentSpotDesignReadSpecId())
			.toString();
	}

    @Override
	public boolean equals(Object other) {
		if ( (this == other ) ) return true;
		if ( !(other instanceof ExperimentSpotDesignReadSpec) ) return false;
		ExperimentSpotDesignReadSpec castOther = (ExperimentSpotDesignReadSpec) other;
		return new EqualsBuilder()
			.append(this.getExperimentSpotDesignReadSpecId(), castOther.getExperimentSpotDesignReadSpecId())
			.isEquals();
	}

    @Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getExperimentSpotDesignReadSpecId())
			.toHashCode();
	}

	
	
	public Integer getLength() {
    return length;
  }

  public void setLength(Integer length) {
    this.length = length;
  }

  public Integer getExperimentSpotDesignReadSpecId() {
		return experimentSpotDesignReadSpecId;
	}

	public void setExperimentSpotDesignReadSpecId(
			Integer experimentSpotDesignReadSpecId) {
		this.experimentSpotDesignReadSpecId = experimentSpotDesignReadSpecId;
	}

	public ExperimentSpotDesign getExperimentSpotDesign() {
    return experimentSpotDesign;
  }

  public void setExperimentSpotDesign(ExperimentSpotDesign experimentSpotDesign) {
    this.experimentSpotDesign = experimentSpotDesign;
  }

  public Integer getReadIndex() {
		return readIndex;
	}

	public void setReadIndex(Integer readIndex) {
		this.readIndex = readIndex;
	}

	public String getReadLabel() {
		return readLabel;
	}

	public void setReadLabel(String readLabel) {
		this.readLabel = readLabel;
	}

	public String getReadClass() {
		return readClass;
	}

	public void setReadClass(String readClass) {
		this.readClass = readClass;
	}

	public String getReadType() {
		return readType;
	}

	public void setReadType(String readType) {
		this.readType = readType;
	}

	public Integer getBaseCoord() {
		return baseCoord;
	}

	public void setBaseCoord(Integer baseCoord) {
		this.baseCoord = baseCoord;
	}

	public Integer getCycleCoord() {
		return cycleCoord;
	}

	public void setCycleCoord(Integer cycleCoord) {
		this.cycleCoord = cycleCoord;
	}

	public String getExpectedBaseCall() {
		return expectedBaseCall;
	}

	public void setExpectedBaseCall(String expectedBaseCall) {
		this.expectedBaseCall = expectedBaseCall;
	}

}
