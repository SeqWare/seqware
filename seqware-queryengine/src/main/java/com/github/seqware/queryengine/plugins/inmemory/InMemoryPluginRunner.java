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
package com.github.seqware.queryengine.plugins.inmemory;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.plugins.MapReducePlugin;
import com.github.seqware.queryengine.plugins.MapperInterface;
import com.github.seqware.queryengine.plugins.PluginInterface;
import com.github.seqware.queryengine.plugins.PluginRunnerInterface;
import com.github.seqware.queryengine.plugins.ReducerInterface;
import com.github.seqware.queryengine.plugins.hbasemr.MRHBasePluginRunner;
import com.github.seqware.queryengine.util.SGID;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.SerializationUtils;

/**
 * Base class for all in-memory plug-in runners.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public final class InMemoryPluginRunner<ResultType> implements PluginRunnerInterface<ResultType>, MapperInterface, ReducerInterface {

    private PluginInterface pluginInterface;
    private Long counter = 0L;
    private final String[] serializedParameters;
    private Object[] ext_parameters;
    private Object[] int_parameters;
    private FeatureSet sourceSet;
    private FeatureSet destSet;

    public InMemoryPluginRunner(PluginInterface pluginInterface, FeatureSet inputSet, Object[] parameters) {
        this.pluginInterface = pluginInterface;
        CreateUpdateManager manager = SWQEFactory.getModelManager();
        //outputSet should attach to the original reference
        FeatureSet outputSet = manager.buildFeatureSet().setReferenceID(inputSet.getReferenceID()).build();
        manager.close();

        byte[] sSet = SWQEFactory.getSerialization().serialize(inputSet);
        byte[] dSet = SWQEFactory.getSerialization().serialize(outputSet);
        // pretend to serialize parameters 
        this.serializedParameters = MRHBasePluginRunner.serializeParametersToString(parameters, pluginInterface, sSet, dSet);

        // pretend to de-serialize 
        final String externalParameters = serializedParameters[0];
        if (externalParameters != null && !externalParameters.isEmpty()) {
            this.ext_parameters = (Object[]) SerializationUtils.deserialize(Base64.decodeBase64(externalParameters));
        }
        final String internalParameters = serializedParameters[1];
        if (internalParameters != null && !internalParameters.isEmpty()) {
            this.int_parameters = (Object[]) SerializationUtils.deserialize(Base64.decodeBase64(internalParameters));
        }
        final String sourceSetParameter = serializedParameters[2];
        if (sourceSetParameter != null && !sourceSetParameter.isEmpty()) {
            this.sourceSet = SWQEFactory.getSerialization().deserialize(Base64.decodeBase64(sourceSetParameter), FeatureSet.class);
        }
        final String destSetParameter = serializedParameters[3];
        if (destSetParameter != null && !destSetParameter.isEmpty()) {
            this.destSet = SWQEFactory.getSerialization().deserialize(Base64.decodeBase64(destSetParameter), FeatureSet.class);
        }

        // this is not currently asynchronous
        if (pluginInterface instanceof MapReducePlugin) {
            MapReducePlugin mrPlugin = (MapReducePlugin) pluginInterface;

            mrPlugin.mapInit(this);
            List<Feature> features = new ArrayList<Feature>();
            for (Feature f : inputSet) {
                features.add(f);
            }
            mrPlugin.map(features, this);
            mrPlugin.mapCleanup();

            mrPlugin.reduceInit();
            // TODO: make this pass through functional in order to simulate MapReduce
            for (Feature f : inputSet) {
                mrPlugin.reduce(null, null, this);
            }
            mrPlugin.reduceCleanup();
            
            mrPlugin.cleanup();
        } else {
            throw new UnsupportedOperationException("Scan plugins not supported yet");
        }
    }

    @Override
    public ResultType get() {
        if (pluginInterface.getResultMechanism() == PluginInterface.ResultMechanism.COUNTER) {
            return (ResultType) counter;
        } else if (pluginInterface.getResultMechanism() == PluginInterface.ResultMechanism.SGID) {
            SGID resultSGID = this.getDestSet().getSGID();
            Class<? extends Atom> resultClass = (Class<? extends Atom>) pluginInterface.getResultClass();
            return (ResultType) SWQEFactory.getQueryInterface().getLatestAtomBySGID(resultSGID, resultClass);
        } else if (pluginInterface.getResultMechanism() == PluginInterface.ResultMechanism.BATCHEDFEATURESET) {
            FeatureSet build = MRHBasePluginRunner.updateAndGet(this.getDestSet());
            return (ResultType) build;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void incrementCounter() {
        counter++;
    }

    @Override
    public Object[] getExt_parameters() {
        return this.ext_parameters;
    }

    @Override
    public Object[] getInt_parameters() {
        return this.int_parameters;
    }

    @Override
    public void write(Object textKey, Object text) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FeatureSet getSourceSet() {
        return this.sourceSet;
    }

    @Override
    public FeatureSet getDestSet() {
        return this.destSet;
    }

    @Override
    public PluginInterface getPlugin() {
        return this.getPlugin();
    }
}
