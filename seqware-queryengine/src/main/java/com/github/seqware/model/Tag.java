package com.github.seqware.model;

import com.github.seqware.model.interfaces.BaseBuilder;
import com.github.seqware.factory.ModelManager;
import com.github.seqware.model.impl.AtomImpl;
import com.github.seqware.util.SGID;
import com.github.seqware.util.SeqWareIterable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A Tag represents the first part of a tuple that can describe an attribute in
 * a GVF (ex: ID=ID_1 or Variant_seq=A,G or Reference_seq=G ).
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
public class Tag extends AtomImpl<Tag>  {

    private TagSet tagSet = null;
    private String key = null;
    private String predicate = "=";
    private Object value = null;
    private ValueType vType = null;
    
    public enum ValueType {STRING, BYTEARR, SGID, FLOAT, DOUBLE, LONG, INTEGER };

    /**
     * Create a new tag
     */
    private Tag() {
        super();
        vType = ValueType.STRING;
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
        return tagSet;
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
     * Get the type of value for the tag value
     * @return 
     */
    public ValueType getvType() {
        return vType;
    }
    
    

    @Override
    public boolean equals(Object obj) {
        // will cause recursion
        return EqualsBuilder.reflectionEquals(this, obj);
//        if (obj instanceof Tag) {
//            Tag other = (Tag) obj;
//            // check for nulls
//            if (this.value == null && other.value != null || this.value != null && other.value == null) {
//                return false;
//            }
//            // check parts guaranteed not to be null
//            boolean eqFirstPart = this.getSGID().equals(other.getSGID()) && this.key.equals(other.key) && this.predicate.equals(other.predicate);
//            boolean eqValue = (this.value == null && other.value == null) || this.value.equals(other.value);
//            return eqFirstPart && eqValue;
//        }
//        return false;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Create a new ACL builder
     *
     * @return
     */
    public static Tag.Builder newBuilder() {
        return new Tag.Builder();
    }

    /**
     * Create an ACL builder started with a copy of this
     *
     * @return
     */
    @Override
    public Tag.Builder toBuilder() {
        Tag.Builder b = new Tag.Builder();
        //b.tag = (Tag) this.copy(false);
        b.tag = this;
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static class Builder implements BaseBuilder {

        private Tag tag = new Tag();

        public Tag.Builder setKey(String key) {
            tag.key = key;
            return this;
        }

        public Tag.Builder setPredicate(String predicate) {
            tag.predicate = predicate;
            return this;
        }

        /**
         * Set the value to one of ValueType
         * @param value
         * @return 
         */
        public Tag.Builder setValue(Object value) {
            tag.value = value;
            if (value instanceof byte[]){
                tag.vType = ValueType.BYTEARR;
            } else if (value instanceof Double){
                tag.vType = ValueType.DOUBLE;
            } else if (value instanceof Float){
                tag.vType = ValueType.FLOAT;
            } else if (value instanceof Integer){
                tag.vType = ValueType.INTEGER;
            } else if (value instanceof Long){
                tag.vType = ValueType.LONG;
            } else if (value instanceof SGID){
                tag.vType = ValueType.SGID;
            } else if (value instanceof String){
                tag.vType = ValueType.STRING;
            }
            return this;
        }

        @Override
        public Tag build() {
            if (tag.key == null) {
                throw new RuntimeException("Invalid build of Tag");
            }
            tag.getManager().objectCreated(tag);
            return tag;
        }

        @Override
        public Builder setManager(ModelManager aThis) {
            tag.setManager(aThis);
            return this;
        }
    }
}
