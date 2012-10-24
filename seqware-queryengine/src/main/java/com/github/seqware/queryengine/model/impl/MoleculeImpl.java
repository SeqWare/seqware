package com.github.seqware.queryengine.model.impl;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.model.Molecule;
import com.github.seqware.queryengine.model.interfaces.ACL;
import com.github.seqware.queryengine.model.interfaces.ACLable;
import com.github.seqware.queryengine.model.interfaces.TTLable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Implements core functionality that is shared by classes that are controlled
 * by permissions and Versionable, Taggable, TTLable via {@link com.github.seqware.queryengine.model.impl.AtomImpl})
 *
 * @author dyuen
 * @version $Id: $Id
 */
public abstract class MoleculeImpl<T extends Molecule> extends AtomImpl<T> implements Molecule<T>, ACLable, TTLable {

    private ACL permissions = ACL.newBuilder().build();
    private long expiryTime = TTLable.FOREVER;

    /** {@inheritDoc} */
    @Override
    public void setPermissions(ACL permissions) {
        this.permissions = permissions;
        if (this.getManager() != null){
            this.getManager().atomStateChange(this, CreateUpdateManager.State.NEW_VERSION);  
        }
    }

    /** {@inheritDoc} */
    @Override
    public ACL getPermissions() {
        return permissions;
    }
    
    /** {@inheritDoc} */
    @Override
    public void setTTL(long time, boolean cascade) {
        this.expiryTime = time;
        // in general molecules ignore cascading
        if (this.getManager() != null){
            this.getManager().atomStateChange(this, CreateUpdateManager.State.NEW_VERSION);  
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setTTL(int hours, boolean cascade) {
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.HOUR, hours);
        this.expiryTime = calendar.getTimeInMillis();
        // in general molecules ignore cascading
        if (this.getManager() != null){
            this.getManager().atomStateChange(this, CreateUpdateManager.State.NEW_VERSION);  
        }
    }

    /** {@inheritDoc} */
    @Override
    public Date getExpiryDate() {
        if (this.expiryTime <= TTLable.FOREVER){
            return null;
        }
        return new Date(this.expiryTime);
    }

    /** {@inheritDoc} */
    @Override
    public int getTTL() {
        long currentTime = System.currentTimeMillis();
        long difference = this.expiryTime - currentTime;
        return (int)Math.round(difference/1000.0/60.0/60.0);
    }

    /** {@inheritDoc} */
    @Override
    public boolean getCascade() {
        // molecules in general do not contain anything to cascade to
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public long getExpiryTime() {
        return this.expiryTime;
    }
    
    /** {@inheritDoc} */
    @Override 
    public boolean isExpires(){
        return this.expiryTime > TTLable.FOREVER;
    }
}
