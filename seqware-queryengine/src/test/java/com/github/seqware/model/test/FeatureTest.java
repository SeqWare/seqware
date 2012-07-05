package com.github.seqware.model.test;

import com.github.seqware.queryengine.factory.Factory;
import com.github.seqware.queryengine.factory.ModelManager;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests of Feature.
 *
 * @author jbaran
 */
public class FeatureTest {

    private static FeatureSet aSet;

    @BeforeClass
    public static void setupTests() {
        ModelManager mManager = Factory.getModelManager();
        aSet = mManager.buildFeatureSet().setReference(mManager.buildReference().setName("Dummy ref").build()).build();
    }

    @Test
    public void testUUIDGenerationNonStrandedFeature() {
        ModelManager mManager = Factory.getModelManager();
        Assert.assertNotNull("Feature UUID is null, which means that no UUID was generated for the feature.", mManager.buildFeature().setId("chr16").setStart(1000000).setStop(1000100).build().getSGID());
    }

    @Test
    public void testUUIDGenerationStrandedFeature() {
        ModelManager mManager = Factory.getModelManager();
        Assert.assertNotNull("Feature UUID is null, which means that no UUID was generated for the feature.", mManager.buildFeature().setId("chr16").setStart(1000000).setStop(1000100).setStrand(Feature.Strand.POSITIVE).build().getSGID());
    }
}
