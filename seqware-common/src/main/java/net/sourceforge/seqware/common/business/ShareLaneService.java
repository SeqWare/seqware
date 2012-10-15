package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.ShareLaneDAO;
import net.sourceforge.seqware.common.model.ShareLane;

/**
 * <p>ShareLaneService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ShareLaneService {

  /**
   * <p>setShareLaneDAO.</p>
   *
   * @param dao a {@link net.sourceforge.seqware.common.dao.ShareLaneDAO} object.
   */
  public abstract void setShareLaneDAO(ShareLaneDAO dao);

  /**
   * <p>insert.</p>
   *
   * @param shareLane a {@link net.sourceforge.seqware.common.model.ShareLane} object.
   */
  public abstract void insert(ShareLane shareLane);

  /**
   * <p>update.</p>
   *
   * @param shareLane a {@link net.sourceforge.seqware.common.model.ShareLane} object.
   */
  public abstract void update(ShareLane shareLane);

  /**
   * <p>delete.</p>
   *
   * @param shareLane a {@link net.sourceforge.seqware.common.model.ShareLane} object.
   */
  public abstract void delete(ShareLane shareLane);

  /**
   * <p>findByID.</p>
   *
   * @param shareLaneID a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareLane} object.
   */
  public abstract ShareLane findByID(Integer shareLaneID);

  /**
   * <p>findByOwnerID.</p>
   *
   * @param registrationID a {@link java.lang.Integer} object.
   * @return a {@link java.util.List} object.
   */
  public abstract List<ShareLane> findByOwnerID(Integer registrationID);

  /**
   * <p>findBySWAccession.</p>
   *
   * @param swAccession a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareLane} object.
   */
  public abstract ShareLane findBySWAccession(Integer swAccession);

  /**
   * <p>updateDetached.</p>
   *
   * @param shareLane a {@link net.sourceforge.seqware.common.model.ShareLane} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareLane} object.
   */
  public abstract ShareLane updateDetached(ShareLane shareLane);
  
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<ShareLane> list();

}
