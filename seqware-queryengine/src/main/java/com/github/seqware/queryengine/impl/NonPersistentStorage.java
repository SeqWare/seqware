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

import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.util.SGID;
import java.util.*;
import java.util.Map.Entry;

/**
 * Doesn't really store things anywhere, just keeps it in memory. This is
 * totally inefficient and is meant only for testing and prototyping purposes
 *
 * @author dyuen
 */
public class NonPersistentStorage extends StorageInterface {

    private Map<SGID, ByteTypePair> map = new HashMap<SGID, ByteTypePair>();
    private final SerializationInterface serializer;

    public NonPersistentStorage(SerializationInterface i) {
        this.serializer = i;
    }

    @Override
    public void serializeAtomToTarget(Atom obj) {
        AtomImpl objImpl = (AtomImpl) obj;
        Class cl = objImpl.getHBaseClass();
        if (objImpl.getPrecedingSGID() != null){
            assert(!(objImpl.getPrecedingSGID().equals(objImpl.getSGID())));
        }
//        obj.getSGID().setBackendTimestamp(new Date(System.currentTimeMillis()));
        ByteTypePair pair = new ByteTypePair(serializer.serialize(obj), cl);
        map.put(obj.getSGID(), pair);
    }
    
    @Override
    public <T extends Atom> void serializeAtomsToTarget(T... atomArr) {
        for(Atom obj : atomArr){
            serializeAtomToTarget(obj);
        }
    }

    @Override
    public Atom deserializeTargetToAtom(SGID sgid) {
        if (!map.containsKey(sgid)) {
            return null;
        }
        Atom a = (Atom) serializer.deserialize(map.get(sgid).bArr, map.get(sgid).cl);
        return a;
    }

    @Override
    public void clearStorage() {
        map.clear();
    }

    @Override
    public Iterable<SGID> getAllAtoms() {
        return map.keySet();
    }

    @Override
    public <T extends Atom> T deserializeTargetToAtom(Class<T> t, SGID sgid) {
        return (T) this.deserializeTargetToAtom(sgid);
    }

    @Override
    public <T extends Atom> List<T> deserializeTargetToAtoms(Class<T> t, SGID... sgid) {
        List<T> list = new ArrayList<T>();
        for (SGID sg : sgid) {
            list.add((T) this.deserializeTargetToAtom(sg));
        }
        return list;
    }

    @Override
    public Atom deserializeTargetToLatestAtom(SGID sgid) {
        List<Atom> aList = new ArrayList<Atom>();
        for (Entry<SGID, ByteTypePair> e : map.entrySet()) {
            if (e.getKey().getRowKey().equals(sgid.getRowKey())) {
                aList.add((Atom) serializer.deserialize(e.getValue().bArr, e.getValue().cl));
            }
        }
        // check for latest one, HBase will do this much more efficiently
        if (aList.isEmpty()) {
            return null;
        }
        Atom latest = aList.get(0);
        for (Atom a : aList) {
            if (a.getTimestamp().after(latest.getTimestamp())) {
                latest = a;
            }
        }
        return latest;
    }

    @Override
    public <T extends Atom> T deserializeTargetToLatestAtom(SGID sgid, Class<T> t) {
        return (T) this.deserializeTargetToLatestAtom(sgid);
    }

    public class ByteTypePair {

        private byte[] bArr;
        private Class cl;

        public ByteTypePair(byte[] bArr, Class cl) {
            this.bArr = bArr;
            this.cl = cl;
        }
    }
}
