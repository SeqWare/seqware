package com.github.seqware.queryengine.model;

import com.github.seqware.queryengine.factory.ModelManager;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.interfaces.BaseBuilder;
import com.github.seqware.queryengine.util.InMemoryIterable;
import com.github.seqware.queryengine.util.SeqWareIterable;
import java.util.ArrayList;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A TagSpec represents the first part of a tuple that can describe an attribute
 * in a GVF (ex: ID=ID_1 or Variant_seq=A,G or Reference_seq=G ).
 *
 * Tags always have subjects, they have a default predicate "=" and they may or
 * may not have objects. A non-specified value is represented as null
 *
 * Tags themselves are immutable (and thus do not need {@link Versionable} but
 * adding and removing tags on objects is of course possible given the right
 * permissions
 *
 * @author dyuen
 */
public class TagSpec extends AtomImpl<TagSpec> {

    public final static String prefix = "TagSpec";
    private String key = null;

    /**
     * Create a new tag
     */
    private TagSpec() {
        super();
    }

    /**
     * Get the key. Examples include ID, Variant_seq, or Reference_seq
     *
     * @return String key
     */
    public String getKey() {
        return key;
    }

    @Override
    public Class getHBaseClass() {
        return TagSpec.class;
    }

    @Override
    public String getHBasePrefix() {
        return TagSpec.prefix;
    }

    @Override
    public boolean equals(Object obj) {
        // will cause recursion
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Return a TagBuilder initialized with the key represented by this
     *
     * @return
     */
    public Tag.Builder newTagBuilder() {
        Tag.Builder newBuilder = Tag.newBuilder();
        newBuilder.setKey(key);
        if (this.getManager() != null) {
            newBuilder.setManager(this.getManager());
        }
        return newBuilder;
    }

    /**
     * Create a new ACL builder
     *
     * @return
     */
    public static TagSpec.Builder newBuilder() {
        return new TagSpec.Builder();
    }

    /**
     * Create an ACL builder started with a copy of this
     *
     * @return
     */
    @Override
    public TagSpec.Builder toBuilder() {
        TagSpec.Builder b = new TagSpec.Builder();
        b.tag = (TagSpec) this.copy(true);
        return b;
    }

    /**
     * Tags are not taggable, but it is actually easier to have the capability
     * via inheritance and just turn it off
     *
     * @param tag new tag to associate with this
     * @return whether the subject was successfully tagged
     */
    @Override
    public boolean associateTag(Tag tag) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Tags are not taggable, but it is actually easier to have the capability
     * via inheritance and just turn it off
     *
     * @param tag tag to be removed
     * @return whether the tag was successfully removed
     */
    @Override
    public boolean dissociateTag(Tag tag) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Tags are not taggable, but it is actually easier to have the capability
     * via inheritance and just turn it off
     *
     * @return Iterable of tags
     */
    @Override
    public SeqWareIterable<Tag> getTags() {
        return new InMemoryIterable(new ArrayList<Tag>());
    }

    public static class Builder implements BaseBuilder {

        private TagSpec tag = new TagSpec();

        public TagSpec.Builder setKey(String key) {
            tag.key = key;
            return this;
        }

        public TagSpec.Builder setPredicate(String predicate) {
            return this;
        }

        @Override
        public TagSpec build() {
            if (tag.key == null) {
                throw new RuntimeException("Invalid build of Tag");
            }
            if (tag.getManager() != null) {
                tag.getManager().objectCreated(tag);
            }
            return tag;
        }

        @Override
        public Builder setManager(ModelManager aThis) {
            tag.setManager(aThis);
            return this;
        }
    }
}
