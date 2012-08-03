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

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Output;
import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.impl.HBaseStorage;
import com.github.seqware.queryengine.impl.SimplePersistentBackEnd;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.QueryInterface.Location;
import com.github.seqware.queryengine.model.impl.FeatureList;
import com.github.seqware.queryengine.plugins.inmemory.InMemoryFeaturesByRangePlugin;
import com.github.seqware.queryengine.plugins.inmemory.InMemoryFeaturesByTagPlugin;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Implements the tag queries
 *
 * @author dyuen
 */
public class MRFeaturesByTagsPlugin extends AbstractMRHBaseBatchedPlugin {

    @Override
    public void performVariableInit(String inputTableName, String outputTableName, Scan scan) {
        try {
            TableMapReduceUtil.initTableMapperJob(
                    inputTableName, // input HBase table name
                    scan, // Scan instance to control CF and attribute selection
                    MRFeaturesByTagsPlugin.Mapper.class, // mapper
                    null, // mapper output key 
                    null, // mapper output value
                    job);
            TableMapReduceUtil.initTableReducerJob(
                    outputTableName, // output table
                    null, // reducer class
                    job);
            job.setNumReduceTasks(0);
        } catch (IOException ex) {
            Logger.getLogger(MRFeaturesByTagsPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Mapper that runs the count.
     */
    private static class Mapper
            extends TableMapper<ImmutableBytesWritable, Result> {

        private FeatureSet sourceSet;
        private FeatureSet destSet;
        private CreateUpdateManager modelManager;
        private String subject;
        private String predicate;
        private String object;

        @Override
        protected void setup(Mapper.Context context) {

            Configuration conf = context.getConfiguration();
            String[] strings = conf.getStrings(PARAMETERS);
            this.sourceSet = SWQEFactory.getSerialization().deserialize(Base64.decodeBase64(strings[1]), FeatureSet.class);
            this.destSet = SWQEFactory.getSerialization().deserialize(Base64.decodeBase64(strings[2]), FeatureSet.class);
            byte[] otherParam =  Base64.decodeBase64(strings[0]);
            Object[] parameters = AbstractMRHBaseBatchedPlugin.handleDeserialization(otherParam);
            this.subject = (String) parameters[0];
            this.predicate = (String) parameters[1];
            this.object = (String) parameters[2];

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
        public void map(ImmutableBytesWritable row, Result values, Mapper.Context context) throws IOException {
            List<FeatureList> list = HBaseStorage.grabFeatureListsGivenRow(values, sourceSet.getSGID(), SWQEFactory.getSerialization());
            Collection<Feature> consolidateRow = SimplePersistentBackEnd.consolidateRow(list);
            Collection<Feature> results = new ArrayList<Feature>();
            for (Feature f : consolidateRow) {
                f.setManager(modelManager);
                boolean match = InMemoryFeaturesByTagPlugin.matchFeatureByTags(f, subject, predicate, object);
                if (match) {
                    results.add(f);
                }
            }
            destSet.add(results);
        }
    }
}
