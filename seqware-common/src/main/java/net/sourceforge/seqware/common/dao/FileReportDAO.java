package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.FileReportRow;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;

/**
 * <p>FileReportDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface FileReportDAO {

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
   * @param orderField a {@link java.lang.String} object.
   * @param sortOrder a {@link java.lang.String} object.
   * @param offset a int.
   * @param limit a int.
   * @return a {@link java.util.List} object.
   */
  List<FileReportRow> getReportForSequencerRun(SequencerRun seqRun, String orderField, String sortOrder, int offset,
      int limit);

  /**
   * <p>getReportForSequencerRuns.</p>
   *
   * @param orderField a {@link java.lang.String} object.
   * @param sortOrder a {@link java.lang.String} object.
   * @param offset a int.
   * @param limit a int.
   * @return a {@link java.util.List} object.
   */
  List<FileReportRow> getReportForSequencerRuns(String orderField, String sortOrder, int offset, int limit);

  /**
   * <p>countOfRows.</p>
   *
   * @param sr a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
   * @return a int.
   */
  int countOfRows(SequencerRun sr);

  /**
   * <p>getReportForSequencerRun.</p>
   *
   * @param seqRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
   * @return a {@link java.util.List} object.
   */
  List<FileReportRow> getReportForSequencerRun(SequencerRun seqRun);
}
