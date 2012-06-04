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

import com.github.seqware.impl.DumbBackEnd;
import com.github.seqware.impl.SimpleModelManager;
import com.github.seqware.model.*;
import com.github.seqware.model.impl.inMemory.*;

/**
 *
 * @author dyuen
 */
public class Factory {
    
    public enum Backend_Type {IN_MEMORY, HBASE};
    
    public static Backend_Type BACKEND = Backend_Type.IN_MEMORY;
    private static BackEndInterface instance = null;
        
    /**
     * Get a reference to the currently operating back-end
     * @return backEnd reference to access underlying DB operations
     */
    public static BackEndInterface getBackEnd(){
        if (instance == null){
            instance = new DumbBackEnd();
        }
        return instance;
    }
    
    /**
     * Get a reference to the currently operating Query Interface
     * @return query interface to do analysis and queries over FeatureSets
     */
    public static QueryInterface getQueryInterface(){
        if (instance == null){
            instance = new DumbBackEnd();
        }
        return (QueryInterface)instance; 
    }
    
     /**
     * Get a reference to the currently operating Query Interface
     * @return feature store interface in order to do simple queries over all
     * objects in the feature store
     */
    public static FeatureStoreInterface getFeatureStoreInterface(){
        if (instance == null){
            instance = new DumbBackEnd();
        }
        return (FeatureStoreInterface)instance; 
    }
    
    /**
     * Return a new model manager to create and keep track of entities
     * @return 
     */
    public static ModelManager getModelManager(){
        return new SimpleModelManager();
    }
}
