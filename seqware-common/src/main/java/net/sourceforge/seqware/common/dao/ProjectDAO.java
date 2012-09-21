package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Project;
import net.sourceforge.seqware.common.model.Registration;

public interface ProjectDAO {
  public void insert(Project project);

  public void update(Project project);

  public List<Project> list(Registration registration);

  public Project findByName(String name);

  public Project findByID(Integer expID);

  public Project updateDetached(Project project);
  
  public List<Project> list();
}
