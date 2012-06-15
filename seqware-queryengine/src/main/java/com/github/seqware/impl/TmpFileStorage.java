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
import com.github.seqware.util.SGID;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

/**
 * Stores objects in the temporary directory of the disk
 * 
 * @author dyuen
 */
public class TmpFileStorage implements StorageInterface {

    private static final boolean PERSIST = true;
    private File tempDir = new File(FileUtils.getTempDirectory(), this.getClass().getCanonicalName());
    private Map<SGID, File> map = new HashMap<SGID, File>();
    private final SerializationInterface serializer;

    public TmpFileStorage(SerializationInterface i) {
        this.serializer = i;
        Logger.getLogger(TmpFileStorage.class.getName()).log(Level.INFO, "Starting with JavaPersistentBackEnd in: {0}", tempDir.getAbsolutePath());
        // make a persistent store exists already, otherwise try to retrieve existing items
        try {
            if (!tempDir.exists()) {
                FileUtils.forceMkdir(tempDir);
            } else {
                if (PERSIST) {
                    boolean oldClassesFound = false;
                    for (File f : FileUtils.listFiles(tempDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
                        byte[] objData = FileUtils.readFileToByteArray(f);
                        try{
                            Atom suspect = serializer.deserialize(objData, Atom.class);
                            map.put(suspect.getSGID(), f);
                        } catch(Exception e){
                            if (!oldClassesFound){
                                oldClassesFound = true;
                                //TODO: we'll probably want something cooler, but for now, if we run into an old version, just warn about it
                                Logger.getLogger(TmpFileStorage.class.getName()).log(Level.INFO, "Obselete classes detected in {0} you may want to clean it", tempDir.getAbsolutePath());
                            }
                        }
                    }
                    Logger.getLogger(TmpFileStorage.class.getName()).log(Level.INFO, "Recovered {0} objects from store directory", map.size());
                } else {
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
        // let's just clone everything on store to simulate hbase
        File target = new File(tempDir, obj.getSGID().toString());
        byte[] serialRep = serializer.serialize(obj);
        try {
            FileUtils.writeByteArrayToFile(target, serialRep);
            map.put(obj.getSGID(), target);
        } catch (IOException ex) {
            Logger.getLogger(TmpFileStorage.class.getName()).log(Level.SEVERE, "Failiure to serialize", ex);
            System.exit(-1);
        }
    }

    @Override
    public Atom deserializeTargetToAtom(SGID sgid) {
        // let's just clone everything on store to simulate hbase
        byte[] objData;
        try {
            if (sgid == null){
                return null;
            }
            File target = map.get(sgid);
            if (target == null) {
                return null;
            }
            objData = FileUtils.readFileToByteArray(target);
            Atom suspect = serializer.deserialize(objData, Atom.class);
            return suspect;
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
        return map.keySet();
    }
}
