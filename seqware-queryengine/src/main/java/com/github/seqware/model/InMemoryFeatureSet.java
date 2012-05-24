package com.github.seqware.model;

import com.github.seqware.util.InMemoryIterator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * An in-memory representation of a feature set.
 *
 * @author jbaran
 */
public class InMemoryFeatureSet extends FeatureSet {

    /**
     * The set of features this instance represents when an in-memory storage model is used.
     */
    private Set<Feature> features = new HashSet<Feature>();

    /**
     * Creates an in-memory feature set.
     */
    public InMemoryFeatureSet(Reference reference) {
        super(reference);
    }

    @Override
    public void add(Feature feature) {
        features.add(feature);
    }

    @Override
    public void add(Set<Feature> features) {
        features.addAll(features);
    }

    @Override
    public Iterator<Feature> getFeatures() {
        return new InMemoryIterator<Feature>(features.iterator());
    }

    public Iterator<Feature> iterator() {
        return getFeatures();
    }


}
