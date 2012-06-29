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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.SerializationException;
import org.apache.commons.lang.SerializationUtils;
import org.apache.hadoop.hbase.util.Bytes;

/**
 *
 * @author dyuen
 */
public class ApacheSerialization implements SerializationInterface {
    
    private boolean corruptClassesDetected = false;

    @Override
    public byte[] serialize(Atom atom) {
        return Bytes.add(Bytes.toBytes(getSerializationConstant()), SerializationUtils.serialize(atom));
        //return SerializationUtils.serialize(atom);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) {
        try {
            int serialConstant = Bytes.toInt(Bytes.head(bytes, 4));
            if (serialConstant == getSerializationConstant()) {
                return (T) SerializationUtils.deserialize(Bytes.tail(bytes, bytes.length - 4));
            }
        } catch (SerializationException e) {
            if (!corruptClassesDetected){
                corruptClassesDetected = true;
                Logger.getLogger(ApacheSerialization.class.getName()).log(Level.INFO, "ApacheSerialization hit an invalid byte array, ignore if this is expected");
            }
        }
        return null;
    }

    @Override
    public int getSerializationConstant() {
        return 0;
    }
}
