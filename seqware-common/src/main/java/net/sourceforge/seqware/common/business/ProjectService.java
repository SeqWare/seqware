package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.ProjectDAO;
import net.sourceforge.seqware.common.model.Project;
import net.sourceforge.seqware.common.model.Registration;

public interface ProjectService {
  public static final String NAME = "projectService";

  public void setProjectDAO(ProjectDAO projectDAO);

  public void insert(Project project);

  public void update(Project project);

  public List<Project> list();
  
  public List<Project> list(Registration registration);

  public Project findByName(String name);

  public Project findByID(Integer expID);

  public boolean hasNameBeenUsed(String oldName, String newName);

  Project updateDetached(Project project);
}

// ex:sw=4:ts=4:
