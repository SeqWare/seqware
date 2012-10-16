package com.github.seqware.queryengine.model.impl;

import com.github.seqware.queryengine.dto.QESupporting.TagPB;
import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.impl.protobufIO.TagIO;
import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.Tag;
import com.github.seqware.queryengine.model.TagSet;
import com.github.seqware.queryengine.model.interfaces.Versionable;
import com.github.seqware.queryengine.util.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.log4j.Logger;

/**
 * Implements core functionality that is shared by all classes that require
 * tags.
 *
 * Deriving Feature and FeatureSet from one base class facilitates code re-use
 * for interface implementations that are shared by those classes.
 *
 * @author jbaran
 * @author dyuen
 * @version $Id: $Id
 */
public abstract class AtomImpl<T extends Atom> implements Atom<T> {

    private final static transient TagIO tagIO = new TagIO();
    /**
     * Unique identifier of this Atom
     */
    private SGID sgid = new SGID();
    /**
     * Exposed timestamp of this Atom
     */
    //private Date clientTimestamp;
    private int externalSerializationVersion = SWQEFactory.getSerialization().getSerializationConstant();

    /** {@inheritDoc} */
    @Override
    public int getExternalSerializationVersion() {
        return externalSerializationVersion;
    }

    /**
     * <p>Setter for the field <code>externalSerializationVersion</code>.</p>
     *
     * @param externalSerializationVersion a int.
     */
    public void setExternalSerializationVersion(int externalSerializationVersion) {
        this.externalSerializationVersion = externalSerializationVersion;
    }
    /**
     * Current manager
     */
    private transient CreateUpdateManager manager = null;
    /**
     * Map from rowkey for tagSet => name for tag => value
     */
    private Map<String, Map<String, Tag>> tags = new HashMap<String, Map<String, Tag>>();
    private LazyReference<T> precedingVersion = new LazyReference<T>(this.getHBaseClass());

    /**
     * <p>Constructor for AtomImpl.</p>
     */
    protected AtomImpl() {
        //this.clientTimestamp = new Date();
    }

    /**
     * {@inheritDoc}
     *
     * Copy constructor, used to generate a shallow copy of a Atom with
     * potentially a new clientTimestamp and UUID
     */
    @Override
    public T copy(boolean newSGID) {
        AtomImpl newAtom;
        if (this instanceof Tag) {
            TagPB m2pb = tagIO.m2pb((Tag) this);
            Tag pb2m = tagIO.pb2m(m2pb);
            newAtom = pb2m;
        } else {
            newAtom = (AtomImpl) SerializationUtils.clone(this);
            // copy over the transient properties for now, but not for Tags
            newAtom.setManager(this.manager);
        }
        if (newSGID) {
            newAtom.impersonate(new SGID());
        } else {
            newAtom.getSGID().setBackendTimestamp(new Date());
            assert (!newAtom.getSGID().equals(this.sgid));
        }

//        SGID oldUUID = this.sgid;
//        // TODO This will have to be replaced with a stronger UUID generation method.
//        if (newSGID) {
//            this.sgid = new SGID();
//            //this.clientTimestamp = new Date();
//        } else{
//            this.sgid = new SGID(this.sgid);
//            this.sgid.setBackendTimestamp(new Date());
//            assert(!oldUUID.equals(this.sgid));
//        }
//        T newAtom = (T) SerializationUtils.clone(this);
//        // copy over the transient properties for now
//        ((AtomImpl) newAtom).setManager(this.manager);
//        this.sgid = oldUUID;
//
        if (newSGID) {
            ((AtomImpl) newAtom).setPrecedingSGID(this.sgid);
        }

        return (T) newAtom;
    }
    
    /** {@inheritDoc} */
    @Override
    public  NestedLevel getNestedTags(TagSet tagSet) {
        return getNestedTags(tagSet.getSGID().getRowKey());
    }

