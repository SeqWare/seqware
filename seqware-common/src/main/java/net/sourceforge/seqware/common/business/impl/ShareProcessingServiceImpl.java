package net.sourceforge.seqware.common.business.impl;

import java.util.List;

import net.sourceforge.seqware.common.business.ShareProcessingService;
import net.sourceforge.seqware.common.dao.ShareProcessingDAO;
import net.sourceforge.seqware.common.model.ShareProcessing;

/**
 * <p>ShareProcessingServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ShareProcessingServiceImpl implements ShareProcessingService {

  private ShareProcessingDAO dao;

  /** {@inheritDoc} */
  @Override
  public void setShareProcessingDAO(ShareProcessingDAO dao) {
    this.dao = dao;
  }

  /** {@inheritDoc} */
  @Override
  public void insert(ShareProcessing shareProcessing) {
    dao.insert(shareProcessing);
  }

  /** {@inheritDoc} */
  @Override
  public void update(ShareProcessing shareProcessing) {
    dao.update(shareProcessing);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(ShareProcessing shareProcessing) {
    dao.delete(shareProcessing);
  }

  /** {@inheritDoc} */
  @Override
  public ShareProcessing findByID(Integer shareProcessingID) {
    return dao.findByID(shareProcessingID);
  }

  /** {@inheritDoc} */
  @Override
  public List<ShareProcessing> findByOwnerID(Integer registrationID) {
    return dao.findByOwnerID(registrationID);
  }

  /** {@inheritDoc} */
  @Override
  public ShareProcessing findBySWAccession(Integer swAccession) {
    return dao.findBySWAccession(swAccession);
  }

  /** {@inheritDoc} */
  @Override
  public ShareProcessing updateDetached(ShareProcessing shareProcessing) {
    return dao.updateDetached(shareProcessing);
  }

    /** {@inheritDoc} */
    @Override
    public List<ShareProcessing> list() {
       return dao.list();
    }
}
