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
import com.github.seqware.queryengine.dto.QueryEngine.AnalysisSetPB;
import com.github.seqware.queryengine.factory.Factory;
import com.github.seqware.queryengine.model.Analysis;
import com.github.seqware.queryengine.model.AnalysisSet;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.impl.MoleculeImpl;
import com.github.seqware.queryengine.model.impl.inMemory.InMemoryAnalysisSet;
import com.github.seqware.queryengine.util.SGID;
import com.google.protobuf.InvalidProtocolBufferException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dyuen
 */
public class AnalysisSetIO implements ProtobufTransferInterface<AnalysisSetPB, AnalysisSet>{

    @Override
    public AnalysisSet pb2m(AnalysisSetPB pb) {
        AnalysisSet.Builder builder = InMemoryAnalysisSet.newBuilder();
        builder = pb.hasName() ? builder.setName(pb.getName()) : builder;
        builder = pb.hasDescription() ? builder.setDescription(pb.getDescription()) : builder;
        AnalysisSet user = builder.build();
        UtilIO.handlePB2Atom(pb.getAtom(), (AtomImpl)user);
        UtilIO.handlePB2Mol(pb.getMol(), (MoleculeImpl)user);
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && pb.hasPrecedingVersion()){
           user.setPrecedingVersion(pb2m(pb.getPrecedingVersion()));
        }
        SGID[] sgidArr = new SGID[pb.getAnalysisIDsCount()];
        for(int i = 0; i < sgidArr.length; i++){
            sgidArr[i] = (SGIDIO.pb2m(pb.getAnalysisIDs(i)));
        }
        List<Analysis> atomsBySGID = Factory.getFeatureStoreInterface().getAtomsBySGID(Analysis.class, sgidArr);
        if (atomsBySGID != null && atomsBySGID.size() > 0) {user.add(atomsBySGID);}
        return user;
    }
    

    @Override
    public AnalysisSetPB m2pb(AnalysisSet aSet) {
        QueryEngine.AnalysisSetPB.Builder builder = QueryEngine.AnalysisSetPB.newBuilder();
        builder = aSet.getName() != null ? builder.setName(aSet.getName()) : builder;
        builder = aSet.getDescription() != null ? builder.setDescription(aSet.getDescription()) : builder;
        builder.setAtom(UtilIO.handleAtom2PB(builder.getAtom(), (AtomImpl)aSet));
        builder.setMol(UtilIO.handleMol2PB(builder.getMol(), (MoleculeImpl)aSet));
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && aSet.getPrecedingVersion() != null){
            builder.setPrecedingVersion(m2pb(aSet.getPrecedingVersion()));
        }
        for(Analysis ref : aSet){
            builder.addAnalysisIDs(SGIDIO.m2pb(ref.getSGID()));
        }
        AnalysisSetPB userpb = builder.build();
        return userpb;
    }

    @Override
    public AnalysisSet byteArr2m(byte[] arr) {
        try {
            QueryEngine.AnalysisSetPB userpb = QueryEngine.AnalysisSetPB.parseFrom(arr);
            return pb2m(userpb);
        } catch (InvalidProtocolBufferException ex) {
            Logger.getLogger(FeatureSetIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