    /** {@inheritDoc} */
    @Override
    public NestedLevel getNestedTags(String tagSetRowKey) {
        NestedLevel rootLevel = new NestedLevel();
        for (Tag t : this.tags.get(tagSetRowKey).values()) {
            String[] keyArr = t.getKey().split(Tag.SEPARATOR);
            NestedLevel point = rootLevel;
            for (int i = 0; i < keyArr.length - 1; i++) {
                String seg = keyArr[i];
                // create a level for all but the last level
                if (!point.getChildMaps().containsKey(seg)) {
                    point.getChildMaps().put(seg, new NestedLevel());
                }
                // then move down before the next segment
                point = point.getChildMaps().get(seg);
            }
            // add the tag as a child at the appropriate level
            point.getChildTags().put(keyArr[keyArr.length - 1], t);
        }
        return rootLevel;
    }

    /**
     * {@inheritDoc}
     *
     * Get the universally unique identifier of this object. This should be
     * unique across the whole backend and not just this resource
     */
    @Override
    public SGID getSGID() {
        return this.sgid;
    }

    /**
     * {@inheritDoc}
     *
     * Get a creation time for this resource. Associated resource timestamps for
     * older versions can be accessed via the {@link Versionable} interface when
     * applicable
     */
    @Override
    public Date getTimestamp() {
        return this.getSGID().getBackendTimestamp();
        //return clientTimestamp;
    }

    /**
     * Set the clientTimestamp, this should never be called outside of the
     * backend
     *
     * @param timestamp a {@link java.util.Date} object.
     */
    public void setTimestamp(Date timestamp) {
        this.getSGID().setBackendTimestamp(timestamp);
        //this.clientTimestamp = timestamp;
    }

    /**
     * Set the UUID, very dangerous, this should never be called outside of the
     * backend
     *
     * @param sgid new SGID
     */
    protected void impersonate(SGID sgid) {
        this.sgid = sgid;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return this.sgid.toString() + " " + super.toString();
    }

    /**
     * Get the model manager for this Atom
     *
     * @return a {@link com.github.seqware.queryengine.factory.CreateUpdateManager} object.
     */
    public CreateUpdateManager getManager() {
        // happens pretty often now when building model objects
//        if (manager == null){
//            Logger.getLogger(Atom.class.getName()).log(Level.WARNING, "Tried to get the CreateUpdateManager for an atom, but it was unmanaged.");
//        }
        return manager;
    }

    /**
     * Set the model manager for this Atom
     *
     * @param manager a {@link com.github.seqware.queryengine.factory.CreateUpdateManager} object.
     */
    public void setManager(CreateUpdateManager manager) {
        this.manager = manager;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AtomImpl) {
            return EqualsBuilder.reflectionEquals(this.sgid, ((AtomImpl) obj).sgid);
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return sgid.hashCode();
    }

    /**
     * Set the UUID, very dangerous, this should never be called outside of the
     * backend
     *
     * @param sgid new UUID
     * @param oldSGID a {@link com.github.seqware.queryengine.util.SGID} object.
     */
    public void impersonate(SGID sgid, SGID oldSGID) {
        this.impersonate(sgid);
        //this.setTimestamp(creationTimeStamp);
        this.precedingVersion.setSGID(oldSGID);
    }

    /** {@inheritDoc} */
    @Override
    public boolean associateTag(Tag tag) {
        if (tag.getTagSetSGID() == null) {
            Logger.getLogger(TagIO.class.getName()).fatal("Tag " + tag.getKey() + " is not owned by a tagset");
            throw new RuntimeException("Tag cannot be associated without a TagSet");
        }

        String rowKey = tag.getTagSetSGID().getRowKey();
        if (!tags.containsKey(rowKey)) {
            tags.put(rowKey, new HashMap<String, Tag>());
        }
        tags.get(rowKey).put(tag.getKey(), tag);
        // this only makes sense if we've attached to a FeatureSet already
        if (this.getManager() != null && this.getSGID() instanceof FSGID) {
            this.getManager().atomStateChange(this, CreateUpdateManager.State.NEW_VERSION);
        }
        //Factory.getBackEnd().associateTag(this, tag);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean dissociateTag(Tag tag) {
        tags.get(tag.getTagSetSGID().getRowKey()).remove(tag.getKey());
        if (this.getManager() != null) {
            this.getManager().atomStateChange(this, CreateUpdateManager.State.NEW_VERSION);
        }
        //Factory.getBackEnd().dissociateTag(this, tag);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public SeqWareIterable<Tag> getTags() {
        return new TagValueIterable(tags);//Factory.getBackEnd().getTags(this);
    }
    
