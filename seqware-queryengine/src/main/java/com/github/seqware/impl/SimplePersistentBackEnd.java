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
import com.github.seqware.model.impl.inMemory.*;
import com.github.seqware.util.InMemoryIterable;
import com.github.seqware.util.SeqWareIterable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * A toy backend implementation that writes all stores objects to disk using
 * Java persistence.
 *
 * @author dyuen
 */
public class SimplePersistentBackEnd implements BackEndInterface, FeatureStoreInterface, QueryInterface {

    private List<AnalysisPluginInterface> apis = new ArrayList<AnalysisPluginInterface>();
    private FileSerializationInterface fsi;

    public SimplePersistentBackEnd(FileSerializationInterface fsi) {
        this.fsi = fsi;
        apis.add(new InMemoryFeaturesAllPlugin());
        apis.add(new InMemoryFeaturesByReferencePlugin());
        apis.add(new InMemoryFeaturesByTypePlugin());
    }

    @Override
    public void store(Particle obj) {
        if (fsi.deserializeTargetToParticle(obj.getUUID()) == null) {
            fsi.serializeParticleToTarget(obj);
        }
//        if (!listOfEverything.contains(obj.getUUID())) {
//            fsi.serializeParticleToTarget(obj);
//            listOfEverything.add(obj.getUUID());
//            versionsOfEverything.put(obj.getUUID(), null);
//        }
    }

    @Override
    public void update(Atom obj) {
        // create a copy of the new particle and store it
        UUID oldUUID = obj.getUUID();
        Atom newParticle = (Atom)obj.copy(true);
        store(newParticle);
        // update the backend
        fsi.serializeParticleToTarget(newParticle);
//        listOfEverything.add(newParticle.getUUID());
//        if (obj instanceof Molecule) {
//            versionsOfEverything.put(newParticle.getUUID(), obj.getUUID());
//        }
        // change the obj we have a reference to look like the new object that was created
        obj.impersonate(newParticle.getUUID(), newParticle.getCreationTimeStamp(), oldUUID);
    }

    @Override
    public Particle refresh(Particle obj) {
        return obj;
    }

//    @Override
//    public void delete(Particle obj) throws AccessControlException {
//        listOfEverything.remove(obj);
//    }
    @Override
    public String getVersion() {
        return "In-memory back-end 0.1";
    }

    @Override
    public Particle getParticleByUUID(UUID uuid) {
        Particle p = fsi.deserializeTargetToParticle(uuid);
        assert(p == null || p.getUUID().equals(uuid));
        return p;
//        for (UUID u : listOfEverything) {
//            Particle p = fsi.deserializeTargetToParticle(u);
//            if (p.getUUID().equals(uuid)) {
//                return p;
//            }
//        }
//        return null;
    }

    @Override
    public SeqWareIterable<User> getUsers() {
        return getAllOfClass(User.class);
    }

    @Override
    public SeqWareIterable<Group> getGroups() {
        return getAllOfClass(Group.class);
    }

    @Override
    public SeqWareIterable<ReferenceSet> getReferenceSets() {
        return getAllOfClass(ReferenceSet.class);
    }

    @Override
    public SeqWareIterable<FeatureSet> getFeatureSets() {
        return getAllOfClass(FeatureSet.class);
    }

    @Override
    public SeqWareIterable<TagSet> getTagSets() {
        return getAllOfClass(TagSet.class);
    }

    @Override
    public SeqWareIterable<Tag> getTags() {
        return getAllOfClass(Tag.class);
    }

    @Override
    public SeqWareIterable<AnalysisSet> getAnalysisSets() {
        return getAllOfClass(AnalysisSet.class);
    }

    @Override
    public SeqWareIterable<AnalysisPluginInterface> getAnalysisPlugins() {
        return new InMemoryIterable(this.apis);
    }

    @Override
    public QueryFuture getFeaturesByType(FeatureSet set, String type, int hours) {
        AnalysisPluginInterface plugin = new InMemoryFeaturesByTypePlugin();
        plugin.init(set, type);
        return InMemoryQueryFutureImpl.newBuilder().setPlugin(plugin).build();
    }

    @Override
    public QueryFuture getFeatures(FeatureSet set, int hours) {
        AnalysisPluginInterface plugin = new InMemoryFeaturesAllPlugin();
        plugin.init(set);
        return InMemoryQueryFutureImpl.newBuilder().setPlugin(plugin).build();
    }

    @Override
    public QueryFuture getFeaturesByReference(FeatureSet set, Reference reference, int hours) {
        AnalysisPluginInterface plugin = new InMemoryFeaturesByReferencePlugin();
        plugin.init(set);
        return InMemoryQueryFutureImpl.newBuilder().setPlugin(plugin).build();
    }

    @Override
    public QueryFuture getFeaturesByRange(FeatureSet set, Location location, long start, long stop, int hours) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public QueryFuture getFeaturesByTag(FeatureSet set, int hours, String subject, String predicate, String object) {
        AnalysisPluginInterface plugin = new InMemoryFeaturesByTagPlugin();
        plugin.init(set, subject, predicate, object);
        return InMemoryQueryFutureImpl.newBuilder().setPlugin(plugin).build();
    }

    private SeqWareIterable getAllOfClass(Class aClass) {
        List list = new ArrayList();
        for (UUID u : fsi.getAllParticles()){ //listOfEverything) {
            Particle p = fsi.deserializeTargetToParticle(u);
            if (aClass.isInstance(p)) {
                list.add(p);
            }
        }
        return new InMemoryIterable(list);
    }

    @Override
    public Atom getPrecedingVersion(Atom obj) {
        Atom target = (Atom)fsi.deserializeTargetToParticle(obj.getUUID());
        if (target == null){
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "{0} had no parent, this may signal an error", obj.getUUID());
            return null;
        }
        return target.getPrecedingVersion();
        //return this.getParticleByUUID(this.versionsOfEverything.get(obj.getUUID()));
    }

    @Override
    public void setPrecedingVersion(Atom predecessor) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
