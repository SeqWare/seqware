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
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.github.seqware.queryengine.model.*;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.impl.inMemory.*;
import com.github.seqware.queryengine.model.interfaces.ACL;
import com.github.seqware.queryengine.util.FSGID;
import com.github.seqware.queryengine.util.SGID;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import org.apache.hadoop.hbase.util.Bytes;
import org.objenesis.strategy.SerializingInstantiatorStrategy;

/**
 * <p>KryoSerialization class.</p>
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class KryoSerialization implements SerializationInterface {

    private Kryo serializer;

    /**
     * <p>Constructor for KryoSerialization.</p>
     */
    public KryoSerialization() {
        this.serializer = new Kryo();
        // Some magic to make serialization work with private default constructors:
        serializer.setInstantiatorStrategy(new SerializingInstantiatorStrategy());
        serializer.setDefaultSerializer(CompatibleFieldSerializer.class);
        serializer.register(ACL.class, new JavaSerializer(), 0);
        serializer.register(SGID.class, 1);
        serializer.register(FSGID.class, 2);
        serializer.register(User.class, 3);
        serializer.register(Group.class, 4);
        serializer.register(Reference.class, 5);
        serializer.register(Tag.class, 6);
        serializer.register(InMemoryTagSet.class, 7);
        serializer.register(InMemoryPlugin.class, 8);
        serializer.register(InMemoryGroup.class, 9);
        serializer.register(InMemoryFeatureSet.class, 11);
        serializer.register(PluginRun.class, 12);
        serializer.register(InMemoryReference.class, 14);
        serializer.register(HashSet.class, 15);
        serializer.register(Feature.class, 16);
        serializer.register(ArrayList.class, 17);
        serializer.register(Feature.Strand.class, 18);
        serializer.register(Date.class, 19);
        serializer.register(InMemoryReferenceSet.class, 20);
        serializer.register(Tag.ValueType.class, 21);
        serializer.register(byte[].class, 22);
        // let's track down this "Encountered unregistered class ID"
        serializer.setRegistrationRequired(true);
    }

    /** {@inheritDoc} */
    @Override
    public byte[] serialize(Atom atom) {
        ByteArrayOutputStream featureBytes = new ByteArrayOutputStream();
        Output o = new Output(featureBytes);
        o.write(Bytes.toBytes(getSerializationConstant()));
        serializer.writeClassAndObject(o, atom);
        o.close();
        return featureBytes.toByteArray();
    
    }

    /** {@inheritDoc} */
    @Override
    public <T extends AtomImpl> T deserialize(byte[] bytes, Class<T> type) {
        int serialConstant = Bytes.toInt(Bytes.head(bytes, 4));
            if (serialConstant == getSerializationConstant()) {
            byte[] byteArr = (Bytes.tail(bytes, bytes.length-4));
            Input input = new Input(new ByteArrayInputStream(byteArr));
            T deserializedAtom = (T) serializer.readClassAndObject(input);
            input.close();
            return deserializedAtom;
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public int getSerializationConstant() {
        return 20000;
    }
}
