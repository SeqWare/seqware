package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

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
	
	public ShareStudy() {
		super();
	}
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
        @Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("shareStudyId", getShareStudyId())
	//		.append("email", getEmail())
			.toString();
	}
        @Override
	public boolean equals(Object other) {
		if ( (this == other ) ) return true;
		if ( !(other instanceof Study) ) return false;
		ShareStudy castOther = (ShareStudy) other;
		return new EqualsBuilder()
			.append(this.getShareStudyId(), castOther.getShareStudyId())
			.isEquals();
	}
        @Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getShareStudyId())
			.toHashCode();
	}
	
	public Integer getShareStudyId() {
		return shareStudyId;
	}
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
	public Integer getStudyId() {
		return studyId;
	}
	
	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
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
