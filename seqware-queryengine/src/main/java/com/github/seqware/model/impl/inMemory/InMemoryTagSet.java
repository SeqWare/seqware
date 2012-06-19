package com.github.seqware.model.impl.inMemory;

import com.github.seqware.model.Atom;
import com.github.seqware.model.Tag;
import com.github.seqware.model.TagSet;
import com.github.seqware.model.impl.AtomImpl;

/**
 * An in-memory representation of a TagSet.
 *
 * @author dyuen
 */
public class InMemoryTagSet extends AbstractInMemorySet<TagSet, Tag> implements TagSet{
    
    private String name = null;
    
    @Override
    public String getName() {
        return name;
    }

    
    public static TagSet.Builder newBuilder() {
        return new InMemoryTagSet.Builder();
    }

    @Override
    public InMemoryTagSet.Builder toBuilder() {
        InMemoryTagSet.Builder b = new InMemoryTagSet.Builder();
        b.aSet = (InMemoryTagSet) this.copy(false);
        return b;
    }

    @Override
    public Class getHBaseClass() {
        return TagSet.class;
    }

    @Override
    public String getHBasePrefix() {
        return TagSet.prefix;
    }

    public static class Builder extends TagSet.Builder {
        
        public Builder(){
            aSet = new InMemoryTagSet();
        }

        @Override
        public TagSet build(boolean newObject) {
            ((AtomImpl)aSet).getManager().objectCreated((Atom)aSet);
            return aSet;
        }

        @Override
        public Builder setName(String name) {
            ((InMemoryTagSet)aSet).name = name;
            return this;
        }
    }

}
