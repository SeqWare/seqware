package com.github.seqware.impl.test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.seqware.queryengine.dto.QueryEngine.FeaturePB;
import com.github.seqware.queryengine.impl.protobufIO.FeatureIO;
import com.github.seqware.queryengine.impl.tuplebinderIO.FeatureTB;
import com.github.seqware.queryengine.impl.tuplebinderIO.SGIDTB;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.impl.inMemory.InMemoryFeatureSet;
import com.github.seqware.queryengine.model.impl.inMemory.InMemoryReference;
import com.github.seqware.queryengine.util.FSGID;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.db.DatabaseEntry;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.apache.commons.lang.SerializationUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;
import org.objenesis.strategy.SerializingInstantiatorStrategy;

/**
 * Simple serialization/deserialization test using HBase.
 *
 * Serialization is done through Kryo and Protobuf. This class also demonstrated
 * the effect of batching on performance. (i.e. a large performance improvement)
 *
 * @author jbaran
 */
public class HBaseTest {

    private static final String TEST_TABLE = "seqwareTestTable";
    private static final String TEST_COLUMN = "fauxColumn";
    /**
     * Number of runs to execute to determine average
     * serialization/de-serialization times.
     */
    private static final int BENCHMARK_RUNS = 10;
    /**
     * Number of features that should be used for benchmarking
     * serialization/de-serialization.
     */
    private static final int BENCHMARK_FEATURES = 10000;
    /**
     * Run benchmarks
     */
    private boolean BENCHMARK;

    /**
     * Determines which framework should be used for serializing/de-serializing
     * objects.
     */
    private enum SerializationFramework {

        KRYO, PROTOBUF, APACHE //, TUPLEBINDER
    };
    /**
     * If Kryo is used, then this reference points to the Feature class
     * serializer/de-serializer:
     */
    private Kryo serializer;
    /**
     * If BerkeleyDB tuple binders are used, then these point to tuple binders
     */
    private FeatureTB featureTB;
    private SGIDTB sgidTB;
    /**
     * If Protobuf is used, then this reference points to the Feature class
     * serializer/de-serializer:
     */
    private FeatureIO fIO;

    /**
     * Represents a feature and its HBase row identifier.
     */
    private static class IdentifiedFeature {

        byte[] id;
        Feature feature;

        public IdentifiedFeature(byte[] id, Feature feature) {
            this.id = id;
            this.feature = feature;
        }
    }

    /**
     * Create a fresh HBase table (drop existing one) and serialize/deserialize
     * some Feature objects using Kryo.
     *
     * @throws IOException
     */
    @Test
    public void testHBaseTableWithKryo() throws IOException, InterruptedException {
        // Alternative to protobuf: Kryo
        // Speed comparison can be found here: http://code.google.com/p/thrift-protobuf-compare/wiki/Benchmarking
        serializer = new Kryo();

        // Some magic to make serialization work with private default constructors:
        serializer.setInstantiatorStrategy(new SerializingInstantiatorStrategy());

        // Super slow: do not use the JavaSerializer:
        //serializer.register(UUID.class, new JavaSerializer());
        testHBaseTable(SerializationFramework.KRYO, false);
        if (!BENCHMARK){return;}
        testHBaseTable(SerializationFramework.KRYO, true);
    }

    /**
     * Create a fresh HBase table (drop existing one) and serialize/deserialize
     * a Feature objects using Protobuf.
     *
     * @throws IOException
     */
    @Test
    public void testHBaseTableWithProtobuf() throws IOException, InterruptedException {
        fIO = new FeatureIO();
        testHBaseTable(SerializationFramework.PROTOBUF, false);
        if (!BENCHMARK){return;}
        testHBaseTable(SerializationFramework.PROTOBUF, true);
    }

