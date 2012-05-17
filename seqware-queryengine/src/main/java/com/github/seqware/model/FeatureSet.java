package com.github.seqware.model;

import java.util.UUID;

/**
 * A feature set is a set (a.k.a. collection, bag) of features.
 */
public abstract class FeatureSet implements Taggable, Versionable, ACLable {

    /**
     * Internally used unique identifier of this feature.
     */
    private UUID uuid;

    /**
     * The set of features this instance represents.
     */
    private Set<Feature> features = new HashSet<Feature>();

    /**
     * Creates an instance of an anonymous feature set.
     */
    public FeatureSet() {
        // TODO This will have to be replaced with a stronger UUID generation method.
        this.uuid = UUID.randomUUID();
    }
}
