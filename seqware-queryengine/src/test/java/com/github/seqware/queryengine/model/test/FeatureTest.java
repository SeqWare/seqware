package com.github.seqware.queryengine.model.test;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests of {@link com.github.seqware.queryengine.model.Feature}.
 *
 * @author jbaran
 * @version $Id: $Id
 * @since 0.13.3
 */
public class FeatureTest {

    private static FeatureSet aSet;

    /**
     * <p>setupTests.</p>
     */
    @BeforeClass
    public static void setupTests() {
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        aSet = mManager.buildFeatureSet().setReference(mManager.buildReference().setName("Dummy_ref").build()).build();
    }

    /**
     * <p>testUUIDGenerationNonStrandedFeature.</p>
     */
    @Test
    public void testUUIDGenerationNonStrandedFeature() {
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        Assert.assertNotNull("Feature UUID is null, which means that no UUID was generated for the feature.", mManager.buildFeature().setSeqid("chr16").setStart(1000000).setStop(1000100).build().getSGID());
    }

    /**
     * <p>testUUIDGenerationStrandedFeature.</p>
     */
    @Test
    public void testUUIDGenerationStrandedFeature() {
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        Assert.assertNotNull("Feature UUID is null, which means that no UUID was generated for the feature.", mManager.buildFeature().setSeqid("chr16").setStart(1000000).setStop(1000100).setStrand(Feature.Strand.POSITIVE).build().getSGID());
    }
    
    /**
     * <p>testSerializationExposed.</p>
     */
    @Test 
    public void testSerializationExposed(){
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        aSet = mManager.buildFeatureSet().setReference(mManager.buildReference().setName("Dummy_ref").build()).build();
        Feature f = mManager.buildFeature().setSeqid("chrom1").setStart(0).setStop(1).build();
        aSet.add(f);
        mManager.flush();
        // the version of a feature we just flushed should match what the factory thinks it is producing
        Feature f1 = SWQEFactory.getQueryInterface().getAtomBySGID(Feature.class, f.getSGID());
        Assert.assertTrue(f1.getExternalSerializationVersion() == SWQEFactory.getSerialization().getSerializationConstant());
        // same with FeatureSet
        FeatureSet s1 = SWQEFactory.getQueryInterface().getAtomBySGID(FeatureSet.class, aSet.getSGID());
        Assert.assertTrue(s1.getExternalSerializationVersion() == SWQEFactory.getSerialization().getSerializationConstant());
    }
}
