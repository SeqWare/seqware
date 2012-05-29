package com.github.seqware.model;

import com.github.seqware.factory.Factory;
import java.io.Serializable;
import java.security.AccessControlException;
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
     * Internally used unique identifier of this feature.
     */
    private UUID uuid;

    protected Particle() {
        // TODO This will have to be replaced with a stronger UUID generation method.
        this.uuid = UUID.randomUUID();
    }

    /**
     * Copy constructor, used to generate a shallow copy of a particle with a
     * new UUID
     *
     * @param particle
     */
    public T copy(boolean newUUID) {
        UUID oldUUID = this.uuid;
        // TODO This will have to be replaced with a stronger UUID generation method.
        if (newUUID){
            this.uuid = UUID.randomUUID();
        }
        T newParticle = (T)SerializationUtils.clone(this);
        this.uuid = oldUUID;
        return newParticle;
    }

    /**
     * Notify the back-end that it should keep track of the current object.
     *
     * @throws AccessControlException if the user is not allowed to write to the
     * parent object (i.e. create a Reference in a ReferenceSet without write
     * permission to that ReferenceSet)
     */
    public void add() throws AccessControlException {
        Factory.getBackEnd().store(this);
    }

    /**
     * Notify the back-end that it should record the changes made to the current
     * object. Updates cascade downward (i.e. changing a ReferenceSet will
     * result in a copy-on-write that copies all children References as well)
     *
     * @throws AccessControlException if the user does not have permission to
     * change this object
     * @return Due to copy-on-write, this can result in a new object that the
     * user may wish to subsequently work on
     */
    public T update() throws AccessControlException {
        return (T) Factory.getBackEnd().update(this);
    }

    /**
     * Update the current object with any changes that may have been made to the
     * current object
     *
     * @throws AccessControlException if the user has lost permission to read
     * the object
     * @return Due to copy-on-write, this may return a new object with updated
     * information
     */
    public T refresh() throws AccessControlException {
        return (T) Factory.getBackEnd().refresh(this);
    }

    /**
     * Delete the current object (will cascade in the case of sets to their
     * children)
     *
     * @throws AccessControlException if the user does not have permission to
     * delete this (or children) objects
     */
    public void delete() throws AccessControlException {
        Factory.getBackEnd().delete(this);
    }

    /**
     * Get the universally unique identifier of this object. This should be
     * unique across the whole backend and not just this resource
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
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
