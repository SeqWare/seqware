package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowAttribute;

/**
 * <p>WorkflowAttributeDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface WorkflowAttributeDAO {

  /**
   * <p>getAll.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<WorkflowAttribute> getAll();

  /**
   * <p>get.</p>
   *
   * @param workflow a {@link net.sourceforge.seqware.common.model.Workflow} object.
   * @return a {@link java.util.List} object.
   */
  public List<WorkflowAttribute> get(Workflow workflow);

  /**
   * <p>get.</p>
   *
   * @param id a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.WorkflowAttribute} object.
   */
  public WorkflowAttribute get(Integer id);

  /**
   * <p>add.</p>
   *
   * @param workflowAttribute a {@link net.sourceforge.seqware.common.model.WorkflowAttribute} object.
   * @return a {@link java.lang.Integer} object.
   */
  public Integer add(WorkflowAttribute workflowAttribute);

  /**
   * <p>update.</p>
   *
   * @param workflowAttribute a {@link net.sourceforge.seqware.common.model.WorkflowAttribute} object.
   */
  public void update(WorkflowAttribute workflowAttribute);

  /**
   * <p>delete.</p>
   *
   * @param workflowAttribute a {@link net.sourceforge.seqware.common.model.WorkflowAttribute} object.
   */
  public void delete(WorkflowAttribute workflowAttribute);

}
