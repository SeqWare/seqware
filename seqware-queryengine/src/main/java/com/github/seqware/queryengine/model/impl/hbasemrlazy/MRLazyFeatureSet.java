package com.github.seqware.queryengine.model.impl.hbasemrlazy;

import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.QueryFuture;
import com.github.seqware.queryengine.model.impl.LazyMolSet;
import com.github.seqware.queryengine.model.impl.lazy.LazyFeatureSet;
import com.github.seqware.queryengine.tutorial.MapReduceFeatureSetCounter;

/**
 * An "lazy" representation of a feature set. This forces individual members to
 * persist and manage their own membership. This version adds M/R for counting.
 *
 * @author dyuen
 */
public class MRLazyFeatureSet extends LazyFeatureSet implements LazyMolSet<FeatureSet, Feature> {

    /**
     * Creates an lazy M/R using feature set.
     */
    protected MRLazyFeatureSet() {
        super();
    }

    @Override
    public long getCount() {
        QueryFuture<Long> featureSetCount = SWQEFactory.getQueryInterface().getFeatureSetCount(0, this);
        return featureSetCount.get();
    }

    public static FeatureSet.Builder newBuilder() {
        return new MRLazyFeatureSet.Builder();
    }

    @Override
    public MRLazyFeatureSet.Builder toBuilder() {
        MRLazyFeatureSet.Builder b = new MRLazyFeatureSet.Builder();
        b.aSet = (MRLazyFeatureSet) this.copy(true);
        return b;
    }

    public static class Builder extends LazyFeatureSet.Builder {

        public Builder() {
            aSet = new MRLazyFeatureSet();
        }
    }
}
