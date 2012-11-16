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
import com.github.seqware.queryengine.dto.QueryEngine.PluginPB;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.Plugin;
import com.github.seqware.queryengine.model.PluginRun;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.impl.MoleculeImpl;
import com.github.seqware.queryengine.model.impl.inMemory.InMemoryPlugin;
import com.github.seqware.queryengine.util.SGID;
import com.google.protobuf.InvalidProtocolBufferException;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * <p>PluginIO class.</p>
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class PluginIO implements ProtobufTransferInterface<PluginPB, Plugin>{

    /** {@inheritDoc} */
    @Override
    public Plugin pb2m(PluginPB pb) {
        Plugin.Builder builder = InMemoryPlugin.newBuilder();
        builder = pb.hasName() ? builder.setName(pb.getName()) : builder;
        builder = pb.hasDescription() ? builder.setDescription(pb.getDescription()) : builder;
        Plugin user = builder.build();
        UtilIO.handlePB2Atom(pb.getAtom(), (AtomImpl)user);
        UtilIO.handlePB2Mol(pb.getMol(), (MoleculeImpl)user);
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && pb.hasPrecedingVersion()){
           user.setPrecedingVersion(pb2m(pb.getPrecedingVersion()));
        }
        SGID[] sgidArr = new SGID[pb.getPluginRunIDsCount()];
        for(int i = 0; i < sgidArr.length; i++){
            sgidArr[i] = (SGIDIO.pb2m(pb.getPluginRunIDs(i)));
        }
        List<PluginRun> atomsBySGID = SWQEFactory.getQueryInterface().getAtomsBySGID(PluginRun.class, sgidArr);
        if (atomsBySGID != null && atomsBySGID.size() > 0) {user.add(atomsBySGID);}
        return user;
    }
    

    /** {@inheritDoc} */
    @Override
    public PluginPB m2pb(Plugin aSet) {
        QueryEngine.PluginPB.Builder builder = QueryEngine.PluginPB.newBuilder();
        builder = aSet.getName() != null ? builder.setName(aSet.getName()) : builder;
        builder = aSet.getDescription() != null ? builder.setDescription(aSet.getDescription()) : builder;
        builder.setAtom(UtilIO.handleAtom2PB(builder.getAtom(), (AtomImpl)aSet));
        builder.setMol(UtilIO.handleMol2PB(builder.getMol(), (MoleculeImpl)aSet));
        if (ProtobufTransferInterface.PERSIST_VERSION_CHAINS && aSet.getPrecedingVersion() != null){
            builder.setPrecedingVersion(m2pb(aSet.getPrecedingVersion()));
        }
        for(PluginRun ref : aSet){
            builder.addPluginRunIDs(SGIDIO.m2pb(ref.getSGID()));
        }
        PluginPB userpb = builder.build();
        return userpb;
    }

    /** {@inheritDoc} */
    @Override
    public Plugin byteArr2m(byte[] arr) {
        try {
            QueryEngine.PluginPB userpb = QueryEngine.PluginPB.parseFrom(arr);
            return pb2m(userpb);
        } catch (InvalidProtocolBufferException ex) {
            Logger.getLogger(FeatureSetIO.class.getName()).fatal("Invalid PB found for Plugin", ex);
        }
        return null;
    }
}
