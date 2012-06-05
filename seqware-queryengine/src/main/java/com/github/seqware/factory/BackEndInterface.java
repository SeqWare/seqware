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
package com.github.seqware.factory;

import com.github.seqware.model.Atom;
import com.github.seqware.model.Particle;
import com.github.seqware.model.Tag;
import com.github.seqware.util.SeqWareIterable;

/**
 * This interface specifies operations to persist, update, and add objects to 
 * the back-end without knowing about the specific database back-end.
 * @author dyuen
 */
public interface BackEndInterface {
    
    /**
     * Store obj in the back-end.
     * @param obj Object to be created
     */
    public void store(Particle obj);
    
    
    /**
     * Crawl through obj and update changes in the back-end.
     * @param obj Object to be updated in the back-end
     */
    public void update(Particle obj);
    
    /**
     * Update the obj using the latest information from the back-end
     * @param obj Object to be refreshed from the back-end
     * @return Due to copy-on-write, this may return a new object with 
     * updated information
     */
    public Particle refresh(Particle obj) ;
    
//    /**
//     * Delete obj (will cascade in the case of sets to their 
//     * children)
//     * @param obj Object to be deleted from the back-end
//     */
//    public void delete(Particle obj);
    
    /**
     * Get the version of the particle
     * @param obj current particle
     * @return version (starts with version 1)
     */
    public long getVersion(Particle obj);
    
    /**
     * Get the preceding Version of a particle
     * @param obj current particle
     * @return current particle's parent
     */
    public Particle getPrecedingVersion(Particle obj);

    /**
     * Set the succeeding Version of a particle
     * @param predecessor  set current particle's parent
     */
    public void setPrecedingVersion(Particle predecessor);  
    
    /**
     * Associate tag with object
     * @param object object to be tagged
     * @param tag tag to associate
     */
    public void associateTag(Atom object, Tag tag) ;
    
    /**
     * Dissociate tag with object
     * @param object object to remove tags
     * @param tag tag to dissociate
     */
    public void dissociateTag(Atom object, Tag tag) ;
    
    /**
     * Get iterable of tags for this atom
     * @return iterable of tags
     */
    public SeqWareIterable<Tag> getTags(Atom atom) ;
}
