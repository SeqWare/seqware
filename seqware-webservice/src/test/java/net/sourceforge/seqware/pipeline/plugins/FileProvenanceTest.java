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
package net.sourceforge.seqware.pipeline.plugins;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sourceforge.seqware.common.util.testtools.BasicTestDatabaseCreator;
import static net.sourceforge.seqware.pipeline.plugins.PluginTest.metadata;
import org.junit.*;

/**
 * Runs the tests for the FileLinker
 * @author dyuen
 */
public class FileProvenanceTest extends ExtendedPluginTest {

    @BeforeClass
    public static void beforeClass(){
        BasicTestDatabaseCreator.resetDatabaseWithUsers();
    }
    
    @Before
    @Override
    public void setUp() {
        super.setUp();
    }
    
    public FileProvenanceTest() {
    }

    @Test
    public void testFileProvenanceNormalFail() {
        List<Map<String, String>> fileProvenanceReport = metadata.fileProvenanceReport(new HashMap());
        Assert.assertTrue("report should be empty until triggered", fileProvenanceReport.isEmpty());
    }
    
    @Test
    public void testFileProvenanceNormalPass(){
        metadata.fileProvenanceReportTrigger();
        List<Map<String, String>> fileProvenanceReport = metadata.fileProvenanceReport(new HashMap());
        Assert.assertTrue("report should be filled in but was size " + fileProvenanceReport.size(), fileProvenanceReport.size() == 483);
    }
}
