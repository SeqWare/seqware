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
 * 
 */
public interface FeatureStoreInterface {
    
    // UTILITY METHODS
    public void setup(SeqWareSettings settings) throws Exception;
    public void close() throws Exception;
    public SeqWareSettings getSettings();
    public void setSettings(SeqWareSettings settings);
    // FIXME: this is a BerkeleyDB exception type!
    public void startTransaction() throws DatabaseException;
    // FIXME: this is a BerkeleyDB exception type!
    public void finishTransaction() throws DatabaseException;
    public boolean isActiveTransaction();
    // FIXME: this is a BerkeleyDB exception type!
    public void abortTransaction() throws DatabaseException;
    // VERSION INFO
    public String getVersion();

    // USER/GROUP/ACL METHODS
    // ACL operations TBD
    public SeqWareIterator getUsers();
    public void addUser(User newUser);
    public void updateUser(User user);
    public SeqWareIterator getGroups();
    public void addGroup(Group newGroup);
    public void updateGroup(Group group);
    public void addUserToGroup(User user, Group group);
    public SeqWareIterator getACLs();
    public void addACL(ACL newACL);
    public void updateACL(ACL acl);
    public void setGroupACL(ACL acl, Group group);
    
    // REFERENCE METHODS
    public SeqWareIterator getReferenceSets();
    public void addReferenceSet(ReferenceSet newReferenceSet);
    public void updateReferenceSet(ReferenceSet referenceSet);
    public void setReferenceSetACL(ACL acl, ReferenceSet referenceSet);
    public SeqWareIterator getReferences(ReferenceSet referenceSet);
    public void addReference(Reference newReference);
    public void updateReference(Reference reference);
    public void setReferenceACL(ACL acl, Reference reference);
    
    // FEATURE SET METHODS
    // feature sets can be parents of other feature sets
    public SeqWareIterator getFeatureSets();
    public void addFeatureSet(FeatureSet newFeatureSet);
    public void updateFeatureSet(FeatureSet featureSet);
    public void setFeatureSetACL(ACL acl, Reference reference);

    // FEATURE METHODS
    // find features by:
    // - feature set
    // - tag set, tags
    // - analysis set, analysis
    // - reference set, reference
    public SeqWareIterator getFeatures(String contig, Integer start, Integer stop, Integer featureSetID, Integer analysisSetID, Integer analysisID, Integer referenceSetID, Integer referenceID);
    public Feature getFeature(String featureId) throws Exception;
    public SeqWareIterator getFeaturesByTag(String contig, Integer start, Integer stop, Integer featureSetID, Integer analysisSetID, Integer analysisID, Integer referenceSetID, Integer referenceID, Integer tagSetID, String key);
    public String putFeature(FeatureSet featureSet, Feature feature);
    public void updateFeature(Feature feature);
    public void setFeatureACL(ACL acl, Feature feature);
    // may need more query methods here 
    
    // TAG/CV SET METHODS
    public SeqWareIterator getTagSets();
    public void addTagSet(TagSet newTagSet);
    public void updateTagSet(TagSet TagSet);
    public void setTagSetACL(ACL acl, TagSet tagSet);
    
    // TAG/CV METHODS
    public SeqWareIterator getTag(Integer tagID);
    public SeqWareIterator getTags(Integer tagSetID);
    public SeqWareIterator getTags(String key);
    public String putTag(Tag Set, Tag tag);
    public void updateTag(Tag tag);
    public void setTagACL(ACL acl, Tag tag);
    
    // ANALYSIS SET
    public SeqWareIterator getAnalysisSets();
    public void addAnalysisSet(AnalysisSet newAnalysisSet);
    public void updateAnalysisSet(AnalysisSet AnalysisSet);
    public void setAnalysisSetACL(ACL acl, AnalysisSet analysisSet);
   
    // ANALYSIS PLUGIN METHODS
    public SeqWareIterator getAnalysisPlugins();
    public void addAnalysisPlugin(AnalysisPlugin plugin);
    public void updateAnalysisPlugin(AnalysisPlugin plugin);
    public void setAnalysisPluginACL(ACL acl, AnalysisPlugin analysisPlugin);
}
