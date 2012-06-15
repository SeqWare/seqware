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
package com.github.seqware.factory;

import com.github.seqware.impl.*;
import com.github.seqware.model.FeatureStoreInterface;
import com.github.seqware.model.QueryInterface;

/**
 *
 * @author dyuen
 */
public class Factory {

    /**'
     * These types describe different types of our model objects, for example the 
     * in memory objects load everything into memory. An HBase backend might load only
     * a database cursor or rowKeys for relevant objects.
     */
    public enum Model_Type {

        IN_MEMORY {

            @Override
            BackEndInterface buildBackEnd(StorageInterface i) {
                return new SimplePersistentBackEnd(i);
            }
        },
        HBASE {

            @Override
            BackEndInterface buildBackEnd(StorageInterface i) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };

        abstract BackEndInterface buildBackEnd(StorageInterface i);
    };

    /**
     * These describe different types of our back-end, whether we store to files, 
     * to a database, or simply keep things in memory
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
                return new HBaseKyroSerialization(i);
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
        KYRO {
            @Override
            SerializationInterface buildSerialization() {
                return new KyroSerialization();
            }
        },
        PROTOBUF {

            @Override
            SerializationInterface buildSerialization() {
                //return new ProtobufSerialization();
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };

        abstract SerializationInterface buildSerialization();
    };
    
    private static final Model_Type DEFAULT_BACKEND = Model_Type.IN_MEMORY;
    private static Model_Type current_backend = DEFAULT_BACKEND;
    private static final Storage_Type DEFAULT_STORAGE = Storage_Type.FILE_STORAGE;
    private static Storage_Type current_storage = DEFAULT_STORAGE;
    private static final Serialization_Type DEFAULT_SERIALIZATION = Serialization_Type.APACHE;
    private static Serialization_Type current_serialization = DEFAULT_SERIALIZATION;
    private static BackEndInterface instance = null;

    /**
     * Get a reference to the currently operating back-end
     *
     * @return backEnd reference to access underlying DB operations
     */
    public static BackEndInterface getBackEnd() {
        if (instance == null) {
            instance = current_backend.buildBackEnd(current_storage.buildStorage(current_serialization.buildSerialization()));
        }
        return instance;
    }

    /**
     * Get a reference to the currently operating Query Interface
     *
     * @return query interface to do analysis and queries over FeatureSets
     */
    public static QueryInterface getQueryInterface() {
        if (instance == null) {
            instance = current_backend.buildBackEnd(current_storage.buildStorage(current_serialization.buildSerialization()));
        }
        return (QueryInterface) instance;
    }

    /**
     * Get a reference to the currently operating Query Interface
     *
     * @return feature store interface in order to do simple queries over all
     * objects in the feature store
     */
    public static FeatureStoreInterface getFeatureStoreInterface() {
        if (instance == null) {
            instance = current_backend.buildBackEnd(current_storage.buildStorage(current_serialization.buildSerialization()));
        }
        return (FeatureStoreInterface) instance;
    }

    /**
     * Return a new model manager to create and keep track of entities
     *
     * @return
     */
    public static ModelManager getModelManager() {
        return new HBaseModelManager();
    }

    /**
     * Used only by testing to override the back-end type
     *
     * @param bType setup backend with a specific type, if either parameter is
     * null, we deallocate the back-end
     * @param storageType setup storage with a specific type, if either parameter is null, we deallocate the back-end
     * @param serialType setup serialization with a specific type, if either parameter is null, we deallocate the back-end
     * 
     */
    public static void setFactoryBackendType(Model_Type bType, Storage_Type storageType, Serialization_Type serializationType) {
        instance = null;
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
