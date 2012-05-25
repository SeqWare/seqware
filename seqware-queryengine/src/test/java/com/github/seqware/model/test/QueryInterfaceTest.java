package com.github.seqware.model.test;

import com.github.seqware.factory.Factory;
import com.github.seqware.model.Feature;
import com.github.seqware.model.FeatureSet;
import com.github.seqware.model.QueryFuture;
import com.github.seqware.model.Reference;
import com.github.seqware.model.impl.inMemory.InMemoryFeatureSet;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests of QueryInterface.
 *
 * @author dyuen
 */
public class QueryInterfaceTest {

    private static InMemoryFeatureSet aSet;
    private static Feature a1, a2, a3, a4;

    @BeforeClass
    public static void setupTests() {
        aSet = new InMemoryFeatureSet(new Reference("testing dummy reference") {
            @Override
            public Iterator<FeatureSet> featureSets() {
                return null;
            }
        });
        // create and store some features
        a1 = new Feature(aSet, 1000000, 1000100, Feature.Strand.NEGATIVE, "type1", 100.0, "Program A", "pragma", ".");
        a2 = new Feature(aSet, 1000001, 1000101, Feature.Strand.POSITIVE, "type2", 80.0, "Program A", "pragma", ".");
        a3 = new Feature(aSet, 1000002, 1000102, Feature.Strand.NOT_STRANDED, "type2", 80.0, "Program B", "pragma", ".");
        a4 = new Feature(aSet, 1000003, 1000103, Feature.Strand.UNKNOWN, "type3", 50.0, "Program B", "pragma", ".");
        a1.add();
        a2.add();
        a3.add();
        a4.add();
        aSet.add(a1);
        aSet.add(a2);
        aSet.add(a3);
        aSet.add(a4);
    }

    @Test
    public void testFeatureCreationAndIterate() {
        // get a FeatureSet from the back-end
        QueryFuture future = Factory.getQueryInterface().getFeatures(aSet, 0);
        // check that Features are present match
        FeatureSet result = future.get();
        boolean b1 = false;
        boolean b2 = false;
        boolean b3 = false;
        for (Feature f : result) {
            if (f.equals(a1)) {
                b1 = true;
            } else if (f.equals(a2)) {
                b2 = true;
            } else if (f.equals(a3)) {
                b3 = true;
            }
        }
        Assert.assertTrue(b1 && b2 && b3);
    }
    
    @Test
    public void testTypeQuery() {
        // get a FeatureSet from the back-end
        QueryFuture future = Factory.getQueryInterface().getFeaturesByType(aSet, "type1", 0);
        // check that Features are present match
        FeatureSet result = future.get();
        int sum = 0;
        for (Feature f : result) {
            sum++;
            Assert.assertTrue(f.getType().equals("type1"));
        }
        Assert.assertTrue(sum == 1);
    }
}
