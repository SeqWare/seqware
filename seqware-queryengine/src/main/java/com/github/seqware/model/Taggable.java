package com.github.seqware.model;

import java.util.List;

/**
 *
 * @author dyuen
 */
public interface Taggable<T> {

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
    
    /**
     * Remove a tag from a subject
     * @param tag tag to be removed
     * @return whether the tag was successfully removed
     */
    public boolean dissociateTag(Tag tag);
    
    /**
     * Get the List of tags that the implementing class has been tagged with
     * @return list of tags
     */
    public List<Tag> getTags();
    
}
