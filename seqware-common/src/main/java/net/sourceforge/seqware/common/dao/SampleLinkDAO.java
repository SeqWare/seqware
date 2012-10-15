package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SampleLink;

/**
 * <p>SampleLinkDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface SampleLinkDAO {

  /**
   * <p>insert.</p>
   *
   * @param sampleLink a {@link net.sourceforge.seqware.common.model.SampleLink} object.
   */
  public abstract void insert(SampleLink sampleLink);

  /**
   * <p>update.</p>
   *
   * @param sampleLink a {@link net.sourceforge.seqware.common.model.SampleLink} object.
   */
  public abstract void update(SampleLink sampleLink);

  /**
   * <p>delete.</p>
   *
   * @param sampleLink a {@link net.sourceforge.seqware.common.model.SampleLink} object.
   */
  public abstract void delete(SampleLink sampleLink);

  /**
   * <p>findAll.</p>
   *
   * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
   * @return a {@link java.util.List} object.
   */
  @SuppressWarnings("unchecked")
  public abstract List<SampleLink> findAll(Sample sample);
  
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<SampleLink> list();

}
