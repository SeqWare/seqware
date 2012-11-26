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

import com.github.seqware.queryengine.backInterfaces.BackEndInterface;
import com.github.seqware.queryengine.backInterfaces.StorageInterface;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.kernel.RPNStack;
import com.github.seqware.queryengine.model.*;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.impl.FeatureList;
import com.github.seqware.queryengine.plugins.PluginInterface;
import com.github.seqware.queryengine.plugins.plugins.FeaturesAllPlugin;
import com.github.seqware.queryengine.plugins.plugins.FeaturesByAttributesPlugin;
import com.github.seqware.queryengine.plugins.plugins.FeaturesByRangePlugin;
import com.github.seqware.queryengine.plugins.plugins.FeaturesByTagPlugin;
import com.github.seqware.queryengine.util.FSGID;
import com.github.seqware.queryengine.util.InMemoryIterable;
import com.github.seqware.queryengine.util.SGID;
import com.github.seqware.queryengine.util.SeqWareIterable;
import java.util.*;
import org.apache.log4j.Logger;

/**
 *
 * A toy backend implementation that writes all stores objects to disk using
 * Java persistence.
 *
 * @author dyuen
 * @author jbaran
 * @version $Id: $Id
 */
public class SimplePersistentBackEnd implements BackEndInterface {

    private Map<Class, PluginInterface> pluginMap = new HashMap<Class, PluginInterface>();
    protected StorageInterface storage;

    /**
     * <p>Constructor for SimplePersistentBackEnd.</p>
     *
     * @param fsi a {@link com.github.seqware.queryengine.impl.StorageInterface} object.
     */
    public SimplePersistentBackEnd(StorageInterface fsi) {
        this.storage = fsi;
        pluginMap.put(FeaturesAllPlugin.class, new FeaturesAllPlugin());
        pluginMap.put(FeaturesByAttributesPlugin.class, new FeaturesByAttributesPlugin());
    }

