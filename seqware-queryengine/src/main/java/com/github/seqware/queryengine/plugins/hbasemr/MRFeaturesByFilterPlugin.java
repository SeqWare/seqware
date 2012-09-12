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

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.impl.HBaseStorage;
import com.github.seqware.queryengine.impl.SimplePersistentBackEnd;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.impl.FeatureList;
import com.github.seqware.queryengine.plugins.inmemory.FeatureFilter;
import com.github.seqware.queryengine.system.importers.SOFeatureImporter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

/**
 * Implements the generic queries which independently decide on whether a
 * Feature is included in a result.
 *
 * @author dyuen
 */
public abstract class MRFeaturesByFilterPlugin extends AbstractMRHBaseBatchedPlugin {

    @Override
    public void performVariableInit(String inputTableName, String outputTableName, Scan scan) {
        try {
            TableMapReduceUtil.initTableMapperJob(
                    inputTableName, // input HBase table name
                    scan, // Scan instance to control CF and attribute selection
                    MRFeaturesByFilterPlugin.Mapper.class, // mapper
                    null, // mapper output key 
                    null, // mapper output value
                    job);
            TableMapReduceUtil.initTableReducerJob(
                    outputTableName, // output table
                    null, // reducer class
                    job);
            job.setNumReduceTasks(0);
        } catch (IOException ex) {
            Logger.getLogger(MRFeaturesByFilterPlugin.class.getName()).fatal("IoException in variable init", ex);
        }
    }

    /**
     * Generic Mapper that uses a FeatureFilter.
     */
    private static class Mapper extends QEMapper<ImmutableBytesWritable, Result> {

        private CreateUpdateManager modelManager;
        private FeatureFilter filter;
        private long count = 0;

        @Override
        protected void setup(Mapper.Context context) {
            super.setup(context);
            
            // specific to this kind of plugin
            this.filter = (FeatureFilter) this.int_parameters[0];

            this.modelManager = SWQEFactory.getModelManager();
            this.modelManager.persist(destSet);
        }

        @Override
        protected void cleanup(Mapper.Context context) {
            Logger.getLogger(MRFeaturesByFilterPlugin.class.getName()).info(new Date().toString() + " cleaning up with total lines: " + count);
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
        public void map(ImmutableBytesWritable row, Result values, Mapper.Context context) throws IOException {
            count++;
            List<FeatureList> list = HBaseStorage.grabFeatureListsGivenRow(values, sourceSet.getSGID(), SWQEFactory.getSerialization());
            Collection<Feature> consolidateRow = SimplePersistentBackEnd.consolidateRow(list);
            Collection<Feature> results = new ArrayList<Feature>();
            for (Feature f : consolidateRow) {
                f.setManager(modelManager);
                boolean match = filter.featurePasses(f, this.ext_parameters);
                if (match) {
                    results.add(f);
                }
            }
            destSet.add(results);
            if (count % 1000 == 0) {
                Logger.getLogger(MRFeaturesByFilterPlugin.class.getName()).info(new Date().toString() + " with total lines so far: " + count);
            }
            // TODO: we only seem to be able to keep about a quarter of the number of Features that we can keep in memory in
            // VCF importer, check this out
            if (count % (SOFeatureImporter.BATCH_SIZE/4) == 0) {
                modelManager.flush();
                modelManager.clear();
                modelManager.persist(destSet);
                Logger.getLogger(MRFeaturesByFilterPlugin.class.getName()).info(new Date().toString() + " cleaning up with total lines: " + count);
            }
        } 
    }
}
