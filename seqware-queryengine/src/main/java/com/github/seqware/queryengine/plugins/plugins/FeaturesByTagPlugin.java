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

import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.Tag;
import java.util.Arrays;
import org.apache.commons.lang.ArrayUtils;

/**
 * <p>FeaturesByTagPlugin class.</p>
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class FeaturesByTagPlugin extends FeaturesByFilterPlugin {

    /** {@inheritDoc} */
    @Override
    protected FeatureFilter getFilter() {
        return new FeaturesByTagPlugin.FeaturesByTagFilter();
    }
    
    public static class FeaturesByTagFilter implements FeatureFilter {

        @Override
        public boolean featurePasses(Feature f, Object... parameters) {
            boolean b[] = new boolean[3];
            String subject = (String) parameters[0];
            String predicate = (String) parameters[1];
            String object = (String) parameters[2];

            Arrays.fill(b, false);
            for (Tag t : f.getTags()) {
                // three cases
                if (subject == null || subject.equals(t.getKey())) {
                    b[0] = true;
                }
                if (predicate == null || predicate.equals(t.getPredicate())) {
                    b[1] = true;
                }
                if (object == null || object.equals(t.getValue())) {
                    b[2] = true;
                }

            }
            boolean result = !ArrayUtils.contains(b, false);
            return result;
        }
    }
}
