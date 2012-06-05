package com.github.seqware.model.test;

import com.github.seqware.factory.Factory;
import com.github.seqware.factory.ModelManager;
import com.github.seqware.model.*;
import com.github.seqware.model.impl.inMemory.InMemoryFeaturesAllPlugin;
import com.github.seqware.model.impl.inMemory.InMemoryQueryFutureImpl;
import com.github.seqware.util.SeqWareIterable;
import java.util.ArrayList;
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
    private static TagSet tSet1, tSet2;
    private static ReferenceSet rSet;
    private static Reference r1;
    private static Group group;
    private static User u1;
    private static AnalysisSet aSet;
    private static Analysis a;

    @BeforeClass
    public static void setupTests() {
        ModelManager mManager = Factory.getModelManager();
        // test tagging every possible class that can be tagged
        // create a few instances of everything that can be tagged
        fSet = mManager.buildFeatureSet().setReference(mManager.buildReference().setName("testing dummy reference").build()).build();
        Set<Feature> testFeatures = new HashSet<Feature>();
        f1 = mManager.buildFeature().setStart(1000000).setStop(1000100).build();
        f2 = mManager.buildFeature().setStart(1000200).setStop(1000300).build();
        f3 = mManager.buildFeature().setStart(1000400).setStop(1000500).build();
        testFeatures.add(f1);      
        testFeatures.add(f2);
        testFeatures.add(f3);
        fSet.add(testFeatures);
        tSet1 = mManager.buildTagSet().setName("Funky tags").build();
        tSet2 = mManager.buildTagSet().setName("Unfunky tags").build();
        rSet = mManager.buildReferenceSet().setName("Minbar").setOrganism("Minbari").build();
        aSet = mManager.buildAnalysisSet().setName("FP").setDescription("Funky program").build();
        // only for testing, Analysis classes 
        a = mManager.buildAnalysis().setParameters(new ArrayList<Object>()).setPlugin(new InMemoryFeaturesAllPlugin()).build();
        r1 = mManager.buildReference().setName("ref1").build();
        rSet.add(r1);
        group = mManager.buildGroup().setName("Developers").setDescription("Users that are working on new stuff").build();
        u1 = mManager.buildUser().setFirstName("Joe").setLastName("Smith").setEmailAddress("joe.smith@googly.com").setPassword("password").build();
        group.add(u1);
        // tag stuff
        Tag t1a = mManager.buildTag().setKey("KR").build();
        Tag t1b = mManager.buildTag().setKey("KR").setPredicate("=").build();
        Tag t1c = mManager.buildTag().setKey("KR").setPredicate("=").setValue("F").build();
        Tag t2a = mManager.buildTag().setKey("AS").build();
        Tag t2b = mManager.buildTag().setKey("AS").setPredicate("=").build();
        Tag t2c = mManager.buildTag().setKey("AS").setPredicate("=").setValue("T800").build();
        Tag t3a = mManager.buildTag().setKey("JC").build();
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

        mManager.flush();
    }

    @Test
    public void testTaggingOnEverything() {
// Some of these global tests are no longer working because the back-end persists between test classes
        SeqWareIterable<TagSet> tagSets = Factory.getFeatureStoreInterface().getTagSets();
        // we have two tag sets
//        Assert.assertTrue(tagSets.getCount() == 2);
//        SeqWareIterable<Tag> tags = Factory.getFeatureStoreInterface().getTags();
        // 7 tags were added to the back-end
//        Assert.assertTrue(tags.getCount() == 7);
        SeqWareIterable<Tag> tags1 = fSet.getTags();
        // 3 tags were associated with the featureSet
        Assert.assertTrue(tags1.getCount() == 3);
        Assert.assertTrue(a.getTags().getCount() == 1);
    }

    @Test
    public void testClassesThatCannotBeTagged() {
        // practically everything can be tagged, except for plugins and tags
        ModelManager mManager = Factory.getModelManager();
        Tag t1a = mManager.buildTag().setKey("KR").build();
        Assert.assertTrue(!(t1a instanceof Taggable));
        for (AnalysisPluginInterface api : Factory.getFeatureStoreInterface().getAnalysisPlugins()) {
            Assert.assertTrue(!(api instanceof Taggable));
        }
        Assert.assertTrue(Factory.getFeatureStoreInterface().getAnalysisPlugins().getCount() > 0);
    }

    @Test
    public void testTagAddingAndRemoval() {
        ModelManager mManager = Factory.getModelManager();
        // tags should be both addable and removable
        // tags should be added and removed without changing version numbers 
        // TODO: (not for now though)
        Tag t1a = mManager.buildTag().setKey("KR").build();
        User u = mManager.buildUser().setFirstName("John").setLastName("Smith").setEmailAddress("john.smith@googly.com").setPassword("password").build();
        u.associateTag(t1a);
        long version1 = u.getVersion();
        Assert.assertTrue(u.getTags().getCount() == 1);
        u.dissociateTag(t1a);
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
        QueryFuture featuresByTag = Factory.getQueryInterface().getFeaturesByTag(fSet, 0, null, null, null);
        Assert.assertTrue(featuresByTag.get().getCount() == 3);
        // should get nothing
        QueryFuture featuresByTag1 = Factory.getQueryInterface().getFeaturesByTag(fSet, 0, "impossible", "impossible", "impossible");
        Assert.assertTrue(featuresByTag1.get().getCount() == 0);
        // should get all three
        QueryFuture featuresByTag2 = Factory.getQueryInterface().getFeaturesByTag(fSet, 0, "KR", null, null);
        Assert.assertTrue(featuresByTag2.get().getCount() == 3);
        // should get all three
        QueryFuture featuresByTag3 = Factory.getQueryInterface().getFeaturesByTag(fSet, 0, "KR", "=", null);
        Assert.assertTrue(featuresByTag3.get().getCount() == 3);
        // should get one
        QueryFuture featuresByTag4 = Factory.getQueryInterface().getFeaturesByTag(fSet, 0, "KR", "=", "F");
        Assert.assertTrue(featuresByTag4.get().getCount() == 1);
        // should get one
        QueryFuture featuresByTag5 = Factory.getQueryInterface().getFeaturesByTag(fSet, 0, "KR", null, "F");
        Assert.assertTrue(featuresByTag5.get().getCount() == 1);

    }
}
