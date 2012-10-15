package net.sourceforge.seqware.common.business.impl;

import static org.junit.Assert.assertEquals;

import java.util.List;

import net.sourceforge.seqware.common.BaseUnit;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.Sample;

import org.junit.Test;

/**
 * <p>SampleServiceImplTest class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 * @since 0.13.3
 */
public class SampleServiceImplTest extends BaseUnit {

  /**
   * <p>Constructor for SampleServiceImplTest.</p>
   *
   * @throws java.lang.Exception if any.
   */
  public SampleServiceImplTest() throws Exception {
    super();
  }

  /**
   * <p>testFindByCriteria.</p>
   */
  @Test
  public void testFindByCriteria() {
    SampleService sampleService = BeanFactory.getSampleServiceBean();

    List<Sample> foundSamples = sampleService.findByCriteria("Sample", false);
    assertEquals(2, foundSamples.size());

    // check case sensitive
    foundSamples = sampleService.findByCriteria("sample", true);
    assertEquals(1, foundSamples.size());

    foundSamples = sampleService.findByCriteria("sample", false);
    assertEquals(2, foundSamples.size());

    // Look for SWID
    foundSamples = sampleService.findByCriteria("4760", false);
    assertEquals(1, foundSamples.size());

    // No samples
    foundSamples = sampleService.findByCriteria("24377eauoeaua", false);
    assertEquals(0, foundSamples.size());
  }
}
