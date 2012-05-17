package com.github.seqware.model;

/**
 * Versions are automatically generated and iterated when a new entity is derived from a parent entity
 * TODO : should this really be changeable by the user as implied by the RESTful API?
 * @author dyuen
 */
public interface Versionable {
    
    /**
     * Get version for the subject
     * @return 
     */
    public long getVersion();
    
    /**
     * Allow the user to override the version for a subject
     * @param version version to set on subject
     */
    public void setVersion(long version);
}
