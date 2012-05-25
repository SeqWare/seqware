package com.github.seqware.model;

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
    public Reference(){
        super();
    }
    
    /**
     * Create a new reference.
     *
     * Note: the created object needs to be part of a ReferenceSet.
     */
    public Reference(String name) {
        this();
        this.name = name;
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
}
