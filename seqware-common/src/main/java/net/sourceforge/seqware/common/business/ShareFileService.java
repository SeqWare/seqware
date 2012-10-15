package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.ShareFileDAO;
import net.sourceforge.seqware.common.model.ShareFile;

/**
 * <p>ShareFileService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ShareFileService {

  /**
   * <p>setShareFileDAO.</p>
   *
   * @param dao a {@link net.sourceforge.seqware.common.dao.ShareFileDAO} object.
   */
  public abstract void setShareFileDAO(ShareFileDAO dao);

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
  public abstract ShareFile findByID(Integer shareFileID);

  /**
   * <p>findByOwnerID.</p>
   *
   * @param registrationID a {@link java.lang.Integer} object.
   * @return a {@link java.util.List} object.
   */
  public abstract List<ShareFile> findByOwnerID(Integer registrationID);

  /**
   * <p>findBySWAccession.</p>
   *
   * @param swAccession a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareFile} object.
   */
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
