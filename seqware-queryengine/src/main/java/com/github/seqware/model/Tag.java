package com.github.seqware.model;

import com.github.seqware.impl.SimpleModelManager;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A Tag represents the first part of a tuple that can describe an attribute
 * in a GVF (ex: ID=ID_1 or Variant_seq=A,G or Reference_seq=G ).
 * 
 * Tags always have subjects, they have a default predicate "=" and they may
 * or may not have objects. A non-specified value is represented as null
 * 
 * Tags themselves are immutable (and thus do not
 * need {@link Versionable} but adding and removing tags on objects is 
 * of course possible given the right permissions
 * @author dyuen
 */
public class Tag extends Particle {
    
    private TagSet tagSet;
    private String key;
    private String predicate = "=";
    private String value = null;
    
    /**
     * Create a new tag
     */
    private Tag() {
        super();
    }

    /**
     * Get the key. Examples include ID, Variant_seq, or Reference_seq 
     * @return String key
     */
    public String getKey() {
        return key;
    }

    /**
     * Get a reference to the parent TagSet
     * @return parent TagSet
     */
    public TagSet getTagSet() {
        return tagSet;
    }

    /**
     * Get the value. Examples include ID_1, A, or G
     * @return String value
     */
    public String getValue() {
        return value;
    }

    /**
     * Get the predicate. Examples include "="
     * @return String predicate
     */
    public String getPredicate() {
        return predicate;
    }

    
    @Override
    public boolean equals(Object obj) {
        // will cause recursion
        //return EqualsBuilder.reflectionEquals(this, obj);
        if (obj instanceof Tag) {
            Tag other = (Tag) obj;
            if (this.value == null && other.value != null || this.value != null && other.value == null) {
                return false;
            }
            return this.getUUID().equals(other.getUUID()) && this.key.equals(other.key) 
                    && this.predicate.equals(other.predicate)&& (this.value != null && this.value.equals(other.value));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
    
    /**
     * Create a new ACL builder
     * @return 
     */
    public static Tag.Builder newBuilder() {
        return new Tag.Builder();
    }
    
    /**
     * Create an ACL builder started with a copy of this
     * @return 
     */
    public Tag.Builder toBuilder(){
        Tag.Builder b = new Tag.Builder();
        b.tag = (Tag) this.copy(true);
        return b;
    }

    public static class Builder {

        private Tag tag = new Tag();

        public Tag.Builder setKey(String key) {
            tag.key = key;
            return this;
        }

        public Tag.Builder setPredicate(String predicate) {
            tag.predicate = predicate;
            return this;
        }

        public Tag.Builder setValue(String value) {
            tag.value = value;
            return this;
        }
        
        public Tag build() {
           return build(true);
        }

        public Tag build(boolean newObject) {
            if (tag.key == null){
                throw new RuntimeException("Invalid build of Tag"); 
            }
            tag.getManager().objectCreated(tag, newObject);
            return tag;
        }

        public Builder setManager(SimpleModelManager aThis) {
            tag.setManager(aThis);
            return this;
        }
    }
}
