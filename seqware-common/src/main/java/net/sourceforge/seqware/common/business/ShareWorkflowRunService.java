package net.sourceforge.seqware.common.business;

import java.util.List;
import net.sourceforge.seqware.common.dao.ShareWorkflowRunDAO;
import net.sourceforge.seqware.common.model.ShareWorkflowRun;

/**
 * <p>ShareWorkflowRunService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ShareWorkflowRunService {
  /** Constant <code>NAME="shareWorkflowRunService"</code> */
  public static final String NAME = "shareWorkflowRunService";

  /**
   * <p>setShareWorkflowRunDAO.</p>
   *
   * @param shareWorkflowRunDAO a {@link net.sourceforge.seqware.common.dao.ShareWorkflowRunDAO} object.
   */
  public void setShareWorkflowRunDAO(ShareWorkflowRunDAO shareWorkflowRunDAO);

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
   * <p>findBySWAccession.</p>
   *
   * @param swAccession a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareWorkflowRun} object.
   */
  public ShareWorkflowRun findBySWAccession(Integer swAccession);

  /**
   * <p>isExistsShare.</p>
   *
   * @param workflowRunId a {@link java.lang.Integer} object.
   * @param registrationId a {@link java.lang.Integer} object.
   * @return a boolean.
   */
  public boolean isExistsShare(Integer workflowRunId, Integer registrationId);

  /**
   * <p>updateDetached.</p>
   *
   * @param shareWorkflowRun a {@link net.sourceforge.seqware.common.model.ShareWorkflowRun} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareWorkflowRun} object.
   */
  ShareWorkflowRun updateDetached(ShareWorkflowRun shareWorkflowRun);
  
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<ShareWorkflowRun> list();
}
