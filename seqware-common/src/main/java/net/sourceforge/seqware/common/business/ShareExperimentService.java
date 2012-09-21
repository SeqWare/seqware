package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.ShareExperimentDAO;
import net.sourceforge.seqware.common.model.ShareExperiment;

public interface ShareExperimentService {

  public abstract void setShareExperimentDAO(ShareExperimentDAO dao);

  public abstract void insert(ShareExperiment shareExperiment);

  public abstract void update(ShareExperiment shareExperiment);

  public abstract void delete(ShareExperiment shareExperiment);

  public abstract ShareExperiment findByID(Integer shareExperimentID);

  public abstract List<ShareExperiment> findByOwnerID(Integer registrationID);

  public abstract ShareExperiment findBySWAccession(Integer swAccession);

  public abstract ShareExperiment updateDetached(ShareExperiment shareExperiment);

  public List<ShareExperiment> list();
  
}