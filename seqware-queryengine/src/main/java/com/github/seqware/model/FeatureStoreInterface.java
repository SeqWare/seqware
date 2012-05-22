package com.github.seqware.model;

import com.github.seqware.util.SeqWareIterator;
import java.util.Iterator;
import java.util.UUID;

/**
 * Leaving this here just to provoke thought
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
     * Find a object in the back-end by a  globally unique UUID
     * @param uuid
     * @return a object from the back-end
     */
    public Particle getParticleByUUID(UUID uuid);

    /**
     * Gets all users in all groups
     * @return an iterator that iterates through all users in all groups
     */
    public Iterator<User> getUsers();

//    public void addUser(User newUser);
//    public void updateUser(User user);

    /**
     * Get all groups
     * @return an iterator that iterates through all groups 
     */
    public Iterator<Group> getGroups();
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
     * @return an iterator that iterates through all reference sets 
     */
    public Iterator<ReferenceSet> getReferenceSets();
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
     * @return an iterator that iterates through all FeatureSets 
     */
    public Iterator<FeatureSet> getFeatureSets();
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
     * @return an iterator that iterates through all TagSets 
     */
    public Iterator<TagSet> getTagSets();
//    public void addTagSet(TagSet newTagSet);
//    public void updateTagSet(TagSet TagSet);
//    public void setTagSetACL(ACL acl, TagSet tagSet);
// 
//    // TAG/CV METHODS
    /**
     * 
     * @return an iterator that that iterates through all Tags  
     */
    public Iterator<Tag> getTags();
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
     * @return an iterator that iterates through all AnalysisSets 
     */
    public Iterator<AnalysisSet> getAnalysisSets();
//    public void addAnalysisSet(AnalysisSet newAnalysisSet);
//    public void updateAnalysisSet(AnalysisSet AnalysisSet);
//    public void setAnalysisSetACL(ACL acl, AnalysisSet analysisSet);
//
//    // ANALYSIS PLUGIN METHODS
    /**
     * 
     * @return an Iterator that iterates through all AnalysisPlugins 
     */
    public Iterator<AnalysisPluginInterface> getAnalysisPlugins();
//    public void addAnalysisPlugin(AnalysisPlugin plugin);
//    public void updateAnalysisPlugin(AnalysisPlugin plugin);
//    public void setAnalysisPluginACL(ACL acl, AnalysisPlugin analysisPlugin);
    
}
