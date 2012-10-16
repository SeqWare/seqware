package net.sourceforge.seqware.common.dao.hibernate;

import java.util.List;

import net.sourceforge.seqware.common.dao.SampleLinkDAO;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SampleLink;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>SampleLinkDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SampleLinkDAOHibernate extends HibernateDaoSupport implements SampleLinkDAO {

  /** {@inheritDoc} */
  @Override
  public void insert(SampleLink sampleLink) {
    this.getHibernateTemplate().save(sampleLink);

  }

  /** {@inheritDoc} */
  @Override
  public void update(SampleLink sampleLink) {
    this.getHibernateTemplate().update(sampleLink);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(SampleLink sampleLink) {
    this.getHibernateTemplate().delete(sampleLink);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("unchecked")
  public List<SampleLink> findAll(Sample sample) {
    String query = "from SampleLink as sl where sl.sample.sampleId = ?";
    Object[] parameters = { sample.getSampleId() };
    return this.getHibernateTemplate().find(query, parameters);
  }

    /** {@inheritDoc} */
    @Override
    public List<SampleLink> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
