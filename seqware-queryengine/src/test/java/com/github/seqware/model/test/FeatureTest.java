package com.github.seqware.model.test;

import com.github.seqware.model.Feature;
import com.github.seqware.model.FeatureSet;
import com.github.seqware.model.impl.inMemory.InMemoryFeatureSet;
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
        aSet = new InMemoryFeatureSet(new Reference("testing dummy reference") {

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
}
