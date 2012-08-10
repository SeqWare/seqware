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
import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.impl.HBaseStorage;
import com.github.seqware.queryengine.impl.SimplePersistentBackEnd;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.impl.FeatureList;
import com.github.seqware.queryengine.model.impl.lazy.LazyFeatureSet;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Experiment with Map/Reduce in order to implement MapReduceFeaturesAll.
 *
 * TODO: This will need lots of refactoring once I figure out what is minimally
 * needed for supporting our M/R interface.
 *
 * @author dyuen
 */
public class MapReduceFeaturesAll {

    private static final String PARAMETERS = "paramaters";

    /**
     * Perform the map reduce and append results to destSet
     *
     * @param sourceSet
     * @param destSet
     */
    public void processForFeatureSet(FeatureSet sourceSet, FeatureSet destSet) {
        try {
            Logger rootLogger = Logger.getRootLogger();
            Level previousLevel = rootLogger.getLevel();
            String tableName = generateTableName(sourceSet);
            String destTableName = generateTableName(destSet);

            Configuration conf = HBaseConfiguration.create();
            HBaseStorage.configureHBaseConfig(conf);

            // we need to pass the parameters for a featureset, maybe we can take advantage of our serializers
            byte[] sSet = SWQEFactory.getSerialization().serialize(sourceSet);
            byte[] dSet = SWQEFactory.getSerialization().serialize(destSet);

            conf.setStrings(PARAMETERS, Base64.encodeBase64String(sSet), Base64.encodeBase64String(dSet));

            Job job = new Job(conf, "MapReduceFeaturesAll");

            Scan scan = new Scan();
            scan.setMaxVersions();       // we need all version data
            scan.setCaching(500);        // 1 is the default in Scan, which will be bad for MapReduce jobs
            scan.setCacheBlocks(false);  // don't set to true for MR jobs

            TableMapReduceUtil.initTableMapperJob(
                    tableName, // input HBase table name
                    scan, // Scan instance to control CF and attribute selection
                    MapReduceFeaturesAll.FeaturesByAllMapper.class, // mapper
                    null, // mapper output key 
                    null, // mapper output value
                    job);
            TableMapReduceUtil.initTableReducerJob(
                    destTableName, // output table
                    null, // reducer class
                    job);
            job.setNumReduceTasks(0);

            job.setJarByClass(MapReduceFeaturesAll.class);
            TableMapReduceUtil.addDependencyJars(job);

            boolean b = job.waitForCompletion(true);
            if (!b) {
                throw new IOException("error with job!");
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(MapReduceFeaturesAll.class.getName()).log(Level.FATAL, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MapReduceFeaturesAll.class.getName()).log(Level.FATAL, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MapReduceFeaturesAll.class.getName()).log(Level.FATAL, null, ex);
        }
    }

    private static String generateTableName(FeatureSet sourceSet) {
        LazyFeatureSet lfSet = (LazyFeatureSet) sourceSet;
        String prefix = lfSet.getTablename();
        String tableName = HBaseStorage.TEST_TABLE_PREFIX + HBaseStorage.SEPARATOR + prefix;
        return tableName;
    }

    /**
     * Mapper that runs the count.
     */
    static class FeaturesByAllMapper
            extends TableMapper<ImmutableBytesWritable, Result> {

        private FeatureSet sourceSet;
        private FeatureSet destSet;
        private CreateUpdateManager modelManager;

        @Override
        protected void setup(Mapper.Context context) {

            Configuration conf = context.getConfiguration();
            String[] strings = conf.getStrings(PARAMETERS);
            this.sourceSet = SWQEFactory.getSerialization().deserialize(Base64.decodeBase64(strings[0]), FeatureSet.class);
            this.destSet = SWQEFactory.getSerialization().deserialize(Base64.decodeBase64(strings[1]), FeatureSet.class);

            this.modelManager = SWQEFactory.getModelManager();
            this.modelManager.persist(destSet);
        }

        @Override
        protected void cleanup(Mapper.Context context) {
            this.modelManager.close();
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
            List<FeatureList> list = HBaseStorage.grabFeatureListsGivenRow(values, sourceSet.getSGID(), SWQEFactory.getSerialization());
            Collection<Feature> consolidateRow = SimplePersistentBackEnd.consolidateRow(list);
            for(Feature f : consolidateRow){
                f.setManager(modelManager);
            }
            destSet.add(consolidateRow);
        }
    }
}
