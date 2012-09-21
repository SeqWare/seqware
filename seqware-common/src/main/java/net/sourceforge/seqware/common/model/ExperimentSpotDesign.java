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

	
	public ExperimentSpotDesign() {
		super();
	}
	
	public int compareTo(ExperimentSpotDesign that) {
		if(that == null)
			return -1;

		if(that.getExperimentSpotDesignId() == this.getExperimentSpotDesignId())	// when both names are null
			return 0;

		if(that.getExperimentSpotDesignId() == null)
			return -1;							// when only the other name is null

		return(that.getExperimentSpotDesignId().compareTo(this.getExperimentSpotDesignId()));
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("experimentSpotDesignId", getExperimentSpotDesignId())
			.toString();
	}

	public boolean equals(Object other) {
		if ( (this == other ) ) return true;
		if ( !(other instanceof ExperimentSpotDesign) ) return false;
		ExperimentSpotDesign castOther = (ExperimentSpotDesign) other;
		return new EqualsBuilder()
			.append(this.getExperimentSpotDesignId(), castOther.getExperimentSpotDesignId())
			.isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder()
			.append(getExperimentSpotDesignId())
			.toHashCode();
	}
	
	public Integer getExperimentSpotDesignId() {
		return experimentSpotDesignId;
	}

	public void setExperimentSpotDesignId(Integer experimentSpotDesignId) {
		this.experimentSpotDesignId = experimentSpotDesignId;
	}

	public Integer getDecodeMethod() {
		return decodeMethod;
	}

	public void setDecodeMethod(Integer decodeMethod) {
		this.decodeMethod = decodeMethod;
	}

	public Integer getReadsPerSpot() {
		return readsPerSpot;
	}

	public void setReadsPerSpot(Integer readsPerSpot) {
		this.readsPerSpot = readsPerSpot;
	}

	public String getReadSpec() {
		return readSpec;
	}

	public void setReadSpec(String readSpec) {
		this.readSpec = readSpec;
	}

	public String getTagSpec() {
		return tagSpec;
	}

	public void setTagSpec(String tagSpec) {
		this.tagSpec = tagSpec;
	}

	public String getAdapterSpec() {
		return adapterSpec;
	}

	public void setAdapterSpec(String adapterSpec) {
		this.adapterSpec = adapterSpec;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public SortedSet<ExperimentSpotDesignReadSpec> getReadSpecs() {
		return readSpecs;
	}

	public void setReadSpecs(SortedSet<ExperimentSpotDesignReadSpec> readSpecs) {
		this.readSpecs = readSpecs;
	}

}
