package com.github.seqware.model;

/**
 * Implements core functionality that is shared by classes that are
 * controlled by permissions and versionable (as well as Taggable)
 * 
 * It does not look like we have anything that supports only two ... so far
 * ACL only supports ACLable and Tag only supports Versionable though
 *
 * @author dyuen
 */
public abstract class Molecule<T> extends Atom implements ACLable, Versionable<T> {
    
    public ACL getPermissions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean setPermissions(ACL acl) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long getVersion() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setVersion(String version) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Versionable<T> getPrecedingVersion() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setPrecedingVersion(Versionable<T> predecessor) {
        throw new UnsupportedOperationException("Not supported yet.");
    }    
}
