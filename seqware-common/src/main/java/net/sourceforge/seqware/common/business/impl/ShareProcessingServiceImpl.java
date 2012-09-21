package net.sourceforge.seqware.common.business.impl;

import java.util.List;

import net.sourceforge.seqware.common.business.ShareProcessingService;
import net.sourceforge.seqware.common.dao.ShareProcessingDAO;
import net.sourceforge.seqware.common.model.ShareProcessing;

public class ShareProcessingServiceImpl implements ShareProcessingService {

  private ShareProcessingDAO dao;

  @Override
  public void setShareProcessingDAO(ShareProcessingDAO dao) {
    this.dao = dao;
  }

  @Override
  public void insert(ShareProcessing shareProcessing) {
    dao.insert(shareProcessing);
  }

  @Override
  public void update(ShareProcessing shareProcessing) {
    dao.update(shareProcessing);
  }

  @Override
  public void delete(ShareProcessing shareProcessing) {
    dao.delete(shareProcessing);
  }

  @Override
  public ShareProcessing findByID(Integer shareProcessingID) {
    return dao.findByID(shareProcessingID);
  }

  @Override
  public List<ShareProcessing> findByOwnerID(Integer registrationID) {
    return dao.findByOwnerID(registrationID);
  }

  @Override
  public ShareProcessing findBySWAccession(Integer swAccession) {
    return dao.findBySWAccession(swAccession);
  }

  @Override
  public ShareProcessing updateDetached(ShareProcessing shareProcessing) {
    return dao.updateDetached(shareProcessing);
  }

    @Override
    public List<ShareProcessing> list() {
       return dao.list();
    }
}
