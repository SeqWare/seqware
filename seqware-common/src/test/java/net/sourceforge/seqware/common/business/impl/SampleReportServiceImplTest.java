package net.sourceforge.seqware.common.business.impl;

import static org.junit.Assert.assertEquals;

import java.util.List;

import net.sourceforge.seqware.common.business.SampleReportService;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.hibernate.InSessionExecutions;
import net.sourceforge.seqware.common.model.SampleReportRow;
import net.sourceforge.seqware.common.model.SequencerRun;

import org.junit.Test;

/**
 * <p>SampleReportServiceImplTest class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 * @since 0.13.3
 */
public class SampleReportServiceImplTest {

  /**
   * <p>testGetRowsForSequencerRun.</p>
   */
  @Test
  public void testGetRowsForSequencerRun() {
    try {
      InSessionExecutions.bindSessionToThread();
      SampleReportService sampleReportService = BeanFactory.getSampleReportServiceBean();
      SequencerRunService sequencerRunService = BeanFactory.getSequencerRunServiceBean();
      SequencerRun sr = sequencerRunService.findByID(1);
      List<SampleReportRow> reportRow = sampleReportService.getRowsForSequencerRun(sr);
      assertEquals(10, reportRow.size());
    } finally {
      InSessionExecutions.unBindSessionFromTheThread();
    }
  }

  /**
   * <p>testGetRowsWithSequencerRuns.</p>
   */
  @Test
  public void testGetRowsWithSequencerRuns() {
    try {
      InSessionExecutions.bindSessionToThread();
      SampleReportService sampleReportService = BeanFactory.getSampleReportServiceBean();
      List<SampleReportRow> reportRows = sampleReportService.getRowsWithSequencerRuns();
      assertEquals(51, reportRows.size());
    } finally {
      InSessionExecutions.unBindSessionFromTheThread();
    }
  }

}
