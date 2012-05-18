package com.github.seqware.model;

import java.util.UUID;

/**
 * @author dyuen
 */
public abstract class Tag implements Versionable {
    
    /**
     * Internally used unique identifier of this feature.
     */
    private UUID uuid;
    
    /**
     * Create a new user
     */
    public Tag() {
        // TODO This will have to be replaced with a stronger UUID generation method.
        this.uuid = UUID.randomUUID();
    }
    
    /**
     * Get the universally unique identifier of this feature.
     */
    public UUID getUUID() {
        return this.uuid;
    }

    
    
}
