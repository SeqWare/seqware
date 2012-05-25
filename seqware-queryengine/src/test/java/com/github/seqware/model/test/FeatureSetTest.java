package com.github.seqware.model.test;

import com.github.seqware.model.impl.inMemory.InMemoryFeatureSet;
import com.github.seqware.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Unit tests of FeatureSet.
 *
 * @author jbaran
 */
public class FeatureSetTest {

    @Test
    public void testConsistentStorageSingleFeatures() {
        FeatureSet aSet = new InMemoryFeatureSet(new Reference() {
            @Override
            public Iterator<FeatureSet> featureSets() {
                return null;
            }
        });

        Set<Feature> testFeatures = new HashSet<Feature>();

        testFeatures.add(new Feature(aSet, 1000000, 1000100));
        testFeatures.add(new Feature(aSet, 1000200, 1000300));
        testFeatures.add(new Feature(aSet, 1000400, 1000500));

        for (Feature testFeature : testFeatures)
            aSet.add(testFeature);

        // NOTE Misses test case where all added features are being dropped and nothing is stored.
        for (Iterator<Feature> i = aSet.getFeatures(); i.hasNext();) {
            Feature resultFeature = i.next();

            Assert.assertTrue("Seeing a feature that is either not in the original test set, or is being returned more than once.", testFeatures.contains(resultFeature));

            testFeatures.remove(resultFeature);
        }

        Assert.assertTrue("Feature set did not return all of the features that had been stored previously.", testFeatures.isEmpty());
    }
}
