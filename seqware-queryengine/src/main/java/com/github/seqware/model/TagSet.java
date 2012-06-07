package com.github.seqware.model;

import com.github.seqware.factory.ModelManager;
import com.github.seqware.util.SeqWareIterable;
import java.util.Set;

/**
 * A set of Tags
 *
 * @author dyuen
 */
public abstract class TagSet extends Molecule<TagSet> implements SeqWareIterable<Tag> {

    private String name;

    /**
     * Create a new TagSet
     */
    protected TagSet() {
        super();
    }

    /**
     * Get the name of the tag set
     *
     * @return the name of the tag set
     */
    public String getName() {
        return name;
    }

    /**
     * Add a tag to this tag set
     *
     * @param tag tag to add
     */
    public abstract TagSet add(Tag tag);

    /**
     * Add a set of tags to this tag set
     *
     * @param tags tags to add
     */
    public abstract void add(Set<Tag> tags);

    /**
     * Create an ACL builder started with a copy of this
     *
     * @return
     */
    @Override
    public abstract TagSet.Builder toBuilder();

    public abstract TagSet remove(Tag tag);

    public abstract static class Builder implements BaseBuilder {

        public TagSet aSet;

        public TagSet.Builder setName(String name) {
            aSet.name = name;
            return this;
        }

        @Override
        public abstract TagSet build();

        @Override
        public Builder setManager(ModelManager aThis) {
            aSet.setManager(aThis);
            return this;
        }
    }
}
