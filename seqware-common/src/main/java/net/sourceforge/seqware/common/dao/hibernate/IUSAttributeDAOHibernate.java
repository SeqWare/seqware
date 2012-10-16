package net.sourceforge.seqware.common.dao.hibernate;

import java.util.List;

import net.sourceforge.seqware.common.dao.IUSAttributeDAO;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.IUSAttribute;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>IUSAttributeDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class IUSAttributeDAOHibernate extends HibernateDaoSupport implements IUSAttributeDAO {

  /** {@inheritDoc} */
  @Override
  public void insert(IUSAttribute IUSAttribute) {
    this.getHibernateTemplate().save(IUSAttribute);

  }

  /** {@inheritDoc} */
  @Override
  public void update(IUSAttribute IUSAttribute) {
    this.getHibernateTemplate().update(IUSAttribute);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(IUSAttribute IUSAttribute) {
    this.getHibernateTemplate().delete(IUSAttribute);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("unchecked")
  public List<IUSAttribute> findAll(IUS ius) {
    String query = "from IUSAttribute as ia where ia.ius.iusId = ?";
    Object[] parameters = { ius.getIusId() };
    return this.getHibernateTemplate().find(query, parameters);
  }

    /** {@inheritDoc} */
    @Override
    public List<IUSAttribute> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
