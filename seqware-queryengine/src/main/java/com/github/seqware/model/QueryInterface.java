package com.github.seqware.model;

/**
 * Sketches out the semantics and types of things we need to consider when
 * creating an API to support asynchronous queries and TTL
 * 
 * One idea is that FeatureSet results are guaranteed to last a minimum number
 * of hours after which we make no guarantees. Parameter can be set to less than
 * or equal to zero for permanent creation
 * 
 * @author dyuen
 */
public interface QueryInterface {
    
    /**
     * filter features by their "type"
     * @param set parent FeatureSet
     * @param type type of feature
     * @param hours minimum time to live
     * @return 
     */
    public QueryFuture getFeaturesByType(FeatureSet set, String type, int hours);
    
    /**
     * (Do not) filter features 
     * TODO: Do we need this?
     * @param set parent FeatureSet
     * @param hours minimum time to live
     * @return 
     */
    public QueryFuture getFeatures(FeatureSet set, int hours);
    
    /**
     * filter features relative to a reference
     * TODO: FeatureSets should only have one reference, not sure what this does
     * @param set parent FeatureSet
     * @param reference reference
     * @param hours minimum time to live
     * @return 
     */
    public QueryFuture getFeaturesByReference(FeatureSet set, Reference reference, int hours);
    

    /**
     * filter features that overlap with a given range
     * @param set parent FeatureSet
     * @param start start co-ordinate inclusive
     * @param stop end co-ordinate exclusive
     * @param hours minimum time to live
     * @return 
     */
    public QueryFuture getFeaturesByRange(FeatureSet set, long start, long stop , int hours);
    
    /**
     * insert more queries here when we nail down the semantics of how to 
     * interact with queries
     * 
     * we have:
     * 
     * do not overlap
     * fully inside
     * exact position
     * features with a particular key (does this mean tag?)
     * features with a particular value (does this mean tag value?)
     * features with a key-value pair (does this mean tag and value (what about predicate?)) 
     */
    
    
    /**
     * Sketches out the result that might be returned from a asynchronous 
     * query. 
     */
    public interface QueryFuture{
        
        /**
         * Blocking call to retrieve results of a query
         * @return FeatureSet with desired results, null in case of failiure?
         */
        public FeatureSet get();
        
        /**
         * Returns true iff the query is ready with its results
         */
        public boolean isDone();
    }
}
