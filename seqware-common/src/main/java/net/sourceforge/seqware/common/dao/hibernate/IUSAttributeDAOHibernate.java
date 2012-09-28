package net.sourceforge.seqware.common.dao.hibernate;

import java.util.List;

import net.sourceforge.seqware.common.dao.IUSAttributeDAO;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.IUSAttribute;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class IUSAttributeDAOHibernate extends HibernateDaoSupport implements IUSAttributeDAO {

  @Override
  public void insert(IUSAttribute IUSAttribute) {
    this.getHibernateTemplate().save(IUSAttribute);

  }

  @Override
  public void update(IUSAttribute IUSAttribute) {
    this.getHibernateTemplate().update(IUSAttribute);
  }

  @Override
  public void delete(IUSAttribute IUSAttribute) {
    this.getHibernateTemplate().delete(IUSAttribute);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<IUSAttribute> findAll(IUS ius) {
    String query = "from IUSAttribute as ia where ia.ius.iusId = ?";
    Object[] parameters = { ius.getIusId() };
    return this.getHibernateTemplate().find(query, parameters);
  }

    @Override
    public List<IUSAttribute> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
