package com.github.seqware.model.impl;

import com.github.seqware.factory.Factory;
import com.github.seqware.factory.ModelManager;
import com.github.seqware.model.Atom;
import com.github.seqware.model.Tag;
import com.github.seqware.model.interfaces.Versionable;
import com.github.seqware.util.InMemoryIterable;
import com.github.seqware.util.SGID;
import com.github.seqware.util.SeqWareIterable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
     * Exposed timestamp of this Atom
     */
    private Date timestamp;
    /**
     * Current manager
     */
    private transient ModelManager manager = null;
    private List<Tag> tags = new ArrayList<Tag>();
    private transient boolean precedingChecked = false;
    private transient T precedingVersion = null;
    private SGID precedingSGID = null;    
    
    protected AtomImpl() {
        this.timestamp = new Date();
    }
    
    
    /**
     * Copy constructor, used to generate a shallow copy of a Atom ith
     * potentially a new timestamp and UUID
     *
     * @param newSGID whether or not to generate a new UUID and timestamp for
     * the new copy
     */
    @Override
    public T copy(boolean newSGID) {
        SGID oldUUID = this.sgid;
        // TODO This will have to be replaced with a stronger UUID generation method.
        if (newSGID) {
            this.sgid = new SGID();
            this.timestamp = new Date();
        }
        T newAtom = (T) SerializationUtils.clone(this);
        // copy over the transient properties for now
        ((AtomImpl)newAtom).setManager(this.manager);
        this.sgid = oldUUID;
        
        if (newSGID){
            ((AtomImpl)newAtom).setPrecedingSGID(this.sgid);
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
    public Date getCreationTimeStamp() {
        return timestamp;
    }

    /**
     * Set the timestamp, this should never be called outside of the backend
     *
     * @param timestamp new time stamp
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
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
        if (manager == null){
            Logger.getLogger(Atom.class.getName()).log(Level.WARNING, "Tried to get the ModelManager for an atom, but it was unmanaged.");
        }
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
        this.precedingSGID = oldSGID;
    }
    
    @Override
    public boolean associateTag(Tag tag) {
        tags.add(tag);
        this.getManager().AtomStateChange(this, ModelManager.State.NEW_VERSION);
        //Factory.getBackEnd().associateTag(this, tag);
        return true;
    }

    @Override
    public boolean dissociateTag(Tag tag) {
        tags.remove(tag);
        this.getManager().AtomStateChange(this, ModelManager.State.NEW_VERSION);
        //Factory.getBackEnd().dissociateTag(this, tag);
        return true;
    }

    @Override
    public SeqWareIterable<Tag> getTags() {
        return new InMemoryIterable(tags);//Factory.getBackEnd().getTags(this);
    }

    @Override
    public long getVersion() {
        if (this.precedingSGID == null){
            return 1;
        } else{
            return 1 + Factory.getBackEnd().getPrecedingVersion(this).getVersion();
        }
    }

    @Override
    public T getPrecedingVersion() {
        if (!precedingChecked) {
            this.precedingVersion = (T) Factory.getFeatureStoreInterface().getAtomBySGID(precedingSGID);
        }
        precedingChecked = true;
        return this.precedingVersion;
    }

    @Override
    public void setPrecedingVersion(T precedingVersion) {
        this.getManager().AtomStateChange(this, ModelManager.State.NEW_VERSION);
        this.precedingChecked = true;
        if (precedingVersion != null) {
            this.precedingVersion = precedingVersion;
            this.precedingSGID = precedingVersion.getSGID();
        } else {
            this.precedingVersion = null;
            this.precedingSGID = null;
        }
    }
    
    /**
     * Used in back-end to set previous version without side-effects
     * @param precedingSGID 
     */
    protected void setPrecedingSGID(SGID precedingSGID) {
        this.precedingSGID = precedingSGID;
    }

    /**
     * Used in back-end to get previous version ID
     * @param precedingSGID 
     */
    public SGID getPrecedingSGID() {
        return precedingSGID;
    }
}