    /**
     * Create a fresh HBase table (drop existing one) and serialize/deserialize
     * a Feature objects using Apache Serialization.
     *
     * @throws IOException
     */
    @Test
    public void testHBaseTableWithApache() throws IOException, InterruptedException {
        testHBaseTable(SerializationFramework.APACHE, false);
        if (!BENCHMARK){return;}
        testHBaseTable(SerializationFramework.APACHE, true);
    }

    /**
     * Create a fresh HBase table (drop existing one) and serialize/deserialize
     * a Feature objects using BerkeleyDB tuple binders.
     *
     * @throws IOException
     */
    @Test
    public void testHBaseTableWithTuple() throws IOException {
        this.featureTB = new FeatureTB();
        this.sgidTB = new SGIDTB();
        //testHBaseTable(SerializationFramework.TUPLEBINDER);
    }

    /**
     * Creates a fresh HBase table (drop existing one) and serialize/deserialize
     * some Feature objects.
     *
     * @throws IOException
     */
    private void testHBaseTable(SerializationFramework framework, boolean batchMode) throws IOException, InterruptedException {
        // Code from http://hbase.apache.org/apidocs/org/apache/hadoop/hbase/client/package-summary.html

        // The HBaseConfiguration reads in hbase-site.xml and hbase-default.xml,
        // as long as these can be found in the CLASSPATH.
        Configuration config = HBaseConfiguration.create();

        String tableName = TEST_TABLE + framework;

        // Create a fresh table, i.e. delete an existing table if it exists:
        HTableDescriptor ht = new HTableDescriptor(tableName);
        ht.addFamily(new HColumnDescriptor(TEST_COLUMN));
        HBaseAdmin hba = new HBaseAdmin(config);
        if (hba.isTableAvailable(tableName)) {
            if (hba.isTableEnabled(tableName)){
                hba.disableTable(tableName);
            }
            hba.deleteTable(tableName);
        }
        hba.createTable(ht);

        HTable table = new HTable(config, tableName);
        table.setAutoFlush(true);

        // run benchmarks in batched mode
        this.BENCHMARK = System.getProperty("com.github.seqware.benchmark", "false").equals("true");

        // Variables that track times for individual benchmark runs:
        long[] serializationTimes = new long[BENCHMARK_RUNS];
        long[] deserializationTimes = new long[BENCHMARK_RUNS];
        long[] totalPutTimes = new long[BENCHMARK_RUNS];
        long[] totalGetTimes = new long[BENCHMARK_RUNS];
        long totalRunTime = System.currentTimeMillis();

        // Create and store a feature/features:
        List<List<IdentifiedFeature>> testFeaturesList = new ArrayList<List<IdentifiedFeature>>();

        if (BENCHMARK) {
            for (int run = 0; run < BENCHMARK_RUNS; run++) {
                totalPutTimes[run] = System.currentTimeMillis();
                List<Row> putBatch = new ArrayList<Row>();
                List<IdentifiedFeature> testFeatures = new ArrayList<IdentifiedFeature>();
                testFeaturesList.add(testFeatures);                
                serializationTimes[run] = System.currentTimeMillis();
                for (int i = 0; i < BENCHMARK_FEATURES; i++) {
                    testFeatures.add(this.storeFauxFeature(framework, table, putBatch, batchMode));
                }
                serializationTimes[run] = System.currentTimeMillis() - serializationTimes[run];
                if (batchMode) {
                    table.batch(putBatch);
                }
                table.flushCommits();
                totalPutTimes[run] = System.currentTimeMillis() - totalPutTimes[run];
            }
        } else {
            List<IdentifiedFeature> testFeatures = new ArrayList<IdentifiedFeature>();
            testFeaturesList.add(testFeatures);    
            testFeatures.add(this.storeFauxFeature(framework, table, null, false));
        }

        // Retrieve a feature/features:
        if (BENCHMARK) {
            for (int run = 0; run < BENCHMARK_RUNS; run++) {
                List<IdentifiedFeature> testFeatures = testFeaturesList.get(run);
                totalGetTimes[run] = System.currentTimeMillis();
                Object[] get = null;
                if (batchMode) {
                    List<Row> batchList = this.getBatchList(testFeatures);
                    get = table.batch(batchList);
                }
                deserializationTimes[run] = System.currentTimeMillis();
                for (int i = 0; i < BENCHMARK_FEATURES; i++) {
                    this.retrieveFauxFeature(framework, table, testFeatures, true, get, batchMode);
                }
                deserializationTimes[run] = System.currentTimeMillis() - deserializationTimes[run];
                totalGetTimes[run] = System.currentTimeMillis() - totalGetTimes[run];
            }
        } else {
            List<IdentifiedFeature> testFeatures = testFeaturesList.get(0);
            this.retrieveFauxFeature(framework, table, testFeatures, false, null, false);
        }

        // Clean-up:
        table.close();
        
        totalRunTime = System.currentTimeMillis() - totalRunTime;
        
        hba.disableTable(tableName);
        hba.deleteTable(tableName);

        // If this is a benchmarking run, then output the benchmarking data now:
        if (BENCHMARK) {
            System.out.println("Benchmarking results (" + framework + "):");
            System.out.println(" objects serialized/deserialized per run:\t" + BENCHMARK_FEATURES);

            long serializationSum = 0, deserializationSum = 0, putSum = 0, getSum = 0; 
            for (int run = 0; run < BENCHMARK_RUNS; run++) {
                System.out.println(" run " + (run + 1) + ":\t" + serializationTimes[run] + "\t" + deserializationTimes[run] + "\t(serialization/deserialization in ms)"
                        + "\t" + totalPutTimes[run] + "\t" + totalGetTimes[run] + "\t(Put/Get in ms)" );
                serializationSum += serializationTimes[run];
                deserializationSum += deserializationTimes[run];
                putSum += totalPutTimes[run];
                getSum += totalGetTimes[run];
            }

            System.out.println(" average:\t" + (1. * serializationSum / serializationTimes.length) + "\t" + (1. * deserializationSum / deserializationTimes.length) + "\t(serialization/deserialization in ms)"
                    + "\t" + (1. * putSum / totalPutTimes.length) + "\t" + (1. * getSum / totalGetTimes.length)  + "\t(Put/Get in ms)" );
            System.out.println(" total runtime: " + totalRunTime);
        }
    }

