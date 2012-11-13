package com.github.seqware.queryengine.model.test;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.*;
import com.github.seqware.queryengine.model.interfaces.Taggable;
import com.github.seqware.queryengine.model.interfaces.Taggable.NestedLevel;
import com.github.seqware.queryengine.plugins.AnalysisPluginInterface;
import com.github.seqware.queryengine.plugins.inmemory.InMemoryFeaturesAllPlugin;
import java.util.*;
import junit.framework.Assert;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests of {@link com.github.seqware.queryengine.model.interfaces.Taggable}.
 *
 * @author dyuen
 * @version $Id: $Id
 * @since 0.13.3
 */
public class TaggableTest {

    private static FeatureSet fSet;
    private static Feature f1, f2, f3;
    private static TagSet tSet1, tSet2;
    private static ReferenceSet rSet;
    private static Reference r1;
    private static Group group;
    private static User u1;
    private static AnalysisType aSet;
    private static AnalysisRun a;
    private static Tag ts1, ts2, ts3;
    private static Tag t1a, t1b, t1c, t2a, t2b, t2c, t3a;

    /**
     * <p>setupTests.</p>
     */
    @BeforeClass
    public static void setupTests() {
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        // test tagging every possible class that can be tagged
        // create a few instances of everything that can be tagged
        fSet = mManager.buildFeatureSet().setReference(mManager.buildReference().setName("testing_Dummy_reference").build()).build();
        Set<Feature> testFeatures = new HashSet<Feature>();
        f1 = mManager.buildFeature().setSeqid("chr16").setStart(1000000).setStop(1000100).build();
        f2 = mManager.buildFeature().setSeqid("chr16").setStart(1000200).setStop(1000300).build();
        f3 = mManager.buildFeature().setSeqid("chr16").setStart(1000400).setStop(1000500).build();
        testFeatures.add(f1);
        testFeatures.add(f2);
        testFeatures.add(f3);
        fSet.add(testFeatures);
        tSet1 = mManager.buildTagSet().setName("Funky tags").build();
        tSet2 = mManager.buildTagSet().setName("Unfunky tags").build();
        rSet = mManager.buildReferenceSet().setName("Minbar").setOrganism("Minbari").build();
        aSet = mManager.buildAnalysisType().setName("FP").setDescription("Funky program").build();
        // only for testing, AnalysisRun classes 
        a = mManager.buildAnalysis().setParameters(new ArrayList<Object>()).setPlugin(new InMemoryFeaturesAllPlugin()).build();
        r1 = mManager.buildReference().setName("ref1").build();
        rSet.add(r1);
        group = mManager.buildGroup().setName("Developers").setDescription("Users that are working on new stuff").build();
        u1 = mManager.buildUser().setFirstName("Joe").setLastName("Smith").setEmailAddress("joe.smith@googly.com").setPassword("password").build();
        group.add(u1);

        // create tag specifications
        ts1 = mManager.buildTag().setKey("KR").build();
        ts2 = mManager.buildTag().setKey("AS").build();
        ts3 = mManager.buildTag().setKey("JC").build();
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

    /**
     * <p>testAddingAndRemovingTagSpecsFromTagSets.</p>
     */
    @Test
    public void testAddingAndRemovingTagSpecsFromTagSets() {
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        // tags are not initially in a key set
        TagSet initialTestSet = (TagSet) SWQEFactory.getQueryInterface().getAtomBySGID(TagSet.class, tSet2.getSGID());
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
        TagSet testSet = (TagSet) SWQEFactory.getQueryInterface().getAtomBySGID(TagSet.class, tSet2.getSGID());
        Assert.assertTrue(testSet.getCount() == 2);
        tSet2.add(ts3);
        mManager.flush();
        testSet = (TagSet) SWQEFactory.getQueryInterface().getAtomBySGID(TagSet.class, tSet2.getSGID());
        Assert.assertTrue(testSet.getCount() == 3);
        Assert.assertTrue(testSet.getPrecedingVersion().getCount() == 2);
        // and then remove them
        tSet2.remove(ts3).remove(ts2);
        mManager.flush();
        testSet = (TagSet) SWQEFactory.getQueryInterface().getAtomBySGID(TagSet.class, tSet2.getSGID());
        Assert.assertTrue(testSet.getCount() == 1);
        Assert.assertTrue(testSet.getPrecedingVersion().getCount() == 3);
    }

    /**
     * <p>testClassesThatCannotBeTagged.</p>
     */
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

    /**
     * <p>testTagAddingAndRemoval.</p>
     */
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
        Assert.assertTrue("found " + u.getTags().getCount() + " tags, expected " + 1, u.getTags().getCount() == 1);
        u.dissociateTag(tag1a);
        Assert.assertTrue("found " + u.getTags().getCount() + " tags, expected " + 0, u.getTags().getCount() == 0);
        long version2 = u.getVersion();
        Assert.assertTrue(version1 == version2);
    }

