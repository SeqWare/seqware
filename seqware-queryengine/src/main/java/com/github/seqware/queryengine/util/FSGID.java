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
package com.github.seqware.queryengine.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.seqware.queryengine.impl.HBaseStorage;
import com.github.seqware.queryengine.backInterfaces.StorageInterface;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.impl.FeatureList;
import java.util.Date;
import java.util.UUID;
import org.apache.log4j.Logger;

;

/**
 * Special class for Features which also stores the row key for features.
 * To enable high capacity feature sets, they also have to store a featureSetID.
 *
 * @author dyuen
 * @author boconnor
 * @version $Id: $Id
 */
public class FSGID extends SGID implements KryoSerializable {
    /** Constant <code>PositionSeparator=":"</code> */
    public static final String PositionSeparator = ":";
//    private String rowKey = null;
    private String referenceName = null;
    private SGID featureSetID = null;
    private boolean tombstone = false;


    /** {@inheritDoc} */
    @Override
    public String toString() {
        return friendlyRowKey + StorageInterface.SEPARATOR + featureSetID.toString() + StorageInterface.SEPARATOR + super.toString();
    }
    
    /**
     * Create a fully functional FSGID given raw data
     *
     * @param mostSig a long.
     * @param leastSig a long.
     * @param timestamp a long.
     * @param rowKey a {@link java.lang.String} object.
     * @param referenceName a {@link java.lang.String} object.
     * @param featureSet a {@link com.github.seqware.queryengine.util.SGID} object.
     * @param tombstone a boolean.
     */
    public FSGID(long mostSig, long leastSig, long timestamp, String rowKey, String referenceName, SGID featureSet, boolean tombstone) {
        super(mostSig, leastSig, timestamp, null);
        this.friendlyRowKey = rowKey;
        this.referenceName = referenceName;
        this.featureSetID = featureSet;
        this.tombstone = tombstone;
    }
    
    /**
     * construct a FSGID based on all the components of a SGID while taking the
     * non-unique aspects on a FSGID, used when creating FeatureLists only
     *
     * @param sgid a {@link com.github.seqware.queryengine.util.SGID} object.
     * @param fsgid a {@link com.github.seqware.queryengine.util.FSGID} object.
     */
    public FSGID(SGID sgid, FSGID fsgid){
        super(sgid.getUuid().getMostSignificantBits(), sgid.getUuid().getLeastSignificantBits(), sgid.getBackendTimestamp().getTime(), null);
        friendlyRowKey = fsgid.friendlyRowKey;
        this.referenceName = fsgid.referenceName;
        this.featureSetID = fsgid.getFeatureSetID();
        this.setBackendTimestamp(fsgid.getBackendTimestamp());
    }


    /**
     * Create a fully functional FSGID given models from the front-end
     *
     * @param sgid a {@link com.github.seqware.queryengine.util.SGID} object.
     * @param f
     * @param fSet
     * @param fSet a {@link com.github.seqware.queryengine.model.FeatureSet} object.
     */
    public FSGID(SGID sgid, Feature f, FeatureSet fSet) {
        super(sgid);
        try {
            this.featureSetID = fSet.getSGID();
            // generate row key
            StringBuilder builder = new StringBuilder();
            builder.append(fSet.getReference().getName()).append(StorageInterface.SEPARATOR).append(f.getSeqid()).append(PositionSeparator).append(padZeros(f.getStart(), HBaseStorage.PAD))/** unnecessary .append(".feature.").append(f.getVersion())*/;
            friendlyRowKey = builder.toString();
            referenceName = fSet.getReference().getName();
        } catch (Exception ex) {
            Logger.getLogger(FSGID.class.getName()).fatal( null, ex);
            Logger.getLogger(FSGID.class.getName()).fatal("Could not upgrade SGID on Feature " + f.getSGID() + " due to location out of bounds");
            Logger.getLogger(FSGID.class.getName()).fatal("    Value of fSet is " + fSet.toString());
            Logger.getLogger(FSGID.class.getName()).fatal("    Value of f is " + f.toString());
            Logger.getLogger(FSGID.class.getName()).fatal("    Value of fSet.getSGID is " + fSet.getSGID().toString());
        }
    }

    /**
     * Back-end method for Features stored within high capacity featuresets
     *
     * @return a boolean.
     */
    public boolean isTombstone() {
        return tombstone;
    }

    /**
     * Back-end method for Features stored within high capacity featuresets
     *
     * @param tombstone a boolean.
     */
    public void setTombstone(boolean tombstone) {
        this.tombstone = tombstone;
    }

    private String padZeros(long input, int totalPlaces) throws Exception {
        String strInput = Long.valueOf(input).toString();
        if (strInput.length() > totalPlaces) {
            throw new Exception("Integer " + input + " is larger than total places of " + totalPlaces + " so padding this string failed.");
        }
        int diff = totalPlaces - strInput.length();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < diff; i++) {
            buffer.append("0");
        }
        buffer.append(strInput);
        return (buffer.toString());
    }

    /**
     * Get the ID for the associated feature set
     *
     * @return a {@link com.github.seqware.queryengine.util.SGID} object.
     */
    public SGID getFeatureSetID() {
        return featureSetID;
    }

    /**
     * {@inheritDoc}
     *
     * Get the HBase-style row-key for this Feature
     */
    @Override
    public String getRowKey() {
        return friendlyRowKey;
    }

    /**
     * <p>Getter for the field <code>referenceName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getReferenceName() {
        return referenceName;
    }

    /** {@inheritDoc} */
    @Override
    public void write(Kryo kryo, Output output) {
        // doesn't seem to inherit properly?
        output.writeLong(super.getUuid().getLeastSignificantBits());
        output.writeLong(super.getUuid().getMostSignificantBits());
        output.writeBoolean(super.getBackendTimestamp() != null);
        if (super.getBackendTimestamp() != null){
            output.writeLong(super.getBackendTimestamp().getTime());
        }
        output.writeString(super.friendlyRowKey);
        output.writeString(referenceName);
    }

    /** {@inheritDoc} */
    @Override
    public void read(Kryo kryo, Input input) {
        long leastSig = input.readLong();
        long mostSig = input.readLong();
        this.setUuid(new UUID(mostSig, leastSig));
        boolean hasTimestamp = input.readBoolean();
        if (hasTimestamp){
            super.setBackendTimestamp(new Date(input.readLong()));
        }
        super.friendlyRowKey = input.readString();
        this.referenceName = input.readString();
    } 
    
    /**
     * <p>getTablename.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTablename(){
        return FeatureList.prefix + StorageInterface.SEPARATOR + this.getReferenceName();
    }
    
    /** {@inheritDoc} */
    @Override
    public void setFriendlyRowKey(String friendlyRowKey) {
        throw new UnsupportedOperationException();
    }

}
