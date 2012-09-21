package net.sourceforge.seqware.common.business.impl;

import java.util.List;

import net.sourceforge.seqware.common.business.FileReportService;
import net.sourceforge.seqware.common.dao.FileReportDAO;
import net.sourceforge.seqware.common.model.FileReportRow;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;

public class FileReportServiceImpl implements FileReportService {

  private FileReportDAO fileReportDAO = null;

  public void setFileReportDAO(FileReportDAO dao) {
    this.fileReportDAO = dao;
  }

  @Override
  public List<FileReportRow> getReportForStudy(Study study) {
    return fileReportDAO.getReportForStudy(study);
  }

  @Override
  public List<FileReportRow> getReportForStudy(Study study, String orderField, String sortOrder, int offset, int limit) {
    return fileReportDAO.getReportForStudy(study, orderField, sortOrder, offset, limit);
  }

  @Override
  public int countOfRows(Study study) {
    return fileReportDAO.countOfRows(study);
  }

  @Override
  public List<FileReportRow> getReportForSequencerRun(SequencerRun seqRun, String sortField, String sortOrder,
      int offset, int limit) {
    if (seqRun != null) {
      return fileReportDAO.getReportForSequencerRun(seqRun, sortField, sortOrder, offset, limit);
    }
    return fileReportDAO.getReportForSequencerRuns(sortField, sortOrder, offset, limit);
  }

  @Override
  public List<FileReportRow> getReportForSequencerRun(SequencerRun seqRun, String sortField, String orderTypeSeqRun) {
    return getReportForSequencerRun(seqRun, sortField, orderTypeSeqRun, 0, Integer.MAX_VALUE);
  }

  @Override
  public List<FileReportRow> getReportForSequencerRun(SequencerRun seqRun) {
    return fileReportDAO.getReportForSequencerRun(seqRun);
  }

  @Override
  public int countOfRows(SequencerRun sr) {
    return fileReportDAO.countOfRows(sr);
  }
}
