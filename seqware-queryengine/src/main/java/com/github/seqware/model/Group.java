package com.github.seqware.model;

import java.util.UUID;

/**
 * A Group of users that may share ACL permissions
 * @author dyuen
 */
public abstract class Group extends Molecule{
    
    /**
     * Internally used unique identifier of this group.
     */
    private UUID uuid;  
    
    /**
     * Create a new user group
     */
    public Group() {
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