    /** {@inheritDoc} */
    @Override
    public Tag getTagByKey(TagSet tagSet, String key) {
        return getTagByKey(tagSet.getSGID().getRowKey(), key);
    }

    /** {@inheritDoc} */
    @Override
    public Tag getTagByKey(String tagSet, String key) {
        if (!tags.containsKey(tagSet)){
            return null;
        }
        return tags.get(tagSet).get(key);
    }

    /** {@inheritDoc} */
    @Override
    public long getVersion() {
        if (this.precedingVersion.get() == null) {
            return 1;
        } else {
            return 1 + this.precedingVersion.get().getVersion();
        }
    }

    /** {@inheritDoc} */
    @Override
    public T getPrecedingVersion() {
        return this.precedingVersion.get();
    }

    /** {@inheritDoc} */
    @Override
    public void setPrecedingVersion(T precedingVersion) {
        // inform the model manager that this is a new version of an object now
        if (this.getManager() != null) {
            this.getManager().atomStateChange(this, CreateUpdateManager.State.NEW_VERSION);
        }
        this.precedingVersion.set(precedingVersion);
    }

    /**
     * Used in back-end to set previous version without side-effects
     *
     * @param precedingSGID a {@link com.github.seqware.queryengine.util.SGID} object.
     */
    public void setPrecedingSGID(SGID precedingSGID) {
        this.precedingVersion.setSGID(precedingSGID);
        //this.precedingSGID = precedingSGID;
    }

    /**
     * Used in back-end to get previous version ID
     *
     * @return a {@link com.github.seqware.queryengine.util.SGID} object.
     */
    public SGID getPrecedingSGID() {
        return this.precedingVersion.getSGID();
        //return precedingSGID;
    }

    /**
     * Get the model class for the HBase where this obj should be stored
     *
     * @return a {@link java.lang.Class} object.
     */
    public abstract Class getHBaseClass();

    /**
     * Get the HBase table prefix where this obj should be stored
     *
     * @return a {@link java.lang.String} object.
     */
    public abstract String getHBasePrefix();

    public static class TagValueIterable implements SeqWareIterable<Tag> {

        private final Map<String, Map<String, Tag>> values;

        public TagValueIterable(Map<String, Map<String, Tag>> tags) {
            this.values = tags;
        }

        @Override
        public long getCount() {
            int count = 0;
            for (Map<String, Tag> c : values.values()) {
                count += c.size();
            }
            return count;
        }

        @Override
        public Iterator<Tag> iterator() {
            return new TagValueIterator(values);
        }
    }

    public static class TagValueIterator implements Iterator<Tag> {

        private final Iterator<Map<String, Tag>> iterator;
        private Iterator<Tag> line = null;

        public TagValueIterator(Map<String, Map<String, Tag>> tags) {
            this.iterator = tags.values().iterator();
            if (iterator.hasNext()) {
                this.line = iterator.next().values().iterator();
            }
        }

        @Override
        public boolean hasNext() {
            if (line != null && line.hasNext()) {
                return true;
            }
            while (iterator.hasNext()) {
                line = iterator.next().values().iterator();
                if (line.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Tag next() {
            if (line != null && line.hasNext()) {
                return line.next();
            }
            while (iterator.hasNext()) {
                line = iterator.next().values().iterator();
                if (line.hasNext()) {
                    return line.next();
                }
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
