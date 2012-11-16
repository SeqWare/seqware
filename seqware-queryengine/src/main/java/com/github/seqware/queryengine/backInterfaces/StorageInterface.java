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
package com.github.seqware.queryengine.backInterfaces;

import com.github.seqware.queryengine.model.*;
import com.github.seqware.queryengine.model.impl.FeatureList;
import com.github.seqware.queryengine.util.SGID;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import java.util.List;

/**
 * Defines a very basic interface that allows us to abstract whether we write to
 * files or to databases or just keep things in memory
 *
 * @author dyuen
 * @version $Id: $Id
 */
public abstract class StorageInterface {
    /**
     * These tables are created on-the-fly with a referenceID appended in order to separate out Features by Reference
     */
    public final BiMap<Class, String> indirectBIMap = new ImmutableBiMap.Builder<Class, String>().put(FeatureList.class, FeatureList.prefix)
            .build();
    /**
     * These tables are always created with the same names
     */
    public final BiMap<Class, String> directBIMap = new ImmutableBiMap.Builder<Class, String>().put(PluginRun.class, PluginRun.prefix)
            .put(Plugin.class, Plugin.prefix).put(Reference.class, Reference.prefix).put(ReferenceSet.class, ReferenceSet.prefix)
            .put(Tag.class, Tag.prefix).put(TagSet.class, TagSet.prefix).put(User.class, User.prefix).put(Group.class, Group.prefix)
            .put(FeatureSet.class, FeatureSet.prefix).build();
    
    /** Constant <code>SEPARATOR="."</code> */
    public static final String SEPARATOR = ".";
    
    /**
     * Generically serialize an Atom into the interface
     * This method is also responsible for ensuring that the atom's backendTimestamp is populated
     *
     * @param obj a {@link com.github.seqware.queryengine.model.Atom} object.
     */
    public abstract void serializeAtomToTarget(Atom obj);
    
    /**
     * Generically serialize a batch of Atoms, all of the same class
     * This method is also responsible for ensuring that the atom's backendTimestamp is populated
     *
     * @param obj a T object.
     */
    public abstract <T extends Atom> void serializeAtomsToTarget(T ... obj);

    /**
     * Generically get back a Atom from the store using a sgid
     *
     * @param sgid a {@link com.github.seqware.queryengine.util.SGID} object.
     * @return null if no Atom is present with this sgid
     */
    public abstract Atom deserializeTargetToAtom(SGID sgid);
    
    /**
     * Generically get back the latest Atom in a chain from the store using a sgid
     * while ignoring the timestamp
     *
     * @param sgid a {@link com.github.seqware.queryengine.util.SGID} object.
     * @return null if no Atom is present with this sgid
     */
    public abstract Atom deserializeTargetToLatestAtom(SGID sgid);
    
    /**
     * Generically get back a specific class of Atom from the store using a sgid
     *
     * @param sgid a {@link com.github.seqware.queryengine.util.SGID} object.
     * @param t a {@link java.lang.Class} object.
     * @return a T object.
     */
    public abstract <T extends Atom> T deserializeTargetToAtom(Class<T> t, SGID sgid);
    
    /**
     * Generically get back a specific class of Atom from the store using a sgid
     *
     * @param sgid a {@link com.github.seqware.queryengine.util.SGID} object.
     * @param t a {@link java.lang.Class} object.
     * @return a {@link java.util.List} object.
     */
    public abstract <T extends Atom> List<T> deserializeTargetToAtoms(Class<T> t, SGID ... sgid);
    
    /**
     * Generically get back a specific class of Atom from the store using a sgid
     * while ignoring the timestamp to get the latest one
     *
     * @param sgid a {@link com.github.seqware.queryengine.util.SGID} object.
     * @param t a {@link java.lang.Class} object.
     * @return a T object.
     */
    public abstract <T extends Atom> T deserializeTargetToLatestAtom(SGID sgid, Class<T> t);
    
    /**
     * For debugging or very non-optimal implementations
     *
     * @return a {@link java.lang.Iterable} object.
     */
    public abstract Iterable<SGID> getAllAtoms();
    
    /**
     * Some storage types should be explicitly told when shutting down to release resources.
     * This should be called by the back-end
     */
    public abstract void closeStorage();
    
    /**
     * For debugging or testing purposes, this will wipe out all objects in the
     * serialization store
     */
    public abstract void clearStorage();
    
    
    /**
     * Iterate through all the feature "buckets" in a feature set, this might be moved later.
     * However, it is currently here because iterating through a FeatureSet might become non-trivial and Storage type dependent.
     * We also need this to return the feature lists in sorted order by rowkey (regardless of timestamp).
     *
     * @param fSet a {@link com.github.seqware.queryengine.model.FeatureSet} object.
     * @return iterator that returns FeatureLists in sorted order
     */
    public abstract Iterable<FeatureList> getAllFeatureListsForFeatureSet(FeatureSet fSet);
}
