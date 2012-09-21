package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.ExperimentLibraryDesignDAO;
import net.sourceforge.seqware.common.model.ExperimentLibraryDesign;
import net.sourceforge.seqware.common.model.Registration;

public interface ExperimentLibraryDesignService {
  public static final String NAME = "ExperimentLibraryDesignService";

  public void setExperimentLibraryDesignDAO(ExperimentLibraryDesignDAO dao);

  public void insert(ExperimentLibraryDesign obj);

  public void update(ExperimentLibraryDesign obj);

  public List<ExperimentLibraryDesign> list();
  
  public List<ExperimentLibraryDesign> list(Registration registration);

  public ExperimentLibraryDesign findByID(Integer expID);

  ExperimentLibraryDesign updateDetached(ExperimentLibraryDesign eld);

}

// ex:sw=4:ts=4:
