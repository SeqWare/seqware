package com.github.seqware.queryengine.model.impl.inMemory;

import com.github.seqware.queryengine.factory.ModelManager;
import com.github.seqware.queryengine.model.impl.MoleculeImpl;
import com.github.seqware.queryengine.model.interfaces.AbstractMolSet;
import com.github.seqware.queryengine.model.interfaces.Taggable;
import java.util.*;

/**
 * An in-memory representation of a AbstractMolSet.
 *
 * @author dyuen
 */
public abstract class AbstractInMemorySet<S extends AbstractMolSet, T> extends MoleculeImpl<S> implements AbstractMolSet<S, T>, Taggable{
    
    protected Set<T> set = new HashSet<T>();
    /**
     * Record whether deletes should cascade
     */
    private boolean cascade = false;
    
    protected AbstractInMemorySet(){
        super();
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
    public boolean getCascade() {
        return cascade;
    }

    @Override
    public Iterator<T> iterator() {
        return set.iterator();
    }

    @Override
    public S add(T tag) {
        set.add(tag);
        if (this.getManager() != null){
            this.getManager().atomStateChange(this, ModelManager.State.NEW_VERSION);
        }
        return (S) this;
    }

    @Override
    public S add(Collection<T> tags) {
        this.set.addAll(tags);
        if (this.getManager() != null){
            this.getManager().atomStateChange(this, ModelManager.State.NEW_VERSION);  
        }
        return (S) this;
    }
    
    @Override
    public S add(T ... tags) {
        this.set.addAll(Arrays.asList(tags));
        if (this.getManager() != null){
            this.getManager().atomStateChange(this, ModelManager.State.NEW_VERSION); 
        }
        return (S) this;
    }
    
    @Override
    public S remove(T tag) {
        this.set.remove(tag);
        if (this.getManager() != null){
            this.getManager().atomStateChange(this, ModelManager.State.NEW_VERSION);
        }
        return (S) this;
    }
    
    @Override
    public void rebuild() {
        Set<T> newSet = new HashSet<T>();
        for(T f : this.set){
            newSet.add(f);
        }
        this.set = newSet;
    }

    @Override
    public long getCount() {
        return set.size();
    }
}
