package com.github.seqware.model;

import com.github.seqware.model.interfaces.BaseBuilder;
import com.github.seqware.model.interfaces.AbstractSet;
import com.github.seqware.factory.ModelManager;
import com.github.seqware.model.impl.MoleculeImpl;
import com.github.seqware.util.SeqWareIterable;
import java.util.Iterator;
import java.util.Set;

/**
 * A ReferenceSet is a collection of references which are a collection of
 * contigs and coordinates. Consider an example to be "Homo Sapiens".
 *
 * @author dyuen
 * @author jbaran
 */
public abstract class ReferenceSet extends MoleculeImpl<ReferenceSet> implements AbstractSet<ReferenceSet, Reference>{
    
    private String name;
    private String organism;
    
    /**
     * Creates an instance of an anonymous feature set.
     */
    protected ReferenceSet() {
        super();
    }

    /**
     * Adds a single new Feature to the set.
     *
     * @param reference The reference that is to be added to the reference set.
     */
    @Override
    public abstract ReferenceSet add(Reference reference);

    /**
     * Adds a collection of new Feature to the set.
     *
     * @param references The references that are to be added to the reference set.
     */
    @Override
    public abstract ReferenceSet add(Set<Reference> references);

    /**
     * Get the list of features associated with this feature set.
     *
     * @return Iterator of features.
     */
    public abstract Iterator<Reference> getReferences();
    
    /**
     * Get the name of the reference set (ex: "Human")
     * @return the name of this reference set
     */
    public String getName(){
        return name;
    }
    
    /**
     * Get the name of the organism (ex: "Homo Sapiens")
     * @return the organism that this reference set is associated with
     */
    public String getOrganism(){
        return organism;
    }
    
    /**
     * Create an FeatureSet builder started with a copy of this
     * @return 
     */
    @Override
    public abstract ReferenceSet.Builder toBuilder();

    public abstract static class Builder implements BaseBuilder {

        public ReferenceSet aSet;

        public ReferenceSet.Builder setName(String name) {
            aSet.name = name;
            return this;
        }
        
        public ReferenceSet.Builder setOrganism(String organism) {
            aSet.organism = organism;
            return this;
        }
        
        @Override
        public ReferenceSet build() {
           return build(true);
        }

        public abstract ReferenceSet build(boolean newObject);

        @Override
        public Builder setManager(ModelManager aThis) {
            aSet.setManager(aThis);
            return this;
        }
    }
}
