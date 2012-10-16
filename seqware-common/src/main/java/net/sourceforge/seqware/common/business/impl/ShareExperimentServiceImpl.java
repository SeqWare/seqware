package net.sourceforge.seqware.common.business.impl;

import java.util.List;

import net.sourceforge.seqware.common.business.ShareExperimentService;
import net.sourceforge.seqware.common.dao.ShareExperimentDAO;
import net.sourceforge.seqware.common.model.ShareExperiment;

/**
 * <p>ShareExperimentServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ShareExperimentServiceImpl implements ShareExperimentService {

  private ShareExperimentDAO dao;

  /** {@inheritDoc} */
  @Override
  public void setShareExperimentDAO(ShareExperimentDAO dao) {
    this.dao = dao;
  }

  /** {@inheritDoc} */
  @Override
  public void insert(ShareExperiment shareExperiment) {
    dao.insert(shareExperiment);
  }

  /** {@inheritDoc} */
  @Override
  public void update(ShareExperiment shareExperiment) {
    dao.update(shareExperiment);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(ShareExperiment shareExperiment) {
    dao.delete(shareExperiment);
  }

  /** {@inheritDoc} */
  @Override
  public ShareExperiment findByID(Integer shareExperimentID) {
    return dao.findByID(shareExperimentID);
  }

  /** {@inheritDoc} */
  @Override
  public List<ShareExperiment> findByOwnerID(Integer registrationID) {
    return dao.findByOwnerID(registrationID);
  }

  /** {@inheritDoc} */
  @Override
  public ShareExperiment findBySWAccession(Integer swAccession) {
    return dao.findBySWAccession(swAccession);
  }

  /** {@inheritDoc} */
  @Override
  public ShareExperiment updateDetached(ShareExperiment shareExperiment) {
    return dao.updateDetached(shareExperiment);
  }

    /** {@inheritDoc} */
    @Override
    public List<ShareExperiment> list() {
        return dao.list();
    }
}
