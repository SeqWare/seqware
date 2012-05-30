package com.github.seqware.model.test;

import com.github.seqware.factory.Factory;
import com.github.seqware.model.impl.inMemory.InMemoryReferenceSet;
import com.github.seqware.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Unit tests of ReferenceSet.
 *
 * @author jbaran
 */
public class ReferenceSetTest {

    @Test
    public void testConsistentStorageSingleFeatures() {
        ReferenceSet aSet = Factory.buildReferenceSet("Human", "Homo Sapiens");

        Set<Reference> testReferences = new HashSet<Reference>();

        testReferences.add(Factory.buildReference("Dummy reference1"));
        testReferences.add(Factory.buildReference("Dummy reference2"));
        testReferences.add(Factory.buildReference("Dummy reference3"));

        for (Reference testReference : testReferences)
            aSet.add(testReference);

        // NOTE Misses test case where all added features are being dropped and nothing is stored.
        for (Iterator<Reference> i = aSet.getReferences(); i.hasNext();) {
            Reference resultReference = i.next();

            Assert.assertTrue("Seeing a reference that is either not in the original test set, or is being returned more than once.", testReferences.contains(resultReference));

            testReferences.remove(resultReference);
        }

        Assert.assertTrue("Reference set did not return all of the references that had been stored previously.", testReferences.isEmpty());
    }
    
    @Test
    public void testVersioningAndFeatureSets(){
        ReferenceSet aSet = Factory.buildReferenceSet("Dummy name", "Dummy organism");
        Assert.assertTrue("versions should start with version 1", aSet.getVersion() == 1);
        aSet.store(); // this should persist a version with no features
        aSet.add(Factory.buildReference("t1"));
        aSet.update(); // this should persist a version with 1 references
        aSet.add(Factory.buildReference("t2"));
        aSet.update(); // this should persist a version with 2 references
        
        ReferenceSet testSet = (ReferenceSet) Factory.getFeatureStoreInterface().getParticleByUUID(aSet.getUUID());
        Assert.assertTrue("referenceSet size wrong", testSet.getVersion() == 3);
        Assert.assertTrue("old referenceSet size wrong", testSet.getPrecedingVersion().getVersion() == 2);
        Assert.assertTrue("very old referenceSet size wrong", testSet.getPrecedingVersion().getPrecedingVersion().getVersion() == 1);
        Assert.assertTrue("referenceSet size wrong", testSet.getCount() == 2);
        Assert.assertTrue("old referenceSet size wrong", testSet.getPrecedingVersion().getCount() == 1);
        Assert.assertTrue("very old referenceSet size wrong", testSet.getPrecedingVersion().getPrecedingVersion().getCount() == 0);
        // assert the same properties with the one in memory already
        Assert.assertTrue("referenceSet size wrong", aSet.getVersion() == 3);
        Assert.assertTrue("old referenceSet size wrong", aSet.getPrecedingVersion().getVersion() == 2);
        Assert.assertTrue("very old referenceSet size wrong", aSet.getPrecedingVersion().getPrecedingVersion().getVersion() == 1);
        Assert.assertTrue("referenceSet size wrong", aSet.getCount() == 2);
        Assert.assertTrue("old referenceSet size wrong", aSet.getPrecedingVersion().getCount() == 1);
        Assert.assertTrue("very old referenceSet size wrong", aSet.getPrecedingVersion().getPrecedingVersion().getCount() == 0);
    }
}
