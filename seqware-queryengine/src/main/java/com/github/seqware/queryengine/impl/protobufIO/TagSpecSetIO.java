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
import com.github.seqware.queryengine.dto.QueryEngine.TagSpecSetPB;
import com.github.seqware.queryengine.factory.Factory;
import com.github.seqware.queryengine.model.TagSpec;
import com.github.seqware.queryengine.model.TagSpecSet;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.impl.MoleculeImpl;
import com.github.seqware.queryengine.model.impl.inMemory.InMemoryTagSpecSet;
import com.github.seqware.queryengine.util.SGID;
import com.google.protobuf.InvalidProtocolBufferException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dyuen
 */
public class TagSpecSetIO implements ProtobufTransferInterface<TagSpecSetPB, TagSpecSet>{

    @Override
    public TagSpecSet pb2m(TagSpecSetPB userpb) {
        TagSpecSet.Builder builder = InMemoryTagSpecSet.newBuilder();
        builder = userpb.hasName() ? builder.setName(userpb.getName()) : builder;
        TagSpecSet user = builder.build();
        UtilIO.handlePB2Atom(userpb.getAtom(), (AtomImpl)user);
        UtilIO.handlePB2Mol(userpb.getMol(), (MoleculeImpl)user);
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && userpb.hasPrecedingVersion()){
           user.setPrecedingVersion(pb2m(userpb.getPrecedingVersion()));
        }
        SGID[] sgidArr = new SGID[userpb.getTagSpecIDsCount()];
        for(int i = 0; i < sgidArr.length; i++){
            sgidArr[i] = (SGIDIO.pb2m(userpb.getTagSpecIDs(i)));
        }
        List<TagSpec> atomsBySGID = Factory.getFeatureStoreInterface().getAtomsBySGID(TagSpec.class, sgidArr);
        if (atomsBySGID != null && atomsBySGID.size() > 0) {user.add(atomsBySGID);}
        return user;
    }
    

    @Override
    public TagSpecSetPB m2pb(TagSpecSet sgid) {
        QueryEngine.TagSpecSetPB.Builder builder = QueryEngine.TagSpecSetPB.newBuilder();
        builder = sgid.getName() != null ? builder.setName(sgid.getName()) : builder;
        builder.setAtom(UtilIO.handleAtom2PB(builder.getAtom(), (AtomImpl)sgid));
        builder.setMol(UtilIO.handleMol2PB(builder.getMol(), (MoleculeImpl)sgid));
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && sgid.getPrecedingVersion() != null){
            builder.setPrecedingVersion(m2pb(sgid.getPrecedingVersion()));
        }
        for(TagSpec ref : sgid){
            builder.addTagSpecIDs(SGIDIO.m2pb(ref.getSGID()));
        }
        TagSpecSetPB userpb = builder.build();
        return userpb;
    }

    @Override
    public TagSpecSet byteArr2m(byte[] arr) {
        try {
            QueryEngine.TagSpecSetPB userpb = QueryEngine.TagSpecSetPB.parseFrom(arr);
            return pb2m(userpb);
        } catch (InvalidProtocolBufferException ex) {
            Logger.getLogger(FeatureSetIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
