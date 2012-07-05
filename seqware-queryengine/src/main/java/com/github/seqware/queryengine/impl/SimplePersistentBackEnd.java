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
package com.github.seqware.queryengine.impl;

import com.github.seqware.queryengine.factory.BackEndInterface;
import com.github.seqware.queryengine.model.*;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.impl.inMemory.*;
import com.github.seqware.queryengine.util.InMemoryIterable;
import com.github.seqware.queryengine.util.SGID;
import com.github.seqware.queryengine.util.SeqWareIterable;
import java.util.ArrayList;
import java.util.List;
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
    private StorageInterface fsi;

    public SimplePersistentBackEnd(StorageInterface fsi) {
        this.fsi = fsi;
        apis.add(new InMemoryFeaturesAllPlugin());
        apis.add(new InMemoryFeaturesByReferencePlugin());
        apis.add(new InMemoryFeaturesByTypePlugin());
    }
    
    @Override
    public void store(Atom ... objArr) {
//        Atom[] storeAtom = new Atom[objArr.length];
//        for(int i = 0; i < objArr.length; i++){
//           storeAtom[i] = (Atom)objArr[i].copy(false);
//        }
        fsi.serializeAtomsToTarget(objArr);
    }

    @Override
    public void update(Atom ... objList) {
        Atom[] storeAtom = new Atom[objList.length];
//        SGID[] oldSGID = new SGID[objList.length];
        for(int i = 0; i < objList.length; i++){
           Atom obj = objList[i];
//           oldSGID[i] = obj.getSGID();
           storeAtom[i] = (Atom)obj.copy(false);
           // need to set preceding ID for new copy
           storeAtom[i].setPrecedingVersion(obj);
        }
        store(storeAtom);
        // change the obj we have a reference to look like the new object that was created
        // doesn't seem applicable anymore?
        for(int i = 0; i < objList.length; i++){
          Atom obj = objList[i];
          ((AtomImpl)obj).impersonate(storeAtom[i].getSGID(), obj.getTimestamp(), obj.getSGID());
        }     
    }

    @Override
    public Atom refresh(Atom obj) {
        return obj;
    }

//    @Override
//    public void delete(Atom obj) throws AccessControlException {
//        listOfEverything.remove(obj);
//    }
    @Override
    public String getVersion() {
        return "In-memory back-end 0.1";
    }

    @Override
    public Atom getAtomBySGID(SGID sgid) {
        Atom p = fsi.deserializeTargetToAtom(sgid);
        assert(p == null || p.getSGID().equals(sgid));
        return p;
    }
    
    @Override
    public <T extends Atom> List getAtomsBySGID(Class<T> t, SGID... sgid){
        return fsi.deserializeTargetToAtoms(t, sgid);
    }
    
    @Override
    public <T extends Atom> T getAtomBySGID(Class<T> t, SGID sgid) {
        T p = fsi.deserializeTargetToAtom(t, sgid);
        assert(p == null || p.getSGID().equals(sgid));
        return p;
    }
    
    @Override
    public Atom getLatestAtomBySGID(SGID sgid) {
        Atom p = fsi.deserializeTargetToLatestAtom(sgid);
        assert(p == null || p.getSGID().getChainID().equals(sgid.getChainID()));
        return p;
    }

    @Override
    public <T extends Atom> T getLatestAtomBySGID(SGID sgid, Class<T> t) {
        T p = fsi.deserializeTargetToLatestAtom(sgid, t);
        assert(p == null || p.getSGID().getChainID().equals(sgid.getChainID()));
        return p;
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
        for (SGID u : fsi.getAllAtoms()){ //listOfEverything) {
            Atom p = fsi.deserializeTargetToLatestAtom(u);
            if (aClass.isInstance(p)) {
                list.add(p);
            }
        }
        return new InMemoryIterable(list);
    }

    @Override
    public Atom getPrecedingVersion(Atom obj) {
        Atom target = (Atom)fsi.deserializeTargetToAtom(((AtomImpl)obj).getHBaseClass(), obj.getSGID());
        if (target == null){
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "{0} had no parent, this may signal an error", obj.getSGID());
            return null;
        }
        return (Atom)target.getPrecedingVersion();
        //return this.getAtomBySGID(this.versionsOfEverything.get(obj.getSGID()));
    }

    @Override
    public void setPrecedingVersion(Atom predecessor) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
