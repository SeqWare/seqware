package com.github.seqware.impl.test;

import com.github.seqware.factory.Factory;
import com.github.seqware.factory.ModelManager;
import com.github.seqware.impl.ApacheUtilsPersistentSerialization;
import com.github.seqware.impl.SimplePersistentBackEnd;
import com.github.seqware.model.Feature;
import com.github.seqware.model.FeatureSet;
import com.github.seqware.model.Particle;
import com.github.seqware.model.test.FeatureStoreInterfaceTest;
import java.util.UUID;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Tests for the {@link SimplePersistentBackEnd}.
 *
 * @author jbaran
 */
public class SimplePersistentBackEndTest extends FeatureStoreInterfaceTest {
    @Test
    public void storageAndRetrievalTest() {
        UUID testID = UUID.randomUUID();
        System.out.println("running subclass test in testID: " + testID.toString());
        
        SimplePersistentBackEnd backend = new SimplePersistentBackEnd(new ApacheUtilsPersistentSerialization());

        try {
            backend.store(this.aSet);
        }
        catch(Exception e) {
            Assert.assertTrue("Backend could not store the given FeatureSet.", false);
        }

        Particle particle = null;
        try {
            particle = backend.getParticleBySGID(this.aSet.getSGID());
        }
        catch(Exception e) {
            Assert.assertTrue("Could not retrieve the previously stored FeatureSet.", false);
        }

        if (!(particle instanceof FeatureSet))
            Assert.assertTrue("Returned result does not match the previously stored object by type.", false);

        FeatureSet returnedSet = (FeatureSet)particle;

        Assert.assertTrue("Returned feature set does not contain the same amount of features as were stored.", this.aSet.getCount() == returnedSet.getCount());
        System.out.println("ending subclass test in testID: " + testID.toString());
    }
}
