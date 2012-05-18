package com.github.seqware.model;

import java.lang.SecurityException;

/**
 * Interface for object under access control.
 *
 * @author dyuen
 * @author jbaran
 */
public interface ACLable {
    
    /**
     * Get permissions for the subject.
     * @return Access control list object.
     */
    public ACL getPermissions();
    
    /**
     * Set permissions for a subject
     * @param acl new permissions to set on the subject
     * @throws SecurityException Thrown if the permissions cannot be set under the current ACL rules.
     */
    public void setPermissions(ACL acl) throws SecurityException;
}
