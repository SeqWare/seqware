package com.github.seqware.model;

/**
 *
 * @author dyuen
 */
public interface Versionable {
    
    /**
     * Get version for the subject
     * @return Version of the subject.
     */
    public String getVersion();
    
    /**
     * Set version for a subject
     * @param version Version to set on subject.
     */
    public void setVersion(String version);

    /**
     * Returns the subject (if any) that represents a previous version.
     *
     * @return Versionable Previous (preceding) subject that represents an earlier version of the subject.
     */
    public Versionable getPrecedingVersion();

    /**
     * Sets the relationship to an earlier version of the subject.
     *
     * @param predecessor Preceding subject that represents an earlier version of this subject.
     */
    public void setPrecedingVersion(Versionable predecessor);
}
