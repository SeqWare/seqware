/*
 * Copyright (C) 2012 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.seqware.queryengine.plugins.plugins;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.plugins.MapReducePlugin;
import com.github.seqware.queryengine.plugins.MapperInterface;
import com.github.seqware.queryengine.plugins.ReducerInterface;
import com.github.seqware.queryengine.system.importers.SOFeatureImporter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.log4j.Logger;

/**
 * Implements the generic queries which independently decide on whether a
 * Feature is included in a result.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public abstract class FeaturesByFilterPlugin extends MapReducePlugin<Collection<Feature>, FeatureSet, FeatureSet, FeatureSet, FeatureSet, FeatureSet, FeatureSet>  {

    private CreateUpdateManager modelManager;
    private long count = 0;

    /**
     * <p>getFilter.</p>
     *
     * @return a
     * {@link com.github.seqware.queryengine.plugins.inmemory.FeatureFilter}
     * object.
     */
    protected abstract FeatureFilter getFilter();
    
    @Override
    public void reduce(FeatureSet reduceKey, Iterable<FeatureSet> reduceValues, ReducerInterface<FeatureSet, FeatureSet> reducerInterface) {
        /** do nothing */
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void map(Collection<Feature> atom, MapperInterface<Collection<Feature>, FeatureSet> mapperInterface) {
        Collection<Feature> results = new ArrayList<Feature>();
        count++;

        for (Feature f : atom) {
            f.setManager(modelManager);
            boolean match = getFilter().featurePasses(f, mapperInterface.getExt_parameters());
            if (match) {
                results.add(f);
            }
        }
        mapperInterface.getDestSet().add(results);
        if (count % 1000 == 0) {
            Logger.getLogger(FeaturesByFilterPlugin.class.getName()).info(new Date().toString() + " with total lines so far: " + count);
        }
        // TODO: we only seem to be able to keep about a quarter of the number of Features that we can keep in memory in
        // VCF importer, check this out
        if (count % (SOFeatureImporter.BATCH_SIZE / 4) == 0) {
            modelManager.flush();
            modelManager.clear();
            modelManager.persist(mapperInterface.getDestSet());
            Logger.getLogger(FeaturesByFilterPlugin.class.getName()).info(new Date().toString() + " cleaning up with total lines: " + count);
        }
    }

    @Override
    public void mapInit(MapperInterface mapperInterface) {

        // specific to this kind of plugin
        //this.filter = (FeatureFilter) mapperInterface.getInt_parameters()[0];

        this.modelManager = SWQEFactory.getModelManager();
        this.modelManager.persist(mapperInterface.getDestSet());
    }

    @Override
    public void mapCleanup() {
        Logger.getLogger(FeaturesByFilterPlugin.class.getName()).info(new Date().toString() + " cleaning up with total lines: " + count);
        this.modelManager.close();
    }

    @Override
    public ResultMechanism getResultMechanism() {
        return ResultMechanism.BATCHEDFEATURESET;
    }

    @Override
    public Class getResultClass() {
        return FeatureSet.class;
    }
    
    @Override
    public Class<?> getOutputClass() {
        return NullOutputFormat.class;
    }
}
