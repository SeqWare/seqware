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

/**
 * <p>SampleReportDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface SampleReportDAO {

  /**
   * <p>getStatusesForStudy.</p>
   *
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   * @return a {@link java.util.List} object.
   */
  List<String> getStatusesForStudy(Study study);

  /**
   * <p>getWorkflowsForStudy.</p>
   *
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   * @return a {@link java.util.List} object.
   */
  List<Workflow> getWorkflowsForStudy(Study study);

  /**
   * <p>countOfStatus.</p>
   *
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   * @param status a {@link java.lang.String} object.
   * @return a int.
   */
  int countOfStatus(Study study, String status);

  /**
   * <p>countOfStatus.</p>
   *
   * @param seqRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
   * @param status a {@link java.lang.String} object.
   * @return a int.
   */
  int countOfStatus(SequencerRun seqRun, String status);

  /**
   * <p>getChildSamples.</p>
   *
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   * @return a {@link java.util.List} object.
   */
  List<Sample> getChildSamples(Study study);

  /**
   * <p>getStatus.</p>
   *
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   * @param childSample a {@link net.sourceforge.seqware.common.model.Sample} object.
   * @param workflow a {@link net.sourceforge.seqware.common.model.Workflow} object.
   * @return a {@link java.lang.String} object.
   */
  String getStatus(Study study, Sample childSample, Workflow workflow);

  /**
   * <p>getStatus.</p>
   *
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
   * @param ius a {@link net.sourceforge.seqware.common.model.IUS} object.
   * @param lane a {@link net.sourceforge.seqware.common.model.Lane} object.
   * @param seqRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
   * @param workflow a {@link net.sourceforge.seqware.common.model.Workflow} object.
   * @return a {@link java.lang.String} object.
   */
  String getStatus(Study study, Sample sample, IUS ius, Lane lane, SequencerRun seqRun, Workflow workflow);

  /**
   * <p>countOfStatus.</p>
   *
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   * @param workflow a {@link net.sourceforge.seqware.common.model.Workflow} object.
   * @param status a {@link java.lang.String} object.
   * @return a int.
   */
  int countOfStatus(Study study, Workflow workflow, String status);

  /**
   * <p>getStatusesForWorkflow.</p>
   *
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   * @param workflow a {@link net.sourceforge.seqware.common.model.Workflow} object.
   * @return a {@link java.util.List} object.
   */
  List<String> getStatusesForWorkflow(Study study, Workflow workflow);

  /**
   * <p>getRowsForSequencerRun.</p>
   *
   * @param sr a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
   * @return a {@link java.util.List} object.
   */
  List<SampleReportRow> getRowsForSequencerRun(SequencerRun sr);

  /**
   * <p>getRowsWithSequencerRuns.</p>
   *
   * @return a {@link java.util.List} object.
   */
  List<SampleReportRow> getRowsWithSequencerRuns();

  /**
   * <p>getStatusesForSequencerRun.</p>
   *
   * @param seqRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
   * @return a {@link java.util.List} object.
   */
  List<String> getStatusesForSequencerRun(SequencerRun seqRun);

  /**
   * <p>getStatusesForWorkflow.</p>
   *
   * @param seqRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
   * @param workflow a {@link net.sourceforge.seqware.common.model.Workflow} object.
   * @return a {@link java.util.List} object.
   */
  List<String> getStatusesForWorkflow(SequencerRun seqRun, Workflow workflow);

  /**
   * <p>countOfStatus.</p>
   *
   * @param seqRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
   * @param workflow a {@link net.sourceforge.seqware.common.model.Workflow} object.
   * @param status a {@link java.lang.String} object.
   * @return a int.
   */
  int countOfStatus(SequencerRun seqRun, Workflow workflow, String status);

  /**
   * <p>getWorkflows.</p>
   *
   * @param seqRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
   * @return a {@link java.util.List} object.
   */
  List<Workflow> getWorkflows(SequencerRun seqRun);

  /**
   * <p>getSequencerRunReportIds.</p>
   *
   * @param seqRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
   * @param sortField a {@link java.lang.String} object.
   * @param sortOrder a {@link java.lang.String} object.
   * @param offset a int.
   * @param limit a int.
   * @return a {@link java.util.List} object.
   */
  List<SequencerRunReportId> getSequencerRunReportIds(SequencerRun seqRun, String sortField, String sortOrder,
      int offset, int limit);

  /**
   * <p>countOfRows.</p>
   *
   * @param seqRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
   * @return a int.
   */
  int countOfRows(SequencerRun seqRun);

  /**
   * <p>getSequencerRunReportIds.</p>
   *
   * @param seqRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
   * @return a {@link java.util.List} object.
   */
  List<SequencerRunReportId> getSequencerRunReportIds(SequencerRun seqRun);
}
