package com.github.seqware.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 
 */
public abstract class AnalysisSet {

    /**
     * Internally used unique identifier of this feature.
     */
    private UUID uuid;

    /**
     * The set of features this instance represents.
     */
    private Set<Analysis> feature = new HashSet<Analysis>();

    /**
     * Creates an instance of an anonymous feature set.
     */
    public AnalysisSet() {
        // TODO This will have to be replaced with a stronger UUID generation method.
        this.uuid = UUID.randomUUID();
    }
}
