package com.github.seqware.model;

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
}
