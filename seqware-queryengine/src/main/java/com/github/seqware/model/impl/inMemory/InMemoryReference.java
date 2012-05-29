package com.github.seqware.model.impl.inMemory;

import com.github.seqware.model.FeatureSet;
import com.github.seqware.model.Reference;
import java.util.Iterator;

/**
 * An in-memory representation of a reference.
 *
 * @author dyuen
 */
public class InMemoryReference extends Reference {
    
    /**
     * Construct reference with a name
     * @param name reference with a name
     */
    public InMemoryReference(String name){
        super(name);
    }

    @Override
    public Iterator<FeatureSet> featureSets() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
