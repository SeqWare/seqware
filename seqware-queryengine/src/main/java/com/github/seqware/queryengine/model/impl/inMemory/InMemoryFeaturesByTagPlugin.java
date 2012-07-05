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

import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.model.AnalysisPluginInterface;
import com.github.seqware.queryengine.factory.Factory;
import com.github.seqware.queryengine.factory.ModelManager;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author dyuen
 */
public class InMemoryFeaturesByTagPlugin implements AnalysisPluginInterface {

    private FeatureSet set;
    private String subject = null;
    private String predicate = null;
    private String object = null;
    private Set<Feature> accumulator = new HashSet<Feature>();

    @Override
    public ReturnValue init(FeatureSet set, Object... parameters) {
        this.set = set;
        assert (parameters.length == 3);
        this.subject = (String) parameters[0];
        this.predicate = (String) parameters[1];
        this.object = (String) parameters[2];
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
    public ReturnValue map() {
        for (Feature f : set) {
            boolean b[] = new boolean[3];
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
            if (!ArrayUtils.contains(b, false)) {
                accumulator.add(f);
            }
        }
        return new ReturnValue();
    }

    @Override
    public ReturnValue reduceInit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue reduce() {
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
       ModelManager mManager = Factory.getModelManager();
        FeatureSet fSet = mManager.buildFeatureSet().setReference(mManager.buildReference().setName("").build()).build();
        fSet.add(accumulator);
        mManager.close();
        return fSet;
    }
}
