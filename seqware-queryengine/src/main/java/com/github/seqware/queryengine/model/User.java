package com.github.seqware.queryengine.model;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.model.impl.MoleculeImpl;
import com.github.seqware.queryengine.model.interfaces.BaseBuilder;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

/**
 * A user of the Generic Feature Store.
 *
 * TODO: If Users (or Groups) are versioned (as implied by the RESTful API), we
 * may need a "persistent" ID that does not change between versions
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class User extends MoleculeImpl<User> {

    /** Constant <code>prefix="User"</code> */
    public final static String prefix = "User";
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String password;
    private List<Group> groups;

    /**
     * Create a new user
     */
    private User() {
        super();
        groups = new ArrayList();
    }

    /**
     * Get email address
     *
     * @return email address as a String
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Check a user's password
     *
     * @param password password login
     * @return true iff password is correct
     */
    public boolean checkPassword(String password) {
        return this.password.equals(hashedPassword(password));
    }

    /**
     * Get first name
     *
     * @return first name as a String
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Get last name
     *
     * @return last name as a String
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Get list of groups that this user is a part of
     *
     * @return list of groups
     */
    public List<Group> getGroups() {
        return Collections.unmodifiableList(groups);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        User rhs = (User) obj;
        return new EqualsBuilder()
                // Group Equality does not need SGID equality, we do not want different Users with different times to be treated differently
                //                .appendSuper(super.equals(obj))
                .append(super.getSGID().getRowKey(), rhs.getSGID().getRowKey())
                .append(firstName, rhs.getFirstName())
                .append(lastName, rhs.getLastName())
                .append(emailAddress, rhs.getEmailAddress())
                .isEquals();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        // you pick a hard-coded, randomly chosen, non-zero, odd number
        // ideally different for each class
        return new HashCodeBuilder(17, 37).
                append(super.getSGID().getRowKey()).
                append(firstName).
                append(lastName).
                append(emailAddress).
                toHashCode();
    }

    /**
     * Hash a password from md5 (copied from {@link http://jdwyah.blogspot.ca/2006/08/java-md5-password-hash.html
     * }
     *
     * @param password
     * @return
     */
    private static String hashedPassword(String password) {
//        return password;
        String hashword;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            Charset charset = Charset.forName("UTF-8");
            md5.update(password.getBytes(charset));
            BigInteger hash = new BigInteger(1, md5.digest());
            hashword = hash.toString(16);
        } catch (NoSuchAlgorithmException nsae) {
            hashword = "testing";
            Logger.getLogger(User.class.getClass()).info("algorithm error with password encryption");
        }
        return pad(hashword, 32, '0');
    }

    private static String pad(String s, int length, char pad) {
        StringBuilder buffer = new StringBuilder(s);
        while (buffer.length() < length) {
            buffer.insert(0, pad);
        }
        return buffer.toString();
    }

    /**
     * Create a new ACL builder
     *
     * @return a {@link com.github.seqware.queryengine.model.User.Builder} object.
     */
    public static User.Builder newBuilder() {
        return new User.Builder();
    }

    /**
     * A hashed password for the user
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPassword() {
        return password;
    }

    /**
     * {@inheritDoc}
     *
     * Create an User builder started with a copy of this
     */
    @Override
    public User.Builder toBuilder() {
        User.Builder b = new User.Builder();
        b.user = this.copy(true);
        return b;
    }

    /** {@inheritDoc} */
    @Override
    public Class getHBaseClass() {
        return User.class;
    }

    /** {@inheritDoc} */
    @Override
    public String getHBasePrefix() {
        return User.prefix;
    }

    public static class Builder extends BaseBuilder {

        private User user = new User();

        public User.Builder setFirstName(String firstName) {
            user.firstName = firstName;
            return this;
        }

        public User.Builder setLastName(String lastName) {
            user.lastName = lastName;
            return this;
        }

        public User.Builder setPasswordWithoutHash(String password) {
            user.password = password;
            return this;
        }

        public User.Builder setPassword(String password) {
            user.password = hashedPassword(password);
            return this;
        }

        public User.Builder setEmailAddress(String emailAddress) {
            user.emailAddress = emailAddress;
            return this;
        }

        @Override
        public User build() {
            if (user.getManager() != null) {
                user.getManager().objectCreated(user);
            }
            return user;
        }

        @Override
        public Builder setManager(CreateUpdateManager aThis) {
            user.setManager(aThis);
            return this;
        }

        @Override
        public Builder setFriendlyRowKey(String rowKey) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
