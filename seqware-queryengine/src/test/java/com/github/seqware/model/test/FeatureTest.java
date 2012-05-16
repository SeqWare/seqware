package com.github.seqware.model.test;

import com.github.seqware.model.Feature;

import org.junit.Assert;
import org.junit.Test;

public class FeatureTest {

    @Test
    public void testUUIDGenerationNonStrandedFeature() {
        Assert.assertNotNull("Feature UUID is null, which means that no UUID was generated for the feature.", new Feature("GRCh37", 1000000, 1000100).getUUID());
    }

    @Test
    public void testUUIDGenerationStrandedFeature() {
        Assert.assertNotNull("Feature UUID is null, which means that no UUID was generated for the feature.", new Feature("GRCh37", 1000000, 1000100, Feature.Strand.POSITIVE).getUUID());
    }
}
