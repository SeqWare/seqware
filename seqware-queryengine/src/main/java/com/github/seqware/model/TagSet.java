package com.github.seqware.model;

import java.util.UUID;

/**
 * A set of Tags
 * TODO: RESTful API implies tagging of tagSets, do we want this or do we remove
 * it along with tagging of tags?
 * @author dyuen
 */
public abstract class TagSet extends Molecule{
    
    /**
     * Internally used unique identifier of this feature.
     */
    private UUID uuid;  
    
    /**
     * Create a new TagSet
     */
    public TagSet() {
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