    /**
     * Serializes a features unique ID and content using Kryo.
     *
     * @param testFeature The feature that should be serialized.
     * @param sgidBytes Stream that is populated with the features unique ID.
     * @param featureBytes Stream that holds the serialized contents of the
     * feature.
     */
    private void serializeFeatureWithKryo(Feature testFeature, ByteArrayOutputStream sgidBytes, ByteArrayOutputStream featureBytes) {
        Output o = new Output(sgidBytes);
        serializer.writeObject(o, testFeature.getSGID());
        o.close();

        o = new Output(featureBytes);
        serializer.writeObject(o, testFeature);
        o.close();
    }

    /**
     * Serializes a features unique ID and content using Apache (really Java)
     * serialization.
     *
     * @param testFeature The feature that should be serialized.
     * @param sgidBytes Stream that is populated with the features unique ID.
     * @param featureBytes Stream that holds the serialized contents of the
     * feature.
     */
    private void serializeFeatureWithApache(Feature testFeature, ByteArrayOutputStream sgidBytes, ByteArrayOutputStream featureBytes) {
        Output o = new Output(sgidBytes);
        o.write(SerializationUtils.serialize(testFeature.getSGID()));
        o.close();

        o = new Output(featureBytes);
        o.write(SerializationUtils.serialize(testFeature));
        o.close();
    }

