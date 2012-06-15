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

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.github.seqware.model.Atom;
import com.github.seqware.model.impl.AtomImpl;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.apache.hadoop.hbase.util.Bytes;
import org.objenesis.strategy.SerializingInstantiatorStrategy;

/**
 *
 * @author dyuen
 */
public class KyroSerialization implements SerializationInterface {

    private Kryo serializer;

    public KyroSerialization() {
        this.serializer = new Kryo();
        // Some magic to make serialization work with private default constructors:
        serializer.setInstantiatorStrategy(new SerializingInstantiatorStrategy());
        serializer.setDefaultSerializer(CompatibleFieldSerializer.class);
        // serializer.register(UUID.class, new JavaSerializer());
    }

    @Override
    public byte[] serialize(Atom atom) {
        ByteArrayOutputStream featureBytes = new ByteArrayOutputStream();
        Output o = new Output(featureBytes);
        serializer.writeClassAndObject(o, atom);
        return o.toBytes();
    
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) {
        Input input = new Input(new ByteArrayInputStream(bytes));
        T deserializedAtom = (T) serializer.readClassAndObject(input);
        return deserializedAtom;
    }
}
