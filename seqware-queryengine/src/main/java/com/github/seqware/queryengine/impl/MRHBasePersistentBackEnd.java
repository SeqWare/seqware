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

import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.kernel.RPNStack;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.QueryFuture;
import com.github.seqware.queryengine.model.TagSet;
import com.github.seqware.queryengine.model.impl.inMemory.InMemoryQueryFutureImpl;
import com.github.seqware.queryengine.plugins.AnalysisPluginInterface;
import com.github.seqware.queryengine.plugins.hbasemr.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Implement HBase optimizations for the back-end. Will implement 
 * and link up actual Map/Reduce plug-ins here
 *
 * @author dyuen
 */
public class MRHBasePersistentBackEnd extends HBasePersistentBackEnd {

    public MRHBasePersistentBackEnd(StorageInterface i) {
        super(i);
    }
    
    @Override
    public QueryFuture getFeatures(int hours, FeatureSet set) {
        AnalysisPluginInterface plugin = new MRFeaturesAllPlugin();
        plugin.init(set);
        return InMemoryQueryFutureImpl.newBuilder().setPlugin(plugin).build();
    }

     @Override
    public QueryFuture getFeaturesByAttributes(int hours, FeatureSet set, RPNStack constraints) {
        AnalysisPluginInterface plugin = new MRFeaturesByAttributesPlugin();
        List<TagSet> tagSets = new LinkedList<TagSet>();
        // TODO
        tagSets.add(SWQEFactory.getQueryInterface().getLatestAtomByRowKey("SO", TagSet.class));
        plugin.init(set, constraints, tagSets);
        return InMemoryQueryFutureImpl.newBuilder().setPlugin(plugin).build();
    }

    //TODO: not implemented, still not sure what this was supposed to be
//    @Override
//    public QueryFuture getFeaturesByReference(int hours, FeatureSet set, Reference reference) {
//        AnalysisPluginInterface plugin = new InMemoryFeaturesByReferencePlugin();
//        plugin.init(set);
//        return InMemoryQueryFutureImpl.newBuilder().setPlugin(plugin).build();
//    }

    @Override
    public QueryFuture getFeaturesByRange(int hours, FeatureSet set, Location location, String structure, long start, long stop) {
        AnalysisPluginInterface plugin = new MRFeaturesByRangePlugin();
        plugin.init(set, location, structure, start, stop);
        return InMemoryQueryFutureImpl.newBuilder().setPlugin(plugin).build();
    }

    @Override
    public QueryFuture getFeaturesByTag(int hours, FeatureSet set, String subject, String predicate, String object) {
        AnalysisPluginInterface plugin = new MRFeaturesByTagsPlugin();
        plugin.init(set, subject, predicate, object);
        return InMemoryQueryFutureImpl.newBuilder().setPlugin(plugin).build();
    }
    
    @Override
    public QueryFuture<Long> getFeatureSetCount(int hours, FeatureSet set) {
        AnalysisPluginInterface plugin = new MRFeatureSetCountPlugin();
        plugin.init(set);
        return InMemoryQueryFutureImpl.newBuilder().setPlugin(plugin).build();
    }
}
