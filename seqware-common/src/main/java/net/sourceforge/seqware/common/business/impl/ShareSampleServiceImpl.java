package net.sourceforge.seqware.common.business.impl;

import java.util.List;

import net.sourceforge.seqware.common.business.ShareSampleService;
import net.sourceforge.seqware.common.dao.ShareSampleDAO;
import net.sourceforge.seqware.common.model.ShareSample;

public class ShareSampleServiceImpl implements ShareSampleService {

  private ShareSampleDAO dao;

  @Override
  public void setShareSampleDAO(ShareSampleDAO dao) {
    this.dao = dao;
  }

  @Override
  public void insert(ShareSample shareSample) {
    dao.insert(shareSample);
  }

  @Override
  public void update(ShareSample shareSample) {
    dao.update(shareSample);
  }

  @Override
  public void delete(ShareSample shareSample) {
    dao.delete(shareSample);
  }

  @Override
  public ShareSample findByID(Integer shareSampleID) {
    return dao.findByID(shareSampleID);
  }

  @Override
  public List<ShareSample> findByOwnerID(Integer registrationID) {
    return dao.findByOwnerID(registrationID);
  }

  @Override
  public ShareSample findBySWAccession(Integer swAccession) {
    return dao.findBySWAccession(swAccession);
  }

  @Override
  public ShareSample updateDetached(ShareSample shareSample) {
    return dao.updateDetached(shareSample);
  }

    @Override
    public List<ShareSample> list() {
        return dao.list();
    }
}
