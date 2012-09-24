package net.sourceforge.seqware.common.dao.hibernate;

import java.util.List;

import net.sourceforge.seqware.common.dao.WorkflowRunAttributeDAO;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.WorkflowRunAttribute;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class WorkflowRunAttributeDAOHibernate implements WorkflowRunAttributeDAO {

  @Autowired
  private SessionFactory sessionFactory;

  private Session currentSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public List<WorkflowRunAttribute> getAll() {
    Query query = currentSession().createQuery("from WorkflowRunAttribute");
    @SuppressWarnings("unchecked")
    List<WorkflowRunAttribute> records = query.list();
    return records;
  }

  @Override
  public List<WorkflowRunAttribute> get(WorkflowRun workflowRun) {
    Query query = currentSession().createQuery("from WorkflowRunAttribute as f where f.workflowRun = :workflowRun");
    query.setEntity("workflowRun", workflowRun);
    @SuppressWarnings("unchecked")
    List<WorkflowRunAttribute> records = query.list();
    return records;
  }

  @Override
  public WorkflowRunAttribute get(Integer id) {
    return (WorkflowRunAttribute) currentSession().get(WorkflowRunAttribute.class, id);
  }

  @Override
  public Integer add(WorkflowRunAttribute workflowRunAttribute) {
    return (Integer) currentSession().save(workflowRunAttribute);
  }

  @Override
  public void update(WorkflowRunAttribute workflowRunAttribute) {
    currentSession().merge(workflowRunAttribute);
  }

  @Override
  public void delete(WorkflowRunAttribute workflowRunAttribute) {
    currentSession().delete(workflowRunAttribute);
  }

}
