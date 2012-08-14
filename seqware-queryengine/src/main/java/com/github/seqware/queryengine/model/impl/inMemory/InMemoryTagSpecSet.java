package com.github.seqware.queryengine.model.impl.inMemory;

import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.model.TagSpecSet;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import java.util.*;
import org.apache.log4j.Logger;


/**
 * An in-memory representation of a TagSpecSet.
 *
 * @author dyuen
 */
public class InMemoryTagSpecSet extends AbstractInMemorySet<TagSpecSet, Tag> implements TagSpecSet {

    private String name = null;
    private Map<String, Tag> map = new HashMap<String, Tag>();

    @Override
    public InMemoryTagSpecSet add(Tag element) {
        if (map.containsKey(element.getKey())){ 
            Logger.getLogger(InMemoryTagSpecSet.class.getName()).warn("Adding duplicated key in TagSet ignored with key " + element.getKey());
            return this;
        }
        super.add(element);
        map.put(element.getKey(), element);
        assert (this.getSGID().getBackendTimestamp() != null);
        element.setTagSpecSet(this);
        return this;
    }

    @Override
    public InMemoryTagSpecSet add(Collection<Tag> elements) {
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
            e.setTagSpecSet(this);
        }
        return this;
    }

    @Override
    public InMemoryTagSpecSet add(Tag... elements) {
        return this.add(Arrays.asList(elements));
    }

    @Override
    public String getName() {
        return name;
    }

    public static TagSpecSet.Builder newBuilder() {
        return new InMemoryTagSpecSet.Builder();
    }

    @Override
    public InMemoryTagSpecSet.Builder toBuilder() {
        InMemoryTagSpecSet.Builder b = new InMemoryTagSpecSet.Builder();
        b.aSet = (InMemoryTagSpecSet) this.copy(true);
        return b;
    }

    @Override
    public Class getHBaseClass() {
        return TagSpecSet.class;
    }

    @Override
    public String getHBasePrefix() {
        return TagSpecSet.prefix;
    }

    @Override
    public boolean containsKey(String tagKey) {
        return this.map.containsKey(tagKey);
    }

    @Override
    public Tag get(String tagKey) {
        return this.map.get(tagKey);
    }

    public static class Builder extends TagSpecSet.Builder {

        public Builder() {
            aSet = new InMemoryTagSpecSet();
        }

        @Override
        public TagSpecSet build(boolean newObject) {
            if (((AtomImpl) aSet).getManager() != null) {
                ((AtomImpl) aSet).getManager().objectCreated((Atom) aSet);
            }
            return aSet;
        }

        @Override
        public Builder setName(String name) {
            ((InMemoryTagSpecSet) aSet).name = name;
            return this;
        }
    }
}
