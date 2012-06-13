package com.github.seqware.impl.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.seqware.dto.QueryEngine.FeaturePB;
import com.github.seqware.factory.Factory;
import com.github.seqware.impl.protobufIO.FeatureIO;
import com.github.seqware.model.Feature;
import junit.framework.Assert;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

/**
 * Simple serialization/deserialization test using HBase with Proto Buffers
 *
 * @author jbaran
 */
public class HBaseTestPB {
    private static final String TEST_TABLE = "seqwareTestTablePB";
    private static final String TEST_COLUMN = "fauxColumnPB";

    /**
     * Create a fresh HBase table (drop existing one) and serialize/deserialize
     * a Feature object.
     *
     * @throws IOException
     */
    @Test
    public void testHBaseTablePB() throws IOException {

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
        Feature testFeature = Factory.getModelManager().buildFeature().setId("chr16").setStart(1000000).setStop(1000100).build();
        FeaturePB fpb = FeatureIO.m2pb(testFeature);
        
        ByteArrayOutputStream sgidBytes = new ByteArrayOutputStream();
        Output o = new Output(sgidBytes);
        fpb.getSgid().writeTo(o);
        o.close();

        ByteArrayOutputStream featureBytes = new ByteArrayOutputStream();
        o = new Output(featureBytes);
        fpb.writeTo(o);
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
        Feature deserializedFeature = FeatureIO.pb2m(FeaturePB.parseFrom(result));

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
                    deserializedFeature = FeatureIO.pb2m(FeaturePB.parseFrom(result));
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
