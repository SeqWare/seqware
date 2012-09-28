package net.sourceforge.seqware.common.business;

import java.util.List;
import java.util.SortedSet;

import net.sourceforge.seqware.common.dao.ExperimentDAO;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Study;

public interface ExperimentService {

  public static final String NAME = "ExperimentService";

  public void setExperimentDAO(ExperimentDAO experimentDAO);

  public Integer insert(Experiment experiment);

  public Integer insert(Registration registration, Experiment experiment);

  public void update(Experiment experiment);

  public void update(Registration registration, Experiment experiment);

  public void delete(Experiment experiment, String deleteRealFiles);

  public List<Experiment> list();

  public List<Experiment> list(Registration registration);

  public List<Experiment> list(Study study);

  public Experiment findByTitle(String name);

  public Experiment findByID(Integer expID);

  public Experiment findBySWAccession(Integer swAccession);

  public boolean hasTitleBeenUsed(String oldName, String newName);

  public List<File> getFiles(Integer expId);

  public boolean isHasFile(Integer expId);

  public List<File> getFiles(Integer studyId, String metaType);

  public boolean isHasFile(Integer studyId, String metaType);

  public SortedSet<Experiment> setWithHasFile(SortedSet<Experiment> list);

  public SortedSet<Experiment> listWithHasFile(SortedSet<Experiment> list, String metaType);

  public Experiment updateDetached(Experiment experiment);

  public Experiment updateDetached(Registration registration, Experiment experiment);

  public List<Experiment> findByOwnerID(Integer registrationID);

  public List<Experiment> findByCriteria(String criteria, boolean isCaseSens);

  void merge(Experiment experiment);
}

// ex:sw=4:ts=4:
