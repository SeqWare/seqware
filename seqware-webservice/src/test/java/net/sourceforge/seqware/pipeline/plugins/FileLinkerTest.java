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

import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.util.testtools.BasicTestDatabaseCreator;
import static net.sourceforge.seqware.pipeline.plugins.PluginTest.metadata;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.junit.*;

/**
 * Runs the tests for the FileLinker
 * @author dyuen
 */
public class FileLinkerTest extends ExtendedPluginTest {

    @BeforeClass
    public static void beforeClass(){
        BasicTestDatabaseCreator.resetDatabaseWithUsers();
    }
    
    @Before
    @Override
    public void setUp() {
        instance = new FileLinker();
        super.setUp();
    }
    
    public FileLinkerTest() {
    }

    @Test
    public void testFileLinkerNormal(){
        String path = FileLinkerTest.class.getResource("file_linker_test.txt").getPath();
        
        launchPlugin("--workflow-accession", "4", "--file-list-file", path);
               
        String s = getOut();

        BasicTestDatabaseCreator dbCreator = new BasicTestDatabaseCreator();
//        Object[] runQuery = dbCreator.runQuery(new ArrayHandler(), "select experiment_library_design_id,experiment_spot_design_id from experiment WHERE sw_accession=?", Integer.valueOf(localExperimentAccession));
//        Assert.assertTrue("optional values were incorrect", runQuery[0].equals(8) && runQuery[1].equals(7));
//        // check that we can get them back via metadata methods as well
//        Experiment e = metadata.getExperiment(Integer.valueOf(localExperimentAccession));
//        Assert.assertTrue("could not retrieve optional fields via metadata", e.getExperimentLibraryDesign() != null && e.getExperimentSpotDesign() != null);
//        Assert.assertTrue("optional fields via metadata were incorrect, found " + e.getExperimentLibraryDesign().getExperimentLibraryDesignId() + ":" + e.getExperimentSpotDesign().getExperimentSpotDesignId(), 
//                e.getExperimentLibraryDesign().getExperimentLibraryDesignId() == 8 && e.getExperimentSpotDesign().getExperimentSpotDesignId() == 7);
    }
}
