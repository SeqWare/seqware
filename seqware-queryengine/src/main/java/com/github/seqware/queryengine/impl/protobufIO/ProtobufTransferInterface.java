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
package com.github.seqware.queryengine.impl.protobufIO;

import com.github.seqware.queryengine.model.Atom;
import com.google.protobuf.Message;

/**
 * Interface for conversion from protein buffer auto-generated objects
 * to model and vice versa
 *
 * @author dyuen
 * @version $Id: $Id
 */
public interface ProtobufTransferInterface<S extends Message,T extends Atom> {
    
    /** Constant <code>PERSIST_VERSION_CHAINS=false</code> */
    public static final boolean PERSIST_VERSION_CHAINS = false;
    
    /**
     * Convert from protein buffer object in byte array to model.
     * Implementations of this method are kinda stupid, we should be able to template/generic this but
     * Java dislikes it for some reason.
     *
     * @param arr in byte form
     * @return a T object.
     */
    public abstract T byteArr2m(byte[] arr);
    
    /**
     * Convert from protein buffer object to model
     *
     * @param protBuff in byte form
     * @return a T object.
     */
    public abstract T pb2m(S protBuff);
    
    /**
     * Convert from model to protein buffer object
     *
     * @param atom a T object.
     * @return a S object.
     */
    public abstract S m2pb(T atom);
    
}
