package com.github.seqware.model;

/**
 * Interface for all classes that can be tagged. Associating or dissociating 
 * tags does not create a copy-on-write operation
 * @author dyuen
 */
public interface Taggable<T> {

    /**
     * associate tag to a subject with a null value and a null predicate
     * @param subject arbitrary value for the subject
     * @return whether the subject was successfully tagged 
     */
    public boolean associateTag(String subject);
    
    /**
     * associate tag to a subject with value and a null predicate
     * @param subject arbitrary value for the subject
     * @param value value to associate in the tuple
     * @return whether the subject was successfully tagged 
     */
    public boolean associateTag(String subject, String value);
    
    /**
     * associate a given tag to a subject with value and predicate
     * @param subject arbitrary value for the subject
     * @param value arbitrary value for the tuple
     * @param predicate arbitrary predicate for the tuple
     * @return whether the subject was successfully tagged 
     */
    public boolean associateTag(String subject, String value, String predicate);
    
    /**
     * Remove a tag from a subject
     * @param tag tag to be removed
     * @return whether the tag was successfully removed
     */
    public boolean dissociateTag(Tag tag);
    
    /**
     * Get tags that the implementing class has been tagged with
     * @return Iterable of tags
     */
    public Iterable<Tag> getTags();
    
}
