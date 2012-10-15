package net.sourceforge.seqware.queryengine.prototypes.hadoop;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders.CoverageTB;
import net.sourceforge.seqware.queryengine.backend.io.berkeleydb.tuplebinders.VariantTB;
import net.sourceforge.seqware.queryengine.backend.model.Coverage;
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.impl.HBaseStore;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
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
public class MapReduceTest2 {

  static final String NAME = "MapReduceTest";
  static final int BINSIZE = 1000;
  
  /**
   * Mapper that runs the count.
   * The types here on TableMapper are for KeyOut (ImmutableBytesWritable) and ValueOut (Result) respectively
   */
  static class MutationMapper
  extends TableMapper<ImmutableBytesWritable, ImmutableBytesWritable> {
    
    /** Counter enumeration to count the actual rows. */
    private static enum Counters {TOTALVARIANTS, NORMAL, TUMOR, TUMORANDNORMAL, COVERAGE, INTUMORNOTNORMAL}
    private int numRecords = 0;
    VariantTB vtb = new VariantTB();
    CoverageTB ctb = new CoverageTB();

    /**
     * Maps the data.
     * 
     * FIXME: thing about poly-allelic locations
     * 
     * @param row  The current table row key.
     * @param values  The columns.
     * @param context  The current context.
     * @throws IOException When something is broken with the data.
     * @see org.apache.hadoop.mapreduce.Mapper#map(KEYIN, VALUEIN, 
     *   org.apache.hadoop.mapreduce.Mapper.Context)
     */
    @Override
    public void map(ImmutableBytesWritable row, Result values, Context context) throws IOException {
      // the row is the key1
      // the values is value1
      // resulting k2, v2 goes into context!
      // based on the param to this class the k2 is ImmutableBytesWritable and the v2 is result (this row)
      boolean isInTumor = false;
      boolean isInNormal = false;
      Variant variantT = null;
      Variant variantN = null;
      Coverage covN = null;
      
      if (values != null) {
        
        // figure out if we're looking at a coverage position
        byte[] bytes = values.getValue(Bytes.toBytes("coverage"), Bytes.toBytes("Genome1102N"));
        if (bytes != null && bytes.length > 0) {
          try {
            covN = (Coverage)ctb.entryToObject(new DatabaseEntry(bytes));
            int bin = covN.getStartPosition() / BINSIZE;
            int startBin = bin * BINSIZE;
            String id = covN.getContig()+":"+HBaseStore.padZeros(startBin, HBaseStore.PAD);
            // now figure out the value for Value2, this is just the tumor variant
            ImmutableBytesWritable key2 = new ImmutableBytesWritable(Bytes.toBytes(id));
            DatabaseEntry value = new DatabaseEntry();
            ctb.objectToEntry(covN, value);
            ImmutableBytesWritable value2 = new ImmutableBytesWritable(value.getData());
            context.getCounter(Counters.COVERAGE).increment(1);
            context.write(key2, value2);
          } catch (Exception e) {
            context.setStatus("Exception happened when creating ID for coverage to pass to reducer: "+e.getMessage());
          }
        }

        // now figure out if this is seen in both normal and tumor
        // pull back normal
        bytes = values.getValue(Bytes.toBytes("variant"), Bytes.toBytes("Genome1102N"));
        if (bytes != null && bytes.length > 0) {
          DatabaseEntry value = new DatabaseEntry(bytes);
          variantN = (Variant)vtb.entryToObject(value);
          if (variantN != null) {
            context.getCounter(Counters.NORMAL).increment(1);
          }
        }
        // pull back tumor
        bytes = values.getValue(Bytes.toBytes("variant"), Bytes.toBytes("Genome1102T"));
        if (bytes != null && bytes.length > 0) {
          DatabaseEntry value = new DatabaseEntry(bytes);
          variantT = (Variant)vtb.entryToObject(value);
          if (variantT != null) {
            isInTumor = true;
            context.getCounter(Counters.TUMOR).increment(1);
            // now look at the other one
            if (variantN != null) {
              if (variantN.getCalledBase().equals(variantT.getCalledBase())) {
                context.getCounter(Counters.TUMORANDNORMAL).increment(1);
                isInNormal = true;
              }
            }
          }
        }
        if (variantT != null || variantN != null) {
          context.getCounter(Counters.TOTALVARIANTS).increment(1);
        }
      }
      // now figure out if this is a potential somatic mutation
      if (isInTumor && !isInNormal) {
        context.getCounter(Counters.INTUMORNOTNORMAL).increment(1);
        // figure out the ID to produce as Key2
        try {
          int bin = variantT.getStartPosition() / BINSIZE;
          int startBin = bin * BINSIZE;
          String id = variantT.getContig()+":"+HBaseStore.padZeros(startBin, HBaseStore.PAD);
          // now figure out the value for Value2, this is just the tumor variant
          ImmutableBytesWritable key2 = new ImmutableBytesWritable(Bytes.toBytes(id));
          DatabaseEntry value = new DatabaseEntry();
          vtb.objectToEntry(variantT, value);
          ImmutableBytesWritable value2 = new ImmutableBytesWritable(value.getData());
          // by only passing in the id and tumor variant I'm cutting down on the amount of data sent to reduce
          context.write(key2, value2);
        } catch (Exception e) {
          context.setStatus("Exception happened when creating ID to pass to reducer: "+e.getMessage());
        }
      }

      numRecords++;
      if ((numRecords % 10000) == 0) {
        context.setStatus("mapper processed "+numRecords+" so far!: ");
      }
    }
  }
  
