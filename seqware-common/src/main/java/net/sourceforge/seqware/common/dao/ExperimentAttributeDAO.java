package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.ExperimentAttribute;

/**
 * <p>ExperimentAttributeDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ExperimentAttributeDAO {

  /**
   * <p>insert.</p>
   *
   * @param experimentAttribute a {@link net.sourceforge.seqware.common.model.ExperimentAttribute} object.
   */
  public abstract void insert(ExperimentAttribute experimentAttribute);

  /**
   * <p>update.</p>
   *
   * @param experimentAttribute a {@link net.sourceforge.seqware.common.model.ExperimentAttribute} object.
   */
  public abstract void update(ExperimentAttribute experimentAttribute);

  /**
   * <p>delete.</p>
   *
   * @param experimentAttribute a {@link net.sourceforge.seqware.common.model.ExperimentAttribute} object.
   */
  public abstract void delete(ExperimentAttribute experimentAttribute);

  /**
   * <p>findAll.</p>
   *
   * @param experiment a {@link net.sourceforge.seqware.common.model.Experiment} object.
   * @return a {@link java.util.List} object.
   */
  @SuppressWarnings("unchecked")
  public abstract List<ExperimentAttribute> findAll(Experiment experiment);
  
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<ExperimentAttribute> list();

}
