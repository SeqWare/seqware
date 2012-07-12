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
import com.github.seqware.queryengine.dto.QESupporting.FSGIDPB;
import com.github.seqware.queryengine.util.FSGID;

/**
 *
 * @author dyuen
 */
public class FSGIDIO {

    public static FSGID pb2m(FSGIDPB pb) {  
        FSGID sgid = new FSGID(pb.getSgid().getMostSigBits(), pb.getSgid().getLeastSigBits(), pb.getSgid().getTimestamp(), pb.getRowKey(), pb.getRefName());
        return sgid;
    }

    public static FSGIDPB m2pb(FSGID sgid) {
        QESupporting.FSGIDPB.Builder builder = QESupporting.FSGIDPB.newBuilder();
        builder.setSgid(SGIDIO.m2pb(sgid));
        builder.setRowKey((sgid).getRowKey());
        builder.setRefName(sgid.getReferenceName());
        FSGIDPB fMesg = builder.build();
        return fMesg;
    }
}
