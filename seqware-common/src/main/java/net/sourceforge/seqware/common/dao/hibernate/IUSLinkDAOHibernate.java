package net.sourceforge.seqware.common.dao.hibernate;

import java.util.List;

import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.IUSLink;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>IUSLinkDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class IUSLinkDAOHibernate extends HibernateDaoSupport implements IUSLinkDAO {

  /** {@inheritDoc} */
  @Override
  public void insert(IUSLink IUSLink) {
    this.getHibernateTemplate().save(IUSLink);

  }

  /** {@inheritDoc} */
  @Override
  public void update(IUSLink IUSLink) {
    this.getHibernateTemplate().update(IUSLink);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(IUSLink IUSLink) {
    this.getHibernateTemplate().delete(IUSLink);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("unchecked")
  public List<IUSLink> findAll(IUS ius) {
    String query = "from IUSLink as ia where ia.ius.iusId = ?";
    Object[] parameters = { ius.getIusId() };
    return this.getHibernateTemplate().find(query, parameters);
  }
}
