package com.github.seqware.model;

import com.github.seqware.impl.SimpleModelManager;
import java.util.Iterator;

/**
 * A reference is a particular build version such as "hg19" or "hg18".
 *
 * Note: Every reference needs to be part of a ReferenceSet.
 *
 * @author dyuen
 * @author jbaran
 */
public abstract class Reference extends Molecule {

    private String name = "Reference name place-holder";
    
    /**
     * Create a anonymous new reference
     */
    protected Reference(){
        super();
    }

    /**
     * Get the list of feature sets associated with this reference.
     *
     * @return Iterator of feature sets associated with this reference.
     */
    public abstract Iterator<FeatureSet> featureSets();
    
    /**
     * The name of this reference (ex: "hg 19")
     * @return return the name of this reference.
     */
    public String getName(){
        return name;
    }
    
    
    /**
     * Create an ACL builder started with a copy of this
     * @return 
     */
    public abstract Reference.Builder toBuilder();

    public abstract static class Builder {

        public Reference reference;

        public Reference.Builder setName(String name) {
            reference.name = name;
            return this;
        }
        
        public Reference build() {
           return build(true);
        }

        public abstract Reference build(boolean newObject);

        public Builder setManager(SimpleModelManager aThis) {
            reference.setManager(aThis);
            return this;
        }
    }
}
