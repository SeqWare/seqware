package com.github.seqware.queryengine.model.impl.inMemory;

import com.github.seqware.queryengine.Constants;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.Reference;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * An in-memory representation of a reference.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class InMemoryReference extends AbstractInMemorySet<Reference, FeatureSet> implements Reference {
    
    private String name;
    
    
    /**
     * Anonymous constructor
     */
    protected InMemoryReference(){
        super();
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<FeatureSet> featureSets() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Create a new Reference builder
     *
     * @return a {@link com.github.seqware.queryengine.model.Reference.Builder} object.
     */
    public static Reference.Builder newBuilder() {
        return new InMemoryReference.Builder();
    }

    /** {@inheritDoc} */
    @Override
    public Reference.Builder toBuilder() {
        InMemoryReference.Builder b = new InMemoryReference.Builder();
        b.reference = (Reference) this.copy(true);
        return b;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public Class getHBaseClass() {
        return Reference.class;
    }

    /** {@inheritDoc} */
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
            if (reference.getName() == null || !Pattern.matches(Constants.refRegex,reference.getName())) {
                throw new RuntimeException("Invalid reference name ("+reference.getName()+") names should be of the form " +Constants.refRegex);
            }
            if (((InMemoryReference)reference).getManager() != null) {
                ((InMemoryReference)reference).getManager().objectCreated(reference);
            }
            return reference;
        }
    }
}
