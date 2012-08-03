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
package com.github.seqware.queryengine.plugins.lazyinmemory;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.kernel.RPNStack;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.plugins.inmemory.AbstractMRInMemoryPlugin;

/**
 * Generic query implementation over all attributes of a Feature (including additional attributes).
 * Experimenting with a managed lazy implementation.
 *
 * @author dyuen
 * @author jbaran
 */
public class LazyFeaturesByAttributesPlugin extends AbstractMRInMemoryPlugin {

    private RPNStack rpnStack;
    private FeatureSet set;
    private CreateUpdateManager manager;

    @Override
    public ReturnValue init(FeatureSet inputSet, Object ... parameters) {
        this.manager = SWQEFactory.getModelManager();
        //output set should attach to the original reference
        set = manager.buildFeatureSet().setReferenceID(inputSet.getReferenceID()).build();
        this.inputSet = inputSet;
        this.rpnStack = (RPNStack)parameters[0];
        // set is no longer managed, but we can still attach to it
        return new ReturnValue();
    }

    @Override
    public ReturnValue mapInit() {
        /** do nothing */
        return null;
    }

    @Override
    public ReturnValue map(Feature feature, FeatureSet mappedSet) {
        // Get the parameters from the RPN stack and replace them with concrete values:
        for (Object parameter : rpnStack.getParameters())
            rpnStack.setParameter(parameter, feature.getAttribute((String)parameter));

        // Now carry out the actual evaluation that determines whether f is relevant:
        if ((Boolean)rpnStack.evaluate() == true){
            Feature build = feature.toBuilder().build();
            build.setManager(manager);
            set.add(build);
        }
        return new ReturnValue();
    }

    @Override
    public ReturnValue reduceInit() {
        /** do nothing */
        return null;
    }

    @Override
    public ReturnValue reduce(FeatureSet mappedSet, FeatureSet resultSet) {
        // doesn't really do anything
        return new ReturnValue();
    }

    @Override
    public FeatureSet getFinalResult() {
        super.performInMemoryRun();
        manager.close();
        return SWQEFactory.getQueryInterface().getLatestAtomBySGID(set.getSGID(), FeatureSet.class);
    }
}
