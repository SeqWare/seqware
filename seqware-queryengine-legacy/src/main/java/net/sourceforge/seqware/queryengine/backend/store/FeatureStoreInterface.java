/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.store;

import com.sleepycat.db.DatabaseException;
import java.util.ArrayList;

import net.sourceforge.seqware.queryengine.backend.model.Consequence;
import net.sourceforge.seqware.queryengine.backend.model.Coverage;
import net.sourceforge.seqware.queryengine.backend.model.Feature;
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;
import net.sourceforge.seqware.queryengine.backend.util.iterators.CursorIterator;
import net.sourceforge.seqware.queryengine.backend.util.iterators.LocatableSecondaryCursorIterator;
import net.sourceforge.seqware.queryengine.backend.util.iterators.SecondaryCursorIterator;

/**
 * <p>FeatureStoreInterface interface.</p>
 *
 * @author boconnor
 *
 * TODO:
 * - throws general FeatureStore exception with a couple different subtypes including an unimplemented exception
 * - need to make the exception and iterators more generic named (e.g. get rid of SeqWare in the name)
 * - use the annotation approach so backends on the classpath can be discovered
 * - iterator needs to support generics so they can be typed
 *
 * Everything has a UUID which is a universally unique identifier (not just for
 * this particular backend instance).
 *
 * There are four set entities within the database backend:
 * 1) Feature Sets
 * 2) Tag Sets
 * 3) Analysis Sets
 * 4) Reference Sets
 *
 * These contain one or more of the core entities in the backend:
 * 1) Features
 * 2) Tags
 * 3) Analysis
 * 4) Reference
 *
 * In addition there are Group and User entities along with ACL (permission) for each of the
 * core entities and sets above.
 *
 * Almost every entity in the database can have one or more of the following
 * associated with it:
 * 1) ACL (permission)
 * 2) tag
 * 3) analysis (version)
 *
 * Tags can have the following associated with them:
 * 1) associated analysis (version)
 * 2) key (required)
 * 3) value (with a value type, including binary values)
 * 4) associated tag set
 *
 * Features are modeled on genomic entities that have a location and should be able
 * to represent a VCF 4.1 (http://www.1000genomes.org/wiki/Analysis/Variant%20Call%20Format/vcf-variant-call-format-version-41)
 * and GVF 1.06 (http://www.sequenceontology.org/resources/gvf.html) variants. These can have the following
 * associated with them:
 * 1) associated analysis (version)
 * 2) tags
 * 3) associated feature set
 *
 * Copy on write vs. updates to features:
 * When an analysis even processes a given feature set(s)/features and produces new tag set/tag
 * associations (or perhaps even new tags) the backend can do one of two things.
 * First, it can save these new tags to the original features and associate them with the
 * analysis entity that created them. This assumes the person triggering the analysis
 * and storage has permission to read/write to these feature sets/features. If the
 * user only has read permission they should be able to create a copy of the feature set/features,
 * link this new feature set to the old one
 *
 * Ideas for plugins:
 * We want to use the plugin interface for as much non-core functionality as possible
 * (but only for things that are backend-type agnostic). These might include:
 * 1) backup/dump process
 * 2) consistency checker
 * 3) general stats collector
 * 4) feature consequence predictor and annotator (aka Annovar)
 * 5) somatic caller
 * 6) maybe some generic
 *
 * Plugins and Security:
 * If we go the route of, say, allowing the GATK walkers to be used with this DB
 * (which would be hugely awesome) it presents security issues since they are
 * not aware of a security model at all. One solution is to implement a filtering
 * API between the raw DB and what the plugins see so a user can't write a plugin
 * that, say, purges the DB of all features. They would only be able to purge
 * features from feature sets that they have read/write access to. Their code
 * would never actually see those feature sets/features they don't have access to.
 *
 * Plugins and GATK:
 * It remains to be seen how easy it will be to graft a GATK loci walker-compatible
 * plugin architecture.  Like the security comment above, a pre-filtering approach
 * will need to be implemented since GATK really only knows about chr position
 * to filter positions examined on. Also, we will only be able to implement
 * a subset of their tools, ones that operate on VCF files I suspect.
 * @version $Id: $Id
 */
public interface FeatureStoreInterface {
    
