package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.util.Date;
import java.util.SortedSet;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

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

	public Project() {
		super();
	}

    @Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("projectId", getProjectId())
			.append("name", getName())
			.toString();
	}

    @Override
	public boolean equals(Object other) {
		if ( (this == other ) ) return true;
		if ( !(other instanceof Project) ) return false;
		Project castOther = (Project) other;
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

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public Integer getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public SortedSet<Sample> getSamples() {
		return samples;
	}

	public void setSamples(SortedSet<Sample> samples) {
		this.samples = samples;
	}

}
