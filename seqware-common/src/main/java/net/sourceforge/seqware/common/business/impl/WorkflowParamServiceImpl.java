package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import java.util.SortedSet;

import net.sourceforge.seqware.common.business.WorkflowParamService;
import net.sourceforge.seqware.common.dao.WorkflowParamDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.model.WorkflowParamValue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>WorkflowParamServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class WorkflowParamServiceImpl implements WorkflowParamService {
  private WorkflowParamDAO workflowParamDAO = null;
  private static final Log log = LogFactory.getLog(WorkflowParamServiceImpl.class);

  /**
   * <p>Constructor for WorkflowParamServiceImpl.</p>
   */
  public WorkflowParamServiceImpl() {
    super();
  }

  /** {@inheritDoc} */
  public void setWorkflowParamDAO(WorkflowParamDAO workflowParamDAO) {
    this.workflowParamDAO = workflowParamDAO;
  }

    /** {@inheritDoc} */
    @Override
  public Integer insert(WorkflowParam workflowParam) {
    return workflowParamDAO.insert(workflowParam);
  }

  /** {@inheritDoc} */
  public void update(WorkflowParam workflowParam) {
    workflowParamDAO.update(workflowParam);
  }

  /** {@inheritDoc} */
  public void delete(WorkflowParam workflowParam) {
    workflowParamDAO.delete(workflowParam);
  }

  /** {@inheritDoc} */
  public WorkflowParam findByID(Integer id) {
    WorkflowParam workflowParam = null;
    if (id != null) {
      try {
        workflowParam = workflowParamDAO.findByID(id);
        // fillInLanes(lane);
      } catch (Exception exception) {
        log.error("Cannot find Lane by expID " + id);
        log.error(exception.getMessage());
      }
    }
    return workflowParam;
  }

  /**
   * <p>findValueByID.</p>
   *
   * @param workflowParam a {@link net.sourceforge.seqware.common.model.WorkflowParam} object.
   * @param id a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.WorkflowParamValue} object.
   */
  public WorkflowParamValue findValueByID(WorkflowParam workflowParam, Integer id) {
    WorkflowParamValue value = null;
    SortedSet<WorkflowParamValue> values = workflowParam.getValues();
    for (WorkflowParamValue workflowParamValue : values) {
      if (workflowParamValue != null && workflowParamValue.getWorkflowParamValueId().equals(id)) {
        value = workflowParamValue;
      }
    }
    return value;
  }

  /** {@inheritDoc} */
  @Override
  public WorkflowParam updateDetached(WorkflowParam workflowParam) {
    return workflowParamDAO.updateDetached(workflowParam);
  }

    /** {@inheritDoc} */
    @Override
    public List<WorkflowParam> list() {
        return workflowParamDAO.list();
    }

    /** {@inheritDoc} */
    @Override
    public void update(Registration registration, WorkflowParam workflowParam) {
        workflowParamDAO.update(registration, workflowParam);
    }

    /** {@inheritDoc} */
    @Override
    public Integer insert(Registration registration, WorkflowParam workflowParam) {
        return workflowParamDAO.insert(registration, workflowParam);
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowParam updateDetached(Registration registration, WorkflowParam workflowParam) {
         return workflowParamDAO.updateDetached(registration, workflowParam);
    }
}
