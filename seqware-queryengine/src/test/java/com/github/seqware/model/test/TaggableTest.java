package com.github.seqware.model.test;

import com.github.seqware.queryengine.plugins.AnalysisPluginInterface;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.model.*;
import com.github.seqware.queryengine.model.impl.inMemory.InMemoryFeaturesAllPlugin;
import com.github.seqware.queryengine.model.interfaces.Taggable;
import com.github.seqware.queryengine.util.SeqWareIterable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests of {@link Taggable}.
 *
 * @author dyuen
 */
public class TaggableTest {

    private static FeatureSet fSet;
    private static Feature f1, f2, f3;
    private static TagSpecSet tSet1, tSet2;
    private static ReferenceSet rSet;
    private static Reference r1;
    private static Group group;
    private static User u1;
    private static AnalysisSet aSet;
    private static Analysis a;
    private static Tag ts1, ts2, ts3;
    private static Tag t1a, t1b, t1c, t2a, t2b, t2c, t3a;

    @BeforeClass
    public static void setupTests() {
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        // test tagging every possible class that can be tagged
        // create a few instances of everything that can be tagged
        fSet = mManager.buildFeatureSet().setReference(mManager.buildReference().setName("testing_Dummy_reference").build()).build();
        Set<Feature> testFeatures = new HashSet<Feature>();
        f1 = mManager.buildFeature().setId("chr16").setStart(1000000).setStop(1000100).build();
        f2 = mManager.buildFeature().setId("chr16").setStart(1000200).setStop(1000300).build();
        f3 = mManager.buildFeature().setId("chr16").setStart(1000400).setStop(1000500).build();
        testFeatures.add(f1);
        testFeatures.add(f2);
        testFeatures.add(f3);
        fSet.add(testFeatures);
        tSet1 = mManager.buildTagSpecSet().setName("Funky tags").build();
        tSet2 = mManager.buildTagSpecSet().setName("Unfunky tags").build();
        rSet = mManager.buildReferenceSet().setName("Minbar").setOrganism("Minbari").build();
        aSet = mManager.buildAnalysisSet().setName("FP").setDescription("Funky program").build();
        // only for testing, Analysis classes 
        a = mManager.buildAnalysis().setParameters(new ArrayList<Object>()).setPlugin(new InMemoryFeaturesAllPlugin()).build();
        r1 = mManager.buildReference().setName("ref1").build();
        rSet.add(r1);
        group = mManager.buildGroup().setName("Developers").setDescription("Users that are working on new stuff").build();
        u1 = mManager.buildUser().setFirstName("Joe").setLastName("Smith").setEmailAddress("joe.smith@googly.com").setPassword("password").build();
        group.add(u1);

        // create tag specifications
        ts1 = mManager.buildTagSpec().setKey("KR").build();
        ts2 = mManager.buildTagSpec().setKey("AS").build();
        ts3 = mManager.buildTagSpec().setKey("JC").build();
        tSet1.add(ts1, ts2, ts3);

        // tag stuff
        t1a = ts1.toBuilder().build();
        t1b = ts1.toBuilder().setPredicate("=").build();
        t1c = ts1.toBuilder().setPredicate("=").setValue("F").build();
        t2a = ts2.toBuilder().build();
        t2b = ts2.toBuilder().setPredicate("=").build();
        t2c = ts2.toBuilder().setPredicate("=").setValue("T800").build();
        t3a = ts3.toBuilder().build();
        // 7 new tags added

        // 12 calls to associate 
        fSet.associateTag(t2a);
        fSet.associateTag(t2b);
        fSet.associateTag(t2c);
        a.associateTag(t3a);
        f1.associateTag(t1a);
        f2.associateTag(t1b);
        f3.associateTag(t1c);
        tSet1.associateTag(t3a);
        aSet.associateTag(t2a);
        rSet.associateTag(t2a);
        r1.associateTag(t2a);
        group.associateTag(t2b);
        u1.associateTag(t2b);
        mManager.close();
    }

