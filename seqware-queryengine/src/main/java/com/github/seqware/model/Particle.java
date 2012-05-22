package com.github.seqware.model;

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
     * Place-holder. I think we can implicitly add objects as they are created.
     * But if we want something implicit, we can decide to use this and reorganize
     */
    public void add(){
        throw new UnsupportedOperationException("It does nothing.");
    }
    
    
    /**
     * Notify the back-end that it should record the changes made to the current 
     * object
     */
    public void update() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Update the current object with any changes that may have been made to the
     * current object
     */
    public void refresh(){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
        /**
     * Get the universally unique identifier of this feature.
     */
    public UUID getUUID() {
        return this.uuid;
    }
}
