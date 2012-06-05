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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang.SerializationUtils;

/**
 *
 * @author dyuen
 */
public class NonPersistentSerialization implements FileSerializationInterface {

    private Map<UUID, Particle> map = new HashMap<UUID, Particle>();
    
    @Override
    public void serializeParticleToTarget(Particle obj) {
        Particle storeObj = (Particle) SerializationUtils.clone(obj);
        map.put(storeObj.getUUID(), storeObj);
    }

    @Override
    public Particle deserializeTargetToParticle(UUID uuid) {
        return map.get(uuid);
    }
    
}
