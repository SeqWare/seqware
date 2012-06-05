package com.github.seqware.model.impl.inMemory;

import com.github.seqware.factory.ModelManager;
import com.github.seqware.model.Reference;
import com.github.seqware.model.ReferenceSet;
import com.github.seqware.util.InMemoryIterator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * An in-memory representation of a reference set.
 *
 * @author jbaran
 */
public class InMemoryReferenceSet extends ReferenceSet {

    /**
     * The set of references this instance represents when an in-memory storage model is used.
     */
    private Set<Reference> references = new HashSet<Reference>();

    /**
     * Creates an in-memory feature set.
     */
    protected InMemoryReferenceSet() {
        super();
    }

    @Override
    public void add(Reference reference) {
        references.add(reference);
        this.getManager().particleStateChange(this, ModelManager.State.NEW_VERSION);  
    }

    @Override
    public void add(Set<Reference> references) {
        references.addAll(references);
        this.getManager().particleStateChange(this, ModelManager.State.NEW_VERSION);  
    }

    @Override
    public Iterator<Reference> getReferences() {
        return new InMemoryIterator<Reference>(references.iterator());
    }

    @Override
    public Iterator<Reference> iterator() {
        return this.getReferences();
    }

    @Override
    public long getCount() {
        return references.size();
    }
    
    /**
     * Create a new AnalysisSet builder
     *
     * @return
     */
    public static ReferenceSet.Builder newBuilder() {
        return new InMemoryReferenceSet.Builder();
    }

    @Override
    public ReferenceSet.Builder toBuilder() {
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
            if (aSet.getName() == null || aSet.getManager() == null) {
                throw new RuntimeException("Invalid build of ReferenceSet");
            }
            aSet.getManager().objectCreated(aSet);
            return aSet;
        }
    }
}
