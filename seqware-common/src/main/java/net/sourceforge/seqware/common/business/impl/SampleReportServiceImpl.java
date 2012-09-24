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

public class SampleReportServiceImpl implements SampleReportService {

  private SampleReportDAO sampleReportDAO = null;

  public void setSampleReportDAO(SampleReportDAO dao) {
    this.sampleReportDAO = dao;
  }

  @Override
  public List<String> getStatusesForStudy(Study study) {
    return sampleReportDAO.getStatusesForStudy(study);
  }

  @Override
  public List<Workflow> getWorkflowsForStudy(Study study) {
    return sampleReportDAO.getWorkflowsForStudy(study);
  }

  @Override
  public List<Workflow> getWorkflows(SequencerRun seqRun) {
    return sampleReportDAO.getWorkflows(seqRun);
  }

  @Override
  public int countOfStatus(Study study, String status) {
    return sampleReportDAO.countOfStatus(study, status);
  }

  @Override
  public int countOfStatus(SequencerRun seqRun, String status) {
    return sampleReportDAO.countOfStatus(seqRun, status);
  }

  @Override
  public int countOfStatus(Study study, Workflow workflow, String status) {
    return sampleReportDAO.countOfStatus(study, workflow, status);
  }

  @Override
  public int countOfStatus(SequencerRun seqRun, Workflow workflow, String status) {
    return sampleReportDAO.countOfStatus(seqRun, workflow, status);
  }

  @Override
  public int countOfRows(SequencerRun seqRun) {
    return sampleReportDAO.countOfRows(seqRun);
  }

  @Override
  public List<Sample> getChildSamples(Study study) {
    return sampleReportDAO.getChildSamples(study);
  }

  @Override
  public String getStatus(Study study, Sample childSample, Workflow workflow) {
    return sampleReportDAO.getStatus(study, childSample, workflow);
  }

  @Override
  public String getStatus(Study study, Sample sample, IUS ius, Lane lane, SequencerRun seqRun, Workflow workflow) {
    if (seqRun == null) {
      return getStatus(study, sample, workflow);
    }
    return sampleReportDAO.getStatus(study, sample, ius, lane, seqRun, workflow);
  }

  @Override
  public List<String> getStatusesForWorkflow(Study study, Workflow workflow) {
    return sampleReportDAO.getStatusesForWorkflow(study, workflow);
  }

  @Override
  public List<SampleReportRow> getRowsForSequencerRun(SequencerRun sr) {
    if (sr == null) {
      return getRowsWithSequencerRuns();
    }
    return sampleReportDAO.getRowsForSequencerRun(sr);
  }

  @Override
  public List<SampleReportRow> getRowsWithSequencerRuns() {
    return sampleReportDAO.getRowsWithSequencerRuns();
  }

  @Override
  public List<String> getStatusesForWorkflow(SequencerRun seqRun, Workflow workflow) {
    return sampleReportDAO.getStatusesForWorkflow(seqRun, workflow);
  }

  @Override
  public List<String> getStatusesForSequencerRun(SequencerRun seqRun) {
    return sampleReportDAO.getStatusesForSequencerRun(seqRun);
  }

  @Override
  public List<SequencerRunReportId> getSequencerRunReportIds(SequencerRun seqRun, String sortField, String sortOrder,
      int offset, int limit) {
    return sampleReportDAO.getSequencerRunReportIds(seqRun, sortField, sortOrder, offset, limit);
  }

  @Override
  public List<SequencerRunReportId> getSequencerRunReportIds(SequencerRun seqRun) {
    return sampleReportDAO.getSequencerRunReportIds(seqRun);
  }

  @Override
  public List<SequencerRunReportId> getSequencerRunReportIds(SequencerRun seqRun, String sortField, String orderType) {
    return getSequencerRunReportIds(seqRun, sortField, orderType, 0, Integer.MAX_VALUE);
  }

}
