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

import com.github.seqware.model.Particle;
import com.github.seqware.util.SGID;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang.SerializationUtils;

/**
 *
 * @author dyuen
 */
public class NonPersistentSerialization implements FileSerializationInterface {

    private Map<SGID, Particle> map = new HashMap<SGID, Particle>();
    
    @Override
    public void serializeParticleToTarget(Particle obj) {
        Particle storeObj = (Particle) SerializationUtils.clone(obj);
        map.put(storeObj.getSGID(), storeObj);
    }

    @Override
    public Particle deserializeTargetToParticle(SGID sgid) {
        return map.get(sgid);
    }

    @Override
    public void clearSerialization() {
        map.clear();
    }

    @Override
    public Iterable<SGID> getAllParticles() {
        return map.keySet();
    }
    
}
