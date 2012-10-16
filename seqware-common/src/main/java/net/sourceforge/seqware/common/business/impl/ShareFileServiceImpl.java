package net.sourceforge.seqware.common.business.impl;

import java.util.List;

import net.sourceforge.seqware.common.business.ShareFileService;
import net.sourceforge.seqware.common.dao.ShareFileDAO;
import net.sourceforge.seqware.common.model.ShareFile;

/**
 * <p>ShareFileServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ShareFileServiceImpl implements ShareFileService {

  private ShareFileDAO dao;

  /** {@inheritDoc} */
  @Override
  public void setShareFileDAO(ShareFileDAO dao) {
    this.dao = dao;
  }

  /** {@inheritDoc} */
  @Override
  public void insert(ShareFile shareFile) {
    dao.insert(shareFile);
  }

  /** {@inheritDoc} */
  @Override
  public void update(ShareFile shareFile) {
    dao.update(shareFile);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(ShareFile shareFile) {
    dao.delete(shareFile);
  }

  /** {@inheritDoc} */
  @Override
  public ShareFile findByID(Integer shareFileID) {
    return dao.findByID(shareFileID);
  }

  /** {@inheritDoc} */
  @Override
  public List<ShareFile> findByOwnerID(Integer registrationID) {
    return dao.findByOwnerID(registrationID);
  }

  /** {@inheritDoc} */
  @Override
  public ShareFile updateDetached(ShareFile shareFile) {
    return dao.updateDetached(shareFile);
  }

  /** {@inheritDoc} */
  @Override
  public ShareFile findBySWAccession(Integer swAccession) {
    return dao.findBySWAccession(swAccession);
  }

    /** {@inheritDoc} */
    @Override
    public List<ShareFile> list() {
        return dao.list();
    }
}
