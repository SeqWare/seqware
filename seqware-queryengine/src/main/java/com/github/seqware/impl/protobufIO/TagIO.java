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

import com.github.seqware.dto.QESupporting;
import com.github.seqware.dto.QESupporting.TagPB;
import com.github.seqware.model.Tag;
import com.github.seqware.util.SGID;
import java.util.Date;

/**
 *
 * @author dyuen
 */
public class TagIO {

    public static Tag pb2m(TagPB tag) {
        Tag.Builder builder = Tag.newBuilder().setKey(tag.getKey());
        builder = tag.hasPredicate() ? builder.setPredicate(tag.getPredicate()) : builder;
        builder = tag.hasValue() ? builder.setValue(tag.getValue()) : builder;
        Tag rTag = builder.build();
        SGID pID = tag.hasPrecedingID() ? SGIDIO.pb2m(tag.getPrecedingID()) : null;
        rTag.impersonate(SGIDIO.pb2m(tag.getSgid()), new Date(tag.getDate()), pID);
        return rTag;
    }

    public static TagPB m2pb(Tag tag) {
        QESupporting.TagPB.Builder builder = QESupporting.TagPB.newBuilder().setKey(tag.getKey());
        builder = tag.getPredicate() != null ? builder.setPredicate(tag.getPredicate()) : builder;
        builder = tag.getValue() != null ? builder.setValue(tag.getValue()) : builder;
        // TODO: TagSet not ready
        builder.setSgid(SGIDIO.m2pb(tag.getSGID()));
        builder = tag.getPrecedingSGID() != null ? builder.setPrecedingID(SGIDIO.m2pb(tag.getPrecedingSGID()))  : builder;
        builder.setDate(tag.getCreationTimeStamp().getTime());
        TagPB fMesg = builder.build();
        return fMesg;
    }
}
