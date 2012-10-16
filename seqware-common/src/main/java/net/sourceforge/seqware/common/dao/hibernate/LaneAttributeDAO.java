package net.sourceforge.seqware.common.dao.hibernate;

import java.util.List;

import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.LaneAttribute;

/**
 * <p>LaneAttributeDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface LaneAttributeDAO {

  /**
   * <p>insert.</p>
   *
   * @param laneAttribute a {@link net.sourceforge.seqware.common.model.LaneAttribute} object.
   */
  public abstract void insert(LaneAttribute laneAttribute);

  /**
   * <p>update.</p>
   *
   * @param laneAttribute a {@link net.sourceforge.seqware.common.model.LaneAttribute} object.
   */
  public abstract void update(LaneAttribute laneAttribute);

  /**
   * <p>delete.</p>
   *
   * @param laneAttribute a {@link net.sourceforge.seqware.common.model.LaneAttribute} object.
   */
  public abstract void delete(LaneAttribute laneAttribute);

  /**
   * <p>findAll.</p>
   *
   * @param lane a {@link net.sourceforge.seqware.common.model.Lane} object.
   * @return a {@link java.util.List} object.
   */
  @SuppressWarnings("unchecked")
  public abstract List<LaneAttribute> findAll(Lane lane);

}
