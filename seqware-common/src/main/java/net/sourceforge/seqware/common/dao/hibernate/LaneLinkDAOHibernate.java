package net.sourceforge.seqware.common.dao.hibernate;

import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.LaneAttribute;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import java.util.List;

/**
 * <p>
 * LaneLinkDAOHibernate class.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class LaneLinkDAOHibernate extends HibernateDaoSupport implements LaneLinkDAO {

    /** {@inheritDoc} */
    @Override
    public void insert(LaneAttribute laneAttribute) {
        this.getHibernateTemplate().save(laneAttribute);

    }

    /** {@inheritDoc} */
    @Override
    public void update(LaneAttribute laneAttribute) {
        this.getHibernateTemplate().update(laneAttribute);
    }

    /** {@inheritDoc} */
    @Override
    public void delete(LaneAttribute laneAttribute) {
        this.getHibernateTemplate().delete(laneAttribute);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public List<LaneAttribute> findAll(Lane lane) {
        String query = "from LaneAttribute as ia where ia.lane.laneId = ?";
        Object[] parameters = { lane.getLaneId() };
        return (List<LaneAttribute>) this.getHibernateTemplate().find(query, parameters);
    }

}
