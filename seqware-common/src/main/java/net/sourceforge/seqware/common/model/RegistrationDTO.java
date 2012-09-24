package net.sourceforge.seqware.common.model;							// -*- tab-width: 4 -*-

import javax.xml.bind.annotation.XmlTransient;


/* DTO == Data{Transfer,Temporary}Object ??
 * Note that the xDTO subclass adds the extra confirmation fields for
 * implementing the create/update forms which are not stored into the database
 * and hence require this seperate class to implement the form.
 */
@XmlTransient
public class RegistrationDTO extends Registration {
	private static final long serialVersionUID = 8465486434105955512L;
	private String			confirmEmailAddress;
	private String			confirmPassword;
	private Registration	domainObject;

	public RegistrationDTO() {
			super();
	}

	public String getConfirmEmailAddress() {
			return confirmEmailAddress;
	}

	public void setConfirmEmailAddress(String confirmEmailAddress) {
			this.confirmEmailAddress = confirmEmailAddress;
	}

	public String getConfirmPassword() {
			return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
			this.confirmPassword = confirmPassword;
	}

	public Registration getDomainObject() {
			return domainObject;
	}

	public void setDomainObject(Registration domainObject) {
			this.domainObject = domainObject;
	}
}

// ex:sw=4:ts=4:
