package com.github.seqware.model;

import java.util.Iterator;
import java.util.Set;

/**
 * A ReferenceSet is a collection of references which are a collection of
 * contigs and coordinates. Consider an example to be "Homo Sapiens".
 *
 * @author dyuen
 * @author jbaran
 */
public abstract class ReferenceSet extends Molecule implements Iterable<Reference>{
    /**
     * Creates an instance of an anonymous feature set.
     */
    public ReferenceSet() {
        super();
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
}
