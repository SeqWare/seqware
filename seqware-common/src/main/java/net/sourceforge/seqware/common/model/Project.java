package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.util.Date;
import java.util.SortedSet;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * <p>Project class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class Project implements Serializable {
	private static final long serialVersionUID = 3132811528293390568L;
	private Integer projectId;
	private Integer	ownerId;
	private String	name;
	private String	description;
	private String	status;
	private Date	createTimestamp;
	private Date	updateTimestamp;

	private SortedSet<Sample> samples;

	/**
	 * <p>Constructor for Project.</p>
	 */
	public Project() {
		super();
	}

    /** {@inheritDoc} */
    @Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("projectId", getProjectId())
			.append("name", getName())
			.toString();
	}

    /** {@inheritDoc} */
    @Override
	public boolean equals(Object other) {
		if ( (this == other ) ) return true;
		if ( !(other instanceof Project) ) return false;
		Project castOther = (Project) other;
		return new EqualsBuilder()
			.append(this.getName(), castOther.getName())
			.isEquals();
	}

    /** {@inheritDoc} */
    @Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getName())
			.toHashCode();
	}

	/**
	 * <p>Getter for the field <code>projectId</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getProjectId() {
		return projectId;
	}

	/**
	 * <p>Setter for the field <code>projectId</code>.</p>
	 *
	 * @param projectId a {@link java.lang.Integer} object.
	 */
	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	/**
	 * <p>Getter for the field <code>ownerId</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getOwnerId() {
		return ownerId;
	}

	/**
	 * <p>Setter for the field <code>ownerId</code>.</p>
	 *
	 * @param ownerId a {@link java.lang.Integer} object.
	 */
	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
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
	 * <p>Getter for the field <code>status</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * <p>Setter for the field <code>status</code>.</p>
	 *
	 * @param status a {@link java.lang.String} object.
	 */
	public void setStatus(String status) {
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
	 * <p>Setter for the field <code>name</code>.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * <p>Getter for the field <code>samples</code>.</p>
	 *
	 * @return a {@link java.util.SortedSet} object.
	 */
	public SortedSet<Sample> getSamples() {
		return samples;
	}

	/**
	 * <p>Setter for the field <code>samples</code>.</p>
	 *
	 * @param samples a {@link java.util.SortedSet} object.
	 */
	public void setSamples(SortedSet<Sample> samples) {
		this.samples = samples;
	}

}
