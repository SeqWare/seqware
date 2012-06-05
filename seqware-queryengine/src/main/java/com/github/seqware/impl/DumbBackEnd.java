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
    private Map<UUID, Set<UUID>> taggedWith = new HashMap<UUID, Set<UUID>>();
    private List<AnalysisPluginInterface> apis = new ArrayList<AnalysisPluginInterface>();

    public DumbBackEnd() {
        apis.add(new InMemoryFeaturesAllPlugin());
        apis.add(new InMemoryFeaturesByReferencePlugin());
        apis.add(new InMemoryFeaturesByTypePlugin());
    }

    @Override
    public void store(Particle obj) {
        if (!listOfEverything.contains(obj)) {
            // let's just clone everything on store to simulate hbase
            Particle storeObj = (Particle) SerializationUtils.clone(obj);
            listOfEverything.add(storeObj);
            versionsOfEverything.put(storeObj.getUUID(), null);
        }
    }

    @Override
    public void update(Particle obj) {
        // create new particle
        Particle newParticle = obj.copy(true);
        this.store(newParticle);
        versionsOfEverything.put(newParticle.getUUID(), obj.getUUID());
        // update this to point at the new particle as represented by a UUID and a timestamp
        obj.setUUID(newParticle.getUUID());
        obj.setTimestamp(newParticle.getCreationTimeStamp());
    }

    @Override
    public Particle refresh(Particle obj)  {
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
        for (Particle p : listOfEverything) {
            if (p.getUUID().equals(uuid)) {
                return p;
            }
        }
        return null;
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
        for (Particle p : listOfEverything) {
            if (aClass.isInstance(p)) {
                list.add(p);
            }
        }
        return new InMemoryIterable(list);
    }

    @Override
    public Particle getPrecedingVersion(Particle obj) {
        return this.getParticleByUUID(this.versionsOfEverything.get(obj.getUUID()));
    }

    @Override
    public void setPrecedingVersion(Particle predecessor) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void associateTag(Atom object, Tag tag) {
        if (!taggedWith.containsKey(object.getUUID())) {
            taggedWith.put(object.getUUID(), new HashSet<UUID>());
        }
        taggedWith.get(object.getUUID()).add(tag.getUUID());
    }

    @Override
    public void dissociateTag(Atom object, Tag tag) {
        if (taggedWith.containsKey(object.getUUID())) {
            Set<UUID> set = taggedWith.get(object.getUUID());
            set.remove(tag.getUUID());
        }

    }

    @Override
    public SeqWareIterable<Tag> getTags(Atom atom) throws AccessControlException {
        List<Tag> tags = new ArrayList<Tag>();
        Set<UUID> set = taggedWith.get(atom.getUUID());
        if (set == null) {
            return new InMemoryIterable(tags);
        }
        for (UUID uid : set) {
            tags.add((Tag) this.getParticleByUUID(uid));
        }
        return  new InMemoryIterable(tags);
    }

    @Override
    public long getVersion(Particle obj) throws AccessControlException {
        Particle parent = this.getPrecedingVersion(obj);
        if (parent != null) {
            return 1 + this.getVersion(parent);
        }
        return 1;
    }
}
