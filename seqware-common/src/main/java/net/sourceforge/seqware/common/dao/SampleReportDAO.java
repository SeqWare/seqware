package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SampleReportRow;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.SequencerRunReportId;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.Workflow;

public interface SampleReportDAO {

  List<String> getStatusesForStudy(Study study);

  List<Workflow> getWorkflowsForStudy(Study study);

  int countOfStatus(Study study, String status);

  int countOfStatus(SequencerRun seqRun, String status);

  List<Sample> getChildSamples(Study study);

  String getStatus(Study study, Sample childSample, Workflow workflow);

  String getStatus(Study study, Sample sample, IUS ius, Lane lane, SequencerRun seqRun, Workflow workflow);

  int countOfStatus(Study study, Workflow workflow, String status);

  List<String> getStatusesForWorkflow(Study study, Workflow workflow);

  List<SampleReportRow> getRowsForSequencerRun(SequencerRun sr);

  List<SampleReportRow> getRowsWithSequencerRuns();

  List<String> getStatusesForSequencerRun(SequencerRun seqRun);

  List<String> getStatusesForWorkflow(SequencerRun seqRun, Workflow workflow);

  int countOfStatus(SequencerRun seqRun, Workflow workflow, String status);

  List<Workflow> getWorkflows(SequencerRun seqRun);

  List<SequencerRunReportId> getSequencerRunReportIds(SequencerRun seqRun, String sortField, String sortOrder,
      int offset, int limit);

  int countOfRows(SequencerRun seqRun);

  List<SequencerRunReportId> getSequencerRunReportIds(SequencerRun seqRun);
}
