package com.github.seqware.impl.test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.github.seqware.dto.QueryEngine.FeaturePB;
import com.github.seqware.factory.Factory;
import com.github.seqware.impl.protobufIO.FeatureIO;
import com.github.seqware.model.Feature;
import com.github.seqware.model.FeatureSet;
import com.github.seqware.model.impl.inMemory.InMemoryFeatureSet;
import com.github.seqware.model.impl.inMemory.InMemoryReference;
import com.github.seqware.util.FSGID;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.github.seqware.util.SGID;
import junit.framework.Assert;
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
 * Serialization is done through Kryo and Protobuf.
 *
 * @author jbaran
 */
public class HBaseTest {
    private static final String TEST_TABLE = "seqwareTestTable";
    private static final String TEST_COLUMN = "fauxColumn";

    /**
     * Number of runs to execute to determine average serialization/deserialization times.
     */
    private static final int BENCHMARK_RUNS = 10;

    /**
     * Number of features that should be used for benchmarking serialization/deserialization.
     */
    private static final int BENCHMARK_FEATURES = 10000;

    /**
     * Determines which framework should be used for serializing/deserializing objects.
     */
    private enum SerializationFramework { KRYO, PROTOBUF };

    /**
     * If Kryo is used, then this reference points to the Feature class serializer/deserializer:
     */
    private Kryo serializer;

    /**
     * If Protobuf is used, then this reference points to the Feature class serializer/deserializer:
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
    public void testHBaseTableWithKryo() throws IOException {
        // Alternative to protobuf: Kryo
        // Speed comparison can be found here: http://code.google.com/p/thrift-protobuf-compare/wiki/Benchmarking
        serializer = new Kryo();

        // Some magic to make serialization work with private default constructors:
        serializer.setInstantiatorStrategy(new SerializingInstantiatorStrategy());

        // Super slow: do not use the JavaSerializer:
        //serializer.register(UUID.class, new JavaSerializer());

        testHBaseTable(SerializationFramework.KRYO);
    }

    /**
     * Create a fresh HBase table (drop existing one) and serialize/deserialize
     * a Feature objects using Protobuf.
     *
     * @throws IOException
     */
    @Test
    public void testHBaseTableWithProtobuf() throws IOException {
        fIO = new FeatureIO();

        testHBaseTable(SerializationFramework.PROTOBUF);
    }

    /**
     * Createsa fresh HBase table (drop existing one) and serialize/deserialize
     * some Feature objects.
     *
     * @throws IOException
     */
    private void testHBaseTable(SerializationFramework framework) throws IOException {
        // Code from http://hbase.apache.org/apidocs/org/apache/hadoop/hbase/client/package-summary.html

        // The HBaseConfiguration reads in hbase-site.xml and hbase-default.xml,
        // as long as these can be found in the CLASSPATH.
        Configuration config = HBaseConfiguration.create();

        String tableName = TEST_TABLE + framework;

        // Create a fresh table, i.e. delete an existing table if it exists:
        HTableDescriptor ht = new HTableDescriptor(tableName);
	    ht.addFamily(new HColumnDescriptor(TEST_COLUMN));
	    HBaseAdmin hba = new HBaseAdmin(config);
        if(hba.isTableAvailable(tableName)){
            hba.disableTable(tableName);
            hba.deleteTable(tableName);
        }
	    hba.createTable( ht );

        HTable table = new HTable(config, tableName);

        // Variables that track times for individual benchmark runs:
        long[] serializationTimes = new long[BENCHMARK_RUNS];
        long[] deserializationTimes = new long[BENCHMARK_RUNS];

        // Create and store a feature/features:
        List<IdentifiedFeature> testFeatures = new LinkedList<IdentifiedFeature>();
        if (System.getProperty("com.github.seqware.benchmark", "false").equals("true")) {
            for (int run = 0; run < BENCHMARK_RUNS; run++) {
                serializationTimes[run] = System.currentTimeMillis();
                for (int i = 0; i < BENCHMARK_FEATURES; i++)
                    testFeatures.add(this.storeFauxFeature(framework, table));
                serializationTimes[run] = System.currentTimeMillis() - serializationTimes[run];
            }
        } else
            testFeatures.add(this.storeFauxFeature(framework, table));

        // Retrieve a feature/features:
        if (System.getProperty("com.github.seqware.benchmark", "false").equals("true")) {
            for (int run = 0; run < BENCHMARK_RUNS; run++) {
                deserializationTimes[run] = System.currentTimeMillis();
                for (int i = 0; i < BENCHMARK_FEATURES; i++)
                    this.retrieveFauxFeature(framework, table, testFeatures, true);
                deserializationTimes[run] = System.currentTimeMillis() - deserializationTimes[run];
            }
        } else
            this.retrieveFauxFeature(framework, table, testFeatures, false);

        // Clean-up:
        hba.disableTable(tableName);
        hba.deleteTable(tableName);

        // If this is a benchmarking run, then output the benchmarking data now:
        if (System.getProperty("com.github.seqware.benchmark", "false").equals("true")) {
            System.out.println("Benchmarking results (" + framework + "):");
            System.out.println(" objects serialized/deserialized per run:\t" + BENCHMARK_FEATURES);

            long serializationSum = 0, deserializationSum = 0;
            for (int run = 0; run < BENCHMARK_RUNS; run++) {
                System.out.println(" run " + (run + 1) + ":\t" + serializationTimes[run] + "\t" + deserializationTimes[run] + "\t(serialization/deserialization in ms)");
                serializationSum += serializationTimes[run];
                deserializationSum += deserializationTimes[run];
            }

            System.out.println(" average:\t" + (1. * serializationSum / serializationTimes.length) + "\t" + (1. * deserializationSum / deserializationTimes.length) + "\t(serialization/deserialization in ms)");
        }
    }

