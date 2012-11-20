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
package com.github.seqware.queryengine.plugins.plugins;

import com.github.seqware.queryengine.kernel.RPNStack;
import com.github.seqware.queryengine.kernel.RPNStack.Constant;
import com.github.seqware.queryengine.kernel.RPNStack.FeatureAttribute;
import com.github.seqware.queryengine.kernel.RPNStack.Parameter;
import com.github.seqware.queryengine.kernel.RPNStack.TagHierarchicalOccurrence;
import com.github.seqware.queryengine.kernel.RPNStack.TagOccurrence;
import com.github.seqware.queryengine.kernel.RPNStack.TagValue;
import com.github.seqware.queryengine.kernel.RPNStack.TagValuePresence;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.model.TagSet;
import com.github.seqware.queryengine.util.SeqWareIterable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Generic query implementation over all attributes of a Feature (including
 * additional attributes).
 *
 * @author dyuen
 * @author jbaran
 * @version $Id: $Id
 */
public class FeaturesByAttributesPlugin extends FeaturesByFilterPlugin {

    /** {@inheritDoc} */
    @Override
    protected FeatureFilter getFilter() {
        return new FeaturesByAttributesFilter();
    }

    public static class FeaturesByAttributesFilter implements FeatureFilter {
        
        private List<TagSet> hierarchyConstraintSets = null;
        private Map<String, Tag> hierarchyCache = new HashMap<String, Tag>();

        @Override
        public boolean featurePasses(Feature feature, Object... parameters) {

            for (int i = 1; hierarchyConstraintSets == null && i < parameters.length; i++) {
                if (parameters[i] instanceof List) {
                    this.hierarchyConstraintSets = (List<TagSet>) parameters[i];
                }
            }

            RPNStack rpnStack = (RPNStack) parameters[0];
            manipulateRPNStack(rpnStack, feature, hierarchyConstraintSets, hierarchyCache);
            boolean result = (Boolean) rpnStack.evaluate();
            return result;
        }

        public static void manipulateRPNStack(RPNStack rpnStack, Feature feature, List<TagSet> hierarchyConstraintSets, Map<String, Tag> hierarchyCache) throws UnsupportedOperationException {
            // Get the parameters from the RPN stack and replace them with concrete values:
            for (Parameter parameter : rpnStack.getParameters()) {
                if (parameter instanceof FeatureAttribute) {
                    rpnStack.setParameter(parameter, feature.getAttribute(parameter.getName()));
                } else if (parameter instanceof TagValue) {
                    TagValue to = (TagValue)parameter;
                    Tag tagByKey = feature.getTagByKey(to.getTagSetRowKey(), parameter.getName());
                    rpnStack.setParameter(parameter, tagByKey == null ? null : tagByKey.getValue());
                } else if (parameter instanceof TagOccurrence) {
                    TagOccurrence to = (TagOccurrence)parameter;
                    rpnStack.setParameter(parameter, feature.getTagByKey(to.getTagSetRowKey(), parameter.getName()) != null);
                } else if (parameter instanceof TagHierarchicalOccurrence) {
                    boolean foundTag = false;
                    SeqWareIterable<Tag> tags = feature.getTags();
                    for (Tag tag : tags) {
                        // TODO For now, it cannot distinguish between various tag sets -- in either the feature tags and ontologies to search.
                        if (!tag.getKey().startsWith("SO:")) {
                            continue;
                        }

                        if (!hierarchyCache.containsKey(tag.getKey())) {
                            for (TagSet tagSet : hierarchyConstraintSets) {
                                Iterator<Tag> tagIterator = tagSet.iterator();
                                while (tagIterator.hasNext()) {
                                    Tag hTag = tagIterator.next();
                                    if (hTag.getKey().replaceFirst(":.* ", ":").equals(tag.getKey())) {
                                        hierarchyCache.put((String) tag.getKey(), hTag);
                                    }
                                }
                            }
                        }

                        // The following can be null, if the tag is from a tag set that we are not looking at:
                        Tag tagWithHierachy = hierarchyCache.get(tag.getKey());

                        if (tagWithHierachy != null && tagWithHierachy.isDescendantOf(parameter.getName())) {
                            foundTag = true;
                            break;
                        }
                    }
                    rpnStack.setParameter(parameter, foundTag);
                } else if (parameter instanceof TagValuePresence) {
                    TagValuePresence to = (TagValuePresence)parameter;
                    Tag tag = feature.getTagByKey(to.getTagSetRowKey(), parameter.getName());
                    
                    rpnStack.setParameter(parameter,
                            tag != null
                            && (tag.getValue() == null && to.getValue() == null
                            || tag.getValue() != null && tag.getValue().equals(to.getValue())));
                } else {
                    throw new UnsupportedOperationException("This plugin can only handle FeatureAttribute parameters.");
                }
            }
        }
    }
}
