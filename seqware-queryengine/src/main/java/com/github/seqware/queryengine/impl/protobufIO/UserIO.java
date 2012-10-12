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
import com.github.seqware.queryengine.dto.QueryEngine.UserPB;
import com.github.seqware.queryengine.model.User;
import com.github.seqware.queryengine.model.impl.MoleculeImpl;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.log4j.Logger;

/**
 * <p>UserIO class.</p>
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class UserIO implements ProtobufTransferInterface<UserPB, User>{

    /** {@inheritDoc} */
    @Override
    public User pb2m(UserPB userpb) {
        User.Builder builder = User.newBuilder();
        builder = userpb.hasFirstName() ? builder.setFirstName(userpb.getFirstName()) : builder;
        builder = userpb.hasLastName()  ? builder.setLastName(userpb.getLastName()) : builder;
        builder = userpb.hasEmailAddress() ? builder.setEmailAddress(userpb.getEmailAddress()) : builder;
        builder = userpb.hasPassword() ? builder.setPasswordWithoutHash(userpb.getPassword()) : builder;
        User user = builder.build();
        UtilIO.handlePB2Atom(userpb.getAtom(), user);
        UtilIO.handlePB2Mol(userpb.getMol(), user);
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && userpb.hasPrecedingVersion()){
           user.setPrecedingVersion(pb2m(userpb.getPrecedingVersion()));
        }
        return user;
    }
    

    /** {@inheritDoc} */
    @Override
    public UserPB m2pb(User sgid) {
        QueryEngine.UserPB.Builder builder = QueryEngine.UserPB.newBuilder();
        builder = sgid.getFirstName() != null ? builder.setFirstName(sgid.getFirstName()) : builder;
        builder = sgid.getLastName() != null ? builder.setLastName(sgid.getLastName()) : builder;
        builder = sgid.getEmailAddress() != null ? builder.setEmailAddress(sgid.getEmailAddress()) : builder;
        builder = sgid.getPassword() != null ? builder.setPassword(sgid.getPassword()) : builder;
        builder.setAtom(UtilIO.handleAtom2PB(builder.getAtom(), sgid));
        builder.setMol(UtilIO.handleMol2PB(builder.getMol(), (MoleculeImpl)sgid));
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && sgid.getPrecedingVersion() != null){
            builder.setPrecedingVersion(m2pb(sgid.getPrecedingVersion()));
        }
        UserPB userpb = builder.build();
        return userpb;
    }

    /** {@inheritDoc} */
    @Override
    public User byteArr2m(byte[] arr) {
        try {
            UserPB userpb = UserPB.parseFrom(arr);
            return pb2m(userpb);
        } catch (InvalidProtocolBufferException ex) {
            Logger.getLogger(FeatureSetIO.class.getName()).fatal( "Invalid PB", ex);
        }
        return null;
    }
}
