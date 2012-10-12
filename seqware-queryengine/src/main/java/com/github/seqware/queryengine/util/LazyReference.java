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

import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.Atom;
import java.io.Serializable;
import org.apache.log4j.Logger;

/**
 * Lazy reference class, needed if we wish to reduce the number of random accesses
 * when de-serializing objects or creating HBase specific model implementations.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class LazyReference<T extends Atom> implements Serializable {
    
    private static final long serialVersionUID = 1L;

    protected transient boolean referenceChecked = false;
    protected transient T referenceCache = null;
    protected SGID referenceSGID = null;
    protected Class<T> type;
    
    /**
     * <p>Constructor for LazyReference.</p>
     *
     * @param type a {@link java.lang.Class} object.
     */
    public LazyReference(Class<T> type){
        this.type = type;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object ref){
        if (ref instanceof LazyReference){
            LazyReference ref2 = (LazyReference) ref;
            if (referenceSGID == null && ref2.referenceSGID == null){
                return true;
            }
            if (referenceSGID != null && ref2.referenceSGID != null){
                return referenceSGID.equals(ref2.referenceSGID);
            }
            return false;
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.referenceSGID != null ? this.referenceSGID.hashCode() : 0);
        return hash;
    }
    
    /**
     * Set the SGID of the object to be lazy referenced
     *
     * @param sgid a {@link com.github.seqware.queryengine.util.SGID} object.
     */
    public void setSGID(SGID sgid) {       
        this.referenceSGID = sgid;
        this.referenceChecked = false;
    }

    /**
     * Retrieve the lazy referenced object if possible and return it
     *
     * @return a T object.
     */
    public T get() {
        //Logger.getLogger(LazyReference.class.getName()).info("Attempting to resolve lazy reference: referenceChecked " + referenceChecked + " referenceSGID " + referenceSGID.getRowKey() + " referenceAddr " + this.referenceCache);
        if (!referenceChecked && referenceSGID != null) {
            this.referenceCache = (T) SWQEFactory.getQueryInterface().getAtomBySGID(type, referenceSGID);
            if (this.referenceCache == null){
                Logger.getLogger(LazyReference.class.getName()).error("Unable to retrieve " + referenceSGID.getRowKey() + " as a " + this.type.getSimpleName());
            }
        }
        referenceChecked = true;
        return this.referenceCache;
    }

    /**
     * Set the lazy reference of the object with an actual object.
     * Can also clear the reference if set to null
     *
     * @param reference a T object.
     */
    public void set(T reference) {     
        this.referenceChecked = true;
        if (reference != null) {
            this.referenceCache = reference;
            this.referenceSGID = reference.getSGID();
        } else {
            this.referenceCache = null;
            this.referenceSGID = null;
        }
    }

    /**
     * Get the underlying SGID for the lazy reference
     *
     * @return a {@link com.github.seqware.queryengine.util.SGID} object.
     */
    public SGID getSGID() {
        return referenceSGID;
    }
}
