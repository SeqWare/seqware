package net.sourceforge.seqware.common.business;

import java.util.List;
import net.sourceforge.seqware.common.dao.ShareStudyDAO;
import net.sourceforge.seqware.common.model.ShareStudy;

public interface ShareStudyService {
  public static final String NAME = "shareStudyService";

  public void setShareStudyDAO(ShareStudyDAO shareStudyDAO);

  public void insert(ShareStudy shareStudy);

  public void update(ShareStudy shareStudy);

  public void delete(ShareStudy shareStudy);

  public ShareStudy findByID(Integer id);

  public ShareStudy findByStudyIdAndRegistrationId(Integer studyId, Integer registrationId);

  public ShareStudy findBySWAccession(Integer swAccession);

  public boolean isExistsShare(Integer studyId, Integer registrationId);

  ShareStudy updateDetached(ShareStudy shareStudy);
  
  public List<ShareStudy> list();
  
}