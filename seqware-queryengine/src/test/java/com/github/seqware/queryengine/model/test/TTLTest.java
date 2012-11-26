package com.github.seqware.queryengine.model.test;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.*;
import com.github.seqware.queryengine.model.interfaces.TTLable;
import com.github.seqware.queryengine.plugins.inmemory.InMemoryPluginRunner;
import com.github.seqware.queryengine.plugins.plugins.FeaturesAllPlugin;
import java.util.*;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests of {@link com.github.seqware.queryengine.model.interfaces.TTLable}.
 *
 * @author dyuen
 * @version $Id: $Id
 * @since 0.13.3
 */
public class TTLTest {

    private static FeatureSet fSet;
    private static Feature f1;
    private static TagSet tSet1;
    private static ReferenceSet rSet;
    private static Reference r1;
    private static Group group, viewerGroup;
    private static User marshmallowUser, titanicUser;
    private static Plugin aSet;
    private static PluginRun a1;

    /**
     * <p>setupTests.</p>
     */
    @BeforeClass
    public static void setupTests() {
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        // test TTL on every possible class that can be TTLed
        fSet = mManager.buildFeatureSet().setReference(mManager.buildReference().setName("testing_Dummy_reference").build()).build();
        fSet.setTTL(1, true);
        Set<Feature> testFeatures = new HashSet<Feature>();
        f1 = mManager.buildFeature().setSeqid("chr16").setStart(1000000).setStop(1000100).build();
        testFeatures.add(f1);
        fSet.add(testFeatures);
        tSet1 = mManager.buildTagSet().setName("Funky tags").build();
        tSet1.setTTL(2, false);
        rSet = mManager.buildReferenceSet().setName("Minbar").setOrganism("Minbari").build();
        // time in the future
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.HOUR, 10);
        rSet.setTTL(calendar.getTimeInMillis(), true);
        aSet = mManager.buildPlugin().setName("FP").setDescription("Funky program").build();
        r1 = mManager.buildReference().setName("ref1").build();
        rSet.add(r1);
        group = mManager.buildGroup().setName("Developers").setDescription("Users that are working on new stuff").build();
        viewerGroup = mManager.buildGroup().setName("Viewers").setDescription("Users that are just looking at stuff").build();
        marshmallowUser = mManager.buildUser().setFirstName("Joe").setLastName("Smith").setEmailAddress("joe.smith@googly.com").setPassword("password").build();
        marshmallowUser.setTTL(10, true);
        titanicUser = mManager.buildUser().setFirstName("Deanna").setLastName("Troi").setEmailAddress("deanna.troi@googly.com").setPassword("password").build();
        titanicUser.setTTL(5, false);
        a1 = mManager.buildPluginRun().setParameters(new ArrayList()).setPluginRunner(SWQEFactory.getPluginRunner(null, null)).build();
        a1.setTTL(TTLable.FOREVER, true);
        group.add(marshmallowUser);
        viewerGroup.add(titanicUser);
        mManager.close();
    }

    /**
     * <p>testTTLOnSets.</p>
     */
    @Test
    public void testTTLOnSets() {
        // do complete tests on FeatureSet
        FeatureSet testSet = SWQEFactory.getQueryInterface().getAtomBySGID(FeatureSet.class, fSet.getSGID());
        Assert.assertTrue("featureSet cascade wrong", testSet.getCascade() == true);
        Assert.assertTrue("featureSet time (in hours) wrong", testSet.getTTL() == 1);
        Date fSetExpiryTime = new Date(testSet.getExpiryTime());
        Calendar twoHoursAhead = new GregorianCalendar();
        twoHoursAhead.add(Calendar.HOUR, 2);
        Assert.assertTrue("featureSet time (as long) wrong", fSetExpiryTime.after(new Date()) && fSetExpiryTime.before(twoHoursAhead.getTime()));
        Assert.assertTrue("featureSet time (as Date) wrong", testSet.getExpiryDate().after(new Date()) && testSet.getExpiryDate().before(twoHoursAhead.getTime()));
        // handle time two hours in future
        TagSet tagSet = SWQEFactory.getQueryInterface().getAtomBySGID(TagSet.class, tSet1.getSGID());
        Assert.assertTrue("tagSet cascade wrong", tagSet.getCascade() == false);
        Assert.assertTrue("tagSet time (in hours) wrong", tagSet.getTTL() == 2);
        Date tSetExpiryTime = new Date(tagSet.getExpiryTime());
        Calendar threeHoursAhead = new GregorianCalendar();
        threeHoursAhead.add(Calendar.HOUR, 3);
        Assert.assertTrue("tagSet time (as long) wrong", tSetExpiryTime.after(new Date()) && tSetExpiryTime.before(threeHoursAhead.getTime()));
        Assert.assertTrue("tagSet time (as Date) wrong", tagSet.getExpiryDate().after(new Date()) && tagSet.getExpiryDate().before(threeHoursAhead.getTime()));
        Assert.assertTrue("tagSet isExpires wrong", tagSet.isExpires() == true);
        // handle third set
        ReferenceSet refSet = SWQEFactory.getQueryInterface().getAtomBySGID(ReferenceSet.class, rSet.getSGID());
        Assert.assertTrue("refSet cascade wrong", refSet.getCascade() == true);
        Assert.assertTrue("refSet time (in hours) wrong", refSet.getTTL() == 10);
        Assert.assertTrue("refSet isExpires wrong", refSet.isExpires() == true);
        // handle unmodified set
        Group g1 = SWQEFactory.getQueryInterface().getAtomBySGID(Group.class, viewerGroup.getSGID());
        Assert.assertTrue("group isExpires wrong", g1.isExpires() == false);
    }

    /**
     * <p>testTTLOnElements.</p>
     */
    @Test
    public void testTTLOnElements() {
        User mUser = SWQEFactory.getQueryInterface().getAtomBySGID(User.class, marshmallowUser.getSGID());
        User tUser = SWQEFactory.getQueryInterface().getAtomBySGID(User.class, titanicUser.getSGID());
        // note: individual elements will just ignore cascading
        Assert.assertTrue("mUser cascade wrong", mUser.getCascade() == false);
        Assert.assertTrue("mUser time (in hours) wrong", mUser.getTTL() == 10);
        Assert.assertTrue("isExpires wrong", mUser.isExpires() == true);
        Assert.assertTrue("tUser cascade wrong", tUser.getCascade() == false);
        Assert.assertTrue("tUser time (in hours) wrong", tUser.getTTL() == 5);
        Assert.assertTrue("isExpires wrong", tUser.isExpires() == true);

    }
}
