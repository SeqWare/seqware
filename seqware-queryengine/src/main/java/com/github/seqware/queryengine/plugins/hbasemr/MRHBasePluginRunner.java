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

import com.github.seqware.queryengine.Constants;
import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.impl.HBaseStorage;
import com.github.seqware.queryengine.impl.SimplePersistentBackEnd;
import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.impl.FeatureList;
import com.github.seqware.queryengine.model.impl.lazy.LazyFeatureSet;
import com.github.seqware.queryengine.plugins.MapReducePlugin;
import com.github.seqware.queryengine.plugins.MapperInterface;
import com.github.seqware.queryengine.plugins.PluginInterface;
import com.github.seqware.queryengine.plugins.PluginRunnerInterface;
import com.github.seqware.queryengine.plugins.ReducerInterface;
import com.github.seqware.queryengine.plugins.plugins.FeatureSetCountPlugin;
import com.github.seqware.queryengine.plugins.plugins.FeaturesByFilterPlugin;
import com.github.seqware.queryengine.plugins.plugins.VCFDumperPlugin;
import com.github.seqware.queryengine.util.SGID;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.SerializationUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.QualifierFilter;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.log4j.Logger;

/**
 * Abstract implementation of an abstract map/reduce-based plug-in runner for a
 * HBase back-end.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public final class MRHBasePluginRunner<ReturnType> implements PluginRunnerInterface<ReturnType> {

    /**
     * Constant
     * <code>INT_PARAMETERS="int_parameters"</code>
     */
    public static final String INT_PARAMETERS = "int_parameters";
    /**
     * Constant
     * <code>EXT_PARAMETERS="ext_parameters"</code>
     */
    public static final String EXT_PARAMETERS = "ext_parameters";
    protected Job job;
    private MapReducePlugin mapReducePlugin;
    private FeatureSet outputSet;

    public MRHBasePluginRunner(MapReducePlugin mapReducePlugin, FeatureSet inputSet, Object... parameters) {
        this.mapReducePlugin = mapReducePlugin;
        try {
            CreateUpdateManager manager = SWQEFactory.getModelManager();
            //outputSet should attach to the original reference
            this.outputSet = manager.buildFeatureSet().setReferenceID(inputSet.getReferenceID()).build();
            manager.close();

            // do setup for Map/Reduce from the HBase API
            String tableName = generateTableName(inputSet);
            String destTableName = generateTableName(outputSet);

            Configuration conf = new Configuration();
            HBaseStorage.configureHBaseConfig(conf);
            HBaseConfiguration.addHbaseResources(conf);

            // we need to pass the parameters for a featureset, maybe we can take advantage of our serializers
            byte[] sSet = SWQEFactory.getSerialization().serialize(inputSet);
            byte[] dSet = SWQEFactory.getSerialization().serialize(outputSet);
            
            String[] str_params = serializeParametersToString(parameters, mapReducePlugin, sSet, dSet);

            File file = new File(new URI(Constants.Term.DEVELOPMENT_DEPENDENCY.getTermValue(String.class)));
            if (file.exists()) {
                conf.setStrings("tmpjars", Constants.Term.DEVELOPMENT_DEPENDENCY.getTermValue(String.class));
            }
            conf.setStrings(EXT_PARAMETERS, str_params);
            conf.set("mapreduce.map.java.opts", "-Xmx4096m  -verbose:gc");
            conf.set("mapreduce.reduce.java.opts", "-Xmx4096m  -verbose:gc");
            conf.set("mapreduce.map.ulimit", "4194304");
            conf.set("mapreduce.reduce.ulimit", "4194304");
            conf.set("mapreduce.map.memory.mb", "4096");
            conf.set("mapreduce.reduce.memory.mb", "4096");
            conf.set("mapreduce.map.memory.physical.mb", "4096");
            conf.set("mapreduce.reduce.memory.physical.mb", "4096");
            // the above settings all seem to be ignored by hboot
            // TODO: only this one works, but as far I know, we're using mapreduce not mapred.
            // Strange
            conf.set("mapred.child.java.opts", "-Xmx2048m -verbose:gc");

            this.job = new Job(conf, mapReducePlugin.getClass().getSimpleName());

            Scan scan = new Scan();
            scan.setMaxVersions();       // we need all version data
            scan.setCaching(500);        // 1 is the default in Scan, which will be bad for MapReduce jobs
            scan.setCacheBlocks(false);  // don't set to true for MR jobs
            byte[] qualiferBytes = Bytes.toBytes(inputSet.getSGID().getUuid().toString());
            scan.addColumn(HBaseStorage.getTEST_FAMILY_INBYTES(), qualiferBytes);
            scan.setFilter(new QualifierFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(qualiferBytes)));

            // handle the part that changes from job to job
            // pluginInterface.performVariableInit(tableName, destTableName, scan);
            TableMapReduceUtil.initTableMapperJob(
                    tableName, // input HBase table name
                    scan, // Scan instance to control CF and attribute selection
                    PluginRunnerMapper.class, // mapper
                    mapReducePlugin.getMapOutputKeyClass(), // mapper output key 
                    mapReducePlugin.getMapOutputValueClass(), // mapper output value
                    job);
            job.setOutputFormatClass(mapReducePlugin.getOutputClass());   // because we aren't emitting anything from mapper
            job.setReducerClass(MRHBasePluginRunner.PluginRunnerReducer.class);    // reducer class
            job.setNumReduceTasks(mapReducePlugin.getNumReduceTasks());

            if (mapReducePlugin.getResultMechanism() == PluginInterface.ResultMechanism.FILE) {
                FileContext fileContext = FileContext.getFileContext(this.job.getConfiguration());
                Path path = new Path("/tmp/" + new BigInteger(20, new SecureRandom()).toString(32) + mapReducePlugin.toString());
                path = fileContext.makeQualified(path);
                TextOutputFormat.setOutputPath(job, path);  // adjust directories as required
            }

            TableMapReduceUtil.addDependencyJars(job);
            job.setJarByClass(MRHBasePluginRunner.class);
            // submit the job, but do not block
            job.submit();
        } catch (URISyntaxException ex) {
            Logger.getLogger(MRHBasePluginRunner.class.getName()).fatal(null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(MRHBasePluginRunner.class.getName()).fatal(null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MRHBasePluginRunner.class.getName()).fatal(null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MRHBasePluginRunner.class.getName()).fatal(null, ex);
        }
    }

    private static String generateTableName(FeatureSet sourceSet) {
        LazyFeatureSet lfSet = (LazyFeatureSet) sourceSet;
        String prefix = lfSet.getTablename();
        String tableName = HBaseStorage.TEST_TABLE_PREFIX + HBaseStorage.SEPARATOR + prefix;
        return tableName;
    }

    @Override
    public ReturnType get() {
        try {
            job.waitForCompletion(true);
            if (mapReducePlugin.getResultMechanism() == PluginInterface.ResultMechanism.COUNTER) {
                return (ReturnType) Long.valueOf(job.getCounters().findCounter(MapperInterface.Counters.ROWS).getValue());
            } else if (mapReducePlugin.getResultMechanism() == PluginInterface.ResultMechanism.SGID) {
                SGID resultSGID = outputSet.getSGID();
                Class<? extends Atom> resultClass = (Class<? extends Atom>) mapReducePlugin.getResultClass();
                return (ReturnType) SWQEFactory.getQueryInterface().getLatestAtomBySGID(resultSGID, resultClass);
            } else if (mapReducePlugin.getResultMechanism() == PluginInterface.ResultMechanism.BATCHEDFEATURESET) {
                FeatureSet build = updateAndGet(outputSet);
                return (ReturnType) build;
            } else {
                throw new UnsupportedOperationException();


            }
        } catch (IOException ex) {
            Logger.getLogger(MRHBasePluginRunner.class
                    .getName()).error(null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(MRHBasePluginRunner.class
                    .getName()).error(null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MRHBasePluginRunner.class
                    .getName()).error(null, ex);
        }
        return null;
    }

    public boolean isComplete() {
        try {
            return job.isComplete();


        } catch (IOException ex) {
            Logger.getLogger(MRHBasePluginRunner.class
                    .getName()).error(null, ex);
        }
        return false;
    }

    @Override
    public PluginInterface getPlugin() {
        return mapReducePlugin;
    }

    public static FeatureSet updateAndGet(FeatureSet outputSet) {
        // after processing, outputSet will actually have been versioned several times, we need the latest one
        FeatureSet latestAtomBySGID = SWQEFactory.getQueryInterface().getLatestAtomBySGID(outputSet.getSGID(), FeatureSet.class);
        //        //TODO: remove the need for this hacky hack, there is some versioning issue here
        CreateUpdateManager modelManager = SWQEFactory.getModelManager();
        SGID sgid = latestAtomBySGID.getSGID();

        sgid.setBackendTimestamp(
                new Date());
        FeatureSet build = latestAtomBySGID.toBuilder().build();

        build.impersonate(sgid, latestAtomBySGID.getSGID());
        build.setPrecedingVersion(build);

        modelManager.persist(build);

        modelManager.close();
        return build;
    }

    public static String[] serializeParametersToString(Object[] parameters, PluginInterface mapReducePlugin, byte[] sSet, byte[] dSet) {
        String[] str_params = new String[6];
        byte[] ext_serials = mapReducePlugin.handleSerialization(parameters);
        byte[] int_serials = mapReducePlugin.handleSerialization(mapReducePlugin.getInternalParameters());
        str_params[0] = Base64.encodeBase64String(ext_serials);
        str_params[1] = Base64.encodeBase64String(int_serials);
        str_params[2] = Base64.encodeBase64String(sSet);
        str_params[3] = Base64.encodeBase64String(dSet);
        str_params[4] = Base64.encodeBase64String(mapReducePlugin.handleSerialization(Constants.getSETTINGS_MAP()));
        str_params[5] = Base64.encodeBase64String(mapReducePlugin.handleSerialization(mapReducePlugin));
        return str_params;
    }

    public static class PluginRunnerReducer<KEYIN, VALUEIN, KEYOUT, VALUEOUT> extends TableReducer<KEYIN, VALUEIN, KEYOUT> implements ReducerInterface<KEYOUT, VALUEOUT> {

        private PluginRunnerReducer.Context context;
        private MapReducePlugin mapReducePlugin;

        @Override
        protected void reduce(KEYIN key, Iterable<VALUEIN> values, Context context) throws IOException, InterruptedException {
            this.context = context;
            mapReducePlugin.reduce(key, values, this);
        }

        @Override
        public void write(KEYOUT keyout, VALUEOUT valueout) {
            try {
                context.write(keyout, valueout);
            } catch (IOException ex) {
                Logger.getLogger(MRHBasePluginRunner.class.getName()).error(null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(MRHBasePluginRunner.class.getName()).error(null, ex);
            }
        }
    }

    public static class PluginRunnerMapper<KEYOUT, VALUEOUT> extends TableMapper<KEYOUT, VALUEOUT> implements MapperInterface<KEYOUT, VALUEOUT> {

        private MapReducePlugin mapReducePlugin;
        private PluginRunnerMapper.Context context;

        @Override
        public void incrementCounter() {
            context.getCounter(Counters.ROWS).increment(1L);
        }

        @Override
        public void write(KEYOUT keyout, VALUEOUT valueout) {
            try {
                context.write(keyout, valueout);
            } catch (IOException ex) {
                Logger.getLogger(MRHBasePluginRunner.class.getName()).error(null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(MRHBasePluginRunner.class.getName()).error(null, ex);
            }
        }

        @Override
        protected void setup(Mapper.Context context) {
            this.baseMapperSetup(context);
            mapReducePlugin.mapInit(this);
        }
        
        @Override
        protected void cleanup(org.apache.hadoop.mapreduce.Mapper.Context context) throws IOException, InterruptedException{
            mapReducePlugin.mapCleanup();
        }

        @Override
        protected void map(ImmutableBytesWritable row, Result values, Mapper.Context context) throws IOException, InterruptedException {
            this.context = context;

            List<FeatureList> list = HBaseStorage.grabFeatureListsGivenRow(values, sourceSet.getSGID(), SWQEFactory.getSerialization());
            Logger.getLogger(FeatureSetCountPlugin.class.getName()).trace("Counting " + sourceSet.getSGID() + " on row with " + list.size() + " lists");
            Collection<Feature> consolidateRow = SimplePersistentBackEnd.consolidateRow(list);
            Logger.getLogger(FeatureSetCountPlugin.class.getName()).trace("Consolidated to  " + consolidateRow.size() + " features");
            mapReducePlugin.map(consolidateRow, this);
        }
        /**
         * parameters that will be usable by the user (the writer of the
         * queries)
         */
        protected Object[] ext_parameters;
        /**
         * parameters that will be handled by the plug-in developer but will not
         * be available to the user of the plug-in
         */
        protected Object[] int_parameters;
        /**
         * the feature set that we will be reading
         */
        protected FeatureSet sourceSet;
        /**
         * the feature set that we will be writing to, may be null
         */
        protected FeatureSet destSet;

        @Override
        public Object[] getExt_parameters() {
            return ext_parameters;
        }

        @Override
        public Object[] getInt_parameters() {
            return int_parameters;
        }

        @Override
        public FeatureSet getSourceSet() {
            return sourceSet;
        }

        @Override
        public FeatureSet getDestSet() {
            return destSet;
        }

        private void baseMapperSetup(Context context) {
            Logger.getLogger(FeatureSetCountPlugin.class.getName()).info("Setting up mapper");
            Configuration conf = context.getConfiguration();
            String[] strings = conf.getStrings(MRHBasePluginRunner.EXT_PARAMETERS);
            Logger.getLogger(PluginRunnerMapper.class.getName()).info("QEMapper configured with: host: " + Constants.Term.HBASE_PROPERTIES.getTermValue(Map.class).toString() + " namespace: " + Constants.Term.NAMESPACE.getTermValue(String.class));
            final String mapParameter = strings[4];
            if (mapParameter != null && !mapParameter.isEmpty()) {
                Map<String, String> settingsMap = (Map<String, String>) ((Object[]) SerializationUtils.deserialize(Base64.decodeBase64(mapParameter)))[0];
                if (settingsMap != null) {
                    Logger.getLogger(FeatureSetCountPlugin.class.getName()).info("Settings map retrieved with " + settingsMap.size() + " entries");
                    Constants.setSETTINGS_MAP(settingsMap);
                }
            }
            Logger.getLogger(PluginRunnerMapper.class.getName()).info("QEMapper configured with: host: " + Constants.Term.HBASE_PROPERTIES.getTermValue(Map.class).toString() + " namespace: " + Constants.Term.NAMESPACE.getTermValue(String.class));
            final String externalParameters = strings[0];
            if (externalParameters != null && !externalParameters.isEmpty()) {
                this.ext_parameters = (Object[]) SerializationUtils.deserialize(Base64.decodeBase64(externalParameters));
            }
            final String internalParameters = strings[1];
            if (internalParameters != null && !internalParameters.isEmpty()) {
                this.int_parameters = (Object[]) SerializationUtils.deserialize(Base64.decodeBase64(internalParameters));
            }
            final String sourceSetParameter = strings[2];
            if (sourceSetParameter != null && !sourceSetParameter.isEmpty()) {
                this.sourceSet = SWQEFactory.getSerialization().deserialize(Base64.decodeBase64(sourceSetParameter), FeatureSet.class);
            }
            final String destSetParameter = strings[3];
            if (destSetParameter != null && !destSetParameter.isEmpty()) {
                this.destSet = SWQEFactory.getSerialization().deserialize(Base64.decodeBase64(destSetParameter), FeatureSet.class);
            }
            final String pluginParameter = strings[5];
            if (pluginParameter != null && !pluginParameter.isEmpty()) {
                Object deserialize = SerializationUtils.deserialize(Base64.decodeBase64(pluginParameter));
                // yuck! I need a cleaner way to do this when done refactoring
                mapReducePlugin = (MapReducePlugin)((Object[])deserialize)[0];
            }
        }
    }

    public File handleFileResult(Path path) {
        FileSystem fs = null;
        try {
            Path outputPartPath = new Path(path, "part-r-00000");
            // copy file from HDFS to local temporary file
            Logger
                    .getLogger(FeaturesByFilterPlugin.class
                    .getName()).info("Source file is " + outputPartPath.toString());
            Configuration conf = new Configuration();

            HBaseStorage.configureHBaseConfig(conf);

            HBaseConfiguration.addHbaseResources(conf);
            fs = FileSystem.get(conf);
            File createTempFile = File.createTempFile("vcf", "out");

            createTempFile.delete();
            Path outPath = new Path(createTempFile.toURI());
            FileSystem localSystem = FileSystem.get(new Configuration());

            Logger.getLogger(FeaturesByFilterPlugin.class
                    .getName()).info("Destination file is " + outPath.toString());
            if (!fs.exists(outputPartPath)) {
                Logger.getLogger(FeaturesByFilterPlugin.class.getName()).fatal("Input file not found");
            }

            if (!fs.isFile(outputPartPath)) {
                Logger.getLogger(FeaturesByFilterPlugin.class.getName()).fatal("Input should be a file");
            }

            if (localSystem.exists(outPath)) {
                Logger.getLogger(FeaturesByFilterPlugin.class.getName()).fatal("Output already exists");
            }
            // doesn't quite work yet, no time to finish before poster, check results manually on hdfs

            FileUtil.copy(fs, outputPartPath, localSystem, outPath,
                    true, true, conf);
            return new File(outPath.toUri());
        } catch (IOException ex) {
            Logger.getLogger(VCFDumperPlugin.class.getName()).fatal(null, ex);
        } finally {
            if (fs != null) {
                try {
                    fs.delete(path, true);
                } catch (IOException ex) {
                    Logger.getLogger(VCFDumperPlugin.class.getName()).warn("IOException when clearing after text output", ex);
                }
            }
        }

        return null;
    }
}
