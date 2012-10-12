package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.ShareSample;

/**
 * <p>ShareSampleDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ShareSampleDAO {

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareSampleDAO#insert(
   * net.sourceforge.seqware.common.model.ShareSample)
   */
  /**
   * <p>insert.</p>
   *
   * @param shareSample a {@link net.sourceforge.seqware.common.model.ShareSample} object.
   */
  public abstract void insert(ShareSample shareSample);

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareSampleDAO#update(
   * net.sourceforge.seqware.common.model.ShareSample)
   */
  /**
   * <p>update.</p>
   *
   * @param shareSample a {@link net.sourceforge.seqware.common.model.ShareSample} object.
   */
  public abstract void update(ShareSample shareSample);

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareSampleDAO#delete(
   * net.sourceforge.seqware.common.model.ShareSample)
   */
  /**
   * <p>delete.</p>
   *
   * @param shareSample a {@link net.sourceforge.seqware.common.model.ShareSample} object.
   */
  public abstract void delete(ShareSample shareSample);

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareSampleDAO#findByID
   * (java.lang.Integer)
   */
  /**
   * <p>findByID.</p>
   *
   * @param shareSampleID a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareSample} object.
   */
  @SuppressWarnings("rawtypes")
  public abstract ShareSample findByID(Integer shareSampleID);

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.seqware.common.dao.hibernate.ShareSampleDAO#findByOwnerID
   * (java.lang.Integer)
   */
  /**
   * <p>findByOwnerID.</p>
   *
   * @param registrationID a {@link java.lang.Integer} object.
   * @return a {@link java.util.List} object.
   */
  @SuppressWarnings("unchecked")
  public abstract List<ShareSample> findByOwnerID(Integer registrationID);

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareSampleDAO#
   * findBySWAccession(java.lang.Integer)
   */
  /**
   * <p>findBySWAccession.</p>
   *
   * @param swAccession a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareSample} object.
   */
  @SuppressWarnings({ "unchecked" })
  public abstract ShareSample findBySWAccession(Integer swAccession);

  /**
   * <p>updateDetached.</p>
   *
   * @param shareSample a {@link net.sourceforge.seqware.common.model.ShareSample} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareSample} object.
   */
  public abstract ShareSample updateDetached(ShareSample shareSample);
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<ShareSample> list();

}
