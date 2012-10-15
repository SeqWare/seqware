package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.ShareWorkflowRun;
import net.sourceforge.seqware.common.model.WorkflowRun;

/**
 * <p>ShareWorkflowRunDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ShareWorkflowRunDAO {
  /**
   * <p>insert.</p>
   *
   * @param shareWorkflowRun a {@link net.sourceforge.seqware.common.model.ShareWorkflowRun} object.
   */
  public void insert(ShareWorkflowRun shareWorkflowRun);

  /**
   * <p>update.</p>
   *
   * @param shareWorkflowRun a {@link net.sourceforge.seqware.common.model.ShareWorkflowRun} object.
   */
  public void update(ShareWorkflowRun shareWorkflowRun);

  /**
   * <p>delete.</p>
   *
   * @param shareWorkflowRun a {@link net.sourceforge.seqware.common.model.ShareWorkflowRun} object.
   */
  public void delete(ShareWorkflowRun shareWorkflowRun);

  /**
   * <p>findByID.</p>
   *
   * @param id a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareWorkflowRun} object.
   */
  public ShareWorkflowRun findByID(Integer id);

  /**
   * <p>findByWorkflowRunIdAndRegistrationId.</p>
   *
   * @param workflowRunId a {@link java.lang.Integer} object.
   * @param registrationId a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareWorkflowRun} object.
   */
  public ShareWorkflowRun findByWorkflowRunIdAndRegistrationId(Integer workflowRunId, Integer registrationId);

  /**
   * <p>list.</p>
   *
   * @param workflowRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   * @return a {@link java.util.List} object.
   */
  public List<ShareWorkflowRun> list(WorkflowRun workflowRun);

  /**
   * <p>getShareWorkflowRun.</p>
   *
   * @param email a {@link java.lang.String} object.
   * @param workflowRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareWorkflowRun} object.
   */
  public ShareWorkflowRun getShareWorkflowRun(String email, WorkflowRun workflowRun);

  /**
   * <p>findBySWAccession.</p>
   *
   * @param swAccession a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareWorkflowRun} object.
   */
  public ShareWorkflowRun findBySWAccession(Integer swAccession);

  /**
   * <p>updateDetached.</p>
   *
   * @param shareWorkflowRun a {@link net.sourceforge.seqware.common.model.ShareWorkflowRun} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareWorkflowRun} object.
   */
  public ShareWorkflowRun updateDetached(ShareWorkflowRun shareWorkflowRun);
  
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<ShareWorkflowRun> list();
  
}
