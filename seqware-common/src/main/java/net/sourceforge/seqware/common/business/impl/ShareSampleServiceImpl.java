package net.sourceforge.seqware.common.business.impl;

import java.util.List;

import net.sourceforge.seqware.common.business.ShareSampleService;
import net.sourceforge.seqware.common.dao.ShareSampleDAO;
import net.sourceforge.seqware.common.model.ShareSample;

/**
 * <p>ShareSampleServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ShareSampleServiceImpl implements ShareSampleService {

  private ShareSampleDAO dao;

  /** {@inheritDoc} */
  @Override
  public void setShareSampleDAO(ShareSampleDAO dao) {
    this.dao = dao;
  }

  /** {@inheritDoc} */
  @Override
  public void insert(ShareSample shareSample) {
    dao.insert(shareSample);
  }

  /** {@inheritDoc} */
  @Override
  public void update(ShareSample shareSample) {
    dao.update(shareSample);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(ShareSample shareSample) {
    dao.delete(shareSample);
  }

  /** {@inheritDoc} */
  @Override
  public ShareSample findByID(Integer shareSampleID) {
    return dao.findByID(shareSampleID);
  }

  /** {@inheritDoc} */
  @Override
  public List<ShareSample> findByOwnerID(Integer registrationID) {
    return dao.findByOwnerID(registrationID);
  }

  /** {@inheritDoc} */
  @Override
  public ShareSample findBySWAccession(Integer swAccession) {
    return dao.findBySWAccession(swAccession);
  }

  /** {@inheritDoc} */
  @Override
  public ShareSample updateDetached(ShareSample shareSample) {
    return dao.updateDetached(shareSample);
  }

    /** {@inheritDoc} */
    @Override
    public List<ShareSample> list() {
        return dao.list();
    }
}
