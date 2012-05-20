package com.github.seqware.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * A feature set is a set (ex: collection, bag) of features. A feature set
 * cannot exist with a reference even if the reference is ad hoc and/or
 * user-created
 *
 * Immutable (but tags are not).
 *
 * @author jbaran
 */
public class FeatureSet extends Molecule {

    /**
     * Internally used unique identifier of this feature.
     */
    private UUID uuid;
    /**
     * The set of features this instance represents.
     */
    private Set<Feature> features = new HashSet<Feature>();
    
    /**
     * associated reference
     */
    private Reference reference;

    /**
     * Creates an instance of an anonymous feature set.
     */
    private FeatureSet() {
        // TODO This will have to be replaced with a stronger UUID generation method.
        this.uuid = UUID.randomUUID();
    }

    /**
     * Creates a FeatureSet with an associated reference
     *
     * @param reference associated reference
     */
    public FeatureSet(Reference reference) {
        this();
        this.reference = reference;
    }

    /**
     * Get the list of features associated with this feature set.
     *
     * @return Iterator of features.
     */
    public Iterator<Feature> features() {
        throw new UnsupportedOperationException("Not yet implemented.");
    }
}
