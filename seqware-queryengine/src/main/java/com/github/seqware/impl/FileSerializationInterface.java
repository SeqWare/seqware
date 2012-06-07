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

/**
 * Defines a very basic interface that allows serialization to the filesystem
 * @author dyuen
 */
public interface FileSerializationInterface {
    /**
     * Generically serialize a particle into the interface
     * @param obj 
     */
    public void serializeParticleToTarget(Particle obj);

    /**
     * Generically get back a particle from the store using a sgid 
     * @param sgid
     * @return null if no particle is present with this sgid
     */
    public Particle deserializeTargetToParticle(SGID sgid);
    
    /**
     * For debugging or very non-optimal implementations
     */
    public Iterable<SGID> getAllParticles();
    
    
    /**
     * For debugging or testing purposes, this will wipe out all objects in the 
     * serialization store
     */
    public void clearSerialization();
}
