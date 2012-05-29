package com.github.seqware.model.impl.inMemory;

import com.github.seqware.model.FeatureSet;
import com.github.seqware.model.Reference;
import java.util.Iterator;

/**
 * An in-memory representation of a reference.
 *
 * @author jbaran
 */
public class InMemoryReference extends Reference {

    @Override
    public Iterator<FeatureSet> featureSets() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
