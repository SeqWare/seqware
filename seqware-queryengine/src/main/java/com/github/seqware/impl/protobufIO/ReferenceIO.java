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
import com.github.seqware.dto.QueryEngine.ReferencePB;
import com.github.seqware.model.Reference;
import com.github.seqware.model.impl.AtomImpl;
import com.github.seqware.model.impl.MoleculeImpl;
import com.github.seqware.model.impl.inMemory.InMemoryReference;
import com.google.protobuf.InvalidProtocolBufferException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dyuen
 */
public class ReferenceIO implements ProtobufTransferInterface<ReferencePB, Reference>{

    @Override
    public Reference pb2m(ReferencePB userpb) {
        Reference.Builder builder = InMemoryReference.newBuilder();
        builder = userpb.hasName() ? builder.setName(userpb.getName()) : builder;
        Reference ref = builder.build();
        UtilIO.handlePB2Atom(userpb.getAtom(), (AtomImpl)ref);
        UtilIO.handlePB2ACL(userpb.getAcl(), (MoleculeImpl)ref);
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && userpb.hasPrecedingVersion()){
           ref.setPrecedingVersion(pb2m(userpb.getPrecedingVersion()));
        }
        return ref;
    }
    

    @Override
    public ReferencePB m2pb(Reference sgid) {
        QueryEngine.ReferencePB.Builder builder = QueryEngine.ReferencePB.newBuilder();
        builder = sgid.getName() != null ? builder.setName(sgid.getName()) : builder;
        builder.setAtom(UtilIO.handleAtom2PB(builder.getAtom(), (AtomImpl)sgid));
        builder.setAcl(UtilIO.handleACL2PB(builder.getAcl(), (MoleculeImpl)sgid));
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && sgid.getPrecedingVersion() != null){
            builder.setPrecedingVersion(m2pb(sgid.getPrecedingVersion()));
        }
        ReferencePB refpb = builder.build();
        return refpb;
    }

    @Override
    public Reference byteArr2m(byte[] arr) {
        try {
            QueryEngine.ReferencePB userpb = QueryEngine.ReferencePB.parseFrom(arr);
            return pb2m(userpb);
        } catch (InvalidProtocolBufferException ex) {
            Logger.getLogger(FeatureSetIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
