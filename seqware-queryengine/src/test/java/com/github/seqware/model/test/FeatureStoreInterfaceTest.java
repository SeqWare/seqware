package com.github.seqware.model.test;

import com.github.seqware.factory.Factory;
import com.github.seqware.model.Feature;
import com.github.seqware.model.FeatureSet;
import com.github.seqware.model.impl.inMemory.InMemoryFeatureSet;
import com.github.seqware.model.impl.inMemory.InMemoryReference;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests of BackEndInterface.
 *
 * @author dyuen
 */
public class FeatureStoreInterfaceTest {


    @Test
    public void testFeatureCreationAndIterate() {
        InMemoryFeatureSet aSet = new InMemoryFeatureSet(new InMemoryReference());
        aSet.add();
        // create and store some features
        Feature a1 = new Feature(aSet, 1000000, 1000100);
        Feature a2 = new Feature(aSet, 1000001, 1000101);
        Feature a3 = new Feature(aSet, 1000002, 1000102);
        aSet.add(a1);         
        aSet.add(a2);         
        aSet.add(a3);
        a1.add();
        a2.add();
        a3.add();
        aSet.update();
        // get FeatureSets from the back-end
        boolean b1 = false;
        boolean b2 = false;
        boolean b3 = false;
        for (FeatureSet fSet : Factory.getFeatureStoreInterface().getFeatureSets()) {
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

        Assert.assertTrue(b1 && b2 && b3);
    }
}
