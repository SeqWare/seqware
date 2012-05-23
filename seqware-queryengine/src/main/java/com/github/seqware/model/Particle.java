package com.github.seqware.model;

import com.github.seqware.factory.Factory;
import java.security.AccessControlException;
import java.util.UUID;

/**
 * Core functionality for all objects that will need to be tracked within the
 * database back-end. Handles the addX and updateX methods in
 * FeatureStoreInterface
 *
 * @author dyuen
 */
public abstract class Particle {
    
    /**
     * Internally used unique identifier of this feature.
     */
    private UUID uuid;
    
    protected Particle(){
         // TODO This will have to be replaced with a stronger UUID generation method.
        this.uuid = UUID.randomUUID();
    }

    /**
     * Notify the back-end that it should keep track of the current object.
     * @throws AccessControlException if the user is not allowed to write to the 
     * parent object (i.e. create a Reference in a ReferenceSet without write 
     * permission to that ReferenceSet)
     */
    public void add() throws AccessControlException {
        Factory.getBackEnd().store(this);
    }
    
    
    /**
     * Notify the back-end that it should record the changes made to the current 
     * object. Updates cascade downward (i.e. changing a ReferenceSet will 
     * result in a copy-on-write that copies all children References as well)
     * @throws AccessControlException if the user does not have permission to
     * change this object
     * @return Due to copy-on-write, this can result in a new object that the 
     * user may wish to subsequently work on
     */
    public Object update() throws AccessControlException{
         return Factory.getBackEnd().update(this);
    }
    
    /**
     * Update the current object with any changes that may have been made to the
     * current object
     * @throws AccessControlException if the user has lost permission to 
     * read the object 
     * @return Due to copy-on-write, this may return a new object with 
     * updated information
     */
    public Object refresh() throws AccessControlException{
        return Factory.getBackEnd().refresh(this);
    }
    
    /**
     * Delete the current object (will cascade in the case of sets to their 
     * children)
     * @throws AccessControlException  if the user does not have permission to
     * delete this (or children) objects
     */
    public void delete() throws AccessControlException{
        Factory.getBackEnd().delete(this);
    }
    
    /**
     * Get the universally unique identifier of this feature.
     */
    public UUID getUUID() {
        return this.uuid;
    }
}
