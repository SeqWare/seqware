package com.github.seqware.model;

/**
 *
 * @author dyuen
 */
public interface ACLable {
    
    /**
     * Get permissions for the subject
     * @return 
     */
    public ACL getPermissions();
    
    /**
     * Set permissions for a subject
     * @param acl new permissions to set on the subject
     */
    public void setPermissions(ACL acl);
}
