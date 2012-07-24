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
package com.github.seqware.model.test;

import com.github.seqware.model.test.util.DynamicSuite;
import com.github.seqware.queryengine.factory.Factory;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

/**
 * This tests non-optimized model objects against a file storage back-end using
 * ProtoBuffer serialization.
 *
 * @author dyuen
 */
@RunWith(DynamicSuite.class)
public class InMemoryFileStoragePBSerializationSuite {

    @BeforeClass
    public static void setupSuite() {
        Logger.getLogger(InMemoryFileStoragePBSerializationSuite.class.getName()).log(Level.INFO, "Running test suite with in-memory objects and PB serialization to the filesystem");
        Factory.setFactoryBackendType(Factory.Model_Type.IN_MEMORY, Factory.Storage_Type.FILE_STORAGE, Factory.Serialization_Type.PROTOBUF);
        Factory.getStorage().clearStorage();
    }

    @AfterClass
    public static void tearDownSuite() {
        Logger.getLogger(InMemoryFileStoragePBSerializationSuite.class.getName()).log(Level.INFO, "Ending test suite and resetting");
        Factory.setFactoryBackendType(null, null, null);
    }
}
