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
package com.github.seqware.queryengine.backInterfaces;

import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.impl.AtomImpl;

/**
 * This simply extracts the serialization of bytes[] to atoms and vice versa.
 *
 * The serializationConstant is a value that is stored as the header of each
 * serialization type in order so that they can record information for
 * compatibility and to quickly detect incompatible serialization types
 *
 * @author dyuen
 * @version $Id: $Id
 */
public interface SerializationInterface {

    /**
     * Given an atom, get back an array of bytes
     *
     * @param atom element to be serialized
     * @return serialized version of the atom with an integer header
     */
    public byte[] serialize(Atom atom);

    /**
     * Given an array of bytes, get back an atom
     *
     * @param bytes byte representation of the desired object
     * @return de-serialized object, or null if the serialization type does not match
     * @param type a {@link java.lang.Class} object.
     */
    public <T extends AtomImpl> T deserialize(byte[] bytes, Class<T> type);

    /**
     * Stored as the first four bytes of serialization so that serialization
     * types can pre-emptively handle their own forward/backward compatibility.
     *
     * 0 has been reserved for ApacheSerialization
     * 10,000-19,999 has been reserved for ProtocolBuffers
     * 20,000-29,999 has been reserved for Kryo
     *
     * @return a int.
     */
    public abstract int getSerializationConstant();
}
