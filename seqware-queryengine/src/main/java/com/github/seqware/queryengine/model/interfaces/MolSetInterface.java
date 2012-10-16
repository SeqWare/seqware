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
package com.github.seqware.queryengine.model.interfaces;

import com.github.seqware.queryengine.model.Molecule;
import com.github.seqware.queryengine.util.SeqWareIterable;
import java.util.Collection;

/**
 * Abstracts out some of the semantics for interacting with each of our Set
 * classes. Name was originally AbstractSet, but a name clash with Java's own
 * AbstractSet lead to some hard to notice errors.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public interface MolSetInterface<S extends MolSetInterface, T> extends SeqWareIterable<T>, Molecule<S> {

    /**
     * Add one element to the set
     *
     * @param element a T object.
     * @return a S object.
     */
    public abstract S add(T element);

    /**
     * Add numerous elements from within a set
     *
     * @param elements a {@link java.util.Collection} object.
     * @return a S object.
     */
    public abstract S add(Collection<T> elements);

    /**
     * Add an array of elements
     *
     * @param elements a T object.
     * @return a S object.
     */
    public abstract S add(T... elements);

    /**
     * Remove one element
     *
     * @param element a T object.
     * @return a S object.
     */
    public abstract S remove(T element);
    
    /**
     * Rebuild the underlying set architecture. This is only intended to be a
     * back-end method since sets may become inconsistent after a flush.
     */
    public void rebuild();
}
