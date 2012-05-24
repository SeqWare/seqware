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
import com.github.seqware.model.FeatureStoreInterface;
import com.github.seqware.model.QueryInterface;

/**
 *
 * @author dyuen
 */
public class Factory {
    
    private static BackEndInterface instance = null;
        
    /**
     * Get a reference to the currently operating back-end
     * @return 
     */
    public static BackEndInterface getBackEnd(){
        if (instance == null){
            instance = new DumbBackEnd();
        }
        return instance;
    }
    
    /**
     * Get a reference to the currently operating Query Interface
     * @return 
     */
    public static QueryInterface getQueryInterface(){
        if (instance == null){
            instance = new DumbBackEnd();
        }
        return (QueryInterface)instance; 
    }
    
     /**
     * Get a reference to the currently operating Query Interface
     * @return 
     */
    public static FeatureStoreInterface getFeatureStoreInterface(){
        if (instance == null){
            instance = new DumbBackEnd();
        }
        return (FeatureStoreInterface)instance; 
    }
}
