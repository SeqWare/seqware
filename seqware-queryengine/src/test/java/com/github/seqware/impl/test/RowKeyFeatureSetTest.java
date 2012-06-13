package com.github.seqware.impl.test;

import com.github.seqware.model.test.*;
import com.github.seqware.factory.Factory;
import com.github.seqware.factory.ModelManager;
import com.github.seqware.model.Feature;
import com.github.seqware.model.FeatureSet;
import java.util.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * Let's test RowKey creation (not working yet)
 *
 * @author dyuen
 */
public class RowKeyFeatureSetTest {

    @Test
    public void testRow() {
        ModelManager mManager = Factory.getModelManager();
        FeatureSet aSet = mManager.buildFeatureSet().setReference(mManager.buildReference().setName("Dummy ref").build()).build();
        Set<Feature> testFeatures = new HashSet<Feature>();
        testFeatures.add(mManager.buildFeature().setId("chr16").setStart(1000000).setStop(1000100).build());
        testFeatures.add(mManager.buildFeature().setId("chr16").setStart(1000200).setStop(1000300).build());
        testFeatures.add(mManager.buildFeature().setId("chr16").setStart(1000400).setStop(1000500).build());

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
}
