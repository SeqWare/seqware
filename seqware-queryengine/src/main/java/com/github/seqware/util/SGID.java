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
package com.github.seqware.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.seqware.model.Feature;
import java.io.Serializable;
import java.util.UUID;

/**
 * A wrapper for our eventual choice of globally unique primary key which may or
 * may not be UUIDs.
 *
 * @author dyuen
 */
public class SGID implements Serializable, KryoSerializable {

    private UUID uuid = null;

    public SGID() {
        uuid = UUID.randomUUID();
    }

    @Override
    public boolean equals(Object other) {
        return this.uuid.equals(((SGID) other).uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return uuid.toString();
    }

    /**
     * Get underlying UUID implementation, should only be called within the back-end
     */
    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void write(Kryo kryo, Output output) {
        output.writeLong(uuid.getLeastSignificantBits());
        output.writeLong(uuid.getMostSignificantBits());
    }

    @Override
    public void read(Kryo kryo, Input input) {
        long leastSig = input.readLong();
        long mostSig = input.readLong();
        uuid = new UUID(mostSig, leastSig);
    }
}
