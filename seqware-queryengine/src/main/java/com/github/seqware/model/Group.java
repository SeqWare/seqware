package com.github.seqware.model;

import com.github.seqware.factory.ModelManager;
import com.github.seqware.model.impl.AtomImpl;
import com.github.seqware.model.interfaces.AbstractMolSet;
import com.github.seqware.model.interfaces.BaseBuilder;

/**
 * A Group of users that may share ACL permissions
 * @author dyuen
 */
public interface Group extends AbstractMolSet<Group, User> {
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
        public Group.Builder setManager(ModelManager aThis) {
            ((AtomImpl)aSet).setManager(aThis);
            return this;
        }

        public abstract Group.Builder setName(String name);
        
        public abstract Group.Builder setDescription(String description);
    }


}
