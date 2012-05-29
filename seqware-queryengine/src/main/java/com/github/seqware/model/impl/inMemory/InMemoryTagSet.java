package com.github.seqware.model.impl.inMemory;

import com.github.seqware.model.Feature;
import com.github.seqware.model.Tag;
import com.github.seqware.model.TagSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * An in-memory representation of a TagSet.
 *
 * @author dyuen
 */
public class InMemoryTagSet extends TagSet {
    
    private Set<Tag> tagSet = new HashSet<Tag>();
    
    /**
     * Construct reference with a name
     * @param name reference with a name
     */
    public InMemoryTagSet(String name){
        super(name);
    }

    @Override
    public Iterator<Tag> iterator() {
        return tagSet.iterator();
    }

    @Override
    public void add(Tag tag) {
        tagSet.add(tag);
    }

    @Override
    public void add(Set<Tag> tags) {
        this.tagSet.addAll(tags);
    }

    @Override
    public long getCount() {
        return tagSet.size();
    }

}
