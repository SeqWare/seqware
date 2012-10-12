package com.github.seqware.queryengine.model.impl.inMemory;

import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.Group;
import com.github.seqware.queryengine.model.User;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * An in-memory representation of a group.
 *
 * @author jbaran
 * @author dyuen
 * @version $Id: $Id
 */
public class InMemoryGroup extends AbstractInMemorySet<Group, User> implements Group {

    private String name = null;
    private String description = null;

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     *
     * Override the equals method for Group, kind of a cheat With other objects
     * we take into account things like version or SGID However, with Group, the
     * object is embedded inside the ACL for other objects explicitly, so the
     * version changes too often for easy API use
     */
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
        Group rhs = (Group) obj;
        return new EqualsBuilder()
                // Group Equality does not need SGID equality, we do not want different Groups with different times to be treated differently
                //                .appendSuper(super.equals(obj))
                .append(super.getSGID().getRowKey(), rhs.getSGID().getRowKey())
                .append(name, rhs.getName())
                .append(description, rhs.getDescription())
                .isEquals();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 71 * hash + (this.description != null ? this.description.hashCode() : 0);
        return hash;
    }

    /**
     * <p>newBuilder.</p>
     *
     * @return a {@link com.github.seqware.queryengine.model.Group.Builder} object.
     */
    public static Group.Builder newBuilder() {
        return new InMemoryGroup.Builder();
    }

    /** {@inheritDoc} */
    @Override
    public InMemoryGroup.Builder toBuilder() {
        InMemoryGroup.Builder b = new InMemoryGroup.Builder();
        b.aSet = (InMemoryGroup) this.copy(true);
        return b;
    }

    /** {@inheritDoc} */
    @Override
    public Class getHBaseClass() {
        return Group.class;
    }

    /** {@inheritDoc} */
    @Override
    public String getHBasePrefix() {
        return Group.prefix;
    }

    public static class Builder extends Group.Builder {

        public Builder() {
            aSet = new InMemoryGroup();
        }

        @Override
        public Group build(boolean newObject) {
            if (((AtomImpl) aSet).getManager() != null) {
                ((AtomImpl) aSet).getManager().objectCreated((Atom) aSet);
            }
            return aSet;
        }

        @Override
        public InMemoryGroup.Builder setName(String name) {
            ((InMemoryGroup) aSet).name = name;
            return this;
        }

        @Override
        public InMemoryGroup.Builder setDescription(String description) {
            ((InMemoryGroup) aSet).description = description;
            return this;
        }
    }
}
