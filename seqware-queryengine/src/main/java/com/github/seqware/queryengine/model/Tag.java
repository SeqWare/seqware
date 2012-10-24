package com.github.seqware.queryengine.model;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.interfaces.BaseBuilder;
import com.github.seqware.queryengine.util.InMemoryIterable;
import com.github.seqware.queryengine.util.LazyReference;
import com.github.seqware.queryengine.util.SGID;
import com.github.seqware.queryengine.util.SeqWareIterable;
import java.util.ArrayList;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A Tag an instance of a TagSpec which contains all three of the values
 * necessary for a GVF (ex: ID=ID_1 or Variant_seq=A,G or Reference_seq=G ).
 *
 * Tags always have subjects, they have a default predicate "=" and they may or
 * may not have objects. A non-specified value is represented as null
 *
 * Tags themselves are immutable (and thus do not need {@link Versionable} but
 * adding and removing tags on objects is of course possible given the right
 * permissions
 *
 * @author dyuen
 * @author jbaran
 * @version $Id: $Id
 */
public class Tag extends AtomImpl<Tag> {

    /** Constant <code>prefix="Tag"</code> */
    public final static String prefix = "Tag";
    /** Constant <code>SEPARATOR="::"</code> */
    public final static String SEPARATOR = "::";
    private LazyReference<TagSet> tagSet = new LazyReference<TagSet>(TagSet.class);

    /**
     * Format: ACCESSION[::NAME]
     *
     *   ACCESSION : some sort of unique identifier for the tag within the tagset
     *   NAME : an optional addition to the accession for denoting names and
     *          synonyms -- note that one accession can appear multiple times in
     *          a tag set under various names and synonyms.
     */
    private String key = null;

    private String predicate = "=";
    private Object value = null;
    private ValueType vType = null;

    public enum ValueType {
        STRING, BYTEARR, SGID, FLOAT, DOUBLE, LONG, INTEGER
    };

    /**
     * Create a new tag
     */
    private Tag() {
        super();
        vType = ValueType.STRING;
    }

    /**
     * Tests whether the given tag is a parent of this tag -- making this
     * tag a descendant of the parameter tag.
     *
     * Only applicable to Sequence Ontology hierarchies right now, because the
     * hierarchy check depends on the coding used to represent the hierarchy.
     *
     * @param tagKey Tag-key that we want to check if we descent from it.
     * @return True if this tag is a descendant of the tag in the parameter.
     */
    public boolean isDescendantOf(String tagKey) {
        if (!this.getKey().startsWith("SO:")) {
            return false;
        }

        if (tagKey == null || !tagKey.startsWith("SO:")) {
            return false;
        }

        String thisKey = this.getKey().replaceFirst("^SO:", "");
        tagKey = tagKey.replaceFirst("^SO:", "");

        return thisKey.equals(tagKey) || // Perfect match. Top of the hierarchy match.
               thisKey.endsWith(" " + tagKey) || // The tag key matches us as a leaf.
               thisKey.startsWith(tagKey + " ") || // Top-level match, i.e. our hierarchy is rooted with tagKey.
               thisKey.contains(" " + tagKey + " "); // The tag key appears somewhere between the root and leaf.
    }

    /**
     * Get the key. Examples include ID, Variant_seq, or Reference_seq
     *
     * @return String key
     */
    public String getKey() {
        return key;
    }

    /**
     * Get a reference to the parent TagSet
     *
     * @return parent TagSet
     */
    public TagSet getTagSet() {
        return tagSet.get();
    }

    /**
     * Get a reference to the parent TagSet
     *
     * @return parent TagSet
     */
    public SGID getTagSetSGID() {
        return tagSet.getSGID();
    }

    /**
     * Get the value. Examples include ID_1, A, or G
     *
     * @return String value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Get the predicate. Examples include "="
     *
     * @return String predicate
     */
    public String getPredicate() {
        return predicate;
    }

    /**
     * Set the TagSet for this particular tag, should not be called outside
     * of the back-end. This is used primary to keep track of which TagSet a tag
     * came from.
     *
     * @param sgid a {@link com.github.seqware.queryengine.util.SGID} object.
     * @return a {@link com.github.seqware.queryengine.model.Tag} object.
     */
    public Tag setTagSet(SGID sgid) {
        this.tagSet.setSGID(sgid);
        return this;
    }

    /**
     * Set the TagSet for this particular tag, should not be called outside
     * of the back-end. This is used primary to keep track of which TagSet a tag
     * came from.
     *
     * @param set a {@link com.github.seqware.queryengine.model.TagSet} object.
     * @return a {@link com.github.seqware.queryengine.model.Tag} object.
     */
    public Tag setTagSet(TagSet set) {
        this.tagSet.set(set);
        return this;
    }

    /**
     * Get the type of value for the tag value
     *
     * @return a {@link com.github.seqware.queryengine.model.Tag.ValueType} object.
     */
    public ValueType getvType() {
        return vType;
    }

    /** {@inheritDoc} */
    @Override
    public Class getHBaseClass() {
        return Tag.class;
    }

    /** {@inheritDoc} */
    @Override
    public String getHBasePrefix() {
        return Tag.prefix;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Tag)) {
            return false;
        }
        EqualsBuilder b = new EqualsBuilder();
        b.append(this.getSGID(), ((Tag)o).getSGID());
        return b.isEquals();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        HashCodeBuilder b = new HashCodeBuilder();
        b.append(this.getSGID());
        return b.hashCode();
    }

    /**
     * Create a new ACL builder
     *
     * @return a {@link com.github.seqware.queryengine.model.Tag.Builder} object.
     */
    public static Tag.Builder newBuilder() {
        return new Tag.Builder();
    }

    /**
     * {@inheritDoc}
     *
     * Create an ACL builder started with a copy of this
     */
    @Override
    public Tag.Builder toBuilder() {
        Tag.Builder b = new Tag.Builder();
        b.tag = (Tag) this.copy(true);
        return b;
    }

    /**
     * {@inheritDoc}
     *
     * Tags are not taggable, but it is actually easier to have the capability
     * via inheritance and just turn it off
     */
    @Override
    public boolean associateTag(Tag tag) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc}
     *
     * Tags are not taggable, but it is actually easier to have the capability
     * via inheritance and just turn it off
     */
    @Override
    public boolean dissociateTag(Tag tag) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc}
     *
     * Tags are not taggable, but it is actually easier to have the capability
     * via inheritance and just turn it off
     */
    @Override
    public SeqWareIterable<Tag> getTags() {
        return new InMemoryIterable(new ArrayList<Tag>());
    }

    public static class Builder extends BaseBuilder {

        private Tag tag = new Tag();

        /**
         * '
         * Should not be called outside of the back-end
         *
         * @param key
         * @return
         */
        public Tag.Builder setKey(String key) {
            assert (tag.key == null);
            tag.key = key;
            return this;
        }

        public Tag.Builder setPredicate(String predicate) {
            tag.predicate = predicate;
            return this;
        }

        public Tag.Builder setTagSet(TagSet set) {
            this.tag.tagSet.set(set);
            return this;
        }

        public Tag.Builder setTagSet(SGID sgid) {
            this.tag.tagSet.setSGID(sgid);
            return this;
        }

        /**
         * Set the value to one of ValueType
         *
         * @param value
         * @return
         */
        public Tag.Builder setValue(Object value) {
            tag.value = value;
            if (value instanceof byte[]) {
                tag.vType = ValueType.BYTEARR;
            } else if (value instanceof Double) {
                tag.vType = ValueType.DOUBLE;
            } else if (value instanceof Float) {
                tag.vType = ValueType.FLOAT;
            } else if (value instanceof Integer) {
                tag.vType = ValueType.INTEGER;
            } else if (value instanceof Long) {
                tag.vType = ValueType.LONG;
            } else if (value instanceof SGID) {
                tag.vType = ValueType.SGID;
            } else if (value instanceof String) {
                tag.vType = ValueType.STRING;
            }
            return this;
        }

        @Override
        public Tag build() {
            if (tag.key == null) {
                throw new RuntimeException("Invalid build of Tag");
            }
            if (tag.getManager() != null) {
                tag.getManager().objectCreated(tag);
            }
            return tag;
        }

        @Override
        public Builder setManager(CreateUpdateManager aThis) {
            tag.setManager(aThis);
            return this;
        }

        @Override
        public Builder setFriendlyRowKey(String rowKey) {
            super.checkFriendlyRowKey(rowKey);
            tag.getSGID().setFriendlyRowKey(rowKey);
            return this;
        }
    }
}
