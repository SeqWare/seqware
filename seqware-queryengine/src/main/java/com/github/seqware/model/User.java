package com.github.seqware.model;

import com.github.seqware.factory.ModelManager;
import com.github.seqware.model.impl.MoleculeImpl;
import com.github.seqware.model.interfaces.BaseBuilder;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A user of the Generic Feature Store.
 *
 * TODO: If Users (or Groups) are versioned (as implied by the RESTful API), we
 * may need a "persistent" ID that does not change between versions
 *
 * @author dyuen
 */
public class User extends MoleculeImpl<User> {
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

    @Override
    public boolean equals(Object obj) {
        // will cause recursion
//        return EqualsBuilder.reflectionEquals(this, obj);
        if (obj instanceof User) {
            User other = (User) obj;
            return this.firstName.equals(other.firstName) && this.lastName.equals(other.lastName)
                    && this.emailAddress.equals(other.emailAddress);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
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
        String hashword = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(password.getBytes());
            BigInteger hash = new BigInteger(1, md5.digest());
            hashword = hash.toString(16);

        } catch (NoSuchAlgorithmException nsae) {
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
     * @return 
     */
    public static User.Builder newBuilder() {
        return new User.Builder();
    }

    /**
     * A hashed password for the user
     * @return 
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Create an User builder started with a copy of this
     * @return 
     */
    @Override
    public User.Builder toBuilder(){
        User.Builder b = new User.Builder();
        b.user = this.copy(true);
        return b;
    }

    @Override
    public Class getHBaseClass() {
        return User.class;
    }

    @Override
    public String getHBasePrefix() {
        return User.prefix;
    }

    public static class Builder implements BaseBuilder {

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
            if (user.getManager() != null){
                user.getManager().objectCreated(user);
            }
            return user;
        }

        @Override
        public Builder setManager(ModelManager aThis) {
            user.setManager(aThis);
            return this;
        }
    }
}
