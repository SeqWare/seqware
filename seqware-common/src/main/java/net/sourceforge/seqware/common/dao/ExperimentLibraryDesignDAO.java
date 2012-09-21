package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.ExperimentLibraryDesign;
import net.sourceforge.seqware.common.model.Registration;

public interface ExperimentLibraryDesignDAO {
  public List<ExperimentLibraryDesign> list(Registration registration);

  public void insert(ExperimentLibraryDesign obj);

  public void update(ExperimentLibraryDesign obj);

  public ExperimentLibraryDesign findByID(Integer id);

  public ExperimentLibraryDesign updateDetached(ExperimentLibraryDesign eld);
  
  public List<ExperimentLibraryDesign> list();
}
