package com.github.seqware.queryengine.model.interfaces;

import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.util.SeqWareIterable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    
    /**
     * Get tags that have been sorted into a structure of Nested Maps.
     * Splits are handled by '::'
     * i.e. MTTS::dbSNP::ID becomes MTTS -> dbSNP -> ID
     * @return 
     */
    public NestedLevel getNestedTags();
    
    /**
     * Quickly get the value of a tag by the key.
     * @param key
     * @return 
     */
    public Tag getTagByKey(String key);
    
    
    /**
     * This class represents both the tags that may be present at a particular level of the nested hash structure
     * as well as tags that may be further up
     */
    public class NestedLevel{
        protected Map<String, NestedLevel> childMaps = new HashMap<String, NestedLevel>();
        protected Map<String, Tag> childTags= new HashMap<String, Tag>();

        public Map<String, NestedLevel> getChildMaps() {
            return childMaps;
        }

        public Map<String, Tag> getChildTags() {
            return childTags;
        }
    }
}
