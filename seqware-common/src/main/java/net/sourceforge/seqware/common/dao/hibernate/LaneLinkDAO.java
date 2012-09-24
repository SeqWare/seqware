package net.sourceforge.seqware.common.dao.hibernate;

import java.util.List;

import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.LaneAttribute;

public interface LaneLinkDAO {

  public abstract void insert(LaneAttribute laneAttribute);

  public abstract void update(LaneAttribute laneAttribute);

  public abstract void delete(LaneAttribute laneAttribute);

  @SuppressWarnings("unchecked")
  public abstract List<LaneAttribute> findAll(Lane lane);

}