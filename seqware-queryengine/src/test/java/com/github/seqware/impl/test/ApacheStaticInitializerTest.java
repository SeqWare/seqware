package com.github.seqware.impl.test;

import com.github.seqware.queryengine.impl.ApacheSerialization;
import com.github.seqware.queryengine.impl.SimpleModelManager;
import com.github.seqware.queryengine.impl.SimplePersistentBackEnd;
import com.github.seqware.queryengine.impl.TmpFileStorage;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import java.util.UUID;
import org.junit.Test;

/**
 * Tests with a static initializer block using ApacheSerialization.
 *
 * @author dyuen
 */
public class ApacheStaticInitializerTest {
    
     private static UUID testID;
     private static FeatureSet aSet;
     private static Feature a1, a2, a3;
     private static SimplePersistentBackEnd backend;

     static {
        ApacheStaticInitializerTest.testID = UUID.randomUUID();
        //System.out.println("starting static init in testID: " + testID.toString());
        backend = new SimplePersistentBackEnd(new TmpFileStorage(new ApacheSerialization()));
        SimpleModelManager mManager = new SimpleModelManager();
        mManager.overrideBackEnd(backend);
        aSet = mManager.buildFeatureSet().setReference(mManager.buildReference().setName("Dummy_ref").build()).build();
        // create and store some features
        a1 = mManager.buildFeature().setId("chr16").setStart(1000000).setStop(1000100).build();
        a2 = mManager.buildFeature().setId("chr16").setStart(1000200).setStop(1000300).build();
        a3 = mManager.buildFeature().setId("chr16").setStart(1000400).setStop(1000500).build();
        aSet.add(a1);
        aSet.add(a2);
        aSet.add(a3);
        mManager.flush();
        //System.out.println("ending static init in testID: " + testID.toString());
    }
    
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
