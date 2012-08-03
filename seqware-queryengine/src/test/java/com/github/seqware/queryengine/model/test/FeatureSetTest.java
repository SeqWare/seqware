package com.github.seqware.queryengine.model.test;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests of {@link FeatureSet}.
 *
 * @author jbaran
 * @author dyuen
 */
public class FeatureSetTest {

    @Test
    public void testConsistentStorageSingleFeatures() {
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        FeatureSet aSet = mManager.buildFeatureSet().setReference(mManager.buildReference().setName("Dummy_ref").build()).build();
        // this is sort of painful, but you cannot assume that a Feature doesn't change when you add it to sets
        //Set<Feature> testFeatures = new HashSet<Feature>();
        List<Feature> testFeatures = new ArrayList<Feature>();
        testFeatures.add(mManager.buildFeature().setSeqid("chr16").setStart(1000000).setStop(1000100).build());
        testFeatures.add(mManager.buildFeature().setSeqid("chr16").setStart(1000200).setStop(1000300).build());
        testFeatures.add(mManager.buildFeature().setSeqid("chr16").setStart(1000400).setStop(1000500).build());

        for (Feature testFeature : testFeatures) {
            aSet.add(testFeature);
        }

        // need to flush FeatureSets first now
        mManager.flush();


        // NOTE Misses test case where all added features are being dropped and nothing is stored.
        for (Iterator<Feature> i = aSet.getFeatures(); i.hasNext();) {
            Feature resultFeature = i.next();

            Assert.assertTrue("Seeing a feature that is either not in the original test set, or is being returned more than once.", testFeatures.contains(resultFeature));

            testFeatures.remove(resultFeature);
        }

        Assert.assertTrue("Feature set did not return all of the features that had been stored previously.", testFeatures.isEmpty());
    }

    @Test
    public void testVersioningAndFeatureSets() {
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        FeatureSet aSet = mManager.buildFeatureSet().setReference(mManager.buildReference().setName("Dummy_ref").build()).build();

        mManager.flush(); // this should persist a version with no features
        aSet.add(mManager.buildFeature().setSeqid("chrX").setStart(1000000).setStop(1000100).build());
        aSet.add(mManager.buildFeature().setSeqid("chrX").setStart(1000200).setStop(1000300).build());
        aSet.add(mManager.buildFeature().setSeqid("chrX").setStart(1000400).setStop(1000500).build());
        mManager.flush(); // this should persist a version with three features
        aSet.add(mManager.buildFeature().setSeqid("chrX").setStart(1000600).setStop(1000610).build());
        aSet.add(mManager.buildFeature().setSeqid("chrX").setStart(1000700).setStop(1000710).build());
        aSet.add(mManager.buildFeature().setSeqid("chrX").setStart(1000800).setStop(1000810).build());
        mManager.flush(); // this should persist a version with six features
        List<Feature> killList = new ArrayList<Feature>();
        for (Feature f : aSet) {
            killList.add(f);
        }
        //TODO: kind of awkward, but there might be a reason iterator remove() support was removed in the prototype
        for (Feature f : killList) {
            aSet.remove(f);
        }
        mManager.flush(); // kill all the features

        FeatureSet testSet = SWQEFactory.getQueryInterface().getAtomBySGID(FeatureSet.class, aSet.getSGID());
        Assert.assertTrue("FeatureSet version wrong, expected 4 and found " + testSet.getVersion(), testSet.getVersion() == 4);
        Assert.assertTrue("old FeatureSet version wrong", testSet.getPrecedingVersion().getVersion() == 3);
        Assert.assertTrue("very old FeatureSet version wrong", testSet.getPrecedingVersion().getPrecedingVersion().getVersion() == 2);
        Assert.assertTrue("first FeatureSet version wrong", testSet.getPrecedingVersion().getPrecedingVersion().getPrecedingVersion().getVersion() == 1);
        Assert.assertTrue("FeatureSet size wrong, expected 0 and found " + testSet.getCount(), testSet.getCount() == 0);
        Assert.assertTrue("old FeatureSet size wrong and found " + testSet.getPrecedingVersion().getCount(), testSet.getPrecedingVersion().getCount() == 6);
        Assert.assertTrue("very old FeatureSet size wrong", testSet.getPrecedingVersion().getPrecedingVersion().getCount() == 3);
        Assert.assertTrue("first FeatureSet size wrong", testSet.getPrecedingVersion().getPrecedingVersion().getPrecedingVersion().getCount() == 0);
        // assert the same properties with the one in memory already
        Assert.assertTrue("FeatureSet version wrong", aSet.getVersion() == 4);
        Assert.assertTrue("old FeatureSet version wrong", aSet.getPrecedingVersion().getVersion() == 3);
        Assert.assertTrue("very old FeatureSet version wrong", aSet.getPrecedingVersion().getPrecedingVersion().getVersion() == 2);
        Assert.assertTrue("first FeatureSet version wrong", aSet.getPrecedingVersion().getPrecedingVersion().getPrecedingVersion().getVersion() == 1);
        Assert.assertTrue("FeatureSet size wrong", aSet.getCount() == 0);
        Assert.assertTrue("old FeatureSet size wrong", aSet.getPrecedingVersion().getCount() == 6);
        Assert.assertTrue("very old FeatureSet size wrong", aSet.getPrecedingVersion().getPrecedingVersion().getCount() == 3);
        Assert.assertTrue("first FeatureSet size wrong", aSet.getPrecedingVersion().getPrecedingVersion().getPrecedingVersion().getCount() == 0);
    }
    
