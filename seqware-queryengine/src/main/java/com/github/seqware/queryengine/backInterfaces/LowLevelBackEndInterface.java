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
package com.github.seqware.queryengine.backInterfaces;

import com.github.seqware.queryengine.model.Atom;

/**
 * This interface specifies operations to persist, update, and add objects to
 * the back-end without knowing about the specific database back-end.
 * 
 * Should only be used by back-end developers
 * 
 * @author dyuen
 * @version $Id: $Id
 */
public interface LowLevelBackEndInterface {
         
    /**
     * Store obj in the back-end.
     *
     * @param obj Object to be created
     */
    public void store(Atom ... obj);
    
    
    /**
     * Crawl through obj and update changes in the back-end.
     *
     * @param obj Object to be updated in the back-end
     */
    public void update(Atom ... obj);
    
    /**
     * Update the obj using the latest information from the back-end
     *
     * @param obj Object to be refreshed from the back-end
     * @return Due to copy-on-write, this may return a new object with
     * updated information
     */
    public Atom refresh(Atom obj) ;
    
//    /**
//     * Delete obj (will cascade in the case of sets to their 
//     * children)
//     * @param obj Object to be deleted from the back-end
//     */
//    public void delete(Atom obj); 
    
}