    /**
     * Serializes a features unique ID and content using TupleBinders.
     *
     * @param testFeature The feature that should be serialized.
     * @param sgidBytes Stream that is populated with the features unique ID.
     * @param featureBytes Stream that holds the serialized contents of the
     * feature.
     */
    private void serializeFeatureWithBinders(Feature testFeature, ByteArrayOutputStream sgidBytes, ByteArrayOutputStream featureBytes) throws IOException {
        Output o = new Output(sgidBytes);
        TupleOutput value = new TupleOutput();
        this.sgidTB.objectToEntry(testFeature.getSGID(), value);
        byte[] data = value.toByteArray();
        o.write(data);
        value.close();
        o.close();

        o = new Output(featureBytes);
        value = new TupleOutput();
        this.featureTB.objectToEntry(testFeature, value);
        data = value.toByteArray();
        o.write(data);
        value.close();
        o.close();
    }

    /**
     * Serializes a features unique ID and content using Protobuf.
     *
     * @param testFeature The feature that should be serialized.
     * @param sgidBytes Stream that is populated with the features unique ID.
     * @param featureBytes Stream that holds the serialized contents of the
     * feature.
     */
    private void serializeFeatureWithProtobuf(Feature testFeature, ByteArrayOutputStream sgidBytes, ByteArrayOutputStream featureBytes) throws IOException {
        FeaturePB fpb = fIO.m2pb(testFeature);

        Output o = new Output(sgidBytes);
        fpb.getAtom().getSgid().writeTo(o);
        o.close();

        o = new Output(featureBytes);
        fpb.writeTo(o);
        o.close();
    }

    /**
     * Generic method for deserializing a feature.
     *
     * @param framework Determines the framework that should be used for the
     * deserialization.
     * @param serializedFeature Input from which the serialized feature will be
     * read.
     * @return A deserialized feature.
     */
    private Feature deserializeFeature(SerializationFramework framework, Input serializedFeature) throws IOException {
        switch (framework) {
            case KRYO:
                return deserializeFeatureWithKryo(serializedFeature);
            case PROTOBUF:
                return deserializeFeatureWithProtobuf(serializedFeature);
            case APACHE:
                return deserializeFeatureWithApache(serializedFeature);
            //case TUPLEBINDER:
            //    return deserializeFeatureWithBinders(serializedFeature);
            default:
                throw new UnsupportedOperationException("Deserialization is not supported for the given method.");
        }
    }

    /**
     * Deserialize a previously serialized feature using Kryo.
     *
     * @return A deserialized feature.
     */
    private Feature deserializeFeatureWithKryo(Input serializedFeature) {
        return serializer.readObject(serializedFeature, Feature.class);
    }

    /**
     * Deserialize a previously serialized feature using Apache.
     *
     * @return A deserialized feature.
     */
    private Feature deserializeFeatureWithApache(Input serializedFeature) {
        return (Feature) SerializationUtils.deserialize(serializedFeature);
    }

