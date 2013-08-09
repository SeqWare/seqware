package net.sourceforge.seqware.common.invitation;

/**
 * <p>InvitationParams class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class InvitationParams {

	boolean isInvitationCode;
	String subjectEmail;
	String templateEmail;
	
	/**
	 * <p>Constructor for InvitationParams.</p>
	 */
	public InvitationParams(boolean isInvitationCode, String subjectEmail, String templateEmail) {
		this.isInvitationCode = isInvitationCode;
		this.subjectEmail = subjectEmail;
		this.templateEmail = templateEmail;
	}
	
	/**
	 * <p>Getter for the field <code>isInvitationCode</code>.</p>
	 */
	public boolean getIsInvitationCode() {
		return isInvitationCode;
	}
	
	/**
	 * <p>Getter for the field <code>subjectEmail</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getSubjectEmail() {
		return subjectEmail;
	}
	
	/**
	 * <p>Getter for the field <code>templateEmail</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getTemplateEmail() {
		return templateEmail;
	}
}
