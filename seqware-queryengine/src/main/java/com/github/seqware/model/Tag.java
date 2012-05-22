package com.github.seqware.model;

import java.util.UUID;

/**
 * A Tag represents the first part of a tuple that can describe an attribute
 * in a GVF (ex: ID=ID_1 or Variant_seq=A,G or Reference_seq=G )
 * @author dyuen
 */
public abstract class Tag implements Versionable {
    
    private TagSet tagSet;
    
    /**
     * Create a new tag
     */
    private Tag() {
        super();
    }
    
    /**
     * Tags are created in bulk in a tag set
     * @param tagSet 
     */
    public Tag(TagSet tagSet){
        this();
        this.tagSet = tagSet; 
    }

    
    
}
