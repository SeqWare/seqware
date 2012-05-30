package com.github.seqware.model;

import com.github.seqware.factory.Factory;

/**
 * Implements core functionality that is shared by classes that are
 * controlled by permissions and {@link Versionable} (as well as {@link Taggable})
 *
 * @author dyuen
 */
public abstract class Molecule<T extends Molecule> extends Atom<T> implements ACLable, Versionable<T> {
    
    private ACL permissions = new ACL();
    
    @Override
    public ACL getPermissions() {
        return permissions;
    }

    @Override
    public long getVersion() {
        return Factory.getBackEnd().getVersion(this);
    }

    @Override
    public T getPrecedingVersion() {
        return (T) Factory.getBackEnd().getPrecedingVersion(this);
    } 
}
