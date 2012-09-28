package net.sourceforge.seqware.common.business.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import net.sourceforge.seqware.common.BaseUnit;
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.hibernate.InSessionExecutions;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.WorkflowRun;

import org.junit.Test;

public class WorkflowRunServiceImplTest extends BaseUnit {

  public WorkflowRunServiceImplTest() throws Exception {
    super();
  }

  @Test
  public void testParentLanes() {
    InSessionExecutions.bindSessionToThread();
    WorkflowRunService wfService = BeanFactory.getWorkflowRunServiceBean();
    WorkflowRun wfRun = wfService.findByID(22);

    SortedSet<Lane> lanes = wfRun.getLanes();
    assertNotNull(lanes);

    System.out.print(lanes.size());

    // Let's try to add new lane to wfRun
    LaneService laneService = BeanFactory.getLaneServiceBean();
    Lane lane = laneService.findByID(3);
    lanes.add(lane);
    wfRun.setLanes(lanes);
    wfService.update(wfRun);
    InSessionExecutions.unBindSessionFromTheThread();
    // Let's open new session
    InSessionExecutions.bindSessionToThread();
    wfRun = wfService.findByID(22);
    lanes = wfRun.getLanes();
    System.out.print(lanes.size());
    assertEquals(1, lanes.size());
    InSessionExecutions.unBindSessionFromTheThread();
  }

  @Test
  public void testParentIus() {
    InSessionExecutions.bindSessionToThread();
    WorkflowRunService wfService = BeanFactory.getWorkflowRunServiceBean();
    WorkflowRun wfRun = wfService.findByID(22);

    SortedSet<IUS> ius = wfRun.getIus();
    assertNotNull(ius);
    assertEquals(0, ius.size());

    // Get some IUS
    IUSService iusService = BeanFactory.getIUSServiceBean();
    IUS someIus = iusService.findByID(4);
    ius.add(someIus);
    wfRun.setIus(ius);
    wfService.update(wfRun);
    InSessionExecutions.unBindSessionFromTheThread();

    InSessionExecutions.bindSessionToThread();
    wfRun = wfService.findByID(22);
    ius = wfRun.getIus();
    assertNotNull(ius);
    assertEquals(1, ius.size());
    InSessionExecutions.unBindSessionFromTheThread();
  }

  @Test
  public void testAttachNewlyCreatedWorkflowRun() {
    // Suppose we created or get WorkflowRun object which is hibernate outbound
    InSessionExecutions.bindSessionToThread();
    WorkflowRun createdWorkflowRun = new WorkflowRun();
    createdWorkflowRun.setWorkflowRunId(22);
    createdWorkflowRun.setIniFile("newIniFile"); // <-- ini file has been
                                                 // updated
    createdWorkflowRun.setStatus("complete");
    createdWorkflowRun.setStatusCmd("newCommand"); // <-- command has been
                                                   // updated
    createdWorkflowRun.setSeqwareRevision("2305M");
    createdWorkflowRun.setSwAccession(64);

    createdWorkflowRun.setCreateTimestamp(new Date());
    createdWorkflowRun.setUpdateTimestamp(new Date());

    WorkflowRunService wfService = BeanFactory.getWorkflowRunServiceBean();
    wfService.updateDetached(createdWorkflowRun);
    InSessionExecutions.unBindSessionFromTheThread();
  }

  @Test
  public void testFindByCriteria() {
    WorkflowRunService wfService = BeanFactory.getWorkflowRunServiceBean();
    List<WorkflowRun> found = wfService.findByCriteria("NC_001807", false);
    assertEquals(4, found.size());

    // Case sensitive
    found = wfService.findByCriteria("ExomesOrHg19Tumour", true);
    assertEquals(1, found.size());

    found = wfService.findByCriteria("exomesOrHg19Tumour", true);
    assertEquals(0, found.size());

    // SWID
    found = wfService.findByCriteria("2862", true);
    assertEquals(1, found.size());
  }

}
