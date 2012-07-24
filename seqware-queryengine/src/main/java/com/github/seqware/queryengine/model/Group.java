package com.github.seqware.queryengine.model;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.interfaces.MolSetInterface;
import com.github.seqware.queryengine.model.interfaces.BaseBuilder;

/**
 * A Group of users that may share ACL permissions
 * @author dyuen
 */
public interface Group extends MolSetInterface<Group, User> {
    public final static String prefix = "Group";

    /**
     * Get the name of the group
     *
     * @return the name of the group
     */
    public String getName();
    
    /**
     * Get the description associated with this group
     * @return the description associated with this group
     */
    public String getDescription();

    /**
     * Create a Group builder started with a copy of this
     * @return 
     */
    @Override
    public abstract Group.Builder toBuilder();

    public abstract static class Builder implements BaseBuilder {

        public Group aSet;
        
        @Override
        public Group build() {
           return build(true);
        }

        public abstract Group build(boolean newObject);

        @Override
        public Group.Builder setManager(CreateUpdateManager aThis) {
            ((AtomImpl)aSet).setManager(aThis);
            return this;
        }

        public abstract Group.Builder setName(String name);
        
        public abstract Group.Builder setDescription(String description);
    }


}