    /**
     * Serializes a features unique ID and content using Kryo.
     *
     * @param testFeature The feature that should be serialized.
     * @param sgidBytes Stream that is populated with the features unique ID.
     * @param featureBytes Stream that holds the serialized contents of the feature.
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
     * Serializes a features unique ID and content using Protobuf.
     *
     * @param testFeature The feature that should be serialized.
     * @param sgidBytes Stream that is populated with the features unique ID.
     * @param featureBytes Stream that holds the serialized contents of the feature.
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
     * @param framework Determines the framework that should be used for the deserialization.
     * @param serializedFeature Input from which the serialized feature will be read.
     * @return A deserialized feature.
     */
    private Feature deserializeFeature(SerializationFramework framework, Input serializedFeature) throws IOException {
        switch (framework) {
        case KRYO:
            return deserializeFeatureWithKryo(serializedFeature);
        case PROTOBUF:
            return deserializeFeatureWithProtobuf(serializedFeature);
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
     * Deserialize a previously serialized feature using Protobuf.
     *
     * @return A deserialized feature.
     */
    private Feature deserializeFeatureWithProtobuf(Input serializedFeature) throws IOException {
        return fIO.pb2m(FeaturePB.parseFrom(serializedFeature));
    }

    private IdentifiedFeature storeFauxFeature(SerializationFramework framework, HTable table) throws IOException {
        // Get one feature to serialize/deserialize:
        Feature testFeature = Feature.newBuilder().setId("chr16").setStart(1000000).setStop(1000100).build();
        FeatureSet set = InMemoryFeatureSet.newBuilder().setReference(InMemoryReference.newBuilder().setName("testRef").build()).build();
        // we need to upgrade the feature with a link to an enforced FeatureSet like in the real back-end
        FSGID fsgid = new FSGID(testFeature.getSGID(),testFeature, set);
        testFeature.impersonate(fsgid, testFeature.getCreationTimeStamp(), testFeature.getPrecedingSGID());

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
            default:
                throw new UnsupportedOperationException("The given serialization method is not supported.");
        }

        Put p = new Put(sgidBytes.toByteArray());

        // Serialize:
        p.add(Bytes.toBytes(TEST_COLUMN), Bytes.toBytes("feature"), featureBytes.toByteArray());
        table.put(p);

        return new IdentifiedFeature(sgidBytes.toByteArray(), testFeature);
    }

    private void retrieveFauxFeature(SerializationFramework framework, HTable table, List<IdentifiedFeature> testFeatures, boolean benchmarking) throws IOException {
        for (IdentifiedFeature identifiedFeature : testFeatures) {
            // Deserialize:
            Get g = new Get(identifiedFeature.id);
            Result r = table.get(g);
            byte[] value = r.getValue(Bytes.toBytes(TEST_COLUMN), Bytes.toBytes("feature"));

            // We should get back a Feature object:
            Input result = new Input(new ByteArrayInputStream(value));
            Feature deserializedFeature = deserializeFeature(framework, result);

            if (benchmarking)
                return;

            Assert.assertEquals("The deserialized feature does not match its expected contents.", identifiedFeature.feature, deserializedFeature);

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
                        for (IdentifiedFeature identifiedFeatureIter : testFeatures)
                            if (identifiedFeatureIter.feature.equals(deserializedFeature))
                                someKnownFeature = true;

                        Assert.assertTrue("A feature has been deserialized that has not been previously put in the table.", someKnownFeature);
                    }
                }
            }
            catch (Exception e) {
                Assert.assertTrue("An exception occurred whilst scanning the HBase table.", false);
            }
            finally {
                scanner.close();
            }
        }
    }
}
