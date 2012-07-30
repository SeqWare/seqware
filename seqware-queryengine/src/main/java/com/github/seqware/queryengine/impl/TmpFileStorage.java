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
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.impl.FeatureList;
import com.github.seqware.queryengine.model.impl.lazy.LazyFeatureSet;
import com.github.seqware.queryengine.util.SGID;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

/**
 * Stores objects in the temporary directory of the disk
 *
 * @author dyuen
 */
public class TmpFileStorage extends StorageInterface {

    boolean oldClassesFound = false;
    private static final boolean PERSIST = Constants.PERSIST;
    private static final String CLASS_BREAK = "~";
    private File tempDir = new File(FileUtils.getTempDirectory(), this.getClass().getCanonicalName());
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
        // let's just clone everything on store to simulate hbase
        File target = new File(tempDir, prefix + CLASS_BREAK + NonPersistentStorage.createKey(obj.getSGID()));
        byte[] serialRep = serializer.serialize(obj);
        try {
            FileUtils.writeByteArrayToFile(target, serialRep);
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
        try {
            if (sgid == null) {
                return null;
            }
            //FileTypePair target = map.get(sgid);
            String suffix = NonPersistentStorage.createKey(sgid);
            String regex = "[a-zA-Z_0-9\\.]+~" + suffix;
            Collection<File> listFiles = FileUtils.listFiles(tempDir, new RegexFileFilter(regex), null);
            assert (listFiles.size() == 1);
            Atom suspect = handleFileWithoutClass(listFiles.iterator().next());
            if (suspect != null) {
                return suspect;
            }
            return null;
        } catch (IOException ex) {
            Logger.getLogger(TmpFileStorage.class.getName()).log(Level.SEVERE, "Failure to deserialize", ex);
        }
        System.exit(-1);
        return null;
    }

    private Atom handleFileWithoutClass(File file) throws IOException {
        String[] names = file.getName().split(TmpFileStorage.CLASS_BREAK);
        String clName = names[0].split("\\" + StorageInterface.SEPARATOR)[0];
        Class cl = directBIMap.inverse().get(clName);
        if (cl == null) {
            cl = indirectBIMap.inverse().get(clName);
        }
        Atom suspect = handleFileGivenClass(file, cl);
        return suspect;
    }

    private Atom handleFileGivenClass(File file, Class cl) throws IOException {
        byte[] objData;
        objData = FileUtils.readFileToByteArray(file);
        assert (cl != null && objData != null);
        Atom suspect = (Atom) serializer.deserialize(objData, cl);
        return suspect;
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
                Atom suspect = this.handleFileWithoutClass(f);
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
        String suffix = NonPersistentStorage.createKey(sgid, false);
        String regex = "\\w+~.*" + suffix + "\\..*";
        for (File file : FileUtils.listFiles(tempDir, new RegexFileFilter(regex), null)) {
            try {
                Atom suspect = handleFileWithoutClass(file);
                if (suspect != null) {
                    aList.add(suspect);
                }
            } catch (IOException ex) {
                Logger.getLogger(TmpFileStorage.class.getName()).log(Level.SEVERE, null, ex);
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
        /**
         * we do not have to do anything particularly specific here
         */
    }

    @Override
    public Iterable<FeatureList> getAllFeatureListsForFeatureSet(FeatureSet fSet) {
        assert (fSet instanceof LazyFeatureSet);
        List<FeatureList> features = new ArrayList<FeatureList>();
        String substring = NonPersistentStorage.createKey(fSet.getSGID());
        String regex = "Feature\\..*~.*" + substring;
        for (File file : FileUtils.listFiles(tempDir, new RegexFileFilter(regex), null)) {
            try {
                FeatureList suspect = (FeatureList) handleFileGivenClass(file, FeatureList.class);
                features.add(suspect);
            } catch (IOException ex) {
                Logger.getLogger(TmpFileStorage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Collections.sort(features, new Comparator<FeatureList>(){
            @Override
            public int compare(FeatureList o1, FeatureList o2) {
                return o1.getSGID().getRowKey().compareTo(o2.getSGID().getRowKey());
            } 
        });
        return features;
    }
}
