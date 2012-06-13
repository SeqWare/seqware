package com.github.seqware.model.impl.inMemory;

import com.github.seqware.factory.ModelManager;
import com.github.seqware.model.Feature;
import com.github.seqware.model.FeatureSet;
import com.github.seqware.util.InMemoryIterator;
import java.util.Arrays;
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
     * User defined description of this feature set, can be used to store pragma information 
     * for a set of features.
     */
    private String description = null;

    /**
     * Creates an in-memory feature set.
     */
    protected InMemoryFeatureSet() {
        super();
    }
    
    public boolean contains(Feature f){
        return features.contains(f);
    }

    @Override
    public FeatureSet add(Feature feature) {
        features.add(feature);
        this.getManager().AtomStateChange(this, ModelManager.State.NEW_VERSION);  
        return this;
    }
    
    @Override
    public FeatureSet add(Feature... elements) {
       this.features.addAll(Arrays.asList(elements));
       this.getManager().AtomStateChange(this, ModelManager.State.NEW_VERSION);  
       return this;
    }
    
    @Override
    public FeatureSet remove(Feature feature) {
        features.remove(feature);
        this.getManager().AtomStateChange(this, ModelManager.State.NEW_VERSION);  
        return this;
    }

    @Override
    public FeatureSet add(Set<Feature> features) {
        this.features.addAll(features);
        this.getManager().AtomStateChange(this, ModelManager.State.NEW_VERSION);  
        return this;
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
        b.aSet = (InMemoryFeatureSet) this.copy(false);
        return b;
    }

    @Override
    public String getDescription() {
        return description;
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
            aSet.getManager().objectCreated(aSet);
            return aSet;
        }
        
        @Override
        public InMemoryFeatureSet.Builder setDescription(String description) {
            ((InMemoryFeatureSet)aSet).description = description;
            return this;
        }
    }

}
