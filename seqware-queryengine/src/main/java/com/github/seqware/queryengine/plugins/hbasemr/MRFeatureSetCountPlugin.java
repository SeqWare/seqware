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
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.impl.HBaseStorage;
import com.github.seqware.queryengine.impl.SimplePersistentBackEnd;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.impl.FeatureList;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.SerializationUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.log4j.Logger;

/**
 * Counts the number of Features in a FeatureSet
 *
 * @author dyuen
 */
public class MRFeatureSetCountPlugin extends AbstractMRHBasePlugin<Long> {

    @Override
    public void performVariableInit(String inputTableName, String destTableName, Scan scan) {
        try {
            TableMapReduceUtil.initTableMapperJob(
                    inputTableName, // input HBase table name
                    scan, // Scan instance to control CF and attribute selection
                    MRFeatureSetCountPlugin.RowCounterMapper.class, // mapper
                    null, // mapper output key 
                    null, // mapper output value
                    job);
            job.setOutputFormatClass(NullOutputFormat.class);   // because we aren't emitting anything from mapper
        } catch (IOException ex) {
            Logger.getLogger(MRFeatureSetCountPlugin.class.getName()).fatal(null, ex);
        }
    }

    @Override
    public Long variableResult() {
        try {
            long result = job.getCounters().findCounter(MRFeatureSetCountPlugin.RowCounterMapper.Counters.ROWS).getValue();
            return result;
        } catch (IOException ex) {
            Logger.getLogger(MRFeatureSetCountPlugin.class.getName()).fatal(null, ex);
        }
        return null;
    }

    @Override
    public byte[] handleSerialization(Object... parameters) {
        byte[] serialize = SerializationUtils.serialize(parameters);
        return serialize;
    }

    @Override
    public Object[] getInternalParameters() {
        return new Object[0];
    }

    private static class RowCounterMapper
            extends TableMapper<ImmutableBytesWritable, Result> {

        private FeatureSet sourceSet;
        private FeatureSet destSet;

        /**
         * Counter enumeration to count the actual rows.
         */
        public static enum Counters {

            ROWS
        }

        @Override
        protected void setup(Mapper.Context context) {
            Configuration conf = context.getConfiguration();
            String[] strings = conf.getStrings(EXT_PARAMETERS);
            Map<String,String> settingsMap = (Map<String,String>) AbstractMRHBaseBatchedPlugin.handleDeserialization(Base64.decodeBase64(strings[4]))[0];
            Logger.getLogger(MRFeatureSetCountPlugin.class.getName()).info("Settings map retrieved with  " + settingsMap.size() + " entries");
            Constants.setSETTINGS_MAP(settingsMap);
            this.sourceSet = SWQEFactory.getSerialization().deserialize(Base64.decodeBase64(strings[2]), FeatureSet.class);
            //this.destSet = SWQEFactory.getSerialization().deserialize(Base64.decodeBase64(strings[3]), FeatureSet.class);
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
            Logger.getLogger(MRFeatureSetCountPlugin.class.getName()).trace("Counting " + sourceSet.getSGID() + " on row with " + list.size() + " lists");
            Collection<Feature> consolidateRow = SimplePersistentBackEnd.consolidateRow(list);
            Logger.getLogger(MRFeatureSetCountPlugin.class.getName()).trace("Consolidated to  " + consolidateRow.size() + " features");
            for (Feature f : consolidateRow) {
                // why can't I increment this by the size directly on the cluster?
                context.getCounter(MRFeatureSetCountPlugin.RowCounterMapper.Counters.ROWS).increment(1);
            }
        }
    }
}
