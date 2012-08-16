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
import com.github.seqware.queryengine.kernel.RPNStack.FeatureAttribute;
import com.github.seqware.queryengine.kernel.RPNStack.TagOccurrence;
import com.github.seqware.queryengine.kernel.RPNStack.TagHierarchicalOccurrence;
import com.github.seqware.queryengine.kernel.RPNStack.TagValuePresence;
import com.github.seqware.queryengine.kernel.RPNStack.Parameter;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.plugins.inmemory.AbstractMRInMemoryPlugin;
import com.github.seqware.queryengine.util.SeqWareIterable;

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
        for (Parameter parameter : rpnStack.getParameters()) {
            if (parameter instanceof FeatureAttribute)
                rpnStack.setParameter(parameter, feature.getAttribute(parameter.getName()));
            else if (parameter instanceof TagOccurrence)
                rpnStack.setParameter(parameter, feature.getTagByKey(parameter.getName()) != null);
            else if (parameter instanceof TagHierarchicalOccurrence) {
                boolean foundTag = false;
                SeqWareIterable<Tag> tags = feature.getTags();
                for (Tag tag : tags)
                    if (tag.isDescendantOf(parameter.getName())) {
                        foundTag = true;
                        break;
                    }
                rpnStack.setParameter(parameter, foundTag);
            } else if (parameter instanceof TagValuePresence) {
                Tag tag = feature.getTagByKey(parameter.getName());
                rpnStack.setParameter(parameter,
                                      tag != null &&
                                      (tag.getValue() == null && ((TagValuePresence) parameter).getValue() == null ||
                                       tag.getValue() != null && tag.getValue().equals(((TagValuePresence) parameter).getValue())));
            } else
                throw new UnsupportedOperationException("This plugin can only handle FeatureAttribute parameters.");
        }

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
