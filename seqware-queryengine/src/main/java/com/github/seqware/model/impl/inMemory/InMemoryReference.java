package com.github.seqware.model.impl.inMemory;

import com.github.seqware.model.AnalysisSet;
import com.github.seqware.model.FeatureSet;
import com.github.seqware.model.Reference;
import java.util.Iterator;

/**
 * An in-memory representation of a reference.
 *
 * @author dyuen
 */
public class InMemoryReference extends Reference {
    
    protected InMemoryReference(){
        super();
    }

    @Override
    public Iterator<FeatureSet> featureSets() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Create a new AnalysisSet builder
     *
     * @return
     */
    public static Reference.Builder newBuilder() {
        return new InMemoryReference.Builder();
    }

    @Override
    public Reference.Builder toBuilder() {
        InMemoryReference.Builder b = new InMemoryReference.Builder();
        b.reference = (Reference) this.copy(true);
        return b;
    }

    public static class Builder extends Reference.Builder {
        
        public Builder(){
            reference = new InMemoryReference();
        }

        @Override
        public Reference build(boolean newObject) {
            reference.getManager().objectCreated(reference, newObject);
            return reference;
        }
    }
}
