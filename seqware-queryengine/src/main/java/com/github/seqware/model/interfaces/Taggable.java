package com.github.seqware.model.interfaces;

import com.github.seqware.model.Tag;
import com.github.seqware.util.SeqWareIterable;

/**
 * Interface for all classes that can be tagged. Associating or dissociating 
 * tags does not create a copy-on-write operation
 * @author dyuen
 */
public interface Taggable<T> {

    /**
     * associate new tag to a subject with a null value and a null predicate
     * @param tag new tag to associate with this
     * @return whether the subject was successfully tagged 
     */
    public boolean associateTag(Tag tag);
    
    /**
     * Remove a new tag from a subject
     * @param tag tag to be removed
     * @return whether the tag was successfully removed
     */
    public boolean dissociateTag(Tag tag);
    
    /**
     * Get tags that the implementing class has been tagged with
     * @return Iterable of tags
     */
    public SeqWareIterable<Tag> getTags();
    
}
