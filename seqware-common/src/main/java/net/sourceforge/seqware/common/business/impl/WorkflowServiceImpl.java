package net.sourceforge.seqware.common.business.impl;

import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.dao.WorkflowDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>WorkflowServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class WorkflowServiceImpl implements WorkflowService {

  private WorkflowDAO workflowDAO = null;
  private static final Log log = LogFactory.getLog(WorkflowServiceImpl.class);

  /**
   * <p>Constructor for WorkflowServiceImpl.</p>
   */
  public WorkflowServiceImpl() {
    super();
  }

  /**
   * {@inheritDoc}
   *
   * Sets a private member variable with an instance of an implementation of
   * WorkflowDAO. This method is called by the Spring framework at run time.
   * @see WorkflowDAO
   */
  public void setWorkflowDAO(WorkflowDAO workflowDAO) {
    this.workflowDAO = workflowDAO;
  }

  /** {@inheritDoc} */
  @Override
  public Integer insert(Workflow workflow) {
    workflow.setCreateTimestamp(new Date());
    return workflowDAO.insert(workflow);
  }

  /** {@inheritDoc} */
  public void update(Workflow workflow) {
    workflowDAO.update(workflow);
  }

  /** {@inheritDoc} */
  public void delete(Workflow workflow) {
    workflowDAO.delete(workflow);
  }

  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<Workflow> list() {
    return workflowDAO.list();
  }

  /** {@inheritDoc} */
  public List<Workflow> list(Registration registration) {
    return workflowDAO.list(registration);
  }

  /** {@inheritDoc} */
  public List<Workflow> listMyShared(Registration registration) {
    return workflowDAO.listMyShared(registration);
  }

  /** {@inheritDoc} */
  public List<Workflow> listSharedWithMe(Registration registration) {
    return workflowDAO.listSharedWithMe(registration);
  }

  /** {@inheritDoc} */
  @Override
  public List<Workflow> listSequencerRunsWorkflows(SequencerRun sr) {
    return workflowDAO.listWorkflows(sr);
  }

  /** {@inheritDoc} */
  public List<Workflow> findByName(String name) {
    List<Workflow> workflows = null;
    if (name != null) {
      try {
        workflows = workflowDAO.findByName(name.trim());
      } catch (Exception exception) {
        log.debug("Cannot find Workflow by name " + name);
      }
    }
    return workflows;
  }

  /** {@inheritDoc} */
  public Workflow findByID(Integer wfID) {
    Workflow workflow = null;
    if (wfID != null) {
      try {
        workflow = workflowDAO.findByID(wfID);
      } catch (Exception exception) {
        log.error("Cannot find Workflow by wfID " + wfID);
        log.error(exception.getMessage());
      }
    }
    return workflow;
  }

  /** {@inheritDoc} */
  @Override
  public Workflow findByIDWithParams(Integer wfID) {
    Workflow workflow = findByID(wfID);
    if (workflow != null) {
      SortedSet<WorkflowParam> params = workflow.getWorkflowParams();
      for (WorkflowParam param : params) {
        param.getWorkflowParamId();
      }
    }
    return workflow;
  }

  /** {@inheritDoc} */
  @Override
  public Workflow findBySWAccession(Integer swAccession) {
    Workflow workflow = null;
    if (swAccession != null) {
      try {
        workflow = workflowDAO.findBySWAccession(swAccession);
      } catch (Exception exception) {
        log.error("Cannot find Workflow by swAccession " + swAccession);
        log.error(exception.getMessage());
      }
    }
    return workflow;
  }

  /** {@inheritDoc} */
  @Override
  public List<Workflow> findByCriteria(String criteria, boolean isCaseSens) {
    return workflowDAO.findByCriteria(criteria, isCaseSens);
  }

  /** {@inheritDoc} */
  @Override
  public Workflow updateDetached(Workflow workflow) {
    return workflowDAO.updateDetached(workflow);
  }

  /** {@inheritDoc} */
  @Override
  public Integer insert(Registration registration, Workflow workflow) {
    workflow.setCreateTimestamp(new Date());
    return workflowDAO.insert(registration, workflow);
  }

  /** {@inheritDoc} */
  @Override
  public Workflow updateDetached(Registration registration, Workflow workflow) {
    return workflowDAO.updateDetached(registration, workflow);
  }

  /** {@inheritDoc} */
  @Override
  public void update(Registration registration, Workflow workflow) {
    workflowDAO.update(registration, workflow);
  }
}

// ex:sw=4:ts=4:
