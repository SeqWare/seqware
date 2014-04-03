package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.business.WorkflowRunParamService;
import net.sourceforge.seqware.common.dao.WorkflowRunDAO;
import net.sourceforge.seqware.common.dao.WorkflowRunParamDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.WorkflowRunParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>WorkflowRunParamServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class WorkflowRunParamServiceImpl implements WorkflowRunParamService {
  private WorkflowRunParamDAO workflowRunParamDAO = null;
  private static final Log log = LogFactory.getLog(WorkflowRunParamServiceImpl.class);

  /**
   * <p>Constructor for WorkflowRunParamServiceImpl.</p>
   */
  public WorkflowRunParamServiceImpl() {
    super();
  }

  /**
   * {@inheritDoc}
   *
   * Sets a private member variable with an instance of an implementation of
   * WorkflowRunDAO. This method is called by the Spring framework at run time.
   * @see WorkflowRunDAO
   */
  public void setWorkflowRunParamDAO(WorkflowRunParamDAO workflowRunParamDAO) {
    this.workflowRunParamDAO = workflowRunParamDAO;
  }

  /** {@inheritDoc} */
  public void insert(WorkflowRunParam workflowRunParam) {
    workflowRunParamDAO.insert(workflowRunParam);
  }

  /** {@inheritDoc} */
  public void update(WorkflowRunParam workflowRunParam) {
    workflowRunParamDAO.update(workflowRunParam);
  }

  /** {@inheritDoc} */
  public void delete(WorkflowRunParam workflowRunParam) {
    workflowRunParamDAO.delete(workflowRunParam);
  }

  /** {@inheritDoc} */
  @Override
  public WorkflowRunParam updateDetached(WorkflowRunParam workflowRunParam) {
    return workflowRunParamDAO.updateDetached(workflowRunParam);
  }

    /** {@inheritDoc} */
    @Override
    public List<WorkflowRunParam> list() {
        return workflowRunParamDAO.list();
    }

    /** {@inheritDoc} */
    @Override
    public void update(Registration registration, WorkflowRunParam workflowRunParam) {
        workflowRunParamDAO.update(registration, workflowRunParam);
    }

    /** {@inheritDoc} */
    @Override
    public void insert(Registration registration, WorkflowRunParam workflowRunParam) {
        workflowRunParamDAO.insert(registration, workflowRunParam);
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowRunParam updateDetached(Registration registration, WorkflowRunParam workflowRunParam) {
        return workflowRunParamDAO.updateDetached(registration, workflowRunParam);
    }
}
