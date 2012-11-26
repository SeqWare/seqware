package com.github.seqware.queryengine.plugins;

import com.github.seqware.queryengine.model.Feature;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.util.SGID;
import java.util.Collection;
import org.apache.commons.lang.SerializationUtils;

/**
 * An abstracted map-reduce interface. These interfaces will eventually restrict
 * our plug-ins. Currently, they are just place-holders.
 *
 * Implementation orients itself on HBase's TableMapper, TableReduce.
 *
 * @author jbaran
 * @version $Id: $Id
 */
public abstract class MapReducePlugin<MAPKEYOUT, MAPVALUEOUT, REDUCEKEYIN, REDUCEVALUEIN, REDUCEKEYOUT, REDUCEVALUEOUT, RESULT> implements PluginInterface {

    /**
     * Mapping implementation that singles out desired atoms into a mapped set.
     *
     * @param atom Atom that is to be either dropped, or added to mappedSet.
     * @param mappedSet Set of atoms that are passed to the reduce
     * implementation.
     * @return a ReturnValue object.
     */
    public abstract void map(Collection<Feature> atom, MapperInterface<MAPKEYOUT, MAPVALUEOUT> mapperInterface);

    /**
     * Reduce implementation that takes mapped atoms and processes them.
     *
     * @param reduceKey Atoms that were selected during the mapping step.
     * @param reduceValues Atoms that are created as a result of the reduce
     * step.
     * @return a ReturnValue object.
     */
    public abstract void reduce(REDUCEKEYIN reduceKey, Iterable<REDUCEVALUEIN> reduceValues, ReducerInterface<REDUCEKEYOUT, REDUCEVALUEOUT> reducerInterface);

    @Override
    public Object[] getInternalParameters(){
        return null; /** do nothing */
    }
    
    /**
     * <p>reduceInit.</p>
     *
     * @return a ReturnValue object.
     */
    public void reduceInit() {
        /**
         * empty method that can be overridden
         */
    }

    /**
     * <p>mapInit.</p>
     *
     * @return a ReturnValue object.
     */
    public void mapInit(MapperInterface mapperInterface) {
        /**
         * empty method that can be overridden
         */
    }

    /** {@inheritDoc} */
    @Override
    public byte[] handleSerialization(Object... parameters) {
        byte[] serialize = SerializationUtils.serialize(parameters);
        return serialize;
    }
    
    /** {@inheritDoc} */
     @Override
    public Object[] handleDeserialization(byte[] data){
        Object[] result = (Object[]) SerializationUtils.deserialize(data);
        return result;
    }
    
    /**
     * <p>mapperCleanup.</p>
     *
     * @return a ReturnValue object.
     */
    public void mapCleanup() {
        /**
         * empty method that can be overridden
         */
    }

    /**
     * <p>reducerCleanup.</p>
     *
     * @return a ReturnValue object.
     */
    public void reduceCleanup() {
        /**
         * empty method that can be overridden
         */
    }

    @Override
    public ReturnValue test() {
        /**
         * empty method that can be overridden
         */
        return new ReturnValue();
    }

    @Override
    public ReturnValue verifyParameters() {
        /**
         * empty method that can be overridden
         */
        return new ReturnValue();
    }

    @Override
    public ReturnValue verifyInput() {
        /**
         * empty method that can be overridden
         */
        return new ReturnValue();
    }

    @Override
    public ReturnValue filterInit() {
        /**
         * empty method that can be overridden
         */
        return new ReturnValue();
    }

    @Override
    public ReturnValue filter() {
        /**
         * empty method that can be overridden
         */
        return new ReturnValue();
    }

    @Override
    public ReturnValue verifyOutput() {
        /**
         * empty method that can be overridden
         */
        return new ReturnValue();
    }

    @Override
    public ReturnValue cleanup() {
        /**
         * empty method that can be overridden
         */
        return new ReturnValue();
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public void init(FeatureSet set, Object... parameters) {
        /** .. */
    }
    
    public Class getMapperClass() {
        return null;
    }

    public Class getReducerClass() {
        return null;
    }

    public Class getMapOutputKeyClass() {
        return null;
    }

    public Class getMapOutputValueClass() {
        return null;
    }

    @Override
    public Class<?> getOutputClass() {
        return null;
    }

    public int getNumReduceTasks() {
        return 0;
    }
}
