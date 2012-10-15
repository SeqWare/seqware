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
import com.github.seqware.queryengine.dto.QueryEngine.ReferenceSetPB;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.Reference;
import com.github.seqware.queryengine.model.ReferenceSet;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.impl.MoleculeImpl;
import com.github.seqware.queryengine.model.impl.inMemory.InMemoryReferenceSet;
import com.github.seqware.queryengine.util.SGID;
import com.google.protobuf.InvalidProtocolBufferException;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * <p>ReferenceSetIO class.</p>
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class ReferenceSetIO implements ProtobufTransferInterface<ReferenceSetPB, ReferenceSet>{

    /** {@inheritDoc} */
    @Override
    public ReferenceSet pb2m(ReferenceSetPB userpb) {
        ReferenceSet.Builder builder = InMemoryReferenceSet.newBuilder();
        builder = userpb.hasName() ? builder.setName(userpb.getName()) : builder;
        builder = userpb.hasOrganism()  ? builder.setOrganism(userpb.getOrganism()) : builder;
        ReferenceSet user = builder.build();
        UtilIO.handlePB2Atom(userpb.getAtom(), (AtomImpl)user);
        UtilIO.handlePB2Mol(userpb.getMol(), (MoleculeImpl)user);
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && userpb.hasPrecedingVersion()){
           user.setPrecedingVersion(pb2m(userpb.getPrecedingVersion()));
        }
        SGID[] sgidArr = new SGID[userpb.getReferenceIDsCount()];
        for(int i = 0; i < sgidArr.length; i++){
            sgidArr[i] = (SGIDIO.pb2m(userpb.getReferenceIDs(i)));
        }
        List<Reference> atomsBySGID = SWQEFactory.getQueryInterface().getAtomsBySGID(Reference.class, sgidArr);
        if (atomsBySGID != null && atomsBySGID.size() > 0) {user.add(atomsBySGID);}
        return user;
    }
    

    /** {@inheritDoc} */
    @Override
    public ReferenceSetPB m2pb(ReferenceSet sgid) {
        QueryEngine.ReferenceSetPB.Builder builder = QueryEngine.ReferenceSetPB.newBuilder();
        builder = sgid.getName() != null ? builder.setName(sgid.getName()) : builder;
        builder = sgid.getOrganism() != null ? builder.setOrganism(sgid.getOrganism()) : builder;
        builder.setAtom(UtilIO.handleAtom2PB(builder.getAtom(), (AtomImpl)sgid));
        builder.setMol(UtilIO.handleMol2PB(builder.getMol(), (MoleculeImpl)sgid));
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && sgid.getPrecedingVersion() != null){
            builder.setPrecedingVersion(m2pb(sgid.getPrecedingVersion()));
        }
        for(Reference ref : sgid){
            builder.addReferenceIDs(SGIDIO.m2pb(ref.getSGID()));
        }
        ReferenceSetPB userpb = builder.build();
        return userpb;
    }

    /** {@inheritDoc} */
    @Override
    public ReferenceSet byteArr2m(byte[] arr) {
        try {
            QueryEngine.ReferenceSetPB userpb = QueryEngine.ReferenceSetPB.parseFrom(arr);
            return pb2m(userpb);
        } catch (InvalidProtocolBufferException ex) {
            Logger.getLogger(FeatureSetIO.class.getName()).fatal( "Invalid PB", ex);
        }
        return null;
    }
}
