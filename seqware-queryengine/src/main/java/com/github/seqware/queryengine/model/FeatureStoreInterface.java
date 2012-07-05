package com.github.seqware.queryengine.model;

import com.github.seqware.queryengine.util.SGID;
import com.github.seqware.queryengine.util.SeqWareIterable;
import java.util.List;

/**
 * Leaving this here just to provoke thought:
 * Update (and add) methods moved to Atom,
 * Query methods are in QueryInterface,
 * ACL Methods are in ACLable.
 * 
 * @author dyuen
 */
public interface FeatureStoreInterface {
//        // UTILITY METHODS
//    public void setup(SeqWareSettings settings) throws Exception;
//    public void close() throws Exception;
//    public SeqWareSettings getSettings();
//    public void setSettings(SeqWareSettings settings);
//    // FIXME: this is a BerkeleyDB exception type!
//    public void startTransaction() throws DatabaseException;
//    // FIXME: this is a BerkeleyDB exception type!
//    public void finishTransaction() throws DatabaseException;
//    public boolean isActiveTransaction();
//    // FIXME: this is a BerkeleyDB exception type!
//    public void abortTransaction() throws DatabaseException;

    /**
     *
     * @return information on the version of the back-end currently in use
     */
    public String getVersion();
// 
//    // USER/GROUP/ACL METHODS
//    // ACL operations TBD
    
    /**
     * Find a object in the back-end by a globally unique SGID
     * @param sgid globally unique id
     * @return a object from the back-end
     * @deprecated It is much more efficient to retrieve an Atom when you know the class
     */
    public Atom getAtomBySGID(SGID sgid);
    

    /**
     * Find a batch of objects in the back-end when a specific class is known using a globally unique SGID
     * while taking into account the timestamp
     * @param <T>
     * @param t type of class to retrieve (used as a filter)
     * @param sgid globally unique id
     * @return 
     */
    public <T extends Atom> List getAtomsBySGID(Class<T> t, SGID... sgid);
    
    /**
     * Find an object in the back-end when a specific class is known using a globally unique SGID
     * while taking into account the timestamp
     * @param <T>
     * @param sgid globally unique id
     * @param t type of class to retrieve (used as a filter)
     * @return 
     */
    public <T extends Atom> T getAtomBySGID(Class<T> t, SGID sgid);
    
    /**
     * Find the latest object in the back-end by a globally unique SGID
     * while ignoring the timestamp
     * @param sgid globally unique id
     * @return a object from the back-end
     * @deprecated It is much more efficient to retrieve an Atom when you know the class
     */
    public Atom getLatestAtomBySGID(SGID sgid);
    
    /**
     * Find an object in the back-end when a specific class is known
     * while ignoring the timestamp
     * @param <T>
     * @param sgid globally unique id
     * @param t type of class to retrieve (used as a filter)
     * @return 
     */
    public <T extends Atom> T getLatestAtomBySGID(SGID sgid, Class<T> t);

    /**
     * Gets all users in all groups
     * @return something iterable that iterates through all users in all groups
     */
    public SeqWareIterable<User> getUsers();

//    public void addUser(User newUser);
//    public void updateUser(User user);

    /**
     * Get all groups
     * @return something iterable that iterates through all groups 
     */
    public SeqWareIterable<Group> getGroups();
//    public void addGroup(Group newGroup);
//    public void updateGroup(Group group);
//    public void addUserToGroup(User user, Group group);
//    public SeqWareIterator getACLs();
//    public void addACL(ACL newACL);
//    public void updateACL(ACL acl);
//    public void setGroupACL(ACL acl, Group group);
// 
//    // REFERENCE METHODS

    /**
     * 
     * @return something iterable that iterates through all reference sets 
     */
    public SeqWareIterable<ReferenceSet> getReferenceSets();
//    public void addReferenceSet(ReferenceSet newReferenceSet);
//    public void updateReferenceSet(ReferenceSet referenceSet);
//    public void setReferenceSetACL(ACL acl, ReferenceSet referenceSet);
    
//    public SeqWareIterator getReferences(ReferenceSet referenceSet);
//    public void addReference(Reference newReference);
//    public void updateReference(Reference reference);
//    public void setReferenceACL(ACL acl, Reference reference);
// 
//    // FEATURE SET METHODS
//    // feature sets can be parents of other feature sets

    /**
     * 
     * @return something iterable that iterates through all FeatureSets 
     */
    public SeqWareIterable<FeatureSet> getFeatureSets();
//    public void addFeatureSet(FeatureSet newFeatureSet);
//    public void updateFeatureSet(FeatureSet featureSet);
//    public void setFeatureSetACL(ACL acl, Reference reference);
// 
//    // FEATURE METHODS
//    // find features by:
//    // - feature set
//    // - tag set, tags
//    // - analysis set, analysis
//    // - reference set, reference
//    public SeqWareIterator getFeatures(String contig, Integer start, Integer stop, Integer featureSetID, Integer analysisSetID, Integer analysisID, Integer referenceSetID, Integer referenceID);
//    public Feature getFeature(String featureId) throws Exception;
//    public SeqWareIterator getFeaturesByTag(String contig, Integer start, Integer stop, Integer featureSetID, Integer analysisSetID, Integer analysisID, Integer referenceSetID, Integer referenceID, Integer tagSetID, String key);
//    public String putFeature(FeatureSet featureSet, Feature feature);
//    public void updateFeature(Feature feature);
//    public void setFeatureACL(ACL acl, Feature feature);
//    // may need more query methods here
// 
//    // TAG/CV SET METHODS
    /**
     * 
     * @return something iterable that iterates through all TagSets 
     */
    public SeqWareIterable<TagSet> getTagSets();
//    public void addTagSet(TagSet newTagSet);
//    public void updateTagSet(TagSet TagSet);
//    public void setTagSetACL(ACL acl, TagSet tagSet);
// 
//    // TAG/CV METHODS
    /**
     * 
     * @return something iterable that that iterates through all Tags  
     */
    public SeqWareIterable<Tag> getTags();
//    public SeqWareIterator getTag(Integer tagID);
//    public SeqWareIterator getTags(Integer tagSetID);
//    public SeqWareIterator getTags(String key);
//    public String putTag(Tag Set, Tag tag);
//    public void updateTag(Tag tag);
//    public void setTagACL(ACL acl, Tag tag);
// 
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
    
}
