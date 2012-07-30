package com.github.seqware.model.test;

import com.github.seqware.Benchmarking;
import com.github.seqware.impl.test.SimplePersistentBackEndTest;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.kernel.RPNStack;
import com.github.seqware.queryengine.kernel.RPNStack.Constant;
import com.github.seqware.queryengine.kernel.RPNStack.Operation;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.QueryFuture;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.seqware.queryengine.model.QueryInterface;
import org.apache.hadoop.hbase.client.Row;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests of {@link com.github.seqware.queryengine.model.QueryInterface}
 *
 * @author dyuen
 * @author jbaran
 */
public class QueryInterfaceTest implements Benchmarking {

    private static FeatureSet aSet, bSet, benchmarkSet;
    private static Feature a1, a2, a3, a4;

    @BeforeClass
    public static void setupTests() {
        CreateUpdateManager mManager = SWQEFactory.getModelManager();

        aSet = mManager.buildFeatureSet().setReference(mManager.buildReference().setName("Dummy_ref").build()).build();
        // create and store some features
        a1 = mManager.buildFeature().setSeqid("chr16").setStart(1000000).setStop(1000100).setStrand(Feature.Strand.NEGATIVE).setType("type1").setScore(100.0).setSource("Program A").setPragma("pragma").setPhase(".").build();
        a2 = mManager.buildFeature().setSeqid("chr16").setStart(1000001).setStop(1000101).setStrand(Feature.Strand.POSITIVE).setType("type2").setScore(80.0).setSource("Program A").setPragma("pragma").setPhase(".").build();
        a3 = mManager.buildFeature().setSeqid("chr16").setStart(1000002).setStop(1000102).setStrand(Feature.Strand.NOT_STRANDED).setType("type2").setScore(80.0).setSource("Program B").setPragma("pragma").setPhase(".").build();
        a4 = mManager.buildFeature().setSeqid("chr16").setStart(1000003).setStop(1000103).setStrand(Feature.Strand.UNKNOWN).setType("type3").setScore(50.0).setSource("Program B").setPragma("pragma").setPhase(".").build();
        aSet.add(a1);
        aSet.add(a2);
        aSet.add(a3);
        aSet.add(a4);

        bSet = FeatureStoreInterfaceTest.diverseBSet(mManager);

        if (BENCHMARK)
            benchmarkSet = FeatureStoreInterfaceTest.largeTestSet(mManager, BENCHMARK_FEATURES);

        //TODO: this test was somewhat invalid, no flush ... causes error ... we may want a new test case with a nice clean error message
        mManager.flush();
    }

    @Test
    public void testGetFeatures() {
        // get a FeatureSet from the back-end
        QueryFuture future = SWQEFactory.getQueryInterface().getFeatures(0, aSet);

        // check that Features are present match
        FeatureSet result = future.get();

        boolean b1 = false;
        boolean b2 = false;
        boolean b3 = false;
        for (Feature f : result) {
            // sadly, Features no longer will be exactly the same after a query, queries generate new features
            // in new FeatureSets
            if (f.getStart() == a1.getStart() && f.getStop() == a1.getStop()) {
                b1 = true;
            } else if (f.getStart() == a2.getStart() && f.getStop() == a2.getStop()) {
                b2 = true;
            } else if (f.getStart() == a3.getStart() && f.getStop() == a3.getStop()) {
                b3 = true;
            }
        }
        Assert.assertTrue(b1 && b2 && b3);

        if (BENCHMARK) {
            future = SWQEFactory.getQueryInterface().getFeatures(0, benchmarkSet);

            this.benchmark("getFeatures(...)", future);
        }
    }

