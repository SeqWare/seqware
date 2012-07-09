package com.github.seqware.queryengine.model.impl.inMemory;

import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.TagSpec;
import com.github.seqware.queryengine.model.TagSpecSet;
import com.github.seqware.queryengine.model.impl.AtomImpl;

/**
 * An in-memory representation of a TagSpecSet.
 *
 * @author dyuen
 */
public class InMemoryTagSpecSet extends AbstractInMemorySet<TagSpecSet, TagSpec> implements TagSpecSet{
    
    private String name = null;
    
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

    public static class Builder extends TagSpecSet.Builder {
        
        public Builder(){
            aSet = new InMemoryTagSpecSet();
        }

        @Override
        public TagSpecSet build(boolean newObject) {
            if (((AtomImpl)aSet).getManager() != null){
                ((AtomImpl)aSet).getManager().objectCreated((Atom)aSet);
            }
            return aSet;
        }

        @Override
        public Builder setName(String name) {
            ((InMemoryTagSpecSet)aSet).name = name;
            return this;
        }
    }

}
