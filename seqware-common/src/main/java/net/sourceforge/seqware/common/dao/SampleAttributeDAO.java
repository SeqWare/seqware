package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SampleAttribute;

/**
 * <p>SampleAttributeDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface SampleAttributeDAO {

  /**
   * <p>insert.</p>
   *
   * @param sampleAttribute a {@link net.sourceforge.seqware.common.model.SampleAttribute} object.
   */
  public abstract void insert(SampleAttribute sampleAttribute);

  /**
   * <p>update.</p>
   *
   * @param sampleAttribute a {@link net.sourceforge.seqware.common.model.SampleAttribute} object.
   */
  public abstract void update(SampleAttribute sampleAttribute);

  /**
   * <p>delete.</p>
   *
   * @param sampleAttribute a {@link net.sourceforge.seqware.common.model.SampleAttribute} object.
   */
  public abstract void delete(SampleAttribute sampleAttribute);

  /**
   * <p>findAll.</p>
   *
   * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
   * @return a {@link java.util.List} object.
   */
  @SuppressWarnings("unchecked")
  public abstract List<SampleAttribute> findAll(Sample sample);
  
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<SampleAttribute> list();

}
