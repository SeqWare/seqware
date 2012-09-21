package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.ShareProcessing;

public interface ShareProcessingDAO {

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.seqware.common.dao.hibernate.ShareProcessingDAO#insert(
   * net.sourceforge.seqware.common.model.ShareProcessing)
   */
  public abstract void insert(ShareProcessing shareProcessing);

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.seqware.common.dao.hibernate.ShareProcessingDAO#update(
   * net.sourceforge.seqware.common.model.ShareProcessing)
   */
  public abstract void update(ShareProcessing shareProcessing);

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.seqware.common.dao.hibernate.ShareProcessingDAO#delete(
   * net.sourceforge.seqware.common.model.ShareProcessing)
   */
  public abstract void delete(ShareProcessing shareProcessing);

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.seqware.common.dao.hibernate.ShareProcessingDAO#findByID
   * (java.lang.Integer)
   */
  @SuppressWarnings("rawtypes")
  public abstract ShareProcessing findByID(Integer shareProcessingID);

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.seqware.common.dao.hibernate.ShareProcessingDAO#findByOwnerID
   * (java.lang.Integer)
   */
  @SuppressWarnings("unchecked")
  public abstract List<ShareProcessing> findByOwnerID(Integer registrationID);

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareProcessingDAO#
   * findBySWAccession(java.lang.Integer)
   */
  @SuppressWarnings({ "unchecked" })
  public abstract ShareProcessing findBySWAccession(Integer swAccession);

  public abstract ShareProcessing updateDetached(ShareProcessing shareProcessing);
    public List<ShareProcessing> list();
}