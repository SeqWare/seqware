package com.github.seqware.model;

import com.github.seqware.factory.Factory;
import com.github.seqware.util.SeqWareIterable;

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
       
    @Override
    public boolean associateTag(Tag tag) {
        Factory.getBackEnd().associateTag(this, tag);
        return true;
    }
    
    @Override
    public boolean dissociateTag(Tag tag){
        Factory.getBackEnd().dissociateTag(this, tag);
        return true;
    }
    
    @Override
    public SeqWareIterable<Tag> getTags(){
        return Factory.getBackEnd().getTags(this);
    }
    
    
}
