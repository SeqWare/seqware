package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.ShareProcessingDAO;
import net.sourceforge.seqware.common.model.ShareProcessing;

/**
 * <p>ShareProcessingService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ShareProcessingService {

  /**
   * <p>setShareProcessingDAO.</p>
   *
   * @param dao a {@link net.sourceforge.seqware.common.dao.ShareProcessingDAO} object.
   */
  public abstract void setShareProcessingDAO(ShareProcessingDAO dao);

  /**
   * <p>insert.</p>
   *
   * @param shareProcessing a {@link net.sourceforge.seqware.common.model.ShareProcessing} object.
   */
  public abstract void insert(ShareProcessing shareProcessing);

  /**
   * <p>update.</p>
   *
   * @param shareProcessing a {@link net.sourceforge.seqware.common.model.ShareProcessing} object.
   */
  public abstract void update(ShareProcessing shareProcessing);

  /**
   * <p>delete.</p>
   *
   * @param shareProcessing a {@link net.sourceforge.seqware.common.model.ShareProcessing} object.
   */
  public abstract void delete(ShareProcessing shareProcessing);

  /**
   * <p>findByID.</p>
   *
   * @param shareProcessingID a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareProcessing} object.
   */
  public abstract ShareProcessing findByID(Integer shareProcessingID);

  /**
   * <p>findByOwnerID.</p>
   *
   * @param registrationID a {@link java.lang.Integer} object.
   * @return a {@link java.util.List} object.
   */
  public abstract List<ShareProcessing> findByOwnerID(Integer registrationID);

  /**
   * <p>findBySWAccession.</p>
   *
   * @param swAccession a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareProcessing} object.
   */
  public abstract ShareProcessing findBySWAccession(Integer swAccession);

  /**
   * <p>updateDetached.</p>
   *
   * @param shareProcessing a {@link net.sourceforge.seqware.common.model.ShareProcessing} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareProcessing} object.
   */
  public abstract ShareProcessing updateDetached(ShareProcessing shareProcessing);

  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<ShareProcessing> list();
  
}
