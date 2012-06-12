package com.github.seqware.model.impl.inMemory;

import com.github.seqware.factory.ModelManager;
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
     * Construct reference with
     * @param name reference
     */
    private InMemoryTagSet(){
        super();
    }

    @Override
    public Iterator<Tag> iterator() {
        return tagSet.iterator();
    }

    @Override
    public TagSet add(Tag tag) {
        tagSet.add(tag);
        this.getManager().AtomStateChange(this, ModelManager.State.NEW_VERSION);
        return this;
    }

    @Override
    public void add(Set<Tag> tags) {
        this.tagSet.addAll(tags);
        this.getManager().AtomStateChange(this, ModelManager.State.NEW_VERSION);  
    }
    
    @Override
    public TagSet remove(Tag tag) {
        this.tagSet.remove(tag);
        this.getManager().AtomStateChange(this, ModelManager.State.NEW_VERSION);
        return this;
    }

    @Override
    public long getCount() {
        return tagSet.size();
    }
    
        /**
     * Create a new AnalysisSet builder
     *
     * @return
     */
    public static TagSet.Builder newBuilder() {
        return new InMemoryTagSet.Builder();
    }

    @Override
    public TagSet.Builder toBuilder() {
        TagSet.Builder b = new InMemoryTagSet.Builder();
        b.aSet = (InMemoryTagSet) this.copy(false);
        return b;
    }

    public static class Builder extends TagSet.Builder {
        
        public Builder(){
            aSet = new InMemoryTagSet();
        }

        @Override
        public TagSet build() {
            if (aSet.getName() == null || aSet.getManager() == null) {
                throw new RuntimeException("Invalid build of tag set");
            }
            aSet.getManager().objectCreated(aSet);
            return aSet;
        }
    }

}
