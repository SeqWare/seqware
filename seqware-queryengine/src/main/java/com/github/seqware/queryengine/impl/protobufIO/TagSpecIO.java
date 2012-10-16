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
import com.github.seqware.queryengine.dto.QESupporting.TagPB;
import com.github.seqware.queryengine.model.Tag;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.log4j.Logger;

/**
 * <p>TagSpecIO class.</p>
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class TagSpecIO implements ProtobufTransferInterface<TagPB, Tag> {

    /** {@inheritDoc} */
    @Override
    public Tag pb2m(TagPB tag) {
        Tag.Builder builder = Tag.newBuilder().setKey(tag.getKey())
                .setPredicate(tag.getPredicate());
        
        Tag rTag = builder.build();
        UtilIO.handlePB2Atom(tag.getAtom(), rTag);
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && tag.hasPrecedingVersion()) {
            rTag.setPrecedingVersion(pb2m(tag.getPrecedingVersion()));
        }
        return rTag;
    }

    /** {@inheritDoc} */
    @Override
    public TagPB m2pb(Tag tag) {
        QESupporting.TagPB.Builder builder = QESupporting.TagPB.newBuilder().setKey(tag.getKey());
        builder.setPredicate(tag.getPredicate());
        // TODO: TagSet not ready
        builder.setAtom(UtilIO.handleAtom2PB(builder.getAtom(), tag));
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && tag.getPrecedingVersion() != null) {
            builder.setPrecedingVersion(m2pb(tag.getPrecedingVersion()));
        }
        TagPB fMesg = builder.build();
        return fMesg;
    }

    /** {@inheritDoc} */
    @Override
    public Tag byteArr2m(byte[] arr) {
        try {
            TagPB userpb = TagPB.parseFrom(arr);
            return pb2m(userpb);
        } catch (InvalidProtocolBufferException ex) {
            Logger.getLogger(FeatureSetIO.class.getName()).fatal( "Invalid PB", ex);
        }
        return null;
    }
}
