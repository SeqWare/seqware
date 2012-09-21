package net.sourceforge.seqware.common.business.impl;

import java.util.List;

import net.sourceforge.seqware.common.business.ShareLaneService;
import net.sourceforge.seqware.common.dao.ShareLaneDAO;
import net.sourceforge.seqware.common.model.ShareLane;

public class ShareLaneServiceImpl implements ShareLaneService {

  private ShareLaneDAO dao;

  @Override
  public void setShareLaneDAO(ShareLaneDAO dao) {
    this.dao = dao;
  }

  @Override
  public void insert(ShareLane shareLane) {
    dao.insert(shareLane);
  }

  @Override
  public void update(ShareLane shareLane) {
    dao.update(shareLane);
  }

  @Override
  public void delete(ShareLane shareLane) {
    dao.delete(shareLane);
  }

  @Override
  public ShareLane findByID(Integer shareLaneID) {
    return dao.findByID(shareLaneID);
  }

  @Override
  public List<ShareLane> findByOwnerID(Integer registrationID) {
    return dao.findByOwnerID(registrationID);
  }

  @Override
  public ShareLane findBySWAccession(Integer swAccession) {
    return dao.findBySWAccession(swAccession);
  }

  @Override
  public ShareLane updateDetached(ShareLane shareLane) {
    return dao.updateDetached(shareLane);
  }

    @Override
    public List<ShareLane> list() {
       return dao.list();
    }
}
