package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.seqware.common.dao.ProjectDAO;
import net.sourceforge.seqware.common.model.Project;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ProjectDAOHibernate extends HibernateDaoSupport implements ProjectDAO {
  public ProjectDAOHibernate() {
    super();
  }

  public void insert(Project project) {
    this.getHibernateTemplate().save(project);
  }

  public void update(Project project) {
    this.getHibernateTemplate().update(project);
  }

  public List<Project> list(Registration registration) {
    ArrayList<Project> projects = new ArrayList<Project>();
    if (registration == null)
      return projects;

    List expmts = null;
    /*
     * if(registration.isTechnician() || registration.isLIMSAdmin()) { //The
     * user can see all projects expmts = this.getHibernateTemplate().find(
     * "from Project as project order by project.name desc" ); } else { // Limit
     * the projects to those owned by the user expmts =
     * this.getHibernateTemplate().find(
     * "from Project as project where owner_id = ? order by project.name desc",
     * registration.getRegistrationId() ); }
     */

    for (Object project : expmts) {
      projects.add((Project) project);
    }
    return projects;
  }

  /**
   * Finds an instance of Project in the database by the Project name.
   * 
   * @param name
   *          name of the Project
   * @return Project or null if not found
   */
  public Project findByName(String name) {
    String query = "from project as project where project.name = ?";
    Project project = null;
    Object[] parameters = { name };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      project = (Project) list.get(0);
    }
    return project;
  }

  /**
   * Finds an instance of Project in the database by the Project ID.
   * 
   * @param expID
   *          ID of the Project
   * @return Project or null if not found
   */
  public Project findByID(Integer expID) {
    String query = "from Project as project where project.projectId = ?";
    Project project = null;
    Object[] parameters = { expID };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      project = (Project) list.get(0);
    }
    return project;
  }

  @Override
  public Project updateDetached(Project project) {
    Project dbObject = findByID(project.getProjectId());
    try {
      BeanUtilsBean beanUtils = new NullBeanUtils();
      beanUtils.copyProperties(dbObject, project);
      return (Project) this.getHibernateTemplate().merge(dbObject);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

    @Override
    public List<Project> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
