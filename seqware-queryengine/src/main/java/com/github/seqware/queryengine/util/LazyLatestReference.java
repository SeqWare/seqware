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

/**
 * Lazy reference class, needed if we wish to reduce the number of random accesses
 * when deserializing objects or creating HBase specific model implementations.
 *
 * This particular implementation should retrieve the latest version of a particular object.
 * This has two functions, allows permission objects to get the freshest version
 * of an Owner or User, and breaks reference loops (in determining timestamps) when serializing
 * to HBase
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class LazyLatestReference<T extends Atom> extends LazyReference<T> {
    
    /**
     * <p>Constructor for LazyLatestReference.</p>
     *
     * @param type a {@link java.lang.Class} object.
     */
    public LazyLatestReference(Class<T> type){
       super(type);
    }
    
    /**
     * {@inheritDoc}
     *
     * Retrieve the lazy referenced object if possible and return it
     */
    @Override
    public T get() {
        if (!referenceChecked && referenceSGID != null) {
            this.referenceCache = (T) SWQEFactory.getQueryInterface().getLatestAtomBySGID(referenceSGID, type);
        }
        referenceChecked = true;
        return this.referenceCache;
    }

    
}
