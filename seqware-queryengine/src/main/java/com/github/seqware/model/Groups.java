package com.github.seqware.model;

import java.util.UUID;

/**
 *
 * @author dyuen
 */
public abstract class Groups {
    
    /**
     * Internally used unique identifier of this feature.
     */
    private UUID uuid;
    private String version;
    
    /**
     * Create a new user
     */
    public Groups() {
        // TODO This will have to be replaced with a stronger UUID generation method.
        this.uuid = UUID.randomUUID();
    }
    
    /**
     * Get the universally unique identifier of this feature.
     */
    public UUID getUUID() {
        return this.uuid;
    }

    /**
     * Get the version number of a user
     * @return  
     */
    public String getVersion() {
        return version;
    }

    /**
     * Set the version number of a user
     * @param version 
     */
    public void setVersion(String version) {
        this.version = version;
    }

    
    
}
