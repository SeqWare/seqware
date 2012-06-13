package com.github.seqware.model.impl.inMemory;

import com.github.seqware.model.Atom;
import com.github.seqware.model.Reference;
import com.github.seqware.model.ReferenceSet;
import com.github.seqware.model.impl.AtomImpl;

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
        b.aSet = (InMemoryReferenceSet) this.copy(false);
        return b;
    }

    public static class Builder extends ReferenceSet.Builder {
        
        public Builder(){
            aSet = new InMemoryReferenceSet();
        }

        @Override
        public ReferenceSet build(boolean newObject) {
            ((AtomImpl)aSet).getManager().objectCreated((Atom)aSet);
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
