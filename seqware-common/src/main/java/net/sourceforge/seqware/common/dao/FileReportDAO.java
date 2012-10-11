package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.FileReportRow;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;

public interface FileReportDAO {

  List<FileReportRow> getReportForStudy(Study study);

  List<FileReportRow> getReportForStudy(Study study, String orderField, String sortOrder, int offset, int limit);

  int countOfRows(Study study);

  List<FileReportRow> getReportForSequencerRun(SequencerRun seqRun, String orderField, String sortOrder, int offset,
      int limit);

  List<FileReportRow> getReportForSequencerRuns(String orderField, String sortOrder, int offset, int limit);

  int countOfRows(SequencerRun sr);

  List<FileReportRow> getReportForSequencerRun(SequencerRun seqRun);
}
