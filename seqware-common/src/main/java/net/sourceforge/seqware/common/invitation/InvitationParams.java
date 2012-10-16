package net.sourceforge.seqware.common.invitation;

/**
 * <p>InvitationParams class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class InvitationParams {

	String isInvitationCode;
	String subjectEmail;
	String templateEmail;
	
	/**
	 * <p>Constructor for InvitationParams.</p>
	 *
	 * @param isInvitationCode a {@link java.lang.String} object.
	 * @param subjectEmail a {@link java.lang.String} object.
	 * @param templateEmail a {@link java.lang.String} object.
	 */
	public InvitationParams(String isInvitationCode, String subjectEmail, String templateEmail) {
		this.isInvitationCode = isInvitationCode;
		this.subjectEmail = subjectEmail;
		this.templateEmail = templateEmail;
	}
	
	/**
	 * <p>Getter for the field <code>isInvitationCode</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getIsInvitationCode() {
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
