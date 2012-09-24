package net.sourceforge.seqware.common.business.impl;

import static org.junit.Assert.assertEquals;

import java.util.List;

import net.sourceforge.seqware.common.BaseUnit;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.SequencerRun;

import org.junit.Test;

public class SequencerRunServiceImplTest extends BaseUnit {

  public SequencerRunServiceImplTest() throws Exception {
    super();
  }

  @Test
  public void testFindByCriteria() {
    SequencerRunService sequencerRunServiceImpl = BeanFactory.getSequencerRunServiceBean();
    List<SequencerRun> found = sequencerRunServiceImpl.findByCriteria("srk", false);
    assertEquals(1, found.size());

    // Case sensitive
    found = sequencerRunServiceImpl.findByCriteria("Run", true);
    assertEquals(0, found.size());

    found = sequencerRunServiceImpl.findByCriteria("Run", false);
    assertEquals(1, found.size());

    // SWID
    found = sequencerRunServiceImpl.findByCriteria("4715", false);
    assertEquals(1, found.size());
  }
}
