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

import com.github.seqware.queryengine.impl.HBaseStorage;
import java.io.IOException;
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

/**
 * Test a MapReduceTest based on the RowCounter tutorial in HBase
 *
 * @author dyuen
 */
public class MapReduceTest_RowCounter {

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
            // Count every row containing data, whether it's in qualifiers or values
            context.getCounter(MapReduceTest_RowCounter.RowCounterMapper.Counters.ROWS).increment(1);
        }
    }

    /**
     * Main entry point.
     *
     * @param args The command line parameters.
     * @throws Exception When running the job fails.
     */
    public static void main(String[] args) throws Exception {
        Configuration conf = HBaseConfiguration.create();
        HBaseStorage.configureHBaseConfig(conf);
        Job job = new Job(conf, "MapReduceTest");
        job.setJarByClass(MapReduceTest_RowCounter.class);

        Scan scan = new Scan();
        scan.setCaching(500);        // 1 is the default in Scan, which will be bad for MapReduce jobs
        scan.setCacheBlocks(false);  // don't set to true for MR jobs

        TableMapReduceUtil.initTableMapperJob(
                "dyuen.hbaseTestTable.Feature.dummy_ref", // input HBase table name
                scan, // Scan instance to control CF and attribute selection
                MapReduceTest_RowCounter.RowCounterMapper.class, // mapper
                null, // mapper output key 
                null, // mapper output value
                job);
        job.setOutputFormatClass(NullOutputFormat.class);   // because we aren't emitting anything from mapper

        boolean b = job.waitForCompletion(true);
        if (!b) {
            throw new IOException("error with job!");
        }
    }
}
