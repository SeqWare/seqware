package net.sourceforge.seqware.common.business.impl;

import java.util.List;

import net.sourceforge.seqware.common.business.SampleReportService;
import net.sourceforge.seqware.common.dao.SampleReportDAO;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SampleReportRow;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.SequencerRunReportId;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.Workflow;

/**
 * <p>SampleReportServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SampleReportServiceImpl implements SampleReportService {

  private SampleReportDAO sampleReportDAO = null;

  /**
   * <p>Setter for the field <code>sampleReportDAO</code>.</p>
   *
   * @param dao a {@link net.sourceforge.seqware.common.dao.SampleReportDAO} object.
   */
  public void setSampleReportDAO(SampleReportDAO dao) {
    this.sampleReportDAO = dao;
  }

  /** {@inheritDoc} */
  @Override
  public List<Status> getStatusesForStudy(Study study) {
    return sampleReportDAO.getStatusesForStudy(study);
  }

  /** {@inheritDoc} */
  @Override
  public List<Workflow> getWorkflowsForStudy(Study study) {
    return sampleReportDAO.getWorkflowsForStudy(study);
  }

  /** {@inheritDoc} */
  @Override
  public List<Workflow> getWorkflows(SequencerRun seqRun) {
    return sampleReportDAO.getWorkflows(seqRun);
  }

  /** {@inheritDoc} */
  @Override
  public int countOfStatus(Study study, Status status) {
    return sampleReportDAO.countOfStatus(study, status);
  }

  /** {@inheritDoc} */
  @Override
  public int countOfStatus(SequencerRun seqRun, Status status) {
    return sampleReportDAO.countOfStatus(seqRun, status);
  }

  /** {@inheritDoc} */
  @Override
  public int countOfStatus(Study study, Workflow workflow, Status status) {
    return sampleReportDAO.countOfStatus(study, workflow, status);
  }

  /** {@inheritDoc} */
  @Override
  public int countOfStatus(SequencerRun seqRun, Workflow workflow, Status status) {
    return sampleReportDAO.countOfStatus(seqRun, workflow, status);
  }

  /** {@inheritDoc} */
  @Override
  public int countOfRows(SequencerRun seqRun) {
    return sampleReportDAO.countOfRows(seqRun);
  }

  /** {@inheritDoc} */
  @Override
  public List<Sample> getChildSamples(Study study) {
    return sampleReportDAO.getChildSamples(study);
  }

  /** {@inheritDoc} */
  @Override
  public Status getStatus(Study study, Sample childSample, Workflow workflow) {
    return sampleReportDAO.getStatus(study, childSample, workflow);
  }

  /** {@inheritDoc} */
  @Override
  public Status getStatus(Study study, Sample sample, IUS ius, Lane lane, SequencerRun seqRun, Workflow workflow) {
    if (seqRun == null) {
      return getStatus(study, sample, workflow);
    }
    return sampleReportDAO.getStatus(study, sample, ius, lane, seqRun, workflow);
  }

  /** {@inheritDoc} */
  @Override
  public List<Status> getStatusesForWorkflow(Study study, Workflow workflow) {
    return sampleReportDAO.getStatusesForWorkflow(study, workflow);
  }

  /** {@inheritDoc} */
  @Override
  public List<SampleReportRow> getRowsForSequencerRun(SequencerRun sr) {
    if (sr == null) {
      return getRowsWithSequencerRuns();
    }
    return sampleReportDAO.getRowsForSequencerRun(sr);
  }

  /** {@inheritDoc} */
  @Override
  public List<SampleReportRow> getRowsWithSequencerRuns() {
    return sampleReportDAO.getRowsWithSequencerRuns();
  }

  /** {@inheritDoc} */
  @Override
  public List<Status> getStatusesForWorkflow(SequencerRun seqRun, Workflow workflow) {
    return sampleReportDAO.getStatusesForWorkflow(seqRun, workflow);
  }

  /** {@inheritDoc} */
  @Override
  public List<Status> getStatusesForSequencerRun(SequencerRun seqRun) {
    return sampleReportDAO.getStatusesForSequencerRun(seqRun);
  }

  /** {@inheritDoc} */
  @Override
  public List<SequencerRunReportId> getSequencerRunReportIds(SequencerRun seqRun, String sortField, String sortOrder,
      int offset, int limit) {
    return sampleReportDAO.getSequencerRunReportIds(seqRun, sortField, sortOrder, offset, limit);
  }

  /** {@inheritDoc} */
  @Override
  public List<SequencerRunReportId> getSequencerRunReportIds(SequencerRun seqRun) {
    return sampleReportDAO.getSequencerRunReportIds(seqRun);
  }

  /** {@inheritDoc} */
  @Override
  public List<SequencerRunReportId> getSequencerRunReportIds(SequencerRun seqRun, String sortField, String orderType) {
    return getSequencerRunReportIds(seqRun, sortField, orderType, 0, Integer.MAX_VALUE);
  }

}