    /**
     * <p>testTagQueries.</p>
     */
    @Test
    public void testTagQueries() {
        // three features in the set
        Assert.assertTrue("expected 3 and found " + fSet.getCount(), fSet.getCount() == 3);
        // test queries that filter based on all three possibilities for tags 
        // subject only, subject and predicate, or all three
        // should get any features tagged with anything
        QueryFuture<FeatureSet> featuresByTag = SWQEFactory.getQueryInterface().getFeaturesByTag(0, fSet, null, null, null);
        Assert.assertTrue(featuresByTag.get().getCount() == 3);
        // should get nothing
        QueryFuture<FeatureSet> featuresByTag1 = SWQEFactory.getQueryInterface().getFeaturesByTag(0, fSet, "impossible", "impossible", "impossible");
        Assert.assertTrue(featuresByTag1.get().getCount() == 0);
        // should get all three
        QueryFuture<FeatureSet> featuresByTag2 = SWQEFactory.getQueryInterface().getFeaturesByTag(0, fSet, "KR", null, null);
        Assert.assertTrue(featuresByTag2.get().getCount() == 3);
        // should get all three
        QueryFuture<FeatureSet> featuresByTag3 = SWQEFactory.getQueryInterface().getFeaturesByTag(0, fSet, "KR", "=", null);
        Assert.assertTrue(featuresByTag3.get().getCount() == 3);
        // should get one
        QueryFuture<FeatureSet> featuresByTag4 = SWQEFactory.getQueryInterface().getFeaturesByTag(0, fSet, "KR", "=", "F");
        Assert.assertTrue(featuresByTag4.get().getCount() == 1);
        // should get one
        QueryFuture<FeatureSet> featuresByTag5 = SWQEFactory.getQueryInterface().getFeaturesByTag(0, fSet, "KR", null, "F");
        Assert.assertTrue(featuresByTag5.get().getCount() == 1);

    }

    /**
     * <p>testFlushingTagsWithoutSets.</p>
     */
    @Test
    public void testFlushingTagsWithoutSets() {
        try {
            CreateUpdateManager mManager = SWQEFactory.getModelManager();
            Tag ta = Tag.newBuilder().setValue("Test_String").build();
            User u = mManager.buildUser().setFirstName("John").setLastName("Smith").setEmailAddress("john.smith@googly.com").setPassword("password").build();
            u.associateTag(ta);

            mManager.close();
            fail("close() should've thrown an exception since there are unattached tags");
        } catch (RuntimeException e) {
            // we expect an exception to be thrown when we try to persist a Tag without a parental TagSet
        }
        
        try {
            CreateUpdateManager mManager = SWQEFactory.getModelManager();
            Tag ta = mManager.buildTag().setValue("Test_String").build();
            mManager.close();
            fail("close() should've thrown an exception since there are unattached tags");
        } catch (RuntimeException e) {
            // we expect an exception to be thrown when we try to persist a Tag without a parental TagSet
        }
    }
    
