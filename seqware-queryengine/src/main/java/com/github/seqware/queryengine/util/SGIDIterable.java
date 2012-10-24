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
import java.util.Iterator;

    
/**
 * A bit of a hack class, allows us to have getCount as well as iterable
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class SGIDIterable<T extends Atom> implements SeqWareIterable<T>{
    private Iterable<SGID> scanner;
    private final Class<T> t;
    
    /**
     * <p>Constructor for SGIDIterable.</p>
     *
     * @param scanner a {@link java.lang.Iterable} object.
     * @param t a {@link java.lang.Class} object.
     */
    public SGIDIterable(Iterable<SGID> scanner, Class<T> t){
        this.scanner = scanner;
        this.t = t;
    }

    /** {@inheritDoc} */
    @Override
    public long getCount() {
        throw new UnsupportedOperationException("HBase Scan class does not seem to support a number of rows operation");
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<T> iterator() {
        return new SGIDIterator();
    }
    
    public class SGIDIterator implements Iterator<T> {
        private final Iterator<SGID> iter;
        
        protected SGIDIterator(){
            iter = scanner.iterator();
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public T next() {
            SGID next = iter.next();
            T p = SWQEFactory.getQueryInterface().getAtomBySGID(t, next);
            return p;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
}
