package net.sourceforge.seqware.common.business;

import java.util.List;
import net.sourceforge.seqware.common.dao.ShareWorkflowRunDAO;
import net.sourceforge.seqware.common.model.ShareWorkflowRun;

public interface ShareWorkflowRunService {
  public static final String NAME = "shareWorkflowRunService";

  public void setShareWorkflowRunDAO(ShareWorkflowRunDAO shareWorkflowRunDAO);

  public void insert(ShareWorkflowRun shareWorkflowRun);

  public void update(ShareWorkflowRun shareWorkflowRun);

  public void delete(ShareWorkflowRun shareWorkflowRun);

  public ShareWorkflowRun findByID(Integer id);

  public ShareWorkflowRun findByWorkflowRunIdAndRegistrationId(Integer workflowRunId, Integer registrationId);

  public ShareWorkflowRun findBySWAccession(Integer swAccession);

  public boolean isExistsShare(Integer workflowRunId, Integer registrationId);

  ShareWorkflowRun updateDetached(ShareWorkflowRun shareWorkflowRun);
  
  public List<ShareWorkflowRun> list();
}
