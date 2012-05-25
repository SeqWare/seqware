package com.github.seqware.model.test;

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
        ReferenceSet aSet = new InMemoryReferenceSet();

        Set<Reference> testReferences = new HashSet<Reference>();

        testReferences.add(new Reference() {
            public Iterator<FeatureSet> featureSets() {
                return null;
            }
        });
        testReferences.add(new Reference() {
            public Iterator<FeatureSet> featureSets() {
                return null;
            }
        });
        testReferences.add(new Reference() {
            public Iterator<FeatureSet> featureSets() {
                return null;
            }
        });

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
}
