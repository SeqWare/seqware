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

import com.github.seqware.queryengine.dto.QESupporting;
import com.github.seqware.queryengine.dto.QESupporting.TagSpecPB;
import com.github.seqware.queryengine.model.TagSpec;
import com.google.protobuf.InvalidProtocolBufferException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dyuen
 */
public class TagSpecIO implements ProtobufTransferInterface<TagSpecPB, TagSpec> {

    @Override
    public TagSpec pb2m(TagSpecPB tag) {
        TagSpec.Builder builder = TagSpec.newBuilder().setKey(tag.getKey());
        TagSpec rTag = builder.build();
        UtilIO.handlePB2Atom(tag.getAtom(), rTag);
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && tag.hasPrecedingVersion()) {
            rTag.setPrecedingVersion(pb2m(tag.getPrecedingVersion()));
        }
        return rTag;
    }

    @Override
    public TagSpecPB m2pb(TagSpec tag) {
        QESupporting.TagSpecPB.Builder builder = QESupporting.TagSpecPB.newBuilder().setKey(tag.getKey());
        // TODO: TagSet not ready
        builder.setAtom(UtilIO.handleAtom2PB(builder.getAtom(), tag));
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && tag.getPrecedingVersion() != null) {
            builder.setPrecedingVersion(m2pb(tag.getPrecedingVersion()));
        }
        TagSpecPB fMesg = builder.build();
        return fMesg;
    }

    @Override
    public TagSpec byteArr2m(byte[] arr) {
        try {
            TagSpecPB userpb = TagSpecPB.parseFrom(arr);
            return pb2m(userpb);
        } catch (InvalidProtocolBufferException ex) {
            Logger.getLogger(FeatureSetIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
