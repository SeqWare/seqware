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
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.impl.FeatureList;
import com.github.seqware.queryengine.system.exporters.VCFDumper;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.SerializationUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
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
        byte[] result = serialParam(parameters);
        return result;
    }

    public static Object[] handleDeserialization(byte[] data) {
        Object[] result = (Object[]) SerializationUtils.deserialize(data);
        return result;
    }

    private byte[] serialParam(Object... obj) {
        byte[] serialize = SerializationUtils.serialize(obj);
        return serialize;
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
            job.setNumReduceTasks(1);    // at least one, adjust as required
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
    private static class PluginMapper extends TableMapper<Text, Text> {

        private FeatureSet sourceSet;
        private FeatureSet destSet;
        private Object[] ext_parameters;
        private Object[] int_parameters;
        private long count = 0;
        private Text text = new Text();
        private Text textKey = new Text();

        @Override
        protected void setup(MRVCFDumperPlugin.PluginMapper.Context context) {

            Configuration conf = context.getConfiguration();
            String[] strings = conf.getStrings(EXT_PARAMETERS);
            this.ext_parameters = AbstractMRHBaseBatchedPlugin.handleDeserialization(Base64.decodeBase64(strings[0]));
            this.int_parameters = AbstractMRHBaseBatchedPlugin.handleDeserialization(Base64.decodeBase64(strings[1]));
            this.sourceSet = SWQEFactory.getSerialization().deserialize(Base64.decodeBase64(strings[2]), FeatureSet.class);
            this.destSet = SWQEFactory.getSerialization().deserialize(Base64.decodeBase64(strings[3]), FeatureSet.class);

            // specific to this kind of plugin
            //this.filter = (FeatureFilter) this.int_parameters[0];
        }

        @Override
        protected void cleanup(MRVCFDumperPlugin.PluginMapper.Context context) {
            System.out.println(new Date().toString() + " cleaning up with total lines: " + count);
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
            Collection<Feature> results = new ArrayList<Feature>();
            for (Feature f : consolidateRow) {
                StringBuffer buffer = new StringBuffer();
                VCFDumper.outputFeatureInVCF(buffer, f);
                text.set(buffer.toString());     // we can only emit Writables...
                textKey.set(f.getSGID().getRowKey());
                context.write(textKey, text);
            }
        }
    }

    private static class PluginReducer extends Reducer<Text, Text, Text, Text> {
        

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            for (Text val : values) {
                context.write(key, val);
            }
        }
    }

    @Override
    public File variableResult() {
        try {
            // copy file from HDFS to local temporary file
            Logger.getLogger(MRFeaturesByFilterPlugin.class.getName()).info("Source file is " + path.toString());
            Configuration conf = new Configuration();
            HBaseStorage.configureHBaseConfig(conf);
            HBaseConfiguration.addHbaseResources(conf);
            FileSystem fs = FileSystem.get(conf);
            Path outPath = new Path(File.createTempFile("vcf", "out").toURI());
            
            FileSystem localSystem = FileSystem.get(new Configuration());
            
            Logger.getLogger(MRFeaturesByFilterPlugin.class.getName()).info("Destination file is " + outPath.toString());
            if (!fs.exists(path)) {
                Logger.getLogger(MRFeaturesByFilterPlugin.class.getName()).fatal("Input file not found");
            }
            if (!fs.isFile(path)) {
                Logger.getLogger(MRFeaturesByFilterPlugin.class.getName()).fatal("Input should be a file");
            }
            if (localSystem.exists(outPath)) {
                Logger.getLogger(MRFeaturesByFilterPlugin.class.getName()).fatal("Output already exists");
            }
            // doesn't quite work yet, no time to finish before poster, check results manually on hdfs
            FileUtil.copy(fs, path, localSystem, outPath, true, true, conf);
            return new File(outPath.toUri());

        } catch (IOException ex) {
            Logger.getLogger(MRVCFDumperPlugin.class.getName()).fatal(null, ex);
        }
        return null;
    }
}
