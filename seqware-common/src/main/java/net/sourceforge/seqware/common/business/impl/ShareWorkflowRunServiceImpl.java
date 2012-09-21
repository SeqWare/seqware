package net.sourceforge.seqware.common.business.impl;

import java.util.Date;
import java.util.List;

import net.sourceforge.seqware.common.business.ShareWorkflowRunService;
import net.sourceforge.seqware.common.dao.ShareWorkflowRunDAO;
import net.sourceforge.seqware.common.model.ShareWorkflowRun;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ShareWorkflowRunServiceImpl implements ShareWorkflowRunService {
  private ShareWorkflowRunDAO dao = null;
  private static final Log log = LogFactory.getLog(ShareWorkflowRunServiceImpl.class);

  public ShareWorkflowRunServiceImpl() {
    super();
  }

  /**
   * Sets a private member variable with an instance of an implementation of
   * ShareWorkflowRunDAO. This method is called by the Spring framework at run
   * time.
   * 
   * @param ShareWorkflowRunDAO
   *          implementation of ShareWorkflowRunDAO
   * @see ShareWorkflowRunDAO
   */
  public void setShareWorkflowRunDAO(ShareWorkflowRunDAO dao) {
    this.dao = dao;
  }

  /**
   * Inserts an instance of ShareWorkflowRun into the database.
   * 
   * @param ShareWorkflowRunDAO
   *          instance of ShareWorkflowRunDAO
   */
  public void insert(ShareWorkflowRun shareWorkflowRun) {
    // shareWorkflowRun.setEmail(shareWorkflowRun.getEmail().trim().toLowerCase());
    shareWorkflowRun.setCreateTimestamp(new Date());

    dao.insert(shareWorkflowRun);
  }

  /**
   * Updates an instance of ShareWorkflowRun in the database.
   * 
   * @param ShareWorkflowRunService
   *          instance of ShareWorkflowRun
   */
  public void update(ShareWorkflowRun shareWorkflowRun) {
    dao.update(shareWorkflowRun);
  }

  public void delete(ShareWorkflowRun shareWorkflowRun) {
    dao.delete(shareWorkflowRun);
  }

  public boolean isExistsShare(Integer WorkflowRunId, Integer registrationId) {
    boolean isExists = false;
    if (findByWorkflowRunIdAndRegistrationId(WorkflowRunId, registrationId) != null) {
      isExists = true;
    }
    return isExists;
  }

  public ShareWorkflowRun findByWorkflowRunIdAndRegistrationId(Integer workflowRunId, Integer registrationId) {
    ShareWorkflowRun shareWorkflowRun = null;
    if (workflowRunId != null && registrationId != null) {
      try {
        shareWorkflowRun = dao.findByWorkflowRunIdAndRegistrationId(workflowRunId, registrationId);
      } catch (Exception exception) {
        log.error("Cannot find ShareWorkflowRun by sorkflowRunID " + workflowRunId + " registrationId "
            + registrationId);
        log.error(exception.getMessage());
      }
    }
    return shareWorkflowRun;
  }

  public ShareWorkflowRun findByID(Integer shareWorkflowRunId) {
    ShareWorkflowRun shareWorkflowRun = null;
    if (shareWorkflowRunId != null) {
      try {
        shareWorkflowRun = dao.findByID(shareWorkflowRunId);
      } catch (Exception exception) {
        log.error("Cannot find ShareWorkflowRun by expID " + shareWorkflowRunId);
        log.error(exception.getMessage());
      }
    }
    return shareWorkflowRun;
  }

  @Override
  public ShareWorkflowRun findBySWAccession(Integer swAccession) {
    ShareWorkflowRun shareWorkflowRun = null;
    if (swAccession != null) {
      try {
        shareWorkflowRun = dao.findBySWAccession(swAccession);
      } catch (Exception exception) {
        log.error("Cannot find ShareWorkflowRun by swAccession " + swAccession);
        log.error(exception.getMessage());
      }
    }
    return shareWorkflowRun;
  }

  @Override
  public ShareWorkflowRun updateDetached(ShareWorkflowRun shareWorkflowRun) {
    return dao.updateDetached(shareWorkflowRun);
  }

    @Override
    public List<ShareWorkflowRun> list() {
        return dao.list();
    }
}
