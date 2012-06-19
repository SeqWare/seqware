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
package com.github.seqware.impl.protobufIO;

/**
 * Interface for conversion from protein buffer auto-generated objects 
 * to model and vice versa
 * @author dyuen
 */
public interface ProtobufTransferInterface<S,T> {
    
    /**
     * Convert from protein buffer object to model
     * @param protbuf
     * @return 
     */
    public abstract T pb2m(S protbuf);
    
    /**
     * Convert from model to protein buffer object
     * @param atom
     * @return 
     */
    public abstract S m2pb(T atom);
    
}
