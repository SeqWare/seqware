package net.sourceforge.seqware.common.business.impl;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import net.sourceforge.seqware.common.BaseUnit;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.hibernate.InSessionExecutions;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.WorkflowRun;

import org.junit.Test;

/**
 * <p>LaneServiceImplTest class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 * @since 0.13.3
 */
public class LaneServiceImplTest extends BaseUnit {

  /**
   * <p>Constructor for LaneServiceImplTest.</p>
   *
   * @throws java.lang.Exception if any.
   */
  public LaneServiceImplTest() throws Exception {
    super();
  }

  /**
   * <p>testAssociatedWorkflowRuns.</p>
   */
  @Test
  public void testAssociatedWorkflowRuns() {
    try {
      InSessionExecutions.bindSessionToThread();
      LaneService laneService = BeanFactory.getLaneServiceBean();
      Lane lane = laneService.findByID(16);
      Set<WorkflowRun> workflowRuns = lane.getWorkflowRuns();
      System.out.print(workflowRuns.size());
      assertEquals(0, workflowRuns.size());
    } finally {
      InSessionExecutions.unBindSessionFromTheThread();
    }
  }

  /**
   * <p>testFindByCriteria.</p>
   */
  @Test
  public void testFindByCriteria() {
    LaneService laneService = BeanFactory.getLaneServiceBean();
    // List<Lane> found = laneService.findByCriteria("_LMP", true);
    // assertEquals(9, found.size());

    // Case Sensitive
    List<Lane> found = laneService.findByCriteria("_lAne", true);
    assertEquals(0, found.size());

    found = laneService.findByCriteria("_lAne", false);
    assertEquals(8, found.size());

    // By SWID
    found = laneService.findByCriteria("4707", false);
    assertEquals(1, found.size());

    // By Description
    // found = laneService.findByCriteria("{", false);
    // assertEquals(1, found.size());

  }
}
