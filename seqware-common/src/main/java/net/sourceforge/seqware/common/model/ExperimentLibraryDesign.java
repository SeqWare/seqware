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
 * <p>ExperimentLibraryDesign class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ExperimentLibraryDesign implements Serializable, Comparable<ExperimentLibraryDesign>, SecondTierModel {
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
	
	/**
	 * <p>Constructor for ExperimentLibraryDesign.</p>
	 */
	public ExperimentLibraryDesign() {
		super();
	}
	
	/**
	 * <p>compareTo.</p>
	 *
	 * @param that a {@link net.sourceforge.seqware.common.model.ExperimentLibraryDesign} object.
	 * @return a int.
	 */
	public int compareTo(ExperimentLibraryDesign that) {
		if(that == null)
			return -1;

		if(that.getName() == this.getName())	// when both names are null
			return 0;

		if(that.getName() == null)
			return -1;							// when only the other name is null

		return(that.getName().compareTo(this.getName()));
	}

	/**
	 * <p>toString.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String toString() {
		return new ToStringBuilder(this)
			.append("experimentLibraryDesignId", getExperimentLibraryDesignId())
			.append("name", getName())
			.toString();
	}

	/** {@inheritDoc} */
	public boolean equals(Object other) {
		if ( (this == other ) ) return true;
		if ( !(other instanceof ExperimentLibraryDesign) ) return false;
		ExperimentLibraryDesign castOther = (ExperimentLibraryDesign) other;
		return new EqualsBuilder()
			.append(this.getName(), castOther.getName())
			.isEquals();
	}

	/**
	 * <p>hashCode.</p>
	 *
	 * @return a int.
	 */
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getName())
			.toHashCode();
	}

	/**
	 * <p>Getter for the field <code>experimentLibraryDesignId</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getExperimentLibraryDesignId() {
		return experimentLibraryDesignId;
	}

	/**
	 * <p>Setter for the field <code>experimentLibraryDesignId</code>.</p>
	 *
	 * @param experimentLibraryDesignId a {@link java.lang.Integer} object.
	 */
	public void setExperimentLibraryDesignId(Integer experimentLibraryDesignId) {
		this.experimentLibraryDesignId = experimentLibraryDesignId;
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
	 * <p>Setter for the field <code>name</code>.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 */
	public void setName(String name) {
		this.name = name;
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
	 * <p>Getter for the field <code>constructionProtocol</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getConstructionProtocol() {
		return constructionProtocol;
	}

	/**
	 * <p>Setter for the field <code>constructionProtocol</code>.</p>
	 *
	 * @param constructionProtocol a {@link java.lang.String} object.
	 */
	public void setConstructionProtocol(String constructionProtocol) {
		this.constructionProtocol = constructionProtocol;
	}

	/**
	 * <p>Getter for the field <code>strategy</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.model.LibraryStrategy} object.
	 */
	public LibraryStrategy getStrategy() {
		return strategy;
	}

	/**
	 * <p>Setter for the field <code>strategy</code>.</p>
	 *
	 * @param strategy a {@link net.sourceforge.seqware.common.model.LibraryStrategy} object.
	 */
	public void setStrategy(LibraryStrategy strategy) {
		this.strategy = strategy;
	}

	/**
	 * <p>Getter for the field <code>source</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.model.LibrarySource} object.
	 */
	public LibrarySource getSource() {
		return source;
	}

	/**
	 * <p>Setter for the field <code>source</code>.</p>
	 *
	 * @param source a {@link net.sourceforge.seqware.common.model.LibrarySource} object.
	 */
	public void setSource(LibrarySource source) {
		this.source = source;
	}

	/**
	 * <p>Getter for the field <code>selection</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.model.LibrarySelection} object.
	 */
	public LibrarySelection getSelection() {
		return selection;
	}

	/**
	 * <p>Setter for the field <code>selection</code>.</p>
	 *
	 * @param selection a {@link net.sourceforge.seqware.common.model.LibrarySelection} object.
	 */
	public void setSelection(LibrarySelection selection) {
		this.selection = selection;
	}

	/**
	 * <p>Getter for the field <code>layout</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getLayout() {
		return layout;
	}

	/**
	 * <p>Setter for the field <code>layout</code>.</p>
	 *
	 * @param layout a {@link java.lang.String} object.
	 */
	public void setLayout(String layout) {
		this.layout = layout;
	}

	/**
	 * <p>Getter for the field <code>pairedOrientation</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getPairedOrientation() {
		return pairedOrientation;
	}

	/**
	 * <p>Setter for the field <code>pairedOrientation</code>.</p>
	 *
	 * @param pairedOrientation a {@link java.lang.String} object.
	 */
	public void setPairedOrientation(String pairedOrientation) {
		this.pairedOrientation = pairedOrientation;
	}

	/**
	 * <p>Getter for the field <code>nominalLength</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getNominalLength() {
    return nominalLength;
  }

  /**
   * <p>Setter for the field <code>nominalLength</code>.</p>
   *
   * @param nominalLength a {@link java.lang.Integer} object.
   */
  public void setNominalLength(Integer nominalLength) {
    this.nominalLength = nominalLength;
  }

  /**
   * <p>Getter for the field <code>nominalSdev</code>.</p>
   *
   * @return a {@link java.lang.Long} object.
   */
  public Long getNominalSdev() {
    return nominalSdev;
  }

  /**
   * <p>Setter for the field <code>nominalSdev</code>.</p>
   *
   * @param nominalSdev a {@link java.lang.Long} object.
   */
  public void setNominalSdev(Long nominalSdev) {
    this.nominalSdev = nominalSdev;
  }

  /**
   * <p>Getter for the field <code>serialVersionUID</code>.</p>
   *
   * @return a long.
   */
  public static long getSerialVersionUID() {
		return serialVersionUID;
	}

    @Override
    public int getModelId() {
        return getExperimentLibraryDesignId();
    }

}
