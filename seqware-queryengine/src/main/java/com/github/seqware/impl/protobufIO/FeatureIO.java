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

import com.github.seqware.dto.QueryEngine;
import com.github.seqware.dto.QueryEngine.FeaturePB;
import com.github.seqware.model.Feature;
import com.google.protobuf.InvalidProtocolBufferException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dyuen
 */
public class FeatureIO implements ProtobufTransferInterface<FeaturePB, Feature> {

    @Override
    public Feature pb2m(FeaturePB feature) {
        Feature.Builder builder = Feature.newBuilder();
        builder = feature.hasPragma() ? builder.setPragma(feature.getPragma()) : builder;
        builder = feature.hasSource() ? builder.setSource(feature.getSource()) : builder;
        builder = feature.hasType() ? builder.setType(feature.getType()) : builder;
        builder = feature.hasScore() ? builder.setScore(feature.getScore()) : builder;
        builder = feature.hasPhase() ? builder.setPhase(feature.getPhase()) : builder;
        builder = feature.hasId() ? builder.setId(feature.getId()) : builder;
        builder.setStart(feature.getStart()).setStop(feature.getStop());
        builder.setStrand(Feature.Strand.valueOf(feature.getStrand().name()));
        Feature fMesg = builder.build();
        UtilIO.handlePB2Atom(feature.getAtom(), fMesg);
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && feature.hasPrecedingVersion()){
           fMesg.setPrecedingVersion(pb2m(feature.getPrecedingVersion()));
        }
        return fMesg;
    }

    @Override
    public FeaturePB m2pb(Feature feature) {
        QueryEngine.FeaturePB.Builder builder = QueryEngine.FeaturePB.newBuilder();
        builder = feature.getPragma() != null ? builder.setPragma(feature.getPragma()) : builder;
        builder = feature.getSource() != null ? builder.setSource(feature.getSource()) : builder;
        builder = feature.getType() != null ? builder.setType(feature.getType()) : builder;
        builder = feature.getScore() != null ? builder.setScore(feature.getScore()) : builder;
        builder = feature.getPhase() != null ? builder.setPhase(feature.getPhase()) : builder;
        builder = feature.getId() != null ? builder.setId(feature.getId()) : builder;
        builder.setStart(feature.getStart()).setStop(feature.getStop());
        builder.setStrand(FeaturePB.StrandPB.valueOf(feature.getStrand().name()));
        builder.setAtom(UtilIO.handleAtom2PB(builder.getAtom(), feature));
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && feature.getPrecedingVersion() != null){
            builder.setPrecedingVersion(m2pb(feature.getPrecedingVersion()));
        }
        FeaturePB fMesg = builder.build();
        return fMesg;
    }

    @Override
    public Feature byteArr2m(byte[] arr) {
        try {
            FeaturePB userpb = FeaturePB.parseFrom(arr);
            return pb2m(userpb);
        } catch (InvalidProtocolBufferException ex) {
            Logger.getLogger(FeatureSetIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
}
