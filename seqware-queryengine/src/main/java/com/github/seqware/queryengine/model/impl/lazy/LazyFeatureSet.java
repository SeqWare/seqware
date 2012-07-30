package com.github.seqware.queryengine.model.impl.lazy;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.impl.HBaseStorage;
import com.github.seqware.queryengine.impl.SimplePersistentBackEnd;
import com.github.seqware.queryengine.impl.StorageInterface;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.model.impl.FeatureList;
import com.github.seqware.queryengine.model.impl.LazyMolSet;
import com.github.seqware.queryengine.util.FSGID;
import com.github.seqware.queryengine.util.LazyReference;
import com.github.seqware.queryengine.util.SGID;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
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
    private LazyFeatureSet() {
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
        return new ListedIterator();
    }

    /**
     * Iterates through Features, taking into account
     */
    public class ListedIterator implements Iterator<Feature> {

        private final Iterator<FeatureList> iterator;
        private List<FeatureList> rowOfFeatureLists = new ArrayList<FeatureList>();
        // grab out a rowKey's worth of FeatureLists
        private String rowKey;
        // current consolidated row of Features
        private List<Feature> rowOfFeatures = new ArrayList<Feature>();

        public ListedIterator() {
            this.iterator = SWQEFactory.getStorage().getAllFeatureListsForFeatureSet(LazyFeatureSet.this).iterator();
        }

        @Override
        public boolean hasNext() {
            return rowOfFeatures.size() > 0 || iterator.hasNext() || rowOfFeatureLists.size() > 0;
        }

        @Override
        public Feature next() {
            FeatureList peekedList = null;
            if (rowOfFeatures.size() > 0) {
                return rowOfFeatures.remove(rowOfFeatures.size() - 1);
            } else {
                while (iterator.hasNext()) {
                    FeatureList nextL = iterator.next();
                    // the first time, we need to set the rowkey pre-emptively
                    if (rowKey == null){
                        rowKey = nextL.getSGID().getRowKey();
                    }
                    // ensure that row keys are ascending
                    assert (nextL.getSGID().getRowKey().compareTo(rowKey) >= 0);
                    if (!rowKey.equals(nextL.getSGID().getRowKey())) {
                        // we've moved onto a new row
                        rowKey = nextL.getSGID().getRowKey();
                        peekedList = nextL;
                        break;
                    } else {
                        // we're continuing the same row
                        rowOfFeatureLists.add(nextL);
                    }
                }
                assert(rowOfFeatureLists.size() > 0 || peekedList != null);
                if (rowOfFeatureLists.isEmpty()){
                    rowOfFeatureLists.add(peekedList);
                    peekedList = null;
                }
                // consolidate list
                rowOfFeatures.addAll(SimplePersistentBackEnd.consolidateRow(rowOfFeatureLists));
                assert(rowOfFeatures.size() > 0);
                rowOfFeatureLists.clear();
                if (peekedList != null){
                    rowOfFeatureLists.add(peekedList);
                    peekedList = null;
                }
                return rowOfFeatures.remove(rowOfFeatures.size() - 1);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public String getTablename() {
        return FeatureList.prefix + StorageInterface.SEPARATOR + this.reference.get().getName();
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
