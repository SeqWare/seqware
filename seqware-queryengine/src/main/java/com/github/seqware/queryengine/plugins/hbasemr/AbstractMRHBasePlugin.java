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
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.impl.lazy.LazyFeatureSet;
import com.github.seqware.queryengine.plugins.AnalysisPluginInterface;
import com.github.seqware.queryengine.plugins.MapReducePlugin;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.QualifierFilter;
import org.apache.hadoop.hbase.filter.WritableByteArrayComparable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.apache.log4j.Logger;

/**
 * Abstract implementation of an abstract map/reduce-based plug-in for a HBase
 * back-end. Have not figured out how to reconcile an interface that defines
 * map/reduce operations with the HBase map/reduce signatures.
 *
 * @author dyuen
 */
public abstract class AbstractMRHBasePlugin<ReturnType> implements MapReducePlugin<Feature, FeatureSet> {

    protected FeatureSet outputSet;
    private CreateUpdateManager manager;
    public static final String INT_PARAMETERS = "int_parameters";
    public static final String EXT_PARAMETERS = "ext_parameters";
    protected Job job;
    
    /**
     * Internal parameters that can be used to pass information to the
     * Mapper and Reducers.
     * @return 
     */
    public abstract Object[] getInternalParameters(); 

    @Override
    public AnalysisPluginInterface.ReturnValue init(FeatureSet inputSet, Object... parameters) {
        try {
            this.manager = SWQEFactory.getModelManager();
            //outputSet should attach to the original reference
            outputSet = manager.buildFeatureSet().setReferenceID(inputSet.getReferenceID()).build();       
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

            String[] str_params = new String[4];
            byte[] ext_serials = this.handleSerialization(parameters);
            byte[] int_serials = this.handleSerialization(this.getInternalParameters());
            str_params[0] = Base64.encodeBase64String(ext_serials);
            str_params[1] = Base64.encodeBase64String(int_serials);
            str_params[2] = Base64.encodeBase64String(sSet);
            str_params[3] = Base64.encodeBase64String(dSet);
            
            File file = new File(new URI(Constants.DEVELOPMENT_DEPENDENCY));
            if (file.exists()){
                conf.setStrings("tmpjars", Constants.DEVELOPMENT_DEPENDENCY);
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

            this.job = new Job(conf, this.getClass().getSimpleName());
            
            Scan scan = new Scan();
            scan.setMaxVersions();       // we need all version data
            scan.setCaching(500);        // 1 is the default in Scan, which will be bad for MapReduce jobs
            scan.setCacheBlocks(false);  // don't set to true for MR jobs
            byte[] qualiferBytes = Bytes.toBytes(inputSet.getSGID().getUuid().toString());
            scan.addColumn(HBaseStorage.TEST_FAMILY_INBYTES, qualiferBytes);
            scan.setFilter(new QualifierFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(qualiferBytes)));

            // handle the part that changes from job to job
            performVariableInit(tableName, destTableName, scan);
            
            TableMapReduceUtil.addDependencyJars(job);
            job.setJarByClass(AbstractMRHBasePlugin.class);
            // submit the job, but do not block
            job.submit();
            return new AnalysisPluginInterface.ReturnValue();
        } catch (URISyntaxException ex) {
            java.util.logging.Logger.getLogger(AbstractMRHBasePlugin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(AbstractMRHBasePlugin.class.getName()).fatal(java.util.logging.Level.SEVERE, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AbstractMRHBasePlugin.class.getName()).fatal(java.util.logging.Level.SEVERE, ex);
        } catch (IOException ex) {
            Logger.getLogger(AbstractMRHBasePlugin.class.getName()).fatal(java.util.logging.Level.SEVERE, ex);
        }
        return null;
    }

    public abstract byte[] handleSerialization(Object... parameters);
    
    private static String generateTableName(FeatureSet sourceSet) {
        LazyFeatureSet lfSet = (LazyFeatureSet) sourceSet;
        String prefix = lfSet.getTablename();
        String tableName = HBaseStorage.TEST_TABLE_PREFIX + HBaseStorage.SEPARATOR + prefix;
        return tableName;
    }

    @Override
    public AnalysisPluginInterface.ReturnValue test() {
        /**
         * do nothing
         */
        return null;
    }

    @Override
    public AnalysisPluginInterface.ReturnValue verifyParameters() {
        /**
         * do nothing
         */
        return null;
    }

    @Override
    public AnalysisPluginInterface.ReturnValue verifyInput() {
        /**
         * do nothing
         */
        return null;
    }

    @Override
    public AnalysisPluginInterface.ReturnValue filterInit() {
        /**
         * do nothing
         */
        return null;
    }

    @Override
    public AnalysisPluginInterface.ReturnValue filter() {
        /**
         * do nothing
         */
        return null;
    }

    @Override
    public AnalysisPluginInterface.ReturnValue mapInit() {
        /**
         * do nothing
         */
        return null;
    }

    @Override
    public AnalysisPluginInterface.ReturnValue reduceInit() {
        /**
         * do nothing
         */
        return null;
    }

    @Override
    public AnalysisPluginInterface.ReturnValue verifyOutput() {
        /**
         * do nothing
         */
        return null;
    }

    @Override
    public AnalysisPluginInterface.ReturnValue cleanup() {
        /**
         * do nothing
         */
        return null;
    }

    @Override
    public ReturnType getFinalResult() {
        try {
            boolean b = job.waitForCompletion(true);
            return variableResult();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(AbstractMRHBasePlugin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(AbstractMRHBasePlugin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AbstractMRHBasePlugin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public ReturnValue map(Feature atom, FeatureSet mappedSet) {
        // doesn't really do anything
        return new AnalysisPluginInterface.ReturnValue();
    }

    @Override
    public ReturnValue reduce(FeatureSet mappedSet, FeatureSet resultSet) {
        // doesn't really do anything
        return new AnalysisPluginInterface.ReturnValue();
    }

    @Override
    public boolean isComplete() {
        try {
            return job.isComplete();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(AbstractMRHBasePlugin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * This should be overridden by implementing classes with code that changes
     * from map/reduce to map/reduce
     */
    public abstract void performVariableInit(String inputTableName, String outputTableName, Scan scan);

    /**
     * This should be overridden with the result.
     *
     * @return
     */
    public abstract ReturnType variableResult();
}
