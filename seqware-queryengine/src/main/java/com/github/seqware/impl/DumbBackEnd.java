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

import com.github.seqware.factory.BackEndInterface;
import com.github.seqware.model.*;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 *
 * A toy backend implementation that stores everything in memory.
 *
 * @author dyuen
 */
public class DumbBackEnd implements BackEndInterface, FeatureStoreInterface, QueryInterface {

    private List<Object> listOfEverything = new ArrayList<Object>();

    public void store(Object obj) throws AccessControlException {
        if (!listOfEverything.contains(obj)) {
            listOfEverything.add(obj);
        }
    }

    public Object update(Object obj) throws AccessControlException {
        return obj;
    }

    public Object refresh(Object obj) throws AccessControlException {
        return obj;
    }

    public void delete(Object obj) throws AccessControlException {
        listOfEverything.remove(obj);
    }

    public String getVersion() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Particle getParticleByUUID(UUID uuid) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Iterator<User> getUsers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Iterator<Group> getGroups() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Iterator<ReferenceSet> getReferenceSets() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Iterator<FeatureSet> getFeatureSets() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Iterator<TagSet> getTagSets() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Iterator<Tag> getTags() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Iterator<AnalysisSet> getAnalysisSets() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Iterator<AnalysisPluginInterface> getAnalysisPlugins() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public QueryFuture getFeaturesByType(FeatureSet set, String type, int hours) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public QueryFuture getFeatures(FeatureSet set, int hours) {
        InMemoryFeatureSet fSet = new InMemoryFeatureSet(new Reference() {

            @Override
            public Iterator<FeatureSet> featureSets() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });

        for (Object obj : listOfEverything) {
            if (obj instanceof Feature) {
                fSet.add((Feature) obj);
            }
        }
        return new QueryFutureImpl(fSet);
    }

    public QueryFuture getFeaturesByReference(FeatureSet set, Reference reference, int hours) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public QueryFuture getFeaturesByRange(FeatureSet set, LOCATION location, long start, long stop, int hours) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public QueryFuture getFeaturesByTag(FeatureSet set, int hours, String... tag) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public class QueryFutureImpl implements QueryInterface.QueryFuture {

        private FeatureSet featureSet;

        public QueryFutureImpl(FeatureSet featureSet) {
            this.featureSet = featureSet;
        }

        public FeatureSet get() {
            return featureSet;
        }

        public boolean isDone() {
            return true;
        }
    }
}
