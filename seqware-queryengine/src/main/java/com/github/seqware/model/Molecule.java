package com.github.seqware.model;

import com.github.seqware.factory.Factory;

/**
 * Implements core functionality that is shared by classes that are
 * controlled by permissions and {@link Versionable} (as well as {@link Taggable})
 *
 * @author dyuen
 */
public abstract class Molecule<T extends Molecule> extends Atom<T> implements ACLable, Versionable<T> {
    
    @Override
    public ACL getPermissions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setPermissions(ACL acl) throws SecurityException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long getVersion() {
        return Factory.getBackEnd().getVersion(this);
    }

    @Override
    public void setVersion(String version) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T getPrecedingVersion() {
        return (T) Factory.getBackEnd().getPrecedingVersion(this);
    }

    @Override
    public void setPrecedingVersion(T predecessor) {
        throw new UnsupportedOperationException("Not supported yet.");
    }    
}
