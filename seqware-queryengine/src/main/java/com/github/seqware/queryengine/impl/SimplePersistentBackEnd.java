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

import com.github.seqware.queryengine.plugins.inmemory.InMemoryFeaturesByTagPlugin;
import com.github.seqware.queryengine.plugins.inmemory.InMemoryFeaturesByRangePlugin;
import com.github.seqware.queryengine.plugins.inmemory.InMemoryFeaturesByReferencePlugin;
import com.github.seqware.queryengine.plugins.inmemory.InMemoryFeaturesAllPlugin;
import com.github.seqware.queryengine.kernel.RPNStack;
import com.github.seqware.queryengine.model.*;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.impl.FeatureList;
import com.github.seqware.queryengine.model.impl.inMemory.*;
import com.github.seqware.queryengine.plugins.lazyinmemory.LazyFeaturesByAttributesPlugin;
import com.github.seqware.queryengine.plugins.AnalysisPluginInterface;
import com.github.seqware.queryengine.util.FSGID;
import com.github.seqware.queryengine.util.InMemoryIterable;
import com.github.seqware.queryengine.util.SGID;
import com.github.seqware.queryengine.util.SeqWareIterable;
import java.util.*;

/**
 *
 * A toy backend implementation that writes all stores objects to disk using
 * Java persistence.
 *
 * @author dyuen
 * @author jbaran
 */
public class SimplePersistentBackEnd implements BackEndInterface, QueryInterface {

    private List<AnalysisPluginInterface> apis = new ArrayList<AnalysisPluginInterface>();
    private StorageInterface fsi;

    public SimplePersistentBackEnd(StorageInterface fsi) {
        this.fsi = fsi;
        apis.add(new InMemoryFeaturesAllPlugin());
        apis.add(new InMemoryFeaturesByReferencePlugin());
        apis.add(new LazyFeaturesByAttributesPlugin());
    }

    @Override
    public void store(Atom... objArr) {
        fsi.serializeAtomsToTarget(objArr);
    }

