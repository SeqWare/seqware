package net.sourceforge.seqware.queryengine.prototypes.hadoop;

import java.io.IOException;

import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders.VariantTB;
import net.sourceforge.seqware.queryengine.backend.model.Variant;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import com.sleepycat.db.DatabaseEntry;

/**
 * TODO:
 * > iterate over each row and pull out variant objects
 * > check to see if both variants exists at position
 * > if they do then increment counter
 * > write the counter to an hdfs file
 * > need to figure out how to pass parameters to map and reduce classes!
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class MapReduceTest {

  static final String NAME = "MapReduceTest";
  
  /**
   * Mapper that runs the count.
   */
  static class RowCounterMapper
  extends TableMapper<ImmutableBytesWritable, Result> {
    
    /** Counter enumeration to count the actual rows. */
    private static enum Counters {ROWS, NORMAL, TUMORANDNORMAL}
    private int numRecords = 0;
    VariantTB vtb = new VariantTB();

    /**
     * Maps the data.
     * 
     * @param row  The current table row key.
     * @param values  The columns.
     * @param context  The current context.
     * @throws IOException When something is broken with the data.
     * @see org.apache.hadoop.mapreduce.Mapper#map(KEYIN, VALUEIN, 
     *   org.apache.hadoop.mapreduce.Mapper.Context)
     */
    @Override
    public void map(ImmutableBytesWritable row, Result values,
      Context context)
    throws IOException {
      // the row is the key1
      // the values is value1
      // resulting k2, v2 goes to context!
      if (values != null) {
        byte[] bytes = values.getValue(Bytes.toBytes("variant"), Bytes.toBytes("Genome1102N"));
        if (bytes != null && bytes.length > 0) {
          DatabaseEntry value = new DatabaseEntry(bytes);
          Variant variantN = (Variant)vtb.entryToObject(value);
          if (variantN != null) {
            context.getCounter(Counters.NORMAL).increment(1);
            // now look at the other one
            if ((numRecords % 1000) == 0) {
              System.err.println("TEST: "+variantN.getCalledBase());
              //System.exit(1);
            }
            bytes = values.getValue(Bytes.toBytes("variant"), Bytes.toBytes("Genome1102T"));
            if (bytes != null && bytes.length > 0) {
              value = new DatabaseEntry(bytes);
              Variant variantT = (Variant)vtb.entryToObject(value);
              if (variantT != null) {
                if (variantT.getCalledBase().equals(variantN.getCalledBase())) {
                  context.getCounter(Counters.TUMORANDNORMAL).increment(1);
                }
              }
            }
          }
        }
      }
      for (KeyValue value: values.list()) {
        if (value.getValue().length > 1) {
          context.getCounter(Counters.ROWS).increment(1);
          // the context lets you write the k2, v2
          break;
        }
      }
      numRecords++;
      if ((numRecords % 10000) == 0) {
        row.get();
        context.setStatus("mapper processed "+numRecords+" so far!: ");
      }
    }
  }
  
  /**
   * Sets up the actual job.
   *
   * @param conf  The current configuration.
   * @param args  The command line parameters.
   * @return The newly created job.
   * @throws java.io.IOException When setting up the job fails.
   */
  public static Job createSubmittableJob(Configuration conf, String[] args) 
  throws IOException {
    String tableName = args[0];
    String family = args[1];
    String label1 = args[2];
    String label2 = args[3];
    // TODO: figure out how to pass these to the mappers/reducers
    Job job = new Job(conf, NAME + "_" + tableName);
    job.setJarByClass(MapReduceTest.class);
    // Columns are space delimited
    //StringBuilder sb = new StringBuilder();
    /*final int columnoffset = 1;
    for (int i = columnoffset; i < args.length; i++) {
      if (i > columnoffset) {
        sb.append(" ");
      }
      sb.append(args[i]);
    }*/
    Scan scan = new Scan();
    //if (sb.length() > 0) scan.addColumns(sb.toString());
    // only care about variant family
    scan.addFamily(Bytes.toBytes(family));
    // the next option will result in only one family per map!
    //scan.setFilter(new FirstKeyOnlyFilter());
    // Second argument is the table name.
    TableMapReduceUtil.initTableMapperJob(tableName, scan,
      RowCounterMapper.class, ImmutableBytesWritable.class, Result.class, job);
    job.setOutputFormatClass(NullOutputFormat.class);
    job.setNumReduceTasks(0);
    // first argument is the output directory.
    // FileOutputFormat.setOutputPath(job, new Path(args[0]));
    return job;
  }

  /**
   * Main entry point.
   *
   * @param args  The command line parameters.
   * @throws java.lang.Exception When running the job fails.
   */
  public static void main(String[] args) throws Exception {
    HBaseConfiguration conf = new HBaseConfiguration();
    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
    if (otherArgs.length < 4) {
      System.err.println("ERROR: Wrong number of parameters: " + args.length);
      System.err.println("Usage: RowCounter <tablename> <family> <label1> <label2>");
      System.exit(-1);
    }
    Job job = createSubmittableJob(conf, otherArgs);
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
  
  // family: "variant", label: "Genome1102T" or Genome1102N
  
}
