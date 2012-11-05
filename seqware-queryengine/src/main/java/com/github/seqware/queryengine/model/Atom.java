package com.github.seqware.queryengine.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.seqware.queryengine.model.interfaces.Versionable;
import com.github.seqware.queryengine.model.interfaces.Taggable;
import com.github.seqware.queryengine.model.interfaces.Buildable;
import com.github.seqware.queryengine.util.SGID;
import java.io.Serializable;
import java.util.Date;

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
public interface Atom<T extends Atom> extends Taggable, Versionable<T>, Serializable, Buildable {

    /**
     * Copy constructor, used to generate a shallow copy of a Atom ith
     * potentially a new timestamp and UUID
     *
     * @param newSGID whether or not to generate a new UUID and timestamp for
     * the new copy
     * @return a T object.
     */
    public T copy(boolean newSGID);

    /**
     * Get the universally unique identifier of this object. This should be
     * unique across the whole backend and not just this resource
     *
     * @return unique identifier for this (version of) resource
     */
    @JsonIgnore
    public SGID getSGID();

    /**
     * Get a creation time for this resource. Associated resource timestamps for
     * older versions can be accessed via the {@link com.github.seqware.queryengine.model.interfaces.Versionable} interface when
     * applicable
     *
     * @return the creation time stamp for this particular instance of the
     * resource
     */
    public Date getTimestamp();
    
    /**
     * Get the serializationVersion for this particular atom. Allows more advanced
     * operations for serialization-aware plug-ins.
     *
     * @return a int.
     */
    @JsonIgnore
    public int getExternalSerializationVersion();

}
