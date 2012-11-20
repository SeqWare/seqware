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

import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.plugins.MapReducePlugin;
import com.github.seqware.queryengine.plugins.MapperInterface;
import com.github.seqware.queryengine.plugins.PluginInterface;
import com.github.seqware.queryengine.plugins.ReducerInterface;
import java.util.Collection;
import org.apache.commons.lang.SerializationUtils;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;

/**
 * Counts the number of Features in a FeatureSet
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class FeatureSetCountPlugin extends MapReducePlugin<Collection<Feature>, Object, Object, Object, Object, Object, Long> {

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] handleSerialization(Object... parameters) {
        byte[] serialize = SerializationUtils.serialize(parameters);
        return serialize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] getInternalParameters() {
        return new Object[0];
    }

    @Override
    public ResultMechanism getResultMechanism() {
        return PluginInterface.ResultMechanism.COUNTER;
    }

    @Override
    public Class<?> getResultClass() {
        return Long.class;
    }
    
    @Override
    public Class<?> getOutputClass() {
        return NullOutputFormat.class;
    }

    @Override
    public void map(Collection<Feature> atom, MapperInterface<Collection<Feature>, Object> mapperInterface) {
        for (Feature f : atom) {
            // why can't I increment this by the size directly on the cluster?
            mapperInterface.incrementCounter();
        }
    }

    @Override
    public void reduce(Object reduceKey, Iterable<Object> reduceValues, ReducerInterface<Object, Object> reducerInterface) {
        /** do nothing */
    }
}
