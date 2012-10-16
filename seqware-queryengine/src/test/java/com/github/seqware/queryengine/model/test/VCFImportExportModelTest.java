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
import com.github.seqware.queryengine.impl.HBasePersistentBackEnd;
import com.github.seqware.queryengine.impl.SimplePersistentBackEnd;
import com.github.seqware.queryengine.system.test.VCFImportExportTest;
import java.io.IOException;
import org.junit.Test;

/**
 * Run the basic VCFImportExportTest across all models
 *
 * @author dyuen
 * @version $Id: $Id
 * @since 0.13.3
 */
public class VCFImportExportModelTest extends VCFImportExportTest {

    /** {@inheritDoc} */
    @Test
    @Override
    public void testNormalVCFImport() {
        if (!(SWQEFactory.getBackEnd() instanceof HBasePersistentBackEnd)){
            // in-memory models are too slow for the following test
            return;
        }
        super.testNormalVCFImport();;
    }

    /** {@inheritDoc} */
    @Test
    @Override
    public void testInvalidVCFImport() {
        // skip this test
    }

    /** {@inheritDoc} */
    @Test
    @Override
    public void testMissingValueVCFImport() {
        // skip this test
    }

    /** {@inheritDoc} */
    @Test
    @Override
    public void testVCFImportParam() throws IOException {
        // skip this test
    }
}
