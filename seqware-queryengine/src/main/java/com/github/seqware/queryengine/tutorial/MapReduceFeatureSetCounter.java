/*
 * Copyright (C) 2012 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.seqware.queryengine.tutorial;

import com.github.seqware.queryengine.Constants;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.impl.HBaseStorage;
import com.github.seqware.queryengine.impl.SimplePersistentBackEnd;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.impl.FeatureList;
import com.github.seqware.queryengine.model.impl.lazy.LazyFeatureSet;
import com.github.seqware.queryengine.util.SGID;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Experiment with Map/Reduce in order to count the number of features in a lazy
 * feature set. 
 * 
 * TODO: This will need lots of refactoring once I figure out what is minimally needed 
 * for supporting our M/R interface. 
 *
 * @author dyuen
 */
public class MapReduceFeatureSetCounter {
    private static final String TIMESTAMP_PARAM = "timestamp";
    private static final String UUID_PARAM = "uuid";
    
    public static Long getCountForFeatureSet(FeatureSet fSet) {
        try {
            Logger rootLogger = Logger.getRootLogger();
            Level previousLevel = rootLogger.getLevel();
            
            LazyFeatureSet lfSet = (LazyFeatureSet) fSet;
            String prefix = lfSet.getTablename();
            String tableName = HBaseStorage.TEST_TABLE_PREFIX + HBaseStorage.SEPARATOR + prefix;
        
            Configuration conf = HBaseConfiguration.create();
            HBaseStorage.configureHBaseConfig(conf);
            
            // we need to pass the parameters for a featureset
            conf.set(UUID_PARAM, fSet.getSGID().getUuid().toString());
            conf.setLong(TIMESTAMP_PARAM, fSet.getSGID().getBackendTimestamp().getTime());
                        
            Job job = new Job(conf, "MapReduceFeatureSetCounter");
            
            Scan scan = new Scan();
            scan.setMaxVersions();       // we need all version data
            scan.setCaching(500);        // 1 is the default in Scan, which will be bad for MapReduce jobs
            scan.setCacheBlocks(false);  // don't set to true for MR jobs

            TableMapReduceUtil.initTableMapperJob(
                    tableName, // input HBase table name
                    scan, // Scan instance to control CF and attribute selection
                    MapReduceFeatureSetCounter.RowCounterMapper.class, // mapper
                    null, // mapper output key 
                    null, // mapper output value
                    job);
            job.setOutputFormatClass(NullOutputFormat.class);   // because we aren't emitting anything from mapper
            job.setJarByClass(MapReduceFeatureSetCounter.class);
            TableMapReduceUtil.addDependencyJars(job);
                
            boolean b = job.waitForCompletion(true);
            if (!b) {
                throw new IOException("error with job!");
            }
            
            return job.getCounters().findCounter(MapReduceTest_RowCounter.RowCounterMapper.Counters.ROWS).getValue();
        } catch (InterruptedException ex) {
            Logger.getLogger(MapReduceFeatureSetCounter.class.getName()).log(Level.FATAL, null, ex);
            return Long.MIN_VALUE;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MapReduceFeatureSetCounter.class.getName()).log(Level.FATAL, null, ex);
            return Long.MIN_VALUE;
        } catch (IOException ex) {
            Logger.getLogger(MapReduceFeatureSetCounter.class.getName()).log(Level.FATAL, null, ex);
            return Long.MIN_VALUE;
        }
    }

    /**
     * Mapper that runs the count.
     */
    static class RowCounterMapper
            extends TableMapper<ImmutableBytesWritable, Result> {

        /**
         * Counter enumeration to count the actual rows.
         */
        public static enum Counters {
            ROWS
        }

        /**
         * Maps the data.
         *
         * @param row The current table row key.
         * @param values The columns.
         * @param context The current context.
         * @throws IOException When something is broken with the data.
         * @see org.apache.hadoop.mapreduce.Mapper#map(KEYIN, VALUEIN,
         * org.apache.hadoop.mapreduce.Mapper.Context)
         */
        @Override
        public void map(ImmutableBytesWritable row, Result values,
                Mapper.Context context)
                throws IOException {
            
            Configuration conf = context.getConfiguration();
            UUID uuid = UUID.fromString(conf.get(UUID_PARAM));
            long time = conf.getLong(TIMESTAMP_PARAM, Integer.MIN_VALUE);
            SGID sgid = new SGID(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits(), time);
            
            List<FeatureList> list = HBaseStorage.grabFeatureListsGivenRow(values, sgid, SWQEFactory.getSerialization());
            Collection<Feature> consolidateRow = SimplePersistentBackEnd.consolidateRow(list);
            context.getCounter(MapReduceTest_RowCounter.RowCounterMapper.Counters.ROWS).increment(consolidateRow.size());
        }
    }

    /**
     * Main entry point.
     *
     * @param args The command line parameters.
     * @throws Exception When running the job fails.
     */
    public static void main(String[] args) throws Exception {
        UUID uuid = UUID.fromString("fe44eda6-ada7-4bba-ad3d-57d22a2d2e76");
        SGID sgid = new SGID(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits(), 0);
        FeatureSet fSet = SWQEFactory.getQueryInterface().getLatestAtomBySGID(sgid, FeatureSet.class);
        System.out.println("Counted " + MapReduceFeatureSetCounter.getCountForFeatureSet(fSet)+ " features in " + uuid.toString());
    }
}
