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
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;

/**
 *
 * @author dyuen
 */
public class MyReadJob {

    public static void main(String[] args) throws Exception {
        Configuration config = HBaseConfiguration.create();
        HBaseStorage.configureHBaseConfig(config);
        Job job = new Job(config, "ExampleRead");
        job.setJarByClass(MyReadJob.class);     // class that contains mapper

        Scan scan = new Scan();
        scan.setCaching(500);        // 1 is the default in Scan, which will be bad for MapReduce jobs
        scan.setCacheBlocks(false);  // don't set to true for MR jobs
// set other scan attrs

        TableMapReduceUtil.initTableMapperJob(
                "dyuen_hbaseTestTable_Feature", // input HBase table name
                scan, // Scan instance to control CF and attribute selection
                MyMapper.class, // mapper
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
