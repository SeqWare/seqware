package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.WorkflowRunAttribute;

public interface WorkflowRunAttributeDAO {

  public List<WorkflowRunAttribute> getAll();

  public List<WorkflowRunAttribute> get(WorkflowRun workflowRun);

  public WorkflowRunAttribute get(Integer id);

  public Integer add(WorkflowRunAttribute workflowRunAttribute);

  public void update(WorkflowRunAttribute workflowRunAttribute);

  public void delete(WorkflowRunAttribute workflowRunAttribute);

}
