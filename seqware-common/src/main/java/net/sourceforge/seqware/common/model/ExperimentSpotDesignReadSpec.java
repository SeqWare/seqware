package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * <p>ExperimentSpotDesignReadSpec class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
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
	
	/**
	 * <p>Constructor for ExperimentSpotDesignReadSpec.</p>
	 */
	public ExperimentSpotDesignReadSpec() {
		super();
	}
	
    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("experimentSpotDesignReadSpecId", getExperimentSpotDesignReadSpecId())
			.toString();
	}

    /** {@inheritDoc} */
    @Override
	public boolean equals(Object other) {
		if ( (this == other ) ) return true;
		if ( !(other instanceof ExperimentSpotDesignReadSpec) ) return false;
		ExperimentSpotDesignReadSpec castOther = (ExperimentSpotDesignReadSpec) other;
		return new EqualsBuilder()
			.append(this.getExperimentSpotDesignReadSpecId(), castOther.getExperimentSpotDesignReadSpecId())
			.isEquals();
	}

    /** {@inheritDoc} */
    @Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getExperimentSpotDesignReadSpecId())
			.toHashCode();
	}

	
	
	/**
	 * <p>Getter for the field <code>length</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getLength() {
    return length;
  }

  /**
   * <p>Setter for the field <code>length</code>.</p>
   *
   * @param length a {@link java.lang.Integer} object.
   */
  public void setLength(Integer length) {
    this.length = length;
  }

  /**
   * <p>Getter for the field <code>experimentSpotDesignReadSpecId</code>.</p>
   *
   * @return a {@link java.lang.Integer} object.
   */
  public Integer getExperimentSpotDesignReadSpecId() {
		return experimentSpotDesignReadSpecId;
	}

	/**
	 * <p>Setter for the field <code>experimentSpotDesignReadSpecId</code>.</p>
	 *
	 * @param experimentSpotDesignReadSpecId a {@link java.lang.Integer} object.
	 */
	public void setExperimentSpotDesignReadSpecId(
			Integer experimentSpotDesignReadSpecId) {
		this.experimentSpotDesignReadSpecId = experimentSpotDesignReadSpecId;
	}

	/**
	 * <p>Getter for the field <code>experimentSpotDesign</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesign} object.
	 */
	public ExperimentSpotDesign getExperimentSpotDesign() {
    return experimentSpotDesign;
  }

  /**
   * <p>Setter for the field <code>experimentSpotDesign</code>.</p>
   *
   * @param experimentSpotDesign a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesign} object.
   */
  public void setExperimentSpotDesign(ExperimentSpotDesign experimentSpotDesign) {
    this.experimentSpotDesign = experimentSpotDesign;
  }

  /**
   * <p>Getter for the field <code>readIndex</code>.</p>
   *
   * @return a {@link java.lang.Integer} object.
   */
  public Integer getReadIndex() {
		return readIndex;
	}

	/**
	 * <p>Setter for the field <code>readIndex</code>.</p>
	 *
	 * @param readIndex a {@link java.lang.Integer} object.
	 */
	public void setReadIndex(Integer readIndex) {
		this.readIndex = readIndex;
	}

	/**
	 * <p>Getter for the field <code>readLabel</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getReadLabel() {
		return readLabel;
	}

	/**
	 * <p>Setter for the field <code>readLabel</code>.</p>
	 *
	 * @param readLabel a {@link java.lang.String} object.
	 */
	public void setReadLabel(String readLabel) {
		this.readLabel = readLabel;
	}

	/**
	 * <p>Getter for the field <code>readClass</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getReadClass() {
		return readClass;
	}

	/**
	 * <p>Setter for the field <code>readClass</code>.</p>
	 *
	 * @param readClass a {@link java.lang.String} object.
	 */
	public void setReadClass(String readClass) {
		this.readClass = readClass;
	}

	/**
	 * <p>Getter for the field <code>readType</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getReadType() {
		return readType;
	}

	/**
	 * <p>Setter for the field <code>readType</code>.</p>
	 *
	 * @param readType a {@link java.lang.String} object.
	 */
	public void setReadType(String readType) {
		this.readType = readType;
	}

	/**
	 * <p>Getter for the field <code>baseCoord</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getBaseCoord() {
		return baseCoord;
	}

	/**
	 * <p>Setter for the field <code>baseCoord</code>.</p>
	 *
	 * @param baseCoord a {@link java.lang.Integer} object.
	 */
	public void setBaseCoord(Integer baseCoord) {
		this.baseCoord = baseCoord;
	}

	/**
	 * <p>Getter for the field <code>cycleCoord</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getCycleCoord() {
		return cycleCoord;
	}

	/**
	 * <p>Setter for the field <code>cycleCoord</code>.</p>
	 *
	 * @param cycleCoord a {@link java.lang.Integer} object.
	 */
	public void setCycleCoord(Integer cycleCoord) {
		this.cycleCoord = cycleCoord;
	}

	/**
	 * <p>Getter for the field <code>expectedBaseCall</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getExpectedBaseCall() {
		return expectedBaseCall;
	}

	/**
	 * <p>Setter for the field <code>expectedBaseCall</code>.</p>
	 *
	 * @param expectedBaseCall a {@link java.lang.String} object.
	 */
	public void setExpectedBaseCall(String expectedBaseCall) {
		this.expectedBaseCall = expectedBaseCall;
	}

}
