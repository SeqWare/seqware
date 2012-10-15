package net.sourceforge.seqware.common.dao.hibernate;

import java.util.List;

import net.sourceforge.seqware.common.dao.SampleSearchDAO;
import net.sourceforge.seqware.common.model.SampleSearch;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
/**
 * <p>SampleSearchDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SampleSearchDAOHibernate implements SampleSearchDAO {

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  /** {@inheritDoc} */
  @Override
  public List<SampleSearch> list() {
    String queryStringCase = "from SampleSearch";

    Query query = currentSession().createQuery(queryStringCase);

    @SuppressWarnings("unchecked")
    List<SampleSearch> records = query.list();

    return records;
  }

  /** {@inheritDoc} */
  @Override
  public SampleSearch findById(Integer id) {
    String queryStringCase = "from SampleSearch as s where s.sampleSearchId = :id";
    Query query = currentSession().createQuery(queryStringCase);
    query.setLong("id", id);

    return (SampleSearch) query.uniqueResult();
  }

  /** {@inheritDoc} */
  @Override
  public Integer create(SampleSearch sampleSearch) {
    return (Integer) currentSession().save(sampleSearch);
  }

}
