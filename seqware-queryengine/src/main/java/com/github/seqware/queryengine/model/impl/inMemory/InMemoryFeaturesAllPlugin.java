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

import com.github.seqware.queryengine.factory.Factory;
import com.github.seqware.queryengine.factory.ModelManager;
import com.github.seqware.queryengine.model.AnalysisPluginInterface;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.plugins.MapReducePlugin;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author dyuen
 */
public class InMemoryFeaturesAllPlugin implements MapReducePlugin<Feature, FeatureSet> {

    private FeatureSet set;
    private Set<Feature> accumulator = new HashSet<Feature>();

    public AnalysisPluginInterface.ReturnValue init(FeatureSet set, Object... parameters) {
        this.set = set;
        return new AnalysisPluginInterface.ReturnValue();
    }

    public AnalysisPluginInterface.ReturnValue test() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public AnalysisPluginInterface.ReturnValue verifyParameters() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public AnalysisPluginInterface.ReturnValue verifyInput() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public AnalysisPluginInterface.ReturnValue filterInit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public AnalysisPluginInterface.ReturnValue filter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public AnalysisPluginInterface.ReturnValue mapInit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }



    public AnalysisPluginInterface.ReturnValue reduceInit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public AnalysisPluginInterface.ReturnValue verifyOutput() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public AnalysisPluginInterface.ReturnValue cleanup() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FeatureSet getFinalResult() {
        ModelManager mManager = Factory.getModelManager();
        FeatureSet fSet = mManager.buildFeatureSet().setReference(mManager.buildReference().setName("ad_hoc_analysis").build()).build();
        fSet.add(accumulator);
        mManager.close();
        return fSet;
    }

    @Override
    public ReturnValue map(Feature atom, FeatureSet mappedSet) {
        for (Feature f : set) {
            accumulator.add(f);
        }
        return new AnalysisPluginInterface.ReturnValue();
    }

    @Override
    public ReturnValue reduce(FeatureSet mappedSet, FeatureSet resultSet) {
        // doesn't really do anything
        return new AnalysisPluginInterface.ReturnValue();
    }
}
