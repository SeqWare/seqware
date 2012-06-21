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

import com.github.seqware.impl.protobufIO.*;
import com.github.seqware.model.*;
import com.github.seqware.model.impl.AtomImpl;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.protobuf.Message;

/**
 *
 * @author dyuen
 */
public class ProtobufSerialization implements SerializationInterface {

    protected BiMap<Class, ProtobufTransferInterface> biMap = new ImmutableBiMap.Builder<Class, ProtobufTransferInterface>().put(Feature.class, new FeatureIO())
            .put(FeatureSet.class, new FeatureSetIO()).put(Analysis.class, new AnalysisIO()).put(AnalysisSet.class, new AnalysisSetIO())
            .put(Reference.class, new ReferenceIO()).put(ReferenceSet.class, new ReferenceSetIO()).put(Tag.class, new TagIO())
            .put(TagSet.class, new TagSetIO()).put(User.class, new UserIO()).put(Group.class, new GroupIO()).build();

    @Override
    public byte[] serialize(Atom atom) {
        Class cl = ((AtomImpl)atom).getHBaseClass();
        ProtobufTransferInterface pb = biMap.get(cl);
        Message m2pb = pb.m2pb((AtomImpl)atom);
        return m2pb.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) {
        ProtobufTransferInterface pb = biMap.get(type);
        return (T)pb.byteArr2m(bytes);
    }
}
