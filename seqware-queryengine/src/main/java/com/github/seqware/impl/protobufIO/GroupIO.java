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
import com.github.seqware.dto.QueryEngine.GroupPB;
import com.github.seqware.factory.Factory;
import com.github.seqware.model.Group;
import com.github.seqware.model.User;
import com.github.seqware.model.impl.AtomImpl;
import com.github.seqware.model.impl.MoleculeImpl;
import com.github.seqware.model.impl.inMemory.InMemoryGroup;
import com.github.seqware.util.SGID;

/**
 *
 * @author dyuen
 */
public class GroupIO implements ProtobufTransferInterface<GroupPB, Group>{

    @Override
    public Group pb2m(GroupPB pb) {
        Group.Builder builder = InMemoryGroup.newBuilder();
        builder = pb.hasName() ? builder.setName(pb.getName()) : builder;
        builder = pb.hasDescription()  ? builder.setDescription(pb.getDescription()) : builder;
        Group user = builder.build();
        UtilIO.handlePB2Atom(pb.getAtom(), (AtomImpl)user);
        UtilIO.handlePB2ACL(pb.getAcl(), (MoleculeImpl)user);
        if (pb.hasPrecedingVersion()){
           user.setPrecedingVersion(pb2m(pb.getPrecedingVersion()));
        }
        for(SGIDPB refID : pb.getUsersList()){
            SGID sgid = SGIDIO.pb2m(refID);
            User ref = (User)Factory.getFeatureStoreInterface().getAtomBySGID(sgid);
            user.add(ref);
        }
        return user;
    }
    

    @Override
    public GroupPB m2pb(Group atom) {
        QueryEngine.GroupPB.Builder builder = QueryEngine.GroupPB.newBuilder();
        builder = atom.getName() != null ? builder.setName(atom.getName()) : builder;
        builder = atom.getDescription() != null ? builder.setDescription(atom.getDescription()) : builder;
        builder.setAtom(UtilIO.handleAtom2PB(builder.getAtom(), (AtomImpl)atom));
        builder.setAcl(UtilIO.handleACL2PB(builder.getAcl(), (MoleculeImpl)atom));
        if (atom.getPrecedingVersion() != null){
            builder.setPrecedingVersion(m2pb(atom.getPrecedingVersion()));
        }
        for(User ref : atom){
            builder.addUsers(SGIDIO.m2pb(ref.getSGID()));
        }
        GroupPB userpb = builder.build();
        return userpb;
    }
}
