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
package com.github.seqware.queryengine.model.impl.inMemory;

import com.github.seqware.queryengine.model.Analysis;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.plugins.AnalysisPluginInterface;
import com.github.seqware.queryengine.plugins.MapReducePlugin;
import com.github.seqware.queryengine.plugins.ScanPlugin;

/**
 *
 * @author dyuen
 */
public class InMemoryQueryFutureImpl extends Analysis {
    
    private transient AnalysisPluginInterface plugin;

    public InMemoryQueryFutureImpl() {
        super();
    }

    @Override
    public FeatureSet get() {
        // these implementations actually make no sense, we are just filling 
        // something in for now 
        if (plugin instanceof MapReducePlugin){
            MapReducePlugin mrp = (MapReducePlugin)plugin;

            // TODO Set of mapped features is currently not used.
            for (Feature f : ((MapReducePlugin) plugin).getInputFeatureSet())
                mrp.map(f, null);
            for (Feature f : ((MapReducePlugin) plugin).getInputFeatureSet())
                mrp.reduce(null, null);
        } else if (plugin instanceof ScanPlugin){
            ScanPlugin sp = (ScanPlugin)plugin;

            // TODO Result set of scan is null for now...
            for (Feature f : ((MapReducePlugin) plugin).getInputFeatureSet())
                sp.scan(f, null);
        } else{
            // we have no other types of plugins yet(?)
            assert(false);
        }
        return getPlugin().getFinalResult();
    }

    @Override
    public boolean isDone() {
        return true;
    }

    /**
     * Create a new AnalysisSet builder
     *
     * @return
     */
    public static Analysis.Builder newBuilder() {
        return new InMemoryQueryFutureImpl.Builder();
    }

    @Override
    public Analysis.Builder toBuilder() {
        InMemoryQueryFutureImpl.Builder b = new InMemoryQueryFutureImpl.Builder();
        b.analysis = (InMemoryQueryFutureImpl) this.copy(true);
        b.setParameters(this.getParameters());
        return b;
    }

    @Override
    public AnalysisPluginInterface getPlugin() {
        return plugin;
    }

    @Override
    protected void setPlugin(AnalysisPluginInterface plugin) {
        this.plugin = plugin;
    }

    @Override
    public Class getHBaseClass() {
        return Analysis.class;
    }

    @Override
    public String getHBasePrefix() {
        return Analysis.prefix;
    }

    public static class Builder extends Analysis.Builder {

        public Builder() {
            analysis = new InMemoryQueryFutureImpl();
        }

        @Override
        public Analysis build() {
            if (analysis.getParameters() == null /**|| analysis.getPlugin() == null**/) {
                throw new RuntimeException("Invalid build of Analysis");
            }
            return analysis;
        }
    }
}