    @Test
    public void testTaggingOnEverything() {
// Some of these global tests are no longer working because the back-end persists between test classes, we need a search API
        SeqWareIterable<TagSpecSet> tagSets = SWQEFactory.getQueryInterface().getTagSpecSets();
        // we have two tag sets
        boolean t1found = false;
        boolean t2found = false;
        for (TagSpecSet t : tagSets) {
            if (t.equals(tSet1)) {
                t1found = true;
            } else if (t.equals(tSet2)) {
                t2found = true;
            }
        }
        Assert.assertTrue(t1found == true && t2found == true);
//        SeqWareIterable<Tag> tags = SWQEFactory.getQueryInterface().getTags();
//        Tag[] tagsCheck = {t1a, t1b, t1c, t2a, t2b, t2c, t3a};
//        boolean[] tagsCheckFound = new boolean[tagsCheck.length];
//        Arrays.fill(tagsCheckFound, false);
////        for(TagSpecSet t : tagSets){
//        for (Tag ta : tags) {
//            for (int i = 0; i < tagsCheckFound.length; i++) {
//                if (tagsCheck[i].equals(ta)) {
//                    tagsCheckFound[i] = true;
//                }
//            }
//        }
////        }
//        for (boolean b : tagsCheckFound) {
//            Assert.assertTrue(b);
//        }
        SeqWareIterable<Tag> tags1 = fSet.getTags();
        // 3 tags were associated with the featureSet
        // behaviour here has changed, but looking at the old prototype, I don't think we meant to allow tagging something with the same
        // key multiple times, albeit with different predicates or values
        Assert.assertTrue(tags1.getCount() == 1);
        Assert.assertTrue(a.getTags().getCount() == 1);
    }

    @Test
    public void testAddingAndRemovingTagSpecsFromTagSpecSets() {
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        // tags are not initially in a key set
        TagSpecSet initialTestSet = (TagSpecSet) SWQEFactory.getQueryInterface().getAtomBySGID(TagSpecSet.class, tSet2.getSGID());
        Tag[] tagsCheck = {ts1, ts2, ts3};
        boolean[] tagsCheckFound = new boolean[tagsCheck.length];
        Arrays.fill(tagsCheckFound, false);
        // there should be nothing here
        for (Tag ta : initialTestSet) {
            for (int i = 0; i < tagsCheckFound.length; i++) {
                if (tagsCheck[i].equals(ta)) {
                    tagsCheckFound[i] = true;
                }
            }
        }
        for (boolean b : tagsCheckFound) {
            Assert.assertTrue(!b);
        }
        // we need to persist these sets so that the new model manager is aware of them
        mManager.persist(tSet1);
        mManager.persist(tSet2);
        tSet2.add(ts1).add(ts2);
        mManager.flush();
        TagSpecSet testSet = (TagSpecSet) SWQEFactory.getQueryInterface().getAtomBySGID(TagSpecSet.class, tSet2.getSGID());
        Assert.assertTrue(testSet.getCount() == 2);
        tSet2.add(ts3);
        mManager.flush();
        testSet = (TagSpecSet) SWQEFactory.getQueryInterface().getAtomBySGID(TagSpecSet.class, tSet2.getSGID());
        Assert.assertTrue(testSet.getCount() == 3);
        Assert.assertTrue(testSet.getPrecedingVersion().getCount() == 2);
        // and then remove them
        tSet2.remove(ts3).remove(ts2);
        mManager.flush();
        testSet = (TagSpecSet) SWQEFactory.getQueryInterface().getAtomBySGID(TagSpecSet.class, tSet2.getSGID());
        Assert.assertTrue(testSet.getCount() == 1);
        Assert.assertTrue(testSet.getPrecedingVersion().getCount() == 3);
    }

