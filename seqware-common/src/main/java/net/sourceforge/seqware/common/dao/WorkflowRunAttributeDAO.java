package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.WorkflowRunAttribute;

/**
 * <p>WorkflowRunAttributeDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface WorkflowRunAttributeDAO {

  /**
   * <p>getAll.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<WorkflowRunAttribute> getAll();

  /**
   * <p>get.</p>
   *
   * @param workflowRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   * @return a {@link java.util.List} object.
   */
  public List<WorkflowRunAttribute> get(WorkflowRun workflowRun);

  /**
   * <p>get.</p>
   *
   * @param id a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.WorkflowRunAttribute} object.
   */
  public WorkflowRunAttribute get(Integer id);

  /**
   * <p>add.</p>
   *
   * @param workflowRunAttribute a {@link net.sourceforge.seqware.common.model.WorkflowRunAttribute} object.
   * @return a {@link java.lang.Integer} object.
   */
  public Integer add(WorkflowRunAttribute workflowRunAttribute);

  /**
   * <p>update.</p>
   *
   * @param workflowRunAttribute a {@link net.sourceforge.seqware.common.model.WorkflowRunAttribute} object.
   */
  public void update(WorkflowRunAttribute workflowRunAttribute);

  /**
   * <p>delete.</p>
   *
   * @param workflowRunAttribute a {@link net.sourceforge.seqware.common.model.WorkflowRunAttribute} object.
   */
  public void delete(WorkflowRunAttribute workflowRunAttribute);

}
