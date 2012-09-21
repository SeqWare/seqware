package net.sourceforge.seqware.common.business;

import java.util.List;
import net.sourceforge.seqware.common.dao.ExperimentSpotDesignReadSpecDAO;
import net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec;

public interface ExperimentSpotDesignReadSpecService {
  public static final String NAME = "ExperimentSpotDesignReadSpecService";

  public void setExperimentSpotDesignReadSpecDAO(ExperimentSpotDesignReadSpecDAO dao);

  public void insert(ExperimentSpotDesignReadSpec obj);

  public void update(ExperimentSpotDesignReadSpec obj);

  public void delete(ExperimentSpotDesignReadSpec obj);

  public ExperimentSpotDesignReadSpec findByID(Integer expID);

  ExperimentSpotDesignReadSpec updateDetached(ExperimentSpotDesignReadSpec experiment);
  
  public List<ExperimentSpotDesignReadSpec> list();
}

// ex:sw=4:ts=4:
