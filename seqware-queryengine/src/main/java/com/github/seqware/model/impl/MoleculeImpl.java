package com.github.seqware.model.impl;

import com.github.seqware.model.interfaces.ACL;
import com.github.seqware.model.interfaces.ACLable;
import com.github.seqware.factory.ModelManager;
import com.github.seqware.model.*;

/**
 * Implements core functionality that is shared by classes that are controlled
 * by permissions and {@link Versionable} (as well as {@link Taggable})
 *
 * @author dyuen
 */
public abstract class MoleculeImpl<T extends Molecule> extends AtomImpl<T> implements Molecule<T>, ACLable {

    private ACL permissions = ACL.newBuilder().build();

    @Override
    public void setPermissions(ACL permissions) {
        this.permissions = permissions;
        this.getManager().AtomStateChange(this, ModelManager.State.NEW_VERSION);  
    }

    @Override
    public ACL getPermissions() {
        return permissions;
    }


}
