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
import com.github.seqware.dto.QESupporting.FSGIDPB;
import com.github.seqware.util.FSGID;
import java.util.UUID;

/**
 *
 * @author dyuen
 */
public class FSGIDIO {

    public static FSGID pb2m(FSGIDPB pb) {
        FSGID sgid = new FSGID();
        sgid.setUuid(new UUID(pb.getSgid().getMostSigBits(), pb.getSgid().getLeastSigBits()));
        sgid.setRowKey(pb.getRowKey());
//        sgid.setFeatureSetID(new SGID(pb.getFeatureSetID().getMostSigBits(), pb.getFeatureSetID().getLeastSigBits()));
        return sgid;
    }

    public static FSGIDPB m2pb(FSGID sgid) {
        QESupporting.FSGIDPB.Builder builder = QESupporting.FSGIDPB.newBuilder();
        builder.setSgid(SGIDIO.m2pb(sgid));
        builder.setRowKey((sgid).getRowKey());
//        builder.setFeatureSetID(SGIDIO.m2pb((sgid).getFeatureSetID()));
        FSGIDPB fMesg = builder.build();
        return fMesg;
    }
}