    // UTILITY METHODS
    /**
     * <p>setup.</p>
     *
     * @param settings a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings} object.
     * @throws java.lang.Exception if any.
     */
    public void setup(SeqWareSettings settings) throws Exception;
    /**
     * <p>close.</p>
     *
     * @throws java.lang.Exception if any.
     */
    public void close() throws Exception;
    /**
     * <p>getSettings.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings} object.
     */
    public SeqWareSettings getSettings();
    /**
     * <p>setSettings.</p>
     *
     * @param settings a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings} object.
     */
    public void setSettings(SeqWareSettings settings);
    // FIXME: this is a BerkeleyDB exception type!
    /**
     * <p>startTransaction.</p>
     *
     * @throws com.sleepycat.db.DatabaseException if any.
     */
    public void startTransaction() throws DatabaseException;
    // FIXME: this is a BerkeleyDB exception type!
    /**
     * <p>finishTransaction.</p>
     *
     * @throws com.sleepycat.db.DatabaseException if any.
     */
    public void finishTransaction() throws DatabaseException;
    /**
     * <p>isActiveTransaction.</p>
     *
     * @return a boolean.
     */
    public boolean isActiveTransaction();
    // FIXME: this is a BerkeleyDB exception type!
    /**
     * <p>abortTransaction.</p>
     *
     * @throws com.sleepycat.db.DatabaseException if any.
     */
    public void abortTransaction() throws DatabaseException;
    // VERSION INFO
    /**
     * <p>getVersion.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getVersion();

    // USER/GROUP/ACL METHODS
    // ACL operations TBD
    /**
     * <p>getUsers.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getUsers();
    /**
     * <p>addUser.</p>
     *
     * @param newUser a {@link net.sourceforge.seqware.queryengine.backend.store.User} object.
     */
    public void addUser(User newUser);
    /**
     * <p>updateUser.</p>
     *
     * @param user a {@link net.sourceforge.seqware.queryengine.backend.store.User} object.
     */
    public void updateUser(User user);
    /**
     * <p>getGroups.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getGroups();
    /**
     * <p>addGroup.</p>
     *
     * @param newGroup a {@link net.sourceforge.seqware.queryengine.backend.store.Group} object.
     */
    public void addGroup(Group newGroup);
    /**
     * <p>updateGroup.</p>
     *
     * @param group a {@link net.sourceforge.seqware.queryengine.backend.store.Group} object.
     */
    public void updateGroup(Group group);
    /**
     * <p>addUserToGroup.</p>
     *
     * @param user a {@link net.sourceforge.seqware.queryengine.backend.store.User} object.
     * @param group a {@link net.sourceforge.seqware.queryengine.backend.store.Group} object.
     */
    public void addUserToGroup(User user, Group group);
    /**
     * <p>getACLs.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getACLs();
    /**
     * <p>addACL.</p>
     *
     * @param newACL a {@link net.sourceforge.seqware.queryengine.backend.store.ACL} object.
     */
    public void addACL(ACL newACL);
    /**
     * <p>updateACL.</p>
     *
     * @param acl a {@link net.sourceforge.seqware.queryengine.backend.store.ACL} object.
     */
    public void updateACL(ACL acl);
    /**
     * <p>setGroupACL.</p>
     *
     * @param acl a {@link net.sourceforge.seqware.queryengine.backend.store.ACL} object.
     * @param group a {@link net.sourceforge.seqware.queryengine.backend.store.Group} object.
     */
    public void setGroupACL(ACL acl, Group group);
    
    // REFERENCE METHODS
    /**
     * <p>getReferenceSets.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getReferenceSets();
    /**
     * <p>addReferenceSet.</p>
     *
     * @param newReferenceSet a {@link net.sourceforge.seqware.queryengine.backend.store.ReferenceSet} object.
     */
    public void addReferenceSet(ReferenceSet newReferenceSet);
    /**
     * <p>updateReferenceSet.</p>
     *
     * @param referenceSet a {@link net.sourceforge.seqware.queryengine.backend.store.ReferenceSet} object.
     */
    public void updateReferenceSet(ReferenceSet referenceSet);
    /**
     * <p>setReferenceSetACL.</p>
     *
     * @param acl a {@link net.sourceforge.seqware.queryengine.backend.store.ACL} object.
     * @param referenceSet a {@link net.sourceforge.seqware.queryengine.backend.store.ReferenceSet} object.
     */
    public void setReferenceSetACL(ACL acl, ReferenceSet referenceSet);
    /**
     * <p>getReferences.</p>
     *
     * @param referenceSet a {@link net.sourceforge.seqware.queryengine.backend.store.ReferenceSet} object.
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getReferences(ReferenceSet referenceSet);
    /**
     * <p>addReference.</p>
     *
     * @param newReference a {@link net.sourceforge.seqware.queryengine.backend.store.Reference} object.
     */
    public void addReference(Reference newReference);
    /**
     * <p>updateReference.</p>
     *
     * @param reference a {@link net.sourceforge.seqware.queryengine.backend.store.Reference} object.
     */
    public void updateReference(Reference reference);
    /**
     * <p>setReferenceACL.</p>
     *
     * @param acl a {@link net.sourceforge.seqware.queryengine.backend.store.ACL} object.
     * @param reference a {@link net.sourceforge.seqware.queryengine.backend.store.Reference} object.
     */
    public void setReferenceACL(ACL acl, Reference reference);
    
