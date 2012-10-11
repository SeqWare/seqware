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
package net.sourceforge.seqware.common.util.workflowtools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.module.ReturnValue;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author boconnor
 */
public class WorkflowToolsTest {
  
  public WorkflowToolsTest() {
  }

  @BeforeClass
  public static void setUpClass() throws Exception {
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }
  
  @Before
  public void setUp() {
  }
  
  @After
  public void tearDown() {
  }

  /**
   * Test of getFailedJobsInfo method, of class Workflow.
   */
  @Test
  public void testGetFailedJobsInfo() {
    System.out.println("getFailedJobsInfo: ");
    File statusDirFile = new File(".");
    System.out.println("Current Dir: "+statusDirFile.getAbsolutePath());
    String statusDir = "src/test/resources";
    WorkflowTools workflowTools = new WorkflowTools();
    ReturnValue[] results = workflowTools.getFailedJobsInfo(statusDir);
    for (ReturnValue result : results) {
      //System.out.println("Result: "+result);
      //System.out.println("Result: stdout\n"+result.getStdout());
      //System.out.println("Result: stderr\n"+result.getStderr());
      assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
      assertFalse(result.getStdout().contains("INCORRECT_FILE"));
    }
  }
  
  /**
   * Test of parsePegasusStatus method, of class Workflow.
   */
  @Test
  public void testParsePegasusStatus() {
    
    WorkflowTools workflowTools = new WorkflowTools();
    String testStatus = "HelloWorld-0.dag is running.\n"
            + "06/24/12 02:54:32  Done     Pre   Queued    Post   Ready   Un-Ready   Failed\n"
            + "06/24/12 02:54:32   ===     ===      ===     ===     ===        ===      ===\n"
            + "06/24/12 02:54:32     1       0        1       0       0          5        0\n"
            + "\n"
            + "WORKFLOW STATUS : RUNNING | 1/7 ( 14% ) | (condor processing workflow)\n";

    String result = workflowTools.parsePegasusStatus(testStatus);
    assertTrue(result.contains("RUNNING: step "));
    
  }
  
}
