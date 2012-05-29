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
import com.github.seqware.model.impl.inMemory.InMemoryFeaturesAllPlugin;
import com.github.seqware.model.impl.inMemory.InMemoryFeaturesByReferencePlugin;
import com.github.seqware.model.impl.inMemory.InMemoryFeaturesByTypePlugin;
import java.security.AccessControlException;
import java.util.*;
import org.apache.commons.lang.SerializationUtils;

/**
 *
 * A toy backend implementation that stores everything in memory.
 *
 * @author dyuen
 */
public class DumbBackEnd implements BackEndInterface, FeatureStoreInterface, QueryInterface {

    private List<Particle> listOfEverything = new ArrayList<Particle>();
    private Map<UUID, UUID> versionsOfEverything = new HashMap<UUID, UUID>(); 

    public void store(Particle obj) throws AccessControlException {
        if (!listOfEverything.contains(obj)) {
            // let's just clone everything on store to simulate hbase
            Particle storeObj = (Particle)SerializationUtils.clone(obj);
            listOfEverything.add(storeObj);
            versionsOfEverything.put(storeObj.getUUID(), null);
        }
    }

    public Particle update(Particle obj) throws AccessControlException {
        // create new particle
        Particle newParticle = obj.copy(true);
        this.store(newParticle);
        versionsOfEverything.put(newParticle.getUUID(), obj.getUUID());
        return newParticle;
    }

    public Particle refresh(Particle obj) throws AccessControlException {
        return obj;
    }

    public void delete(Particle obj) throws AccessControlException {
        listOfEverything.remove(obj);
    }

    public String getVersion() {
        return "In-memory back-end 0.1";
    }

    public Particle getParticleByUUID(UUID uuid) {
        for(Particle p : listOfEverything){
            if (p.getUUID().equals(uuid)){
                return p;
            }
        }
        return null;
    }

    public Iterable<User> getUsers() {
        return getAllOfClass(User.class);
    }

    public Iterable<Group> getGroups() {
        return getAllOfClass(Group.class);
    }

    public Iterable<ReferenceSet> getReferenceSets() {
        return getAllOfClass(ReferenceSet.class);
    }

    public Iterable<FeatureSet> getFeatureSets() {
        return getAllOfClass(FeatureSet.class);
    }

    public Iterable<TagSet> getTagSets() {
        return getAllOfClass(TagSet.class);
    }

    public Iterable<Tag> getTags() {
        return getAllOfClass(Tag.class);
    }

    public Iterable<AnalysisSet> getAnalysisSets() {
        return getAllOfClass(AnalysisSet.class);
    }

    public Iterable<AnalysisPluginInterface> getAnalysisPlugins() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public QueryFuture getFeaturesByType(FeatureSet set, String type, int hours) {
//        InMemoryFeatureSet fSet = new InMemoryFeatureSet(set.getReference());
//        for (Feature f : set){
//            if (f.getType().equals(type)){
//                fSet.add(f);
//            }
//        }
        AnalysisPluginInterface plugin = new InMemoryFeaturesByTypePlugin();
        plugin.init(set, type);
        return new QueryFutureImpl(plugin);
    }

    public QueryFuture getFeatures(FeatureSet set, int hours) {
 //       InMemoryFeatureSet fSet = new InMemoryFeatureSet(set.getReference());

//        for (Object obj : set) {
//            if (obj instanceof Feature) {
//                fSet.add((Feature) obj);
//            }
//        }
        AnalysisPluginInterface plugin = new InMemoryFeaturesAllPlugin();
        plugin.init(set);
        return new QueryFutureImpl(plugin);
    }

    public QueryFuture getFeaturesByReference(FeatureSet set, Reference reference, int hours) {
//       InMemoryFeatureSet fSet = new InMemoryFeatureSet(set.getReference());
//        for (Object obj : set) {
//            if (obj instanceof Feature) {
//                fSet.add((Feature) obj);
//            }
//        }
        AnalysisPluginInterface plugin = new InMemoryFeaturesByReferencePlugin();
        plugin.init(set);
        return new QueryFutureImpl(plugin);
    }

    public QueryFuture getFeaturesByRange(FeatureSet set, Location location, long start, long stop, int hours) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public QueryFuture getFeaturesByTag(FeatureSet set, int hours, String... tag) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private Iterable getAllOfClass(Class aClass) {
        List list = new ArrayList();
        for(Particle p : listOfEverything){
            if (aClass.isInstance(p)){
                list.add(p);
            }
        }
        return list;
    }

    public Particle getPrecedingVersion(Particle obj) {
        return this.getParticleByUUID(this.versionsOfEverything.get(obj.getUUID()));
    }

    public void setPrecedingVersion(Particle predecessor) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public class QueryFutureImpl extends Analysis {

        public QueryFutureImpl(AnalysisPluginInterface plugin) {
            super(plugin);
        }

        public FeatureSet get() {
            getPlugin().map();
            getPlugin().reduce();
            return super.getPlugin().getFinalResult();
        }

        public boolean isDone() {
            return true;
        }

        @Override
        public Analysis getParentAnalysis() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Set<Analysis> getSuccessorAnalysisSet() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
