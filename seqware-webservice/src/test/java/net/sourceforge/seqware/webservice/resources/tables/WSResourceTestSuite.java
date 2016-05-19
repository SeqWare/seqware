/*
 * Copyright (C) 2011 SeqWare
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
package net.sourceforge.seqware.webservice.resources.tables;

import net.sourceforge.seqware.common.util.testtools.BasicTestDatabaseCreatorWrapper;
import net.sourceforge.seqware.common.util.testtools.JndiDatasourceCreator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * 
 * @author mtaschuk
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ net.sourceforge.seqware.webservice.resources.tables.DummyExperimentIDResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.ExperimentIDResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.ExperimentLibraryDesignResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.ExperimentSpotDesignResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.ExperimentSpotDesignReadSpecResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.StudyResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.SequencerRunResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.SampleIDResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.FileResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.WorkflowRunResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.IusResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.ProcessResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.SampleResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.RootSampleResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.FileChildWorkflowRunsResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.ProcessIDTest.class,
        net.sourceforge.seqware.webservice.resources.tables.WorkflowIDResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.FileIDResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.WorkflowResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.StudyIDResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.LaneIDResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.WorkflowRunIDResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.IusIDResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.SequencerRunIDResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.ExperimentResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.LaneResourceTest.class,
        net.sourceforge.seqware.common.metadata.MetadataWSTest.class, net.sourceforge.seqware.common.metadata.MetadataDBTest.class })
public class WSResourceTestSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
        BasicTestDatabaseCreatorWrapper.resetDatabaseWithUsers();
        JndiDatasourceCreator.create();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        BasicTestDatabaseCreatorWrapper.dropDatabase();
    }
}
