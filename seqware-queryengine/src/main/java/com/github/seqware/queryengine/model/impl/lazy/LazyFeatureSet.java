package com.github.seqware.queryengine.model.impl.lazy;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.impl.HBaseStorage;
import com.github.seqware.queryengine.impl.StorageInterface;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.model.impl.FeatureList;
import com.github.seqware.queryengine.model.impl.LazyMolSet;
import com.github.seqware.queryengine.util.FSGID;
import com.github.seqware.queryengine.util.LazyReference;
import com.github.seqware.queryengine.util.SGID;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An "lazy" representation of a feature set. This forces individual members to
 * persist and manage their own membership.
 *
 * @author dyuen
 */
public class LazyFeatureSet extends FeatureSet implements LazyMolSet<FeatureSet, Feature> {

    /**
     * Associated reference.
     */
    private LazyReference<Reference> reference = new LazyReference<Reference>(Reference.class);
    /**
     * User defined description of this feature set, can be used to store pragma
     * information for a set of features.
     */
    private String description = null;

    /**
     * Creates an in-memory feature set.
     */
    protected LazyFeatureSet() {
        super();
    }

    /**
     * Get the reference for this featureSet
     *
     * @return reference for the feature set
     */
    @Override
    public Reference getReference() {
        return this.reference.get();
    }

    @Override
    public FeatureSet add(Feature feature) {
        upgradeFeatureSGID(feature);
        if (this.getManager() != null) {
            this.getManager().atomStateChange(this, CreateUpdateManager.State.NEW_VERSION);
        }
        return this;
    }

    private void upgradeFeatureSGID(Feature feature) {
        // try upgrading Feature IDs here, faster than in model manager and FeatureSets should be guaranteed to have references
//        if (!(feature.getSGID() instanceof FSGID)) {
            FSGID fsgid = new FSGID(feature.getSGID(), feature, this);
            // as a convenience, we should have Features in a FeatureSet and the associated FeatureLists take on the time
            // of the FeatureSet
            fsgid.setBackendTimestamp(this.getTimestamp());
            feature.impersonate(fsgid, feature.getPrecedingSGID());
            if (getManager() != null) {
                getManager().atomStateChange(feature, CreateUpdateManager.State.NEW_VERSION);
            }
//        }
    }

    private void entombFeatureSGID(Feature feature) {
        assert (feature.getSGID() instanceof FSGID);
        FSGID fsgid = (FSGID) feature.getSGID();
        fsgid.setTombstone(true);
        if (getManager() != null) {
            getManager().atomStateChange(feature, CreateUpdateManager.State.NEW_VERSION);
        }
    }

    @Override
    public FeatureSet add(Feature... elements) {
        for (Feature f : elements) {
            upgradeFeatureSGID(f);
        }
        if (this.getManager() != null) {
            this.getManager().atomStateChange(this, CreateUpdateManager.State.NEW_VERSION);
        }
        return this;
    }

    @Override
    public FeatureSet remove(Feature feature) {
        entombFeatureSGID(feature);
        if (this.getManager() != null) {
            this.getManager().atomStateChange(this, CreateUpdateManager.State.NEW_VERSION);
        }
        return this;
    }

    @Override
    public FeatureSet add(Collection<Feature> features) {
        for (Feature f : features) {
            upgradeFeatureSGID(f);
        }
        if (this.getManager() != null) {
            this.getManager().atomStateChange(this, CreateUpdateManager.State.NEW_VERSION);
        }
        return this;
    }

    @Override
    public Iterator<Feature> getFeatures() {
        // for now, this only makes sense for HBase
        assert (SWQEFactory.getStorage() instanceof HBaseStorage);
        return ((HBaseStorage) SWQEFactory.getStorage()).getAllFeaturesForFeatureSet(this).iterator();
    }

    public String getTablename() {
        return FeatureList.prefix + StorageInterface.separator + this.reference.get().getName();
    }

    @Override
    public Iterator<Feature> iterator() {
        return getFeatures();
    }

    @Override
    public long getCount() {
        Logger.getLogger(HBaseStorage.class.getName()).log(Level.WARNING, "Iterating through a LazyFeatureSet is expensive, avoid this");
        // expensive, we need to iterate and count
        Iterator<Feature> features = this.getFeatures();
        long count = 0;
        while (features.hasNext()) {
            Feature next = features.next();
            if (((FSGID) next.getSGID()).isTombstone()) {
                continue;
            }
            count++;
        }
        return count;
    }

    public static FeatureSet.Builder newBuilder() {
        return new LazyFeatureSet.Builder();
    }

    @Override
    public LazyFeatureSet.Builder toBuilder() {
        LazyFeatureSet.Builder b = new LazyFeatureSet.Builder();
        b.aSet = (LazyFeatureSet) this.copy(true);
        return b;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public SGID getReferenceID() {
        return this.reference.getSGID();
    }

    @Override
    public Class getHBaseClass() {
        return FeatureSet.class;
    }

    @Override
    public String getHBasePrefix() {
        return FeatureSet.prefix;
    }

    @Override
    public void rebuild() {
        /**
         * do nothing
         */
    }

    public static class Builder extends FeatureSet.Builder {

        public Builder() {
            aSet = new LazyFeatureSet();
        }

        @Override
        public FeatureSet build(boolean newObject) {
            if (aSet.getReferenceID() == null && aSet.getManager() != null) {
                throw new RuntimeException("Invalid build of AnalysisSet");
            }
            if (aSet.getManager() != null) {
                aSet.getManager().objectCreated(aSet);
            }
            return aSet;
        }

        @Override
        public LazyFeatureSet.Builder setDescription(String description) {
            ((LazyFeatureSet) aSet).description = description;
            return this;
        }

        @Override
        public Builder setReference(Reference reference) {
            ((LazyFeatureSet) aSet).reference.set(reference);
            return this;
        }

        @Override
        public Builder setReferenceID(SGID referenceSGID) {
            ((LazyFeatureSet) aSet).reference.setSGID(referenceSGID);
            return this;
        }
    }
}
