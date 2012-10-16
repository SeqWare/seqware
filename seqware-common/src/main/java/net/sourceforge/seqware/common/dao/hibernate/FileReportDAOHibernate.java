package net.sourceforge.seqware.common.dao.hibernate;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.seqware.common.dao.FileReportDAO;
import net.sourceforge.seqware.common.model.FileReportRow;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>FileReportDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class FileReportDAOHibernate extends HibernateDaoSupport implements FileReportDAO {

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  public List<FileReportRow> getReportForStudy(Study study) {
    String query = "from FileReportRow as row where row.study.studyId = ?";
    List<FileReportRow> fileReport = new ArrayList<FileReportRow>();
    Object[] parameters = { study.getStudyId() };
    List list = this.getHibernateTemplate().find(query, parameters);
    for (Object obj : list) {
      fileReport.add((FileReportRow) obj);
    }
    return fileReport;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  public List<FileReportRow> getReportForStudy(Study study, String orderField, String sortOrder, int offset, int limit) {
    String query = "from FileReportRow as row where row.study.studyId = ? order by row." + orderField + " " + sortOrder;
    List<FileReportRow> fileReport = new ArrayList<FileReportRow>();
    List list = this.getSession().createQuery(query).setFirstResult(offset).setMaxResults(limit)
        .setInteger(0, study.getStudyId()).list();
    for (Object obj : list) {
      fileReport.add((FileReportRow) obj);
    }
    return fileReport;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  public List<FileReportRow> getReportForSequencerRun(SequencerRun seqRun, String orderField, String sortOrder,
      int offset, int limit) {
    String query = "from FileReportRow as row where row.lane.sequencerRun.sequencerRunId = ? order by row."
        + orderField + " " + sortOrder;
    List<FileReportRow> fileReport = new ArrayList<FileReportRow>();
    List list = this.getSession().createQuery(query).setFirstResult(offset).setMaxResults(limit)
        .setInteger(0, seqRun.getSequencerRunId()).list();
    for (Object obj : list) {
      fileReport.add((FileReportRow) obj);
    }
    return fileReport;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  public List<FileReportRow> getReportForSequencerRuns(String orderField, String sortOrder, int offset, int limit) {
    String query = "from FileReportRow as row where row.lane.sequencerRun.sequencerRunId != null order by row."
        + orderField + " " + sortOrder;
    List<FileReportRow> fileReport = new ArrayList<FileReportRow>();
    List list = this.getSession().createQuery(query).setFirstResult(offset).setMaxResults(limit).list();
    for (Object obj : list) {
      fileReport.add((FileReportRow) obj);
    }
    return fileReport;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  public List<FileReportRow> getReportForSequencerRun(SequencerRun seqRun) {
    if (seqRun == null) {
      String query = "from FileReportRow as row where row.lane.sequencerRun.sequencerRunId != null ";
      List<FileReportRow> fileReport = new ArrayList<FileReportRow>();
      List list = this.getSession().createQuery(query).list();
      for (Object obj : list) {
        fileReport.add((FileReportRow) obj);
      }
      return fileReport;
    } else {
      String query = "from FileReportRow as row where row.lane.sequencerRun.sequencerRunId = ? ";
      List<FileReportRow> fileReport = new ArrayList<FileReportRow>();
      List list = this.getSession().createQuery(query).setInteger(0, seqRun.getSequencerRunId()).list();
      for (Object obj : list) {
        fileReport.add((FileReportRow) obj);
      }
      return fileReport;
    }
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  public int countOfRows(Study study) {
    String query = "select count(*) from file_report where study_id = ?";
    List result = this.getSession().createSQLQuery(query).setInteger(0, study.getStudyId()).list();
    int count = 0;
    if (result.size() > 0) {
      count = ((BigInteger) result.get(0)).intValue();
    }
    return count;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  public int countOfRows(SequencerRun sr) {
    String query = null;
    List result = null;
    if (sr != null) {
      query = "select count(*) from file_report as fr cross join lane as la "
          + "where fr.lane_id=la.lane_id and (la.sequencer_run_id = ?)";
      result = this.getSession().createSQLQuery(query).setInteger(0, sr.getSequencerRunId()).list();
    } else {
      query = "select count(*) from file_report as fr cross join lane as la "
          + "where fr.lane_id=la.lane_id and (la.sequencer_run_id is not null)";
      result = this.getSession().createSQLQuery(query).list();

    }
    int count = 0;
    if (result.size() > 0) {
      count = ((BigInteger) result.get(0)).intValue();
    }
    return count;
  }

}
