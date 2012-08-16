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

import com.github.seqware.queryengine.kernel.RPNStack;
import com.github.seqware.queryengine.kernel.RPNStack.TagOccurrence;
import com.github.seqware.queryengine.kernel.RPNStack.TagValuePresence;
import com.github.seqware.queryengine.kernel.RPNStack.TagHierarchicalOccurrence;
import com.github.seqware.queryengine.kernel.RPNStack.FeatureAttribute;
import com.github.seqware.queryengine.kernel.RPNStack.Parameter;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.util.SeqWareIterable;

import java.util.Iterator;

/**
 * Generic query implementation over all attributes of a Feature (including
 * additional attributes).
 *
 * @author dyuen
 * @author jbaran
 */
public class InMemoryFeaturesByAttributesPlugin extends InMemoryFeaturesByFilterPlugin {

    @Override
    protected FeatureFilter getFilter() {
        return new FeaturesByAttributesFilter();
    }
    
    public static class FeaturesByAttributesFilter implements FeatureFilter {
        @Override
        public boolean featurePasses(Feature f, Object... parameters) {
            RPNStack rpnStack = (RPNStack) parameters[0];
            // Get the parameters from the RPN stack and replace them with concrete values:
            for (Parameter parameter : rpnStack.getParameters()) {
                if (parameter instanceof FeatureAttribute)
                    rpnStack.setParameter(parameter, f.getAttribute(parameter.getName()));
                else if (parameter instanceof TagOccurrence)
                    rpnStack.setParameter(parameter, f.getTagByKey(parameter.getName()) != null);
                else if (parameter instanceof TagHierarchicalOccurrence) {
                    boolean foundTag = false;
                    SeqWareIterable<Tag> tags = f.getTags();
                    for (Tag tag : tags)
                        if (tag.isDescendantOf(parameter.getName())) {
                            foundTag = true;
                            break;
                        }
                    rpnStack.setParameter(parameter, foundTag);
                } else if (parameter instanceof TagValuePresence) {
                    Tag tag = f.getTagByKey(parameter.getName());
                    rpnStack.setParameter(parameter,
                                          tag != null &&
                                          (tag.getValue() == null && ((TagValuePresence) parameter).getValue() == null ||
                                           tag.getValue() != null && tag.getValue().equals(((TagValuePresence) parameter).getValue())));
                } else
                    throw new UnsupportedOperationException("This plugin can only handle FeatureAttribute parameters.");
            }
            boolean result = (Boolean) rpnStack.evaluate();
            return result;
        }
    }
}
