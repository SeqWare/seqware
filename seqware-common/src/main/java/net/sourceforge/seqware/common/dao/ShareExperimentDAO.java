package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.ShareExperiment;

/**
 * <p>ShareExperimentDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ShareExperimentDAO {

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.seqware.common.dao.hibernate.ShareExperimentDAO#insert(
   * net.sourceforge.seqware.common.model.ShareExperiment)
   */
  /**
   * <p>insert.</p>
   *
   * @param shareExperiment a {@link net.sourceforge.seqware.common.model.ShareExperiment} object.
   */
  public abstract void insert(ShareExperiment shareExperiment);

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.seqware.common.dao.hibernate.ShareExperimentDAO#update(
   * net.sourceforge.seqware.common.model.ShareExperiment)
   */
  /**
   * <p>update.</p>
   *
   * @param shareExperiment a {@link net.sourceforge.seqware.common.model.ShareExperiment} object.
   */
  public abstract void update(ShareExperiment shareExperiment);

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.seqware.common.dao.hibernate.ShareExperimentDAO#delete(
   * net.sourceforge.seqware.common.model.ShareExperiment)
   */
  /**
   * <p>delete.</p>
   *
   * @param shareExperiment a {@link net.sourceforge.seqware.common.model.ShareExperiment} object.
   */
  public abstract void delete(ShareExperiment shareExperiment);

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.seqware.common.dao.hibernate.ShareExperimentDAO#findByID
   * (java.lang.Integer)
   */
  /**
   * <p>findByID.</p>
   *
   * @param shareExperimentID a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareExperiment} object.
   */
  @SuppressWarnings("rawtypes")
  public abstract ShareExperiment findByID(Integer shareExperimentID);

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.seqware.common.dao.hibernate.ShareExperimentDAO#findByOwnerID
   * (java.lang.Integer)
   */
  /**
   * <p>findByOwnerID.</p>
   *
   * @param registrationID a {@link java.lang.Integer} object.
   * @return a {@link java.util.List} object.
   */
  @SuppressWarnings("unchecked")
  public abstract List<ShareExperiment> findByOwnerID(Integer registrationID);

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareExperimentDAO#
   * findBySWAccession(java.lang.Integer)
   */
  /**
   * <p>findBySWAccession.</p>
   *
   * @param swAccession a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareExperiment} object.
   */
  @SuppressWarnings({ "unchecked" })
  public abstract ShareExperiment findBySWAccession(Integer swAccession);

  /**
   * <p>updateDetached.</p>
   *
   * @param shareExperiment a {@link net.sourceforge.seqware.common.model.ShareExperiment} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareExperiment} object.
   */
  public abstract ShareExperiment updateDetached(ShareExperiment shareExperiment);
  
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<ShareExperiment> list();

}
