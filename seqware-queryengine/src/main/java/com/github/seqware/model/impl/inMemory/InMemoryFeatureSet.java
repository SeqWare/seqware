package com.github.seqware.model.impl.inMemory;

import com.github.seqware.model.AnalysisSet;
import com.github.seqware.model.Feature;
import com.github.seqware.model.FeatureSet;
import com.github.seqware.model.Reference;
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
    protected InMemoryFeatureSet() {
        super();
    }

    @Override
    public void add(Feature feature) {
        features.add(feature);
        this.setPrecedingVersion(this);
    }

    @Override
    public void add(Set<Feature> features) {
        this.features.addAll(features);
        this.setPrecedingVersion(this);
    }

    @Override
    public Iterator<Feature> getFeatures() {
        return new InMemoryIterator<Feature>(features.iterator());
    }

    @Override
    public Iterator<Feature> iterator() {
        return getFeatures();
    }

    @Override
    public long getCount() {
        return features.size();
    }

    public static FeatureSet.Builder newBuilder() {
        return new InMemoryFeatureSet.Builder();
    }

    @Override
    public InMemoryFeatureSet.Builder toBuilder() {
        InMemoryFeatureSet.Builder b = new InMemoryFeatureSet.Builder();
        b.aSet = (InMemoryFeatureSet) this.copy(true);
        return b;
    }

    public static class Builder extends FeatureSet.Builder {
        
        public Builder(){
            aSet = new InMemoryFeatureSet();
        }

        @Override
        public FeatureSet build(boolean newObject) {
            if (aSet.getReference() == null && aSet.getManager() != null) {
                throw new RuntimeException("Invalid build of AnalysisSet");
            }
            aSet.getManager().objectCreated(aSet, newObject);
            return aSet;
        }
    }

}
