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
package com.github.seqware.model.impl.inMemory;

import com.github.seqware.model.AnalysisPluginInterface;
import com.github.seqware.model.Feature;
import com.github.seqware.model.FeatureSet;
import com.github.seqware.model.Reference;
import java.util.*;

/**
 *
 * @author dyuen
 */
public class InMemoryFeaturesByTypePlugin implements AnalysisPluginInterface {

    private FeatureSet set;
    private String type;
    private Set<Feature> accumulator = new HashSet<Feature>();

    public ReturnValue init(FeatureSet set, Object ... parameters) {
        this.set = set;
        this.type = (String)parameters[0];
        return new ReturnValue();
    }

    public ReturnValue test() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ReturnValue verifyParameters() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ReturnValue verifyInput() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ReturnValue filterInit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ReturnValue filter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ReturnValue mapInit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ReturnValue map() {
        for (Feature f : set) {
            if (f.getType().equals(type)) {
                accumulator.add(f);
            }
        }
        return new ReturnValue();
    }

    public ReturnValue reduceInit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ReturnValue reduce() {
        // doesn't really do anything
        return new ReturnValue();
    }

    public ReturnValue verifyOutput() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ReturnValue cleanup() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public FeatureSet getFinalResult() {
        InMemoryFeatureSet fSet = new InMemoryFeatureSet(new Reference(){
            @Override
            public Iterator<FeatureSet> featureSets() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
            
        });
        
        fSet.add(accumulator);
        return fSet;
    }
}
