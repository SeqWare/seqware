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
import com.github.seqware.queryengine.model.QueryInterface;
import com.github.seqware.queryengine.model.QueryInterface.Location;
import java.util.HashSet;
import java.util.Set;

/**
 * Retrieves features based on their chromosomal location.
 *
 * @author jbaran
 */
public class InMemoryFeaturesByRangePlugin extends AbstractMRInMemoryPlugin {

    private QueryInterface.Location location;
    private String structure;
    private long start;
    private long stop;
    private Set<Feature> accumulator = new HashSet<Feature>();

    @Override
    public ReturnValue init(FeatureSet inputSet, Object... parameters) {
        this.inputSet = inputSet;

        assert (parameters.length == 4);

        this.location = (QueryInterface.Location) parameters[0];
        this.structure = (String) parameters[1];
        this.start = (Long) parameters[2];
        this.stop = (Long) parameters[3];
        return new ReturnValue();
    }

    @Override
    public ReturnValue mapInit() {
        /** do nothing */
        return null;
    }

    @Override
    public ReturnValue map(Feature atom, FeatureSet mappedSet) {
        boolean match = matchRange(atom, location, structure, start, stop);

        if (match) {
            Feature build = atom.toBuilder().build();
            accumulator.add(build);
        }

        return new ReturnValue();
    }

    public static boolean matchRange(Feature atom, Location location, String structure, long start, long stop) throws UnsupportedOperationException {
        boolean match = false;
        switch (location) {
            case OVERLAPS:
                match = atom.getSeqid().equals(structure) &&
                        (atom.getStart() >= start && atom.getStart() <= stop || atom.getStop() >= start && atom.getStop() <= stop);
                break;
            case EXCLUDES:
                match = !atom.getSeqid().equals(structure) ||
                        (atom.getSeqid().equals(structure) && atom.getStart() <= start && atom.getStop() >= stop);
                break;
            case INCLUDES:
                match = atom.getSeqid().equals(structure) && atom.getStart() >= start && atom.getStop() <= stop;
                break;
            case EXACT:
                match = atom.getSeqid().equals(structure) && atom.getStart() == start && atom.getStop() == stop;
                break;
            default:
                throw new UnsupportedOperationException("This range restriction on chromosomal locations has not been implemented yet.");
        }
        return match;
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
    public boolean isComplete() {
        return true;
    }
}
