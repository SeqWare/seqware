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
import java.security.AccessControlException;

/**
 * This interface specifies operations to persist, update, and add objects to 
 * the back-end without knowing about the specific database back-end.
 * @author dyuen
 */
public interface BackEndInterface {
    
    /**
     * Store obj in the back-end.
     * @param obj Object to be created
     * @throws AccessControlException if the user is not allowed to write to the 
     * parent object (i.e. create a Reference in a ReferenceSet without write 
     * permission to that ReferenceSet)
     */
    public void store(Particle obj) throws AccessControlException;
    
    
    /**
     * Crawl through obj and update changes in the back-end.
     * @param obj Object to be updated in the back-end
     * @throws AccessControlException if the user does not have permission to
     * change this object
     * @return Due to copy-on-write, this can result in a new object that the 
     * user may wish to subsequently work on
     */
    public Particle update(Particle obj) throws AccessControlException;
    
    /**
     * Update the obj using the latest information from the back-end
     * @param obj Object to be refreshed from the back-end
     * @throws AccessControlException if the user has lost permission to 
     * read the object 
     * @return Due to copy-on-write, this may return a new object with 
     * updated information
     */
    public Particle refresh(Particle obj) throws AccessControlException;
    
    /**
     * Delete obj (will cascade in the case of sets to their 
     * children)
     * @param obj Object to be deleted from the back-end
     * @throws AccessControlException  if the user does not have permission to
     * delete this (or children) objects
     */
    public void delete(Particle obj) throws AccessControlException;
    
    /**
     * Get the version of the particle
     * @param obj current particle
     * @return version (starts with version 1)
     * @throws AccessControlException  if the user does not have permission to read this
     */
    public long getVersion(Particle obj) throws AccessControlException;
    
    /**
     * Get the preceding Version of a particle
     * @param obj current particle
     * @return current particle's parent
     * @throws AccessControlException  if the user does not have permission to read this
     */
    public Particle getPrecedingVersion(Particle obj) throws AccessControlException;

    /**
     * Set the succeeding Version of a particle
     * @param predecessor  set current particle's parent
     * @throws AccessControlException  if the user does not have permission to write to this
     */
    public void setPrecedingVersion(Particle predecessor) throws AccessControlException;  
    
    /**
     * Associate tag with object
     * @param object object to be tagged
     * @param tag tag to associate
     * @throws AccessControlException  if the user does not have permission to write to this
     */
    public void associateTag(Atom object, Tag tag) throws AccessControlException;
    
    /**
     * Dissociate tag with object
     * @param object object to remove tags
     * @param tag tag to dissociate
     * @throws AccessControlException  if the user does not have permission to write to this
     */
    public void dissociateTag(Atom object, Tag tag) throws AccessControlException;
    
    /**
     * Get iterable of tags for this atom
     * @return iterable of tags
     * @throws AccessControlException, if the user does not have read permission to the 
     * object
     */
    public SeqWareIterable<Tag> getTags(Atom atom) throws AccessControlException;
}
