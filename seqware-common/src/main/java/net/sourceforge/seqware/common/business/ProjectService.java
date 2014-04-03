package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.ProjectDAO;
import net.sourceforge.seqware.common.model.Project;
import net.sourceforge.seqware.common.model.Registration;

/**
 * <p>ProjectService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ProjectService {
  /** Constant <code>NAME="projectService"</code> */
  public static final String NAME = "projectService";

  /**
   * <p>setProjectDAO.</p>
   *
   * @param projectDAO a {@link net.sourceforge.seqware.common.dao.ProjectDAO} object.
   */
  public void setProjectDAO(ProjectDAO projectDAO);

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
   * @return a {@link java.util.List} object.
   */
  public List<Project> list();
  
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
   * <p>hasNameBeenUsed.</p>
   *
   * @param oldName a {@link java.lang.String} object.
   * @param newName a {@link java.lang.String} object.
   * @return a boolean.
   */
  public boolean hasNameBeenUsed(String oldName, String newName);

  /**
   * <p>updateDetached.</p>
   *
   * @param project a {@link net.sourceforge.seqware.common.model.Project} object.
   * @return a {@link net.sourceforge.seqware.common.model.Project} object.
   */
  Project updateDetached(Project project);
}

// ex:sw=4:ts=4:
