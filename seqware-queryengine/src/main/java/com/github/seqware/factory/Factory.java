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

    public enum Backend_Type {

        IN_MEMORY {

            @Override
            BackEndInterface buildBackEnd(FileSerializationInterface i) {
                return new SimplePersistentBackEnd(i);
            }
        },
        HBASE {

            @Override
            BackEndInterface buildBackEnd(FileSerializationInterface i) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };

        abstract BackEndInterface buildBackEnd(FileSerializationInterface i);
    };

    public enum Serialization_Type {

        IN_MEMORY_CLONE {

            @Override
            FileSerializationInterface buildSerialization() {
                return new NonPersistentSerialization();
            }
        },
        APACHE_SERIALIZATION {

            @Override
            FileSerializationInterface buildSerialization() {
                return new ApacheUtilsPersistentSerialization();
            }
        },
        PROTOBUF {

            @Override
            FileSerializationInterface buildSerialization() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };

        abstract FileSerializationInterface buildSerialization();
    };
    private static final Backend_Type DEFAULT_BACKEND = Backend_Type.IN_MEMORY;
    private static Backend_Type current_backend = DEFAULT_BACKEND;
    private static final Serialization_Type DEFAULT_SERIALIZATION = Serialization_Type.APACHE_SERIALIZATION;
    private static Serialization_Type current_serialization = DEFAULT_SERIALIZATION;
    private static BackEndInterface instance = null;

    /**
     * Get a reference to the currently operating back-end
     *
     * @return backEnd reference to access underlying DB operations
     */
    public static BackEndInterface getBackEnd() {
        if (instance == null) {
            instance = current_backend.buildBackEnd(current_serialization.buildSerialization());
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
            instance = current_backend.buildBackEnd(current_serialization.buildSerialization());
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
            instance = current_backend.buildBackEnd(current_serialization.buildSerialization());
        }
        return (FeatureStoreInterface) instance;
    }

    /**
     * Return a new model manager to create and keep track of entities
     *
     * @return
     */
    public static ModelManager getModelManager() {
        return new SimpleModelManager();
    }

    /**
     * Used only by testing to override the back-end type
     *
     * @param btype setup backend with a specific type, if either parameter is
     * null, we deallocate the back-end
     * @param stype setup serialization with a specific type, if either
     * parameter is null, we deallocate the back-end
     */
    public static void setFactoryBackendType(Backend_Type btype, Serialization_Type stype) {
        instance = null;
        if (btype == null) {
            current_backend = DEFAULT_BACKEND;
        } else {
            current_backend = btype;
        }
        if (stype == null) {
            current_serialization = DEFAULT_SERIALIZATION;
        } else {
            current_serialization = stype;
        }
    }
}