    private void benchmark(String title, QueryFuture future) {
        long[] totalTimes = new long[BENCHMARK_RUNS];

        long totalRunTime = System.currentTimeMillis();
        for (int run = 0; run < BENCHMARK_RUNS; run++) {
            totalTimes[run] = System.currentTimeMillis();

            FeatureSet result = future.get();
            Iterator<Feature> iterator = result.getFeatures();
            while (iterator.hasNext())
                iterator.next();

            totalTimes[run] = System.currentTimeMillis() - totalTimes[run];
        }
        totalRunTime = System.currentTimeMillis() - totalRunTime;

        System.out.println("Benchmarking results of " + title + ":");
        System.out.println(" features in set:\t" + BENCHMARK_FEATURES);

        long sum = 0;
        for (int run = 0; run < BENCHMARK_RUNS; run++) {
            System.out.println(" get() + iteration through set " + (run + 1) + ":\t" + totalTimes[run] + "\t(in ms)" );
            sum += totalTimes[run];
        }

        System.out.println(" average:\t" + (1. * sum / totalTimes.length)  + "\t(in ms)" );
        System.out.println(" total runtime: " + totalRunTime);
    }

    @Test
    public void testGetFeaturesByRange() {
        /*
            getFeaturesByRange IS NOT IMPLEMENTED YET!

        // get a FeatureSet from the back-end
        String structure = "chr16";
        int start = 1000000;
        int stop = 1000105;
        QueryFuture future = SWQEFactory.getQueryInterface().getFeaturesByRange(0, bSet, QueryInterface.Location.INCLUDES, structure, start, stop);

        int featuresInRange = 0;
        for (Feature feature : bSet)
            if (feature.getSeqid().equals(structure) && feature.getStart() >= start && feature.getStop() <= stop)
                featuresInRange++;

        FeatureSet result = future.get();

        Assert.assertTrue("Number of returned features within the given range does not match the number of features that were stored there.", result.getCount() == featuresInRange);
        if (BENCHMARK) {
            future = SWQEFactory.getQueryInterface().getFeaturesByRange(0, benchmarkSet, QueryInterface.Location.INCLUDES, "chr1", 10000, 100000);

            this.benchmark("getFeaturesByRange(...)", future);
        }
         */
    }

    @Test
    public void testTypeQuery() {
        // get a FeatureSet from the back-end
        QueryFuture future = SWQEFactory.getQueryInterface().getFeaturesByAttributes(0, aSet, new RPNStack(
                new Constant("type1"), "type", Operation.EQUAL));
        // check that Features are present match
        FeatureSet result = future.get();
        for (Feature f : result) {
            Assert.assertTrue(f.getType().equals("type1"));
        }
        Assert.assertTrue("Query results wrong, expected 1 and found " + result.getCount(), result.getCount() == 1);
    }

    @Test
    public void complexQueryTest() {
        // this version of complexQueryTest is model-agnostic and will run on all back-ends
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        try {
            mManager.persist(bSet);
        } catch (Exception e) {
            Logger.getLogger(SimplePersistentBackEndTest.class.getName()).log(Level.SEVERE, "Exception", e);
            junit.framework.Assert.assertTrue("Backend could not store the given FeatureSet.", false);
        }

        QueryFuture queryFuture = SWQEFactory.getQueryInterface().getFeaturesByAttributes(1, bSet, new RPNStack(
                new Constant("chr16"),
                "seqid",
                Operation.EQUAL));
        FeatureSet resultSet = queryFuture.get();
        junit.framework.Assert.assertTrue("Setting a query constraints with 1 operation on 'id' failed, expected 10 and found " + resultSet.getCount(), resultSet.getCount() == 10);

        queryFuture = SWQEFactory.getQueryInterface().getFeaturesByAttributes(1, bSet, new RPNStack(
                new Constant(Feature.Strand.NEGATIVE),
                "strand",
                Operation.EQUAL));
        resultSet = queryFuture.get();
        junit.framework.Assert.assertTrue("Setting a query constraints with 1 operation on 'strand' failed.", resultSet.getCount() == 3);

        queryFuture = SWQEFactory.getQueryInterface().getFeaturesByAttributes(1, bSet, new RPNStack(
                new Constant(Feature.Strand.NEGATIVE),
                "strand",
                Operation.EQUAL,
                new Constant("chr16"),
                "seqid",
                Operation.EQUAL,
                Operation.AND));
        resultSet = queryFuture.get();
        junit.framework.Assert.assertTrue("Setting a query constraints with 3 operations failed.", resultSet.getCount() == 2);
    }
}
