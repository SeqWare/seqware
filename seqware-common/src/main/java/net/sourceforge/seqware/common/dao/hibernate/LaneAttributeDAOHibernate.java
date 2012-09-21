package net.sourceforge.seqware.common.dao.hibernate;

import java.util.List;

import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.LaneAttribute;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class LaneAttributeDAOHibernate extends HibernateDaoSupport implements LaneAttributeDAO {

  @Override
  public void insert(LaneAttribute laneAttribute) {
    this.getHibernateTemplate().save(laneAttribute);

  }

  @Override
  public void update(LaneAttribute laneAttribute) {
    this.getHibernateTemplate().update(laneAttribute);
  }

  @Override
  public void delete(LaneAttribute laneAttribute) {
    this.getHibernateTemplate().delete(laneAttribute);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<LaneAttribute> findAll(Lane lane) {
    String query = "from LaneAttribute as ia where ia.lane.laneId = ?";
    Object[] parameters = { lane.getLaneId() };
    return this.getHibernateTemplate().find(query, parameters);
  }
}
