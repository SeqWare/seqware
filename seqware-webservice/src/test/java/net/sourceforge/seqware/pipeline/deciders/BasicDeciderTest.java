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
package net.sourceforge.seqware.pipeline.deciders;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.seqware.pipeline.plugins.PluginTest;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * <p>BasicDeciderTest class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 * @since 0.13.3
 */
public class BasicDeciderTest extends PluginTest {
  /**
   * <p>Constructor for BasicDeciderTest.</p>
   */
  public BasicDeciderTest() {
  }
    @Before
    @Override
    public void setUp() {
        super.setUp();
        instance = new BasicDecider();
        instance.setMetadata(metadata);
    }

  /**
   * <p>testCompareWorkflowRunFiles_Same.</p>
   */
  @Test
  public void testCompareWorkflowRunFiles_Same() {
	List<String> filesToRun = new ArrayList<String>();
	filesToRun.add("s3://abcco.uploads/s_G1_L001_R1_001_index8.fastq.gz");
	filesToRun.add("s3://abcco.uploads/s_G1_L001_R2_001_index8.fastq.gz");
	String workflowRunAcc="6654";	

      	//assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
//	assertFalse(((BasicDecider)instance).compareWorkflowRunFiles(workflowRunAcc, filesToRun));
    }
  
  /**
   * <p>testCompareWorkflowRunFiles_Bigger.</p>
   */
  @Test
  public void testCompareWorkflowRunFiles_Bigger() {

	List<String> filesToRun = new ArrayList<String>();
	filesToRun.add("s3://abcco.uploads/s_G1_L001_R1_001_index8.fastq.gz");
	filesToRun.add("s3://abcco.uploads/s_G1_L001_R2_001_index8.fastq.gz");
	filesToRun.add("s3://abcco.uploads/s_G1_L001_R3_001_index8.fastq.gz");
	String workflowRunAcc="6654";	

      	//assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
//	assertTrue(((BasicDecider)instance).compareWorkflowRunFiles(workflowRunAcc, filesToRun));
    }

  /**
   * <p>testCompareWorkflowRunFiles_SameButDifferent.</p>
   */
  @Test
  public void testCompareWorkflowRunFiles_SameButDifferent() {

	List<String> filesToRun = new ArrayList<String>();
	filesToRun.add("s3://abcco.uploads/s_G1_L001_R1_001_index8.fastq.gz");
	filesToRun.add("s3://abcco.uploads/s_G1_L001_R3_001_index8.fastq.gz");
	String workflowRunAcc="6654";	

      	//assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
//	assertTrue(((BasicDecider)instance).compareWorkflowRunFiles(workflowRunAcc, filesToRun));
    }

  /**
   * <p>testCompareWorkflowRunFiles_Smaller.</p>
   */
  @Test
  public void testCompareWorkflowRunFiles_Smaller() {
	

	List<String> filesToRun = new ArrayList<String>();
	filesToRun.add("s3://abcco.uploads/s_G1_L001_R1_001_index8.fastq.gz");
	String workflowRunAcc="6654";	

      	//assertTrue(result.getStdout().contains("UNIT_TEST_TOKEN"));
//	assertFalse(((BasicDecider)instance).compareWorkflowRunFiles(workflowRunAcc, filesToRun));
    }




  
}
