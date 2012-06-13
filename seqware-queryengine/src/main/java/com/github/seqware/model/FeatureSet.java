package com.github.seqware.model;

import com.github.seqware.model.interfaces.BaseBuilder;
import com.github.seqware.model.interfaces.AbstractSet;
import com.github.seqware.factory.ModelManager;
import com.github.seqware.model.impl.MoleculeImpl;
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
public abstract class FeatureSet extends MoleculeImpl<FeatureSet> implements AbstractSet<FeatureSet, Feature> {

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
    @Override
    public abstract FeatureSet add(Feature feature);

    /**
     * Adds a collection of new Feature to the set.
     *
     * @param features The features that are to be added to the feature set.
     */
    @Override
    public abstract FeatureSet add(Set<Feature> features);
    
    public abstract FeatureSet remove(Feature feature);

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
    @Override
    public abstract FeatureSet.Builder toBuilder();

    public abstract static class Builder implements BaseBuilder {

        public FeatureSet aSet;

        public Builder setReference(Reference reference) {
            aSet.reference = reference;
            return this;
        }
        
        @Override
        public FeatureSet build() {
           return build(true);
        }

        public abstract FeatureSet build(boolean newObject);

        @Override
        public Builder setManager(ModelManager aThis) {
            aSet.setManager(aThis);
            return this;
        }
    }
}
