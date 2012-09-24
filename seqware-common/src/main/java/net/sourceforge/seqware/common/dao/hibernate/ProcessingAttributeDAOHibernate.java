package net.sourceforge.seqware.common.dao.hibernate;

import java.util.List;

import net.sourceforge.seqware.common.dao.ProcessingAttributeDAO;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingAttribute;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ProcessingAttributeDAOHibernate extends HibernateDaoSupport implements ProcessingAttributeDAO {

  @Override
  public void insert(ProcessingAttribute processingAttribute) {
    this.getHibernateTemplate().save(processingAttribute);

  }

  @Override
  public void update(ProcessingAttribute processingAttribute) {
    this.getHibernateTemplate().update(processingAttribute);
  }

  @Override
  public void delete(ProcessingAttribute processingAttribute) {
    this.getHibernateTemplate().delete(processingAttribute);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<ProcessingAttribute> findAll(Processing processing) {
    String query = "from ProcessingAttribute as ia where ia.processing.processingId = ?";
    Object[] parameters = { processing.getProcessingId() };
    return this.getHibernateTemplate().find(query, parameters);
  }

    @Override
    public List<ProcessingAttribute> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
