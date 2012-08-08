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

import com.esotericsoftware.kryo.KryoException;
import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.util.SGID;
import java.util.Date;
import org.apache.commons.lang.SerializationUtils;

/**
 * This plug-in abstracts plug-ins that use a model manager to keep track of
 * Features that "pass" the mapper
 *
 * @author dyuen
 */
public abstract class AbstractMRHBaseBatchedPlugin extends AbstractMRHBasePlugin<FeatureSet> {
    
    @Override
    public byte[] handleSerialization(Object... parameters) {
        byte[] result = serialParam(parameters);
        return result;
    }
    
    public static Object[] handleDeserialization(byte[] data){
        // why doesn't this work?
        //Kryo kryo = new Kryo();
        //kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        //Input input = new Input(new ByteArrayInputStream(data));
        //Object[] result = (Object[]) kryo.readClassAndObject(input);
        //input.close();
        Object[] result = (Object[]) SerializationUtils.deserialize(data);
        return result;
    }

    private byte[] serialParam(Object ... obj) throws KryoException {
        //        Kryo kryo = new Kryo();
        //        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        //        ByteArrayOutputStream featureBytes = new ByteArrayOutputStream();
        //        Output o = new Output(featureBytes);
        //        kryo.writeClassAndObject(o, obj);
        //        o.close();
        //        return featureBytes.toByteArray();
        byte[] serialize = SerializationUtils.serialize(obj);
        return serialize;
    }
    
    
    @Override
    public FeatureSet variableResult() {
        // after processing, outputSet will actually have been versioned several times, we need the latest one
        FeatureSet latestAtomBySGID = SWQEFactory.getQueryInterface().getLatestAtomBySGID(outputSet.getSGID(), FeatureSet.class);
//        //TODO: remove the need for this hacky hack, there is some versioning issue here
        CreateUpdateManager modelManager = SWQEFactory.getModelManager();
        SGID sgid = latestAtomBySGID.getSGID();
        sgid.setBackendTimestamp(new Date());
        FeatureSet build = latestAtomBySGID.toBuilder().build();
        build.impersonate(sgid, latestAtomBySGID.getSGID());
        build.setPrecedingVersion(build);
        modelManager.persist(build);
        modelManager.close();
        return build;
        //return latestAtomBySGID;
    }
    
}
