package com.github.seqware.queryengine.model;

import com.github.seqware.queryengine.plugins.AnalysisPluginInterface;
import com.github.seqware.queryengine.kernel.RPNStack;
import com.github.seqware.queryengine.util.SGID;
import com.github.seqware.queryengine.util.SeqWareIterable;
import java.util.List;

/**
 * This interface allows users to access queries that are built into the query
 * engine, as well as install/uninstall plugins that allow the user to perform
 * additional annotation and/or other operations (in other words, this handles
 * the R operations from CRUD)). This interface also specifies the semantics and
 * types of things we need to consider when creating an API to support
 * asynchronous queries.
 *
 * The idea is that FeatureSet results are guaranteed to last a minimum number
 * of hours after which we make no guarantees. Parameter can be set to less than
 * zero for permanent creation.
 *
 * This interface specifies all queries that are intrinsic (i.e. built into the
 * query engine). While they share a similar structure to plugins which can be
 * installed, parts of the back-end will call these so it does not make sense to
 * install/uninstall them.
 *
 *
 * @author jbaran
 * @author dyuen
 */
public interface QueryInterface {

    /**
     *
     * @return information on the version of the back-end currently in use
     */
    public String getVersion();

//    // USER/GROUP/ACL METHODS
    /**
     * Find a object in the back-end by a globally unique SGID
     *
     * @param sgid globally unique id
     * @return a object from the back-end
     * @deprecated It is much more efficient to retrieve an Atom when you know
     * the class
     */
    public Atom getAtomBySGID(SGID sgid);

    /**
     * Find a batch of objects in the back-end when a specific class is known
     * using a globally unique SGID while taking into account the timestamp
     *
     * @param <T>
     * @param t type of class to retrieve (used as a filter)
     * @param sgid globally unique id
     * @return
     */
    public <T extends Atom> List getAtomsBySGID(Class<T> t, SGID... sgid);

    /**
     * Find an object in the back-end when a specific class is known using a
     * globally unique SGID while taking into account the timestamp
     *
     * @param <T>
     * @param sgid globally unique id
     * @param t type of class to retrieve (used as a filter)
     * @return
     */
    public <T extends Atom> T getAtomBySGID(Class<T> t, SGID sgid);

    /**
     * Find the latest object in the back-end by a globally unique SGID while
     * ignoring the timestamp
     *
     * @param sgid globally unique id
     * @return a object from the back-end
     * @deprecated It is much more efficient to retrieve an Atom when you know
     * the class
     */
    public Atom getLatestAtomBySGID(SGID sgid);

    /**
     * Find an object in the back-end when a specific class is known while
     * ignoring the timestamp
     *
     * @param <T>
     * @param sgid globally unique id
     * @param t type of class to retrieve (used as a filter)
     * @return
     */
    public <T extends Atom> T getLatestAtomBySGID(SGID sgid, Class<T> t);

    /**
     * Gets all users in all groups
     *
     * @return something iterable that iterates through all users in all groups
     */
    public SeqWareIterable<User> getUsers();

    /**
     * Get all groups
     *
     * @return something iterable that iterates through all groups
     */
    public SeqWareIterable<Group> getGroups();

//    // REFERENCE METHODS
    /**
     *
     * @return something iterable that iterates through all reference sets
     */
    public SeqWareIterable<ReferenceSet> getReferenceSets();

    /**
     *
     * @return SeqWareIterable that iterates through all references
     */
    public SeqWareIterable<Reference> getReferences();

//    // FEATURE SET METHODS
//    // feature sets can be parents of other feature sets
    /**
     *
     * @return something iterable that iterates through all FeatureSets
     */
    public SeqWareIterable<FeatureSet> getFeatureSets();

//    // TAG/CV SET METHODS
    /**
     *
     * @return something iterable that iterates through all TagSets
     */
    public SeqWareIterable<TagSpecSet> getTagSpecSets();

//    // TAG/CV METHODS
    /**
     *
     * @return something iterable that that iterates through all Tags
     */
    public SeqWareIterable<Tag> getTags();

//    // ANALYSIS SET
    /**
     *
     * @return something iterable that iterates through all AnalysisSets
     */
    public SeqWareIterable<AnalysisSet> getAnalysisSets();
//    public void addAnalysisSet(AnalysisSet newAnalysisSet);
//    public void updateAnalysisSet(AnalysisSet AnalysisSet);
//    public void setAnalysisSetACL(ACL acl, AnalysisSet analysisSet);
//
//    // ANALYSIS PLUGIN METHODS

    /**
     *
     * @return something iterable through all AnalysisPlugins
     */
    public SeqWareIterable<AnalysisPluginInterface> getAnalysisPlugins();
//    public void addAnalysisPlugin(AnalysisPlugin plugin);
//    public void updateAnalysisPlugin(AnalysisPlugin plugin);
//    public void setAnalysisPluginACL(ACL acl, AnalysisPlugin analysisPlugin);

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
     * @param set parent FeatureSet, can be null if we want to query over the
     * entire back-end
     * @param constraints A stack of concrete values (constants), parameters and
     * operations that are used to set query constraints.
     * @return featureSet with features filtered by type
     */
    public QueryFuture getFeaturesByAttributes(int hours, FeatureSet set, RPNStack constraints);

    /**
     * (Do not) filter features
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
     * @param structure chromosome, scaffold, etc
     * @param start start co-ordinate inclusive
     * @param stop end co-ordinate inclusive
     * @return featureSet with features filtered by location/range
     */
    public QueryFuture getFeaturesByRange(int hours, FeatureSet set, Location location, String structure, long start, long stop);

    /**
     * filter features with tags.
     *
     * @param hours minimum time to live
     * @param set parent feature set
     * @param subject tag subject (always required)
     * @param predicate may be null to get Tags with all predicates
     * @param object may be null to get Tags with all (or no) objects
     * @return featureSet with features filtered by tags
     */
    public QueryFuture getFeaturesByTag(int hours, FeatureSet set, String subject, String predicate, String object);
}
