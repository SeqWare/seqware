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
import com.github.seqware.dto.QueryEngine.UserPB;
import com.github.seqware.model.User;
import com.google.protobuf.InvalidProtocolBufferException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dyuen
 */
public class UserIO implements ProtobufTransferInterface<UserPB, User>{

    @Override
    public User pb2m(UserPB userpb) {
        User.Builder builder = User.newBuilder();
        builder = userpb.hasFirstName() ? builder.setFirstName(userpb.getFirstName()) : builder;
        builder = userpb.hasLastName()  ? builder.setLastName(userpb.getLastName()) : builder;
        builder = userpb.hasEmailAddress() ? builder.setEmailAddress(userpb.getEmailAddress()) : builder;
        builder = userpb.hasPassword() ? builder.setPasswordWithoutHash(userpb.getPassword()) : builder;
        User user = builder.build();
        UtilIO.handlePB2Atom(userpb.getAtom(), user);
        UtilIO.handlePB2ACL(userpb.getAcl(), user);
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && userpb.hasPrecedingVersion()){
           user.setPrecedingVersion(pb2m(userpb.getPrecedingVersion()));
        }
        return user;
    }
    

    @Override
    public UserPB m2pb(User sgid) {
        QueryEngine.UserPB.Builder builder = QueryEngine.UserPB.newBuilder();
        builder = sgid.getFirstName() != null ? builder.setFirstName(sgid.getFirstName()) : builder;
        builder = sgid.getLastName() != null ? builder.setLastName(sgid.getLastName()) : builder;
        builder = sgid.getEmailAddress() != null ? builder.setEmailAddress(sgid.getEmailAddress()) : builder;
        builder = sgid.getPassword() != null ? builder.setPassword(sgid.getPassword()) : builder;
        builder.setAtom(UtilIO.handleAtom2PB(builder.getAtom(), sgid));
        builder.setAcl(UtilIO.handleACL2PB(builder.getAcl(), sgid));
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && sgid.getPrecedingVersion() != null){
            builder.setPrecedingVersion(m2pb(sgid.getPrecedingVersion()));
        }
        UserPB userpb = builder.build();
        return userpb;
    }

    @Override
    public User byteArr2m(byte[] arr) {
        try {
            UserPB userpb = UserPB.parseFrom(arr);
            return pb2m(userpb);
        } catch (InvalidProtocolBufferException ex) {
            Logger.getLogger(FeatureSetIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
