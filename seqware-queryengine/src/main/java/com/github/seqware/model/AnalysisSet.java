package com.github.seqware.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 
 */
public abstract class AnalysisSet extends Particle {

    /**
     * The set of features this instance represents.
     */
    private Set<Analysis> feature = new HashSet<Analysis>();

    /**
     * Creates an instance of an anonymous feature set.
     */
    public AnalysisSet() {
        super();
    }
}
