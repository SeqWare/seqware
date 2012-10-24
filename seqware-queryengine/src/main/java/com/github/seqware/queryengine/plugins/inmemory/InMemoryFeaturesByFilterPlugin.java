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
import java.util.HashSet;
import java.util.Set;

/**
 * Generic query implementation over all attributes of a Feature using a FeatureFilter.
 *
 * @author dyuen
 * @author jbaran
 * @version $Id: $Id
 */
public abstract class InMemoryFeaturesByFilterPlugin extends AbstractMRInMemoryPlugin {
    private Object[] parameters;    
    private Set<Feature> accumulator = new HashSet<Feature>();
    
    /**
     * <p>getFilter.</p>
     *
     * @return a {@link com.github.seqware.queryengine.plugins.inmemory.FeatureFilter} object.
     */
    protected abstract FeatureFilter getFilter();

    /** {@inheritDoc} */
    @Override
    public ReturnValue init(FeatureSet inputSet, Object ... parameters) {
        this.inputSet = inputSet;
        this.parameters = parameters;
        return new ReturnValue();
    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue map(Feature feature, FeatureSet mappedSet) {
        boolean result = getFilter().featurePasses(feature, parameters);

        // Now carry out the actual evaluation that determines whether f is relevant:
        if (result){
            Feature build = feature.toBuilder().build();
            accumulator.add(build);
        }

        return new ReturnValue();
    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue reduce(FeatureSet mappedSet, FeatureSet resultSet) {
        // doesn't really do anything
        return new ReturnValue();
    }

    /** {@inheritDoc} */
    @Override
    public FeatureSet getFinalResult() {
        super.performInMemoryRun();
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        FeatureSet fSet = mManager.buildFeatureSet().setReference(inputSet.getReference()).build();
        for(Feature f : accumulator){
            mManager.objectCreated(f);
        }
        fSet.add(accumulator);
        mManager.close();
        return fSet;
    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue reduceInit() {
        // doesn't really do anything
        return new ReturnValue();
    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue mapInit() {
        // doesn't really do anything
        return new ReturnValue();
    }
}
