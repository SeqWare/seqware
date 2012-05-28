package com.github.seqware.model;

/**
 * Implements core functionality that is shared by all classes that require tags.
 *
 * Deriving Feature and FeatureSet from one base class facilitates code re-use
 * for interface implementations that are shared by those classes.
 *
 * @author jbaran
 */
public abstract class Atom<T> extends Particle<T> implements Taggable {
       
    public boolean associateTag(String subject) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean associateTag(String subject, String value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean associateTag(String subject, String value, String predicate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public boolean dissociateTag(Tag tag){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public Iterable<Tag> getTags(){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
}
