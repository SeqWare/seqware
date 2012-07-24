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
package com.github.seqware.impl.test;

import com.github.seqware.impl.test.util.DynamicSuite;
import com.github.seqware.queryengine.factory.Factory;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

/**
 * Run all the tests in this directory which are implementation-specific. 
 * @author dyuen
 */
@RunWith(DynamicSuite.class)
public class ImplTestSuite {

    @BeforeClass
    public static void setupSuite() {
        Logger.getLogger(ImplTestSuite.class.getName()).log(Level.INFO, "Running test suite for implementation-specific tests");
    }
    
    @AfterClass
    public static void tearDownSuite(){
        Logger.getLogger(ImplTestSuite.class.getName()).log(Level.INFO, "Ending test suite and resetting");
        Factory.setFactoryBackendType(null, null, null);
    }
}
