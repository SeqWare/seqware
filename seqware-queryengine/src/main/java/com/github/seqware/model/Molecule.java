package com.github.seqware.model;

import com.github.seqware.factory.ModelManager;

/**
 * Implements core functionality that is shared by classes that are controlled
 * by permissions and {@link Versionable} (as well as {@link Taggable})
 *
 * @author dyuen
 */
public abstract class Molecule<T extends Molecule> extends Atom<T> implements ACLable {

    private ACL permissions = ACL.newBuilder().build();

    @Override
    public void setPermissions(ACL permissions) {
        this.permissions = permissions;
        this.getManager().particleStateChange(this, ModelManager.State.NEW_VERSION);  
    }

    @Override
    public ACL getPermissions() {
        return permissions;
    }


}
