/*
 * Copyright (C) 2014 SeqWare
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
package io.seqware.pipeline.whitestar;

import java.nio.file.Path;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

/**
 * Run a helloworld using whitestar.
 *
 * @author dyuen
 */
public class WhiteStarMetadataET {

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @BeforeClass
    public static void setupWhiteStarTest() {

    }

    @AfterClass
    public static void teardownWhiteStarTest() {

    }

    @Test
    public void testWhiteStarSGEWorkflow() throws Exception {
        Path createTempFile = WhiteStarTest.createSettingsFile("whitestar-sge", "webservice");
        WhiteStarTest.createAndRunWorkflow(createTempFile, true);
    }

    @Test
    public void testWhiteStarParallelWorkflowWithMetadata() throws Exception {
        Path createTempFile = WhiteStarTest.createSettingsFile("whitestar-parallel", "webservice");
        WhiteStarTest.createAndRunWorkflow(createTempFile, true);
    }

    @Test
    public void testWhiteStarWorkflowWithMetadata() throws Exception {
        Path createTempFile = WhiteStarTest.createSettingsFile("whitestar-parallel", "webservice");
        WhiteStarTest.createAndRunWorkflow(createTempFile, true);
    }

}
