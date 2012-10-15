package net.sourceforge.seqware.common.business.impl;

import java.util.List;

import net.sourceforge.seqware.common.business.ShareLaneService;
import net.sourceforge.seqware.common.dao.ShareLaneDAO;
import net.sourceforge.seqware.common.model.ShareLane;

/**
 * <p>ShareLaneServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ShareLaneServiceImpl implements ShareLaneService {

  private ShareLaneDAO dao;

  /** {@inheritDoc} */
  @Override
  public void setShareLaneDAO(ShareLaneDAO dao) {
    this.dao = dao;
  }

  /** {@inheritDoc} */
  @Override
  public void insert(ShareLane shareLane) {
    dao.insert(shareLane);
  }

  /** {@inheritDoc} */
  @Override
  public void update(ShareLane shareLane) {
    dao.update(shareLane);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(ShareLane shareLane) {
    dao.delete(shareLane);
  }

  /** {@inheritDoc} */
  @Override
  public ShareLane findByID(Integer shareLaneID) {
    return dao.findByID(shareLaneID);
  }

  /** {@inheritDoc} */
  @Override
  public List<ShareLane> findByOwnerID(Integer registrationID) {
    return dao.findByOwnerID(registrationID);
  }

  /** {@inheritDoc} */
  @Override
  public ShareLane findBySWAccession(Integer swAccession) {
    return dao.findBySWAccession(swAccession);
  }

  /** {@inheritDoc} */
  @Override
  public ShareLane updateDetached(ShareLane shareLane) {
    return dao.updateDetached(shareLane);
  }

    /** {@inheritDoc} */
    @Override
    public List<ShareLane> list() {
       return dao.list();
    }
}
