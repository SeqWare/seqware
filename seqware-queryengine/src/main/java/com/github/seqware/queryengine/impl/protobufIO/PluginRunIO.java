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
import com.github.seqware.queryengine.dto.QueryEngine.PluginRunPB;
import com.github.seqware.queryengine.model.PluginRun;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.log4j.Logger;

/**
 * Place-holder
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class PluginRunIO implements ProtobufTransferInterface<PluginRunPB, PluginRun>{

    /** {@inheritDoc} */
    @Override
    public PluginRun pb2m(PluginRunPB pb) {
        PluginRun.Builder builder = PluginRun.newBuilder();
//        builder = pb.hasFirstName() ? builder.setFirstName(pb.getFirstName()) : builder;
//        builder = pb.hasLastName()  ? builder.setLastName(pb.getFirstName()) : builder;
//        builder = pb.hasEmailAddress() ? builder.setEmailAddress(pb.getEmailAddress()) : builder;
//        builder = pb.hasPassword() ? builder.setPassword(pb.getPassword()) : builder;
        PluginRun user = builder.build();
        UtilIO.handlePB2Atom(pb.getAtom(), user);
        UtilIO.handlePB2Mol(pb.getMol(), user);
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && pb.hasPrecedingVersion()){
           user.setPrecedingVersion(pb2m(pb.getPrecedingVersion()));
        }
        return user;
    }
    

    /** {@inheritDoc} */
    @Override
    public PluginRunPB m2pb(PluginRun atom) {
        QueryEngine.PluginRunPB.Builder builder = QueryEngine.PluginRunPB.newBuilder();
//        builder = atom.getFirstName() != null ? builder.setFirstName(atom.getFirstName()) : builder;
//        builder = atom.getLastName() != null ? builder.setLastName(atom.getFirstName()) : builder;
//        builder = atom.getEmailAddress() != null ? builder.setEmailAddress(atom.getEmailAddress()) : builder;
//        builder = atom.getPassword() != null ? builder.setPassword(atom.getPassword()) : builder;
        builder.setAtom(UtilIO.handleAtom2PB(builder.getAtom(), atom));
        builder.setMol(UtilIO.handleMol2PB(builder.getMol(), atom));
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && atom.getPrecedingVersion() != null){
            builder.setPrecedingVersion(m2pb((PluginRun)atom.getPrecedingVersion()));
        }
        PluginRunPB userpb = builder.build();
        return userpb;
    }
    
    /** {@inheritDoc} */
    @Override
    public PluginRun byteArr2m(byte[] arr) {
        try {
            QueryEngine.PluginRunPB userpb = QueryEngine.PluginRunPB.parseFrom(arr);
            return pb2m(userpb);
        } catch (InvalidProtocolBufferException ex) {
            Logger.getLogger(FeatureSetIO.class.getName()).fatal("Invalid PB found for Plugin", ex);
        }
        return null;
    }
}
