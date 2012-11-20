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
package com.github.seqware.queryengine.impl;

import com.github.seqware.queryengine.plugins.plugins.FeatureSetCountPlugin;
import com.github.seqware.queryengine.backInterfaces.StorageInterface;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.kernel.RPNStack;
import com.github.seqware.queryengine.kernel.RPNStack.Parameter;
import com.github.seqware.queryengine.kernel.RPNStack.TagHierarchicalOccurrence;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.PluginRun;
import com.github.seqware.queryengine.model.QueryFuture;
import com.github.seqware.queryengine.model.TagSet;
import com.github.seqware.queryengine.plugins.PluginInterface;
import com.github.seqware.queryengine.plugins.hbasemr.*;
import com.github.seqware.queryengine.plugins.plugins.FeaturesAllPlugin;
import com.github.seqware.queryengine.plugins.plugins.FeaturesByAttributesPlugin;
import com.github.seqware.queryengine.plugins.plugins.FeaturesByRangePlugin;
import com.github.seqware.queryengine.plugins.plugins.FeaturesByTagPlugin;

import java.util.LinkedList;
import java.util.List;

/**
 * Implement HBase optimizations for the back-end. Will implement
 * and link up actual Map/Reduce plug-ins here
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class MRHBasePersistentBackEnd extends HBasePersistentBackEnd {

    /**
     * <p>Constructor for MRHBasePersistentBackEnd.</p>
     *
     * @param i a {@link com.github.seqware.queryengine.impl.StorageInterface} object.
     */
    public MRHBasePersistentBackEnd(StorageInterface i) {
        super(i);
    }
    
    /** {@inheritDoc} */
    @Override
    public QueryFuture getFeatures(int hours, FeatureSet set) {
        PluginInterface plugin = new FeaturesAllPlugin();
        plugin.init(set);
        return PluginRun.newBuilder().setPluginRunner(SWQEFactory.getPluginRunner(plugin, set)).build();
    }

     /** {@inheritDoc} */
     @Override
    public QueryFuture getFeaturesByAttributes(int hours, FeatureSet set, RPNStack constraints) {
        PluginInterface plugin = new FeaturesByAttributesPlugin();
        List<TagSet> tagSets = new LinkedList<TagSet>();
        // If there are hierarchical occurrences to be checked, retrieve the tag set now, so that paths in
        // trees can be resolved later on.
        for (Parameter parameter : constraints.getParameters())
            if (parameter instanceof TagHierarchicalOccurrence)
                tagSets.add(SWQEFactory.getQueryInterface().getLatestAtomByRowKey(((TagHierarchicalOccurrence) parameter).getTagSetRowKey(), TagSet.class));
        plugin.init(set, constraints, tagSets);
        return PluginRun.newBuilder().setPluginRunner(SWQEFactory.getPluginRunner(plugin, set, constraints, tagSets)).build();
    }

    //TODO: not implemented, still not sure what this was supposed to be
//    @Override
//    public QueryFuture getFeaturesByReference(int hours, FeatureSet set, Reference reference) {
//        PluginInterface plugin = new InMemoryFeaturesByReferencePlugin();
//        plugin.init(set);
//        return InMemoryQueryFutureImpl.newBuilder().setPlugin(plugin).build();
//    }

    /** {@inheritDoc} */
    @Override
    public QueryFuture getFeaturesByRange(int hours, FeatureSet set, Location location, String structure, long start, long stop) {
        PluginInterface plugin = new FeaturesByRangePlugin();
        plugin.init(set, location, structure, start, stop);
        return PluginRun.newBuilder().setPluginRunner(SWQEFactory.getPluginRunner(plugin, set, location, structure, start, stop)).build();
    }

    /** {@inheritDoc} */
    @Override
    public QueryFuture getFeaturesByTag(int hours, FeatureSet set, String subject, String predicate, String object) {
        PluginInterface plugin = new FeaturesByTagPlugin();
        plugin.init(set, subject, predicate, object);
        return PluginRun.newBuilder().setPluginRunner(SWQEFactory.getPluginRunner(plugin, set, subject, predicate, object)).build();
    }
    
    /** {@inheritDoc} */
    @Override
    public QueryFuture<Long> getFeatureSetCount(int hours, FeatureSet set) {
        PluginInterface plugin = new FeatureSetCountPlugin();
        plugin.init(set);
        return PluginRun.newBuilder().setPluginRunner(SWQEFactory.getPluginRunner(plugin, set)).build();
    }
}
