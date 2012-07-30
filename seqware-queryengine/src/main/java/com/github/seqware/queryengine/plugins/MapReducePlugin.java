package com.github.seqware.queryengine.plugins;

import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.interfaces.MolSetInterface;

/**
 * An abstracted map-reduce interface.
 *
 * Implementation orients itself on HBase's TableMapper, TableReduce.
 *
 * @author jbaran
 */
public abstract class MapReducePlugin<T extends Atom, S extends MolSetInterface> implements AnalysisPluginInterface {

    protected FeatureSet inputSet;

    /**
     * Returns the feature set on which the map-reduce is carried out on.
     */
    public FeatureSet getInputFeatureSet() {
        return this.inputSet;
    }

    /**
     * Mapping implementation that singles out desired atoms into a mapped set.
     *
     * @param atom Atom that is to be either dropped, or added to mappedSet.
     * @param mappedSet Set of atoms that are passed to the reduce
     * implementation.
     */
    public abstract ReturnValue map(T atom, S mappedSet);

    /**
     * Reduce implementation that takes mapped atoms and processes them.
     *
     * @param mappedSet Atoms that were selected during the mapping step.
     * @param resultSet Atoms that are created as a result of the reduce step.
     */
    public abstract ReturnValue reduce(S mappedSet, S resultSet);

    public abstract ReturnValue reduceInit();

    public abstract ReturnValue mapInit();
}
