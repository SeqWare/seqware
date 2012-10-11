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
package net.sourceforge.seqware.common.business.impl;

import junit.framework.TestCase;
import net.sourceforge.seqware.common.util.testtools.DatabaseCreator;
import net.sourceforge.seqware.common.util.testtools.JndiDatasourceCreator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * 
 * @author mtaschuk
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ net.sourceforge.seqware.common.business.impl.IusServiceImplTest.class,
    net.sourceforge.seqware.common.business.impl.LaneServiceImplTest.class,
    net.sourceforge.seqware.common.business.impl.StudyServiceImplTest.class,
    net.sourceforge.seqware.common.business.impl.SampleServiceImplTest.class,
    net.sourceforge.seqware.common.business.impl.SequencerRunServiceImplTest.class,
    net.sourceforge.seqware.common.business.impl.ExperimentServiceImplTest.class,
    net.sourceforge.seqware.common.business.impl.FileServiceImplTest.class,
    net.sourceforge.seqware.common.business.impl.WorkflowRunServiceImplTest.class,
    net.sourceforge.seqware.common.business.impl.SampleReportServiceImplTest.class,
    net.sourceforge.seqware.database.QueryTest.class })
public class HibernateTestSuite extends TestCase {

  @BeforeClass
  public static void setUpClass() throws Exception {
    DatabaseCreator.createDatabase();
    JndiDatasourceCreator.create();
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
    DatabaseCreator.dropDatabase();
  }

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }
}
