package net.sourceforge.seqware.common.dao.hibernate;

import java.util.List;

import net.sourceforge.seqware.common.dao.ProcessingAttributeDAO;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingAttribute;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>ProcessingAttributeDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ProcessingAttributeDAOHibernate extends HibernateDaoSupport implements ProcessingAttributeDAO {

  /** {@inheritDoc} */
  @Override
  public void insert(ProcessingAttribute processingAttribute) {
    this.getHibernateTemplate().save(processingAttribute);

  }

  /** {@inheritDoc} */
  @Override
  public void update(ProcessingAttribute processingAttribute) {
    this.getHibernateTemplate().update(processingAttribute);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(ProcessingAttribute processingAttribute) {
    this.getHibernateTemplate().delete(processingAttribute);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("unchecked")
  public List<ProcessingAttribute> findAll(Processing processing) {
    String query = "from ProcessingAttribute as ia where ia.processing.processingId = ?";
    Object[] parameters = { processing.getProcessingId() };
    return this.getHibernateTemplate().find(query, parameters);
  }

    /** {@inheritDoc} */
    @Override
    public List<ProcessingAttribute> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
