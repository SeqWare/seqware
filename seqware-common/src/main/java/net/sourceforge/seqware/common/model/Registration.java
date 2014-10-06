package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import net.sourceforge.seqware.common.security.PermissionsAware;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registration
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class Registration extends PermissionsAware implements Serializable, Comparable<Registration> {

    /**
     * <p>
     * Getter for the field <code>institution</code>.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getInstitution() {
        return institution;
    }

    /**
     * <p>
     * Setter for the field <code>institution</code>.
     * </p>
     * 
     * @param institution
     *            a {@link java.lang.String} object.
     */
    public void setInstitution(String institution) {
        this.institution = institution;
    }

    private static final long serialVersionUID = -6290711918776330816L;
    private Integer registrationId;
    private String emailAddress;
    private String password;
    private String passwordHint;
    private String firstName;
    private String lastName;
    private String institution;
    private boolean limsAdmin;
    private boolean payee;
    private Date createTimestamp;
    private Date updateTimestamp;
    private String invitationCode;
    private boolean joinUsersMailingList;
    private boolean joinDevelopersMailingList;
    final Logger logger = LoggerFactory.getLogger(Registration.class);

    /**
     * <p>
     * isJoinDevelopersMailingList.
     * </p>
     * 
     * @return a boolean.
     */
    public boolean isJoinDevelopersMailingList() {
        return joinDevelopersMailingList;
    }

    /**
     * <p>
     * Getter for the field <code>joinDevelopersMailingList</code>.
     * </p>
     * 
     * @return a boolean.
     */
    public boolean getJoinDevelopersMailingList() {
        return joinDevelopersMailingList;
    }

    /**
     * <p>
     * Setter for the field <code>joinDevelopersMailingList</code>.
     * </p>
     * 
     * @param b
     *            a boolean.
     */
    public void setJoinDevelopersMailingList(boolean b) {
        this.joinDevelopersMailingList = b;
    }

    /**
     * <p>
     * isJoinUsersMailingList.
     * </p>
     * 
     * @return a boolean.
     */
    public boolean isJoinUsersMailingList() {
        return joinUsersMailingList;
    }

    /**
     * <p>
     * Getter for the field <code>joinUsersMailingList</code>.
     * </p>
     * 
     * @return a boolean.
     */
    public boolean getJoinUsersMailingList() {
        return joinUsersMailingList;
    }

    /**
     * <p>
     * Setter for the field <code>joinUsersMailingList</code>.
     * </p>
     * 
     * @param b
     *            a boolean.
     */
    public void setJoinUsersMailingList(boolean b) {
        this.joinUsersMailingList = b;
    }

    /**
     * <p>
     * Constructor for Registration.
     * </p>
     */
    public Registration() {
        super();
    }

    // public boolean equals(Object other) {
    // if ((this == other)) {
    // return true;
    // }
    // if (!(other instanceof Registration)) {
    // return false;
    // }
    // boolean result = false;
    // Registration castOther = (Registration) other;
    // if (this.getRegistrationId().equals(castOther.getRegistrationId())) {
    // result = true;
    // } else if (new EqualsBuilder().append(this.getEmailAddress(), castOther.getEmailAddress()).append(this.getPassword(),
    // castOther.getPassword()).isEquals()) {
    // result = true;
    // }
    // return (result);
    // }
    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            logger.warn("Compared-to object is null");
            return false;
        }
        if (!(obj instanceof Registration)) {
            logger.warn("Compared-to object is different class");
            return false;
        }
        final Registration other = (Registration) obj;
        if (this.compareTo((Registration) obj) != 0) {
            logger.warn("Compared-to object has different email");
            return false;
        }
        logger.warn("Compared-to object is equivalent");
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @param t
     */
    @Override
    public int compareTo(Registration t) {
        return emailAddress.compareTo(t.getEmailAddress());
    }

    /**
     * <p>
     * hashCode.
     * </p>
     * 
     * @return a int.
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getEmailAddress()).append(getPassword()).toHashCode();
    }

    /*
     * public boolean equals(Object other) { if (this == other) return true; if (!(other instanceof Registration)) return false;
     * Registration castOther = (Registration) other; return this.getEmailAddress().equals(castOther.getEmailAddress()) &&
     * this.getPassword().equals(castOther.getPassword()); }
     * 
     * public int hashCode() { return getEmailAddress().hashCode() ^ getPassword().hashCode(); }
     */
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Registration{" + "registrationId=" + registrationId + ", emailAddress=" + emailAddress + ", passwordHint=" + passwordHint
                + ", firstName=" + firstName + ", lastName=" + lastName + ", institution=" + institution + ", limsAdmin=" + limsAdmin
                + ", createTimestamp=" + createTimestamp + ", updateTimestamp=" + updateTimestamp + ", invitationCode=" + invitationCode
                + ", joinUsersMailingList=" + joinUsersMailingList + ", joinDevelopersMailingList=" + joinDevelopersMailingList + '}';
    }

    /**
     * <p>
     * Getter for the field <code>createTimestamp</code>.
     * </p>
     * 
     * @return a {@link java.util.Date} object.
     */
    public Date getCreateTimestamp() {
        return createTimestamp;
    }

    /**
     * <p>
     * Setter for the field <code>createTimestamp</code>.
     * </p>
     * 
     * @param createTimestamp
     *            a {@link java.util.Date} object.
     */
    public void setCreateTimestamp(Date createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    /**
     * <p>
     * Getter for the field <code>emailAddress</code>.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * <p>
     * Setter for the field <code>emailAddress</code>.
     * </p>
     * 
     * @param emailAddress
     *            a {@link java.lang.String} object.
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * <p>
     * Getter for the field <code>firstName</code>.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * <p>
     * Setter for the field <code>firstName</code>.
     * </p>
     * 
     * @param firstName
     *            a {@link java.lang.String} object.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * <p>
     * Getter for the field <code>lastName</code>.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * <p>
     * Setter for the field <code>lastName</code>.
     * </p>
     * 
     * @param lastName
     *            a {@link java.lang.String} object.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * <p>
     * Getter for the field <code>password</code>.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getPassword() {
        return password;
    }

    /**
     * <p>
     * Setter for the field <code>password</code>.
     * </p>
     * 
     * @param password
     *            a {@link java.lang.String} object.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * <p>
     * Getter for the field <code>passwordHint</code>.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getPasswordHint() {
        return passwordHint;
    }

    /**
     * <p>
     * Setter for the field <code>passwordHint</code>.
     * </p>
     * 
     * @param passwordHint
     *            a {@link java.lang.String} object.
     */
    public void setPasswordHint(String passwordHint) {
        this.passwordHint = passwordHint;
    }

    /**
     * <p>
     * Getter for the field <code>registrationId</code>.
     * </p>
     * 
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getRegistrationId() {
        return registrationId;
    }

    /**
     * <p>
     * Setter for the field <code>registrationId</code>.
     * </p>
     * 
     * @param registrationId
     *            a {@link java.lang.Integer} object.
     */
    public void setRegistrationId(Integer registrationId) {
        this.registrationId = registrationId;
    }

    /**
     * <p>
     * Getter for the field <code>updateTimestamp</code>.
     * </p>
     * 
     * @return a {@link java.util.Date} object.
     */
    public Date getUpdateTimestamp() {
        return updateTimestamp;
    }

    /**
     * <p>
     * Setter for the field <code>updateTimestamp</code>.
     * </p>
     * 
     * @param updateTimestamp
     *            a {@link java.util.Date} object.
     */
    public void setUpdateTimestamp(Date updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    /**
     * <p>
     * isLIMSAdmin.
     * </p>
     * 
     * @return a boolean.
     */
    public boolean isLIMSAdmin() {
        return limsAdmin;
    }

    /**
     * <p>
     * setLIMSAdmin.
     * </p>
     * 
     * @param b
     *            a boolean.
     */
    public void setLIMSAdmin(boolean b) {
        this.limsAdmin = b;
    }

    /**
     * <p>
     * Getter for the field <code>invitationCode</code>.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getInvitationCode() {
        return invitationCode;
    }

    /**
     * <p>
     * Setter for the field <code>invitationCode</code>.
     * </p>
     * 
     * @param invitationCode
     *            a {@link java.lang.String} object.
     */
    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    /**
     * <p>
     * isTechnician.
     * </p>
     * 
     * @return a boolean.
     */
    public boolean isTechnician() {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public boolean givesPermissionInternal(Registration registration, Set<Integer> considered) {
        boolean hasPermission;
        if (registration == null) {
            hasPermission = false;
        } else if (registration.equals(this) || registration.isLIMSAdmin()) {
            hasPermission = true;
        } else {
            hasPermission = false;
        }
        if (!hasPermission) {
            logger.info("Registration does not give permission");
            throw new SecurityException("User " + registration.getEmailAddress() + " not permitted to modify the account of "
                    + this.getEmailAddress());
        }
        return hasPermission;

    }

    /**
     * <p>
     * isPayee.
     * </p>
     * 
     * @return a boolean.
     */
    public boolean isPayee() {
        return payee;
    }

    /**
     * <p>
     * Setter for the field <code>payee</code>.
     * </p>
     * 
     * @param payee
     *            a boolean.
     */
    public void setPayee(boolean payee) {
        this.payee = payee;
    }

}