    @Test
    public void testClassesThatCannotBeTagged() {
        // practically everything can be tagged, except for plugins and tags
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        Tag tag1a = ts1.toBuilder().build();
        boolean tagException = false;
        try {
            tag1a.associateTag(tag1a);
        } catch (UnsupportedOperationException e) {
            tagException = true;
        }
        Assert.assertTrue(tagException);
        for (AnalysisPluginInterface api : SWQEFactory.getQueryInterface().getAnalysisPlugins()) {
            Assert.assertTrue(!(api instanceof Taggable));
        }
        Assert.assertTrue(SWQEFactory.getQueryInterface().getAnalysisPlugins().getCount() > 0);
    }

    @Test
    public void testTagAddingAndRemoval() {
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        // tags should be both addable and removable
        // tags should be added and removed without changing version numbers 
        // TODO: (not for now though)
        Tag tag1a = ts1.toBuilder().build();
        User u = mManager.buildUser().setFirstName("John").setLastName("Smith").setEmailAddress("john.smith@googly.com").setPassword("password").build();
        u.associateTag(tag1a);
        long version1 = u.getVersion();
        Assert.assertTrue(u.getTags().getCount() == 1);
        u.dissociateTag(tag1a);
        Assert.assertTrue(u.getTags().getCount() == 0);
        long version2 = u.getVersion();
        Assert.assertTrue(version1 == version2);
    }

    @Test
    public void testTagQueries() {
        // three features in the set
        Assert.assertTrue(fSet.getCount() == 3);
        // test queries that filter based on all three possibilities for tags 
        // subject only, subject and predicate, or all three
        // should get any features tagged with anything
        QueryFuture featuresByTag = SWQEFactory.getQueryInterface().getFeaturesByTag(0, fSet, null, null, null);
        Assert.assertTrue(featuresByTag.get().getCount() == 3);
        // should get nothing
        QueryFuture featuresByTag1 = SWQEFactory.getQueryInterface().getFeaturesByTag(0, fSet, "impossible", "impossible", "impossible");
        Assert.assertTrue(featuresByTag1.get().getCount() == 0);
        // should get all three
        QueryFuture featuresByTag2 = SWQEFactory.getQueryInterface().getFeaturesByTag(0, fSet, "KR", null, null);
        Assert.assertTrue(featuresByTag2.get().getCount() == 3);
        // should get all three
        QueryFuture featuresByTag3 = SWQEFactory.getQueryInterface().getFeaturesByTag(0, fSet, "KR", "=", null);
        Assert.assertTrue(featuresByTag3.get().getCount() == 3);
        // should get one
        QueryFuture featuresByTag4 = SWQEFactory.getQueryInterface().getFeaturesByTag(0, fSet, "KR", "=", "F");
        Assert.assertTrue(featuresByTag4.get().getCount() == 1);
        // should get one
        QueryFuture featuresByTag5 = SWQEFactory.getQueryInterface().getFeaturesByTag(0, fSet, "KR", null, "F");
        Assert.assertTrue(featuresByTag5.get().getCount() == 1);

    }

    @Test
    public void tagWithDifferentTypes() {
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        Tag ta = ts1.toBuilder().setValue("Test_String").build();
        Tag tb = ts1.toBuilder().setValue("Test_String".getBytes()).build();
        Tag tc = ts1.toBuilder().setValue(new Float(0.1f)).build();
        Tag td = ts1.toBuilder().setValue(new Double(0.1)).build();
        Tag te = ts1.toBuilder().setValue(new Long(1)).build();
        Tag tf = ts1.toBuilder().setValue(new Integer(10)).build();
        Tag tg = ts1.toBuilder().setValue(fSet.getSGID()).build();
        User u = mManager.buildUser().setFirstName("John").setLastName("Smith").setEmailAddress("john.smith@googly.com").setPassword("password").build();
        u.associateTag(ta);
        u.associateTag(tb);
        u.associateTag(tc);
        u.associateTag(td);
        u.associateTag(te);
        u.associateTag(tf);
        u.associateTag(tg);
        mManager.flush();
        // check that the tags are present
        User user = (User) SWQEFactory.getQueryInterface().getAtomBySGID(User.class, u.getSGID());
        Tag[] tagsCheck = {t1a, t1b, t1c, t2a, t2b, t2c, t3a};
        boolean[] tagsCheckFound = new boolean[tagsCheck.length];
        Arrays.fill(tagsCheckFound, false);
        // there should be nothing here
        for (Tag t : user.getTags()) {
            for (int i = 0; i < tagsCheckFound.length; i++) {
                if (tagsCheck[i].equals(ta)) {
                    tagsCheckFound[i] = true;
                }
            }
        }
        for (boolean b : tagsCheckFound) {
            Assert.assertTrue(!b);
        }
    }

