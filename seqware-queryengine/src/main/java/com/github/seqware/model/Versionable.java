package com.github.seqware.model;

/**
 *
 * @author dyuen
 */
public interface Versionable {
    
    /**
     * Get version for the subject
     * @return 
     */
    public String getVersion();
    
    /**
     * Set version for a subject
     * @param String version to set on subject
     */
    public void setVersion(String version);
}
