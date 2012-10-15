package com.github.seqware.queryengine.model.impl;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.interfaces.BaseBuilder;
import com.github.seqware.queryengine.util.FSGID;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * This is an internal atom class, that should never be viewed by the user.
 * Used to explicitly handle co-location of Features and to facilitate the
 * development of high capacity FeatureSets.
 *
 * Features should be detected and placed into this container automatically.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class FeatureList extends AtomImpl<FeatureList> {

    /** Constant <code>prefix="Feature"</code> */
    public final static String prefix = "Feature";

    private List<Feature> features = new ArrayList<Feature>();

    /**
     * <p>Constructor for FeatureList.</p>
     */
    public FeatureList() {
        super();
    }
    
    /**
     * Returns a list of features at this location, should not be modified.
     * TODO: Make this immutable, changes won't be tracked by manager this way
     *
     * @return a {@link java.util.List} object.
     */
    public List<Feature> getFeatures(){
        return features;
    }

    /** {@inheritDoc} */
    @Override
    public Class getHBaseClass() {
        return FeatureList.class;
    }

    /** {@inheritDoc} */
    @Override
    public String getHBasePrefix() {
        assert(this.getSGID() instanceof FSGID);
        FSGID fsgid = (FSGID) this.getSGID();
        return fsgid.getTablename();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * <p>newBuilder.</p>
     *
     * @return a {@link com.github.seqware.queryengine.model.impl.FeatureList.Builder} object.
     */
    public static FeatureList.Builder newBuilder() {
        return new FeatureList.Builder();
    }

    /** {@inheritDoc} */
    @Override
    public FeatureList.Builder toBuilder() {
        FeatureList.Builder b = new FeatureList.Builder();
        b.featureArr = (FeatureList) this.copy(true);
        return b;
    }
    
    /**
     * <p>add.</p>
     *
     * @param element a {@link com.github.seqware.queryengine.model.Feature} object.
     * @return a {@link com.github.seqware.queryengine.model.impl.FeatureList} object.
     */
    public FeatureList add(Feature element) {
        features.add(element);
//        if (this.getManager() != null){
//            this.getManager().atomStateChange(this, CreateUpdateManager.State.NEW_VERSION);
//        }
        return this;
    }
    
    /**
     * <p>remove.</p>
     *
     * @param element a {@link com.github.seqware.queryengine.model.Feature} object.
     * @return a {@link com.github.seqware.queryengine.model.impl.FeatureList} object.
     */
    public FeatureList remove(Feature element) {
        this.features.remove(element);
//        if (this.getManager() != null){
//            this.getManager().atomStateChange(this, CreateUpdateManager.State.NEW_VERSION);
//        }
        return this;
    }

    public static class Builder extends BaseBuilder {

        private FeatureList featureArr = new FeatureList();

        @Override
        public FeatureList build() {
//            if (featureArr.getManager() != null) {
//                featureArr.getManager().objectCreated(featureArr);
//            }
            return featureArr;
        }

        @Override
        public Builder setManager(CreateUpdateManager aThis) {
            featureArr.setManager(aThis);
            return this;
        }

        @Override
        public Builder setFriendlyRowKey(String rowKey) {
            throw new UnsupportedOperationException("FeatureLists do not support custom rowKey.");
        }
    }
}
