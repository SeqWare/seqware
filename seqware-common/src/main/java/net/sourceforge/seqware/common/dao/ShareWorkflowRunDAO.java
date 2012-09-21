package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.ShareWorkflowRun;
import net.sourceforge.seqware.common.model.WorkflowRun;

public interface ShareWorkflowRunDAO {
  public void insert(ShareWorkflowRun shareWorkflowRun);

  public void update(ShareWorkflowRun shareWorkflowRun);

  public void delete(ShareWorkflowRun shareWorkflowRun);

  public ShareWorkflowRun findByID(Integer id);

  public ShareWorkflowRun findByWorkflowRunIdAndRegistrationId(Integer workflowRunId, Integer registrationId);

  public List<ShareWorkflowRun> list(WorkflowRun workflowRun);

  public ShareWorkflowRun getShareWorkflowRun(String email, WorkflowRun workflowRun);

  public ShareWorkflowRun findBySWAccession(Integer swAccession);

  public ShareWorkflowRun updateDetached(ShareWorkflowRun shareWorkflowRun);
  
  public List<ShareWorkflowRun> list();
  
}
