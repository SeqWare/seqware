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

import io.seqware.pipeline.engines.whitestar.WhiteStarWorkflowEngine;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Run a helloworld using whitestar.
 *
 * Attempt at this, doesn't work. Looks like Mocking interacts with our class loader in some weird way.
 *
 *
 * @author dyuen
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ WhiteStarWorkflowEngine.class, ConfigTools.class })
public class WhiteStarSGETest {

    @BeforeClass
    public static void setupWhiteStarTest() {

    }

    @AfterClass
    public static void teardownWhiteStarTest() {

    }

    // Checking this in if we can figure out the workflow class loader issue.

    // @Test
    // public void testWhiteStarStandardWorkflow() throws Exception {
    // Path createTempFile = createSettingsFile("whitestar-sge");
    //
    // DefaultExecutor executorMock = createMockAndExpectNew(DefaultExecutor.class);
    // expect(executorMock.execute(isA(CommandLine.class))).andReturn(0).anyTimes();
    //
    // mockStatic(ConfigTools.class);
    // // overide settings file location, environment variables seem screwed up by mocking?
    // HashMap<String, String> hm = new HashMap<>();
    // MapTools.ini2Map(createTempFile.toString(), hm, true);
    // expect(ConfigTools.getSettings()).andReturn(hm).anyTimes();
    //
    // PowerMock.replay(ConfigTools.class);
    // replay(executorMock);
    // WhiteStarTest.createAndRunWorkflow(createTempFile);
    // }

}
