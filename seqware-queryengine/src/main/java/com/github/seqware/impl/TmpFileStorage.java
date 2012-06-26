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
public class TmpFileStorage extends StorageInterface {

    private static final boolean PERSIST = true;
    private File tempDir = new File(FileUtils.getTempDirectory(), this.getClass().getCanonicalName());
    private Map<SGID, FileTypePair> map = new HashMap<SGID, FileTypePair>();
    private final SerializationInterface serializer;

    public TmpFileStorage(SerializationInterface i) {
        this.serializer = i;
        Logger.getLogger(TmpFileStorage.class.getName()).log(Level.INFO, "Starting with {0} in {1} using {2}", new Object[]{TmpFileStorage.class.getSimpleName(), tempDir.getAbsolutePath(), serializer.getClass().getSimpleName()});
        // make a persistent store exists already, otherwise try to retrieve existing items
        try {
            if (!tempDir.exists()) {
                FileUtils.forceMkdir(tempDir);
            } else {
                if (PERSIST) {
                    boolean oldClassesFound = false;
                    for (File f : FileUtils.listFiles(tempDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
                        byte[] objData = FileUtils.readFileToByteArray(f);
                        try {
                            String[] names = f.getName().split("\\.");
                            Class cl = biMap.inverse().get(names[0]);
                            Atom suspect = (Atom) serializer.deserialize(objData, cl);
                            if (suspect != null){
                                map.put(suspect.getSGID(), new FileTypePair(f,cl));
                            }
                        } catch (Exception e) {
                            if (!oldClassesFound) {
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
        String prefix = ((AtomImpl)obj).getHBasePrefix();
        // let's just clone everything on store to simulate hbase
        File target = new File(tempDir, prefix + separator + obj.getSGID().toString());
        byte[] serialRep = serializer.serialize(obj);
        try {
            FileUtils.writeByteArrayToFile(target, serialRep);
            map.put(obj.getSGID(), new FileTypePair(target, ((AtomImpl)obj).getHBaseClass()));
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
            if (sgid == null) {
                return null;
            }
            FileTypePair target = map.get(sgid);
            if (target == null) {
                return null;
            }
            objData = FileUtils.readFileToByteArray(target.file);
            Atom suspect = (Atom) serializer.deserialize(objData, target.cl);
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

    @Override
    public <T extends Atom> T deserializeTargetToAtom(SGID sgid, Class<T> t) {
        return (T) this.deserializeTargetToAtom(sgid);
    }

    public class FileTypePair {
        private File file;
        private Class cl;

        public FileTypePair(File file, Class cl) {
            this.file = file;
            this.cl = cl;
        }
    }
}
