package com.github.seqware.model;

/**
 *
 * @author dyuen
 */
public interface Taggable {

    /**
     * associate tag to a subject with a null value and a null predicate
     * @param tag tag to associate
     * @return whether the subject was successfully tagged 
     */
    public boolean associateTag(Tag tag);
    
    /**
     * associate tag to a subject with value and a null predicate
     * @param tag tag to associate
     * @param value value to associate in the tuple
     * @return whether the subject was successfully tagged 
     */
    public boolean associateTag(Tag tag, String value);
    
    /**
     * associate a given tag to a subject with value and predicate
     * @param tag tag to associate
     * @param value arbitrary value for the tuple
     * @param predicate arbitrary predicate for the tuple
     * @return whether the subject was successfully tagged 
     */
    public boolean associateTag(Tag tag, String value, String predicate);
    
    
}
