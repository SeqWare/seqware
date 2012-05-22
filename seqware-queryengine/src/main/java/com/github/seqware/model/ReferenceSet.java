package com.github.seqware.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * A ReferenceSet is a collection of references which are a collection of
 * contigs and coordinates. Consider an example to be "Homo Sapiens"
 * 
 *
 * @author dyuen
 */
public abstract class ReferenceSet extends Molecule {

    private Set<Reference> references = new HashSet<Reference>();

    /**
     * Creates an instance of an anonymous feature set.
     */
    public ReferenceSet() {
        super();
    }

    /**
     * Get the set of references that variants may be associated with this set
     *
     * @return set of references
     */
    public Set<Reference> getReferences() {
        return references;
    }
}
