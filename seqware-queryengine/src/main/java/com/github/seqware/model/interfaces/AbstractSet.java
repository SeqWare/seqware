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
package com.github.seqware.model.interfaces;

import com.github.seqware.model.Molecule;
import com.github.seqware.util.SeqWareIterable;
import java.util.Set;

/**
 * Abstracts out some of the semantics for interacting with each of our Set
 * classes 
 * 
 * @author dyuen
 */
public interface AbstractSet<S extends AbstractSet, T> extends SeqWareIterable<T>, Molecule<S> {

    public abstract S add(T element);

    public abstract S add(Set<T> elements);
    
    public abstract S add(T ... elements);
    
    public abstract S remove(T element);
    
}
