package net.sourceforge.seqware.common.business.impl;

import java.util.List;

import net.sourceforge.seqware.common.business.ShareExperimentService;
import net.sourceforge.seqware.common.dao.ShareExperimentDAO;
import net.sourceforge.seqware.common.model.ShareExperiment;

public class ShareExperimentServiceImpl implements ShareExperimentService {

  private ShareExperimentDAO dao;

  @Override
  public void setShareExperimentDAO(ShareExperimentDAO dao) {
    this.dao = dao;
  }

  @Override
  public void insert(ShareExperiment shareExperiment) {
    dao.insert(shareExperiment);
  }

  @Override
  public void update(ShareExperiment shareExperiment) {
    dao.update(shareExperiment);
  }

  @Override
  public void delete(ShareExperiment shareExperiment) {
    dao.delete(shareExperiment);
  }

  @Override
  public ShareExperiment findByID(Integer shareExperimentID) {
    return dao.findByID(shareExperimentID);
  }

  @Override
  public List<ShareExperiment> findByOwnerID(Integer registrationID) {
    return dao.findByOwnerID(registrationID);
  }

  @Override
  public ShareExperiment findBySWAccession(Integer swAccession) {
    return dao.findBySWAccession(swAccession);
  }

  @Override
  public ShareExperiment updateDetached(ShareExperiment shareExperiment) {
    return dao.updateDetached(shareExperiment);
  }

    @Override
    public List<ShareExperiment> list() {
        return dao.list();
    }
}
