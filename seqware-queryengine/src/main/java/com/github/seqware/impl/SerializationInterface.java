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

/**
 * This simply extracts the serialization of bytes[] to atoms and vice versa
 * @author dyuen
 */
public interface SerializationInterface {
    
    /**
     * Given an atom, get back an array of bytes
     * @param atom 
     * @return 
     */
    public byte[] serialize(Atom atom);
    
    /**
     * Given an array of bytes, get back an atom
     * @param <T> class type, behaviour is undefined if the class is incorrect
     * @param bytes byte representation of the desired object
     * @return 
     */
    public <T> T deserialize(byte[] bytes, Class<T> type);
    
}
