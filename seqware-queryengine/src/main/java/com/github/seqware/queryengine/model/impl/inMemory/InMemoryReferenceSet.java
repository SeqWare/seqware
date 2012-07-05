package com.github.seqware.queryengine.model.impl.inMemory;

import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.model.ReferenceSet;
import com.github.seqware.queryengine.model.impl.AtomImpl;

/**
 * An in-memory representation of a reference set.
 *
 * @author jbaran
 * @author dyuen
 */
public class InMemoryReferenceSet extends AbstractInMemorySet<ReferenceSet, Reference> implements ReferenceSet{
    
    private String name = null;
    private String organism = null;
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override 
    public String getOrganism(){
        return organism;
    }

    
    public static ReferenceSet.Builder newBuilder() {
        return new InMemoryReferenceSet.Builder();
    }

    @Override
    public InMemoryReferenceSet.Builder toBuilder() {
        InMemoryReferenceSet.Builder b = new InMemoryReferenceSet.Builder();
        b.aSet = (InMemoryReferenceSet) this.copy(true);
        return b;
    }

    @Override
    public Class getHBaseClass() {
        return ReferenceSet.class;
    }

    @Override
    public String getHBasePrefix() {
        return ReferenceSet.prefix;
    }

    public static class Builder extends ReferenceSet.Builder {
        
        public Builder(){
            aSet = new InMemoryReferenceSet();
        }

        @Override
        public ReferenceSet build(boolean newObject) {
            if (((AtomImpl)aSet).getManager() != null){
                ((AtomImpl)aSet).getManager().objectCreated((Atom)aSet);
            }
            return aSet;
        }

        @Override
        public InMemoryReferenceSet.Builder setName(String name) {
            ((InMemoryReferenceSet)aSet).name = name;
            return this;
        }
        
        @Override
        public InMemoryReferenceSet.Builder setOrganism(String organism) {
            ((InMemoryReferenceSet)aSet).organism = organism;
            return this;
        }
    }

}
