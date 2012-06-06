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

import com.github.seqware.model.Molecule;
import com.github.seqware.model.Particle;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.SerializationUtils;

/**
 *
 * @author dyuen
 */
public class ApacheUtilsPersistentSerialization implements FileSerializationInterface {

    private static final boolean PERSIST = true;
    private File tempDir = new File(FileUtils.getTempDirectory(), this.getClass().getCanonicalName());
    private Map<UUID, File> map = new HashMap<UUID, File>();

    public ApacheUtilsPersistentSerialization() {
        Logger.getLogger(ApacheUtilsPersistentSerialization.class.getName()).log(Level.INFO, "Starting with JavaPersistentBackEnd in: {0}", tempDir.getAbsolutePath());
        // make a persistent store exists already, otherwise try to retrieve existing items
        try {
            if (!tempDir.exists()) {
                FileUtils.forceMkdir(tempDir);
            } else {
                if (PERSIST) {
                    for (File f : FileUtils.listFiles(tempDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
                        byte[] objData = FileUtils.readFileToByteArray(f);
                        Particle suspect = (Particle) SerializationUtils.deserialize(objData);
                        map.put(suspect.getUUID(), f);
                    }
                    Logger.getLogger(ApacheUtilsPersistentSerialization.class.getName()).log(Level.INFO, "Recovered {0} objects from store directory", map.size());
                } else {
                    this.clearSerialization();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(SimplePersistentBackEnd.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }

    @Override
    public void serializeParticleToTarget(Particle obj) {
        // let's just clone everything on store to simulate hbase
        File target = new File(tempDir, obj.getUUID().toString());
        byte[] serialRep = SerializationUtils.serialize(obj);
        try {
            FileUtils.writeByteArrayToFile(target, serialRep);
            map.put(obj.getUUID(), target);
        } catch (IOException ex) {
            Logger.getLogger(ApacheUtilsPersistentSerialization.class.getName()).log(Level.SEVERE, "Failiure to serialize", ex);
            System.exit(-1);
        }
    }

    @Override
    public Particle deserializeTargetToParticle(UUID uuid) {
        // let's just clone everything on store to simulate hbase
        byte[] objData;
        try {
            File target = map.get(uuid);
            if (target == null) {
                return null;
            }
            objData = FileUtils.readFileToByteArray(target);
            Particle suspect = (Particle) SerializationUtils.deserialize(objData);
            return suspect;
        } catch (IOException ex) {
            Logger.getLogger(ApacheUtilsPersistentSerialization.class.getName()).log(Level.SEVERE, "Failure to deserialize", ex);
        }
        System.exit(-1);
        return null;
    }

    @Override
    public void clearSerialization() {
        try {
            FileUtils.cleanDirectory(tempDir);
        } catch (IOException ex) {
            Logger.getLogger(ApacheUtilsPersistentSerialization.class.getName()).log(Level.SEVERE, "failed to clear serialization store", ex);
        }
    }

    @Override
    public Iterable<UUID> getAllParticles() {
        return map.keySet();
    }
}
