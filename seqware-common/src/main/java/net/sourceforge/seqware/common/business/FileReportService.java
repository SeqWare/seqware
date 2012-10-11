package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.model.FileReportRow;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;

public interface FileReportService {
  List<FileReportRow> getReportForStudy(Study study);

  List<FileReportRow> getReportForStudy(Study study, String orderField, String sortOrder, int offset, int limit);

  int countOfRows(Study study);

  List<FileReportRow> getReportForSequencerRun(SequencerRun seqRun, String sortField, String sortOrder, int i,
      int rowsPages);

  List<FileReportRow> getReportForSequencerRun(SequencerRun seqRun, String sortField, String orderTypeSeqRun);

  List<FileReportRow> getReportForSequencerRun(SequencerRun seqRun);

  int countOfRows(SequencerRun sr);

}
