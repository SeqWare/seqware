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
package com.github.seqware.util;

import com.github.seqware.factory.Factory;
import com.github.seqware.model.Atom;

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
 */
public class LazyLatestReference<T extends Atom> extends LazyReference<T> {

    /**
     * Retrieve the lazy referenced object if possible and return it
     * @return 
     */
    @Override
    public T get() {
        if (!referenceChecked && referenceSGID != null) {
            this.referenceCache = (T) Factory.getFeatureStoreInterface().getLatestAtomBySGID(referenceSGID);
        }
        referenceChecked = true;
        return this.referenceCache;
    }

    
}