    @Test
    public void testTagSetParentReferences() {
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        // create an "ontology" tag set
        TagSpecSet tagset = mManager.buildTagSpecSet().setName("one tag set to bind them all").build();
        // add a few tag "specifications" to the tag set
        Tag dwarfTag = mManager.buildTagSpec().setKey("dwarf").build();
        Tag elvenTag = mManager.buildTagSpec().setKey("elven").build();
        Tag humanTag = mManager.buildTagSpec().setKey("human").build();
        tagset.add(dwarfTag, elvenTag, humanTag);
        mManager.flush();
                
        // build a FeatureSet, add features to it
        FeatureSet fset = mManager.buildFeatureSet().setReference(mManager.buildReference().setName("testing_Dummy_reference").build()).build();
        Feature fe1 = mManager.buildFeature().setId("chr16").setStart(1000000).setStop(1000100).build();
        Feature fe2 = mManager.buildFeature().setId("chr16").setStart(1000200).setStop(1000300).build();
        Feature fe3 = mManager.buildFeature().setId("chr16").setStart(1000400).setStop(1000500).build();
        fset.add(fe1, fe2, fe3);
        // tag the feature set 
        fset.associateTag(dwarfTag.toBuilder().setValue("digs").build());
        fset.associateTag(elvenTag.toBuilder().setValue("immigrates").build());
        fset.associateTag(humanTag.toBuilder().setValue("fights").build());
        fe1.associateTag(dwarfTag.toBuilder().setValue("gimli").build());
        fe1.associateTag(elvenTag.toBuilder().setValue("legolas").build());
        fe1.associateTag(humanTag.toBuilder().setValue("boromir").build());
        
        Assert.assertTrue("feature set does not have proper number of tags", fset.getTags().getCount() == 3);
        Assert.assertTrue("fe2 count", fe2.getTags().getCount() == 0);
        Assert.assertTrue("fe1 tag count", fe1.getTags().getCount() == 3);
        // check parents
        boolean correctParent = true;
        for(Tag t : fset.getTags()){
            correctParent = t.getTagSet().getSGID().equals(tagset.getSGID());
        }
        for(Tag t : fe1.getTags()){
            correctParent = t.getTagSet().getSGID().equals(tagset.getSGID());
        }
        Assert.assertTrue("tags do not have proper parents", correctParent);
        mManager.flush();
        
        // try it again for persisted tags
        fset = SWQEFactory.getQueryInterface().getAtomBySGID(FeatureSet.class, fset.getSGID());
        fe2 = SWQEFactory.getQueryInterface().getAtomBySGID(Feature.class, fe2.getSGID());
        fe1 = SWQEFactory.getQueryInterface().getAtomBySGID(Feature.class, fe1.getSGID());
        Assert.assertTrue("persisted feature set does not have proper number of tags", fset.getTags().getCount() == 3);
        Assert.assertTrue("persisted fe2 count", fe2.getTags().getCount() == 0);
        Assert.assertTrue("persisted fe1 tag count", fe1.getTags().getCount() == 3);
        // check parents
        correctParent = true;
        for(Tag t : fset.getTags()){
            correctParent = t.getTagSet().getSGID().equals(tagset.getSGID());
        }
        for(Tag t : fe1.getTags()){
            correctParent = t.getTagSet().getSGID().equals(tagset.getSGID());
        }
        Assert.assertTrue("persisted tags do not have proper parents", correctParent);
    }
}
