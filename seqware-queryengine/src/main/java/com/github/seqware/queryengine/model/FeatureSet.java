package com.github.seqware.queryengine.model;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.model.impl.AbstractMolSet;
import com.github.seqware.queryengine.model.interfaces.BaseBuilder;
import com.github.seqware.queryengine.model.interfaces.MolSetInterface;
import com.github.seqware.queryengine.util.SGID;
import java.util.Collection;
import java.util.Iterator;

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
 * @version $Id: $Id
 */
public abstract class FeatureSet extends AbstractMolSet<FeatureSet> implements MolSetInterface<FeatureSet, Feature> {
    /** Constant <code>prefix="FeatureSet"</code> */
    public final static String prefix = "FeatureSet";

    /**
     * Creates an instance of an anonymous feature set.
     */
    protected FeatureSet() {
        super();
    }
        
    /**
     * Get the description associated with this FeatureSet
     *
     * @return the description associated with this FeatureSet
     */
    public abstract String getDescription();

    /**
     * {@inheritDoc}
     *
     * Adds a single new Feature to the set.
     */
    @Override
    public abstract FeatureSet add(Feature feature);

    /**
     * {@inheritDoc}
     *
     * Adds a collection of new Feature to the set.
     */
    @Override
    public abstract FeatureSet add(Collection<Feature> features);
    
    /** {@inheritDoc} */
    @Override
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
    public abstract Reference getReference();
    
    /**
     * Get the referenceID for the associated reference
     *
     * @return a {@link com.github.seqware.queryengine.util.SGID} object.
     */
    public abstract SGID getReferenceID();
    
    /**
     * {@inheritDoc}
     *
     * Create an FeatureSet builder started with a copy of this
     */
    @Override
    public abstract FeatureSet.Builder toBuilder();

    public abstract static class Builder extends BaseBuilder {

        public FeatureSet aSet;

        public abstract Builder setReference(Reference reference);
        
        @Override
        public abstract FeatureSet build();

        @Override
        public Builder setManager(CreateUpdateManager aThis) {
            aSet.setManager(aThis);
            return this;
        }
        
        public abstract FeatureSet.Builder setDescription(String description);
        public abstract FeatureSet.Builder setReferenceID(SGID referenceSGID);
        
        @Override
        public FeatureSet.Builder setFriendlyRowKey(String rowKey) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
