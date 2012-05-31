package com.github.seqware.model;

import com.github.seqware.factory.Factory;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import org.apache.commons.lang.SerializationUtils;

/**
 * Core functionality for all objects that will need to be tracked within the
 * database back-end. Handles the addX and updateX methods in
 * FeatureStoreInterface
 *
 * @author dyuen
 */
public abstract class Particle<T extends Particle> implements Serializable {

    /**
     * Internally used unique identifier of this particle
     */
    private UUID uuid;
    /**
     * Exposed timestamp of this particle
     */
    private Date timestamp;

    protected Particle() {
        // TODO This will have to be replaced with a stronger UUID generation method.
        this.uuid = UUID.randomUUID();
        this.timestamp = new Date();
    }

    /**
     * Copy constructor, used to generate a shallow copy of a particle ith 
     * potentially a new timestamp and UUID
     *
     * @param newUUID whether or not to generate a new UUID and timestamp for the new copy
     */
    public T copy(boolean newUUID) {
        UUID oldUUID = this.uuid;
        // TODO This will have to be replaced with a stronger UUID generation method.
        if (newUUID) {
            this.uuid = UUID.randomUUID();
            this.timestamp = new Date();
        }
        T newParticle = (T) SerializationUtils.clone(this);
        this.uuid = oldUUID;
        return newParticle;
    }

    /**
     * Notify the back-end that it should keep track of the current object.
     * A store operation cascades downward (i.e. storing a ReferenceSet will
     * store all children References as well) 
     * 
     */
    public void store() {
        Factory.getBackEnd().store(this);
    }

    /**
     * Notify the back-end that it should record the changes made to the current
     * object. Updates cascade downward (i.e. changing a ReferenceSet will
     * result in a copy-on-write that copies all children References as well)
     * Note that the UUID of this may change due to copy-on-write as this may now 
     * be a reference to a new entity in the database due to copy-on-write
     */
    public void update()  {
        Factory.getBackEnd().update(this);
    }

    /**
     * Update the current object with any changes that may have been made to the
     * current object
     *
     */
    public void refresh()  {
        Factory.getBackEnd().refresh(this);
    }

//    /**
//     * Delete the current object (will cascade in the case of sets to their
//     * children)
//     *
//     */
//    public void delete()  {
//        Factory.getBackEnd().delete(this);
//    }

    /**
     * Get the universally unique identifier of this object. This should be
     * unique across the whole backend and not just this resource
     * @return unique identifier for this (version of) resource 
     */
    public UUID getUUID() {
        return this.uuid;
    }

    /**
     * Get a creation time for this resource. Associated resource timestamps for
     * older versions can be accessed via the {@link Versionable} interface when
     * applicable
     *
     * @return the creation time stamp for this particular instance of the
     * resource
     */
    public Date getCreationTimeStamp() {
        return timestamp;
    }
    
    /**
     * Set the timestamp, this should never be called outside of the backend
     * @param timestamp new time stamp
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Set the UUID, very dangerous, this should never be called outside of the
     * backend
     * @param uuid new UUID 
     */
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }
    
    @Override
    public String toString(){
        return this.uuid.toString() + " " + super.toString();
    }
    
    
}
