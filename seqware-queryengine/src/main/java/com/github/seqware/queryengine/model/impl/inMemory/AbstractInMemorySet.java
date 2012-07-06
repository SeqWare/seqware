package com.github.seqware.queryengine.model.impl.inMemory;

import com.github.seqware.queryengine.model.impl.AbstractMolSet;
import com.github.seqware.queryengine.factory.ModelManager;
import com.github.seqware.queryengine.model.impl.MoleculeImpl;
import com.github.seqware.queryengine.model.interfaces.MolSetInterface;
import com.github.seqware.queryengine.model.interfaces.Taggable;
import java.util.*;

/**
 * An in-memory representation of a MolSetInterface.
 *
 * @author dyuen
 */
public abstract class AbstractInMemorySet<S extends MolSetInterface, T> extends AbstractMolSet<S> implements MolSetInterface<S, T>, Taggable{
    
    protected Set<T> set = new HashSet<T>();
    
    protected AbstractInMemorySet(){
        super();
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
