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
package com.github.seqware.system.test.util;

import com.github.seqware.system.test.GVFImportExportTest;
import com.github.seqware.system.test.VCFImportExportTest;
import org.junit.runner.RunWith;

/**
 * Builds test suites
 * Taken from {@link http://stackoverflow.com/questions/1070202/junit-suiteclasses-with-a-static-list-of-classes }
 * @author dyuen
 */
@RunWith(DynamicSuite.class)
public class DynamicSuiteBuilder {
    
    private static Class<?>[] SystemTests = {GVFImportExportTest.class, VCFImportExportTest.class};
    
    public static Class[] modelSuite() {
         return DynamicSuiteBuilder.SystemTests;
   }
}