    @Test
    public void testMultipleFeaturesSameLocation(){
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        FeatureSet aSet = mManager.buildFeatureSet().setReference(mManager.buildReference().setName("Dummy_ref").build()).build();
        mManager.flush(); // this should persist a version with no features
        
        Feature[] arr = new Feature[]{mManager.buildFeature().setSeqid("chrX").setStart(1000000).setStop(1000100).build(),
        mManager.buildFeature().setSeqid("chrX").setStart(1000000).setStop(1000100).build(), 
        mManager.buildFeature().setSeqid("chrX").setStart(1000000).setStop(1000100).build()};
        aSet.add(arr);
        
        // start with zero features before a flush
        Assert.assertTrue("FeatureSet size wrong, expected 0 and found " + aSet.getCount(), aSet.getCount() == 0);
        mManager.flush();
        // expect 3 features after a flush
        Assert.assertTrue("FeatureSet size wrong, expected 3 and found " + aSet.getCount(), aSet.getCount() == 3);
        Assert.assertTrue("preceding FeatureSet size wrong, expected 0 and found " + aSet.getPrecedingVersion().getCount(), aSet.getPrecedingVersion().getCount() == 0);
        // try to delete features
        for(Feature f : arr){
            aSet.remove(f);
        }
        mManager.flush();
        Assert.assertTrue("FeatureSet size wrong, expected 0 and found " + aSet.getCount(), aSet.getCount() == 0);
        // add them back
        aSet.add(arr);
        mManager.flush();
        Assert.assertTrue("FeatureSet size wrong, expected 3 and found " + aSet.getCount(), aSet.getCount() == 3);
    }
    
    @Test 
    public void testDetachedOperations(){
        //unlike other sets, lazy FeatureSets should also allow you to attach even when the set itself is unmanaged
        // this is very dangerous though, you need to make sure that you do not write to the same row between two different operations
        // since there is no manager active
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        FeatureSet aSet = mManager.buildFeatureSet().setReference(mManager.buildReference().setName("Dummy_ref").build()).build();
        Feature[] arr = new Feature[]{mManager.buildFeature().setSeqid("chrX").setStart(1000000).setStop(1000100).build(),
        mManager.buildFeature().setSeqid("chrX").setStart(1000000).setStop(1000100).build(), 
        mManager.buildFeature().setSeqid("chrX").setStart(1000000).setStop(1000100).build()};
        aSet.add(arr);
        
        mManager.flush();
        Assert.assertTrue("FeatureSet size wrong, expected 3 and found " + aSet.getCount(), aSet.getCount() == 3);
        // clear manager to release memory
        mManager.clear();
        // aSet is no longer managed, but can be added to by reference (without changing its version)
        
        Feature[] arr2 = new Feature[]{mManager.buildFeature().setSeqid("chrX").setStart(1000001).setStop(1000100).build(),
        mManager.buildFeature().setSeqid("chrX").setStart(1000001).setStop(1000100).build(), 
        mManager.buildFeature().setSeqid("chrX").setStart(1000001).setStop(1000100).build()};
        aSet.add(arr2);
        mManager.flush();
        Assert.assertTrue("FeatureSet size wrong, expected 6 and found " + aSet.getCount(), aSet.getCount() == 6);
        
        Feature[] arr3 = new Feature[]{mManager.buildFeature().setSeqid("chrX").setStart(1000000).setStop(1000100).build(),
        mManager.buildFeature().setSeqid("chrX").setStart(1000000).setStop(1000100).build(), 
        mManager.buildFeature().setSeqid("chrX").setStart(1000000).setStop(1000100).build()};
        aSet.add(arr3);
        mManager.flush();
        Assert.assertTrue("FeatureSet size wrong, expected 9 and found " + aSet.getCount(), aSet.getCount() == 9);
        // should be the same size when we go through the query interface
        FeatureSet testSet = SWQEFactory.getQueryInterface().getAtomBySGID(FeatureSet.class, aSet.getSGID());
        Assert.assertTrue("FeatureSet size wrong, expected 9 and found " + testSet.getCount(), aSet.getCount() == 9);
        mManager.close();
    }
}
