package net.sourceforge.seqware.common.dao.hibernate;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.seqware.common.business.SampleReportService.Status;
import net.sourceforge.seqware.common.dao.SampleReportDAO;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SampleReportRow;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.SequencerRunReportId;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.Workflow;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>SampleReportDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SampleReportDAOHibernate extends HibernateDaoSupport implements SampleReportDAO {

  private static List<Status> statuses(List result){
    List<Status> statuses = new ArrayList<Status>();
    for (Object obj : result) {
      statuses.add(Status.valueOf((String) obj));
    }
    return statuses;
  }
  
  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  public List<Status> getStatusesForStudy(Study study) {
    String query = "select distinct status from sample_report where study_id = ?";
    List result = this.getSession().createSQLQuery(query).setInteger(0, study.getStudyId()).list();
    return statuses(result);
  }

  /** {@inheritDoc} */
  @Override
  public List<Status> getStatusesForWorkflow(Study study, Workflow workflow) {
    String query = "select distinct status from sample_report where study_id = ? and workflow_id = ?";
    @SuppressWarnings("rawtypes")
    List result = this.getSession().createSQLQuery(query).setInteger(0, study.getStudyId())
        .setInteger(1, workflow.getWorkflowId()).list();
    return statuses(result);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  public List<Workflow> getWorkflowsForStudy(Study study) {
    String query = "select distinct row.workflow from SampleReportRow as row where row.study.studyId = ?";
    List<Workflow> usedWorkflows = new ArrayList<Workflow>();
    Object[] parameters = { study.getStudyId() };
    List list = this.getHibernateTemplate().find(query, parameters);
    for (Object obj : list) {
      usedWorkflows.add((Workflow) obj);
    }
    return usedWorkflows;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  public List<Workflow> getWorkflows(SequencerRun seqRun) {
    if (seqRun != null) {
      String query = "select distinct row.workflow from SampleReportRow as row where row.sequencerRun.sequencerRunId = ?";
      List<Workflow> usedWorkflows = new ArrayList<Workflow>();
      Object[] parameters = { seqRun.getSequencerRunId() };
      List list = this.getHibernateTemplate().find(query, parameters);
      for (Object obj : list) {
        usedWorkflows.add((Workflow) obj);
      }
      return usedWorkflows;
    } else {
      String query = "select distinct row.workflow from SampleReportRow as row where row.sequencerRun.sequencerRunId != null";
      List<Workflow> usedWorkflows = new ArrayList<Workflow>();
      List list = this.getHibernateTemplate().find(query);
      for (Object obj : list) {
        usedWorkflows.add((Workflow) obj);
      }
      return usedWorkflows;
    }
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  public int countOfStatus(Study study, Status status) {
    String query = "select count(status) from sample_report where study_id = ? and status = ?";
    List result = this.getSession().createSQLQuery(query).setInteger(0, study.getStudyId()).setString(1, status.name()).list();
    int count = 0;
    if (result.size() > 0) {
      count = ((BigInteger) result.get(0)).intValue();
    }
    return count;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  public int countOfStatus(Study study, Workflow workflow, Status status) {
    String query = "select count(status) from sample_report where study_id = ? and status = ? and workflow_id = ? ";
    List result = this.getSession().createSQLQuery(query).setInteger(0, study.getStudyId()).setString(1, status.name())
        .setInteger(2, workflow.getWorkflowId()).list();
    int count = 0;
    if (result.size() > 0) {
      count = ((BigInteger) result.get(0)).intValue();
    }
    return count;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  public int countOfStatus(SequencerRun seqRun, Workflow workflow, Status status) {
    if (seqRun != null) {
      String query = "select count(status) from sample_report where sequencer_run_id = ? and status = ? and workflow_id = ? ";
      List result = this.getSession().createSQLQuery(query).setInteger(0, seqRun.getSequencerRunId())
          .setString(1, status.name()).setInteger(2, workflow.getWorkflowId()).list();
      int count = 0;
      if (result.size() > 0) {
        count = ((BigInteger) result.get(0)).intValue();
      }
      return count;
    } else {
      String query = "select count(status) from sample_report where sequencer_run_id is not null and status = ? and workflow_id = ? ";
      List result = this.getSession().createSQLQuery(query).setString(0, status.name())
          .setInteger(1, workflow.getWorkflowId()).list();
      int count = 0;
      if (result.size() > 0) {
        count = ((BigInteger) result.get(0)).intValue();
      }
      return count;
    }
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  public int countOfStatus(SequencerRun seqRun, Status status) {
    if (seqRun != null) {
      String query = "select count(status) from sample_report where sequencer_run_id = ? and status = ? ";
      List result = this.getSession().createSQLQuery(query).setInteger(0, seqRun.getSequencerRunId())
          .setString(1, status.name()).list();
      int count = 0;
      if (result.size() > 0) {
        count = ((BigInteger) result.get(0)).intValue();
      }
      return count;
    } else {
      String query = "select count(status) from sample_report where sequencer_run_id is not null and status = ? ";
      List result = this.getSession().createSQLQuery(query).setString(0, status.name()).list();
      int count = 0;
      if (result.size() > 0) {
        count = ((BigInteger) result.get(0)).intValue();
      }
      return count;
    }
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  public int countOfRows(SequencerRun seqRun) {
    if (seqRun != null) {
      String query = "select count(*) from ( select distinct sr.study_id, sr.child_sample_id, sr.sequencer_run_id, sr.lane_id, sr.ius_id from sample_report sr where sr.sequencer_run_id = ? ) as result";
      List result = this.getSession().createSQLQuery(query).setInteger(0, seqRun.getSequencerRunId()).list();
      int count = 0;
      if (result.size() > 0) {
        count = ((BigInteger) result.get(0)).intValue();
      }
      return count;
    } else {
      String query = "select count(*) from ( select distinct sr.study_id, sr.child_sample_id, sr.sequencer_run_id, sr.lane_id, sr.ius_id from sample_report sr where sr.sequencer_run_id is not null ) as result";
      List result = this.getSession().createSQLQuery(query).list();
      int count = 0;
      if (result.size() > 0) {
        count = ((BigInteger) result.get(0)).intValue();
      }
      return count;
    }
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  public List<Sample> getChildSamples(Study study) {
    String query = "select distinct row.childSample from SampleReportRow as row where row.study.studyId = ?";
    List<Sample> childSamples = new ArrayList<Sample>();
    Object[] parameters = { study.getStudyId() };
    // this.getHibernateTemplate().setFetchSize(5000);
    List list = this.getHibernateTemplate().find(query, parameters);
    for (Object obj : list) {
      childSamples.add((Sample) obj);
    }
    return childSamples;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  public Status getStatus(Study study, Sample childSample, Workflow workflow) {
    String query = "select sr.status from sample_report sr where sr.study_id = ? and sr.child_sample_id = ? and sr.workflow_id = ?";
    List result = this.getSession().createSQLQuery(query).setInteger(0, study.getStudyId())
        .setInteger(1, childSample.getSampleId()).setInteger(2, workflow.getWorkflowId()).list();
    Status status = Status.notstarted;
    if (result.size() > 0) {
      status = Status.valueOf((String) result.get(0));
    }
    return status;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  public Status getStatus(Study study, Sample sample, IUS ius, Lane lane, SequencerRun seqRun, Workflow workflow) {
    String query = "select sr.status from sample_report sr where sr.study_id = ? "
        + " and sr.child_sample_id = ? and sr.workflow_id = ? and sr.sequencer_run_id = ?"
        + " and sr.ius_id = ? and sr.lane_id = ?";
    List result = this.getSession().createSQLQuery(query).setInteger(0, study.getStudyId())
        .setInteger(1, sample.getSampleId()).setInteger(2, workflow.getWorkflowId())
        .setInteger(3, seqRun.getSequencerRunId()).setInteger(4, ius.getIusId()).setInteger(5, lane.getLaneId()).list();
    Status status = Status.notstarted;
    if (result.size() > 0) {
      status = Status.valueOf((String) result.get(0));
    }
    return status;
  }

  /** {@inheritDoc} */
  @Override
  public List<SampleReportRow> getRowsForSequencerRun(SequencerRun sr) {
    String query = "from SampleReportRow as sr where sr.sequencerRun.sequencerRunId = ?";
    Object[] parameters = { sr.getSequencerRunId() };
    @SuppressWarnings({ "unchecked" })
    List<SampleReportRow> list = this.getHibernateTemplate().find(query, parameters);
    return list;
  }

  /** {@inheritDoc} */
  @Override
  public List<SampleReportRow> getRowsWithSequencerRuns() {
    String query = "from SampleReportRow as sr where sr.sequencerRun.sequencerRunId is not null";
    @SuppressWarnings({ "unchecked" })
    List<SampleReportRow> list = this.getHibernateTemplate().find(query);
    return list;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  public List<Status> getStatusesForSequencerRun(SequencerRun seqRun) {
    if (seqRun != null) {
      String query = "select distinct status from sample_report where sequencer_run_id = ?";
      List result = this.getSession().createSQLQuery(query).setInteger(0, seqRun.getSequencerRunId()).list();
      return statuses(result);
    } else {
      String query = "select distinct status from sample_report where sequencer_run_id is not null";
      List result = this.getSession().createSQLQuery(query).list();
      return statuses(result);
    }
  }

  /** {@inheritDoc} */
  @Override
  public List<Status> getStatusesForWorkflow(SequencerRun seqRun, Workflow workflow) {
    if (seqRun != null) {
      String query = "select distinct status from sample_report where sequencer_run_id = ? and workflow_id = ?";
      @SuppressWarnings("rawtypes")
      List result = this.getSession().createSQLQuery(query).setInteger(0, seqRun.getSequencerRunId())
          .setInteger(1, workflow.getWorkflowId()).list();
      return statuses(result);
    } else {
      String query = "select distinct status from sample_report where sequencer_run_id is not null and workflow_id = ?";
      @SuppressWarnings("rawtypes")
      List result = this.getSession().createSQLQuery(query).setInteger(0, workflow.getWorkflowId()).list();
      return statuses(result);
    }
  }

  /** {@inheritDoc} */
  @Override
  public List<SequencerRunReportId> getSequencerRunReportIds(SequencerRun seqRun, String sortField, String sortOrder,
      int offset, int limit) {
    String query = null;
    if (seqRun != null) {
      query = "select distinct sr.study, sr.childSample, sr.sequencerRun, sr.lane, sr.ius ";
      if (sortField != null) {
        query += ", sr." + sortField;
      }
      query += " from SampleReportRow sr where sr.sequencerRun.sequencerRunId = ?";
    } else {
      query = "select distinct sr.study, sr.childSample, sr.sequencerRun, sr.lane, sr.ius";
      if (sortField != null) {
        query += ", sr." + sortField;
      }
      query += " from SampleReportRow sr where sr.sequencerRun.sequencerRunId != null";
    }

    if (sortField != null && sortOrder != null) {
      query += " order by sr." + sortField + " " + sortOrder;
    }

    @SuppressWarnings("rawtypes")
    List result = null;

    if (seqRun != null) {
      result = this.getSession().createQuery(query).setFirstResult(offset).setMaxResults(limit)
          .setInteger(0, seqRun.getSequencerRunId()).list();
    } else {
      result = this.getSession().createQuery(query).setFirstResult(offset).setMaxResults(limit).list();
    }

    List<SequencerRunReportId> keys = new ArrayList<SequencerRunReportId>();

    for (Object obj : result) {
      Object[] objArr = (Object[]) obj;
      SequencerRunReportId runReportId = new SequencerRunReportId();
      runReportId.setStudy((Study) objArr[0]);
      runReportId.setChildSample((Sample) objArr[1]);
      runReportId.setSequencerRun((SequencerRun) objArr[2]);
      runReportId.setLane((Lane) objArr[3]);
      runReportId.setIus((IUS) objArr[4]);
      keys.add(runReportId);
    }

    return keys;
  }

  /** {@inheritDoc} */
  @Override
  public List<SequencerRunReportId> getSequencerRunReportIds(SequencerRun seqRun) {
    return getSequencerRunReportIds(seqRun, null, null, 0, Integer.MAX_VALUE);
  }
}
