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

import com.github.seqware.dto.QESupporting.SGIDPB;
import com.github.seqware.dto.QueryEngine;
import com.github.seqware.dto.QueryEngine.ReferenceSetPB;
import com.github.seqware.factory.Factory;
import com.github.seqware.model.Reference;
import com.github.seqware.model.ReferenceSet;
import com.github.seqware.model.impl.AtomImpl;
import com.github.seqware.model.impl.MoleculeImpl;
import com.github.seqware.model.impl.inMemory.InMemoryReferenceSet;
import com.github.seqware.util.SGID;

/**
 *
 * @author dyuen
 */
public class ReferenceSetIO implements ProtobufTransferInterface<ReferenceSetPB, ReferenceSet>{

    @Override
    public ReferenceSet pb2m(ReferenceSetPB userpb) {
        ReferenceSet.Builder builder = InMemoryReferenceSet.newBuilder();
        builder = userpb.hasName() ? builder.setName(userpb.getName()) : builder;
        builder = userpb.hasOrganism()  ? builder.setOrganism(userpb.getOrganism()) : builder;
        ReferenceSet user = builder.build();
        UtilIO.handlePB2Atom(userpb.getAtom(), (AtomImpl)user);
        UtilIO.handlePB2ACL(userpb.getAcl(), (MoleculeImpl)user);
        if (userpb.hasPrecedingVersion()){
           user.setPrecedingVersion(pb2m(userpb.getPrecedingVersion()));
        }
        for(SGIDPB refID : userpb.getReferenceIDsList()){
            SGID sgid = SGIDIO.pb2m(refID);
            Reference ref = (Reference)Factory.getFeatureStoreInterface().getAtomBySGID(sgid);
            user.add(ref);
        }
        return user;
    }
    

    @Override
    public ReferenceSetPB m2pb(ReferenceSet sgid) {
        QueryEngine.ReferenceSetPB.Builder builder = QueryEngine.ReferenceSetPB.newBuilder();
        builder = sgid.getName() != null ? builder.setName(sgid.getName()) : builder;
        builder = sgid.getOrganism() != null ? builder.setOrganism(sgid.getOrganism()) : builder;
        builder.setAtom(UtilIO.handleAtom2PB(builder.getAtom(), (AtomImpl)sgid));
        builder.setAcl(UtilIO.handleACL2PB(builder.getAcl(), (MoleculeImpl)sgid));
        if (sgid.getPrecedingVersion() != null){
            builder.setPrecedingVersion(m2pb(sgid.getPrecedingVersion()));
        }
        for(Reference ref : sgid){
            builder.addReferenceIDs(SGIDIO.m2pb(ref.getSGID()));
        }
        ReferenceSetPB userpb = builder.build();
        return userpb;
    }
}
