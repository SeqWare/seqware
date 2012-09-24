package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.ShareExperiment;

public interface ShareExperimentDAO {

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.seqware.common.dao.hibernate.ShareExperimentDAO#insert(
   * net.sourceforge.seqware.common.model.ShareExperiment)
   */
  public abstract void insert(ShareExperiment shareExperiment);

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.seqware.common.dao.hibernate.ShareExperimentDAO#update(
   * net.sourceforge.seqware.common.model.ShareExperiment)
   */
  public abstract void update(ShareExperiment shareExperiment);

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.seqware.common.dao.hibernate.ShareExperimentDAO#delete(
   * net.sourceforge.seqware.common.model.ShareExperiment)
   */
  public abstract void delete(ShareExperiment shareExperiment);

  /*
   * (non-Javadoc)
   * 
   * @see
   * net.sourceforge.seqware.common.dao.hibernate.ShareExperimentDAO#findByID
   * (java.lang.Integer)
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
  @SuppressWarnings("unchecked")
  public abstract List<ShareExperiment> findByOwnerID(Integer registrationID);

  /*
   * (non-Javadoc)
   * 
   * @see net.sourceforge.seqware.common.dao.hibernate.ShareExperimentDAO#
   * findBySWAccession(java.lang.Integer)
   */
  @SuppressWarnings({ "unchecked" })
  public abstract ShareExperiment findBySWAccession(Integer swAccession);

  public abstract ShareExperiment updateDetached(ShareExperiment shareExperiment);
  
  public List<ShareExperiment> list();

}