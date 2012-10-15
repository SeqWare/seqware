package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.ShareProcessing;

/**
 * <p>ShareProcessingDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ShareProcessingDAO {

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.seqware.common.dao.hibernate.ShareProcessingDAO#insert(
   * net.sourceforge.seqware.common.model.ShareProcessing)
   */
  /**
   * <p>insert.</p>
   *
   * @param shareProcessing a {@link net.sourceforge.seqware.common.model.ShareProcessing} object.
   */
  public abstract void insert(ShareProcessing shareProcessing);

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.seqware.common.dao.hibernate.ShareProcessingDAO#update(
   * net.sourceforge.seqware.common.model.ShareProcessing)
   */
  /**
   * <p>update.</p>
   *
   * @param shareProcessing a {@link net.sourceforge.seqware.common.model.ShareProcessing} object.
   */
  public abstract void update(ShareProcessing shareProcessing);

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.seqware.common.dao.hibernate.ShareProcessingDAO#delete(
   * net.sourceforge.seqware.common.model.ShareProcessing)
   */
  /**
   * <p>delete.</p>
   *
   * @param shareProcessing a {@link net.sourceforge.seqware.common.model.ShareProcessing} object.
   */
  public abstract void delete(ShareProcessing shareProcessing);

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.seqware.common.dao.hibernate.ShareProcessingDAO#findByID
   * (java.lang.Integer)
   */
  /**
   * <p>findByID.</p>
   *
   * @param shareProcessingID a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareProcessing} object.
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
  /**
   * <p>findByOwnerID.</p>
   *
   * @param registrationID a {@link java.lang.Integer} object.
   * @return a {@link java.util.List} object.
   */
  @SuppressWarnings("unchecked")
  public abstract List<ShareProcessing> findByOwnerID(Integer registrationID);

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareProcessingDAO#
   * findBySWAccession(java.lang.Integer)
   */
  /**
   * <p>findBySWAccession.</p>
   *
   * @param swAccession a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareProcessing} object.
   */
  @SuppressWarnings({ "unchecked" })
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
