package com.github.seqware.model;

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
public abstract class ReferenceSet extends Molecule<ReferenceSet> implements SeqWareIterable<Reference>{
    
    private String name;
    private String organism;
    
    /**
     * Creates an instance of an anonymous feature set.
     */
    private ReferenceSet() {
        super();
    }
    
    public ReferenceSet(String name, String organism){
        this();
        this.name = name;
        this.organism = organism;
    }

    /**
     * Adds a single new Feature to the set.
     *
     * @param reference The reference that is to be added to the reference set.
     */
    public abstract void add(Reference reference);

    /**
     * Adds a collection of new Feature to the set.
     *
     * @param references The references that are to be added to the reference set.
     */
    public abstract void add(Set<Reference> references);

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
}
