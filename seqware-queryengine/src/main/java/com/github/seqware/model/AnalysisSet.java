package com.github.seqware.model;

import java.util.HashSet;
import java.util.Set;

/**
 * An AnalysisSet object represents analysis events that are created by software 
 * suites or related tools. 
 * @author dyuen
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
