package net.sourceforge.seqware.common.dao.hibernate;

import java.util.List;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.LaneAttribute;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

/**
 * <p>
 * LaneAttributeDAOHibernate class.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class LaneAttributeDAOHibernate extends HibernateDaoSupport implements LaneAttributeDAO {

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
