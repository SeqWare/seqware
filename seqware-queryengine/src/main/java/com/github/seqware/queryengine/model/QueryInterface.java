package com.github.seqware.queryengine.model;

import com.github.seqware.queryengine.kernel.RPNStack;
import com.github.seqware.queryengine.plugins.PluginInterface;
import com.github.seqware.queryengine.util.SGID;
import com.github.seqware.queryengine.util.SeqWareIterable;
import java.util.List;

/**
 * This interface allows users to access queries that are built into the query
 * engine, as well as install/un-install plug-ins that allow the user to perform
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
 * query engine). While they share a similar structure to plug-ins which can be
 * installed, parts of the back-end will call these so it does not make sense to
 * install/un-install them.
 *
 * @author jbaran
 * @author dyuen
 * @version $Id: $Id
 */
public interface QueryInterface {

    /**
     * <p>getVersion.</p>
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
     * @param t type of class to retrieve (used as a filter)
     * @param sgid globally unique id
     * @return a {@link java.util.List} object.
     */
    public <T extends Atom> List getAtomsBySGID(Class<T> t, SGID... sgid);

    /**
     * Find an object in the back-end when a specific class is known using a
     * globally unique SGID while taking into account the timestamp
     *
     * @param sgid globally unique id
     * @param t type of class to retrieve (used as a filter)
     * @return atom if SGID exists, null if not
     */
    public <T extends Atom> T getAtomBySGID(Class<T> t, SGID sgid);

    /**
     * Find the latest object in the back-end by a globally unique SGID while
     * ignoring the timestamp
     *
     * @param sgid globally unique id
     * @return a object from the back-end, null if it does not exist
     * @deprecated It is much more efficient to retrieve an Atom when you know
     * the class
     */
    public Atom getLatestAtomBySGID(SGID sgid);

    /**
     * Find an object in the back-end when a specific class is known while
     * ignoring the timestamp
     *
     * @param sgid globally unique id, null if not
     * @param t type of class to retrieve (used as a filter)
     * @return a T object.
     */
    public <T extends Atom> T getLatestAtomBySGID(SGID sgid, Class<T> t);
    
    /**
     * Find an atom in the back-end when a specific class is known and a
     * specific row key is known
     *
     * @param rowKey rowKey for the atom
     * @param t type of atom to return
     * @return a T object.
     */
    public <T extends Atom> T getLatestAtomByRowKey(String rowKey, Class<T> t);
    

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
     * <p>getReferenceSets.</p>
     *
     * @return something iterable that iterates through all reference sets
     */
    public SeqWareIterable<ReferenceSet> getReferenceSets();

    /**
     * <p>getReferences.</p>
     *
     * @return SeqWareIterable that iterates through all references
     */
    public SeqWareIterable<Reference> getReferences();

//    // FEATURE SET METHODS
//    // feature sets can be parents of other feature sets
    /**
     * <p>getFeatureSets.</p>
     *
     * @return something iterable that iterates through all FeatureSets
     */
    public SeqWareIterable<FeatureSet> getFeatureSets();

//    // TAG/CV SET METHODS
    /**
     * <p>getTagSets.</p>
     *
     * @return something iterable that iterates through all TagSets
     */
    public SeqWareIterable<TagSet> getTagSets();

//    // TAG/CV METHODS
    /**
     * <p>getTags.</p>
     *
     * @return something iterable that that iterates through all Tags
     */
    public SeqWareIterable<Tag> getTags();
    
    //  ANALYSIS
    /**
     * <p>getPluginRuns.</p>
     *
     * @return something iterable that iterates through all PluginRuns
     */
    public SeqWareIterable<PluginRun> getPluginRuns();

//    // ANALYSIS SET
    /**
     * <p>getPlugins.</p>
     *
     * @return something iterable that iterates through all Plugins
     */
    public SeqWareIterable<Plugin> getPlugins();
//    public void addAnalysisSet(Plugin newAnalysisSet);
//    public void updateAnalysisSet(Plugin Plugin);
//    public void setAnalysisSetACL(ACL acl, Plugin analysisSet);
//
//    // ANALYSIS PLUGIN METHODS

    /**
     * <p>getPluginInterfaces.</p>
     *
     * @return something iterable through all AnalysisPlugins
     */
    public SeqWareIterable<PluginInterface> getPluginInterfaces();
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
     * Install a plug-in so that it can be listed
     *
     * @param plugin a {@link com.github.seqware.queryengine.plugins.PluginInterface} object.
     */
    public void installPlugin(PluginInterface plugin);
    
    /**
     * Call an arbitrary installed plug-in by class and initiate it
     *
     * @param hours minimum time to live
     * @param pluginClass plug-in class to use
     * @param parameters parameters to use when initializing the plug-in
     * @return null if there is an error, QueryFuture if the plug-in is successfully run
     * @param set a {@link com.github.seqware.queryengine.model.FeatureSet} object.
     * @param <ReturnValue> a ReturnValue object.
     */
    public <ReturnValue> QueryFuture<ReturnValue> getFeaturesByPlugin(int hours, Class<? extends PluginInterface> pluginClass,  FeatureSet set, Object ... parameters);

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
    public QueryFuture<FeatureSet> getFeaturesByAttributes(int hours, FeatureSet set, RPNStack constraints);

    /**
     * (Do not) filter features
     *
     * @param hours minimum time to live
     * @param set parent FeatureSet
     * @return featureSet with features not filtered
     */
    public QueryFuture<FeatureSet> getFeatures(int hours, FeatureSet set);

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
    public QueryFuture<FeatureSet> getFeaturesByRange(int hours, FeatureSet set, Location location, String structure, long start, long stop);

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
    public QueryFuture<FeatureSet> getFeaturesByTag(int hours, FeatureSet set, String subject, String predicate, String object);
    
    /**
     * Get the number of features in a feature set
     *
     * @param hours a int.
     * @param set a {@link com.github.seqware.queryengine.model.FeatureSet} object.
     * @return a {@link com.github.seqware.queryengine.model.QueryFuture} object.
     */
    public QueryFuture<Long> getFeatureSetCount(int hours, FeatureSet set);
}
