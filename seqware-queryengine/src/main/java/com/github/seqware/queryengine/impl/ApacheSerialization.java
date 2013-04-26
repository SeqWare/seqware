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
package com.github.seqware.queryengine.impl;

import com.github.seqware.queryengine.backInterfaces.SerializationInterface;
import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import org.apache.commons.lang.SerializationException;
import org.apache.commons.lang.SerializationUtils;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

/**
 * <p>ApacheSerialization class.</p>
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class ApacheSerialization implements SerializationInterface {
    
    private boolean corruptClassesDetected = false;

    /** {@inheritDoc} */
    @Override
    public byte[] serialize(Atom atom) {
        return Bytes.add(Bytes.toBytes(getSerializationConstant()), SerializationUtils.serialize(atom));
        //return SerializationUtils.serialize(atom);
    }

    /** {@inheritDoc} */
    @Override
    public <T extends AtomImpl> T deserialize(byte[] bytes, Class<T> type) {
        try {
            int serialConstant = Bytes.toInt(Bytes.head(bytes, 4));
            if (serialConstant == getSerializationConstant()) {
                T t = (T) SerializationUtils.deserialize(Bytes.tail(bytes, bytes.length - 4));
                return t;
            }
        } catch (SerializationException e) {
            if (!corruptClassesDetected){
                corruptClassesDetected = true;
                Logger.getLogger(ApacheSerialization.class.getName()).info("ApacheSerialization hit an invalid byte array, ignore if this is expected");
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public int getSerializationConstant() {
        return 0;
    }
}
