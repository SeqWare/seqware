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

import com.github.seqware.dto.QESupporting;
import com.github.seqware.dto.QESupporting.AtomPB;
import com.github.seqware.dto.QESupporting.FeatureAtomPB;
import com.github.seqware.dto.QueryEngine;
import com.github.seqware.dto.QueryEngine.ACLPB;
import com.github.seqware.model.Feature;
import com.github.seqware.model.Tag;
import com.github.seqware.model.impl.AtomImpl;
import com.github.seqware.model.impl.MoleculeImpl;
import com.github.seqware.model.interfaces.ACL;
import com.github.seqware.util.FSGID;
import com.github.seqware.util.SGID;
import java.util.Date;
import java.util.Iterator;

/**
 *
 * @author dyuen
 */
public class UtilIO {

    private static final TagIO tagIO = new TagIO();

    /**
     * Handle deserialization of the core atom
     *
     * @param atompb
     * @param atomImpl
     */
    public static void handlePB2Atom(QESupporting.AtomPB atompb, AtomImpl atomImpl) {
        for (QESupporting.TagPB t : atompb.getTagsList()) {
            atomImpl.associateTag(tagIO.pb2m(t));
        }
        SGID pID = atompb.hasPrecedingID() ? SGIDIO.pb2m(atompb.getPrecedingID()) : null;
        atomImpl.impersonate(SGIDIO.pb2m(atompb.getSgid()), new Date(atompb.getDate()), pID);
    }

    /**
     * Handle serialization of the core atom
     *
     * @param atompb
     * @param atomImpl
     * @return
     */
    public static AtomPB handleAtom2PB(QESupporting.AtomPB atompb, AtomImpl atomImpl) {
        QESupporting.AtomPB.Builder builder = atompb.newBuilderForType();
        for (Iterator it = atomImpl.getTags().iterator(); it.hasNext();) {
            //TODO: weird, we shouldn't have to cast here
            Tag t = (Tag) it.next();
            builder.addTags(tagIO.m2pb(t));
        }
        builder.setSgid(SGIDIO.m2pb(atomImpl.getSGID()));
        builder.setDate(atomImpl.getTimestamp().getTime());
        if (atomImpl.getPrecedingSGID() != null) {
            builder.setPrecedingID(SGIDIO.m2pb(atomImpl.getPrecedingSGID()));
        }
        return builder.build();
    }

    /**
     * Handle de-serialization of the core atom just for Features FIXME: this
     * should be collapsible with normal atom
     *
     * @param atompb
     * @param feature
     */
    public static void handlePB2Atom(QESupporting.FeatureAtomPB atompb, Feature feature) {
        for (QESupporting.TagPB t : atompb.getTagsList()) {
            feature.associateTag(tagIO.pb2m(t));
        }
        SGID pID = atompb.hasPrecedingID() ? FSGIDIO.pb2m(atompb.getPrecedingID()) : null;
        feature.impersonate(FSGIDIO.pb2m(atompb.getSgid()), new Date(atompb.getDate()), pID);
    }

    /**
     * Handle serialization of the core atom just for Features FIXME: this
     * should be collapsible with normal atom
     *
     * @param atompb
     * @param feature
     * @return
     */
    public static FeatureAtomPB handleAtom2PB(QESupporting.FeatureAtomPB atompb, Feature feature) {
        QESupporting.FeatureAtomPB.Builder builder = atompb.newBuilderForType();
        for (Iterator it = feature.getTags().iterator(); it.hasNext();) {
            //TODO: weird, we shouldn't have to cast here
            Tag t = (Tag) it.next();
            builder.addTags(tagIO.m2pb(t));
        }
        builder.setSgid(FSGIDIO.m2pb((FSGID) feature.getSGID()));
        builder.setDate(feature.getTimestamp().getTime());
        if (feature.getPrecedingSGID() != null) {
            builder.setPrecedingID(FSGIDIO.m2pb((FSGID) feature.getPrecedingSGID()));
        }
        return builder.build();
    }

    /**
     * This should handle deserialization of ACL
     *
     * @param aclpb
     * @param molImpl
     */
    public static void handlePB2ACL(QueryEngine.ACLPB aclpb, MoleculeImpl molImpl) {
        ACL.Builder builder = ACL.newBuilder();
        builder.setRights(aclpb.getRightsList());
        if (aclpb.hasUser()) {
            SGID sgid = SGIDIO.pb2m(aclpb.getUser());
            builder.setOwner(sgid);
        }
        if (aclpb.hasGrp()) {
            SGID sgid = SGIDIO.pb2m(aclpb.getGrp());
            builder.setGroup(sgid);
        }
        molImpl.setPermissions(builder.build());
    }

    /**
     * This should handle serialization of ACL
     *
     * @param aclpb
     * @param molImpl
     * @return
     */
    public static ACLPB handleACL2PB(QueryEngine.ACLPB aclpb, MoleculeImpl molImpl) {
        ACLPB.Builder builder = aclpb.newBuilderForType();
        builder.addAllRights(molImpl.getPermissions().getAccess());
        if (molImpl.getPermissions().getOwnerSGID() != null) {
            builder.setUser(SGIDIO.m2pb(molImpl.getPermissions().getOwnerSGID()));
        }
        if (molImpl.getPermissions().getGroupSGID() != null) {
            builder.setGrp(SGIDIO.m2pb(molImpl.getPermissions().getGroupSGID()));
        }
        return builder.build();
    }
}
