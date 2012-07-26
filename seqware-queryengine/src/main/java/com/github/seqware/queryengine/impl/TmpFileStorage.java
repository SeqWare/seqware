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

import com.github.seqware.queryengine.Constants;
import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.impl.FeatureList;
import com.github.seqware.queryengine.util.SGID;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

/**
 * Stores objects in the temporary directory of the disk
 *
 * @author dyuen
 */
public class TmpFileStorage extends StorageInterface {

    boolean oldClassesFound = false;
    private static final boolean PERSIST = Constants.PERSIST;
    private File tempDir = new File(FileUtils.getTempDirectory(), this.getClass().getCanonicalName());
    // I think we can get away without the map to get of weird cycles in the init
    //private Map<SGID, FileTypePair> map = new HashMap<SGID, FileTypePair>();
    private final SerializationInterface serializer;

    public TmpFileStorage(SerializationInterface i) {
        this.serializer = i;
        Logger.getLogger(TmpFileStorage.class.getName()).log(Level.INFO, "Starting with {0} in {1} using {2}", new Object[]{TmpFileStorage.class.getSimpleName(), tempDir.getAbsolutePath(), serializer.getClass().getSimpleName()});
        // make a persistent store exists already, otherwise try to retrieve existing items
        try {
            if (!tempDir.exists()) {
                FileUtils.forceMkdir(tempDir);
            } else {
                if (!PERSIST) {
                    this.clearStorage();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(SimplePersistentBackEnd.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }

    @Override
    public void serializeAtomToTarget(Atom obj) {
        String prefix = ((AtomImpl) obj).getHBasePrefix();
//        obj.getSGID().setBackendTimestamp(new Date(System.currentTimeMillis()));
        // let's just clone everything on store to simulate hbase
        File target = new File(tempDir, prefix + separator + obj.getSGID().getRowKey() + separator + obj.getSGID().getBackendTimestamp().getTime());
        byte[] serialRep = serializer.serialize(obj);
        try {
            FileUtils.writeByteArrayToFile(target, serialRep);
//            map.put(obj.getSGID(), new FileTypePair(target, ((AtomImpl)obj).getHBaseClass()));
        } catch (IOException ex) {
            Logger.getLogger(TmpFileStorage.class.getName()).log(Level.SEVERE, "Failiure to serialize", ex);
            System.exit(-1);
        }
    }

    @Override
    public void serializeAtomsToTarget(Atom... atomArr) {
        for (Atom obj : atomArr) {
            serializeAtomToTarget(obj);
        }
    }

    @Override
    public Atom deserializeTargetToAtom(SGID sgid) {
        // let's just clone everything on store to simulate hbase
        byte[] objData;
        try {
            if (sgid == null) {
                return null;
            }
            // if this is a Feature in a FeatureList, we do not have granularity of 
            
            //FileTypePair target = map.get(sgid);
            String suffix = sgid.getRowKey() + StorageInterface.separator + sgid.getBackendTimestamp().getTime();
            for (File file : FileUtils.listFiles(tempDir, new SuffixFileFilter(suffix), null)) {
                String[] names = file.getName().split("\\"+StorageInterface.separator);
                Class cl = directBIMap.inverse().get(names[0]);
                if (cl == null){
                    cl = indirectBIMap.inverse().get(names[0]);
                }
                objData = FileUtils.readFileToByteArray(file);
                assert(cl != null && objData != null);
                Atom suspect = (Atom) serializer.deserialize(objData, cl);
                if (suspect instanceof FeatureList){
                    // dig deeper if this is a list of Features
                    for(Feature f : ((FeatureList)suspect).getFeatures()){
                        if (f.getSGID().equals(sgid)){
                            return f;
                        }
                    }
                }
                return suspect;
            }
            return null;
        } catch (IOException ex) {
            Logger.getLogger(TmpFileStorage.class.getName()).log(Level.SEVERE, "Failure to deserialize", ex);
        }
        System.exit(-1);
        return null;
    }

    @Override
    public final void clearStorage() {
        try {
            FileUtils.cleanDirectory(tempDir);
        } catch (IOException ex) {
            Logger.getLogger(TmpFileStorage.class.getName()).log(Level.SEVERE, "failed to clear serialization store", ex);
        }
    }

    @Override
    public Iterable<SGID> getAllAtoms() {
        List<SGID> list = new ArrayList<SGID>();
        for (File f : FileUtils.listFiles(tempDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
            try {
                byte[] objData = FileUtils.readFileToByteArray(f);
                String[] names = f.getName().split("\\"+StorageInterface.separator);
                Class cl = directBIMap.inverse().get(names[0]);
                Atom suspect = (Atom) serializer.deserialize(objData, cl);
                if (suspect != null) {
                    list.add(suspect.getSGID());
                }
            } catch (Exception e) {
                if (!oldClassesFound) {
                    oldClassesFound = true;
                    //TODO: we'll probably want something cooler, but for now, if we run into an old version, just warn about it
                    Logger.getLogger(TmpFileStorage.class.getName()).log(Level.INFO, "Obselete classes detected in {0} you may want to clean it", tempDir.getAbsolutePath());
                }
            }
        }
        return list;
    }

    @Override
    public <T extends Atom> List deserializeTargetToAtoms(Class<T> t, SGID... sgidArr) {
        List<T> list = new ArrayList<T>();
        for (SGID sgid : sgidArr) {
            list.add((T) this.deserializeTargetToAtom(sgid));
        }
        return list;
    }

    @Override
    public <T extends Atom> T deserializeTargetToAtom(Class<T> t, SGID sgid) {
        return (T) this.deserializeTargetToAtom(sgid);
    }

    @Override
    public Atom deserializeTargetToLatestAtom(SGID sgid) {
        List<Atom> aList = new ArrayList<Atom>();
        for (File file : FileUtils.listFiles(tempDir, new WildcardFileFilter("*" + sgid.getRowKey() + "*"), null)) {
            String[] names = file.getName().split("\\"+StorageInterface.separator);
            long time = Long.valueOf(names[names.length - 1]);
            SGID newSGID = new SGID(sgid.getUuid().getMostSignificantBits(), sgid.getUuid().getLeastSignificantBits(), time);
            Atom atomCandidate = deserializeTargetToAtom(newSGID);
            if (atomCandidate != null) {
                aList.add(atomCandidate);
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

    @Override
    public void closeStorage() {
        /** we do not have to do anything particularly specific here */
    }
}
