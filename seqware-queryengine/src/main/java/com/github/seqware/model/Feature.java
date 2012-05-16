package com.github.seqware.model;

public class Feature {

    /**
     * Strand locations of features.
     *
     * Positive/negative strand are relative to the landmark.
     */
    public enum Strand { POSITIVE, NEGATIVE, NOT_STRANDED, UNKNOWN }

    /**
     * Internally used unique identifier of this feature.
     */
    private long uid;

    /**
     * User provided ID for the feature (optional).
     */
    private String id;

    /**
     * Reference system used for coordinates, for example, versioned name of genome assembly used.
     */
    String reference;

    /**
     * Start coordinate.
     */
	private long start;

    /**
     * Stop coordinate.
     */
	private long stop;

    /**
     * Strand of the feature.
     */
    private Strand strand;
}