  /**
   * The types here on TableReducer are for KeyIn (?) and ValueIn (?), and KeyOut (?) respectively
   * @author boconnor
   *
   */
  static class MutationReducer 
    extends Reducer <ImmutableBytesWritable, ImmutableBytesWritable, Text, Text> {
    
    VariantTB vtb = new VariantTB();
    CoverageTB ctb = new CoverageTB();
    
    public void reduce(ImmutableBytesWritable keyIn, Iterable<ImmutableBytesWritable> valuesIn, Context context)
    throws IOException, InterruptedException {
      
      Coverage covN = null;
      ArrayList<Variant> variantArrayList = new ArrayList<Variant>();
      
      // first, iterate over and find coverage object, populate array of variants
      for (ImmutableBytesWritable value : valuesIn) {
        byte[] bValue = value.get();
        try {
          covN = (Coverage)ctb.entryToObject(new DatabaseEntry(bValue));
        } catch (Exception e) {
          // FIXME: this is a little sketchy, instead the TupleBinding objects
          // should be smart enough to return null if they are reading a byte[] 
          // for an unsupported object rather than throwing all these exceptions!
          // LEFT OFF HERE
          Variant mut = (Variant)vtb.entryToObject(new DatabaseEntry(value.get()));
          variantArrayList.add(mut);
        }
      }
      
      // now iterate over all the variants found
      for (Variant mut : variantArrayList) {
        try {
          if (true) {
            int cov = covN.getCoverage(mut.getStartPosition());
            String bedString = "SNV:"+mut.getReferenceBase()+"->"+mut.getCalledBase();
            
            StringBuffer lengthString = new StringBuffer();
            int blockSize = 1;
            for (int i=0; i<mut.getCalledBase().length(); i++) { lengthString.append("-"); }
            if (mut.getType() == Variant.INSERTION) {
              bedString = "INS:"+lengthString+"->"+mut.getCalledBase();
            } else if (mut.getType() == Variant.DELETION) {
              bedString = "DEL:"+mut.getCalledBase()+"->"+lengthString;
              blockSize = lengthString.length();
            }
            
            DecimalFormat df = new DecimalFormat("##0.0");
            
            double calledPercent = 0.0;
            double calledFwdPercent = 0.0;
            double calledRvsPercent = 0.0;
            if (mut.getReadCount() > 0) {
              calledPercent = ((double)mut.getCalledBaseCount() / (double)mut.getReadCount()) * (double)100.0;
              calledFwdPercent = ((double)mut.getCalledBaseCountForward() / (double)mut.getReadCount()) * (double)100.0;
              calledRvsPercent = ((double)mut.getCalledBaseCountReverse() / (double)mut.getReadCount()) * (double)100.0;
            }
            
            String callStr = "heterozygous";
            if (mut.getZygosity() == mut.HOMOZYGOUS) { callStr = "homozygous"; }
            
            String name = mut.getStartPosition()+"\t"+mut.getStopPosition()+"\t"+bedString+
                          "("+mut.getReadCount()+":"+mut.getCalledBaseCount()+":"+df.format(calledPercent)+
                          "%[F:"+mut.getCalledBaseCountForward()+":"+df.format(calledFwdPercent)+"%|R:"
                          +mut.getCalledBaseCountReverse()+":"+df.format(calledRvsPercent)+"%]"+
                          "call="+callStr+":genome_phred="+mut.getReferenceCallQuality()+":snp_phred="+
                          mut.getConsensusCallQuality()+":max_mapping_qual="+mut.getMaximumMappingQuality()+
                          ":mismatch_id="+mut.getId()+
                          "\tNormCov:"+cov+"\tTumorCov:"+mut.getReadCount();
            context.write(new Text(mut.getContig()), new Text(name));
          }
        } catch (Exception e) {
          //ignored
        }
      }
      
      /*for (ImmutableBytesWritable value : valuesIn) {
        try {
          Variant mut = (Variant)vtb.entryToObject(new DatabaseEntry(value.get()));
          if (true) {
            //int cov = covN.getCoverage(mut.getStartPosition());
            int cov = 5;
            String type = mut.getReferenceBase()+"->"+mut.getCalledBase();
            if (mut.getType() == Variant.DELETION) { type = "DEL:"+mut.getReferenceBase()+"->-"; }
            else if (mut.getType() == Variant.INSERTION) { type = "INS:-->"+mut.getCalledBase(); }
            String name = mut.getStartPosition()+"\t"+mut.getStopPosition()+"\t"+type+"\tNormCov:"+cov+
                          "\tTumorCov:"+mut.getReadCount();
            context.write(new Text(mut.getContig()), new Text(name));
          }
        } catch (Exception e) {
          //ignored
        }
      }*/
      
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
    String outputDir = args[4];
    // TODO: figure out how to pass these to the mappers/reducers
    Job job = new Job(conf, NAME + "_" + tableName);
    job.setJarByClass(MapReduceTest2.class);
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
    scan.addFamily(Bytes.toBytes("coverage"));
    // the next option will result in only one family per map!
    //scan.setFilter(new FirstKeyOnlyFilter());
    // Second argument is the table name.
    TableMapReduceUtil.initTableMapperJob(tableName, scan,
      MutationMapper.class, ImmutableBytesWritable.class, Result.class, job);
    job.setReducerClass(MutationReducer.class);
    FileOutputFormat.setOutputPath(job, new Path(outputDir));
    //job.setOutputFormatClass(NullOutputFormat.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    job.setMapOutputKeyClass(ImmutableBytesWritable.class);
    job.setMapOutputValueClass(ImmutableBytesWritable.class);
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
    if (otherArgs.length < 5) {
      System.err.println("ERROR: Wrong number of parameters: " + args.length);
      System.err.println("Usage: RowCounter <tablename> <family> <label1> <label2> <output_dir>");
      System.exit(-1);
    }
    Job job = createSubmittableJob(conf, otherArgs);
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
  
  // family: "variant", label: "Genome1102T" or Genome1102N
  
}
