package com.github.seqware.model;

import java.util.Iterator;
import java.util.Set;

/**
 * A feature set is a set (ex: collection, bag) of features. A feature set
 * cannot exist without a reference even if the reference is ad hoc and/or
 * user-created.
 *
 * Immutable (but tags are not).
 *
 * @author jbaran
 */
public abstract class FeatureSet extends Molecule implements Iterable<Feature>{

    /**
     * Associated reference.
     */
    private Reference reference;

    /**
     * Creates an instance of an anonymous feature set.
     */
    private FeatureSet() {
        super();
    }

    /**
     * Creates a FeatureSet with an associated reference.
     *
     * @param reference associated reference
     */
    public FeatureSet(Reference reference) {
        this();
        this.reference = reference;
    }

    /**
     * Adds a single new Feature to the set.
     *
     * @param feature The feature that is to be added to the feature set.
     */
    public abstract void add(Feature feature);

    /**
     * Adds a collection of new Feature to the set.
     *
     * @param features The features that are to be added to the feature set.
     */
    public abstract void add(Set<Feature> features);

    /**
     * Get the list of features associated with this feature set.
     *
     * @return iterator over features.
     */
    public abstract Iterator<Feature> getFeatures();
}
