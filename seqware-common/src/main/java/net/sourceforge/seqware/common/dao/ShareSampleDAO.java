package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.ShareSample;

public interface ShareSampleDAO {

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareSampleDAO#insert(
   * net.sourceforge.seqware.common.model.ShareSample)
   */
  public abstract void insert(ShareSample shareSample);

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareSampleDAO#update(
   * net.sourceforge.seqware.common.model.ShareSample)
   */
  public abstract void update(ShareSample shareSample);

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareSampleDAO#delete(
   * net.sourceforge.seqware.common.model.ShareSample)
   */
  public abstract void delete(ShareSample shareSample);

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareSampleDAO#findByID
   * (java.lang.Integer)
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
  @SuppressWarnings("unchecked")
  public abstract List<ShareSample> findByOwnerID(Integer registrationID);

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareSampleDAO#
   * findBySWAccession(java.lang.Integer)
   */
  @SuppressWarnings({ "unchecked" })
  public abstract ShareSample findBySWAccession(Integer swAccession);

  public abstract ShareSample updateDetached(ShareSample shareSample);
  public List<ShareSample> list();

}