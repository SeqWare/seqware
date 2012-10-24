package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.ShareFile;

/**
 * <p>ShareFileDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ShareFileDAO {

  /**
   * <p>insert.</p>
   *
   * @param shareFile a {@link net.sourceforge.seqware.common.model.ShareFile} object.
   */
  public abstract void insert(ShareFile shareFile);

  /**
   * <p>update.</p>
   *
   * @param shareFile a {@link net.sourceforge.seqware.common.model.ShareFile} object.
   */
  public abstract void update(ShareFile shareFile);

  /**
   * <p>delete.</p>
   *
   * @param shareFile a {@link net.sourceforge.seqware.common.model.ShareFile} object.
   */
  public abstract void delete(ShareFile shareFile);

  /**
   * <p>findByID.</p>
   *
   * @param shareFileID a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareFile} object.
   */
  @SuppressWarnings("rawtypes")
  public abstract ShareFile findByID(Integer shareFileID);

  /**
   * <p>findByOwnerID.</p>
   *
   * @param registrationID a {@link java.lang.Integer} object.
   * @return a {@link java.util.List} object.
   */
  @SuppressWarnings("unchecked")
  public abstract List<ShareFile> findByOwnerID(Integer registrationID);

  /**
   * <p>findBySWAccession.</p>
   *
   * @param swAccession a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareFile} object.
   */
  @SuppressWarnings({ "unchecked" })
  public abstract ShareFile findBySWAccession(Integer swAccession);

  /**
   * <p>updateDetached.</p>
   *
   * @param shareFile a {@link net.sourceforge.seqware.common.model.ShareFile} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareFile} object.
   */
  public abstract ShareFile updateDetached(ShareFile shareFile);
  
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<ShareFile> list();

}
