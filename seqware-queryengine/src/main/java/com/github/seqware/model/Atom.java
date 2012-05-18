package com.github.seqware.model;

import java.util.List;

/**
 * Implements core functionality that is shared by Feature and FeatureSets.
 *
 * Deriving Feature and FeatureSet from one base class facilitates code re-use
 * for interface implementations that are shared by those classes.
 *
 * @author jbaran
 */
public abstract class Atom implements Taggable {
    @Override
    public boolean associateTag(Tag tag) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean associateTag(Tag tag, String value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean associateTag(Tag tag, String value, String predicate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public boolean dissociateTag(Tag tag){
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public List<Tag> getTags(){
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
