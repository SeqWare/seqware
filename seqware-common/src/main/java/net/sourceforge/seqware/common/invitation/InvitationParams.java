package net.sourceforge.seqware.common.invitation;

public class InvitationParams {

	String isInvitationCode;
	String subjectEmail;
	String templateEmail;
	
	public InvitationParams(String isInvitationCode, String subjectEmail, String templateEmail) {
		this.isInvitationCode = isInvitationCode;
		this.subjectEmail = subjectEmail;
		this.templateEmail = templateEmail;
	}
	
	public String getIsInvitationCode() {
		return isInvitationCode;
	}
	
	public String getSubjectEmail() {
		return subjectEmail;
	}
	
	public String getTemplateEmail() {
		return templateEmail;
	}
}
