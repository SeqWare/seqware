package com.github.seqware.model.test;

import com.github.seqware.factory.Factory;
import com.github.seqware.model.Feature;
import com.github.seqware.model.FeatureSet;
import com.github.seqware.model.InMemoryFeatureSet;
import com.github.seqware.model.QueryInterface.QueryFuture;
import com.github.seqware.model.Reference;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests of Feature.
 *
 * @author jbaran
 */
public class FeatureTest {

    private static InMemoryFeatureSet aSet;

    @BeforeClass
    public static void setupTests() {
        aSet = new InMemoryFeatureSet(new Reference() {

            @Override
            public Iterator<FeatureSet> featureSets() {
                return null;
            }
        });
    }

    @Test
    public void testUUIDGenerationNonStrandedFeature() {
        Assert.assertNotNull("Feature UUID is null, which means that no UUID was generated for the feature.", new Feature(aSet, 1000000, 1000100).getUUID());
    }

    @Test
    public void testUUIDGenerationStrandedFeature() {
        Assert.assertNotNull("Feature UUID is null, which means that no UUID was generated for the feature.", new Feature(aSet, 1000000, 1000100, Feature.Strand.POSITIVE).getUUID());
    }

    @Test
    public void testFeatureCreationAndIterate() {
        // create and store some features
        Feature a1 = new Feature(aSet, 1000000, 1000100);
        Feature a2 = new Feature(aSet, 1000001, 1000101);
        Feature a3 = new Feature(aSet, 1000002, 1000102);
        a1.add();
        a2.add();
        a3.add();
        // get a FeatureSet from the back-end
        QueryFuture future = Factory.getQueryInterface().getFeatures(aSet, 0);
        // check that Features are present match
        FeatureSet result = future.get();
        boolean b1 = false;
        boolean b2 = false;
        boolean b3 = false;
        for (Feature f : result) {
            if (f.equals(a1)) {
                b1 = true;
            } else if (f.equals(a2)) {
                b2 = true;
            } else if (f.equals(a3)) {
                b3 = true;
            }
        }
        Assert.assertTrue(b1 && b2 && b3);
    }
}
