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

import com.github.seqware.model.*;
import com.github.seqware.util.SGID;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import java.util.List;

/**
 * Defines a very basic interface that allows us to abstract whether we write to 
 * files or to databases or just keep things in memory
 * @author dyuen
 */
public abstract class StorageInterface {
    protected BiMap<Class, String> biMap = new ImmutableBiMap.Builder<Class, String>().put(Feature.class, Feature.prefix)
            .put(FeatureSet.class, FeatureSet.prefix).put(Analysis.class, Analysis.prefix).put(AnalysisSet.class, AnalysisSet.prefix)
            .put(Reference.class, Reference.prefix).put(ReferenceSet.class, ReferenceSet.prefix).put(Tag.class, Tag.prefix)
            .put(TagSet.class, TagSet.prefix).put(User.class, User.prefix).put(Group.class, Group.prefix).build();
    public static final String separator = ".";
    
    /**
     * Generically serialize an Atom into the interface
     * This method is also responsible for ensuring that the atom's backendTimestamp is populated
     * @param obj 
     */
    public abstract void serializeAtomToTarget(Atom obj);
    
    /**
     * Generically serialize a batch of Atoms, all of the same class
     * This method is also responsible for ensuring that the atom's backendTimestamp is populated
     * @param <T>
     * @param obj 
     */
    public abstract <T extends Atom> void serializeAtomsToTarget(T ... obj);

    /**
     * Generically get back a Atom from the store using a sgid 
     * @param sgid
     * @return null if no Atom is present with this sgid
     */
    public abstract Atom deserializeTargetToAtom(SGID sgid);
    
    /**
     * Generically get back the latest Atom in a chain from the store using a sgid
     * while ignoring the timestamp
     * @param sgid
     * @return null if no Atom is present with this sgid
     */
    public abstract Atom deserializeTargetToLatestAtom(SGID sgid);
    
    /**
     * Generically get back a specific class of Atom from the store using a sgid 
     * @param <T>
     * @param sgid
     * @param t
     * @return 
     */
    public abstract <T extends Atom> T deserializeTargetToAtom(Class<T> t, SGID sgid);
    
    /**
     * Generically get back a specific class of Atom from the store using a sgid 
     * @param <T>
     * @param sgid
     * @param t
     * @return 
     */
    public abstract <T extends Atom> List<T> deserializeTargetToAtoms(Class<T> t, SGID ... sgid);
    
    /**
     * Generically get back a specific class of Atom from the store using a sgid 
     * while ignoring the timestamp to get the latest one
     * @param <T>
     * @param sgid
     * @param t
     * @return 
     */
    public abstract <T extends Atom> T deserializeTargetToLatestAtom(SGID sgid, Class<T> t);
    
    /**
     * For debugging or very non-optimal implementations
     */
    public abstract Iterable<SGID> getAllAtoms();
    
    
    /**
     * For debugging or testing purposes, this will wipe out all objects in the 
     * serialization store
     */
    public abstract void clearStorage();
}
