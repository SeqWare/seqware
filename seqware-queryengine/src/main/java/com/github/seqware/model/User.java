package com.github.seqware.model;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A user of the Generic Feature Store.
 *
 * TODO: If Users (or Groups) are versioned (as implied by the RESTful API), we
 * may need a "persistent" ID that does not change between versions
 *
 * @author dyuen
 */
public class User extends Molecule<User> {

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
     * Create a user (input will probably need to be cleaned and checked for
     * hazards at some point)
     *
     * @param firstName
     * @param lastName
     * @param emailAddress
     */
    public User(Group group, String firstName, String lastName, String emailAddress, String password) {
        this();
        this.groups.add(group);
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.password = hashedPassword(password);
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
     * Change a User's password
     * @param password new password
     */
    public void setPassword(String password) {
        this.password = hashedPassword(password);
    }
    
    /**
     * Check a user's password
     * @param password password login
     * @return true iff password is correct
     */
    public boolean checkPassword(String password){
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
        return groups;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
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
    private String hashedPassword(String password) {
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

    private String pad(String s, int length, char pad) {
        StringBuilder buffer = new StringBuilder(s);
        while (buffer.length() < length) {
            buffer.insert(0, pad);
        }
        return buffer.toString();
    }
}
