package com.github.seqware.model;

import com.github.seqware.util.SeqWareIterator;

/**
 * Implements core functionality that is shared by all classes that require tags.
 *
 * Deriving Feature and FeatureSet from one base class facilitates code re-use
 * for interface implementations that are shared by those classes.
 *
 * @author jbaran
 */
public abstract class Atom extends Particle implements Taggable {
       
    public boolean associateTag(Tag tag) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean associateTag(Tag tag, String value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean associateTag(Tag tag, String value, String predicate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public boolean dissociateTag(Tag tag){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public SeqWareIterator<Tag> getTags(){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
}
