package com.github.seqware.model;

import com.github.seqware.impl.SimpleModelManager;
import com.github.seqware.util.SeqWareIterable;
import java.util.Iterator;
import java.util.Set;

/**
 * A feature set is a set (ex: collection, bag) of features. A feature set
 * cannot exist without a reference even if the reference is ad hoc and/or
 * user-created.
 *
 * Immutable (but tags are not). TODO: Should we consider deleting Features from
 * FeatureSets as well as adding them or can that be implicit when a Feature
 * itself is deleted?
 *
 * @author jbaran
 */
public abstract class FeatureSet extends Molecule<FeatureSet> implements SeqWareIterable<Feature> {

    /**
     * Associated reference.
     */
    private Reference reference;

    /**
     * Creates an instance of an anonymous feature set.
     */
    protected FeatureSet() {
        super();
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

    /**
     * Get the reference for this featureSet
     *
     * @return reference for the feature set
     */
    public Reference getReference() {
        return reference;
    }
    
     /**
     * Create an FeatureSet builder started with a copy of this
     * @return 
     */
    public abstract FeatureSet.Builder toBuilder();

    public abstract static class Builder {

        public FeatureSet aSet;

        public Builder setReference(Reference reference) {
            aSet.reference = reference;
            return this;
        }
        
        public FeatureSet build() {
           return build(true);
        }

        public abstract FeatureSet build(boolean newObject);

        public Builder setManager(SimpleModelManager aThis) {
            aSet.setManager(aThis);
            return this;
        }
    }
}
