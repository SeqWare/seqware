package com.github.seqware.model;

import com.github.seqware.util.SeqWareIterable;
import java.util.Set;

/**
 * A set of Tags
 * @author dyuen
 */
public abstract class TagSet extends Molecule implements SeqWareIterable<Tag>{
    
    private String name;
    
    /**
     * Create a new TagSet
     * @param name name of the new tag set
     */
    public TagSet(String name) {
        super();
        this.name = name;
    }

    /**
     * Get the name of the tag set
     * @return the name of the tag set
     */
    public String getName() {
        return name;
    }
    
    /**
     * Add a tag to this tag set
     * @param tag tag to add
     */
    public abstract void add(Tag tag);

    /**
     * Ass a set of tags to this tag set
     * @param tags tags to add
     */
    public abstract void add(Set<Tag> tags);
    
}
