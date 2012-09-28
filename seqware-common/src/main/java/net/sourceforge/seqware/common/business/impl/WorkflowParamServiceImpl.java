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

public class WorkflowParamServiceImpl implements WorkflowParamService {
  private WorkflowParamDAO workflowParamDAO = null;
  private static final Log log = LogFactory.getLog(WorkflowParamServiceImpl.class);

  public WorkflowParamServiceImpl() {
    super();
  }

  public void setWorkflowParamDAO(WorkflowParamDAO workflowParamDAO) {
    this.workflowParamDAO = workflowParamDAO;
  }

    @Override
  public Integer insert(WorkflowParam workflowParam) {
    return workflowParamDAO.insert(workflowParam);
  }

  public void update(WorkflowParam workflowParam) {
    workflowParamDAO.update(workflowParam);
  }

  public void delete(WorkflowParam workflowParam) {
    workflowParamDAO.delete(workflowParam);
  }

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

  @Override
  public WorkflowParam updateDetached(WorkflowParam workflowParam) {
    return workflowParamDAO.updateDetached(workflowParam);
  }

    @Override
    public List<WorkflowParam> list() {
        return workflowParamDAO.list();
    }

    @Override
    public void update(Registration registration, WorkflowParam workflowParam) {
        workflowParamDAO.update(registration, workflowParam);
    }

    @Override
    public Integer insert(Registration registration, WorkflowParam workflowParam) {
        return workflowParamDAO.insert(registration, workflowParam);
    }

    @Override
    public WorkflowParam updateDetached(Registration registration, WorkflowParam workflowParam) {
         return workflowParamDAO.updateDetached(registration, workflowParam);
    }
}