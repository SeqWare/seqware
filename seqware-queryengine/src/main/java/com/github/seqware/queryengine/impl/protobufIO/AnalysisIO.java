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
import com.github.seqware.queryengine.dto.QueryEngine.AnalysisPB;
import com.github.seqware.queryengine.model.Analysis;
import com.github.seqware.queryengine.model.impl.inMemory.InMemoryQueryFutureImpl;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.log4j.Logger;

/**
 * Place-holder
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class AnalysisIO implements ProtobufTransferInterface<AnalysisPB, Analysis>{

    /** {@inheritDoc} */
    @Override
    public Analysis pb2m(AnalysisPB pb) {
        Analysis.Builder builder = InMemoryQueryFutureImpl.newBuilder();
//        builder = pb.hasFirstName() ? builder.setFirstName(pb.getFirstName()) : builder;
//        builder = pb.hasLastName()  ? builder.setLastName(pb.getFirstName()) : builder;
//        builder = pb.hasEmailAddress() ? builder.setEmailAddress(pb.getEmailAddress()) : builder;
//        builder = pb.hasPassword() ? builder.setPassword(pb.getPassword()) : builder;
        Analysis user = builder.build();
        UtilIO.handlePB2Atom(pb.getAtom(), user);
        UtilIO.handlePB2Mol(pb.getMol(), user);
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && pb.hasPrecedingVersion()){
           user.setPrecedingVersion(pb2m(pb.getPrecedingVersion()));
        }
        return user;
    }
    

    /** {@inheritDoc} */
    @Override
    public AnalysisPB m2pb(Analysis atom) {
        QueryEngine.AnalysisPB.Builder builder = QueryEngine.AnalysisPB.newBuilder();
//        builder = atom.getFirstName() != null ? builder.setFirstName(atom.getFirstName()) : builder;
//        builder = atom.getLastName() != null ? builder.setLastName(atom.getFirstName()) : builder;
//        builder = atom.getEmailAddress() != null ? builder.setEmailAddress(atom.getEmailAddress()) : builder;
//        builder = atom.getPassword() != null ? builder.setPassword(atom.getPassword()) : builder;
        builder.setAtom(UtilIO.handleAtom2PB(builder.getAtom(), atom));
        builder.setMol(UtilIO.handleMol2PB(builder.getMol(), atom));
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && atom.getPrecedingVersion() != null){
            builder.setPrecedingVersion(m2pb((Analysis)atom.getPrecedingVersion()));
        }
        AnalysisPB userpb = builder.build();
        return userpb;
    }
    
    /** {@inheritDoc} */
    @Override
    public Analysis byteArr2m(byte[] arr) {
        try {
            QueryEngine.AnalysisPB userpb = QueryEngine.AnalysisPB.parseFrom(arr);
            return pb2m(userpb);
        } catch (InvalidProtocolBufferException ex) {
            Logger.getLogger(FeatureSetIO.class.getName()).fatal("Invalid PB found for Analysis", ex);
        }
        return null;
    }
}
