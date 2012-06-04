package com.github.seqware.model;

import com.github.seqware.factory.Factory;

/**
 * Implements core functionality that is shared by classes that are
 * controlled by permissions and {@link Versionable} (as well as {@link Taggable})
 *
 * @author dyuen
 */
public abstract class Molecule<T extends Molecule> extends Atom<T> implements ACLable, Versionable<T> {
    
    private ACL permissions = ACL.newBuilder().build();
    private transient boolean precedingChecked = false; 
    private transient T precedingVersion = null;
    
    @Override
    public void setPermissions(ACL permissions){
        this.permissions = permissions;
        precedingChecked = true;
        precedingVersion = (T) this;
        super.getManager().objectCreated(this, false);
    }
    
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
        if (!precedingChecked){
            this.precedingVersion = (T) Factory.getBackEnd().getPrecedingVersion(this);
        }
        precedingChecked = true;
        return this.precedingVersion;
    } 
    
    @Override
    public void setPrecedingVersion(T precedingVersion) {
        this.precedingChecked = true;
        this.precedingVersion = precedingVersion;
    } 
}
