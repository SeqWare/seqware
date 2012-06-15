package com.github.seqware.plugins;

import com.github.seqware.model.Atom;
import com.github.seqware.model.interfaces.AbstractSet;

/**
 * An abstracted map-reduce interface.
 *
 * Implementation orients itself on HBase's Scan.
 *
 * @author jbaran
 */
public interface ScanPlugin<T extends Atom, S extends AbstractSet> {
    /**
     * Scanner implementation that processes atoms and aggregates results in a new set.
     *
     * @param atom Atom that falls within the scan range.
     * @param resultSet Set of atoms that are the result of the scanning process.
     */
    public void scan(Atom<T> atom, AbstractSet<S, T> resultSet);
}
