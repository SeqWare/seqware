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

import com.github.seqware.model.Atom;
import com.github.seqware.model.impl.AtomImpl;
import com.github.seqware.util.SGID;
import java.util.*;
import java.util.Map.Entry;

/**
 * Doesn't really store things anywhere, just keeps it in memory.
 * This is totally inefficient and is meant only for testing and prototyping purposes
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
        Class cl = ((AtomImpl)obj).getHBaseClass();
        obj.getSGID().setBackendTimestamp(new Date(System.currentTimeMillis()));
        ByteTypePair pair = new ByteTypePair(serializer.serialize(obj), cl);
        map.put(obj.getSGID(), pair);
    }

    @Override
    public Atom deserializeTargetToAtom(SGID sgid) {
        if (!map.containsKey(sgid)){
            return null;
        }
        Atom a =  (Atom) serializer.deserialize(map.get(sgid).bArr, map.get(sgid).cl);
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
    public <T extends Atom> T deserializeTargetToAtom(SGID sgid, Class<T> t) {
        return (T) this.deserializeTargetToAtom(sgid);
    }

    @Override
    public Atom deserializeTargetToLatestAtom(SGID sgid) {
        List<Atom> aList = new ArrayList<Atom>();
        for(Entry<SGID, ByteTypePair> e : map.entrySet()){
            if (e.getKey().getChainID().equals(sgid.getChainID())){
                aList.add((Atom)serializer.deserialize(e.getValue().bArr, e.getValue().cl));
            }
        }
        // check for latest one, HBase will do this much more efficiently
        if (aList.isEmpty()) {return null;}
        Atom latest = aList.get(0);
        for(Atom a : aList){
            if (a.getTimestamp().after(latest.getTimestamp())){
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
