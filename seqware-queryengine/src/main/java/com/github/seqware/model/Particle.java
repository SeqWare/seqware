package com.github.seqware.model;

import com.github.seqware.factory.ModelManager;
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
public abstract class Particle<T extends Particle> implements Serializable, Buildable {
    
    /**
     * Internally used unique identifier of this particle
     */
    private UUID uuid;
    /**
     * Exposed timestamp of this particle
     */
    private Date timestamp;
    /**
     * Current manager
     */
    private transient ModelManager manager = null;

    
    protected Particle() {
        // TODO This will have to be replaced with a stronger UUID generation method.
        this.uuid = UUID.randomUUID();
        this.timestamp = new Date();
    }

    /**
     * Copy constructor, used to generate a shallow copy of a particle ith
     * potentially a new timestamp and UUID
     *
     * @param newUUID whether or not to generate a new UUID and timestamp for
     * the new copy
     */
    public T copy(boolean newUUID) {
        UUID oldUUID = this.uuid;
        // TODO This will have to be replaced with a stronger UUID generation method.
        if (newUUID) {
            this.uuid = UUID.randomUUID();
            this.timestamp = new Date();
        }
        T newParticle = (T) SerializationUtils.clone(this);
        // copy over the transient properties for now
        newParticle.setManager(this.manager);
        this.uuid = oldUUID;
        return newParticle;
    }

    /**
     * Get the universally unique identifier of this object. This should be
     * unique across the whole backend and not just this resource
     *
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
     * @param uuid new UUID
     */ 
    protected void impersonate(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return this.uuid.toString() + " " + super.toString();
    }

    /**
     * Get the model manager for this particle
     *
     * @return
     */
    public ModelManager getManager() {
        return manager;
    }

    /**
     * Set the model manager for this particle
     *
     * @param manager
     */
    public void setManager(ModelManager manager) {
        this.manager = manager;
    }
    
}
