package net.sourceforge.seqware.common.dao.hibernate;

import java.util.List;

import net.sourceforge.seqware.common.dao.SampleAttributeDAO;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SampleAttribute;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>SampleAttributeDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SampleAttributeDAOHibernate extends HibernateDaoSupport implements SampleAttributeDAO {

  /** {@inheritDoc} */
  @Override
  public void insert(SampleAttribute sampleAttribute) {
    this.getHibernateTemplate().save(sampleAttribute);

  }

  /** {@inheritDoc} */
  @Override
  public void update(SampleAttribute sampleAttribute) {
    this.getHibernateTemplate().update(sampleAttribute);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(SampleAttribute sampleAttribute) {
    this.getHibernateTemplate().delete(sampleAttribute);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("unchecked")
  public List<SampleAttribute> findAll(Sample sample) {
    String query = "from SampleAttribute as ia where ia.sample.sampleId = ?";
    Object[] parameters = { sample.getSampleId() };
    return this.getHibernateTemplate().find(query, parameters);
  }

    /** {@inheritDoc} */
    @Override
    public List<SampleAttribute> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
