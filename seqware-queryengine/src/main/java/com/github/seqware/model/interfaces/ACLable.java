package com.github.seqware.model.interfaces;

/**
 * Interface for object under access control.
 *
 * @author dyuen
 * @author jbaran
 */
public interface ACLable {
    
    
    /**
     * Set permissions for this
     * @param permissions new set of permissions
     */
    public void setPermissions(ACL permissions);
    
    /**
     * Get permissions for the subject.
     * @return Access control list object.
     */
    public ACL getPermissions();
}
