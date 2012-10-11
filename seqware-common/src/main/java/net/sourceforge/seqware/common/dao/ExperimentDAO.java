package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Study;

public interface ExperimentDAO {

  public Integer insert(Experiment sequencerRun);

  public Integer insert(Registration registration, Experiment sequencerRun);

  public void update(Experiment sequencerRun);

  public void update(Registration registration, Experiment experiment);

  public void delete(Experiment sequencerRun);

  public List<Experiment> list();

  public List<Experiment> list(Registration registration);

  public List<Experiment> list(Study study);

  public Experiment findByTitle(String title);

  public Experiment findByID(Integer studyID);

  public Experiment findBySWAccession(Integer swAccession);

  public List<File> getFiles(Integer experimentId);

  public boolean isHasFile(Integer experimentId);

  public List<File> getFiles(Integer expId, String metaType);

  public boolean isHasFile(Integer expId, String metaType);

  public Experiment updateDetached(Experiment experiment);

  public Experiment updateDetached(Registration registration, Experiment experiment);

  public List<Experiment> findByOwnerID(Integer registrationID);

  public List<Experiment> findByCriteria(String criteria, boolean isCaseSens);

  void merge(Experiment experiment);
}
