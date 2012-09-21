package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.ExperimentLink;

public interface ExperimentLinkDAO {

  public abstract void insert(ExperimentLink experimentLink);

  public abstract void update(ExperimentLink experimentLink);

  public abstract void delete(ExperimentLink experimentLink);

  @SuppressWarnings("unchecked")
  public abstract List<ExperimentLink> findAll(Experiment experiment);
  public List<ExperimentLink> list();

}