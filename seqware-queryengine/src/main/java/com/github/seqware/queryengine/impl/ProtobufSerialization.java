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

import com.github.seqware.queryengine.impl.protobufIO.AnalysisSetIO;
import com.github.seqware.queryengine.impl.protobufIO.ReferenceIO;
import com.github.seqware.queryengine.impl.protobufIO.TagSetIO;
import com.github.seqware.queryengine.impl.protobufIO.FeatureSetIO;
import com.github.seqware.queryengine.impl.protobufIO.UserIO;
import com.github.seqware.queryengine.impl.protobufIO.ProtobufTransferInterface;
import com.github.seqware.queryengine.impl.protobufIO.AnalysisIO;
import com.github.seqware.queryengine.impl.protobufIO.ReferenceSetIO;
import com.github.seqware.queryengine.impl.protobufIO.TagIO;
import com.github.seqware.queryengine.impl.protobufIO.GroupIO;
import com.github.seqware.queryengine.impl.protobufIO.FeatureIO;
import com.github.seqware.queryengine.model.AnalysisSet;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.ReferenceSet;
import com.github.seqware.queryengine.model.TagSet;
import com.github.seqware.queryengine.model.Analysis;
import com.github.seqware.queryengine.model.Group;
import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.User;
import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.protobuf.Message;
import org.apache.commons.lang.SerializationUtils;
import org.apache.hadoop.hbase.util.Bytes;

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
        return Bytes.add(Bytes.toBytes(getSerializationConstant()),m2pb.toByteArray());
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) {
        int serialConstant = Bytes.toInt(Bytes.head(bytes, 4));
        if (serialConstant == getSerializationConstant()){
            ProtobufTransferInterface pb = biMap.get(type);
            return (T)pb.byteArr2m(Bytes.tail(bytes, bytes.length-4));
        }
        return null;
    }

    @Override
    public int getSerializationConstant() {
        return 10000;
    }
}
