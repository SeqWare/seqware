package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * <p>ExperimentSpotDesign class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ExperimentSpotDesign implements Serializable, Comparable<ExperimentSpotDesign> {
	/**
	 * 
	 * LEFT OFF WITH: NEED TO FINISH THIS OBJECT
	 * 
	 */
	private static final long serialVersionUID = 173283927487293893L;
	private Integer experimentSpotDesignId;
	private Integer decodeMethod;
	private Integer readsPerSpot;
	private String	readSpec;
	private String	tagSpec;
	private String  adapterSpec;
	private SortedSet<ExperimentSpotDesignReadSpec>	readSpecs = new TreeSet<ExperimentSpotDesignReadSpec>();

	
	/**
	 * <p>Constructor for ExperimentSpotDesign.</p>
	 */
	public ExperimentSpotDesign() {
		super();
	}
	
	/**
	 * <p>compareTo.</p>
	 *
	 * @param that a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesign} object.
	 * @return a int.
	 */
	public int compareTo(ExperimentSpotDesign that) {
		if(that == null)
			return -1;

		if(that.getExperimentSpotDesignId() == this.getExperimentSpotDesignId())	// when both names are null
			return 0;

		if(that.getExperimentSpotDesignId() == null)
			return -1;							// when only the other name is null

		return(that.getExperimentSpotDesignId().compareTo(this.getExperimentSpotDesignId()));
	}

	/**
	 * <p>toString.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String toString() {
		return new ToStringBuilder(this)
			.append("experimentSpotDesignId", getExperimentSpotDesignId())
			.toString();
	}

	/** {@inheritDoc} */
	public boolean equals(Object other) {
		if ( (this == other ) ) return true;
		if ( !(other instanceof ExperimentSpotDesign) ) return false;
		ExperimentSpotDesign castOther = (ExperimentSpotDesign) other;
		return new EqualsBuilder()
			.append(this.getExperimentSpotDesignId(), castOther.getExperimentSpotDesignId())
			.isEquals();
	}

	/**
	 * <p>hashCode.</p>
	 *
	 * @return a int.
	 */
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getExperimentSpotDesignId())
			.toHashCode();
	}
	
	/**
	 * <p>Getter for the field <code>experimentSpotDesignId</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getExperimentSpotDesignId() {
		return experimentSpotDesignId;
	}

	/**
	 * <p>Setter for the field <code>experimentSpotDesignId</code>.</p>
	 *
	 * @param experimentSpotDesignId a {@link java.lang.Integer} object.
	 */
	public void setExperimentSpotDesignId(Integer experimentSpotDesignId) {
		this.experimentSpotDesignId = experimentSpotDesignId;
	}

	/**
	 * <p>Getter for the field <code>decodeMethod</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getDecodeMethod() {
		return decodeMethod;
	}

	/**
	 * <p>Setter for the field <code>decodeMethod</code>.</p>
	 *
	 * @param decodeMethod a {@link java.lang.Integer} object.
	 */
	public void setDecodeMethod(Integer decodeMethod) {
		this.decodeMethod = decodeMethod;
	}

	/**
	 * <p>Getter for the field <code>readsPerSpot</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getReadsPerSpot() {
		return readsPerSpot;
	}

	/**
	 * <p>Setter for the field <code>readsPerSpot</code>.</p>
	 *
	 * @param readsPerSpot a {@link java.lang.Integer} object.
	 */
	public void setReadsPerSpot(Integer readsPerSpot) {
		this.readsPerSpot = readsPerSpot;
	}

	/**
	 * <p>Getter for the field <code>readSpec</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getReadSpec() {
		return readSpec;
	}

	/**
	 * <p>Setter for the field <code>readSpec</code>.</p>
	 *
	 * @param readSpec a {@link java.lang.String} object.
	 */
	public void setReadSpec(String readSpec) {
		this.readSpec = readSpec;
	}

	/**
	 * <p>Getter for the field <code>tagSpec</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getTagSpec() {
		return tagSpec;
	}

	/**
	 * <p>Setter for the field <code>tagSpec</code>.</p>
	 *
	 * @param tagSpec a {@link java.lang.String} object.
	 */
	public void setTagSpec(String tagSpec) {
		this.tagSpec = tagSpec;
	}

	/**
	 * <p>Getter for the field <code>adapterSpec</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getAdapterSpec() {
		return adapterSpec;
	}

	/**
	 * <p>Setter for the field <code>adapterSpec</code>.</p>
	 *
	 * @param adapterSpec a {@link java.lang.String} object.
	 */
	public void setAdapterSpec(String adapterSpec) {
		this.adapterSpec = adapterSpec;
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
	 * <p>Getter for the field <code>readSpecs</code>.</p>
	 *
	 * @return a {@link java.util.SortedSet} object.
	 */
	public SortedSet<ExperimentSpotDesignReadSpec> getReadSpecs() {
		return readSpecs;
	}

	/**
	 * <p>Setter for the field <code>readSpecs</code>.</p>
	 *
	 * @param readSpecs a {@link java.util.SortedSet} object.
	 */
	public void setReadSpecs(SortedSet<ExperimentSpotDesignReadSpec> readSpecs) {
		this.readSpecs = readSpecs;
	}

}
