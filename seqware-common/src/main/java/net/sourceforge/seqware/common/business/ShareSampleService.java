package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.ShareSampleDAO;
import net.sourceforge.seqware.common.model.ShareSample;

public interface ShareSampleService {

  public abstract void setShareSampleDAO(ShareSampleDAO dao);

  public abstract void insert(ShareSample shareSample);

  public abstract void update(ShareSample shareSample);

  public abstract void delete(ShareSample shareSample);

  public abstract ShareSample findByID(Integer shareSampleID);

  public abstract List<ShareSample> findByOwnerID(Integer registrationID);

  public abstract ShareSample findBySWAccession(Integer swAccession);

  public abstract ShareSample updateDetached(ShareSample shareSample);
  
  public List<ShareSample> list();

}