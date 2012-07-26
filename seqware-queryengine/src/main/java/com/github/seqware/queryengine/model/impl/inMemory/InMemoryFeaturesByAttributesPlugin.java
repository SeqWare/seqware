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

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.kernel.RPNStack;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.plugins.MapReducePlugin;
import java.util.HashSet;
import java.util.Set;

/**
 * Generic query implementation over all attributes of a Feature (including additional attributes).
 *
 * @author dyuen
 * @author jbaran
 */
public class InMemoryFeaturesByAttributesPlugin implements MapReducePlugin<Feature, FeatureSet> {

    private FeatureSet set;
    private RPNStack rpnStack;
    private Set<Feature> accumulator = new HashSet<Feature>();

    @Override
    public ReturnValue init(FeatureSet set, Object ... parameters) {
        this.set = set;
        this.rpnStack = (RPNStack)parameters[0];

        return new ReturnValue();
    }

    @Override
    public ReturnValue test() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue verifyParameters() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue verifyInput() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue filterInit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue filter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue mapInit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue map(Feature feature, FeatureSet mappedSet) {
        for (Feature f : set) {
            // Get the parameters from the RPN stack and replace them with concrete values:
            for (Object parameter : rpnStack.getParameters())
                rpnStack.setParameter(parameter, f.getAttribute((String)parameter));

            // Now carry out the actual evaluation that determines whether f is relevant:
            if ((Boolean)rpnStack.evaluate() == true){
                Feature build = f.toBuilder().build();
                accumulator.add(build);
            }
        }

        return new ReturnValue();
    }

    @Override
    public ReturnValue reduceInit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue reduce(FeatureSet mappedSet, FeatureSet resultSet) {
        // doesn't really do anything
        return new ReturnValue();
    }

    @Override
    public ReturnValue verifyOutput() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue cleanup() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FeatureSet getFinalResult() {
        CreateUpdateManager mManager = SWQEFactory.getModelManager();

        FeatureSet fSet = mManager.buildFeatureSet().setReference(mManager.buildReference().setName("ad_hoc_analysis").build()).build();
        for(Feature f : accumulator){
            mManager.objectCreated(f);
        }
        fSet.add(accumulator);
        mManager.close();

        return fSet;
    }
}