    // FEATURE SET METHODS
    // feature sets can be parents of other feature sets
    /**
     * <p>getFeatureSets.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getFeatureSets();
    /**
     * <p>addFeatureSet.</p>
     *
     * @param newFeatureSet a {@link net.sourceforge.seqware.queryengine.backend.store.FeatureSet} object.
     */
    public void addFeatureSet(FeatureSet newFeatureSet);
    /**
     * <p>updateFeatureSet.</p>
     *
     * @param featureSet a {@link net.sourceforge.seqware.queryengine.backend.store.FeatureSet} object.
     */
    public void updateFeatureSet(FeatureSet featureSet);
    /**
     * <p>setFeatureSetACL.</p>
     *
     * @param acl a {@link net.sourceforge.seqware.queryengine.backend.store.ACL} object.
     * @param reference a {@link net.sourceforge.seqware.queryengine.backend.store.Reference} object.
     */
    public void setFeatureSetACL(ACL acl, Reference reference);

    // FEATURE METHODS
    // find features by:
    // - feature set
    // - tag set, tags
    // - analysis set, analysis
    // - reference set, reference
    /**
     * <p>getFeatures.</p>
     *
     * @param contig a {@link java.lang.String} object.
     * @param start a {@link java.lang.Integer} object.
     * @param stop a {@link java.lang.Integer} object.
     * @param featureSetID a {@link java.lang.Integer} object.
     * @param analysisSetID a {@link java.lang.Integer} object.
     * @param analysisID a {@link java.lang.Integer} object.
     * @param referenceSetID a {@link java.lang.Integer} object.
     * @param referenceID a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getFeatures(String contig, Integer start, Integer stop, Integer featureSetID, Integer analysisSetID, Integer analysisID, Integer referenceSetID, Integer referenceID);
    /**
     * <p>getFeature.</p>
     *
     * @param featureId a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.queryengine.backend.model.Feature} object.
     * @throws java.lang.Exception if any.
     */
    public Feature getFeature(String featureId) throws Exception;
    /**
     * <p>getFeaturesByTag.</p>
     *
     * @param contig a {@link java.lang.String} object.
     * @param start a {@link java.lang.Integer} object.
     * @param stop a {@link java.lang.Integer} object.
     * @param featureSetID a {@link java.lang.Integer} object.
     * @param analysisSetID a {@link java.lang.Integer} object.
     * @param analysisID a {@link java.lang.Integer} object.
     * @param referenceSetID a {@link java.lang.Integer} object.
     * @param referenceID a {@link java.lang.Integer} object.
     * @param tagSetID a {@link java.lang.Integer} object.
     * @param key a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getFeaturesByTag(String contig, Integer start, Integer stop, Integer featureSetID, Integer analysisSetID, Integer analysisID, Integer referenceSetID, Integer referenceID, Integer tagSetID, String key);
    /**
     * <p>putFeature.</p>
     *
     * @param featureSet a {@link net.sourceforge.seqware.queryengine.backend.store.FeatureSet} object.
     * @param feature a {@link net.sourceforge.seqware.queryengine.backend.model.Feature} object.
     * @return a {@link java.lang.String} object.
     */
    public String putFeature(FeatureSet featureSet, Feature feature);
    /**
     * <p>updateFeature.</p>
     *
     * @param feature a {@link net.sourceforge.seqware.queryengine.backend.model.Feature} object.
     */
    public void updateFeature(Feature feature);
    /**
     * <p>setFeatureACL.</p>
     *
     * @param acl a {@link net.sourceforge.seqware.queryengine.backend.store.ACL} object.
     * @param feature a {@link net.sourceforge.seqware.queryengine.backend.model.Feature} object.
     */
    public void setFeatureACL(ACL acl, Feature feature);
    // may need more query methods here 
    
