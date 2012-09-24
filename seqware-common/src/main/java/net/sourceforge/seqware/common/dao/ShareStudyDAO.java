package net.sourceforge.seqware.common.dao;

import java.util.List;
import net.sourceforge.seqware.common.model.ShareStudy;

public interface ShareStudyDAO {
  public void insert(ShareStudy ShareStudy);

  public void update(ShareStudy ShareStudy);

  public void delete(ShareStudy ShareStudy);

  public ShareStudy findByID(Integer id);

  public ShareStudy findByStudyIdAndRegistrationId(Integer studyId, Integer registrationId);

  public ShareStudy updateDetached(ShareStudy shareStudy);
  
  public List<ShareStudy> list();
}
