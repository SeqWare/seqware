package net.sourceforge.seqware.common.dao.hibernate;

import java.util.List;

import net.sourceforge.seqware.common.dao.WorkflowAttributeDAO;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowAttribute;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
/**
 * <p>WorkflowAttributeDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class WorkflowAttributeDAOHibernate implements WorkflowAttributeDAO {

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  /** {@inheritDoc} */
  @Override
  public List<WorkflowAttribute> getAll() {
    Query query = currentSession().createQuery("from WorkflowAttribute");
    @SuppressWarnings("unchecked")
    List<WorkflowAttribute> records = query.list();
    return records;
  }

  /** {@inheritDoc} */
  @Override
  public List<WorkflowAttribute> get(Workflow workflow) {
    Query query = currentSession().createQuery("from WorkflowAttribute as f where f.workflow = :workflow");
    query.setEntity("workflow", workflow);
    @SuppressWarnings("unchecked")
    List<WorkflowAttribute> records = query.list();
    return records;
  }

  /** {@inheritDoc} */
  @Override
  public WorkflowAttribute get(Integer id) {
    return (WorkflowAttribute) currentSession().get(WorkflowAttribute.class, id);
  }

  /** {@inheritDoc} */
  @Override
  public Integer add(WorkflowAttribute workflowAttribute) {
    return (Integer) currentSession().save(workflowAttribute);
  }

  /** {@inheritDoc} */
  @Override
  public void update(WorkflowAttribute workflowAttribute) {
    currentSession().merge(workflowAttribute);
  }

  /** {@inheritDoc} */
  @Override
  public void delete(WorkflowAttribute workflowAttribute) {
    currentSession().delete(workflowAttribute);
  }

}
