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
package com.github.seqware.queryengine.model.test;

/**
 * Not currently working. We do not have a full Kyro serialization
 * implementation.
 *
 * @author dyuen
 */
//@RunWith(DynamicSuite.class)
//public class InMemoryFileStorageKyroSerializationSuite {
//
//    @BeforeClass
//    public static void setupSuite() {
//        Logger.getLogger(InMemoryFileStorageKyroSerializationSuite.class.getName()).info( "Running test suite with in-memory objects using Kyro serialization to HBase");
//        SWQEFactory.setFactoryBackendType(SWQEFactory.Model_Type.IN_MEMORY, SWQEFactory.Storage_Type.FILE_STORAGE, SWQEFactory.Serialization_Type.KRYO);
//    }
//    
//    @AfterClass
//    public static void tearDownSuite(){
//        Logger.getLogger(InMemoryFileStorageKyroSerializationSuite.class.getName()).info( "Ending test suite and resetting");
//        SWQEFactory.setFactoryBackendType(null, null, null);
//    }
//}
