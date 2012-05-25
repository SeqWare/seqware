package com.github.seqware.model;

/**
 * This interface specifies the semantics and types of things we need to
 * consider when creating an API to support asynchronous queries and TTL.
 *
 * One idea is that FeatureSet results are guaranteed to last a minimum number
 * of hours after which we make no guarantees. Parameter can be set to less than
 * zero for permanent creation
 *
 *
 * @author dyuen
 */
public interface QueryInterface {

    /**
     * Type of location query
     */
    public enum Location {

        /**
         * filter features that overlap with this range (either start or stop
         * are inside the range)
         */
        OVERLAPS,
        /**
         * filter features that are fully outside this range (both start and
         * stop) are outside of this range
         */
        EXCLUDES,
        /**
         * filter features that are fully inside this range (both start and stop
         * are inside this range)
         */
        INCLUDES,
        /**
         * filter features that have this exact start and stop
         */
        EXACT
    }

    /**
     * filter features by their "type"
     *
     * @param set parent FeatureSet, can be null if we want to query over the
     * entire back-end
     * @param type type of feature
     * @param hours minimum time to live
     * @return featureSet with features filtered by type
     */
    public QueryFuture getFeaturesByType(FeatureSet set, String type, int hours);

    /**
     * (Do not) filter features TODO: Do we need this?
     *
     * @param set parent FeatureSet
     * @param hours minimum time to live
     * @return featureSet with features not filtered
     */
    public QueryFuture getFeatures(FeatureSet set, int hours);

    /**
     * filter features relative to a reference TODO: FeatureSets should only
     * have one reference, not sure what this does
     *
     * @param set parent FeatureSet
     * @param reference reference
     * @param hours minimum time to live
     * @return featureSet with features filtered by reference
     */
    public QueryFuture getFeaturesByReference(FeatureSet set, Reference reference, int hours);

    /**
     * filter features that overlap with a given range
     *
     * @param set parent FeatureSet
     * @param location specify type of location query
     * @param start start co-ordinate inclusive
     * @param stop end co-ordinate inclusive
     * @param hours minimum time to live
     * @return featureSet with features filtered by location/range
     */
    public QueryFuture getFeaturesByRange(FeatureSet set, Location location, long start, long stop, int hours);

    /**
     * filter features with tags. Specify the subject to get all Features that
     * are tagged with a specific subject (and any values for the other two).
     * Specify the subject and the predicate to get all Features that are tagged
     * with the subject and predicate (and any values for the object). Specify
     * the the subject, predicate, and object to get only Features tagged with
     * all three.
     *
     * @param set parent FeatureSet
     * @param hours minimum time to live
     * @param tag subject or subject and predicate or subject and predicate and
     * object
     * @return featureSet with features filtered by tag subject, tag (subject
     * and predicate), or tag (all parts)
     */
    public QueryFuture getFeaturesByTag(FeatureSet set, int hours, String... tag);
}
