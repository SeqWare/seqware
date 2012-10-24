package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.ShareLane;

/**
 * <p>ShareLaneDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ShareLaneDAO {

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareLaneDAO#insert(
   * net.sourceforge.seqware.common.model.ShareLane)
   */
  /**
   * <p>insert.</p>
   *
   * @param shareLane a {@link net.sourceforge.seqware.common.model.ShareLane} object.
   */
  public abstract void insert(ShareLane shareLane);

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareLaneDAO#update(
   * net.sourceforge.seqware.common.model.ShareLane)
   */
  /**
   * <p>update.</p>
   *
   * @param shareLane a {@link net.sourceforge.seqware.common.model.ShareLane} object.
   */
  public abstract void update(ShareLane shareLane);

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareLaneDAO#delete(
   * net.sourceforge.seqware.common.model.ShareLane)
   */
  /**
   * <p>delete.</p>
   *
   * @param shareLane a {@link net.sourceforge.seqware.common.model.ShareLane} object.
   */
  public abstract void delete(ShareLane shareLane);

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareLaneDAO#findByID
   * (java.lang.Integer)
   */
  /**
   * <p>findByID.</p>
   *
   * @param shareLaneID a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareLane} object.
   */
  @SuppressWarnings("rawtypes")
  public abstract ShareLane findByID(Integer shareLaneID);

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.seqware.common.dao.hibernate.ShareLaneDAO#findByOwnerID
   * (java.lang.Integer)
   */
  /**
   * <p>findByOwnerID.</p>
   *
   * @param registrationID a {@link java.lang.Integer} object.
   * @return a {@link java.util.List} object.
   */
  @SuppressWarnings("unchecked")
  public abstract List<ShareLane> findByOwnerID(Integer registrationID);

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareLaneDAO#
   * findBySWAccession(java.lang.Integer)
   */
  /**
   * <p>findBySWAccession.</p>
   *
   * @param swAccession a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareLane} object.
   */
  @SuppressWarnings({ "unchecked" })
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
