package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.ShareLane;

public interface ShareLaneDAO {

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareLaneDAO#insert(
   * net.sourceforge.seqware.common.model.ShareLane)
   */
  public abstract void insert(ShareLane shareLane);

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareLaneDAO#update(
   * net.sourceforge.seqware.common.model.ShareLane)
   */
  public abstract void update(ShareLane shareLane);

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareLaneDAO#delete(
   * net.sourceforge.seqware.common.model.ShareLane)
   */
  public abstract void delete(ShareLane shareLane);

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareLaneDAO#findByID
   * (java.lang.Integer)
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
  @SuppressWarnings("unchecked")
  public abstract List<ShareLane> findByOwnerID(Integer registrationID);

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareLaneDAO#
   * findBySWAccession(java.lang.Integer)
   */
  @SuppressWarnings({ "unchecked" })
  public abstract ShareLane findBySWAccession(Integer swAccession);

  public abstract ShareLane updateDetached(ShareLane shareLane);
  
  public List<ShareLane> list();

}