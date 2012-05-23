package com.github.seqware.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A user of the Generic Feature Store.
 * 
 * TODO: If Users (or Groups) are versioned (as implied by the RESTful API), we may need a 
 * "persistent" ID that does not change between versions
 * 
 * @author dyuen
 */
public abstract class User extends Molecule {

    private String firstName;
    private String lastName;
    private String emailAddress;
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
    public User(Group group, String firstName, String lastName, String emailAddress) {
        this();
        this.groups.add(group);
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
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
     * Get first name
     *
     * @return first name as a String
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Get last name
     * @return last name as a String 
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Get list of groups that this user is a part of
     * @return list of groups
     */
    public List<Group> getGroups() {
        return groups;
    }
    
    
}
