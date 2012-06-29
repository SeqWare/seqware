package com.github.seqware.model.impl.inMemory;

import com.github.seqware.factory.ModelManager;
import com.github.seqware.model.impl.MoleculeImpl;
import com.github.seqware.model.interfaces.AbstractMolSet;
import com.github.seqware.model.interfaces.Taggable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * An in-memory representation of a AbstractMolSet.
 *
 * @author dyuen
 */
public abstract class AbstractInMemorySet<S extends AbstractMolSet, T> extends MoleculeImpl<S> implements AbstractMolSet<S, T>, Taggable{
    
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
            this.getManager().AtomStateChange(this, ModelManager.State.NEW_VERSION);
        }
        return (S) this;
    }

    @Override
    public S add(Set<T> tags) {
        this.set.addAll(tags);
        if (this.getManager() != null){
            this.getManager().AtomStateChange(this, ModelManager.State.NEW_VERSION);  
        }
        return (S) this;
    }
    
    @Override
    public S add(T ... tags) {
        this.set.addAll(Arrays.asList(tags));
        if (this.getManager() != null){
            this.getManager().AtomStateChange(this, ModelManager.State.NEW_VERSION); 
        }
        return (S) this;
    }
    
    @Override
    public S remove(T tag) {
        this.set.remove(tag);
        if (this.getManager() != null){
            this.getManager().AtomStateChange(this, ModelManager.State.NEW_VERSION);
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
