package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import net.sourceforge.seqware.common.factory.DBAccess;
import net.sourceforge.seqware.common.security.PermissionsAware;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

/**
 * Registration
 */
public class Registration implements Serializable, PermissionsAware, Comparable<Registration> {

    public String getInstitution() {
        return institution;
    }

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

    public boolean isJoinDevelopersMailingList() {
        return joinDevelopersMailingList;
    }

    public boolean getJoinDevelopersMailingList() {
        return joinDevelopersMailingList;
    }

    public void setJoinDevelopersMailingList(boolean b) {
        this.joinDevelopersMailingList = b;
    }

    public boolean isJoinUsersMailingList() {
        return joinUsersMailingList;
    }

    public boolean getJoinUsersMailingList() {
        return joinUsersMailingList;
    }

    public void setJoinUsersMailingList(boolean b) {
        this.joinUsersMailingList = b;
    }

    public Registration() {
        super();
    }

//    public boolean equals(Object other) {
//        if ((this == other)) {
//            return true;
//        }
//        if (!(other instanceof Registration)) {
//            return false;
//        }
//        boolean result = false;
//        Registration castOther = (Registration) other;
//        if (this.getRegistrationId().equals(castOther.getRegistrationId())) {
//            result = true;
//        } else if (new EqualsBuilder().append(this.getEmailAddress(), castOther.getEmailAddress()).append(this.getPassword(), castOther.getPassword()).isEquals()) {
//            result = true;
//        }
//        return (result);
//    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            Logger.getLogger(Registration.class).warn("Compared-to object is null");
            return false;
        }
        if  (!(obj instanceof Registration)) {
            Logger.getLogger(Registration.class).warn("Compared-to object is different class");
            return false;
        }
        final Registration other = (Registration) obj;
        if (this.compareTo((Registration) obj) != 0) {
            Logger.getLogger(Registration.class).warn("Compared-to object has different email");
            return false;
        }
        Logger.getLogger(Registration.class).warn("Compared-to object is equivalent");
        return true;
    }

    @Override
    public int compareTo(Registration t) {
        return emailAddress.compareTo(t.getEmailAddress());
    }

    public int hashCode() {
        return new HashCodeBuilder().append(getEmailAddress()).append(getPassword()).toHashCode();
    }

    /*
     * public boolean equals(Object other) { if (this == other)	return true; if
     * (!(other instanceof Registration))	return false; Registration castOther =
     * (Registration) other; return
     * this.getEmailAddress().equals(castOther.getEmailAddress()) &&
     * this.getPassword().equals(castOther.getPassword()); }
     *
     * public int hashCode() { return	getEmailAddress().hashCode() ^
     * getPassword().hashCode(); }
     */
    @Override
    public String toString() {
        return "Registration{" + "registrationId=" + registrationId + ", emailAddress=" + emailAddress + ", passwordHint=" + passwordHint + ", firstName=" + firstName + ", lastName=" + lastName + ", institution=" + institution + ", limsAdmin=" + limsAdmin + ", createTimestamp=" + createTimestamp + ", updateTimestamp=" + updateTimestamp + ", invitationCode=" + invitationCode + ", joinUsersMailingList=" + joinUsersMailingList + ", joinDevelopersMailingList=" + joinDevelopersMailingList + '}';
    }

    public Date getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Date createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordHint() {
        return passwordHint;
    }

    public void setPasswordHint(String passwordHint) {
        this.passwordHint = passwordHint;
    }

    public Integer getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Integer registrationId) {
        this.registrationId = registrationId;
    }

    public Date getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Date updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public boolean isLIMSAdmin() {
        return limsAdmin;
    }

    public void setLIMSAdmin(boolean b) {
        this.limsAdmin = b;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public boolean isTechnician() {
        return false;
    }

    public static Registration cloneFromDB(int ownerId) throws SQLException {
        Registration registration = null;
        try {
            ResultSet rs = DBAccess.get().executeQuery("SELECT * FROM registration WHERE registration_id=" + ownerId);

            if (rs.next()) {
                registration = new Registration();
                registration.setRegistrationId(rs.getInt("registration_id"));
                registration.setEmailAddress(rs.getString("email"));
                registration.setPasswordHint(rs.getString("password_hint"));
                registration.setFirstName(rs.getString("first_name"));
                registration.setLastName(rs.getString("last_name"));
                registration.setInstitution(rs.getString("institution"));
                registration.setLIMSAdmin(rs.getBoolean("lims_admin"));
                registration.setCreateTimestamp(rs.getTimestamp("create_tstmp"));
                registration.setUpdateTimestamp(rs.getTimestamp("last_update_tstmp"));
                registration.setJoinDevelopersMailingList(rs.getBoolean("developer_ml"));
                registration.setJoinUsersMailingList(rs.getBoolean("user_ml"));

            }
        } finally {
            DBAccess.close();
        }
        return registration;
    }

    @Override
    public boolean givesPermission(Registration registration) {
        boolean hasPermission = false;
        if (registration == null) {
            hasPermission = false;
        } else if (registration.equals(this) || registration.isLIMSAdmin()) {
            hasPermission = true;
        } else {
            hasPermission = false;
        }
        if (!hasPermission) {
            Logger.getLogger(Registration.class).info("Registration does not give permission");
            throw new SecurityException("User " + registration.getEmailAddress() + " not permitted to modify the account of " + this.getEmailAddress());
        }
        return hasPermission;

    }

    public boolean isPayee() {
        return payee;
    }

    public void setPayee(boolean payee) {
        this.payee = payee;
    }
    
    
}
