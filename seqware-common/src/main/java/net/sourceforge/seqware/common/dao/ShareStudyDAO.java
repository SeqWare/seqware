package net.sourceforge.seqware.common.dao;

import java.util.List;
import net.sourceforge.seqware.common.model.ShareStudy;

/**
 * <p>ShareStudyDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ShareStudyDAO {
  /**
   * <p>insert.</p>
   *
   * @param ShareStudy a {@link net.sourceforge.seqware.common.model.ShareStudy} object.
   */
  public void insert(ShareStudy ShareStudy);

  /**
   * <p>update.</p>
   *
   * @param ShareStudy a {@link net.sourceforge.seqware.common.model.ShareStudy} object.
   */
  public void update(ShareStudy ShareStudy);

  /**
   * <p>delete.</p>
   *
   * @param ShareStudy a {@link net.sourceforge.seqware.common.model.ShareStudy} object.
   */
  public void delete(ShareStudy ShareStudy);

  /**
   * <p>findByID.</p>
   *
   * @param id a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareStudy} object.
   */
  public ShareStudy findByID(Integer id);

  /**
   * <p>findByStudyIdAndRegistrationId.</p>
   *
   * @param studyId a {@link java.lang.Integer} object.
   * @param registrationId a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareStudy} object.
   */
  public ShareStudy findByStudyIdAndRegistrationId(Integer studyId, Integer registrationId);

  /**
   * <p>updateDetached.</p>
   *
   * @param shareStudy a {@link net.sourceforge.seqware.common.model.ShareStudy} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareStudy} object.
   */
  public ShareStudy updateDetached(ShareStudy shareStudy);
  
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<ShareStudy> list();
}
