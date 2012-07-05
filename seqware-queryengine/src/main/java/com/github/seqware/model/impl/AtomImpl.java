package com.github.seqware.model.impl;

import com.github.seqware.factory.Factory;
import com.github.seqware.factory.ModelManager;
import com.github.seqware.model.Atom;
import com.github.seqware.model.Tag;
import com.github.seqware.model.interfaces.Versionable;
import com.github.seqware.util.InMemoryIterable;
import com.github.seqware.util.LazyReference;
import com.github.seqware.util.SGID;
import com.github.seqware.util.SeqWareIterable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * Implements core functionality that is shared by all classes that require
 * tags.
 *
 * Deriving Feature and FeatureSet from one base class facilitates code re-use
 * for interface implementations that are shared by those classes.
 *
 * @author jbaran
 * @author dyuen
 */
public abstract class AtomImpl<T extends Atom> implements Atom<T> {

    /**
     * Unique identifier of this Atom
     */
    private SGID sgid = new SGID();
    /**
     * Exposed clientTimestamp of this Atom
     */
    private Date clientTimestamp;
    /**
     * Current manager
     */
    private transient ModelManager manager = null;
    private List<Tag> tags = new ArrayList<Tag>();
    
    private LazyReference<T> precedingVersion = new LazyReference<T>(this.getHBaseClass());

    protected AtomImpl() {
        this.clientTimestamp = new Date();
    }

    /**
     * Copy constructor, used to generate a shallow copy of a Atom with
     * potentially a new clientTimestamp and UUID
     *
     * @param newSGID whether or not to generate a new UUID and clientTimestamp for
     * the new copy
     */
    @Override
    public T copy(boolean newSGID) {
        SGID oldUUID = this.sgid;
        // TODO This will have to be replaced with a stronger UUID generation method.
        if (newSGID) {
            this.sgid = new SGID();
            this.clientTimestamp = new Date();
        }
        T newAtom = (T) SerializationUtils.clone(this);
        // copy over the transient properties for now
        ((AtomImpl) newAtom).setManager(this.manager);
        this.sgid = oldUUID;

        if (newSGID) {
            ((AtomImpl) newAtom).setPrecedingSGID(this.sgid);
        }

        return newAtom;
    }

    /**
     * Get the universally unique identifier of this object. This should be
     * unique across the whole backend and not just this resource
     *
     * @return unique identifier for this (version of) resource
     */
    @Override
    public SGID getSGID() {
        return this.sgid;
    }

    /**
     * Get a creation time for this resource. Associated resource timestamps for
     * older versions can be accessed via the {@link Versionable} interface when
     * applicable
     *
     * @return the creation time stamp for this particular instance of the
     * resource
     */
    @Override
    public Date getTimestamp() {
        return clientTimestamp;
    }

    /**
     * Set the clientTimestamp, this should never be called outside of the backend
     *
     * @param clientTimestamp new time stamp
     */
    public void setTimestamp(Date timestamp) {
        this.clientTimestamp = timestamp;
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

    @Override
    public String toString() {
        return this.sgid.toString() + " " + super.toString();
    }

    /**
     * Get the model manager for this Atom
     *
     * @return
     */
    public ModelManager getManager() {
        // happens pretty often now when building model objects
//        if (manager == null){
//            Logger.getLogger(Atom.class.getName()).log(Level.WARNING, "Tried to get the ModelManager for an atom, but it was unmanaged.");
//        }
        return manager;
    }

    /**
     * Set the model manager for this Atom
     *
     * @param manager
     */
    public void setManager(ModelManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AtomImpl) {
            return EqualsBuilder.reflectionEquals(this.sgid, ((AtomImpl) obj).sgid);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return sgid.hashCode();
    }

    /**
     * Set the UUID, very dangerous, this should never be called outside of the
     * backend
     *
     * @param sgid new UUID
     */
    public void impersonate(SGID sgid, Date creationTimeStamp, SGID oldSGID) {
        this.impersonate(sgid);
        this.setTimestamp(creationTimeStamp);
        this.precedingVersion.setSGID(oldSGID);
    }

    @Override
    public boolean associateTag(Tag tag) {
        tags.add(tag);
        if (this.getManager() != null) {
            this.getManager().atomStateChange(this, ModelManager.State.NEW_VERSION);
        }
        //Factory.getBackEnd().associateTag(this, tag);
        return true;
    }

    @Override
    public boolean dissociateTag(Tag tag) {
        tags.remove(tag);
        if (this.getManager() != null) {
            this.getManager().atomStateChange(this, ModelManager.State.NEW_VERSION);
        }
        //Factory.getBackEnd().dissociateTag(this, tag);
        return true;
    }

    @Override
    public SeqWareIterable<Tag> getTags() {
        return new InMemoryIterable(tags);//Factory.getBackEnd().getTags(this);
    }

    @Override
    public long getVersion() {
        if (this.precedingVersion.get() == null) {
            return 1;
        } else {
            return 1 + Factory.getBackEnd().getPrecedingVersion(this).getVersion();
        }
    }

    @Override
    public T getPrecedingVersion() {
        return this.precedingVersion.get();
    }

    @Override
    public void setPrecedingVersion(T precedingVersion) {
        // inform the model manager that this is a new version of an object now
        if (this.getManager() != null) {
            this.getManager().atomStateChange(this, ModelManager.State.NEW_VERSION);
        }
        this.precedingVersion.set(precedingVersion);
    }

    /**
     * Used in back-end to set previous version without side-effects
     *
     * @param precedingSGID
     */
    protected void setPrecedingSGID(SGID precedingSGID) {
        this.precedingVersion.setSGID(precedingSGID);
        //this.precedingSGID = precedingSGID;
    }

    /**
     * Used in back-end to get previous version ID
     *
     * @param precedingSGID
     */
    public SGID getPrecedingSGID() {
        return this.precedingVersion.getSGID();
        //return precedingSGID;
    }

    /**
     * Get the model class for the HBase where this obj should be stored
     *
     * @param obj
     * @return
     */
    public abstract Class getHBaseClass();

    /**
     * Get the HBase table prefix where this obj should be stored
     *
     * @param obj
     * @return
     */
    public abstract String getHBasePrefix();
}
