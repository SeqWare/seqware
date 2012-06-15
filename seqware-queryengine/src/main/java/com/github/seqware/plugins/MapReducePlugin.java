package com.github.seqware.plugins;

import com.github.seqware.model.Atom;
import com.github.seqware.model.interfaces.AbstractSet;

/**
 * An abstracted map-reduce interface.
 *
 * Implementation orients itself on HBase's TableMapper, TableReduce.
 *
 * @author jbaran
 */
public interface MapReducePlugin<T extends Atom, S extends AbstractSet> {
    /**
     * Mapping implementation that singles out desired atoms into a mapped set.
     *
     * @param atom Atom that is to be either dropped, or added to mappedSet.
     * @param mappedSet Set of atoms that are passed to the reduce implementation.
     */
    public void map(Atom<T> atom, AbstractSet<S, T> mappedSet);

    /**
     * Reduce implementation that takes mapped atoms and processes them.
     *
     * @param mappedSet Atoms that were selected during the mapping step.
     * @param resultSet Atoms that are created as a result of the reduce step.
     */
    public void reduce(AbstractSet<S, T> mappedSet, AbstractSet<S, T> resultSet);
}
