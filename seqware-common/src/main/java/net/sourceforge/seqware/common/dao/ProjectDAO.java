package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Project;
import net.sourceforge.seqware.common.model.Registration;

/**
 * <p>ProjectDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ProjectDAO {
  /**
   * <p>insert.</p>
   *
   * @param project a {@link net.sourceforge.seqware.common.model.Project} object.
   */
  public void insert(Project project);

  /**
   * <p>update.</p>
   *
   * @param project a {@link net.sourceforge.seqware.common.model.Project} object.
   */
  public void update(Project project);

  /**
   * <p>list.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @return a {@link java.util.List} object.
   */
  public List<Project> list(Registration registration);

  /**
   * <p>findByName.</p>
   *
   * @param name a {@link java.lang.String} object.
   * @return a {@link net.sourceforge.seqware.common.model.Project} object.
   */
  public Project findByName(String name);

  /**
   * <p>findByID.</p>
   *
   * @param expID a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.Project} object.
   */
  public Project findByID(Integer expID);

  /**
   * <p>updateDetached.</p>
   *
   * @param project a {@link net.sourceforge.seqware.common.model.Project} object.
   * @return a {@link net.sourceforge.seqware.common.model.Project} object.
   */
  public Project updateDetached(Project project);
  
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<Project> list();
}
