package com.github.seqware.queryengine.plugins;

import com.github.seqware.queryengine.model.Atom;

/**
 * An abstracted map-reduce interface. These interfaces will eventually restrict our plug-ins.
 * Currently, they are just place-holders.
 *
 * Implementation orients itself on HBase's Scan.
 *
 * @author jbaran
 * @version $Id: $Id
 * @param <T> input type for the scan
 * @param <S> result type for the scan
 */
public interface ScanPlugin<T extends Atom, S> extends PluginInterface{
    /**
     * Scanner implementation that processes atoms and aggregates results in a new set.
     *
     * @param atom Atom that falls within the scan range.
     * @param resultSet Set of atoms that are the result of the scanning process.
     */
    public void scan(T atom);
}
