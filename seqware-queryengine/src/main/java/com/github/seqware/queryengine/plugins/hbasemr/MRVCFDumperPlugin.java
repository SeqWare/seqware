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
package com.github.seqware.queryengine.plugins.hbasemr;

import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.impl.HBaseStorage;
import com.github.seqware.queryengine.impl.SimplePersistentBackEnd;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.impl.FeatureList;
import com.github.seqware.queryengine.system.exporters.VCFDumper;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.SerializationUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.log4j.Logger;

/**
 * This plug-in implements a quick and dirty export using Map/Reduce
 * 
 * TODO: Copy from HDFS and parse key value file to VCF properly.
 *
 * @author dyuen
 */
public class MRVCFDumperPlugin extends AbstractMRHBasePlugin<File> {

    private Path path = new Path("/tmp/" + new BigInteger(20, new SecureRandom()).toString(32) + "testVCFDumper.vcf");

    @Override
    public byte[] handleSerialization(Object... parameters) {
        byte[] serialize = SerializationUtils.serialize(parameters);
        return serialize;
    }

    public static Object[] handleDeserialization(byte[] data) {
        Object[] result = (Object[]) SerializationUtils.deserialize(data);
        return result;
    }

    @Override
    public void performVariableInit(String inputTableName, String outputTableName, Scan scan) {
        try {
            TableMapReduceUtil.initTableMapperJob(
                    inputTableName, // input HBase table name
                    scan, // Scan instance to control CF and attribute selection
                    MRVCFDumperPlugin.PluginMapper.class, // mapper
                    Text.class, // mapper output key 
                    Text.class, // mapper output value
                    job);
            job.setReducerClass(MRVCFDumperPlugin.PluginReducer.class);    // reducer class
            job.setNumReduceTasks(1);    // restrict to one reducer so we have one output file
            FileContext fileContext = FileContext.getFileContext(this.job.getConfiguration());
            path = fileContext.makeQualified(path);
            TextOutputFormat.setOutputPath(job, path);  // adjust directories as required
        } catch (IOException ex) {
            Logger.getLogger(MRFeaturesByFilterPlugin.class.getName()).fatal(null, ex);
        }
    }

    @Override
    public Object[] getInternalParameters() {
        return new Object[0];
    }

    /**
     * Mapper that emits rows of the resulting VCF
     */
    private static class PluginMapper extends QEMapper<Text, Text> {

        private long count = 0;
        private Text text = new Text();
        private Text textKey = new Text();

        @Override
        protected void setup(MRVCFDumperPlugin.PluginMapper.Context context) {
            super.setup(context);
        }

        @Override
        protected void cleanup(MRVCFDumperPlugin.PluginMapper.Context context) {
            Logger.getLogger(MRFeaturesByFilterPlugin.class.getName()).info(new Date().toString() + " cleaning up with total lines: " + count);  
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
        public void map(ImmutableBytesWritable row, Result values, MRVCFDumperPlugin.PluginMapper.Context context) throws IOException, InterruptedException {
            count++;
            List<FeatureList> list = HBaseStorage.grabFeatureListsGivenRow(values, sourceSet.getSGID(), SWQEFactory.getSerialization());
            Collection<Feature> consolidateRow = SimplePersistentBackEnd.consolidateRow(list);
            for (Feature f : consolidateRow) {
                StringBuffer buffer = new StringBuffer();
                VCFDumper.outputFeatureInVCF(buffer, f);
                text.set(buffer.toString());     // we can only emit Writables...
                textKey.set(f.getSGID().getRowKey());
                context.write(textKey, text);
            }
        }
    }

    private static class PluginReducer extends QEReducer<Text, Text, Text, Text> {

        private Text text = new Text();

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            for (Text val : values) {
                context.write(val, text);
            }
        }
    }

    @Override
    public File variableResult() {
        FileSystem fs = null;
        try {
            Path outputPartPath = new Path(path, "part-r-00000");
            // copy file from HDFS to local temporary file
            Logger.getLogger(MRFeaturesByFilterPlugin.class.getName()).info("Source file is " + outputPartPath.toString());
            Configuration conf = new Configuration();
            HBaseStorage.configureHBaseConfig(conf);
            HBaseConfiguration.addHbaseResources(conf);
            fs = FileSystem.get(conf);
            File createTempFile = File.createTempFile("vcf", "out");
            createTempFile.delete();
            Path outPath = new Path(createTempFile.toURI());
            
            FileSystem localSystem = FileSystem.get(new Configuration());
            
            Logger.getLogger(MRFeaturesByFilterPlugin.class.getName()).info("Destination file is " + outPath.toString());
            if (!fs.exists(outputPartPath)) {
                Logger.getLogger(MRFeaturesByFilterPlugin.class.getName()).fatal("Input file not found");
            }
            if (!fs.isFile(outputPartPath)) {
                Logger.getLogger(MRFeaturesByFilterPlugin.class.getName()).fatal("Input should be a file");
            }
            if (localSystem.exists(outPath)) {
                Logger.getLogger(MRFeaturesByFilterPlugin.class.getName()).fatal("Output already exists");
            }
            // doesn't quite work yet, no time to finish before poster, check results manually on hdfs
            FileUtil.copy(fs, outputPartPath, localSystem, outPath, true, true, conf);
            return new File(outPath.toUri());
        } catch (IOException ex) {
            Logger.getLogger(MRVCFDumperPlugin.class.getName()).fatal(null, ex);
        } finally{
            if (fs != null){
                try {
                    fs.delete(path, true);
                } catch (IOException ex) {
                    Logger.getLogger(MRVCFDumperPlugin.class.getName()).warn("IOException when clearing after text output", ex);
                }
            }
        }
        return null;
    }
}
