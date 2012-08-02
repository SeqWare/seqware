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
package com.github.seqware.queryengine.impl.test.util;

import com.github.seqware.queryengine.impl.test.*;
import org.junit.runner.RunWith;

/**
 * Add tests here to run them via the test suites
 * @author dyuen
 */
@RunWith(DynamicSuite.class)
public class DynamicSuiteBuilder {
    
    private static Class[] ImplTests = {SimplePersistentBackEndTest.class, FactoryStaticInitializerTest.class, FactoryConstructorTest.class
        , ApacheConstructorTest.class, ApacheStaticInitializerTest.class, HBaseTest.class, RowKeyFeatureSetTest.class};
    
    public static Class[] implSuite(){
        return DynamicSuiteBuilder.ImplTests;
    }
}
