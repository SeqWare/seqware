package com.github.seqware.impl.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.github.seqware.factory.Factory;
import com.github.seqware.model.Feature;
import java.util.UUID;
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
 * @author jbaran
 */
public class HBaseTest {
    private static final String TEST_TABLE = "seqwareTestTable";
    private static final String TEST_COLUMN = "fauxColumn";

    /**
     * Create a fresh HBase table (drop existing one) and serialize/deserialize
     * a Feature object.
     *
     * @throws IOException
     */
    @Test
    public void testHBaseTable() throws IOException {

        // Code from http://hbase.apache.org/apidocs/org/apache/hadoop/hbase/client/package-summary.html

        // The HBaseConfiguration reads in hbase-site.xml and hbase-default.xml,
        // as long as these can be found in the CLASSPATH.
        Configuration config = HBaseConfiguration.create();

        // Create a fresh table, i.e. delete an existing table if it exists:
        HTableDescriptor ht = new HTableDescriptor(TEST_TABLE);
	    ht.addFamily(new HColumnDescriptor(TEST_COLUMN));
	    HBaseAdmin hba = new HBaseAdmin(config);
        if(hba.isTableAvailable(TEST_TABLE)){
            hba.disableTable(TEST_TABLE);
            hba.deleteTable(TEST_TABLE);
        }
	    hba.createTable( ht );

        HTable table = new HTable(config, TEST_TABLE);

        // Get one feature to serialize/deserialize:
        Feature testFeature = Factory.getModelManager().buildFeature().setStart(1000000).setStop(1000100).build();

        // Alternative to protobuf: Kryo
        // Speed comparison can be found here: http://code.google.com/p/thrift-protobuf-compare/wiki/Benchmarking
        Kryo serializer = new Kryo();

        // Some magic to make serialization work with private default constructors:
        serializer.setInstantiatorStrategy(new SerializingInstantiatorStrategy());
        //serializer.setDefaultSerializer(JavaSerializer.class);
        serializer.register(UUID.class, new JavaSerializer());
        
        ByteArrayOutputStream sgidBytes = new ByteArrayOutputStream();
        Output o = new Output(sgidBytes);
        serializer.writeObject(o, testFeature.getSGID());
        o.close();

        ByteArrayOutputStream featureBytes = new ByteArrayOutputStream();
        o = new Output(featureBytes);
        serializer.writeObject(o, testFeature);
        o.close();

        Put p = new Put(sgidBytes.toByteArray());

        // Serialize:
        p.add(Bytes.toBytes(TEST_COLUMN), Bytes.toBytes("feature"), featureBytes.toByteArray());
        table.put(p);

        // Deserialize:
        Get g = new Get(sgidBytes.toByteArray());
        Result r = table.get(g);
        byte[] value = r.getValue(Bytes.toBytes(TEST_COLUMN), Bytes.toBytes("feature"));

        // We should get back a Feature object:
        Input result = new Input(new ByteArrayInputStream(value));
        Feature deserializedFeature = serializer.readObject(result, Feature.class);

        // NOTE This test fails right now, which is due to the equals() implementation
        //      in Feature. When inspecting the features in debugging mode, then they
        //      are clearly equal in terms of UUID and values.
        Assert.assertEquals(testFeature, deserializedFeature);
        //testFeature.equals(deserializedFeature);

        // Check if the only object in the HBase table is the feature we put there:
        Scan s = new Scan();
        s.addColumn(Bytes.toBytes(TEST_COLUMN), Bytes.toBytes("feature"));
        ResultScanner scanner = table.getScanner(s);
        try {
            for (Result rr : scanner) {
                KeyValue[] kvArray = rr.raw();
                for (KeyValue kv : kvArray) {
                    result = new Input(new ByteArrayInputStream(kv.getValue()));
                    deserializedFeature = serializer.readObject(result, Feature.class);

                    // NOTE Same as above: fails due to equals() implementation.
                    Assert.assertEquals(testFeature, deserializedFeature);
                    //testFeature.equals(deserializedFeature);
                }
            }
        }
        catch (Exception e) {
            Assert.assertTrue("An exception occurred whilst scanning the HBase table.", false);
        }
        finally {
            scanner.close();
        }

        // Clean-up:
        hba.disableTable(TEST_TABLE);
        hba.deleteTable(TEST_TABLE);
    }
}
