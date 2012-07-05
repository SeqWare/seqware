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
import com.github.seqware.queryengine.dto.QueryEngine.FeatureSetPB;
import com.github.seqware.queryengine.factory.Factory;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.impl.MoleculeImpl;
import com.github.seqware.queryengine.model.impl.inMemory.InMemoryFeatureSet;
import com.github.seqware.queryengine.util.FSGID;
import com.github.seqware.queryengine.util.SGID;
import com.google.protobuf.InvalidProtocolBufferException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dyuen
 */
public class FeatureSetIO implements ProtobufTransferInterface<FeatureSetPB, FeatureSet> {

    @Override
    public FeatureSet pb2m(FeatureSetPB userpb) {
        FeatureSet.Builder builder = InMemoryFeatureSet.newBuilder();
        builder = userpb.hasDescription() ? builder.setDescription(userpb.getDescription()) : builder;
        builder = userpb.hasReferenceID() ? builder.setReferenceID(SGIDIO.pb2m(userpb.getReferenceID())) : builder;
        FeatureSet user = builder.build();
        UtilIO.handlePB2Atom(userpb.getAtom(), (AtomImpl) user);
        UtilIO.handlePB2ACL(userpb.getAcl(), (MoleculeImpl) user);
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && userpb.hasPrecedingVersion()) {
            user.setPrecedingVersion(pb2m(userpb.getPrecedingVersion()));
        }
        SGID[] sgidArr = new SGID[userpb.getFeaturesCount()];
        for(int i = 0; i < sgidArr.length; i++){
            sgidArr[i] = (FSGIDIO.pb2m(userpb.getFeatures(i)));
        }
        List<Feature> atomsBySGID = Factory.getFeatureStoreInterface().getAtomsBySGID(Feature.class, sgidArr);
        if (atomsBySGID != null && atomsBySGID.size() > 0) {user.add(atomsBySGID);}
        return user;
    }

    @Override
    public FeatureSetPB m2pb(FeatureSet sgid) {
        QueryEngine.FeatureSetPB.Builder builder = QueryEngine.FeatureSetPB.newBuilder();
        builder = sgid.getDescription() != null ? builder.setDescription(sgid.getDescription()) : builder;
        builder = sgid.getReferenceID() != null ? builder.setReferenceID(SGIDIO.m2pb(sgid.getReferenceID())) : builder;
        builder.setAtom(UtilIO.handleAtom2PB(builder.getAtom(), (AtomImpl) sgid));
        builder.setAcl(UtilIO.handleACL2PB(builder.getAcl(), (MoleculeImpl) sgid));
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && sgid.getPrecedingVersion() != null) {
            builder.setPrecedingVersion(m2pb(sgid.getPrecedingVersion()));
        }
        for (Feature ref : sgid) {
            builder.addFeatures(FSGIDIO.m2pb((FSGID) ref.getSGID()));
        }
        FeatureSetPB userpb = builder.build();
        return userpb;
    }

    @Override
    public FeatureSet byteArr2m(byte[] arr) {
        try {
            FeatureSetPB userpb = FeatureSetPB.parseFrom(arr);
            return pb2m(userpb);
        } catch (InvalidProtocolBufferException ex) {
            Logger.getLogger(FeatureSetIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
