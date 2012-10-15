package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.ExperimentLink;

/**
 * <p>ExperimentLinkDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ExperimentLinkDAO {

  /**
   * <p>insert.</p>
   *
   * @param experimentLink a {@link net.sourceforge.seqware.common.model.ExperimentLink} object.
   */
  public abstract void insert(ExperimentLink experimentLink);

  /**
   * <p>update.</p>
   *
   * @param experimentLink a {@link net.sourceforge.seqware.common.model.ExperimentLink} object.
   */
  public abstract void update(ExperimentLink experimentLink);

  /**
   * <p>delete.</p>
   *
   * @param experimentLink a {@link net.sourceforge.seqware.common.model.ExperimentLink} object.
   */
  public abstract void delete(ExperimentLink experimentLink);

  /**
   * <p>findAll.</p>
   *
   * @param experiment a {@link net.sourceforge.seqware.common.model.Experiment} object.
   * @return a {@link java.util.List} object.
   */
  @SuppressWarnings("unchecked")
  public abstract List<ExperimentLink> findAll(Experiment experiment);
  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<ExperimentLink> list();

}
