package com.github.seqware.model;

/**
 * A Tag represents the first part of a tuple that can describe an attribute
 * in a GVF (ex: ID=ID_1 or Variant_seq=A,G or Reference_seq=G ).
 * 
 * Tags themselves are immutable (and thus do not
 * need {@link Versionable} but adding and removing tags on objects is 
 * of course possible given the right permissions
 * @author dyuen
 */
public abstract class Tag extends Particle {
    
    private TagSet tagSet;
    private String subject;
    private String predicate = "=";
    private String object = null;
    
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

    /**
     * Get the subject. Examples include ID, Variant_seq, or Reference_seq 
     * @return String subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Get a reference to the parent TagSet
     * @return parent TagSet
     */
    public TagSet getTagSet() {
        return tagSet;
    }

    /**
     * Get the object. Examples include ID_1, A, or G
     * @return String object
     */
    public String getObject() {
        return object;
    }

    /**
     * Get the predicate. Examples include "="
     * @return String predicate
     */
    public String getPredicate() {
        return predicate;
    }

    
    
}
