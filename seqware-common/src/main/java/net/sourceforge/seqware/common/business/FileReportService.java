package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.model.FileReportRow;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;

/**
 * <p>FileReportService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface FileReportService {
  /**
   * <p>getReportForStudy.</p>
   *
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   * @return a {@link java.util.List} object.
   */
  List<FileReportRow> getReportForStudy(Study study);

  /**
   * <p>getReportForStudy.</p>
   *
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   * @param orderField a {@link java.lang.String} object.
   * @param sortOrder a {@link java.lang.String} object.
   * @param offset a int.
   * @param limit a int.
   * @return a {@link java.util.List} object.
   */
  List<FileReportRow> getReportForStudy(Study study, String orderField, String sortOrder, int offset, int limit);

  /**
   * <p>countOfRows.</p>
   *
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   * @return a int.
   */
  int countOfRows(Study study);

  /**
   * <p>getReportForSequencerRun.</p>
   *
   * @param seqRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
   * @param sortField a {@link java.lang.String} object.
   * @param sortOrder a {@link java.lang.String} object.
   * @param i a int.
   * @param rowsPages a int.
   * @return a {@link java.util.List} object.
   */
  List<FileReportRow> getReportForSequencerRun(SequencerRun seqRun, String sortField, String sortOrder, int i,
      int rowsPages);

  /**
   * <p>getReportForSequencerRun.</p>
   *
   * @param seqRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
   * @param sortField a {@link java.lang.String} object.
   * @param orderTypeSeqRun a {@link java.lang.String} object.
   * @return a {@link java.util.List} object.
   */
  List<FileReportRow> getReportForSequencerRun(SequencerRun seqRun, String sortField, String orderTypeSeqRun);

  /**
   * <p>getReportForSequencerRun.</p>
   *
   * @param seqRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
   * @return a {@link java.util.List} object.
   */
  List<FileReportRow> getReportForSequencerRun(SequencerRun seqRun);

  /**
   * <p>countOfRows.</p>
   *
   * @param sr a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
   * @return a int.
   */
  int countOfRows(SequencerRun sr);

}
