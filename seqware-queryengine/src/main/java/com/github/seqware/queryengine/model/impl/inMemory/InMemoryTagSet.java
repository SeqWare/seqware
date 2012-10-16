package com.github.seqware.queryengine.model.impl.inMemory;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.model.TagSet;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import java.util.*;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.log4j.Logger;


/**
 * An in-memory representation of a TagSet.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class InMemoryTagSet extends AbstractInMemorySet<TagSet, Tag> implements TagSet {

    private String name = null;
    private Map<String, Tag> map = new HashMap<String, Tag>();

    /** {@inheritDoc} */
    @Override
    public InMemoryTagSet add(Tag element) {
        if (map.containsKey(element.getKey())){ 
            Logger.getLogger(InMemoryTagSet.class.getName()).warn("Adding duplicated key in TagSet ignored with key " + element.getKey());
            element.getManager().atomStateChange(element, CreateUpdateManager.State.UNMANAGED);
            element.setManager(null);
            return this;
        }
        super.add(element);
        map.put(element.getKey(), element);
        assert (this.getSGID().getBackendTimestamp() != null);
        element.setTagSet(this);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public InMemoryTagSet add(Collection<Tag> elements) {
        Collection<Tag> newCol = new ArrayList<Tag>();
        for(Tag element : elements){
            if (!map.containsKey(element.getKey())){
                newCol.add(element);
                map.put(element.getKey(), element);
            }
        }
        
        super.add(newCol);
        assert (this.getSGID().getBackendTimestamp() != null);
        for (Tag e : newCol) {
            e.setTagSet(this);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public InMemoryTagSet add(Tag... elements) {
        return this.add(Arrays.asList(elements));
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }

    /**
     * <p>newBuilder.</p>
     *
     * @return a {@link com.github.seqware.queryengine.model.TagSet.Builder} object.
     */
    public static TagSet.Builder newBuilder() {
        return new InMemoryTagSet.Builder();
    }

    /** {@inheritDoc} */
    @Override
    public InMemoryTagSet.Builder toBuilder() {
        InMemoryTagSet.Builder b = new InMemoryTagSet.Builder();
        b.aSet = (InMemoryTagSet) this.copy(true);
        return b;
    }

    /** {@inheritDoc} */
    @Override
    public Class getHBaseClass() {
        return TagSet.class;
    }

    /** {@inheritDoc} */
    @Override
    public String getHBasePrefix() {
        return TagSet.prefix;
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsKey(String tagKey) {
        return this.map.containsKey(tagKey);
    }

    /** {@inheritDoc} */
    @Override
    public Tag get(String tagKey) {
        return this.map.get(tagKey);
    }
    
    /** {@inheritDoc} */
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
        TagSet rhs = (TagSet) obj;
        return new EqualsBuilder()
                // Group Equality does not need SGID equality, we do not want different Users with different times to be treated differently
                //                .appendSuper(super.equals(obj))
                .append(super.getSGID().getRowKey(), rhs.getSGID().getRowKey())
                .append(name, rhs.getName())
                .isEquals();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    public static class Builder extends TagSet.Builder {

        public Builder() {
            aSet = new InMemoryTagSet();
        }

        @Override
        public TagSet build(boolean newObject) {
            if (((AtomImpl) aSet).getManager() != null) {
                ((AtomImpl) aSet).getManager().objectCreated((Atom) aSet);
            }
            return aSet;
        }

        @Override
        public Builder setName(String name) {
            ((InMemoryTagSet) aSet).name = name;
            return this;
        }
    }
}
