package com.github.seqware.queryengine.model.impl.inMemory;

import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.Reference;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * An in-memory representation of a reference.
 *
 * @author dyuen
 */
public class InMemoryReference extends AbstractInMemorySet<Reference, FeatureSet> implements Reference {
    
    private String name;
    public static final String refRegex = "[a-zA-Z_0-9]+";
    
    /**
     * Anonymous constructor
     */
    protected InMemoryReference(){
        super();
    }

    @Override
    public Iterator<FeatureSet> featureSets() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Create a new AnalysisSet builder
     *
     * @return
     */
    public static Reference.Builder newBuilder() {
        return new InMemoryReference.Builder();
    }

    @Override
    public Reference.Builder toBuilder() {
        InMemoryReference.Builder b = new InMemoryReference.Builder();
        b.reference = (Reference) this.copy(true);
        return b;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class getHBaseClass() {
        return Reference.class;
    }

    @Override
    public String getHBasePrefix() {
        return Reference.prefix;
    }

    public static class Builder extends Reference.Builder {
        
        public Builder(){
            reference = new InMemoryReference();
        }
        
        @Override
        public Reference.Builder setName(String name) {
            ((InMemoryReference)reference).name = name;
            return this;
        }

        @Override
        public Reference build() {
            
            if (reference.getName() == null || !Pattern.matches(refRegex,reference.getName())) {
                throw new RuntimeException("Invalid reference name, names should be of the form " +refRegex);
            }
            if (((InMemoryReference)reference).getManager() != null) {
                ((InMemoryReference)reference).getManager().objectCreated(reference);
            }
            return reference;
        }
    }
}
