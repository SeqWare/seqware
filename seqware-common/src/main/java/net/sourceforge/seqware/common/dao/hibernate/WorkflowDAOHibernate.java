package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.seqware.common.dao.WorkflowDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class WorkflowDAOHibernate extends HibernateDaoSupport implements WorkflowDAO {

  public WorkflowDAOHibernate() {
    super();
  }

  @Override
  public Integer insert(Workflow workflow) {
    this.getHibernateTemplate().save(workflow);
    this.getSession().flush();
    return workflow.getSwAccession();
  }

  public void update(Workflow workflow) {
    getHibernateTemplate().update(workflow);
    getSession().flush();
  }

  public void delete(Workflow workflow) {
    getHibernateTemplate().delete(workflow);
  }

  public List<Workflow> list() {
    ArrayList<Workflow> workflows = new ArrayList<Workflow>();

    List expmts = null;

    // Limit the workflows to those owned by the user
    expmts = this.getHibernateTemplate()
        .find("from Workflow as workflow order by workflow.name, workflow.version desc");

    // expmts =
    // this.getHibernateTemplate().find("from Workflow as workflow order by workflow.name desc");
    for (Object workflow : expmts) {
      workflows.add((Workflow) workflow);
    }
    return workflows;
  }

  public List<Workflow> list(Registration registration) {
    ArrayList<Workflow> workflows = new ArrayList<Workflow>();

    // Limit the workflows to those owned by the user
    String query = "";
    Object[] parameters = { registration.getRegistrationId() };

    if (registration.isLIMSAdmin()) {
      query = "from Workflow as workflow order by create_tstmp desc ";
      parameters = null;
    } else {
      query = "from Workflow as workflow where workflow.owner.registrationId=? order by create_tstmp desc";
    }

    List list = this.getHibernateTemplate().find(query, parameters);

    for (Object workflow : list) {
      workflows.add((Workflow) workflow);
    }
    return workflows;
  }

  public List<Workflow> listMyShared(Registration registration) {
    List<Workflow> sharedWorkflows = new ArrayList<Workflow>();
    List<Workflow> workflows = list(registration);
    for (Workflow workflow : workflows) {
      /*
       * if(!workflow.getSharedStudies().isEmpty()){ sharedWorkflows.add(study);
       * }
       */
    }
    return sharedWorkflows;
  }

  public List<Workflow> listSharedWithMe(Registration registration) {
    return new ArrayList<Workflow>();
  }

  public List<Workflow> findByName(String name) {
    String query = "from Workflow as workflow where workflow.name = ?";
    Object[] parameters = { name };
    List list = this.getHibernateTemplate().find(query, parameters);
    return list;
  }

  public Workflow findByID(Integer wfID) {
    String query = "from Workflow as workflow where workflow.workflowId = ?";
    Workflow workflow = null;
    Object[] parameters = { wfID };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      workflow = (Workflow) list.get(0);
    }
    return workflow;
  }

  @Override
  public Workflow findBySWAccession(Integer swAccession) {
    String query = "from Workflow as workflow where workflow.swAccession = ?";
    Workflow workflow = null;
    Object[] parameters = { swAccession };
    List<Workflow> list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      workflow = (Workflow) list.get(0);
    } else
    {
        Log.error("Could not find workflow of swaccession = "+swAccession);
    }
    return workflow;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Workflow> findByCriteria(String criteria, boolean isCaseSens) {
    String queryStringCase = "from Workflow as w where w.description like :description "
        + " or cast(w.swAccession as string) like :sw " + " or w.name like :name order by w.name, w.description";
    String queryStringICase = "from Workflow as w where lower(w.description) like :description "
        + " or cast(w.swAccession as string) like :sw " + " or lower(w.name) like :name order by w.name, w.description";
    Query query = isCaseSens ? this.getSession().createQuery(queryStringCase) : this.getSession().createQuery(
        queryStringICase);
    if (!isCaseSens) {
      criteria = criteria.toLowerCase();
    }
    criteria = "%" + criteria + "%";
    query.setString("description", criteria);
    query.setString("sw", criteria);
    query.setString("name", criteria);

    return query.list();
  }

  @Override
  public Workflow updateDetached(Workflow workflow) {
    Workflow dbObject = findByID(workflow.getWorkflowId());
    try {
      BeanUtilsBean beanUtils = new NullBeanUtils();
      beanUtils.copyProperties(dbObject, workflow);
      return (Workflow) this.getHibernateTemplate().merge(dbObject);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void update(Registration registration, Workflow workflow) {
    Workflow dbObject = reattachWorkflow(workflow);
    Logger logger = Logger.getLogger(WorkflowDAOHibernate.class);
    if (registration == null) {
      logger.error("WorkflowDAOHibernate update: Registration is null - exiting");
    } else if (registration.isLIMSAdmin()
        || (workflow.givesPermission(registration) && dbObject.givesPermission(registration))) {
      logger.info("Updating workflow object");
      update(workflow);
    } else {
      logger.error("WorkflowDAOHibernate update: Registration is incorrect - exiting");
    }
  }

  @Override
  public Integer insert(Registration registration, Workflow workflow) {
    Logger logger = Logger.getLogger(WorkflowDAOHibernate.class);
    if (registration == null) {
      logger.error("WorkflowDAOHibernate insert: Registration is null - exiting");
    } else {
      logger.info("insert workflow object");
      return insert(workflow);
    }
    return null;
    // else if (registration.equals(workflow.getOwner())) {
    // logger.info("insert workflow object");
    // insert(workflow);
    // } else {
    // logger.error("WorkflowDAOHibernate update: Registration is incorrect - exiting");
    // }

  }

  @Override
  public List<Workflow> listWorkflows(SequencerRun sr) {
    String query = null;
    if (sr != null) {
      query = "select distinct w.* from sequencer_run sr " + "join processing_sequencer_runs psr "
          + "on (sr.sequencer_run_id = psr.sequencer_run_id) " + "join processing p "
          + "on (p.processing_id = psr.processing_id) " + "join workflow_run wr "
          + "on (wr.workflow_run_id = p.workflow_run_id) " + "join workflow w "
          + "on (w.workflow_id = wr.workflow_id) " + "where sr.sequencer_run_id = :seq_run_id " + "union "
          + "select distinct w.* from sequencer_run sr  " + "join lane l "
          + "on (l.sequencer_run_id = sr.sequencer_run_id) " + "join ius i " + "on (i.lane_id = l.lane_id) "
          + "join ius_workflow_runs iwr " + "on (iwr.ius_id = i.ius_id) " + "join workflow_run wr "
          + "on (wr.workflow_run_id = iwr.workflow_run_id) " + "join workflow w "
          + "on (w.workflow_id = wr.workflow_id) " + "where sr.sequencer_run_id = :seq_run_id ";
    } else {
      query = "select distinct w.* from sequencer_run sr " + "join processing_sequencer_runs psr "
          + "on (sr.sequencer_run_id = psr.sequencer_run_id) " + "join processing p "
          + "on (p.processing_id = psr.processing_id) " + "join workflow_run wr "
          + "on (wr.workflow_run_id = p.workflow_run_id) " + "join workflow w "
          + "on (w.workflow_id = wr.workflow_id) " + "union " + "select distinct w.* from sequencer_run sr  "
          + "join lane l " + "on (l.sequencer_run_id = sr.sequencer_run_id) " + "join ius i "
          + "on (i.lane_id = l.lane_id) " + "join ius_workflow_runs iwr " + "on (iwr.ius_id = i.ius_id) "
          + "join workflow_run wr " + "on (wr.workflow_run_id = iwr.workflow_run_id) " + "join workflow w "
          + "on (w.workflow_id = wr.workflow_id) ";
    }

    SQLQuery sqlQuery = this.getSession().createSQLQuery(query);
    if (sr != null) {
      sqlQuery.setInteger("seq_run_id", sr.getSequencerRunId());
    }
    @SuppressWarnings("rawtypes")
    List result = sqlQuery.addEntity(Workflow.class).list();
    List<Workflow> workflows = new ArrayList<Workflow>(result.size());
    for (Object obj : result) {
      workflows.add((Workflow) obj);
    }
    return workflows;
  }

  @Override
  public Workflow updateDetached(Registration registration, Workflow workflow) {
    Workflow dbObject = reattachWorkflow(workflow);
    Logger logger = Logger.getLogger(WorkflowDAOHibernate.class);
    if (registration == null) {
      logger.error("WorkflowDAOHibernate updateDetached: Registration is null - exiting");
    } else if (registration.isLIMSAdmin() || dbObject.givesPermission(registration)) {
      logger.info("updateDetached workflow object");
      return updateDetached(workflow);
    } else {
      logger.error("WorkflowDAOHibernate updateDetached: Registration is incorrect - exiting");
    }
    return null;
  }

  private Workflow reattachWorkflow(Workflow workflow) throws IllegalStateException {
    Workflow dbObject = workflow;
    if (!getSession().contains(workflow)) {
      dbObject = findByID(workflow.getWorkflowId());
    }
    return dbObject;
  }
}
