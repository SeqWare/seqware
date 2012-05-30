package com.github.seqware.model;

/**
 * Interface for versioning, versions are automatically generated and iterated
 * when a new entity is derived from a parent entity. 
 *
 * @author dyuen
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
}
