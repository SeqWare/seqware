package net.sourceforge.seqware.common.dao.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.seqware.common.dao.ShareWorkflowRunDAO;
import net.sourceforge.seqware.common.model.ShareWorkflowRun;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.util.NullBeanUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * <p>ShareWorkflowRunDAOHibernate class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ShareWorkflowRunDAOHibernate extends HibernateDaoSupport implements ShareWorkflowRunDAO {
  /**
   * <p>Constructor for ShareWorkflowRunDAOHibernate.</p>
   */
  public ShareWorkflowRunDAOHibernate() {
    super();
  }

  /**
   * {@inheritDoc}
   *
   * Inserts an instance of ShareWorkflowRun into the database.
   */
  public void insert(ShareWorkflowRun shareWorkflowRun) {
    this.getHibernateTemplate().save(shareWorkflowRun);
  }

  /**
   * {@inheritDoc}
   *
   * Updates an instance of ShareWorkflowRun in the database.
   */
  public void update(ShareWorkflowRun shareWorkflowRun) {

    this.getHibernateTemplate().update(shareWorkflowRun);
  }

  /**
   * {@inheritDoc}
   *
   * Updates an instance of ShareWorkflowRun in the database.
   */
  public void delete(ShareWorkflowRun shareWorkflowRun) {

    this.getHibernateTemplate().delete(shareWorkflowRun);
  }

  /**
   * {@inheritDoc}
   *
   * Finds an instance of ShareWorkflowRun in the database by the
   * ShareWorkflowRun ID.
   */
  public ShareWorkflowRun findByWorkflowRunIdAndRegistrationId(Integer workflowRunId, Integer registrationId) {
    String query = "from ShareWorkflowRun as shareWorkflowRun where shareWorkflowRun.workflowRunId = ? and shareWorkflowRun.registration.registrationId = ?";
    ShareWorkflowRun shareWorkflowRun = null;
    Object[] parameters = { workflowRunId, registrationId };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      shareWorkflowRun = (ShareWorkflowRun) list.get(0);
    }
    return shareWorkflowRun;
  }

  /**
   * {@inheritDoc}
   *
   * Finds an instance of ShareWorkflowRun in the database by the
   * ShareWorkflowRun ID.
   */
  public ShareWorkflowRun findByID(Integer id) {
    String query = "from ShareWorkflowRun as shareWorkflowRun where shareWorkflowRun.shareWorkflowRunId = ?";
    ShareWorkflowRun shareWorkflowRun = null;
    Object[] parameters = { id };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      shareWorkflowRun = (ShareWorkflowRun) list.get(0);
    }
    return shareWorkflowRun;
  }

  /** {@inheritDoc} */
  @SuppressWarnings("unchecked")
  @Override
  public ShareWorkflowRun findBySWAccession(Integer swAccession) {
    String query = "from ShareWorkflowRun as shareWorkflowRun where shareWorkflowRun.swAccession = ?";
    ShareWorkflowRun shareWorkflowRun = null;
    Object[] parameters = { swAccession };
    List<ShareWorkflowRun> list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      shareWorkflowRun = (ShareWorkflowRun) list.get(0);
    }
    return shareWorkflowRun;
  }

  /** {@inheritDoc} */
  public List<ShareWorkflowRun> list(WorkflowRun workflowRun) {
    List<ShareWorkflowRun> shareWorkflowRuns = new ArrayList<ShareWorkflowRun>();
    String query = "from ShareWorkflowRun as shareWorkflowRun where shareWorkflowRun.workflowRunId = ?";
    Object[] parameters = { workflowRun.getWorkflowRunId() };
    List list = this.getHibernateTemplate().find(query, parameters);

    for (Object shareWorkflowRun : list) {
      shareWorkflowRuns.add((ShareWorkflowRun) shareWorkflowRun);
    }
    return shareWorkflowRuns;
  }

  /** {@inheritDoc} */
  public ShareWorkflowRun getShareWorkflowRun(String email, WorkflowRun workflowRun) {
    String query = "from ShareWorkflowRun as shareWorkflowRun where shareWorkflowRun.workflowRunId = ? and shareWorkflowRun.email = ?";
    ShareWorkflowRun shareWorkflowRun = null;
    Object[] parameters = { workflowRun.getWorkflowRunId(), email };
    List list = this.getHibernateTemplate().find(query, parameters);
    if (list.size() > 0) {
      shareWorkflowRun = (ShareWorkflowRun) list.get(0);
    }
    return shareWorkflowRun;
  }

  /** {@inheritDoc} */
  @Override
  public ShareWorkflowRun updateDetached(ShareWorkflowRun shareWorkflowRun) {
    ShareWorkflowRun dbObject = findByID(shareWorkflowRun.getShareWorkflowRunId());
    try {
      BeanUtilsBean beanUtils = new NullBeanUtils();
      beanUtils.copyProperties(dbObject, shareWorkflowRun);
      return (ShareWorkflowRun) this.getHibernateTemplate().merge(dbObject);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

    /** {@inheritDoc} */
    @Override
    public List<ShareWorkflowRun> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
