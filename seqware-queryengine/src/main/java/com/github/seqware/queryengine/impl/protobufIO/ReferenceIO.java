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
import com.github.seqware.queryengine.dto.QueryEngine.ReferencePB;
import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.impl.MoleculeImpl;
import com.github.seqware.queryengine.model.impl.inMemory.InMemoryReference;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.log4j.Logger;

/**
 * <p>ReferenceIO class.</p>
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class ReferenceIO implements ProtobufTransferInterface<ReferencePB, Reference>{

    /** {@inheritDoc} */
    @Override
    public Reference pb2m(ReferencePB userpb) {
        Reference.Builder builder = InMemoryReference.newBuilder();
        builder = userpb.hasName() ? builder.setName(userpb.getName()) : builder;
        Reference ref = builder.build();
        UtilIO.handlePB2Atom(userpb.getAtom(), (AtomImpl)ref);
        UtilIO.handlePB2Mol(userpb.getMol(), (MoleculeImpl)ref);
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && userpb.hasPrecedingVersion()){
           ref.setPrecedingVersion(pb2m(userpb.getPrecedingVersion()));
        }
        return ref;
    }
    

    /** {@inheritDoc} */
    @Override
    public ReferencePB m2pb(Reference sgid) {
        QueryEngine.ReferencePB.Builder builder = QueryEngine.ReferencePB.newBuilder();
        builder = sgid.getName() != null ? builder.setName(sgid.getName()) : builder;
        builder.setAtom(UtilIO.handleAtom2PB(builder.getAtom(), (AtomImpl)sgid));
        builder.setMol(UtilIO.handleMol2PB(builder.getMol(), (MoleculeImpl)sgid));
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && sgid.getPrecedingVersion() != null){
            builder.setPrecedingVersion(m2pb(sgid.getPrecedingVersion()));
        }
        ReferencePB refpb = builder.build();
        return refpb;
    }

    /** {@inheritDoc} */
    @Override
    public Reference byteArr2m(byte[] arr) {
        try {
            QueryEngine.ReferencePB userpb = QueryEngine.ReferencePB.parseFrom(arr);
            return pb2m(userpb);
        } catch (InvalidProtocolBufferException ex) {
            Logger.getLogger(FeatureSetIO.class.getName()).fatal( "Invalid PB", ex);
        }
        return null;
    }
}
