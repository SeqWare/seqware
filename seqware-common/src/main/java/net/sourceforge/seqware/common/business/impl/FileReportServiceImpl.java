package net.sourceforge.seqware.common.business.impl;

import java.util.List;

import net.sourceforge.seqware.common.business.FileReportService;
import net.sourceforge.seqware.common.dao.FileReportDAO;
import net.sourceforge.seqware.common.model.FileReportRow;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;

/**
 * <p>FileReportServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class FileReportServiceImpl implements FileReportService {

  private FileReportDAO fileReportDAO = null;

  /**
   * <p>Setter for the field <code>fileReportDAO</code>.</p>
   *
   * @param dao a {@link net.sourceforge.seqware.common.dao.FileReportDAO} object.
   */
  public void setFileReportDAO(FileReportDAO dao) {
    this.fileReportDAO = dao;
  }

  /** {@inheritDoc} */
  @Override
  public List<FileReportRow> getReportForStudy(Study study) {
    return fileReportDAO.getReportForStudy(study);
  }

  /** {@inheritDoc} */
  @Override
  public List<FileReportRow> getReportForStudy(Study study, String orderField, String sortOrder, int offset, int limit) {
    return fileReportDAO.getReportForStudy(study, orderField, sortOrder, offset, limit);
  }

  /** {@inheritDoc} */
  @Override
  public int countOfRows(Study study) {
    return fileReportDAO.countOfRows(study);
  }

  /** {@inheritDoc} */
  @Override
  public List<FileReportRow> getReportForSequencerRun(SequencerRun seqRun, String sortField, String sortOrder,
      int offset, int limit) {
    if (seqRun != null) {
      return fileReportDAO.getReportForSequencerRun(seqRun, sortField, sortOrder, offset, limit);
    }
    return fileReportDAO.getReportForSequencerRuns(sortField, sortOrder, offset, limit);
  }

  /** {@inheritDoc} */
  @Override
  public List<FileReportRow> getReportForSequencerRun(SequencerRun seqRun, String sortField, String orderTypeSeqRun) {
    return getReportForSequencerRun(seqRun, sortField, orderTypeSeqRun, 0, Integer.MAX_VALUE);
  }

  /** {@inheritDoc} */
  @Override
  public List<FileReportRow> getReportForSequencerRun(SequencerRun seqRun) {
    return fileReportDAO.getReportForSequencerRun(seqRun);
  }

  /** {@inheritDoc} */
  @Override
  public int countOfRows(SequencerRun sr) {
    return fileReportDAO.countOfRows(sr);
  }
}
