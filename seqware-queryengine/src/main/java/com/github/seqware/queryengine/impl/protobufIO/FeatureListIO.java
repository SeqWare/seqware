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

import com.github.seqware.queryengine.dto.QueryEngine;
import com.github.seqware.queryengine.dto.QueryEngine.FeatureListPB;
import com.github.seqware.queryengine.dto.QueryEngine.FeaturePB;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.impl.FeatureList;
import com.github.seqware.queryengine.util.FSGID;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.log4j.Logger;

/**
 * <p>FeatureListIO class.</p>
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class FeatureListIO implements ProtobufTransferInterface<FeatureListPB, FeatureList> {

    private FeatureIO featureIO = new FeatureIO();
    
    /** {@inheritDoc} */
    @Override
    public FeatureList pb2m(FeatureListPB featureList) {
        FeatureList.Builder builder = FeatureList.newBuilder();
        FeatureList fMesg = builder.build();
        assert(featureList.getFeaturesCount() > 0);
        for(FeaturePB fpb: featureList.getFeaturesList()){
            fMesg.add(featureIO.pb2m(fpb));
        }
        UtilIO.handlePB2Atom(featureList.getAtom(), fMesg);
//        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && featureList.hasPrecedingVersion()){
//           fMesg.setPrecedingVersion(pb2m(feature.getPrecedingVersion()));
//        }
        // upgrade SGID on the way out based on the first feature, this is an optimization to save space
        // so that the FeatureList does not have to store a full FSGID, just a SGID
        FSGID fsgid = new FSGID(fMesg.getSGID(), (FSGID)fMesg.getFeatures().get(0).getSGID());
        fMesg.impersonate(fsgid, fMesg.getPrecedingSGID());
        return fMesg;
    }

    /** {@inheritDoc} */
    @Override
    public FeatureListPB m2pb(FeatureList featureList) {
        assert(featureList.getSGID() instanceof FSGID);
        QueryEngine.FeatureListPB.Builder builder = QueryEngine.FeatureListPB.newBuilder();
        builder.setAtom(UtilIO.handleAtom2PB(builder.getAtom(), featureList));
        assert(featureList.getFeatures().size() > 0);
        for(Feature fpb: featureList.getFeatures()){
            builder.addFeatures(featureIO.m2pb(fpb));
        }
        FeatureListPB fMesg = builder.build();
        return fMesg;
    }

    /** {@inheritDoc} */
    @Override
    public FeatureList byteArr2m(byte[] arr) {
        try {
            FeatureListPB userpb = FeatureListPB.parseFrom(arr);
            return pb2m(userpb);
        } catch (InvalidProtocolBufferException ex) {
            Logger.getLogger(FeatureSetIO.class.getName()).fatal( "Invalid PB", ex);
        }
        return null;
    }
    
    
}