    /**
     * <p>testSameTagInTwoDifferentTagSets.</p>
     */
    @Test 
    public void testSameTagInTwoDifferentTagSets() {
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        // create an "ontology" tag set
        TagSet tolkienSet = mManager.buildTagSet().setName("tolkien").build();
        // add a few tag "specifications" to the tag set
        Tag dwarfTag = mManager.buildTag().setKey("dwarf").build();
        Tag elvenTag = mManager.buildTag().setKey("elven").build();
        Tag humanTag = mManager.buildTag().setKey("human").build();
        tolkienSet.add(dwarfTag, elvenTag, humanTag);
        
        TagSet elderSet = mManager.buildTagSet().setName("elder scrolls").build();
        // add a few tag "specifications" to the tag set
        Tag dwarfTag2 = mManager.buildTag().setKey("dwarf").build();
        Tag elvenTag2 = mManager.buildTag().setKey("elven").build();
        Tag humanTag2 = mManager.buildTag().setKey("human").build();
        elderSet.add(dwarfTag2, elvenTag2, humanTag2);
        
        User u = mManager.buildUser().setFirstName("John").setLastName("Smith").setEmailAddress("john.smith@googly.com").setPassword("password").build();
        u.associateTag(dwarfTag); 
        u.associateTag(elvenTag);
        u.associateTag(humanTag);
        u.associateTag(dwarfTag2); 
        u.associateTag(elvenTag2);
        u.associateTag(humanTag2);
        mManager.flush();
        
        User u2 = SWQEFactory.getQueryInterface().getAtomBySGID(User.class, u.getSGID());
        Assert.assertTrue("expected 6 tags, found " + u.getTags().getCount(), u.getTags().getCount() == 6);
        Assert.assertTrue("expected 6 tags, found " + u2.getTags().getCount(), u2.getTags().getCount() == 6);
        Assert.assertTrue("could not find dwarf tag ", u.getTagByKey(tolkienSet, "dwarf") != null);
        Assert.assertTrue("could not find dwarf tag ", u2.getTagByKey(elderSet, "dwarf") != null);
        Assert.assertTrue("could not find elven tag ", u.getTagByKey(tolkienSet, "elven") != null);
        Assert.assertTrue("could not find elven tag ", u2.getTagByKey(elderSet, "elven") != null);
        Assert.assertTrue("could not find human tag ", u.getTagByKey(tolkienSet, "human") != null);
        Assert.assertTrue("could not find human tag ", u2.getTagByKey(elderSet, "human") != null);
    }

