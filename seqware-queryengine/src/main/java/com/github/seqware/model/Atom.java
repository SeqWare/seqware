package com.github.seqware.model;

import com.github.seqware.factory.Factory;
import com.github.seqware.factory.ModelManager;
import com.github.seqware.util.InMemoryIterable;
import com.github.seqware.util.SeqWareIterable;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements core functionality that is shared by all classes that require tags.
 *
 * Deriving Feature and FeatureSet from one base class facilitates code re-use
 * for interface implementations that are shared by those classes.
 *
 * @author jbaran
 * @author dyuen
 */
public abstract class Atom<T extends Atom> extends Particle<T> implements Taggable {
    
    private List<Tag> tags = new ArrayList<Tag>();
    
    @Override
    public boolean associateTag(Tag tag) {
        tags.add(tag);
        this.getManager().particleStateChange(this, ModelManager.State.NEW_VERSION);  
        //Factory.getBackEnd().associateTag(this, tag);
        return true;
    }
    
    @Override
    public boolean dissociateTag(Tag tag){
        tags.remove(tag);
        this.getManager().particleStateChange(this, ModelManager.State.NEW_VERSION);  
        //Factory.getBackEnd().dissociateTag(this, tag);
        return true;
    }
    
    @Override
    public SeqWareIterable<Tag> getTags(){
        return new InMemoryIterable(tags);//Factory.getBackEnd().getTags(this);
    }
    
    
}
