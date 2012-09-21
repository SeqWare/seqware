package net.sourceforge.seqware.common.business.impl;

import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import net.sourceforge.seqware.common.business.ProjectService;
import net.sourceforge.seqware.common.dao.ProjectDAO;
import net.sourceforge.seqware.common.model.Project;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProjectServiceImpl implements ProjectService {
  private ProjectDAO projectDAO = null;
  private static final Log log = LogFactory.getLog(ProjectServiceImpl.class);

  public ProjectServiceImpl() {
    super();
  }

  /**
   * Sets a private member variable with an instance of an implementation of
   * ProjectDAO. This method is called by the Spring framework at run time.
   * 
   * @param projectDAO
   *          implementation of ProjectDAO
   * @see ProjectDAO
   */
  public void setProjectDAO(ProjectDAO projectDAO) {
    this.projectDAO = projectDAO;
  }

  /**
   * Inserts an instance of Project into the database.
   * 
   * @param projectDAO
   *          instance of ProjectDAO
   */
  public void insert(Project project) {
    // FIXME: need to set the names of each sample!!
    TreeSet<Sample> list = new TreeSet<Sample>();
    // FIXME: why do I have to manually set these!?!?!
    /*
     * project.getSample1().setCreateTimestamp(new Date());
     * project.getSample2().setCreateTimestamp(new Date());
     * project.getSample3().setCreateTimestamp(new Date());
     * project.getSample4().setCreateTimestamp(new Date());
     * project.getSample5().setCreateTimestamp(new Date());
     * project.getSample6().setCreateTimestamp(new Date());
     * project.getSample7().setCreateTimestamp(new Date());
     * project.getSample8().setCreateTimestamp(new Date());
     * project.getSample1().setProject(project);
     * project.getSample2().setProject(project);
     * project.getSample3().setProject(project);
     * project.getSample4().setProject(project);
     * project.getSample5().setProject(project);
     * project.getSample6().setProject(project);
     * project.getSample7().setProject(project);
     * project.getSample8().setProject(project); list.add(project.getSample1());
     * list.add(project.getSample2()); list.add(project.getSample3());
     * list.add(project.getSample4()); list.add(project.getSample5());
     * list.add(project.getSample6()); list.add(project.getSample7());
     * list.add(project.getSample8());
     */
    project.setSamples(list);
    project.setCreateTimestamp(new Date());

    // Debug.put(": readyToProcess = " + project.getReadyToProcess());
    // Debug.put(": ownerId = " + project.getOwnerId());

    // if (project.getReadyToProcess() != null &&
    // project.getReadyToProcess().equals("Y")) {
    // project.setStatus("ready_to_process"); }
    // else { project.setStatus("not_ready_to_process"); };
    projectDAO.insert(project);
  }

  /**
   * Updates an instance of Project in the database.
   * 
   * @param project
   *          instance of Project
   */
  public void update(Project project) {

    // if (project.getReadyToProcess() != null &&
    // project.getReadyToProcess().equals("Y")) {
    // project.setStatus("ready_to_process"); }
    // else { project.setStatus("not_ready_to_process"); }

    projectDAO.update(project);

  }

  public List<Project> list(Registration registration) {
    return projectDAO.list(registration);
  }

  /**
   * Finds an instance of Project in the database by the Project emailAddress,
   * and copies the Project properties to an instance of Project.
   * 
   * @return instance of Project, or null if a Project cannot be found
   */
  public Project findByName(String name) {
    Project project = null;
    if (name != null) {
      try {
        project = projectDAO.findByName(name.trim().toLowerCase());
      } catch (Exception exception) {
        log.debug("Cannot find Project by name " + name);
      }
    }
    return project;
  }

  public Project findByID(Integer expID) {
    Project project = null;
    if (expID != null) {
      try {
        project = projectDAO.findByID(expID);
        fillInSamples(project);
      } catch (Exception exception) {
        log.error("Cannot find Project by expID " + expID);
        log.error(exception.getMessage());
      }
    }
    return project;
  }

  /**
   * Determines if an email address has already been used.
   * 
   * @param oldEmail
   *          The previous email address, or null if this method is being called
   *          for a new email address
   * 
   * @param newEmail
   *          The email address that is being checked
   * 
   * @return true if the newEmail has already been used, and false otherwise
   */
  public boolean hasNameBeenUsed(String oldName, String newName) {
    boolean nameUsed = false;
    boolean checkName = true;

    if (newName != null) {
      if (oldName != null) {
        /*
         * We do not want to check if an name address has been used if the user
         * is updating an existing project and has not changed the nameAddress.
         */
        checkName = !newName.trim().equalsIgnoreCase(oldName.trim());
      }

      if (checkName) {
        Project project = this.findByName(newName.trim().toLowerCase());
        if (project != null) {
          nameUsed = true;
        }
      }
    }
    return nameUsed;
  }

  private void fillInSamples(Project project) {
    Object[] samples = project.getSamples().toArray();
    if (samples.length >= 8) {
      /*
       * project.setSample1((Sample)samples[7]);
       * project.setSample2((Sample)samples[6]);
       * project.setSample3((Sample)samples[5]);
       * project.setSample4((Sample)samples[4]);
       * project.setSample5((Sample)samples[3]);
       * project.setSample6((Sample)samples[2]);
       * project.setSample7((Sample)samples[1]);
       * project.setSample8((Sample)samples[0]);
       */
    }
  }

  @Override
  public Project updateDetached(Project project) {
    return projectDAO.updateDetached(project);
  }

    @Override
    public List<Project> list() {
        return projectDAO.list();
    }
}

// ex:sw=4:ts=4:
