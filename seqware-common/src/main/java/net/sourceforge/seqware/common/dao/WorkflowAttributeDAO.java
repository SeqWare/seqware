package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowAttribute;

public interface WorkflowAttributeDAO {

  public List<WorkflowAttribute> getAll();

  public List<WorkflowAttribute> get(Workflow workflow);

  public WorkflowAttribute get(Integer id);

  public Integer add(WorkflowAttribute workflowAttribute);

  public void update(WorkflowAttribute workflowAttribute);

  public void delete(WorkflowAttribute workflowAttribute);

}
