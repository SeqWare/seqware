package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class ShareWorkflowRun implements Serializable, Comparable<ShareWorkflowRun>{
	
	private static final long serialVersionUID = 1L;

	private Integer shareWorkflowRunId;
	private Integer workflowRunId;
	private Registration registration;
	private Boolean active;
	private Integer swAccession;
	private Date 	createTimestamp;
	private Date 	updateTimestamp;
	//private String  email;
	
	public ShareWorkflowRun() {
		super();
	}
	@Override
	public int compareTo(ShareWorkflowRun that) {
		if(that == null)
			return -1;

		if(that.getShareWorkflowRunId() == this.getShareWorkflowRunId())	// when both names are null
			return 0;

		if(that.getShareWorkflowRunId() == null)
			return -1;							// when only the other name is null

		return(that.getShareWorkflowRunId().compareTo(this.getShareWorkflowRunId()));
	}
        @Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("shareWorkflowRunId", getShareWorkflowRunId())
		//	.append("email", getEmail())
			.toString();
	}
        @Override
	public boolean equals(Object other) {
		if ( (this == other ) ) return true;
		if ( !(other instanceof WorkflowRun) ) return false;
		ShareWorkflowRun castOther = (ShareWorkflowRun) other;
		return new EqualsBuilder()
			.append(this.getShareWorkflowRunId(), castOther.getShareWorkflowRunId())
			.isEquals();
	}
        @Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getShareWorkflowRunId())
			.toHashCode();
	}
	
	public Integer getShareWorkflowRunId() {
		return shareWorkflowRunId;
	}
	public void setShareWorkflowRunId(Integer shareWorkflowRunId) {
		this.shareWorkflowRunId = shareWorkflowRunId;
	}
	
	public Integer getWorkflowRunId() {
		return workflowRunId;
	}
	public void setWorkflowRunId(Integer workflowRunId) {
		this.workflowRunId = workflowRunId;
	}

	public Registration getRegistration() {
		return registration;
	}

	public void setRegistration(Registration registration) {
		this.registration = registration;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Integer getSwAccession() {
		return swAccession;
	}

	public void setSwAccession(Integer swAccession) {
		this.swAccession = swAccession;
	}

	public Date getCreateTimestamp() {
		return createTimestamp;
	}

	public void setCreateTimestamp(Date createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public Date getUpdateTimestamp() {
		return updateTimestamp;
	}

	public void setUpdateTimestamp(Date updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}
}
