package com.github.seqware.model;

import java.util.UUID;

/**
 * A Tag represents the first part of a tuple that can describe an attribute
 * in a GVF (ex: ID=ID_1 or Variant_seq=A,G or Reference_seq=G )
 * @author dyuen
 */
public abstract class Tag implements Versionable {
    
    /**
     * Internally used unique identifier of this feature.
     */
    private UUID uuid;
    private TagSet tagSet;
    
    /**
     * Create a new tag
     */
    private Tag() {
        // TODO This will have to be replaced with a stronger UUID generation method.
        this.uuid = UUID.randomUUID();
    }
    
    /**
     * Tags are created in bulk in a tag set
     * @param tagSet 
     */
    public Tag(TagSet tagSet){
        this();
        this.tagSet = tagSet; 
    }
    
    /**
     * Get the universally unique identifier of this feature.
     */
    public UUID getUUID() {
        return this.uuid;
    }

    
    
}
