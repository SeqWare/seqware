package net.sourceforge.seqware.common.business.impl;

import java.util.Date;
import java.util.List;

import net.sourceforge.seqware.common.business.ShareWorkflowRunService;
import net.sourceforge.seqware.common.dao.ShareWorkflowRunDAO;
import net.sourceforge.seqware.common.model.ShareWorkflowRun;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>ShareWorkflowRunServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ShareWorkflowRunServiceImpl implements ShareWorkflowRunService {
  private ShareWorkflowRunDAO dao = null;
  private static final Log log = LogFactory.getLog(ShareWorkflowRunServiceImpl.class);

  /**
   * <p>Constructor for ShareWorkflowRunServiceImpl.</p>
   */
  public ShareWorkflowRunServiceImpl() {
    super();
  }

  /**
   * {@inheritDoc}
   *
   * Sets a private member variable with an instance of an implementation of
   * ShareWorkflowRunDAO. This method is called by the Spring framework at run
   * time.
   * @see ShareWorkflowRunDAO
   */
  public void setShareWorkflowRunDAO(ShareWorkflowRunDAO dao) {
    this.dao = dao;
  }

  /**
   * {@inheritDoc}
   *
   * Inserts an instance of ShareWorkflowRun into the database.
   */
  public void insert(ShareWorkflowRun shareWorkflowRun) {
    // shareWorkflowRun.setEmail(shareWorkflowRun.getEmail().trim().toLowerCase());
    shareWorkflowRun.setCreateTimestamp(new Date());

    dao.insert(shareWorkflowRun);
  }

  /**
   * {@inheritDoc}
   *
   * Updates an instance of ShareWorkflowRun in the database.
   */
  public void update(ShareWorkflowRun shareWorkflowRun) {
    dao.update(shareWorkflowRun);
  }

  /** {@inheritDoc} */
  public void delete(ShareWorkflowRun shareWorkflowRun) {
    dao.delete(shareWorkflowRun);
  }

  /** {@inheritDoc} */
  public boolean isExistsShare(Integer WorkflowRunId, Integer registrationId) {
    boolean isExists = false;
    if (findByWorkflowRunIdAndRegistrationId(WorkflowRunId, registrationId) != null) {
      isExists = true;
    }
    return isExists;
  }

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
  @Override
  public ShareWorkflowRun updateDetached(ShareWorkflowRun shareWorkflowRun) {
    return dao.updateDetached(shareWorkflowRun);
  }

    /** {@inheritDoc} */
    @Override
    public List<ShareWorkflowRun> list() {
        return dao.list();
    }
}
