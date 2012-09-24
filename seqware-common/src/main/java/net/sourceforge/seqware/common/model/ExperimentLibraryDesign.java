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

public class ExperimentLibraryDesign implements Serializable, Comparable<ExperimentLibraryDesign> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer experimentLibraryDesignId;
	private String	name;
	private String	description;
	private String  constructionProtocol;
	private LibraryStrategy  strategy;
	private LibrarySource  source;
	private LibrarySelection  selection;
	private String  layout;
	private String  pairedOrientation;
	private Integer  nominalLength;
	private Long  nominalSdev;
	
	public ExperimentLibraryDesign() {
		super();
	}
	
	public int compareTo(ExperimentLibraryDesign that) {
		if(that == null)
			return -1;

		if(that.getName() == this.getName())	// when both names are null
			return 0;

		if(that.getName() == null)
			return -1;							// when only the other name is null

		return(that.getName().compareTo(this.getName()));
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("experimentLibraryDesignId", getExperimentLibraryDesignId())
			.append("name", getName())
			.toString();
	}

	public boolean equals(Object other) {
		if ( (this == other ) ) return true;
		if ( !(other instanceof ExperimentLibraryDesign) ) return false;
		ExperimentLibraryDesign castOther = (ExperimentLibraryDesign) other;
		return new EqualsBuilder()
			.append(this.getName(), castOther.getName())
			.isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder()
			.append(getName())
			.toHashCode();
	}

	public Integer getExperimentLibraryDesignId() {
		return experimentLibraryDesignId;
	}

	public void setExperimentLibraryDesignId(Integer experimentLibraryDesignId) {
		this.experimentLibraryDesignId = experimentLibraryDesignId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getConstructionProtocol() {
		return constructionProtocol;
	}

	public void setConstructionProtocol(String constructionProtocol) {
		this.constructionProtocol = constructionProtocol;
	}

	public LibraryStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(LibraryStrategy strategy) {
		this.strategy = strategy;
	}

	public LibrarySource getSource() {
		return source;
	}

	public void setSource(LibrarySource source) {
		this.source = source;
	}

	public LibrarySelection getSelection() {
		return selection;
	}

	public void setSelection(LibrarySelection selection) {
		this.selection = selection;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public String getPairedOrientation() {
		return pairedOrientation;
	}

	public void setPairedOrientation(String pairedOrientation) {
		this.pairedOrientation = pairedOrientation;
	}

	public Integer getNominalLength() {
    return nominalLength;
  }

  public void setNominalLength(Integer nominalLength) {
    this.nominalLength = nominalLength;
  }

  public Long getNominalSdev() {
    return nominalSdev;
  }

  public void setNominalSdev(Long nominalSdev) {
    this.nominalSdev = nominalSdev;
  }

  public static long getSerialVersionUID() {
		return serialVersionUID;
	}

}
