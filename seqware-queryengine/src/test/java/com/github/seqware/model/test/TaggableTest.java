package com.github.seqware.model.test;

import com.github.seqware.factory.Factory;
import com.github.seqware.model.*;
import com.github.seqware.util.SeqWareIterable;
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
        // test tagging every possible class that can be tagged
        // create a few instances of everything that can be tagged
        fSet = Factory.buildFeatureSet(Factory.buildReference("testing dummy reference"));
        Set<Feature> testFeatures = new HashSet<Feature>();
        f1 = new Feature(fSet, 1000000, 1000100);
        f2 = new Feature(fSet, 1000200, 1000300);
        f3 = new Feature(fSet, 1000400, 1000500);
        testFeatures.add(f1);      
        testFeatures.add(f2);
        testFeatures.add(f3);
        fSet.add(testFeatures);
        tSet1 = Factory.buildTagSet("Funky tags");
        tSet2 = Factory.buildTagSet("Unfunky tags");
        rSet = Factory.buildReferenceSet("Minbar", "Minbari");
        aSet = Factory.buildAnalysisSet("FP", "Funky program");
        a = Factory.buildAnalysis(null);
        r1 = Factory.buildReference("ref1");
        rSet.add(r1);
        group = new Group("Developers", "Users that are working on new stuff");
        u1 = new User(group, "Joe", "Smith", "joe.smith@googly.com", "password");
        group.add(u1);
        // tag stuff
        Tag t1a = new Tag(tSet1, "KR");
        Tag t1b = new Tag(tSet1, "KR", "=");
        Tag t1c = new Tag(tSet1, "KR", "=", "F");
        Tag t2a = new Tag(tSet1, "AS");
        Tag t2b = new Tag(tSet1, "AS", "=");
        Tag t2c = new Tag(tSet1, "AS", "=", "T800");
        Tag t3a = new Tag(tSet2, "JC");
        // 7 new tags added
        t1a.store();
        t1b.store();
        t1c.store();
        t2a.store();
        t2b.store();
        t2c.store();
        t3a.store();

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

        // persist everything
        fSet.store();
        f1.store();
        f2.store();
        f3.store();
        tSet1.store();
        tSet2.store();
        rSet.store();
        aSet.store();
        r1.store();
        group.store();
        u1.store();
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
        Tag t1a = new Tag(tSet1, "KR");
        Assert.assertTrue(!(t1a instanceof Taggable));
        for (AnalysisPluginInterface api : Factory.getFeatureStoreInterface().getAnalysisPlugins()) {
            Assert.assertTrue(!(api instanceof Taggable));
        }
        Assert.assertTrue(Factory.getFeatureStoreInterface().getAnalysisPlugins().getCount() > 0);
    }

    @Test
    public void testTagAddingAndRemoval() {
        // tags should be both addable and removable
        // tags should be added and removed without changing version numbers 
        Tag t1a = new Tag(tSet1, "KR");
        User u = new User(group, "John", "Smith", "john.smith@googly.com", "password");
        t1a.store();
        u.store();
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