    /**
     * Deserialize a previously serialized feature using Apache.
     *
     * @return A deserialized feature.
     */
    private Feature deserializeFeatureWithBinders(Input serializedFeature) {
        // not the most efficient, but good enough?
        long length;
        try {
            length = serializedFeature.available();
            byte[] bytes = new byte[(int) length];
            serializedFeature.read(bytes);
            TupleInput input = new TupleInput(bytes);
            return (Feature) this.featureTB.entryToObject(new DatabaseEntry(bytes));
        } catch (IOException ex) {
            Logger.getLogger(HBaseTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Deserialize a previously serialized feature using Protobuf.
     *
     * @return A deserialized feature.
     */
    private Feature deserializeFeatureWithProtobuf(Input serializedFeature) throws IOException {
        return fIO.pb2m(FeaturePB.parseFrom(serializedFeature));
    }

    private IdentifiedFeature storeFauxFeature(SerializationFramework framework, HTable table, List<Row> rowList, boolean batchMode) throws IOException {
        // Get one feature to serialize/deserialize:
        Feature testFeature = Feature.newBuilder().setId("chr16").setStart(1000000).setStop(1000100).build();
        FeatureSet set = InMemoryFeatureSet.newBuilder().setReference(InMemoryReference.newBuilder().setName("testRef").build()).build();
        // we need to upgrade the feature with a link to an enforced FeatureSet like in the real back-end
        FSGID fsgid = new FSGID(testFeature.getSGID(), testFeature, set);
        testFeature.impersonate(fsgid, testFeature.getPrecedingSGID());

        // Streams that will hold the serialized objects:
        ByteArrayOutputStream sgidBytes = new ByteArrayOutputStream();
        ByteArrayOutputStream featureBytes = new ByteArrayOutputStream();

        switch (framework) {
            case KRYO:
                this.serializeFeatureWithKryo(testFeature, sgidBytes, featureBytes);
                break;
            case PROTOBUF:
                this.serializeFeatureWithProtobuf(testFeature, sgidBytes, featureBytes);
                break;
            case APACHE:
                this.serializeFeatureWithApache(testFeature, sgidBytes, featureBytes);
                break;
            //case TUPLEBINDER:
            //    this.serializeFeatureWithBinders(testFeature, sgidBytes, featureBytes);
            //    break;
            default:
                throw new UnsupportedOperationException("The given serialization method is not supported.");
        }

        Put p = new Put(sgidBytes.toByteArray());

        // Serialize:
        p.add(Bytes.toBytes(TEST_COLUMN), Bytes.toBytes("feature"), featureBytes.toByteArray());
        if (batchMode) {
            rowList.add(p);
        } else {
            table.put(p);
        }

        return new IdentifiedFeature(sgidBytes.toByteArray(), testFeature);
    }

    private void retrieveFauxFeature(SerializationFramework framework, HTable table, List<IdentifiedFeature> testFeatures, boolean benchmarking, Object[] batchedResults, boolean batchMode) throws IOException {
        int counter = 0;
        for (IdentifiedFeature identifiedFeature : testFeatures) {
            // Deserialize:
            Result r;
            if (batchMode) {
                r = (Result)batchedResults[counter++];
            } else {
                Get g = new Get(identifiedFeature.id);
                r = table.get(g);
            }
            byte[] value = r.getValue(Bytes.toBytes(TEST_COLUMN), Bytes.toBytes("feature"));

            // We should get back a Feature object:
            Input result = new Input(new ByteArrayInputStream(value));
            Feature deserializedFeature = deserializeFeature(framework, result);

            Assert.assertEquals("The deserialized feature does not match its expected contents.", identifiedFeature.feature, deserializedFeature);
            if (benchmarking) {
                return;
            }
            
            // Check if the only object in the HBase table is the feature we put there:
            Scan s = new Scan();
            s.addColumn(Bytes.toBytes(TEST_COLUMN), Bytes.toBytes("feature"));
            ResultScanner scanner = table.getScanner(s);
            try {
                for (Result rr : scanner) {
                    KeyValue[] kvArray = rr.raw();
                    for (KeyValue kv : kvArray) {
                        result = new Input(new ByteArrayInputStream(kv.getValue()));
                        deserializedFeature = deserializeFeature(framework, result);

                        boolean someKnownFeature = false;
                        for (IdentifiedFeature identifiedFeatureIter : testFeatures) {
                            if (identifiedFeatureIter.feature.equals(deserializedFeature)) {
                                someKnownFeature = true;
                            }
                        }

                        Assert.assertTrue("A feature has been deserialized that has not been previously put in the table.", someKnownFeature);
                    }
                }
            } catch (Exception e) {
                Assert.assertTrue("An exception occurred whilst scanning the HBase table.", false);
            } finally {
                scanner.close();
            }
        }
    }

    private List<Row> getBatchList(List<IdentifiedFeature> testFeatures) throws IOException {
        List<Row> batchList = new ArrayList<Row>();
        for (IdentifiedFeature identifiedFeature : testFeatures) {
            // Deserialize:
            Get g = new Get(identifiedFeature.id);
            batchList.add(g);
        }
        return batchList;
    }
}
