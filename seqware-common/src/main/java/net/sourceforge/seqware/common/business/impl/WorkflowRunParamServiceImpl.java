package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.business.WorkflowRunParamService;
import net.sourceforge.seqware.common.dao.WorkflowRunDAO;
import net.sourceforge.seqware.common.dao.WorkflowRunParamDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.WorkflowRunParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WorkflowRunParamServiceImpl implements WorkflowRunParamService {
  private WorkflowRunParamDAO workflowRunParamDAO = null;
  private static final Log log = LogFactory.getLog(WorkflowRunParamServiceImpl.class);

  public WorkflowRunParamServiceImpl() {
    super();
  }

  /**
   * Sets a private member variable with an instance of an implementation of
   * WorkflowRunDAO. This method is called by the Spring framework at run time.
   * 
   * @param WorkflowRunDAO
   *          implementation of WorkflowRunDAO
   * @see WorkflowRunDAO
   */
  public void setWorkflowRunParamDAO(WorkflowRunParamDAO workflowRunParamDAO) {
    this.workflowRunParamDAO = workflowRunParamDAO;
  }

  public void insert(WorkflowRunParam workflowRunParam) {
    workflowRunParamDAO.insert(workflowRunParam);
  }

  public void update(WorkflowRunParam workflowRunParam) {
    workflowRunParamDAO.update(workflowRunParam);
  }

  public void delete(WorkflowRunParam workflowRunParam) {
    workflowRunParamDAO.delete(workflowRunParam);
  }

  @Override
  public WorkflowRunParam updateDetached(WorkflowRunParam workflowRunParam) {
    return workflowRunParamDAO.updateDetached(workflowRunParam);
  }

    @Override
    public List<WorkflowRunParam> list() {
        return workflowRunParamDAO.list();
    }

    @Override
    public void update(Registration registration, WorkflowRunParam workflowRunParam) {
        workflowRunParamDAO.update(registration, workflowRunParam);
    }

    @Override
    public void insert(Registration registration, WorkflowRunParam workflowRunParam) {
        workflowRunParamDAO.insert(registration, workflowRunParam);
    }

    @Override
    public WorkflowRunParam updateDetached(Registration registration, WorkflowRunParam workflowRunParam) {
        return workflowRunParamDAO.updateDetached(registration, workflowRunParam);
    }
}