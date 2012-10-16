package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.sourceforge.seqware.common.dao.ProcessingRelationshipDAO;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingRelationship;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>ProcessingRelationshipDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ProcessingRelationshipDAOHibernate extends HibernateDaoSupport implements ProcessingRelationshipDAO {

  /** {@inheritDoc} */
  @Override
  public void insert(ProcessingRelationship processingRelationship) {
    this.getHibernateTemplate().save(processingRelationship);
  }

  /** {@inheritDoc} */
  @Override
  public void update(ProcessingRelationship processingRelationship) {
    this.getHibernateTemplate().update(processingRelationship);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(ProcessingRelationship processingRelationship) {
    this.getHibernateTemplate().delete(processingRelationship);
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("rawtypes")
  public ProcessingRelationship findByProcessings(Processing processingParent, Processing processingChild) {
    String query = "from ProcessingRelationship as pl where pl.processingByParentId.processingId = ? and pl.processingByChildId.processingId = ?";
    ProcessingRelationship obj = null;
    Object[] parameters = { processingParent.getProcessingId(), processingChild.getProcessingId() };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      obj = (ProcessingRelationship) list.get(0);
    }
    return obj;
  }

  /** {@inheritDoc} */
  @Override
  public ProcessingRelationship updateDetached(ProcessingRelationship processingRelationship) {
    ProcessingRelationship dbObject = findByProcessings(processingRelationship.getProcessingByParentId(),
        processingRelationship.getProcessingByChildId());
    try {
      BeanUtilsBean beanUtils = new NullBeanUtils();
      beanUtils.copyProperties(dbObject, processingRelationship);
      return (ProcessingRelationship) this.getHibernateTemplate().merge(dbObject);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

    /** {@inheritDoc} */
    @Override
    public List<ProcessingRelationship> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
