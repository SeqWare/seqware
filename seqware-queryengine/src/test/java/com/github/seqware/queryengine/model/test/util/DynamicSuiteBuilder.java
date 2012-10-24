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
package com.github.seqware.queryengine.model.test.util;

import com.github.seqware.queryengine.model.test.*;
import org.junit.runner.RunWith;

/**
 * Add tests here to have them run in the tests suites for the model directory.
 *
 * @author dyuen
 * @version $Id: $Id
 * @since 0.13.3
 */
@RunWith(DynamicSuite.class)
public class DynamicSuiteBuilder {
    
    private static Class<?>[] ModelTests = {ACLTest.class, FeatureSetTest.class, FeatureStoreInterfaceTest.class, 
        FeatureTest.class, QueryInterfaceTest.class, ReferenceSetTest.class, TaggableTest.class, GVFFormatTest.class, 
        UserGroupTest.class, TTLTest.class, FriendlyNameTest.class, VCFImportExportModelTest.class};
    
    /**
     * <p>modelSuite.</p>
     *
     * @return an array of {@link java.lang.Class} objects.
     */
    public static Class[] modelSuite() {
         return DynamicSuiteBuilder.ModelTests;
   }
}
