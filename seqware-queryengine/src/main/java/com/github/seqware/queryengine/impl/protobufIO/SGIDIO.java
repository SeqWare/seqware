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
import com.github.seqware.queryengine.dto.QESupporting.SGIDPB;
import com.github.seqware.queryengine.util.SGID;

/**
 *
 * @author dyuen
 */
public class SGIDIO {

    public static SGID pb2m(SGIDPB sgidpg) {
        return new SGID(sgidpg.getMostSigBits(), sgidpg.getLeastSigBits(), sgidpg.getTimestamp());
    }

    public static SGIDPB m2pb(SGID sgid) {
        QESupporting.SGIDPB.Builder builder = QESupporting.SGIDPB.newBuilder().setLeastSigBits(sgid.getUuid().getLeastSignificantBits());
        builder.setMostSigBits(sgid.getUuid().getMostSignificantBits());
        if (sgid.getBackendTimestamp() != null) {
            builder.setTimestamp(sgid.getBackendTimestamp().getTime());
        }
        SGIDPB fMesg = builder.build();
        return fMesg;
    }
}
