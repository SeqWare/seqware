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

/**
 * Defines a very basic interface that allows us to abstract whether we write to 
 * files or to databases or just keep things in memory
 * @author dyuen
 */
public interface StorageInterface {
    /**
     * Generically serialize a Atom into the interface
     * @param obj 
     */
    public void serializeAtomToTarget(Atom obj);

    /**
     * Generically get back a Atom from the store using a sgid 
     * @param sgid
     * @return null if no Atom is present with this sgid
     */
    public Atom deserializeTargetToAtom(SGID sgid);
    
    /**
     * For debugging or very non-optimal implementations
     */
    public Iterable<SGID> getAllAtoms();
    
    
    /**
     * For debugging or testing purposes, this will wipe out all objects in the 
     * serialization store
     */
    public void clearStorage();
}
