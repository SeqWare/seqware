package com.github.seqware.model.test;

import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import java.util.UUID;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests that storing FeatureSets and retrieving FeatureSets results in
 * consistent results.
 *
 * @author dyuen
 * @author jbaran
 */
public class FeatureStoreInterfaceTest {

    protected static FeatureSet aSet;
    protected static FeatureSet bSet;
    protected static Feature a1, a2, a3;

    @BeforeClass
    public static void setupTests() {
        UUID testID = UUID.randomUUID();
        //System.out.println("starting beforeClass in testID: " + testID.toString());
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        aSet = mManager.buildFeatureSet().setReference(mManager.buildReference().setName("Dummy_ref").build()).build();
        // create and store some features
        a1 = mManager.buildFeature().setId("chr16").setStart(1000000).setStop(1000100).build();
        a2 = mManager.buildFeature().setId("chr16").setStart(1000200).setStop(1000300).build();
        a3 = mManager.buildFeature().setId("chr16").setStart(1000400).setStop(1000500).build();
        aSet.add(a1);
        aSet.add(a2);
        aSet.add(a3);
        mManager.flush();
        //System.out.println("ending beforeClass in testID: " + testID.toString());
        bSet = diverseBSet(mManager);
        mManager.flush();
    }

    public static FeatureSet diverseBSet(CreateUpdateManager mManager) {
        // Build another test set that is a bit more diverse in its data values:
        FeatureSet set = mManager.buildFeatureSet().setReference(mManager.buildReference().setName("Diverse_Set").build()).build();
        set.add(mManager.buildFeature().setId("chr16").setStart(1000000).setStop(1000100).setStrand(Feature.Strand.POSITIVE).build());
        set.add(mManager.buildFeature().setId("chr16").setStart(1000000).setStop(1000101).setStrand(Feature.Strand.POSITIVE).build());
        set.add(mManager.buildFeature().setId("chr16").setStart(2000000).setStop(2000102).setStrand(Feature.Strand.POSITIVE).build());
        set.add(mManager.buildFeature().setId("chr16").setStart(2000000).setStop(2000101).setStrand(Feature.Strand.NEGATIVE).build());
        set.add(mManager.buildFeature().setId("chr16").setStart(2000000).setStop(2000102).setStrand(Feature.Strand.POSITIVE).build());
        set.add(mManager.buildFeature().setId("chr16").setStart(2000000).setStop(2000101).setStrand(Feature.Strand.POSITIVE).build());
        set.add(mManager.buildFeature().setId("chr16").setStart(3000000).setStop(3000102).setStrand(Feature.Strand.POSITIVE).build());
        set.add(mManager.buildFeature().setId("chr16").setStart(3000000).setStop(3000102).setStrand(Feature.Strand.POSITIVE).build());
        set.add(mManager.buildFeature().setId("chr16").setStart(3000000).setStop(3000101).setStrand(Feature.Strand.NEGATIVE).build());
        set.add(mManager.buildFeature().setId("chr16").setStart(3000000).setStop(3000102).setStrand(Feature.Strand.POSITIVE).build());
        set.add(mManager.buildFeature().setId("chr17").setStart(3000000).setStop(3000102).setStrand(Feature.Strand.POSITIVE).build());
        set.add(mManager.buildFeature().setId("chr17").setStart(3000000).setStop(3000101).setStrand(Feature.Strand.POSITIVE).build());
        set.add(mManager.buildFeature().setId("chr17").setStart(4000000).setStop(4000101).setStrand(Feature.Strand.POSITIVE).build());
        set.add(mManager.buildFeature().setId("chr17").setStart(4000000).setStop(4000101).setStrand(Feature.Strand.NEGATIVE).build());
        set.add(mManager.buildFeature().setId("chr17").setStart(4000000).setStop(4000102).setStrand(Feature.Strand.POSITIVE).build());
        set.add(mManager.buildFeature().setId("chr17").setStart(4000000).setStop(4000101).setStrand(Feature.Strand.POSITIVE).build());
        set.add(mManager.buildFeature().setId("chr17").setStart(4000000).setStop(4000102).setStrand(Feature.Strand.POSITIVE).build());
        return set;
    }

    @Test
    public void testFeatureCreationAndIterate() {
        UUID testID = UUID.randomUUID();
        //System.out.println("running base test in testID: " + testID.toString());
        // get FeatureSets from the back-end
        boolean b1 = false;
        boolean b2 = false;
        boolean b3 = false;
        for (FeatureSet fSet : SWQEFactory.getQueryInterface().getFeatureSets()) {
            for (Feature f : fSet) {
                // sadly, Features no longer will be exactly the same after a query, we need a "contents" equals
                if (f.getStart() == a1.getStart() && f.getStart() == a1.getStart()) {
                    b1 = true;
                } else if (f.getStart() == a2.getStart() && f.getStart() == a2.getStart()) {
                    b2 = true;
                } else if (f.getStart() == a3.getStart() && f.getStart() == a3.getStart()) {
                    b3 = true;
                }
            }
        }

        Assert.assertTrue(b1 && b2 && b3);
        // System.out.println("ending base test in testID: " + testID.toString());
    }
}
