package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * <p>ShareStudy class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ShareStudy implements Serializable, Comparable<ShareStudy>{
	
	private static final long serialVersionUID = 1L;

	private Integer shareStudyId;
	private Integer studyId;
//	private Integer registrationId;
	private Registration registration;
	private Boolean active;
	private Integer swAccession;
	private Date 	createTimestamp;
	private Date 	updateTimestamp;
//	private String  email;
	
	/**
	 * <p>Constructor for ShareStudy.</p>
	 */
	public ShareStudy() {
		super();
	}
	/** {@inheritDoc} */
	@Override
	public int compareTo(ShareStudy that) {
		if(that == null)
			return -1;

		if(that.getShareStudyId() == this.getShareStudyId())	// when both names are null
			return 0;

		if(that.getShareStudyId() == null)
			return -1;							// when only the other name is null

		return(that.getShareStudyId().compareTo(this.getShareStudyId()));
	}
        /** {@inheritDoc} */
        @Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("shareStudyId", getShareStudyId())
	//		.append("email", getEmail())
			.toString();
	}
        /** {@inheritDoc} */
        @Override
	public boolean equals(Object other) {
		if ( (this == other ) ) return true;
		if ( !(other instanceof Study) ) return false;
		ShareStudy castOther = (ShareStudy) other;
		return new EqualsBuilder()
			.append(this.getShareStudyId(), castOther.getShareStudyId())
			.isEquals();
	}
        /** {@inheritDoc} */
        @Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getShareStudyId())
			.toHashCode();
	}
	
	/**
	 * <p>Getter for the field <code>shareStudyId</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getShareStudyId() {
		return shareStudyId;
	}
	/**
	 * <p>Setter for the field <code>shareStudyId</code>.</p>
	 *
	 * @param shareStudyId a {@link java.lang.Integer} object.
	 */
	public void setShareStudyId(Integer shareStudyId) {
		this.shareStudyId = shareStudyId;
	}
	
/*	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
*/	
	/**
	 * <p>Getter for the field <code>studyId</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getStudyId() {
		return studyId;
	}
	
	/**
	 * <p>Setter for the field <code>studyId</code>.</p>
	 *
	 * @param studyId a {@link java.lang.Integer} object.
	 */
	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
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
