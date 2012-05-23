package com.github.seqware.model;

import com.github.seqware.util.InMemoryIterator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * An in-memory representation of a reference set.
 *
 * @author jbaran
 */
public class InMemoryReferenceSet extends ReferenceSet {

    /**
     * The set of references this instance represents when an in-memory storage model is used.
     */
    private Set<Reference> references = new HashSet<Reference>();

    /**
     * Creates an in-memory feature set.
     */
    public InMemoryReferenceSet() {
        super();
    }

    @Override
    public void add(Reference reference) {
        references.add(reference);
    }

    @Override
    public void add(Set<Reference> references) {
        references.addAll(references);
    }

    @Override
    public Iterator<Reference> getReferences() {
        return new InMemoryIterator<Reference>(references.iterator());
    }
}
