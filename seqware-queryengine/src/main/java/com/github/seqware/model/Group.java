package com.github.seqware.model;

import com.github.seqware.util.SeqWareIterable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A Group of users that may share ACL permissions
 * @author dyuen
 */
public class Group extends Molecule implements SeqWareIterable<User>{
    
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

    public void add(User user) {
        users.add(user);
    }

    public void add(Set<User> users) {
        users.addAll(users);
    }
    
    public void add(User ... users) {
        for (User user : users){
            this.add(user);
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public long getCount() {
        return users.size();
    }

    @Override
    public Iterator<User> iterator() {
        return users.iterator();
    }
    
}
