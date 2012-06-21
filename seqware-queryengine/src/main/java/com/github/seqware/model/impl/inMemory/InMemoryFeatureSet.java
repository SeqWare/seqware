package com.github.seqware.model.impl.inMemory;

import com.github.seqware.factory.Factory;
import com.github.seqware.factory.ModelManager;
import com.github.seqware.model.Feature;
import com.github.seqware.model.FeatureSet;
import com.github.seqware.model.Reference;
import com.github.seqware.util.InMemoryIterator;
import com.github.seqware.util.LazyReference;
import com.github.seqware.util.SGID;
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
     * Associated reference.
     */
    private LazyReference<Reference> reference = new LazyReference<Reference>();

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
        features.add(feature);
        if (this.getManager() != null){
            this.getManager().AtomStateChange(this, ModelManager.State.NEW_VERSION); 
        }
        return this;
    }
    
    @Override
    public FeatureSet add(Feature... elements) {
       this.features.addAll(Arrays.asList(elements));
       if (this.getManager() != null){
            this.getManager().AtomStateChange(this, ModelManager.State.NEW_VERSION);  
       }
       return this;
    }
    
    @Override
    public FeatureSet remove(Feature feature) {
        features.remove(feature);
        if (this.getManager() != null){
            this.getManager().AtomStateChange(this, ModelManager.State.NEW_VERSION);  
        }
        return this;
    }

    @Override
    public FeatureSet add(Set<Feature> features) {
        this.features.addAll(features);
        if (this.getManager() != null){
            this.getManager().AtomStateChange(this, ModelManager.State.NEW_VERSION);  
        }
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
        b.aSet = (InMemoryFeatureSet) this.copy(true);
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

    public static class Builder extends FeatureSet.Builder {
        
        public Builder(){
            aSet = new InMemoryFeatureSet();
        }

        @Override
        public FeatureSet build(boolean newObject) {
            if (aSet.getReference() == null && aSet.getManager() != null) {
                throw new RuntimeException("Invalid build of AnalysisSet");
            }
            if (aSet.getManager() != null){
                aSet.getManager().objectCreated(aSet);
            }
            return aSet;
        }
        
        @Override
        public InMemoryFeatureSet.Builder setDescription(String description) {
            ((InMemoryFeatureSet)aSet).description = description;
            return this;
        }
        
        @Override
        public Builder setReference(Reference reference) {
            ((InMemoryFeatureSet)aSet).reference.set(reference);
            return this;
        }
        
        @Override
        public Builder setReferenceID(SGID referenceSGID){
            ((InMemoryFeatureSet)aSet).reference.setSGID(referenceSGID);
            return this;
        }
    }

}
