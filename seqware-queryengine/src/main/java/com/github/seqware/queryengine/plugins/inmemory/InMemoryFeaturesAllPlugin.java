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
package com.github.seqware.queryengine.plugins.inmemory;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.plugins.AnalysisPluginInterface;
import com.github.seqware.queryengine.plugins.MapReducePlugin;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author dyuen
 */
public class InMemoryFeaturesAllPlugin extends AbstractMRInMemoryPlugin {

    private Set<Feature> accumulator = new HashSet<Feature>();

    @Override
    public AnalysisPluginInterface.ReturnValue init(FeatureSet inputSet, Object... parameters) {
        this.inputSet = inputSet;
        return new AnalysisPluginInterface.ReturnValue();
    }

    @Override
    public AnalysisPluginInterface.ReturnValue mapInit() {
        /** do nothing */
        return null;
    }


    @Override
    public AnalysisPluginInterface.ReturnValue reduceInit() {
        /** do nothing */
        return null;
    }

    @Override
    public FeatureSet getFinalResult() {
        super.performInMemoryRun();
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        FeatureSet fSet = mManager.buildFeatureSet().setReference(mManager.buildReference().setName("ad_hoc_analysis").build()).build();
        for(Feature f : accumulator){
            mManager.objectCreated(f);
        }
        fSet.add(accumulator);
        mManager.close();
        return fSet;
    }

    @Override
    public ReturnValue map(Feature atom, FeatureSet mappedSet) {
        for (Feature f : this.inputSet) {
            Feature build = f.toBuilder().build();
            accumulator.add(build);
        }
        return new AnalysisPluginInterface.ReturnValue();
    }

    @Override
    public ReturnValue reduce(FeatureSet mappedSet, FeatureSet resultSet) {
        // doesn't really do anything
        return new AnalysisPluginInterface.ReturnValue();
    }
}
