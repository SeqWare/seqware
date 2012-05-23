package com.github.seqware.model;

import java.util.Iterator;
import java.util.UUID;

/**
 * A reference is a particular build version such as "hg19" or "hg18".
 *
 * Note: Every reference needs to be part of a ReferenceSet.
 *
 * @author dyuen
 * @author jbaran
 */
public abstract class Reference extends Molecule {

    /**
     * Create a new reference.
     *
     * Note: the created object needs to be part of a ReferenceSet.
     */
    public Reference() {
        super();
    }

    /**
     * Get the list of feature sets associated with this reference.
     *
     * @return Iterator of feature sets associated with this reference.
     */
    public abstract Iterator<FeatureSet> featureSets();
}
