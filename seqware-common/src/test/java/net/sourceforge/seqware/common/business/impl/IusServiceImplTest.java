package net.sourceforge.seqware.common.business.impl;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import net.sourceforge.seqware.common.BaseUnit;
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.hibernate.InSessionExecutions;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.util.Log;

import org.junit.Test;

public class IusServiceImplTest extends BaseUnit {

  public IusServiceImplTest() throws Exception {
    super();
  }

  @Test
  public void testAssociatedWorkflowRuns() {
    InSessionExecutions.bindSessionToThread();
    IUSService iusService = BeanFactory.getIUSServiceBean();
    IUS ius = iusService.findByID(4);
    Set<WorkflowRun> iuses = ius.getWorkflowRuns();
    Log.info("Count " + iuses.size());
    InSessionExecutions.unBindSessionFromTheThread();
  }

  @Test
  public void testFindByCriteria() {
    IUSService iusService = BeanFactory.getIUSServiceBean();
    // List<IUS> found = iusService.findByCriteria("Test IUS 2", true);
    // assertEquals(1, found.size());

    // Case sensitive search
    // Make sure that there is no data with name in lower case
    // found = iusService.findByCriteria("test ius 2", true);
    // assertEquals(0, found.size());

    // Insensitive search
    // found = iusService.findByCriteria("test ius 2", false);
    // assertEquals(1, found.size());

    // Test SW accession
    List<IUS> found = iusService.findByCriteria("4765", false);
    assertEquals(1, found.size());
  }
}
