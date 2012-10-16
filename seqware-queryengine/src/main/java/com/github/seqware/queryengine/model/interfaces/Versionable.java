package com.github.seqware.queryengine.model.interfaces;

/**
 * Interface for versioning, versions are automatically generated and iterated
 * when a new entity is derived from a parent entity.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public interface Versionable<T extends Versionable> {

    /**
     * Get version for the subject
     *
     * @return Version of the subject.
     */
    public long getVersion();

    /**
     * Returns the subject (if any) that represents a previous version.
     *
     * @return Previous (preceding) subject that represents an earlier version
     * of the subject.
     */
    public T getPrecedingVersion();
    
    /**
     * Explicitly set the previous version of a Atom. Setting this to null
     * will make this a completely new object in the back-end.
     *
     * @param Atom previous version
     */
    public void setPrecedingVersion(T Atom); 
    
}
