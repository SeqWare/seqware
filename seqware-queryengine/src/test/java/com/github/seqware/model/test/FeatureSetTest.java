package com.github.seqware.model.test;

import com.github.seqware.factory.Factory;
import com.github.seqware.factory.ModelManager;
import com.github.seqware.model.Feature;
import com.github.seqware.model.FeatureSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests of FeatureSet.
 *
 * @author jbaran
 * @author dyuen
 */
public class FeatureSetTest {

    @Test
    public void testConsistentStorageSingleFeatures() {
        ModelManager mManager = Factory.getModelManager();
        FeatureSet aSet = mManager.buildFeatureSet().setReference(mManager.buildReference().setName("Dummy ref").build()).build();
        Set<Feature> testFeatures = new HashSet<Feature>();
        testFeatures.add(mManager.buildFeature().setStart(1000000).setStop(1000100).build());
        testFeatures.add(mManager.buildFeature().setStart(1000200).setStop(1000300).build());
        testFeatures.add(mManager.buildFeature().setStart(1000400).setStop(1000500).build());

        for (Feature testFeature : testFeatures){
            aSet.add(testFeature);
        }

        // NOTE Misses test case where all added features are being dropped and nothing is stored.
        for (Iterator<Feature> i = aSet.getFeatures(); i.hasNext();) {
            Feature resultFeature = i.next();

            Assert.assertTrue("Seeing a feature that is either not in the original test set, or is being returned more than once.", testFeatures.contains(resultFeature));

            testFeatures.remove(resultFeature);
        }

        Assert.assertTrue("Feature set did not return all of the features that had been stored previously.", testFeatures.isEmpty());
    }
    
    @Test
    public void testVersioningAndFeatureSets(){
        ModelManager mManager = Factory.getModelManager();
        FeatureSet aSet = mManager.buildFeatureSet().setReference(mManager.buildReference().setName("Dummy ref").build()).build();
        mManager.flush(); // this should persist a version with no features
        aSet.add(mManager.buildFeature().setStart(1000000).setStop(1000100).build());
        aSet.add(mManager.buildFeature().setStart(1000200).setStop(1000300).build());
        aSet.add(mManager.buildFeature().setStart(1000400).setStop(1000500).build());
        mManager.flush(); // this should persist a version with three features
        aSet.add(mManager.buildFeature().setStart(1000600).setStop(1000610).build());
        aSet.add(mManager.buildFeature().setStart(1000700).setStop(1000710).build());
        aSet.add(mManager.buildFeature().setStart(1000800).setStop(1000810).build());
 
        mManager.flush(); // this should persist a version with six features
               
        FeatureSet testSet = (FeatureSet) Factory.getFeatureStoreInterface().getParticleByUUID(aSet.getUUID());
        Assert.assertTrue("FeatureSet version wrong", testSet.getVersion() == 3);
        Assert.assertTrue("old FeatureSet version wrong", testSet.getPrecedingVersion().getVersion() == 2);
        Assert.assertTrue("very old FeatureSet version wrong", testSet.getPrecedingVersion().getPrecedingVersion().getVersion() == 1);
        Assert.assertTrue("FeatureSet size wrong", testSet.getCount() == 6);
        Assert.assertTrue("old FeatureSet size wrong", testSet.getPrecedingVersion().getCount() == 3);
        Assert.assertTrue("very old FeatureSet size wrong", testSet.getPrecedingVersion().getPrecedingVersion().getCount() == 0);
        // assert the same properties with the one in memory already
        Assert.assertTrue("referenceSet version wrong", aSet.getVersion() == 3);
        Assert.assertTrue("old referenceSet version wrong", aSet.getPrecedingVersion().getVersion() == 2);
        Assert.assertTrue("very old referenceSet version wrong", aSet.getPrecedingVersion().getPrecedingVersion().getVersion() == 1);
        Assert.assertTrue("referenceSet size wrong", aSet.getCount() == 6);
        Assert.assertTrue("old referenceSet size wrong", aSet.getPrecedingVersion().getCount() == 3);
        Assert.assertTrue("very old referenceSet size wrong", aSet.getPrecedingVersion().getPrecedingVersion().getCount() == 0);
    }
}
