package com.github.seqware.queryengine.model.impl.inMemory;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.util.FSGID;
import com.github.seqware.queryengine.util.InMemoryIterator;
import com.github.seqware.queryengine.util.LazyReference;
import com.github.seqware.queryengine.util.SGID;
import java.util.*;

/**
 * An in-memory representation of a feature set.
 *
 * @author jbaran
 * @version $Id: $Id
 */
public class InMemoryFeatureSet extends FeatureSet {

    /**
     * Associated reference.
     */
    private LazyReference<Reference> reference = new LazyReference<Reference>(Reference.class);
    /**
     * The set of features this instance represents when an in-memory storage
     * model is used.
     */
    private Set<Feature> features = new HashSet<Feature>();
    /**
     * User defined description of this feature set, can be used to store pragma
     * information for a set of features.
     */
    private String description = null;

    /**
     * Creates an in-memory feature set.
     */
    private InMemoryFeatureSet() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * Get the reference for this featureSet
     */
    @Override
    public Reference getReference() {
        return this.reference.get();
    }

    /** {@inheritDoc} */
    @Override
    public FeatureSet add(Feature feature) {
        upgradeFeatureSGID(feature);
        features.add(feature);
        if (this.getManager() != null) {
            this.getManager().atomStateChange(this, CreateUpdateManager.State.NEW_VERSION);
        }
        return this;
    }

    /**
     * <p>upgradeFeatureSGID.</p>
     *
     * @param feature a {@link com.github.seqware.queryengine.model.Feature} object.
     */
    protected void upgradeFeatureSGID(Feature feature) {
        // try upgrading Feature IDs here, faster than in model manager and FeatureSets should be guaranteed to have references
        if (!(feature.getSGID() instanceof FSGID)){
            FSGID fsgid = new FSGID(feature.getSGID(), feature, this);
            // as a convenience, we should have Features in a FeatureSet and the associated FeatureLists take on the time
            // of the FeatureSet
            fsgid.setBackendTimestamp(this.getTimestamp());
            feature.impersonate(fsgid, feature.getPrecedingSGID());
        }
    }

    /** {@inheritDoc} */
    @Override
    public FeatureSet add(Feature... elements) {
        for (Feature f : elements) {
            upgradeFeatureSGID(f);
        }
        this.features.addAll(Arrays.asList(elements));
        if (this.getManager() != null) {
            this.getManager().atomStateChange(this, CreateUpdateManager.State.NEW_VERSION);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public FeatureSet remove(Feature feature) {
        features.remove(feature);
        if (this.getManager() != null) {
            this.getManager().atomStateChange(this, CreateUpdateManager.State.NEW_VERSION);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public FeatureSet add(Collection<Feature> features) {
        for (Feature f : features) {
            upgradeFeatureSGID(f);
        }
        this.features.addAll(features);
        if (this.getManager() != null) {
            this.getManager().atomStateChange(this, CreateUpdateManager.State.NEW_VERSION);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<Feature> getFeatures() {
        return new InMemoryIterator<Feature>(features.iterator());
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<Feature> iterator() {
        return getFeatures();
    }

    /** {@inheritDoc} */
    @Override
    public long getCount() {
        return features.size();
    }

    /**
     * <p>newBuilder.</p>
     *
     * @return a {@link com.github.seqware.queryengine.model.FeatureSet.Builder} object.
     */
    public static FeatureSet.Builder newBuilder() {
        return new InMemoryFeatureSet.Builder();
    }

    /** {@inheritDoc} */
    @Override
    public InMemoryFeatureSet.Builder toBuilder() {
        InMemoryFeatureSet.Builder b = new InMemoryFeatureSet.Builder();
        b.aSet = (InMemoryFeatureSet) this.copy(true);
        return b;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc} */
    @Override
    public SGID getReferenceID() {
        return this.reference.getSGID();
    }

    /** {@inheritDoc} */
    @Override
    public Class getHBaseClass() {
        return FeatureSet.class;
    }

    /** {@inheritDoc} */
    @Override
    public String getHBasePrefix() {
        return FeatureSet.prefix;
    }

    /** {@inheritDoc} */
    @Override
    public void rebuild() {
        Set<Feature> newSet = new HashSet<Feature>();
        for (Feature f : this.features) {
            newSet.add(f);
        }
        this.features = newSet;
    }

    public static class Builder extends FeatureSet.Builder {

        public Builder() {
            aSet = new InMemoryFeatureSet();
        }

        @Override
        public FeatureSet build() {
            if (aSet.getReferenceID() == null && aSet.getManager() != null) {
                throw new RuntimeException("Invalid build of Plugin");
            }
            if (aSet.getManager() != null) {
                aSet.getManager().objectCreated(aSet);
            }
            return aSet;
        }

        @Override
        public InMemoryFeatureSet.Builder setDescription(String description) {
            ((InMemoryFeatureSet) aSet).description = description;
            return this;
        }

        @Override
        public Builder setReference(Reference reference) {
            ((InMemoryFeatureSet) aSet).reference.set(reference);
            return this;
        }

        @Override
        public Builder setReferenceID(SGID referenceSGID) {
            ((InMemoryFeatureSet) aSet).reference.setSGID(referenceSGID);
            return this;
        }
    }
}
