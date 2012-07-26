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
 */
public class FeatureList extends AtomImpl<FeatureList> {

    public final static String prefix = "Feature";

    private List<Feature> features = new ArrayList<Feature>();

    public FeatureList() {
        super();
    }
    
    /**
     * Returns a list of features at this location, should not be modified.
     * TODO: Make this immutable, changes won't be tracked by manager this way
     * @return 
     */
    public List<Feature> getFeatures(){
        return features;
    }

    @Override
    public Class getHBaseClass() {
        return FeatureList.class;
    }

    @Override
    public String getHBasePrefix() {
        assert(this.getSGID() instanceof FSGID);
        FSGID fsgid = (FSGID) this.getSGID();
        return fsgid.getTablename();
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public static FeatureList.Builder newBuilder() {
        return new FeatureList.Builder();
    }

    @Override
    public FeatureList.Builder toBuilder() {
        FeatureList.Builder b = new FeatureList.Builder();
        b.featureArr = (FeatureList) this.copy(true);
        return b;
    }
    
    public FeatureList add(Feature element) {
        features.add(element);
//        if (this.getManager() != null){
//            this.getManager().atomStateChange(this, CreateUpdateManager.State.NEW_VERSION);
//        }
        return this;
    }
    
    public FeatureList remove(Feature element) {
        this.features.remove(element);
//        if (this.getManager() != null){
//            this.getManager().atomStateChange(this, CreateUpdateManager.State.NEW_VERSION);
//        }
        return this;
    }

    public static class Builder implements BaseBuilder {

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
    }
}
