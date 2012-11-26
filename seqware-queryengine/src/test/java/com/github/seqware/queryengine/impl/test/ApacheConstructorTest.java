package com.github.seqware.queryengine.impl.test;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.impl.SimplePersistentBackEnd;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import java.util.UUID;
import org.junit.Test;

/**
 * Tests specifically using Apache serialization.
 *
 * @author dyuen
 * @version $Id: $Id
 * @since 0.13.3
 */
public class ApacheConstructorTest  {

    private static UUID testID;
    private static FeatureSet aSet;
    private static Feature a1, a2, a3;
    private static SimplePersistentBackEnd backend;
    
    /**
     * <p>Constructor for ApacheConstructorTest.</p>
     */
    public ApacheConstructorTest() {
        ApacheConstructorTest.testID = UUID.randomUUID();
        SWQEFactory.setFactoryBackendType(SWQEFactory.Model_Type.IN_MEMORY, SWQEFactory.Storage_Type.FILE_STORAGE, SWQEFactory.Serialization_Type.APACHE);
        backend = (SimplePersistentBackEnd) SWQEFactory.getQueryInterface();
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        aSet = mManager.buildFeatureSet().setReference(mManager.buildReference().setName("Dummy_ref").build()).build();
        // create and store some features
        a1 = mManager.buildFeature().setSeqid("chr16").setStart(1000000).setStop(1000100).build();
        a2 = mManager.buildFeature().setSeqid("chr16").setStart(1000200).setStop(1000300).build();
        a3 = mManager.buildFeature().setSeqid("chr16").setStart(1000400).setStop(1000500).build();
        aSet.add(a1);
        aSet.add(a2);
        aSet.add(a3);
        mManager.flush();
        //System.out.println("ending static init in testID: " + testID.toString());
    }

    /**
     * <p>testFeatureCreationAndIterate.</p>
     */
    @Test
    public void testFeatureCreationAndIterate() {
        //System.out.println("running base test in testID: " + testID.toString());
        // get FeatureSets from the back-end
        boolean b1 = false;
        boolean b2 = false;
        boolean b3 = false;
        for (FeatureSet fSet : backend.getFeatureSets()) {
            for (Feature f : fSet) {
                if (f.equals(a1)) {
                    b1 = true;
                } else if (f.equals(a2)) {
                    b2 = true;
                } else if (f.equals(a3)) {
                    b3 = true;
                }
            }
        }

        org.junit.Assert.assertTrue(b1 && b2 && b3);
        //System.out.println("ending base test in testID: " + testID.toString());
    }
}
