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
import com.github.seqware.queryengine.model.FeatureSet;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.log4j.Logger;

/**
 * Base mapper class used by all Query Engine plug-ins.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public abstract class QEMapper<KEYOUT, VALUEOUT> extends TableMapper<KEYOUT, VALUEOUT> {
    
    /** {@inheritDoc} */
    @Override
    protected void setup(Context context) {
        this.baseMapperSetup(context);
    }
    /**
     * parameters that will be usable by the user (the writer of the queries)
     */
    protected Object[] ext_parameters;
    /**
     * parameters that will be handled by the plug-in developer but will not be
     * available to the user
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
    
    private void baseMapperSetup(Context context) {
        Logger.getLogger(MRFeatureSetCountPlugin.class.getName()).info("Setting up mapper");
        Configuration conf = context.getConfiguration();
        String[] strings = conf.getStrings(AbstractMRHBaseBatchedPlugin.EXT_PARAMETERS);
        Logger.getLogger(QEMapper.class.getName()).info("QEMapper configured with: host: " + Constants.Term.HBASE_PROPERTIES.getTermValue(Map.class).toString() + " namespace: " +  Constants.Term.NAMESPACE.getTermValue(String.class));
        final String mapParameter = strings[4];
        if (mapParameter != null && !mapParameter.isEmpty()){
            Map<String, String> settingsMap = (Map<String, String>) AbstractMRHBaseBatchedPlugin.handleDeserialization(Base64.decodeBase64(mapParameter))[0];
            if (settingsMap != null){
                Logger.getLogger(MRFeatureSetCountPlugin.class.getName()).info("Settings map retrieved with " + settingsMap.size() + " entries");
                Constants.setSETTINGS_MAP(settingsMap);
            }
        }
        Logger.getLogger(QEMapper.class.getName()).info("QEMapper configured with: host: " + Constants.Term.HBASE_PROPERTIES.getTermValue(Map.class).toString() + " namespace: " +  Constants.Term.NAMESPACE.getTermValue(String.class));
        final String externalParameters = strings[0];
        if (externalParameters != null && !externalParameters.isEmpty()){
            this.ext_parameters = AbstractMRHBaseBatchedPlugin.handleDeserialization(Base64.decodeBase64(externalParameters));
        }
        final String internalParameters = strings[1];
        if (internalParameters != null && !internalParameters.isEmpty()){
            this.int_parameters = AbstractMRHBaseBatchedPlugin.handleDeserialization(Base64.decodeBase64(internalParameters));
        }
        final String sourceSetParameter = strings[2];
        if (sourceSetParameter != null && !sourceSetParameter.isEmpty()){
            this.sourceSet = SWQEFactory.getSerialization().deserialize(Base64.decodeBase64(sourceSetParameter), FeatureSet.class);
        }
        final String destSetParameter = strings[3];
        if (destSetParameter != null && !destSetParameter.isEmpty()){
            this.destSet = SWQEFactory.getSerialization().deserialize(Base64.decodeBase64(destSetParameter), FeatureSet.class);
        }
    }
}
