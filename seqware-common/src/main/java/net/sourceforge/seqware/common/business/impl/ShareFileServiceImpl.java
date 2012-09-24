package net.sourceforge.seqware.common.business.impl;

import java.util.List;

import net.sourceforge.seqware.common.business.ShareFileService;
import net.sourceforge.seqware.common.dao.ShareFileDAO;
import net.sourceforge.seqware.common.model.ShareFile;

public class ShareFileServiceImpl implements ShareFileService {

  private ShareFileDAO dao;

  @Override
  public void setShareFileDAO(ShareFileDAO dao) {
    this.dao = dao;
  }

  @Override
  public void insert(ShareFile shareFile) {
    dao.insert(shareFile);
  }

  @Override
  public void update(ShareFile shareFile) {
    dao.update(shareFile);
  }

  @Override
  public void delete(ShareFile shareFile) {
    dao.delete(shareFile);
  }

  @Override
  public ShareFile findByID(Integer shareFileID) {
    return dao.findByID(shareFileID);
  }

  @Override
  public List<ShareFile> findByOwnerID(Integer registrationID) {
    return dao.findByOwnerID(registrationID);
  }

  @Override
  public ShareFile updateDetached(ShareFile shareFile) {
    return dao.updateDetached(shareFile);
  }

  @Override
  public ShareFile findBySWAccession(Integer swAccession) {
    return dao.findBySWAccession(swAccession);
  }

    @Override
    public List<ShareFile> list() {
        return dao.list();
    }
}
