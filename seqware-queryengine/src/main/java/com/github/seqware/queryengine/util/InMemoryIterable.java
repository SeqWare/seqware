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

import java.util.Collection;
import java.util.Iterator;

/**
 * A bit of a hack class, allows us to have getCount as well as iterable
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class InMemoryIterable<T> implements SeqWareIterable<T>{
    private Collection col;      
    
    /**
     * <p>Constructor for InMemoryIterable.</p>
     *
     * @param col a {@link java.util.Collection} object.
     */
    public InMemoryIterable(Collection col){
        this.col = col;
    }

    /** {@inheritDoc} */
    @Override
    public long getCount() {
        return col.size();
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<T> iterator() {
        return col.iterator();
    }
    
}
