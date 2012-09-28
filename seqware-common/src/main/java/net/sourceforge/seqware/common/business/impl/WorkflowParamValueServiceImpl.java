package net.sourceforge.seqware.common.business.impl;

import java.util.List;
import net.sourceforge.seqware.common.business.WorkflowParamValueService;
import net.sourceforge.seqware.common.dao.WorkflowParamValueDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.WorkflowParamValue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WorkflowParamValueServiceImpl implements WorkflowParamValueService {
  private WorkflowParamValueDAO workflowParamValueDAO = null;
  private static final Log log = LogFactory.getLog(WorkflowParamValueServiceImpl.class);

  public WorkflowParamValueServiceImpl() {
    super();
  }

  public void setWorkflowParamValueDAO(WorkflowParamValueDAO workflowParamValueDAO) {
    this.workflowParamValueDAO = workflowParamValueDAO;
  }

  public Integer insert(WorkflowParamValue workflowParamValue) {
    return workflowParamValueDAO.insert(workflowParamValue);
  }

  public void update(WorkflowParamValue workflowParamValue) {
    workflowParamValueDAO.update(workflowParamValue);
  }

  public void delete(WorkflowParamValue workflowParamValue) {
    workflowParamValueDAO.delete(workflowParamValue);
  }

  public WorkflowParamValue findByID(Integer id) {
    WorkflowParamValue workflowParamValue = null;
    if (id != null) {
      try {
        workflowParamValue = workflowParamValueDAO.findByID(id);
      } catch (Exception exception) {
        log.error("Cannot find Lane by Workflow Param ID " + id);
        log.error(exception.getMessage());
      }
    }
    return workflowParamValue;
  }

  @Override
  public WorkflowParamValue updateDetached(WorkflowParamValue workflowParamValue) {
    return workflowParamValueDAO.updateDetached(workflowParamValue);
  }

    @Override
    public List<WorkflowParamValue> list() {
        return workflowParamValueDAO.list();
    }

    @Override
    public void update(Registration registration, WorkflowParamValue workflowParamValue) {
        workflowParamValueDAO.update(registration, workflowParamValue);
    }

    @Override
    public Integer insert(Registration registration, WorkflowParamValue workflowParamValue) {
        return workflowParamValueDAO.insert(registration, workflowParamValue);
    }

    @Override
    public WorkflowParamValue updateDetached(Registration registration, WorkflowParamValue workflowParamValue) {
        return workflowParamValueDAO.updateDetached(registration, workflowParamValue);
    }
}
