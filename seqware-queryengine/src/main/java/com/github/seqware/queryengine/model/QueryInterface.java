package com.github.seqware.queryengine.model;

import com.github.seqware.queryengine.kernel.RPNStack;

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
 * @author jbaran
 */
public interface QueryInterface {

    /**
     * Type of location query.
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
     * Filter features by a range of attributes.
     *
     * @param hours minimum time to live
     * @param set parent FeatureSet, can be null if we want to query over the entire back-end
     * @param constraints A stack of concrete values (constants), parameters and operations that are used to set query constraints.
     * @return featureSet with features filtered by type
     */
    public QueryFuture getFeaturesByAttributes(int hours, FeatureSet set, RPNStack constraints);

    /**
     * (Do not) filter features TODO: Do we need this?
     *
     * @param hours minimum time to live
     * @param set parent FeatureSet
     * @return featureSet with features not filtered
     */
    public QueryFuture getFeatures(int hours, FeatureSet set);

    /**
     * filter features relative to a reference TODO: FeatureSets should only
     * have one reference, not sure what this does
     *
     * @param hours minimum time to live
     * @param set parent FeatureSet
     * @param reference reference
     * @return featureSet with features filtered by reference
     */
    public QueryFuture getFeaturesByReference(int hours, FeatureSet set, Reference reference);

    /**
     * filter features that overlap with a given range
     *
     * @param hours minimum time to live
     * @param set parent FeatureSet
     * @param location specify type of location query
     * @param start start co-ordinate inclusive
     * @param stop end co-ordinate inclusive
     * @return featureSet with features filtered by location/range
     */
    public QueryFuture getFeaturesByRange(int hours, FeatureSet set, Location location, long start, long stop);

   
    /**
     * filter features with tags.
     * @param hours minimum time to live
     * @param set parent feature set
     * @param subject tag subject (always required)
     * @param predicate may be null to get Tags with all predicates
     * @param object may be null to get Tags with all (or no) objects 
     * @return featureSet with features filtered by tags
     */
    public QueryFuture getFeaturesByTag(int hours, FeatureSet set, String subject, String predicate, String object);
}
