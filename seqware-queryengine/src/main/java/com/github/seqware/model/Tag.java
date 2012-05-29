package com.github.seqware.model;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A Tag represents the first part of a tuple that can describe an attribute
 * in a GVF (ex: ID=ID_1 or Variant_seq=A,G or Reference_seq=G ).
 * 
 * Tags always have subjects, they have a default predicate "=" and they may
 * or may not have objects. A non-specified object is represented as null
 * 
 * Tags themselves are immutable (and thus do not
 * need {@link Versionable} but adding and removing tags on objects is 
 * of course possible given the right permissions
 * @author dyuen
 */
public class Tag extends Particle {
    
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
     * Create tag with only subject and a default predicate of "="
     * @param tagSet parental tagset
     * @param subject subject
     */
    public Tag(TagSet tagSet, String subject){
        this();
        this.tagSet = tagSet;
        this.subject = subject;
    }
    
    /**
     * Create tags with subject and predicate
     * @param tagSet parental tagset
     * @param subject subject 
     * @param predicate predicate
     */
    public Tag(TagSet tagSet, String subject, String predicate){
        this(tagSet, subject);
        this.predicate = predicate;
    }
    
    /**
     * Create fully specified tags
     * @param tagSet parental tagset
     * @param subject subject 
     * @param predicate predicate
     * @param object object 
     */
    public Tag(TagSet tagSet, String subject, String predicate, String object){
        this(tagSet, subject, predicate);
        this.object = object;
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

    
    @Override
    public boolean equals(Object obj) {
        // will cause recursion
        //return EqualsBuilder.reflectionEquals(this, obj);
        if (obj instanceof Tag) {
            Tag other = (Tag) obj;
            if (this.object == null && other.object != null || this.object != null && other.object == null) {
                return false;
            }
            return this.getUUID().equals(other.getUUID()) && this.subject.equals(other.subject) 
                    && this.predicate.equals(other.predicate)&& (this.object != null && this.object.equals(other.object));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