    // TAG/CV SET METHODS
    /**
     * <p>getTagSets.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getTagSets();
    /**
     * <p>addTagSet.</p>
     *
     * @param newTagSet a {@link net.sourceforge.seqware.queryengine.backend.store.TagSet} object.
     */
    public void addTagSet(TagSet newTagSet);
    /**
     * <p>updateTagSet.</p>
     *
     * @param TagSet a {@link net.sourceforge.seqware.queryengine.backend.store.TagSet} object.
     */
    public void updateTagSet(TagSet TagSet);
    /**
     * <p>setTagSetACL.</p>
     *
     * @param acl a {@link net.sourceforge.seqware.queryengine.backend.store.ACL} object.
     * @param tagSet a {@link net.sourceforge.seqware.queryengine.backend.store.TagSet} object.
     */
    public void setTagSetACL(ACL acl, TagSet tagSet);
    
    // TAG/CV METHODS
    /**
     * <p>getTag.</p>
     *
     * @param tagID a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getTag(Integer tagID);
    /**
     * <p>getTags.</p>
     *
     * @param tagSetID a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getTags(Integer tagSetID);
    /**
     * <p>getTags.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getTags(String key);
    /**
     * <p>putTag.</p>
     *
     * @param Set a {@link net.sourceforge.seqware.queryengine.backend.store.Tag} object.
     * @param tag a {@link net.sourceforge.seqware.queryengine.backend.store.Tag} object.
     * @return a {@link java.lang.String} object.
     */
    public String putTag(Tag Set, Tag tag);
    /**
     * <p>updateTag.</p>
     *
     * @param tag a {@link net.sourceforge.seqware.queryengine.backend.store.Tag} object.
     */
    public void updateTag(Tag tag);
    /**
     * <p>setTagACL.</p>
     *
     * @param acl a {@link net.sourceforge.seqware.queryengine.backend.store.ACL} object.
     * @param tag a {@link net.sourceforge.seqware.queryengine.backend.store.Tag} object.
     */
    public void setTagACL(ACL acl, Tag tag);
    
    // ANALYSIS SET
    /**
     * <p>getAnalysisSets.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getAnalysisSets();
    /**
     * <p>addAnalysisSet.</p>
     *
     * @param newAnalysisSet a {@link net.sourceforge.seqware.queryengine.backend.store.AnalysisSet} object.
     */
    public void addAnalysisSet(AnalysisSet newAnalysisSet);
    /**
     * <p>updateAnalysisSet.</p>
     *
     * @param AnalysisSet a {@link net.sourceforge.seqware.queryengine.backend.store.AnalysisSet} object.
     */
    public void updateAnalysisSet(AnalysisSet AnalysisSet);
    /**
     * <p>setAnalysisSetACL.</p>
     *
     * @param acl a {@link net.sourceforge.seqware.queryengine.backend.store.ACL} object.
     * @param analysisSet a {@link net.sourceforge.seqware.queryengine.backend.store.AnalysisSet} object.
     */
    public void setAnalysisSetACL(ACL acl, AnalysisSet analysisSet);
   
    // ANALYSIS PLUGIN METHODS
    /**
     * <p>getAnalysisPlugins.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator} object.
     */
    public SeqWareIterator getAnalysisPlugins();
    /**
     * <p>addAnalysisPlugin.</p>
     *
     * @param plugin a {@link net.sourceforge.seqware.queryengine.backend.store.AnalysisPlugin} object.
     */
    public void addAnalysisPlugin(AnalysisPlugin plugin);
    /**
     * <p>updateAnalysisPlugin.</p>
     *
     * @param plugin a {@link net.sourceforge.seqware.queryengine.backend.store.AnalysisPlugin} object.
     */
    public void updateAnalysisPlugin(AnalysisPlugin plugin);
    /**
     * <p>setAnalysisPluginACL.</p>
     *
     * @param acl a {@link net.sourceforge.seqware.queryengine.backend.store.ACL} object.
     * @param analysisPlugin a {@link net.sourceforge.seqware.queryengine.backend.store.AnalysisPlugin} object.
     */
    public void setAnalysisPluginACL(ACL acl, AnalysisPlugin analysisPlugin);
}
