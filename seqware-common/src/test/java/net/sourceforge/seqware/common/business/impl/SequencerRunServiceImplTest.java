package net.sourceforge.seqware.common.business.impl;

import static org.junit.Assert.assertEquals;

import java.util.List;

import net.sourceforge.seqware.common.BaseUnit;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.SequencerRun;

import org.junit.Test;

/**
 * <p>SequencerRunServiceImplTest class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 * @since 0.13.3
 */
public class SequencerRunServiceImplTest extends BaseUnit {

  /**
   * <p>Constructor for SequencerRunServiceImplTest.</p>
   *
   * @throws java.lang.Exception if any.
   */
  public SequencerRunServiceImplTest() throws Exception {
    super();
  }

  /**
   * <p>testFindByCriteria.</p>
   */
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
