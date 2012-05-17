package com.github.seqware.model;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author dyuen
 */
public abstract class User implements Taggable, ACLable, Versionable{
    
    /**
     * Internally used unique identifier of this feature.
     */
    private UUID uuid;
    
    /**
     * Create a new user
     */
    public User() {
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
     * Get the List of tags that this user has been tagged with
     * @return 
     */
    public abstract List<Tag> getTags();
    
}