    @Override
    public void update(Atom... objList) {
        Atom[] storeAtom = new Atom[objList.length];
        for (int i = 0; i < objList.length; i++) {
            Atom obj = objList[i];
            storeAtom[i] = (Atom) obj.copy(false);
            // need to set preceding ID for new copy
            assert (!storeAtom[i].getSGID().equals(obj.getSGID()));
            ((AtomImpl) storeAtom[i]).setPrecedingSGID(obj.getSGID());
        }
        store(storeAtom);
        // change the obj we are working with to look like the new object that was created
        for (int i = 0; i < objList.length; i++) {
            Atom obj = objList[i];
            ((AtomImpl) obj).impersonate(storeAtom[i].getSGID(), obj.getSGID());
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
        p = locateWithinFeatureList(p, sgid);
        assert (p == null || p.getSGID().equals(sgid));
        return p;
    }

    @Override
    public <T extends Atom> List getAtomsBySGID(Class<T> t, SGID... sgid) {
        return fsi.deserializeTargetToAtoms(t, sgid);
    }

    @Override
    public <T extends Atom> T getAtomBySGID(Class<T> t, SGID sgid) {
        T p = fsi.deserializeTargetToAtom(t, sgid);
        p = (T) locateWithinFeatureList(p, sgid);
        assert (p == null || p.getSGID().equals(sgid));
        return p;
    }

    @Override
    public Atom getLatestAtomBySGID(SGID sgid) {
        Atom p = fsi.deserializeTargetToLatestAtom(sgid);
        p = locateWithinFeatureList(p, sgid);
        assert (p == null || p.getSGID().getRowKey().equals(sgid.getRowKey()));
        return p;
    }

    @Override
    public <T extends Atom> T getLatestAtomBySGID(SGID sgid, Class<T> t) {
        T p = fsi.deserializeTargetToLatestAtom(sgid, t);
        assert (p == null || p.getSGID().getRowKey().equals(sgid.getRowKey()));
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
    public SeqWareIterable<Reference> getReferences() {
        return getAllOfClass(Reference.class);
    }

    @Override
    public SeqWareIterable<FeatureSet> getFeatureSets() {
        return getAllOfClass(FeatureSet.class);
    }

    @Override
    public SeqWareIterable<TagSpecSet> getTagSpecSets() {
        return getAllOfClass(TagSpecSet.class);
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
    public QueryFuture getFeaturesByAttributes(int hours, FeatureSet set, RPNStack constraints) {
        AnalysisPluginInterface plugin = new LazyFeaturesByAttributesPlugin();
        plugin.init(set, constraints);
        return InMemoryQueryFutureImpl.newBuilder().setPlugin(plugin).build();
    }

    @Override
    public QueryFuture getFeatures(int hours, FeatureSet set) {
        AnalysisPluginInterface plugin = new InMemoryFeaturesAllPlugin();
        plugin.init(set);
        return InMemoryQueryFutureImpl.newBuilder().setPlugin(plugin).build();
    }

    @Override
    public QueryFuture getFeaturesByReference(int hours, FeatureSet set, Reference reference) {
        AnalysisPluginInterface plugin = new InMemoryFeaturesByReferencePlugin();
        plugin.init(set);
        return InMemoryQueryFutureImpl.newBuilder().setPlugin(plugin).build();
    }

    @Override
    public QueryFuture getFeaturesByRange(int hours, FeatureSet set, Location location, String structure, long start, long stop) {
        AnalysisPluginInterface plugin = new InMemoryFeaturesByRangePlugin();
        plugin.init(set, location, structure, start, stop);
        return InMemoryQueryFutureImpl.newBuilder().setPlugin(plugin).build();
    }

    @Override
    public QueryFuture getFeaturesByTag(int hours, FeatureSet set, String subject, String predicate, String object) {
        AnalysisPluginInterface plugin = new InMemoryFeaturesByTagPlugin();
        plugin.init(set, subject, predicate, object);
        return InMemoryQueryFutureImpl.newBuilder().setPlugin(plugin).build();
    }

    private SeqWareIterable getAllOfClass(Class aClass) {
        List list = new ArrayList();
        for (SGID u : fsi.getAllAtoms()) { //listOfEverything) {
            Atom p = fsi.deserializeTargetToLatestAtom(u);
            if (aClass.isInstance(p)) {
                list.add(p);
            }
        }
        return new InMemoryIterable(list);
    }

    /**
     * If we actually got a FeatureList from the back-end, we need to dig deeper
     * @param p
     * @param sgid
     * @return 
     */
    private Atom locateWithinFeatureList(Atom p, SGID sgid) {
        // it would be better if we handle all List operations on the BackEnd layer
        // rather than the Storage layer, to avoid reimplementing it
        if (p instanceof FeatureList) {
            for (Feature f : ((FeatureList) p).getFeatures()) {
                if (f.getSGID().equals(sgid)) {
                    p = f;
                    break;
                }
            }
        }
        return p;
    }
    
    /**
     * Given a collection of FeatureLists all with the same row key
     * (and differing timestamps), create a consistent view of the features 
     * in the feature set corresponding effectively to the last timestamp
     * @param fLists
     * @return 
     */
    public static Collection<Feature> consolidateRow(List<FeatureList> fLists){
       String rowKey = null;
       long time = Long.MIN_VALUE;
        // sort by time ascending
        Collections.sort(fLists, new Comparator<FeatureList>(){
            @Override
            public int compare(FeatureList o1, FeatureList o2) {
                return o1.getSGID().getBackendTimestamp().compareTo(o2.getSGID().getBackendTimestamp());
            }
        });
        Map<String, Feature> map = new HashMap<String, Feature>();
        for(FeatureList list : fLists){
            if (rowKey == null){
                rowKey = list.getSGID().getRowKey();
                time = list.getTimestamp().getTime();
            }
            // might as well make sure that the rowkeys are identical 
            assert(list.getSGID().getRowKey().equals(rowKey));
            // make sure time is ascending
            assert(time <= list.getSGID().getBackendTimestamp().getTime());
            time = list.getSGID().getBackendTimestamp().getTime();
            
            for(Feature f : list.getFeatures()){
                FSGID fsgid = (FSGID) f.getSGID();
                boolean tomb = fsgid.isTombstone();
                String key = fsgid.getUuid().toString();
                if (!tomb){
                    map.put(key, f);
                } else{
                    map.remove(key);
                }
            }
        }
        return map.values();
    }
}
