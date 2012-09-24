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

public class WorkflowServiceImpl implements WorkflowService {

  private WorkflowDAO workflowDAO = null;
  private static final Log log = LogFactory.getLog(WorkflowServiceImpl.class);

  public WorkflowServiceImpl() {
    super();
  }

  /**
   * Sets a private member variable with an instance of an implementation of
   * WorkflowDAO. This method is called by the Spring framework at run time.
   * 
   * @param workflowDAO
   *          implementation of WorkflowDAO
   * @see WorkflowDAO
   */
  public void setWorkflowDAO(WorkflowDAO workflowDAO) {
    this.workflowDAO = workflowDAO;
  }

  @Override
  public Integer insert(Workflow workflow) {
    workflow.setCreateTimestamp(new Date());
    return workflowDAO.insert(workflow);
  }

  public void update(Workflow workflow) {
    workflowDAO.update(workflow);
  }

  public void delete(Workflow workflow) {
    workflowDAO.delete(workflow);
  }

  public List<Workflow> list() {
    return workflowDAO.list();
  }

  public List<Workflow> list(Registration registration) {
    return workflowDAO.list(registration);
  }

  public List<Workflow> listMyShared(Registration registration) {
    return workflowDAO.listMyShared(registration);
  }

  public List<Workflow> listSharedWithMe(Registration registration) {
    return workflowDAO.listSharedWithMe(registration);
  }

  @Override
  public List<Workflow> listSequencerRunsWorkflows(SequencerRun sr) {
    return workflowDAO.listWorkflows(sr);
  }

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

  @Override
  public List<Workflow> findByCriteria(String criteria, boolean isCaseSens) {
    return workflowDAO.findByCriteria(criteria, isCaseSens);
  }

  @Override
  public Workflow updateDetached(Workflow workflow) {
    return workflowDAO.updateDetached(workflow);
  }

  @Override
  public Integer insert(Registration registration, Workflow workflow) {
    workflow.setCreateTimestamp(new Date());
    return workflowDAO.insert(registration, workflow);
  }

  @Override
  public Workflow updateDetached(Registration registration, Workflow workflow) {
    return workflowDAO.updateDetached(registration, workflow);
  }

  @Override
  public void update(Registration registration, Workflow workflow) {
    workflowDAO.update(registration, workflow);
  }
}

// ex:sw=4:ts=4:
