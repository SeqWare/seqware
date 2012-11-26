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
package com.github.seqware.queryengine.factory;

import com.github.seqware.queryengine.backInterfaces.SerializationInterface;
import com.github.seqware.queryengine.backInterfaces.StorageInterface;
import com.github.seqware.queryengine.impl.*;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.QueryInterface;
import com.github.seqware.queryengine.plugins.MapReducePlugin;
import com.github.seqware.queryengine.plugins.PluginInterface;
import com.github.seqware.queryengine.plugins.PluginRunnerInterface;
import com.github.seqware.queryengine.plugins.hbasemr.MRHBasePluginRunner;
import com.github.seqware.queryengine.plugins.inmemory.InMemoryPluginRunner;

/**
 * This is the SeqWare Query Engine factory and should be used as the primary
 * entry-point for developers. Only go "behind" to access serialization or
 * back-ends directly if you want to work on implementation-specific classes.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class SWQEFactory {

    public static PluginRunnerInterface getPluginRunner(PluginInterface plugin, FeatureSet inputSet, Object ... parameters) {
        if (plugin == null){
            return null;
        }
        if (current_backend == Model_Type.MRHBASE){
            MapReducePlugin mrPlugin = (MapReducePlugin)plugin;
            return new MRHBasePluginRunner(mrPlugin, inputSet, parameters);
        } else{
            return new InMemoryPluginRunner(plugin, inputSet, parameters);
        }
    }

    /**
     * '
     * These types describe different types of our model objects, for example
     * the in memory objects load everything into memory. An HBase backend might
     * load only a database cursor or rowKeys for relevant objects.
     */
    public enum Model_Type {

        IN_MEMORY {

            @Override
            QueryInterface buildBackEnd(StorageInterface i) {
                return new SimplePersistentBackEnd(i);
            }
        },
        HBASE {

            @Override
            QueryInterface buildBackEnd(StorageInterface i) {
                return new HBasePersistentBackEnd(i);
            }
        },
        MRHBASE{
            @Override
            QueryInterface buildBackEnd(StorageInterface i){
                return new MRHBasePersistentBackEnd(i);
            }
        }
        ;

        abstract QueryInterface buildBackEnd(StorageInterface i);
    };

    /**
     * These describe different types of our back-end, whether we store to
     * files, to a database, or simply keep things in memory
     */
    public enum Storage_Type {

        IN_MEMORY {

            @Override
            StorageInterface buildStorage(SerializationInterface i) {
                return new NonPersistentStorage(i);
            }
        },
        FILE_STORAGE {

            @Override
            StorageInterface buildStorage(SerializationInterface i) {
                return new TmpFileStorage(i);
            }
        },
        HBASE_STORAGE {

            @Override
            StorageInterface buildStorage(SerializationInterface i) {
                return new HBaseStorage(i);
            }
        };

        abstract StorageInterface buildStorage(SerializationInterface i);
    };

    /**
     * These describe different types of serialization only
     */
    public enum Serialization_Type {

        APACHE {

            @Override
            SerializationInterface buildSerialization() {
                return new ApacheSerialization();
            }
        },
        KRYO {

            @Override
            SerializationInterface buildSerialization() {
                return new KryoSerialization();
            }
        },
        PROTOBUF {

            @Override
            SerializationInterface buildSerialization() {
                return new ProtobufSerialization();
            }
        };

        abstract SerializationInterface buildSerialization();
    };
    private static final Model_Type DEFAULT_BACKEND = Model_Type.MRHBASE;
    private static Model_Type current_backend = DEFAULT_BACKEND;
    private static final Storage_Type DEFAULT_STORAGE = Storage_Type.HBASE_STORAGE;
    private static Storage_Type current_storage = DEFAULT_STORAGE;
    private static final Serialization_Type DEFAULT_SERIALIZATION = Serialization_Type.PROTOBUF;
    private static Serialization_Type current_serialization = DEFAULT_SERIALIZATION;
    private static SerializationInterface serialInstance = null;
    private static StorageInterface storeInstance = null;
    private static QueryInterface instance = null;

    /**
     * Get a reference to the current operating serialization method
     *
     * @return a {@link com.github.seqware.queryengine.impl.SerializationInterface} object.
     */
    public static SerializationInterface getSerialization() {
        if (serialInstance == null) {
            serialInstance = current_serialization.buildSerialization();
        }
        return serialInstance;
    }

    /**
     * Get a reference to the currently operating storage method
     *
     * @return a {@link com.github.seqware.queryengine.impl.StorageInterface} object.
     */
    public static StorageInterface getStorage() {
        if (storeInstance == null) {
            storeInstance = current_storage.buildStorage(getSerialization());
        }
        return storeInstance;
    }

    /**
     * Close the currently operating storage method
     */
    public static void closeStorage() {
        if (storeInstance != null) {
            StorageInterface ref = storeInstance;
            storeInstance = null;
            instance = null;
            ref.closeStorage();
        }
    }

    /**
     * Get a reference to the currently operating Query Interface
     *
     * @return query interface in order to do queries over all
     * objects in the feature store
     */
    public static QueryInterface getQueryInterface() {
        if (instance == null) {
            instance = current_backend.buildBackEnd(getStorage());
        }
        return (QueryInterface) instance;
    }

    /**
     * Return a new model manager to create and keep track of entities
     *
     * @return a {@link com.github.seqware.queryengine.factory.CreateUpdateManager} object.
     */
    public static CreateUpdateManager getModelManager() {
        if (current_backend == Model_Type.MRHBASE){
            return new MRHBaseModelManager();
        } else{
            return new HBaseModelManager();
        }
        // we do not really have to use the base ModelManager, HBaseModelManager is fully compatible
    }

    /**
     * Used only by testing to override the back-end type
     *
     * @param bType setup backend with a specific type, if either parameter is
     * null, we deallocate the back-end
     * @param storageType setup storage with a specific type, if either
     * parameter is null, we deallocate the back-end
     * @param serializationType a {@link com.github.seqware.queryengine.factory.SWQEFactory.Serialization_Type} object.
     */
    public static void setFactoryBackendType(Model_Type bType, Storage_Type storageType, Serialization_Type serializationType) {
        instance = null;
        storeInstance = null;
        serialInstance = null;
        if (bType == null) {
            current_backend = DEFAULT_BACKEND;
        } else {
            current_backend = bType;
        }
        if (storageType == null) {
            current_storage = DEFAULT_STORAGE;
        } else {
            current_storage = storageType;
        }
        if (serializationType == null) {
            current_serialization = DEFAULT_SERIALIZATION;
        } else {
            current_serialization = serializationType;
        }
    }
}
