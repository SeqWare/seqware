package net.sourceforge.seqware.common.business;

import java.util.List;
import net.sourceforge.seqware.common.dao.ExperimentSpotDesignDAO;
import net.sourceforge.seqware.common.model.ExperimentSpotDesign;

public interface ExperimentSpotDesignService {
  public static final String NAME = "ExperimentSpotDesignService";

  public void setExperimentSpotDesignDAO(ExperimentSpotDesignDAO dao);

  public void insert(ExperimentSpotDesign obj);

  public void update(ExperimentSpotDesign obj);

  public ExperimentSpotDesign findByID(Integer expID);

  ExperimentSpotDesign updateDetached(ExperimentSpotDesign experiment);
  
  public List<ExperimentSpotDesign> list();
  

}

// ex:sw=4:ts=4:
