package com.github.seqware.model;

/**
 * Versions are automatically generated and iterated when a new entity is derived from a parent entity
 * TODO : should this really be changeable by the user as implied by the RESTful API?
 * @author dyuen
 */
public interface Versionable {
    
    /**
     * Get version for the subject
     * @return Version of the subject.
     */
    public long getVersion();

    /*
     * Set version for a subject
     * @param version Version to set on subject.
     */
    public void setVersion(String version);

    /**
     * Returns the subject (if any) that represents a previous version.
     *
     * @return Previous (preceding) subject that represents an earlier version of the subject.
     */
    public Versionable getPrecedingVersion();

    /**
     * Sets the relationship to an earlier version of the subject.
     *
     * @param predecessor Preceding subject that represents an earlier version of this subject.
     */
    public void setPrecedingVersion(Versionable predecessor);
}
