package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.ShareSampleDAO;
import net.sourceforge.seqware.common.model.ShareSample;

/**
 * <p>ShareSampleService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ShareSampleService {

  /**
   * <p>setShareSampleDAO.</p>
   *
   * @param dao a {@link net.sourceforge.seqware.common.dao.ShareSampleDAO} object.
   */
  public abstract void setShareSampleDAO(ShareSampleDAO dao);

  /**
   * <p>insert.</p>
   *
   * @param shareSample a {@link net.sourceforge.seqware.common.model.ShareSample} object.
   */
  public abstract void insert(ShareSample shareSample);

  /**
   * <p>update.</p>
   *
   * @param shareSample a {@link net.sourceforge.seqware.common.model.ShareSample} object.
   */
  public abstract void update(ShareSample shareSample);

  /**
   * <p>delete.</p>
   *
   * @param shareSample a {@link net.sourceforge.seqware.common.model.ShareSample} object.
   */
  public abstract void delete(ShareSample shareSample);

  /**
   * <p>findByID.</p>
   *
   * @param shareSampleID a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareSample} object.
   */
  public abstract ShareSample findByID(Integer shareSampleID);

  /**
   * <p>findByOwnerID.</p>
   *
   * @param registrationID a {@link java.lang.Integer} object.
   * @return a {@link java.util.List} object.
   */
  public abstract List<ShareSample> findByOwnerID(Integer registrationID);

  /**
   * <p>findBySWAccession.</p>
   *
   * @param swAccession a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.ShareSample} object.
   */
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
