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

import com.github.seqware.queryengine.dto.QESupporting.SGIDPB;
import com.github.seqware.queryengine.dto.QueryEngine;
import com.github.seqware.queryengine.dto.QueryEngine.GroupPB;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.Group;
import com.github.seqware.queryengine.model.User;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.impl.MoleculeImpl;
import com.github.seqware.queryengine.model.impl.inMemory.InMemoryGroup;
import com.github.seqware.queryengine.util.SGID;
import com.google.protobuf.InvalidProtocolBufferException;
import java.util.List;
import org.apache.log4j.Logger;

;

/**
 * <p>GroupIO class.</p>
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class GroupIO implements ProtobufTransferInterface<GroupPB, Group>{

    /** {@inheritDoc} */
    @Override
    public Group pb2m(GroupPB pb) {
        Group.Builder builder = InMemoryGroup.newBuilder();
        builder = pb.hasName() ? builder.setName(pb.getName()) : builder;
        builder = pb.hasDescription()  ? builder.setDescription(pb.getDescription()) : builder;
        Group user = builder.build();
        UtilIO.handlePB2Atom(pb.getAtom(), (AtomImpl)user);
        UtilIO.handlePB2Mol(pb.getMol(), (MoleculeImpl)user);
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && pb.hasPrecedingVersion()){
           user.setPrecedingVersion(pb2m(pb.getPrecedingVersion()));
        }
        SGID[] sgidArr = new SGID[pb.getUsersCount()];
        for(int i = 0; i < sgidArr.length; i++){
            sgidArr[i] = (SGIDIO.pb2m(pb.getUsers(i)));
        }
        List<User> atomsBySGID = SWQEFactory.getQueryInterface().getAtomsBySGID(User.class, sgidArr);
        if (atomsBySGID != null && atomsBySGID.size() > 0) {user.add(atomsBySGID);}
        return user;
    }
    

    /** {@inheritDoc} */
    @Override
    public GroupPB m2pb(Group atom) {
        QueryEngine.GroupPB.Builder builder = QueryEngine.GroupPB.newBuilder();
        builder = atom.getName() != null ? builder.setName(atom.getName()) : builder;
        builder = atom.getDescription() != null ? builder.setDescription(atom.getDescription()) : builder;
        builder.setAtom(UtilIO.handleAtom2PB(builder.getAtom(), (AtomImpl)atom));
        builder.setMol(UtilIO.handleMol2PB(builder.getMol(), (MoleculeImpl)atom));
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && atom.getPrecedingVersion() != null){
            builder.setPrecedingVersion(m2pb(atom.getPrecedingVersion()));
        }
        for(User ref : atom){
            builder.addUsers(SGIDIO.m2pb(ref.getSGID()));
        }
        GroupPB userpb = builder.build();
        return userpb;
    }

    /** {@inheritDoc} */
    @Override
    public Group byteArr2m(byte[] arr) {
        try {
            QueryEngine.GroupPB userpb = QueryEngine.GroupPB.parseFrom(arr);
            return pb2m(userpb);
        } catch (InvalidProtocolBufferException ex) {
            Logger.getLogger(FeatureSetIO.class.getName()).fatal( "Invalid PB", ex);
        }
        return null;
    }
}
