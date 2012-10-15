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

import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.test.util.DynamicSuite;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

/**
 * This tests non-optimized in-memory objects against a non-persistent
 * (in-memory) back-end using ProtoBuffer serialization.
 *
 * @author dyuen
 * @version $Id: $Id
 * @since 0.13.3
 */
@RunWith(DynamicSuite.class)
public class InMemoryNonPersistentPBSerializationSuite {

    /**
     * <p>setupSuite.</p>
     */
    @BeforeClass
    public static void setupSuite() {
        Logger.getLogger(InMemoryNonPersistentPBSerializationSuite.class.getName()).info( "Running test suite with in-memory objects and PB serialization non-persistently");
        SWQEFactory.setFactoryBackendType(SWQEFactory.Model_Type.IN_MEMORY, SWQEFactory.Storage_Type.IN_MEMORY, SWQEFactory.Serialization_Type.PROTOBUF);
        SWQEFactory.getStorage().clearStorage();
    }

    /**
     * <p>tearDownSuite.</p>
     */
    @AfterClass
    public static void tearDownSuite() {
        Logger.getLogger(InMemoryNonPersistentPBSerializationSuite.class.getName()).info( "Ending test suite and resetting");
        SWQEFactory.setFactoryBackendType(null, null, null);
    }
}
