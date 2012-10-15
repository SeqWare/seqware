package com.github.seqware.queryengine.model.interfaces;

/**
 * Interface for object under access control.
 *
 * @author dyuen
 * @author jbaran
 * @version $Id: $Id
 */
public interface ACLable {
    
    
    /**
     * Set permissions for this
     *
     * @param permissions new set of permissions
     */
    public void setPermissions(ACL permissions);
    
    /**
     * Get permissions for the subject.
     *
     * @return Access control list object.
     */
    public ACL getPermissions();
}
