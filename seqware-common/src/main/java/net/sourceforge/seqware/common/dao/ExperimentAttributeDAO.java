package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.ExperimentAttribute;

public interface ExperimentAttributeDAO {

  public abstract void insert(ExperimentAttribute experimentAttribute);

  public abstract void update(ExperimentAttribute experimentAttribute);

  public abstract void delete(ExperimentAttribute experimentAttribute);

  @SuppressWarnings("unchecked")
  public abstract List<ExperimentAttribute> findAll(Experiment experiment);
  
  public List<ExperimentAttribute> list();

}