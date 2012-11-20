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
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.QueryInterface;
import com.github.seqware.queryengine.model.QueryInterface.Location;
import com.github.seqware.queryengine.plugins.MapperInterface;
import com.github.seqware.queryengine.plugins.ReducerInterface;
import java.util.Collection;

/**
 * Retrieves features based on their chromosomal location.
 *
 * @author jbaran
 * @version $Id: $Id
 */
public class FeaturesByRangePlugin extends FeaturesByFilterPlugin {

     /** {@inheritDoc} */
     @Override
    protected FeatureFilter getFilter() {
        return new FeaturesByRangePlugin.FeaturesByRangeFilter();
    }
    
    public static class FeaturesByRangeFilter implements FeatureFilter {

        @Override
        public boolean featurePasses(Feature f, Object... parameters) {
            boolean match = false;
            Location location = (QueryInterface.Location) parameters[0];
            String structure = (String) parameters[1];
            long start = (Long) parameters[2];
            long stop = (Long) parameters[3];
            switch (location) {
                case OVERLAPS:
                    match = f.getSeqid().equals(structure)
                            && (f.getStart() >= start && f.getStart() <= stop || f.getStop() >= start && f.getStop() <= stop);
                    break;
                case EXCLUDES:
                    match = !f.getSeqid().equals(structure)
                            || (f.getSeqid().equals(structure) && f.getStart() <= start && f.getStop() >= stop);
                    break;
                case INCLUDES:
                    match = f.getSeqid().equals(structure) && f.getStart() >= start && f.getStop() <= stop;
                    break;
                case EXACT:
                    match = f.getSeqid().equals(structure) && f.getStart() == start && f.getStop() == stop;
                    break;
                default:
                    throw new UnsupportedOperationException("This range restriction on chromosomal locations has not been implemented yet.");
            }
            return match;
        }
    }
}