    /**
     * <p>tagWithDifferentTypes.</p>
     */
    @Test
    public void tagWithDifferentTypes() {
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        Tag ta = ts1.toBuilder().setValue("Test_String").build();
        Tag tb = ts1.toBuilder().setValue("Test_String".getBytes()).build();
        Tag tc = ts1.toBuilder().setValue(Float.valueOf(0.1f)).build();
        Tag td = ts1.toBuilder().setValue(Double.valueOf(0.1)).build();
        Tag te = ts1.toBuilder().setValue(Long.valueOf(1)).build();
        Tag tf = ts1.toBuilder().setValue(Integer.valueOf(10)).build();
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

    /**
     * <p>testTagSetParentReferences.</p>
     */
    @Test
    public void testTagSetParentReferences() {
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        // create an "ontology" tag set
        TagSet tagset = mManager.buildTagSet().setName("one tag set to bind them all").build();
        // add a few tag "specifications" to the tag set
        Tag dwarfTag = mManager.buildTag().setKey("dwarf").build();
        Tag elvenTag = mManager.buildTag().setKey("elven").build();
        Tag humanTag = mManager.buildTag().setKey("human").build();
        tagset.add(dwarfTag, elvenTag, humanTag);
        mManager.flush();

        // build a FeatureSet, add features to it
        FeatureSet fset = mManager.buildFeatureSet().setReference(mManager.buildReference().setName("testing_Dummy_reference").build()).build();
        Feature fe1 = mManager.buildFeature().setSeqid("chr16").setStart(1000000).setStop(1000100).build();
        Feature fe2 = mManager.buildFeature().setSeqid("chr16").setStart(1000200).setStop(1000300).build();
        Feature fe3 = mManager.buildFeature().setSeqid("chr16").setStart(1000400).setStop(1000500).build();
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
        for (Tag t : fset.getTags()) {
            correctParent = t.getTagSet().getSGID().equals(tagset.getSGID());
        }
        for (Tag t : fe1.getTags()) {
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
        for (Tag t : fset.getTags()) {
            correctParent = t.getTagSet().getSGID().equals(tagset.getSGID());
        }
        for (Tag t : fe1.getTags()) {
            correctParent = t.getTagSet().getSGID().equals(tagset.getSGID());
        }
        Assert.assertTrue("persisted tags do not have proper parents", correctParent);
    }

    /**
     * <p>testNestedHashes.</p>
     */
    @Test
    public void testNestedHashes() {
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        TagSet tagset = mManager.buildTagSet().build();
        Tag ta = mManager.buildTag().setKey("MTTS::dbSNP::ID").setValue("rs123").build();
        Tag tb = mManager.buildTag().setKey("MTTS::dbSNP::pop_freq").setValue(0.70f).build();
        User u = mManager.buildUser().setFirstName("John").setLastName("Smith").setEmailAddress("john.smith@googly.com").setPassword("password").build();
        tagset.add(ta, tb);
        u.associateTag(ta);
        u.associateTag(tb);

        Tag t1 = mManager.buildTag().setKey("TS::A").setValue(1).build();
        Tag t2 = mManager.buildTag().setKey("TS::B").setValue(2).build();
        Tag t3 = mManager.buildTag().setKey("TS::C::1").setValue(3).build();
        Tag t4 = mManager.buildTag().setKey("TS::C::2").setValue(4).build();
        Tag t5 = mManager.buildTag().setKey("TS::C::3").setValue(5).build();
        Tag t6 = mManager.buildTag().setKey("TS::D::1").setValue(6).build();
        Tag t7 = mManager.buildTag().setKey("TS::D::2").setValue(7).build();
        Tag t8 = mManager.buildTag().setKey("TS::Z1").setValue(8).build();
        Tag t9 = mManager.buildTag().setKey("TS::Z2").setValue(9).build();
        Tag t10 = mManager.buildTag().setKey("TQ::A").setValue(10).build();

        tagset.add(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10);

        User u2 = mManager.buildUser().setFirstName("John").setLastName("Smith").setEmailAddress("john.smith@googly.com").setPassword("password").build();
        u2.associateTag(t1);
        u2.associateTag(t2);
        u2.associateTag(t3);
        u2.associateTag(t4);
        u2.associateTag(t5);
        u2.associateTag(t6);
        u2.associateTag(t7);
        u2.associateTag(t8);
        u2.associateTag(t9);
        u2.associateTag(t10);
        mManager.flush();

        // do our checking on the objects after we retrieve them
        User userPersisted1 = SWQEFactory.getQueryInterface().getAtomBySGID(User.class, u.getSGID());
        User userPersisted2 = SWQEFactory.getQueryInterface().getAtomBySGID(User.class, u2.getSGID());

        // check the first nested hash and check the values while we're at it
        NestedLevel nestedTags = userPersisted1.getNestedTags(tagset);
        Assert.assertTrue("root level ok", nestedTags.getChildTags().isEmpty() && nestedTags.getChildMaps().size() == 1);
        nestedTags = nestedTags.getChildMaps().get("MTTS");
        Assert.assertTrue("MTTS level ok", nestedTags.getChildTags().isEmpty() && nestedTags.getChildMaps().size() == 1);
        nestedTags = nestedTags.getChildMaps().get("dbSNP");
        Assert.assertTrue("dbSNP level ok", nestedTags.getChildTags().size() == 2 && nestedTags.getChildMaps().isEmpty());
        Assert.assertTrue("ID Tag ok", nestedTags.getChildTags().get("ID").getValue().equals("rs123"));
        Assert.assertTrue("pop_freq Tag ok", (Float) nestedTags.getChildTags().get("pop_freq").getValue() == 0.70f);

        // check the second one, just level numbers
        nestedTags = userPersisted2.getNestedTags(tagset);
        Assert.assertTrue("root level ok", nestedTags.getChildTags().isEmpty() && nestedTags.getChildMaps().size() == 2);
        nestedTags = nestedTags.getChildMaps().get("TS");
        Assert.assertTrue("TS level ok", nestedTags.getChildTags().size() == 4 && nestedTags.getChildMaps().size() == 2);
        nestedTags = userPersisted2.getNestedTags(tagset).getChildMaps().get("TQ");
        Assert.assertTrue("TQ level ok", nestedTags.getChildTags().size() == 1 && nestedTags.getChildMaps().isEmpty());
    }
}
