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
package com.github.seqware.impl;

import com.github.seqware.factory.Factory;
import com.github.seqware.factory.ModelManager;
import com.github.seqware.model.*;
import com.github.seqware.model.impl.inMemory.*;
import java.util.Map.Entry;
import java.util.*;

/**
 * A Simple implementation of the ModelManager interface. We can make this more
 * efficient later.
 *
 * The current idea is that we try to minimize the interaction with the user by
 * using Hibernate/JPA-like semantics
 *
 * @author dyuen
 */
public class SimpleModelManager implements ModelManager {

    private Map<Particle, Boolean> dirtySet = new HashMap<Particle, Boolean>();

    @Override
    public void persist(Particle p) {
        this.dirtySet.put(p, true);
    }

    @Override
    public void clear() {
        dirtySet.clear();
    }

    @Override
    public void close() {
        this.flush();
        this.clear();
    }

    @Override
    public void flush() {
        // update dirty objects
        for (Entry<Particle, Boolean> p : dirtySet.entrySet()) {
            if (p.getValue()){
                if (p.getKey() instanceof Versionable && ((Versionable)p.getKey()).getPrecedingVersion() != null){
                    // if they have preceding versions do an update, otherwise store
                    Factory.getBackEnd().update(p.getKey());
                } else {
                    Factory.getBackEnd().store(p.getKey());
                }
            }
            p.setValue(false);
        }
        
    }

    @Override
    public FeatureSet.Builder buildFeatureSet() {
        FeatureSet.Builder fSet = null;
        if (Factory.BACKEND.equals(Factory.Backend_Type.IN_MEMORY)) {
            fSet = InMemoryFeatureSet.newBuilder().setManager(this);
        }
        return fSet;
    }

    @Override
    public Reference.Builder buildReference() {
        Reference.Builder ref = null;
        if (Factory.BACKEND.equals(Factory.Backend_Type.IN_MEMORY)) {
            ref = InMemoryReference.newBuilder().setManager(this);
        }
        assert (ref != null);
        return ref;
    }

    @Override
    public ReferenceSet.Builder buildReferenceSet() {
        ReferenceSet.Builder rSet = null;
        if (Factory.BACKEND.equals(Factory.Backend_Type.IN_MEMORY)) {
            rSet = InMemoryReferenceSet.newBuilder().setManager(this);
        }
        assert (rSet != null);
        return rSet;
    }

    @Override
    public TagSet.Builder buildTagSet() {
        TagSet.Builder tSet = null;
        if (Factory.BACKEND.equals(Factory.Backend_Type.IN_MEMORY)) {
            tSet = InMemoryTagSet.newBuilder().setManager(this);
        }
        assert (tSet != null);
        return tSet;
    }

    @Override
    public AnalysisSet.Builder buildAnalysisSet() {
        AnalysisSet.Builder aSet = null;
        if (Factory.BACKEND.equals(Factory.Backend_Type.IN_MEMORY)) {
            return InMemoryAnalysisSet.newBuilder().setManager(this);
        }
        assert (aSet != null);
        return aSet;
    }

    @Override
    public User.Builder buildUser() {
        User.Builder aSet = null;
        if (Factory.BACKEND.equals(Factory.Backend_Type.IN_MEMORY)) {
            return User.newBuilder().setManager(this);
        }
        assert (aSet != null);
        return aSet;
    }

    @Override
    public Group.Builder buildGroup() {
        Group.Builder aSet = null;
        if (Factory.BACKEND.equals(Factory.Backend_Type.IN_MEMORY)) {
            return Group.newBuilder().setManager(this);
        }
        assert (aSet != null);
        return aSet;
    }

    @Override
    public Tag.Builder buildTag() {
        Tag.Builder aSet = null;
        if (Factory.BACKEND.equals(Factory.Backend_Type.IN_MEMORY)) {
            return Tag.newBuilder().setManager(this);
        }
        assert (aSet != null);
        return aSet;
    }

    @Override
    public Feature.Builder buildFeature() {
        Feature.Builder aSet = null;
        if (Factory.BACKEND.equals(Factory.Backend_Type.IN_MEMORY)) {
            return Feature.newBuilder().setManager(this);
        }
        assert (aSet != null);
        return aSet;
    }
  
    
    @Override
    public void objectCreated(Particle source, boolean newObject) {
        this.dirtySet.put(source, true);
    }
}