    /** {@inheritDoc} */
    @Override
    public void store(Atom... objArr) {
        storage.serializeAtomsToTarget(objArr);
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public Atom refresh(Atom obj) {
        return obj;
    }

//    @Override
//    public void delete(Atom obj) throws AccessControlException {
//        listOfEverything.remove(obj);
//    }
    /** {@inheritDoc} */
    @Override
    public String getVersion() {
        return "In-memory back-end 0.1";
    }

    /** {@inheritDoc} */
    @Override
    public Atom getAtomBySGID(SGID sgid) {
        Atom p = storage.deserializeTargetToAtom(sgid);
        p = locateWithinFeatureList(p, sgid);
        assert (p == null || p.getSGID().equals(sgid));
        return p;
    }

    /** {@inheritDoc} */
    @Override
    public <T extends Atom> List getAtomsBySGID(Class<T> t, SGID... sgid) {
        return storage.deserializeTargetToAtoms(t, sgid);
    }

    /** {@inheritDoc} */
    @Override
    public <T extends Atom> T getAtomBySGID(Class<T> t, SGID sgid) {
        T p = storage.deserializeTargetToAtom(t, sgid);
        p = (T) locateWithinFeatureList(p, sgid);
        assert (p == null || p.getSGID().equals(sgid) || p.getSGID().getFriendlyRowKey().equals(sgid.getFriendlyRowKey()));
        return p;
    }

    /** {@inheritDoc} */
    @Override
    public Atom getLatestAtomBySGID(SGID sgid) {
        Atom p = storage.deserializeTargetToLatestAtom(sgid);
        p = locateWithinFeatureList(p, sgid);
        assert (p == null || p.getSGID().getRowKey().equals(sgid.getRowKey())) || p.getSGID().getFriendlyRowKey().equals(sgid.getFriendlyRowKey());
        return p;
    }
    
    /** {@inheritDoc} */
    @Override
    public <T extends Atom> T getLatestAtomByRowKey(String rowKey, Class<T> t) {
        SGID sgid = new SGID(rowKey);
        return this.getLatestAtomBySGID(sgid, t);
    }

    /** {@inheritDoc} */
    @Override
    public <T extends Atom> T getLatestAtomBySGID(SGID sgid, Class<T> t) {
        T p = storage.deserializeTargetToLatestAtom(sgid, t);
        p = (T) locateWithinFeatureList(p, sgid);
        assert (p == null || p.getSGID().getRowKey().equals(sgid.getRowKey()) || p.getSGID().getFriendlyRowKey().equals(sgid.getFriendlyRowKey()));
        return p;
    }

    /** {@inheritDoc} */
    @Override
    public SeqWareIterable<User> getUsers() {
        return getAllOfClass(User.class);
    }
    
    /** {@inheritDoc} */
    @Override
    public SeqWareIterable<PluginRun> getPluginRuns() {
        return getAllOfClass(PluginRun.class);
    }

    /** {@inheritDoc} */
    @Override
    public SeqWareIterable<Group> getGroups() {
        return getAllOfClass(Group.class);
    }

    /** {@inheritDoc} */
    @Override
    public SeqWareIterable<ReferenceSet> getReferenceSets() {
        return getAllOfClass(ReferenceSet.class);
    }

    /** {@inheritDoc} */
    @Override
    public SeqWareIterable<Reference> getReferences() {
        return getAllOfClass(Reference.class);
    }

    /** {@inheritDoc} */
    @Override
    public SeqWareIterable<FeatureSet> getFeatureSets() {
        return getAllOfClass(FeatureSet.class);
    }

    /** {@inheritDoc} */
    @Override
    public SeqWareIterable<TagSet> getTagSets() {
        return getAllOfClass(TagSet.class);
    }

    /** {@inheritDoc} */
    @Override
    public SeqWareIterable<Tag> getTags() {
        return getAllOfClass(Tag.class);
    }

    /** {@inheritDoc} */
    @Override
    public SeqWareIterable<Plugin> getPlugins() {
        return getAllOfClass(Plugin.class);
    }

    /** {@inheritDoc} */
    @Override
    public SeqWareIterable<PluginInterface> getPluginInterfaces() {
        return new InMemoryIterable(this.pluginMap.values());
    }

    /** {@inheritDoc} */
    @Override
    public QueryFuture getFeaturesByAttributes(int hours, FeatureSet set, RPNStack constraints) {
        PluginInterface plugin = new FeaturesByAttributesPlugin();
        List<TagSet> tagSets = new LinkedList<TagSet>();
        // If there are hierarchical occurrences to be checked, retrieve the tag set now, so that paths in
        // trees can be resolved later on.
        for (RPNStack.Parameter parameter : constraints.getParameters()) {
            if (parameter instanceof RPNStack.TagHierarchicalOccurrence) {
                tagSets.add(SWQEFactory.getQueryInterface().getLatestAtomByRowKey(((RPNStack.TagHierarchicalOccurrence) parameter).getTagSetRowKey(), TagSet.class));
            }
        }
        plugin.init(set, constraints, tagSets);
        return PluginRun.newBuilder().setPluginRunner(SWQEFactory.getPluginRunner(plugin, set, constraints, tagSets)).build();
    }

    /** {@inheritDoc} */
    @Override
    public QueryFuture getFeatures(int hours, FeatureSet set) {
        PluginInterface plugin = new FeaturesAllPlugin();
        plugin.init(set);
        return PluginRun.newBuilder().setPluginRunner(SWQEFactory.getPluginRunner(plugin, set)).build();
    }

    /** {@inheritDoc} */
    @Override
    public QueryFuture getFeaturesByRange(int hours, FeatureSet set, Location location, String structure, long start, long stop) {
        PluginInterface plugin = new FeaturesByRangePlugin();
        plugin.init(set, location, structure, start, stop);
        return PluginRun.newBuilder().setPluginRunner(SWQEFactory.getPluginRunner(plugin, set, location, structure, start, stop)).build();
    }

    /** {@inheritDoc} */
    @Override
    public QueryFuture getFeaturesByTag(int hours, FeatureSet set, String subject, String predicate, String object) {
        PluginInterface plugin = new FeaturesByTagPlugin();
        plugin.init(set, subject, predicate, object);
        return PluginRun.newBuilder().setPluginRunner(SWQEFactory.getPluginRunner(plugin, set, subject, predicate, object)).build();
    }

    private SeqWareIterable getAllOfClass(Class aClass) {
        List list = new ArrayList();
        for (SGID u : storage.getAllAtoms()) { //listOfEverything) {
            Atom p = storage.deserializeTargetToLatestAtom(u);
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
     *
     * @param fLists a {@link java.util.List} object.
     * @return a {@link java.util.Collection} object.
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

    /** {@inheritDoc} */
    @Override
    public QueryFuture<Long> getFeatureSetCount(int hours, FeatureSet set) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /** {@inheritDoc} */
    @Override
    public <ReturnValue> QueryFuture<ReturnValue> getFeaturesByPlugin(int hours, Class<? extends PluginInterface> pluginClass, FeatureSet set, Object... parameters) {
        try {
            PluginInterface plugin = pluginClass.newInstance();
            plugin.init(set, parameters);
            return PluginRun.newBuilder().setPluginRunner(SWQEFactory.getPluginRunner(plugin, set, parameters)).build();
        } catch (InstantiationException ex) {
            Logger.getLogger(SimplePersistentBackEnd.class.getName()).fatal( null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SimplePersistentBackEnd.class.getName()).fatal( null, ex);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void installPlugin(PluginInterface plugin) {
        this.pluginMap.put(plugin.getClass(), plugin);
    }
}
