package com.github.seqware.model;

import com.github.seqware.factory.ModelManager;
import com.github.seqware.impl.SimpleModelManager;
import com.github.seqware.util.SeqWareIterable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A Group of users that may share ACL permissions
 * @author dyuen
 */
public class Group extends Molecule<Group> implements SeqWareIterable<User>{
    
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
        this.getManager().particleStateChange(this, ModelManager.State.NEW_VERSION);  
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
         if (obj instanceof Group) {
            Group other = (Group) obj;
            return this.name.equals(other.name) && this.description.equals(other.description) && this.users.equals(other.users);
        }
        return false;
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
    
     /**
     * Create a new ACL builder
     * @return 
     */
    public static Group.Builder newBuilder() {
        return new Group.Builder();
    }
    
    /**
     * Create an ACL builder started with a copy of this
     * @return 
     */
    @Override
    public Group.Builder toBuilder(){
        Group.Builder b = new Group.Builder();
        b.group = this.copy(false);
        return b;
    }

    public static class Builder implements BaseBuilder {

        private Group group = new Group();

        public Group.Builder setName(String name) {
            group.name = name;
            return this;
        }

        public Group.Builder setDescription(String description) {
            group.description = description;
            return this;
        }

        @Override
        public Group build() {
            group.getManager().objectCreated(group);
            return group;
        }

        @Override
        public Builder setManager(ModelManager aThis) {
            group.setManager(aThis);
            return this;
        }
    }
}
