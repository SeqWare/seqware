package com.github.seqware.model;

import com.github.seqware.factory.Factory;
import com.github.seqware.factory.ModelManager;
import com.github.seqware.util.InMemoryIterable;
import com.github.seqware.util.SGID;
import com.github.seqware.util.SeqWareIterable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
public abstract class Atom<T extends Atom> extends Particle<T> implements Taggable, Versionable<T> {

    private List<Tag> tags = new ArrayList<Tag>();
    private transient boolean precedingChecked = false;
    private transient T precedingVersion = null;
    private SGID precedingSGID = null;    
    
    /**
     * Set the UUID, very dangerous, this should never be called outside of the
     * backend
     *
     * @param sgid new UUID
     */ 
    public void impersonate(SGID sgid, Date creationTimeStamp, SGID oldSGID) {
        super.impersonate(sgid);
        this.setTimestamp(creationTimeStamp);
        this.precedingSGID = oldSGID;
    }
    
    @Override
    public boolean associateTag(Tag tag) {
        tags.add(tag);
        this.getManager().particleStateChange(this, ModelManager.State.NEW_VERSION);
        //Factory.getBackEnd().associateTag(this, tag);
        return true;
    }
    
    @Override
    public T copy(boolean newSGID) {
        T  newParticle = super.copy(newSGID);
        if (newSGID){
            newParticle.setPrecedingSGID(this.getSGID());
        }
        return newParticle;
    }

    @Override
    public boolean dissociateTag(Tag tag) {
        tags.remove(tag);
        this.getManager().particleStateChange(this, ModelManager.State.NEW_VERSION);
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
            this.precedingVersion = (T) Factory.getFeatureStoreInterface().getParticleBySGID(precedingSGID);
        }
        precedingChecked = true;
        return this.precedingVersion;
    }

    @Override
    public void setPrecedingVersion(T precedingVersion) {
        this.getManager().particleStateChange(this, ModelManager.State.NEW_VERSION);
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
