package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * <p>ShareWorkflowRun class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
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
	
	/**
	 * <p>Constructor for ShareWorkflowRun.</p>
	 */
	public ShareWorkflowRun() {
		super();
	}
	/** {@inheritDoc} */
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
        /** {@inheritDoc} */
        @Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("shareWorkflowRunId", getShareWorkflowRunId())
		//	.append("email", getEmail())
			.toString();
	}
        /** {@inheritDoc} */
        @Override
	public boolean equals(Object other) {
		if ( (this == other ) ) return true;
		if ( !(other instanceof WorkflowRun) ) return false;
		ShareWorkflowRun castOther = (ShareWorkflowRun) other;
		return new EqualsBuilder()
			.append(this.getShareWorkflowRunId(), castOther.getShareWorkflowRunId())
			.isEquals();
	}
        /** {@inheritDoc} */
        @Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getShareWorkflowRunId())
			.toHashCode();
	}
	
	/**
	 * <p>Getter for the field <code>shareWorkflowRunId</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getShareWorkflowRunId() {
		return shareWorkflowRunId;
	}
	/**
	 * <p>Setter for the field <code>shareWorkflowRunId</code>.</p>
	 *
	 * @param shareWorkflowRunId a {@link java.lang.Integer} object.
	 */
	public void setShareWorkflowRunId(Integer shareWorkflowRunId) {
		this.shareWorkflowRunId = shareWorkflowRunId;
	}
	
	/**
	 * <p>Getter for the field <code>workflowRunId</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getWorkflowRunId() {
		return workflowRunId;
	}
	/**
	 * <p>Setter for the field <code>workflowRunId</code>.</p>
	 *
	 * @param workflowRunId a {@link java.lang.Integer} object.
	 */
	public void setWorkflowRunId(Integer workflowRunId) {
		this.workflowRunId = workflowRunId;
	}

	/**
	 * <p>Getter for the field <code>registration</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.model.Registration} object.
	 */
	public Registration getRegistration() {
		return registration;
	}

	/**
	 * <p>Setter for the field <code>registration</code>.</p>
	 *
	 * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
	 */
	public void setRegistration(Registration registration) {
		this.registration = registration;
	}

	/**
	 * <p>Getter for the field <code>active</code>.</p>
	 *
	 * @return a {@link java.lang.Boolean} object.
	 */
	public Boolean getActive() {
		return active;
	}

	/**
	 * <p>Setter for the field <code>active</code>.</p>
	 *
	 * @param active a {@link java.lang.Boolean} object.
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}

	/**
	 * <p>Getter for the field <code>swAccession</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getSwAccession() {
		return swAccession;
	}

	/**
	 * <p>Setter for the field <code>swAccession</code>.</p>
	 *
	 * @param swAccession a {@link java.lang.Integer} object.
	 */
	public void setSwAccession(Integer swAccession) {
		this.swAccession = swAccession;
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
}
