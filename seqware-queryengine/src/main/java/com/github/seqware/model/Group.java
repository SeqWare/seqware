package com.github.seqware.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A Group of users that may share ACL permissions
 * @author dyuen
 */
public abstract class Group extends Molecule{
    
    private String name;
    private String description;
    private List<User> users;
    
    /**
     * Create a new user group
     */
    private Group() {
        super();
        users = new ArrayList();
    }
    
    public Group(String name, String description){
        this();
        this.name = name;
        this.description = description;
    }

    /**
     * Get a description of the organization
     * @return String description of the organization
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get name of the organization
     * @return String of the organization's name 
     */
    public String getName() {
        return name;
    }

    
    
}
