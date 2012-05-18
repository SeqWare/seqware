package com.github.seqware.model;

import java.util.List;
import java.util.UUID;

/**
 * A reference is a particular build version such as "hg 19" or "hg 18"
 *
 * @author dyuen
 */
public abstract class Reference extends Molecule {

    /**
     * Internally used unique identifier of this feature.
     */
    private UUID uuid;
    /**
     * parent set
     */
    private ReferenceSet set;

    /**
     * Create a new reference
     */
    private Reference() {
        // TODO This will have to be replaced with a stronger UUID generation method.
        this.uuid = UUID.randomUUID();
    }

    /**
     * References are always created in association with an existing reference
     * set
     *
     * @param set the parent set for this reference
     */
    public Reference(ReferenceSet set) {
        this();
        this.set = set;
    }

    /**
     * Get the universally unique identifier of this feature.
     */
    public UUID getUUID() {
        return this.uuid;
    }

    /**
     * Get the list of feature sets associated with this reference 
     * TODO: Wouldn't this be large? Maybe there should be a better way of 
     * accessing this information?
     *
     * @return list of feature sets associated with this reference
     */
    public abstract List<FeatureSet> featureSets();
}
