package com.github.seqware.queryengine.model.impl;

import com.github.seqware.queryengine.factory.ModelManager;
import com.github.seqware.queryengine.model.Molecule;
import com.github.seqware.queryengine.model.interfaces.ACL;
import com.github.seqware.queryengine.model.interfaces.ACLable;
import com.github.seqware.queryengine.model.interfaces.TTLable;
import java.util.Date;

/**
 * Implements core functionality that is shared by classes that are controlled
 * by permissions and Versionable, Taggable, TTLable via {@link AtomImpl})
 *
 * @author dyuen
 */
public abstract class MoleculeImpl<T extends Molecule> extends AtomImpl<T> implements Molecule<T>, ACLable, TTLable {

    private ACL permissions = ACL.newBuilder().build();
    private long expiryTime = TTLable.FOREVER;

    @Override
    public void setPermissions(ACL permissions) {
        this.permissions = permissions;
        if (this.getManager() != null){
            this.getManager().atomStateChange(this, ModelManager.State.NEW_VERSION);  
        }
    }

    @Override
    public ACL getPermissions() {
        return permissions;
    }

    @Override
    public void setTTL(Date time, boolean cascade) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTTL(int hours, boolean cascade) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Date getExpiryDate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getTTL() {
        return 0;
    }

    @Override
    public boolean getCascade() {
        // molecules in general do not contain anything to cascade to
        return false;
    }
}
