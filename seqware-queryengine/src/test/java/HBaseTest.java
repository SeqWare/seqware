import java.io.IOException;

import junit.framework.Assert;
import org.junit.Test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

public class HBaseTest {

    @Test
    public void main() throws IOException {
// You need a configuration object to tell the client where to connect.
// When you create a HBaseConfiguration, it reads in whatever you've set
// into your hbase-site.xml and in hbase-default.xml, as long as these
// can be found on the CLASSPATH
        Configuration config = HBaseConfiguration.create();

        HTable table = new HTable(config, "myLittleHBaseTable");

        Put p = new Put(Bytes.toBytes("myLittleRow"));

        p.add(Bytes.toBytes("myLittleFamily"), Bytes.toBytes("someQualifier"),
                Bytes.toBytes("Some Value"));

        table.put(p);

        Get g = new Get(Bytes.toBytes("myLittleRow"));
        Result r = table.get(g);
        byte[] value = r.getValue(Bytes.toBytes("myLittleFamily"), Bytes
                .toBytes("someQualifier"));
        String valueStr = Bytes.toString(value);
        System.out.println("GET: " + valueStr);

        Scan s = new Scan();
        s.addColumn(Bytes.toBytes("myLittleFamily"), Bytes
                .toBytes("someQualifier"));
        ResultScanner scanner = table.getScanner(s);
        try {
            for (Result rr = scanner.next(); rr != null; rr = scanner.next()) {
                System.out.println("Found row: " + rr);
            }
        } finally {
            scanner.close();
        }
    }
}

