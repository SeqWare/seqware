package com.github.seqware.queryengine.model;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.interfaces.BaseBuilder;
import com.github.seqware.queryengine.model.interfaces.MolSetInterface;

/**
 * A Group of users that may share ACL permissions
 *
 * @author dyuen
 * @version $Id: $Id
 */
public interface Group extends MolSetInterface<Group, User> {
    /** Constant <code>prefix="Group"</code> */
    public final static String prefix = "Group";

    /**
     * Get the name of the group
     *
     * @return the name of the group
     */
    public String getName();
    
    /**
     * Get the description associated with this group
     *
     * @return the description associated with this group
     */
    public String getDescription();

    /**
     * {@inheritDoc}
     *
     * Create a Group builder started with a copy of this
     */
    @Override
    public abstract Group.Builder toBuilder();

    public abstract static class Builder extends BaseBuilder {

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
        
        @Override
        public Group.Builder setFriendlyRowKey(String rowKey) {
            super.checkFriendlyRowKey(rowKey);
            aSet.getSGID().setFriendlyRowKey(rowKey);
            return this;
        }
    }


}
