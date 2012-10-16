package net.sourceforge.seqware.common.model;							// -*- tab-width: 4 -*-

import javax.xml.bind.annotation.XmlTransient;


/* DTO == Data{Transfer,Temporary}Object ??
 * Note that the xDTO subclass adds the extra confirmation fields for
 * implementing the create/update forms which are not stored into the database
 * and hence require this seperate class to implement the form.
 */
@XmlTransient
/**
 * <p>RegistrationDTO class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class RegistrationDTO extends Registration {
	private static final long serialVersionUID = 8465486434105955512L;
	private String			confirmEmailAddress;
	private String			confirmPassword;
	private Registration	domainObject;

	/**
	 * <p>Constructor for RegistrationDTO.</p>
	 */
	public RegistrationDTO() {
			super();
	}

	/**
	 * <p>Getter for the field <code>confirmEmailAddress</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getConfirmEmailAddress() {
			return confirmEmailAddress;
	}

	/**
	 * <p>Setter for the field <code>confirmEmailAddress</code>.</p>
	 *
	 * @param confirmEmailAddress a {@link java.lang.String} object.
	 */
	public void setConfirmEmailAddress(String confirmEmailAddress) {
			this.confirmEmailAddress = confirmEmailAddress;
	}

	/**
	 * <p>Getter for the field <code>confirmPassword</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getConfirmPassword() {
			return confirmPassword;
	}

	/**
	 * <p>Setter for the field <code>confirmPassword</code>.</p>
	 *
	 * @param confirmPassword a {@link java.lang.String} object.
	 */
	public void setConfirmPassword(String confirmPassword) {
			this.confirmPassword = confirmPassword;
	}

	/**
	 * <p>Getter for the field <code>domainObject</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.model.Registration} object.
	 */
	public Registration getDomainObject() {
			return domainObject;
	}

	/**
	 * <p>Setter for the field <code>domainObject</code>.</p>
	 *
	 * @param domainObject a {@link net.sourceforge.seqware.common.model.Registration} object.
	 */
	public void setDomainObject(Registration domainObject) {
			this.domainObject = domainObject;
	}
}

// ex:sw=4:ts=4:
