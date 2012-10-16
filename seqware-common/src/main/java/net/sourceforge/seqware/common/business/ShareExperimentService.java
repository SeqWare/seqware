package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.ShareExperimentDAO;
import net.sourceforge.seqware.common.model.ShareExperiment;

/**
 * <p>ShareExperimentService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ShareExperimentService {

  /**
   * <p>setShareExperimentDAO.</p>
   *
   * @param dao a {@link net.sourceforge.seqware.common.dao.ShareExperimentDAO} object.
   */
  public abstract void setShareExperimentDAO(ShareExperimentDAO dao);

  /**
   * <p>insert.</p>
   *
   * @param shareExperiment a {@link net.sourceforge.seqware.common.model.ShareExperiment} object.
   */
  public abstract void insert(ShareExperiment shareExperiment);

  /**
   * <p>update.</p>
   *
   * @param shareExperiment a {@link net.sourceforge.seqware.common.model.ShareExperiment} object.
   */
  public abstract void update(ShareExperiment shareExperiment);

  /**
   * <p>delete.</p>
   *
   * @param shareExperiment a {@link net.sourceforge.seqware.common.model.ShareExperiment} object.
   */
  public abstract void delete(ShareExperiment shareExperiment);

  /**
   * <p>findByID.</p>
   *
   * @param shareExperimentID a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareExperiment} object.
   */
  public abstract ShareExperiment findByID(Integer shareExperimentID);

  /**
   * <p>findByOwnerID.</p>
   *
   * @param registrationID a {@link java.lang.Integer} object.
   * @return a {@link java.util.List} object.
   */
  public abstract List<ShareExperiment> findByOwnerID(Integer registrationID);

  /**
   * <p>findBySWAccession.</p>
   *
   * @param swAccession a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareExperiment} object.
   */
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
